package com.artifex.mupdflib;

import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import com.mykingdom.mupdf.MupdfModule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;

public abstract class SearchTask {

	private static final int SEARCH_PROGRESS_DELAY = 200;
	private final Context mContext;
	private final MuPDFCore mCore;
	private final Handler mHandler;
	private AsyncTask<Void, Integer, SearchTaskResult> mSearchTask;

	public SearchTask(Context context, MuPDFCore core) {
		mContext = context;
		mCore = core;
		mHandler = new Handler();
	}

	protected abstract void onTextFound(SearchTaskResult result);


	public void stop() {
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
			mSearchTask = null;
		}
	}

	public void go(final String text, int direction, int displayPage,
			int searchPage) {
		if (mCore == null)
			return;
		stop();

		final int increment = direction;
		final int startIndex = searchPage == -1 ? displayPage : searchPage
				+ increment;

		mSearchTask = new AsyncTask<Void, Integer, SearchTaskResult>() {
			@Override
			protected SearchTaskResult doInBackground(Void... params) {
				int index = startIndex;

				while (0 <= index && index < mCore.countPages()
						&& !isCancelled()) {
					publishProgress(index);
					int page = index;
					if (mCore.getDisplayPages() == 2) {
						page = (page * 2) - 1;
					}

					RectF searchHits[] = mCore.searchPage(page, text);
					RectF searchHitsPrim[] = mCore.getDisplayPages() == 2 ? mCore
							.searchPage(page + 1, text) : null;

					if ((searchHits != null && searchHits.length > 0)
							|| (searchHitsPrim != null && searchHitsPrim.length > 0))
						return new SearchTaskResult(text, index, searchHits,
								searchHitsPrim);

					index += increment;
				}
				return null;
			}

			@Override
			protected void onPostExecute(SearchTaskResult result) {
				if (result != null) {
					onTextFound(result);
				} 
			}

			@Override
			protected void onCancelled() {

			}

			@Override
			protected void onProgressUpdate(Integer... values) {

			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mHandler.postDelayed(new Runnable() {
					public void run() {

					}
				}, SEARCH_PROGRESS_DELAY);
			}
		};

		mSearchTask.execute();
	}
}

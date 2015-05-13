package com.artifex.mupdflib;

import android.content.Context;
import android.graphics.RectF;

public abstract class SearchTask {
	
	private final MuPDFCore mCore;
	private AsyncTask<Void, Integer, SearchTaskResult> mSearchTask;

	public SearchTask(Context context, MuPDFCore core) {
		mCore = core;
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
				int max = index + 1;
				
				while (0 <= index && index < max
						&& !isCancelled()) {
					publishProgress(index);
					RectF searchHits[] = mCore.searchPage(index, text);

					if (searchHits != null && searchHits.length > 0)
						return new SearchTaskResult(text, startIndex, searchHits);

					index = max;
				}
				return new SearchTaskResult(text, startIndex, null);
			}

			@Override
			protected void onPostExecute(SearchTaskResult result) {
				onTextFound(result);
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
			}
		};

		mSearchTask.execute();
	}
}

package com.artifex.mupdflib;

import android.content.Context;
import android.graphics.RectF;

public abstract class SearchTask {
	
	private final MuPDFCore mCore;
	private AsyncTask<Void, Integer, Object> mSearchTask;

	public SearchTask(Context context, MuPDFCore core) {
		mCore = core;
	}

	protected abstract void onTextFound(SearchTaskResult result);

	protected abstract void onTextNotFound(int page);

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

		mSearchTask = new AsyncTask<Void, Integer, Object>() {
			@Override
			protected Object doInBackground(Void... params) {
				int index = startIndex;

				while (0 <= index && index < mCore.countPages()
						&& !isCancelled()) {
					publishProgress(index);
					RectF searchHits[] = mCore.searchPage(index, text);

					if (searchHits != null && searchHits.length > 0)
						return new SearchTaskResult(text, index, searchHits);

					index += increment;
				}
				return index;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof SearchTaskResult) {
					onTextFound((SearchTaskResult) result);
				} else {
					onTextNotFound((Integer) result);
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
			}
		};

		mSearchTask.execute();
	}
}

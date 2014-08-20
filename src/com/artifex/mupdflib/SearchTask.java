package com.artifex.mupdflib;

import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;

class ProgressDialogX extends ProgressDialog {
	public ProgressDialogX(Context context) {
		super(context);
	}

	private boolean mCancelled = false;

	public boolean isCancelled() {
		return mCancelled;
	}

	@Override
	public void cancel() {
		mCancelled = true;
		super.cancel();
	}
}

public abstract class SearchTask {
	private static final int SEARCH_PROGRESS_DELAY = 200;
	private final Context mContext;
	private final MuPDFCore mCore;
	private final Handler mHandler;
	private final AlertDialog.Builder mAlertBuilder;
	private AsyncTask<Void, Integer, SearchTaskResult> mSearchTask;

	public SearchTask(Context context, MuPDFCore core) {
		mContext = context;
		mCore = core;
		mHandler = new Handler();
		mAlertBuilder = new AlertDialog.Builder(context);
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

		final ProgressDialogX progressDialog = new ProgressDialogX(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		try {
			progressDialog.setTitle(mContext.getString(TiRHelper
					.getResource("string.searching_")));
		} catch (ResourceNotFoundException exp) {
			Log.e("SearchTask", "XML resouce not found!");
		}
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						stop();
					}
				});
		progressDialog.setMax(mCore.countPages());

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
				progressDialog.cancel();
				if (result != null) {
					onTextFound(result);
				} else {
					try {
						mAlertBuilder
								.setTitle(SearchTaskResult.get() == null ? TiRHelper
										.getResource("string.text_not_found")
										: TiRHelper
												.getResource("string.no_further_occurrences_found"));
						AlertDialog alert = mAlertBuilder.create();
						alert.setButton(AlertDialog.BUTTON_POSITIVE, mContext
								.getString(TiRHelper
										.getResource("string.dismiss")),
								(DialogInterface.OnClickListener) null);
						alert.show();
					} catch (ResourceNotFoundException exp) {
						Log.e("SearchTask", "XML resouce not found!");
					}
				}
			}

			@Override
			protected void onCancelled() {
				progressDialog.cancel();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				progressDialog.setProgress(values[0].intValue());
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mHandler.postDelayed(new Runnable() {
					public void run() {
						if (!progressDialog.isCancelled()) {
							progressDialog.show();
							progressDialog.setProgress(startIndex);
						}
					}
				}, SEARCH_PROGRESS_DELAY);
			}
		};

		mSearchTask.execute();
	}
}

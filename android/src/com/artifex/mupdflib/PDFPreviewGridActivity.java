package com.artifex.mupdflib;

import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

public class PDFPreviewGridActivity extends Activity {
	private MuPDFCore mCore;
	private int mPosition;
	private GridView mGrid;
	private PDFPreviewGridAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LibraryUtils.reloadLocale(getApplicationContext());

		mCore = PDFPreviewGridActivityData.get().core;
		mPosition = PDFPreviewGridActivityData.get().position;

		try {
			setContentView(TiRHelper
					.getResource("layout.preview_grid_fragment"));

			mGrid = (GridView) findViewById(TiRHelper
					.getResource("id.preview_grid"));
		} catch (ResourceNotFoundException exp) {
			Log.e("MuPDFPreviewGridActvity", "XML resouce not found!");
		}

		mAdapter = new PDFPreviewGridAdapter(this, mCore, mPosition);
		mGrid.setAdapter(mAdapter);
		mGrid.smoothScrollToPosition(mPosition);
	}

	public void OnCancelPreviewButtonClick(View v) {
		setResult(mPosition);
		finish();
	}

}

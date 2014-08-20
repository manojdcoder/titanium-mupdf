package com.artifex.mupdf;

import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OutlineAdapter extends BaseAdapter {
	private final OutlineItem mItems[];
	private final LayoutInflater mInflater;

	public OutlineAdapter(LayoutInflater inflater, OutlineItem items[]) {
		mInflater = inflater;
		mItems = items;
	}

	public int getCount() {
		return mItems.length;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		try {
			if (convertView == null) {
				v = mInflater.inflate(
						TiRHelper.getResource("layout.outline_entry"), null);
			} else {
				v = convertView;
			}
			int level = mItems[position].level;
			if (level > 8)
				level = 8;
			String space = "";
			for (int i = 0; i < level; i++)
				space += "   ";
			((TextView) v.findViewById(TiRHelper.getResource("id.title")))
					.setText(space + mItems[position].title);
			((TextView) v.findViewById(TiRHelper.getResource("id.page")))
					.setText(String.valueOf(mItems[position].page + 1));
		} catch (ResourceNotFoundException exp) {
			Log.e("OutlineAdapter", "XML resouce not found!");
		}
		return v;
	}

}

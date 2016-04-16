package edu.foothill.myflipbook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ColorAdapter extends BaseAdapter {
	
	private Context mContext;
	private Integer[] mColors;
	private int mSize;
	
	public ColorAdapter(Context context, Integer[] colors, int size) {
		mContext = context;
		mColors = colors;
		mSize = size;
	}

	@Override
	public int getCount() {
		return mColors.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image;
		if (convertView == null) {
			image = new ImageView(mContext);
			image.setLayoutParams(new LayoutParams(mSize, mSize));
			image.setScaleType(ScaleType.FIT_CENTER);
			image.setBackgroundColor(Color.LTGRAY);
			image.setPadding(1, 1, 1, 1);
		} else {
			image = (ImageView) convertView;
		}
		
		image.setImageDrawable(new ColorDrawable(mColors[position]));
		return image;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ImageView image;
		if (convertView == null) {
			image= new ImageView(mContext);
			image.setLayoutParams(new LayoutParams(mSize, mSize));
			image.setScaleType(ScaleType.CENTER_CROP);
		} else {
			image = (ImageView) convertView;
		}
		
		image.setImageDrawable(new ColorDrawable(mColors[position]));
		return image;
	}
}

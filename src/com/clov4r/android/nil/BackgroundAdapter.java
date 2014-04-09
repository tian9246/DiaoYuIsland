package com.clov4r.android.nil;



import org.mummy.activity.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class BackgroundAdapter extends BaseAdapter {
 
	int selectedIndex = 0;
	public static int resourceId[] = new int[] { 0, R.drawable.pad_layout_bg,
			R.drawable.pad_layout_bg_1, R.drawable.pad_layout_bg_2,
			R.drawable.pad_layout_bg_3, R.drawable.pad_layout_bg_4,
			R.drawable.pad_layout_bg_5 };   

	Context con = null;

	public BackgroundAdapter(Context context) {
		con = context;
	}

	public void setSelected(int index) {
		selectedIndex = index;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return resourceId.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RelativeLayout layout = null;
		ImageView background = null;
		if (convertView == null) {
			layout = new RelativeLayout(con);
			ImageView img = new ImageView(con);
			img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			background = new ImageView(con);
			background.setBackgroundResource(R.drawable.bg_img_frame);
			if (position == selectedIndex)
				background.setVisibility(View.VISIBLE);
			else
				background.setVisibility(View.INVISIBLE);
			if (position == 0)
				img.setBackgroundResource(R.drawable.background_0); 
			else
				img.setBackgroundResource(resourceId[position]);
			LayoutParams params = new LayoutParams(100, 100);
			params.setMargins(2, 2, 2, 2);
			layout.setGravity(Gravity.CENTER);
			layout.addView(background, params);
			layout.addView(img, params);
			return layout;
		} else
			background = (ImageView) ((RelativeLayout) convertView)
					.getChildAt(0);
		if (position == selectedIndex)
			background.setVisibility(View.VISIBLE);
		else
			background.setVisibility(View.INVISIBLE);

		if (position == selectedIndex)
			convertView.setBackgroundResource(R.drawable.bg_img_frame);
		else
			convertView.setBackgroundResource(0);
		return convertView;
	}

}

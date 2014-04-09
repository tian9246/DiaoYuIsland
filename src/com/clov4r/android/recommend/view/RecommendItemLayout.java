package com.clov4r.android.recommend.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class RecommendItemLayout extends LinearLayout {

	public RecommendItemLayout(Context con) {
		this(con, null);
		setOnTouchListener(mOnTouchListener);
		setFocusableInTouchMode(true);
		requestFocusFromTouch();
	}

	public RecommendItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isFocused() {
		return true;
	}

	OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				setBackgroundColor(Color.BLUE);
				Object obj = getTag();
				if (obj != null)
					Log.e("recommend", obj.toString() + "-->ACTION_DOWN");
				break;

			case MotionEvent.ACTION_UP:
				setBackgroundColor(Color.parseColor("#00000000"));
				Object objs = getTag();
				if (objs != null)
					Log.e("recommend", objs.toString() + "-->ACTION_UP");
				break;
			}
			return true;
		}
	};
}

package com.clov4r.android.recommend.adapter;

import java.util.ArrayList;
import java.util.HashMap;

//import com.adsmogo.adview.AdsMogoLayout;

import com.clov4r.android.nil.Global;
import com.clov4r.android.nil.Global.CountPair;
import com.clov4r.android.recommend.lib.AdViewCreateLib;
import com.clov4r.android.recommend.lib.RecommendLib;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.recommend.view.RecommendItemLayout;
import com.clov4r.android.recommend.RecommendActivity;
import org.mummy.activity.R;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {
	// key:行 value:dataList中的位置
	public HashMap<Integer, CountPair> relationMap = new HashMap<Integer, CountPair>();
	public ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	public Context mContext = null;
	public LayoutInflater layoutInflater = null;
	Handler mHandler = null;

	public ItemAdapter(HashMap<Integer, CountPair> arg0, Context arg1,
			ArrayList<RecommendData> arg2) {
		relationMap = arg0;
		mContext = arg1;
		dataList = arg2;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	OnClickListener mOnClickListener = null;

	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return relationMap.size();
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

	AdViewCreateLib mAdViewCreateLib = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// LinearLayout linearLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.weight = 1;
		LinearLayout linearLayout = new LinearLayout(mContext);
		layoutInflater = LayoutInflater.from(mContext);
		View view = new View(mContext);
		CountPair countPair = relationMap.get(position);
		int index = countPair.startIndex;// position行的第一个值对应的object
		int count = countPair.count;

		for (int i = 0; i < Global.colums; i++) {
			view = new View(mContext);
			if (index + i < dataList.size() && i < count) {
				RecommendData data = dataList.get(index + i);
				if (3 == data.dataType) {
					mAdViewCreateLib = new AdViewCreateLib(mContext,
							AdViewCreateLib.type_of_ad);//
					view = mAdViewCreateLib.getAdView();
					params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);//
					params.gravity = Gravity.CENTER_VERTICAL;
					params.weight = 1;

				} else {
					if (0 == data.displayType || 1 == data.displayType
							|| 3 == data.displayType) {// 0:google
						// 1:淘宝
						// 2：纯图片
						if (0 == data.displayType) {
							view = layoutInflater.inflate(
									R.layout.item_adapter0, null);
							// if (count > 1)
							// view = layoutInflater.inflate(
							// R.layout.item_adapter0, null);
							// else
							// view = layoutInflater.inflate(
							// R.layout.item_adapter4, null);

						} else if (1 == data.displayType) {
							view = layoutInflater.inflate(
									R.layout.item_adapter1, null);
						} else if (3 == data.displayType) {
							view = layoutInflater.inflate(
									R.layout.item_adapter_movie, null);
						}

						TextView appName = (TextView) view
								.findViewById(R.id.app_name);
						TextView appAuthor = (TextView) view
								.findViewById(R.id.app_author);
						TextView appProperty = (TextView) view
								.findViewById(R.id.app_property);
						TextView appCount = (TextView) view
								.findViewById(R.id.app_count);
						ImageView appPropertyImg = (ImageView) view
								.findViewById(R.id.app_property_img);
						RatingBar appRating = (RatingBar) view
								.findViewById(R.id.app_rating);

						appCount.setVisibility(View.GONE);
						if ((1 == data.dataLevel && RecommendData.TYPE_OPEN_APP != data.dataType)
								|| 3 == data.displayType) {// 无子目录(无二级分类)
							appName.setText(index + i + 1 + "." + data.appName);
							appAuthor.setText(data.appAuthor);
							appProperty.setText(data.appProperty);
							Bitmap appPropertyBitmap = Global.getBitmap(data
									.getImagePathByUrl(data.appPropertyImgUrl));
							if (null != appPropertyBitmap) {
								appPropertyImg
										.setImageBitmap(appPropertyBitmap);
								appPropertyImg.setBackgroundResource(0);
							}
							appRating.setRating(data.appRate);
							appRating.setVisibility(View.VISIBLE);
							appPropertyImg.setVisibility(View.VISIBLE);
						} else if (0 == data.dataLevel
								|| RecommendData.TYPE_OPEN_APP == data.dataType) {// 有子目录(二级分类)
							appName.setText(index + i + 1 + "." + data.appName);
							appPropertyImg.setVisibility(View.GONE);
							appProperty.setVisibility(View.GONE);
							appRating.setVisibility(View.GONE);
							if (0 == data.dataLevel) {
								appCount.setVisibility(View.VISIBLE);
								appCount.setText(data.childrenCount + "");
							} else {
								appCount.setVisibility(View.GONE);
							}
							appAuthor.setText("");
							appProperty.setText("");

						}

					} else if (2 == data.displayType) {
						view = layoutInflater.inflate(R.layout.item_adapter2,
								null);
					}
				}
				if (!(view instanceof AdView)) {//view instanceof AdsMogoLayout || 
					view.setTag(data);
					ImageView appImg = (ImageView) view
							.findViewById(R.id.app_img);
					Bitmap appBitmap = Global.getBitmap(data
							.getImagePathByUrl(data.appImgUrl));
					if (null != appBitmap && appImg != null) {
						appImg.setImageBitmap(appBitmap);
						appImg.setBackgroundResource(0);
					} else {
					}

					view.setBackgroundResource(R.drawable.recommend_bg);
					// view.setOnTouchListener(mOnTouchListener);
					view.setOnClickListener(mOnClickListener);
					linearLayout.addView(view, params);
				} else {
					mAdViewCreateLib.addViewTo(linearLayout, params);
				}

				view.setTag(data);
				view.setTag(R.id.tag_recommend_index, position);
				ImageView sep = new ImageView(mContext);
				LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
						1, LinearLayout.LayoutParams.MATCH_PARENT);
				sep.setImageResource(R.drawable.recommend_sep_v);
				sep.setBackgroundColor(Color.parseColor("#55464646"));
				linearLayout.addView(sep, params2);
				// linearLayout.setBackgroundResource(R.drawable.recommend_bg);

				// linearLayout.setOnTouchListener(new OnTouchListener() {
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// // TODO Auto-generated method stub
				//
				// return true;
				// }
				// });

			}
		}

		linearLayout.setTag(R.id.tag_recommend_index, position);
		return linearLayout;
	}

	OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				v.setPressed(true);
				break;
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				break;

			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setPressed(false);
				break;
			}
			return false;
		}
	};

	// //item_adapter1 googleplay布局
	// class ViewHolder1{
	// private ImageView appImg = null;
	// private TextView appName = null;
	// private TextView appAuthor = null;
	// private RatingBar appRating = null;
	// }

}

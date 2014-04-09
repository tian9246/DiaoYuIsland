package com.clov4r.android.recommend;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.clov4r.android.nil.Global;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.recommend.lib.RecommendLib;
import org.mummy.activity.R;

public class AdapterRecommend extends BaseAdapter {
	ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	LayoutInflater inflater = null;
	Bitmap defaultImgBitmap = null, defaultPropertyBitmap = null;

	public AdapterRecommend(Context con, ArrayList<RecommendData> arrays) {
		if (arrays != null) {
			dataList.addAll(arrays);
		}
		inflater = LayoutInflater.from(con);

		defaultImgBitmap = BitmapFactory.decodeResource(con.getResources(),
				R.drawable.recommend_default_app);
		defaultPropertyBitmap = BitmapFactory.decodeResource(
				con.getResources(), R.drawable.recommend_property_img);
	}

	public void insertData(ArrayList<RecommendData> arrays) {
		if (arrays != null) {
			if (null != dataList) {
				dataList.clear();
			}
			for (int i = 0; i < arrays.size(); i++) {
				RecommendData tempData = arrays.get(i);
				if (!hasContained(tempData))
					dataList.add(tempData);
			}
		}
	}

	private boolean hasContained(RecommendData data) {
		// String name = data.appName + "";
		// for (int i = 0; i < dataList.size(); i++) {
		// RecommendData tempData = dataList.get(i);
		// if (name.equals(tempData.appName + ""))
		// return true;
		// }
		return false;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			notifyDataSetChanged();
		}
	};

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
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
		TextView tv_name, tv_author, tv_property, tv_size;
		ImageView img_app, img_property;
		RatingBar ratingBar = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_recommend, null);

		}

		RecommendData data = dataList.get(position);

		tv_name = (TextView) convertView
				.findViewById(R.id.recommend_adapter_app_name);
		tv_author = (TextView) convertView
				.findViewById(R.id.recommend_adapter_app_author);
		tv_property = (TextView) convertView
				.findViewById(R.id.recommend_adapter_app_property_text);
		tv_size = (TextView) convertView
				.findViewById(R.id.recommend_adapter_app_size);
		img_app = (ImageView) convertView
				.findViewById(R.id.recommend_adapter_img);
		img_property = (ImageView) convertView
				.findViewById(R.id.recommend_adapter_app_property_img);
		ratingBar = (RatingBar) convertView
				.findViewById(R.id.recommend_adapter_rating);

		tv_name.setText(data.appName);
		tv_author.setText(data.appAuthor);
		tv_property.setText(data.appProperty);
		tv_size.setText(data.appSize);
		ratingBar.setRating(data.appRate);
		Bitmap appBitmap = Global.getBitmap(data
				.getImagePathByUrl(data.appImgUrl));
		Bitmap appPropertyBitmap = Global.getBitmap(data
				.getImagePathByUrl(data.appPropertyImgUrl));
		if (appBitmap != null)
			img_app.setImageBitmap(appBitmap);
		else {
//			RecommendLib.downloadImage(data.appImgUrl, mHandler);
			img_app.setImageBitmap(defaultImgBitmap);
		}

		if (appPropertyBitmap != null)
			img_property.setImageBitmap(appPropertyBitmap);
		else {
//			RecommendLib.downloadImage(data.appPropertyImgUrl, mHandler);
			img_property.setImageBitmap(defaultPropertyBitmap);
		}

		return convertView;
	}

}

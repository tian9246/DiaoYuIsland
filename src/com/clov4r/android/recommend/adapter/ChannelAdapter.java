/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clov4r.android.recommend.adapter;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.clov4r.android.nil.Global;
import com.clov4r.android.recommend.lib.RecommendCategoryData;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.recommend.view.TitleProvider;
import org.mummy.activity.R;

public class ChannelAdapter extends BaseAdapter implements TitleProvider {

	private LayoutInflater mInflater;
	ArrayList<RecommendCategoryData> categoryList = new ArrayList<RecommendCategoryData>();
	ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	HashMap<Integer, ArrayList<RecommendData>> dataMap = new HashMap<Integer, ArrayList<RecommendData>>();
	private Context mContext;
	Drawable drawable = null;

	public ChannelAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		drawable = context.getResources().getDrawable(
				R.drawable.recommend_sep_h);
	}

	public void setCategoryData(ArrayList<RecommendCategoryData> categoryList) {
		if (categoryList != null) {
			this.categoryList.clear();
			this.categoryList.addAll(categoryList);
			notifyDataSetChanged();
		}
	}

	public void setRecommendData(ArrayList<RecommendData> recommendList,
			int index) {
		if (recommendList != null) {
			dataMap.put(index, recommendList);
			dataList = dataMap.get(index);
			notifyDataSetChanged();
		}
	}

	Handler mHandler = null;

	public void setHandler(Handler handle) {
		mHandler = handle;
	}

	OnClickListener mOnClickListener = null;

	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}

	@Override
	public int getCount() {
		return categoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.flow_item, null);
		}
		dataList = dataMap.get(position);
		if (dataList == null)
			dataList = new ArrayList<RecommendData>();
		ListView listView = (ListView) convertView
				.findViewById(R.id.recommend_data_list);
		ItemAdapter tempItem = (ItemAdapter)listView.getAdapter();
		if (tempItem != null) {
			tempItem.notifyDataSetChanged();
		} else {
			listView.setDivider(drawable);
			listView.setDividerHeight(3);
			ItemAdapter itemAdapter = new ItemAdapter(
					Global.getRelationMap(dataList), mContext, dataList);
			itemAdapter.setHandler(mHandler);
			itemAdapter.setOnClickListener(mOnClickListener);
			listView.setAdapter(itemAdapter);
			// listView.setFocusable(false);
			// listView.setItemsCanFocus(true);
			listView.setCacheColorHint(0);
		}
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.taptwo.android.widget.TitleProvider#getTitle(int)
	 */
	@Override
	public String getTitle(int position) {
		return categoryList.get(position).categoryName + "";
	}

}

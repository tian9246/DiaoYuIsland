package com.clov4r.android.recommend;

import java.util.ArrayList;

import com.clov4r.android.nil.ActivityMoboLineVideo;
import com.clov4r.android.recommend.lib.DataSaveLib;
import com.clov4r.android.recommend.lib.RecommendCategoryData;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.recommend.lib.RecommendLib;
import org.mummy.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRecommend extends Activity {
	public static final int type_of_category = 1;
	public static final int type_of_data = 2;
	RecommendLib mRecommendLib = null;
	ArrayList<RecommendCategoryData> categoryList = new ArrayList<RecommendCategoryData>();
	ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	DataSaveLib mDataSaveLib = null;
	ProgressBar loading = null;

	ListView recommend_list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_recommend_content);
		initViews();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	void initViews() {
		recommend_list = (ListView) findViewById(R.id.recommend_content_list);
		loading = (ProgressBar) findViewById(R.id.loading);
		recommend_list.setOnItemClickListener(mOnItemClickListener);

		mDataSaveLib = new DataSaveLib(this, DataSaveLib.flag_recommend_data,
				"recommend_category.tmp");
		categoryList = (ArrayList<RecommendCategoryData>) mDataSaveLib
				.getData();
		if (categoryList == null || categoryList.size() == 0) {//
			new DownloadThread(RecommendLib.jsonCategoryUrl, type_of_category,//
					"root_channel").start();
		}else{
			CategoryAdapter adapter = new CategoryAdapter();
			recommend_list.setAdapter(adapter);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RecommendLib.msg_download_failure:
				Toast.makeText(ActivityRecommend.this,
						R.string.data_download_failed, Toast.LENGTH_SHORT)
						.show();
				break;

			case RecommendLib.msg_download_success:
				int type = msg.arg1;
				if (type == type_of_category) {
					CategoryAdapter adapter = new CategoryAdapter();
					recommend_list.setAdapter(adapter);
				} else if (type == type_of_data) {
					AdapterRecommend adapter = new AdapterRecommend(
							ActivityRecommend.this, dataList);
					recommend_list.setAdapter(adapter);
				}

				break;

			case RecommendLib.msg_img_download_success:

				break;
			}
		}
	};

	class DownloadThread extends Thread {
		String downloadUrl = null;
		int type = 0;
		String channelName = null;

		public DownloadThread(String url, int type, String channelName) {
			mRecommendLib = new RecommendLib(mHandler);
			// url = url.replace("http://www.moboplayer.com/update/",
			// "/sdcard/mobo_recommend/");
			downloadUrl = url;
			this.type = type;
			this.channelName = channelName;
		}

		@Override
		public void run() {
			Message msg = new Message();
			msg.arg1 = type;
			if (type == type_of_category) {
				mRecommendLib.downloadChannelInfo(downloadUrl);
				categoryList = mRecommendLib.getCategoryList();
				if (categoryList != null && categoryList.size() > 0) {
					mDataSaveLib = new DataSaveLib(ActivityRecommend.this,
							DataSaveLib.flag_recommend_data,
							"recommend_category.tmp");
					mDataSaveLib.saveData(categoryList);
					msg.what = RecommendLib.msg_download_success;
					mHandler.sendMessage(msg);
				} else {
					msg.what = RecommendLib.msg_download_failure;
					mHandler.sendMessage(msg);
				}
			} else if (type == type_of_data) {
				mRecommendLib.downloadData(downloadUrl);
				dataList = mRecommendLib.getDataList();
				if (dataList != null && dataList.size() > 0) {
					mDataSaveLib = new DataSaveLib(ActivityRecommend.this,
							DataSaveLib.flag_recommend_data, channelName
									+ ".tmp");
					mDataSaveLib.saveData(dataList);
					msg.what = RecommendLib.msg_download_success;
					mHandler.sendMessage(msg);
				} else {
					msg.what = RecommendLib.msg_download_failure;
					mHandler.sendMessage(msg);
				}
			}
		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Object obj = arg1.getTag();
			if (obj != null) {
				if (obj instanceof RecommendData) {
					RecommendData data = (RecommendData) obj;
					int dataLevel = data.dataLevel;
					if (dataLevel == 1) {// 鏈�綆绾х洰褰曪紝鐩存帴鎵撳紑鐩稿簲鐨勭▼搴�
						String url = data.appUrl_1;
						String url_2 = data.appUrl_2;
						String action = data.intentAction;
						String smsBody = data.smsBody;
						int type = data.dataType;
						try {
							if (url != null) {
								Intent urlIntent = new Intent(action);
								if (type == RecommendData.TYPE_SMS
										&& smsBody != null)
									urlIntent.putExtra("sms_body", smsBody);
								urlIntent.setData(Uri.parse(url));
								startActivity(urlIntent);
							}
						} catch (Exception ee) {
							try {
								if (url_2 != null) {
									Intent urlIntent = new Intent(action);
									urlIntent.setData(Uri.parse(url_2));
									if (type == RecommendData.TYPE_SMS
											&& smsBody != null)
										urlIntent.putExtra("sms_body", smsBody);
									startActivity(urlIntent);
								}
							} catch (Exception eee) {
							}
						}
					} else {// 琚偣鍑荤殑鏄洰褰曪紝涓嬮潰杩樻湁鍐呭銆備笅杞界浉搴旂殑鍐呭
						String url = data.nextLevelUrl;
						String recommendName = data.appName;
						long lastUpdate = data.pubDate;
						mDataSaveLib = new DataSaveLib(ActivityRecommend.this,
								DataSaveLib.flag_recommend_data, recommendName
										+ ".tmp");

						try {
							dataList = (ArrayList<RecommendData>) mDataSaveLib
									.getData();
						} catch (Exception e) {

						}

						if (url != null && recommendName != null)
							new DownloadThread(url, type_of_data, recommendName)
									.start();
					}
				}else if(obj instanceof RecommendCategoryData){
					new DownloadThread(((RecommendCategoryData)obj).downloadUrl, type_of_data,//
							((RecommendCategoryData)obj).categoryName).start();
				}
			}
		}

	};

	class CategoryAdapter extends BaseAdapter {

		public CategoryAdapter() {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return categoryList == null ? 0 : categoryList.size();
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
			RecommendCategoryData category = categoryList.get(position);
			TextView tv = new TextView(ActivityRecommend.this);
			tv.setTag(category);
			tv.setText(category.categoryName + "");
			return tv;
		}

	}

}

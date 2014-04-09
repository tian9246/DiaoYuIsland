package com.clov4r.android.recommend;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import org.mummy.activity.R;
import org.mummy.activity.WelcomeActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clov4r.android.nil.ActivityMoboLineVideo;
import com.clov4r.android.nil.Global;
import com.clov4r.android.recommend.adapter.ChannelAdapter;
import com.clov4r.android.recommend.adapter.ItemAdapter;
import com.clov4r.android.recommend.lib.DataSaveLib;
import com.clov4r.android.recommend.lib.RecommendCategoryData;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.recommend.lib.RecommendLib;
import com.clov4r.android.recommend.view.RecommendTitleView;
import com.clov4r.android.recommend.view.TitleFlowIndicator;
import com.clov4r.android.recommend.view.ViewFlow;
import com.clov4r.android.recommend.view.ViewFlow.OnScreenChangedListener;

public class RecommendActivity extends Activity {
	ArrayList<RecommendCategoryData> oldCategoryList = new ArrayList<RecommendCategoryData>();
	ArrayList<RecommendCategoryData> categoryList = new ArrayList<RecommendCategoryData>();
	ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	public static final int type_of_category = 1;
	public static final int type_of_data = 2;
	public static final int type_of_click_item = 3;

	RecommendLib mRecommendLib = null;
	DataSaveLib mDataSaveLib = null;

	private ViewFlow viewFlow;
	ProgressBar loading = null;

	RecommendTitleView titleView = null;
	Stack<String> titleStack = new Stack<String>();

	int pageIndex = 0;
	OnScreenChangedListener mListener = new OnScreenChangedListener() {
		@Override
		public void onChanged(int index) {
			// TODO Auto-generated method stub
			pageIndex = index;
			loading.setVisibility(View.VISIBLE);
			RecommendCategoryData tempData = categoryList.get(index);
			RecommendCategoryData category = tempData;
			tempData.showCount++;
			categoryList.set(index, tempData);
			new DownloadThread(category, type_of_data).start();
			titleStack.removeAllElements();
			titleView.enterNext(0, tempData.categoryName);
			titleStack.add(tempData.categoryName);
			new UploadThread(tempData.categoryName, null).start();
		}
	};
	ChannelAdapter mChannelAdapter = null;
	ItemAdapter itemAdapter;

	DisplayMetrics dm = null;
	int screenWidth = 0, screenHeight = 0;

	SharedPreferences sp = null;
	Editor editor = null;
	long lastTime = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("推荐位~~~~~~", "onCreate");
		sp = getSharedPreferences("delete_recommend_img", 0);
		editor = sp.edit();
		lastTime = sp.getLong("last_update_time", 0);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.layout_recommend);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		if (screenWidth > screenHeight) {
			int temp = screenHeight;
			screenHeight = screenWidth;
			screenWidth = temp;
		}

		Global.setColums(RecommendActivity.this);
		initViews();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Global.setColums(RecommendActivity.this);
		mChannelAdapter.notifyDataSetChanged();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			titleView.setWidth(screenHeight);
			titleView.measure(0, 0);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			titleView.setWidth(screenWidth);
			titleView.measure(0, 0);
		}
	}

	RelativeLayout adLayout = null;
	IntentFilter filter = null;

	void initViews() {
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		loading = (ProgressBar) findViewById(R.id.loading);
		mChannelAdapter = new ChannelAdapter(this);
		mChannelAdapter.setHandler(mHandler);
		mChannelAdapter.setOnClickListener(mOnClickListener);
		viewFlow.setAdapter(mChannelAdapter);
		viewFlow.setOnScreenChangedListener(mListener);
		// itemAdapter = new ItemAdapter(Global.getRelationMap(dataList.size()),
		// this, dataList);
		titleView = (RecommendTitleView) findViewById(R.id.title_view);

		loading.setVisibility(View.VISIBLE);

		mDataSaveLib = new DataSaveLib(this, DataSaveLib.flag_recommend_data,
				"recommend_category.tmp");
		oldCategoryList = (ArrayList<RecommendCategoryData>) mDataSaveLib
				.getData();

		if (!checkWifi()) {
			if (checkMobile()) {
				showDialog(dialog_msg_no_wifi);
			} else {
				showToast(getString(R.string.no_net));
				finish();
				return;
			}
		} else {
			filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);// "android.net.conn.CONNECTIVITY_CHANGE"
			registerReceiver(networkListener, filter);
			startDownload();
		}
	}

	void startDownload() {
		String categoryUrl = null;
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			categoryUrl = RecommendLib.jsonCategoryUrl;
		} else {
			categoryUrl = RecommendLib.jsonCategoryUrl_en;
		}

		new DownloadThread(categoryUrl, type_of_category,//
				"root_channel").start();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (titleStack.size() > 1) {
					titleStack.pop();
					String title = titleStack.peek();
					mDataSaveLib = new DataSaveLib(this,
							DataSaveLib.flag_recommend_data, title + ".tmp");
					try {
						dataList = (ArrayList<RecommendData>) mDataSaveLib
								.getData();
						titleView.backPrevious();
						mChannelAdapter.setRecommendData(dataList, pageIndex);
					} catch (Exception e) {

					}
					return true;
				} else {
					finish();
					Intent intent = new Intent(this,WelcomeActivity.class);
					startActivity(intent);
					return super.dispatchKeyEvent(event);
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (filter != null) {
			try {
				unregisterReceiver(networkListener);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case RecommendLib.msg_download_success:// 下载成功
				// recommendData = mRecommendLib.getAppList();// 获取所有应用
				// if (recommendData != null && recommendData.size() > 0)
				// getRecommendKind(recommendData);// 获取所有分类
				// else {
				// Toast.makeText(RecommendActivity.this,
				// getString(R.string.recommend_download_failed),
				// Toast.LENGTH_SHORT);
				// }
				// refresh();
				break;
			case RecommendLib.msg_download_failure:
				Toast.makeText(RecommendActivity.this, "下载失败！",
						Toast.LENGTH_LONG);
				break;
			case RecommendLib.msg_img_download_success:
				itemAdapter.notifyDataSetChanged();
				break;
			}
		}

	};

	private final int msg_show_next = 111;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RecommendLib.msg_download_failure:
				// Toast.makeText(RecommendActivity.this,
				// R.string.data_download_failed, Toast.LENGTH_SHORT)
				// .show();
				showToast(getString(R.string.data_download_failed));
				loading.setVisibility(View.GONE);
				break;

			case RecommendLib.msg_download_success:
				int type = msg.arg1;
				if (type == type_of_category) {
					mChannelAdapter.setCategoryData(categoryList);
					TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
					indicator.setTitleProvider(mChannelAdapter);
					viewFlow.setFlowIndicator(indicator);
					// new DownloadThread(categoryList.get(0).downloadUrl,
					// type_of_data,//
					// categoryList.get(0).categoryName).start();
					new DownloadThread(categoryList.get(0), type_of_data)
							.start();
					String categoryName = categoryList.get(0).categoryName;
					// if (!hasEntered(categoryName)) {
					titleView.enterNext(0, categoryName);
					titleStack.add(categoryName);
					// }
				} else if (type == type_of_data) {
					loading.setVisibility(View.GONE);
					mChannelAdapter.setRecommendData(dataList, pageIndex);

				}

				break;

			case RecommendLib.msg_img_download_success:
				// mChannelAdapter.notifyDataSetChanged();
				View layoutView = viewFlow.getChildAt(pageIndex);
				if (layoutView != null) {
					ListView listView = (ListView) layoutView
							.findViewById(R.id.recommend_data_list);
					if (listView != null) {
						BaseAdapter adapter = (BaseAdapter) listView
								.getAdapter();
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						}
					}
				}
				break;
			case RecommendLib.msg_download_sign:
				RecommendData data = (RecommendData) msg.obj;
				if (data != null) {
					new DownloadThread(data, type_of_data// .nextLevelUrl,data.appName
					).start();
				}
				break;
			case msg_show_next:
				if (msg.obj != null) {
					data = (RecommendData) msg.obj;
					titleView.enterNext(1, data.appName);
					titleStack.add(data.appName);
					loading.setVisibility(View.GONE);
				}
				break;
			}
		}
	};

	class UploadThread extends Thread {
		String itemName = null;
		String channelName = null;

		public UploadThread(String channelName, String itemName) {
			this.itemName = itemName;
			this.channelName = channelName;
		}

		@Override
		public void run() {
			mRecommendLib = new RecommendLib(mHandler);
			mRecommendLib.uploadStatistics(channelName, itemName);
		}

	}

	class DownloadThread extends Thread {
		String downloadUrl = null;
		int type = 0;
		String channelName = null;

		RecommendCategoryData mRecommendCategoryData = null;
		RecommendData mRecommendData = null;

		public DownloadThread(RecommendData data, int type) {
			mRecommendLib = new RecommendLib(mHandler);
			this.type = type;
			downloadUrl = data.nextLevelUrl;
			channelName = data.appName;
			mRecommendData = data;
		}

		public DownloadThread(RecommendCategoryData data, int type) {
			mRecommendLib = new RecommendLib(mHandler);
			this.type = type;
			downloadUrl = data.downloadUrl;
			channelName = data.categoryName;
			mRecommendCategoryData = data;
		}

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
					// oldCategoryList = categoryList;
					mDataSaveLib = new DataSaveLib(RecommendActivity.this,
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
				if (mRecommendCategoryData != null) {
					if (hasUpdatedCategory(mRecommendCategoryData)) {
						dataList = getDataList(mRecommendCategoryData.categoryName);
						if (dataList == null || dataList.size() == 0) {
							dataList = mRecommendLib.downloadData(downloadUrl);
							// dataList = mRecommendLib.getDataList();
						}
					} else {
						dataList = mRecommendLib.downloadData(downloadUrl);
						// dataList = mRecommendLib.getDataList();
					}

				} else {
					dataList = mRecommendLib.downloadData(downloadUrl);
					// dataList = mRecommendLib.getDataList();
				}

				boolean needDelete = true;

				long currentTime = System.currentTimeMillis();
				if (currentTime - lastTime > 60 * 60 * 1000) {
					editor.putLong("last_update_time", currentTime);
					editor.commit();
					needDelete = true;
				} else
					needDelete = false;

				if (dataList != null && dataList.size() > 0) {
					for (int i = 0; i < dataList.size(); i++) {
						mRecommendLib.downloadImage(dataList.get(i).appImgUrl,
								mHandler, needDelete);
						mRecommendLib.downloadImage(
								dataList.get(i).appPropertyImgUrl, mHandler,
								false);
					}
					mDataSaveLib = new DataSaveLib(RecommendActivity.this,
							DataSaveLib.flag_recommend_data, channelName
									+ ".tmp");
					mDataSaveLib.saveData(dataList);
					if (mRecommendData != null) {
						Message msg2 = new Message();
						msg2.what = msg_show_next;
						msg2.obj = mRecommendData;
						mHandler.sendMessage(msg2);
					}
					msg.what = RecommendLib.msg_download_success;
					mHandler.sendMessage(msg);
				} else {
					msg.what = RecommendLib.msg_download_failure;
					mHandler.sendMessage(msg);

				}
			}
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Object obj = v.getTag();
			Object indexObj = v.getTag(R.id.tag_recommend_index);
			int clickIndex = 0;
			if (indexObj != null) {
				try {
					clickIndex = Integer.parseInt(indexObj.toString());

				} catch (Exception e) {

				}
			}
			if (obj != null && obj instanceof RecommendData) {
				RecommendData data = (RecommendData) obj;
				int dataLevel = data.dataLevel;
				if (dataLevel == 1) {
					String url = data.appUrl_1;
					String url_2 = data.appUrl_2;
					String action = data.intentAction;
					String smsBody = data.smsBody;
					String next_url = data.nextLevelUrl;
					int type = data.dataType;
					try {
						if (url != null) {
							if (type == RecommendData.TYPE_PLAY_VIDEO) {
								try {
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									ComponentName component = new ComponentName(
											ActivityMoboLineVideo.componentName,
											ActivityMoboLineVideo.activityName);
									Uri uri = Uri.parse(url);
									intent.setData(uri);
									intent.setComponent(component);
									startActivity(intent);
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(
											RecommendActivity.this,
											getResources()
													.getString(
															R.string.please_download_mobo),
											Toast.LENGTH_LONG).show();
								}
							} else {
								Intent urlIntent = new Intent(action);
								if (type == RecommendData.TYPE_SMS
										&& smsBody != null) {
									Uri uri = Uri.parse("smsto:"
											+ data.phoneNumber);
									urlIntent.setData(uri);
									urlIntent.putExtra("sms_body", smsBody);
								} else if (type == RecommendData.TYPE_OPEN_APP) {
									// ComponentName com = new
									// ComponentName(url,
									// url_2);
									// urlIntent.setComponent(com);
									// urlIntent.setData(Uri.parse(next_url));
									Class activityClass = Class.forName(url_2);
									urlIntent = new Intent(
											RecommendActivity.this,
											activityClass);
									urlIntent.setData(Uri.parse(next_url));
								} else
									urlIntent.setData(Uri.parse(url));
								startActivity(urlIntent);
							}
						}
					} catch (ActivityNotFoundException anfe) {
						Intent intent = new Intent();
						intent.setData(Uri.parse(next_url));
						intent.setClass(RecommendActivity.this,
								ActivityMoboLineVideo.class);
						startActivity(intent);
					} catch (java.lang.SecurityException se) {
						Intent intent = new Intent();
						intent.setData(Uri.parse(next_url));
						intent.setClass(RecommendActivity.this,
								ActivityMoboLineVideo.class);
						startActivity(intent);
					} catch (Exception ee) {
						ee.printStackTrace();
						try {
							if (url_2 != null) {
								if (type == RecommendData.TYPE_PLAY_VIDEO) {
									// SystemPlayer.startPlayer(
									// RecommendActivity.this, 0, url_2);
								} else {
									Intent urlIntent = new Intent(action);
									if (type == RecommendData.TYPE_SMS
											&& smsBody != null) {
										Uri uri = Uri.parse("smsto:"
												+ data.phoneNumber);
										urlIntent.setData(uri);
										urlIntent.putExtra("sms_body", smsBody);
									} else if (type == RecommendData.TYPE_OPEN_APP) {
										ComponentName com = new ComponentName(
												url, url_2);
										urlIntent.setComponent(com);
										urlIntent.setData(Uri.parse(next_url));
									} else
										urlIntent.setData(Uri.parse(url_2));
									startActivity(urlIntent);
								}
							}
						} catch (java.lang.SecurityException se) {
							Intent intent = new Intent();
							intent.setData(Uri.parse(next_url));
							intent.setClass(RecommendActivity.this,
									ActivityMoboLineVideo.class);
							startActivity(intent);
						} catch (Exception eee) {
							eee.printStackTrace();
						}
					}
				} else {
					if (!hasEntered(data.appName)) {
						Message msg = new Message();
						msg.obj = data;
						msg.what = RecommendLib.msg_download_sign;
						mHandler.sendMessage(msg);
						loading.setVisibility(View.VISIBLE);
					}
				}

				if (clickIndex < dataList.size()) {
					dataList.get(clickIndex).clickCount++;
					new UploadThread(null, dataList.get(clickIndex).appName)
							.start();
				}
			}
		}
	};

	/**
	 * 该频道是否已经更新过了
	 * 
	 * @param mRecommendCategoryData
	 *            待更新的频道的数据结构
	 * @return
	 */
	boolean hasUpdatedCategory(RecommendCategoryData mRecommendCategoryData) {
		if (oldCategoryList != null && mRecommendCategoryData != null) {
			for (int i = 0; i < oldCategoryList.size(); i++) {
				RecommendCategoryData tempData = oldCategoryList.get(i);
				String tempName = tempData.categoryName + "";
				String name = mRecommendCategoryData.categoryName + "";
				if (tempName.equals(name)) {
					if (tempData.pubDate >= mRecommendCategoryData.pubDate) {
						return true;
					}

				}
			}
		}
		return false;
	}

	/**
	 * 获取卡中保存的数据
	 * 
	 * @param categoryName
	 * @return
	 */
	ArrayList<RecommendData> getDataList(String categoryName) {
		ArrayList<RecommendData> tempList = null;

		mDataSaveLib = new DataSaveLib(RecommendActivity.this,
				DataSaveLib.flag_recommend_data, categoryName + ".tmp");

		tempList = (ArrayList<RecommendData>) mDataSaveLib.getData();
		return tempList;
	}

	boolean hasEntered(String title) {
		if (title == null) {
			title = "";
			return true;
		}
		if (titleStack.size() == 0) {
			return false;
		} else {
			for (int i = 0; i < titleStack.size(); i++) {
				String tempTitle = titleStack.get(i);
				if (tempTitle != null) {
					if (tempTitle.equals(title)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	Toast toast = null;

	private void showToast(String content) {
		if (toast == null)
			toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_SHORT);
		LinearLayout layout = new LinearLayout(this);
		TextView textView = new TextView(this);
		textView.setText(content);
		textView.setShadowLayer(1.0f, 1.0f, 1.0f, Color.BLACK);
		layout.addView(textView);
		toast.setView(layout);
		toast.show();
	}

	BroadcastReceiver networkListener = new BroadcastReceiver() {
		int lastNetworkState = -1;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// String action = intent.getAction();
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			NetworkInfo mobileNetwork = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetwork = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (info == null || !manager.getBackgroundDataSetting()) {// 网络中断

			} else {
				int networkState = info.getType(); // 联通：3g:HSPA;2g:EDGE ; 移动：
													// 电信：CDMA - EvDo rev. A
				if (lastNetworkState != networkState) {// 网络状态发生改变了
					// textView.setText(info.getTypeName());
					if (wifiNetwork.isAvailable()) {// 当前网络是wifi

					} else if (mobileNetwork.isAvailable()) {// 当前网络是mobile
						showDialog(dialog_msg_network_changed);
					}
				}
				lastNetworkState = networkState;
			}
		}
	};

	/**
	 * 检测wifi是否可用
	 * 
	 * @return
	 */
	public boolean checkWifi() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null) {
			State wifiState = wifiNetwork.getState();
			if (wifiState != null)
				return wifiState.equals(State.CONNECTED);
		}

		return false;
	}

	/**
	 * 检测移动网络是否可用
	 * 
	 * @return
	 */
	public boolean checkMobile() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetwork = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null) {
			State mobileState = mobileNetwork.getState();
			if (mobileState != null) {
				return mobileState.equals(State.CONNECTED);
			}
		}

		return false;
	}

	final int dialog_msg_no_wifi = 1;
	final int dialog_msg_network_changed = 2;

	@Override
	public Dialog onCreateDialog(final int id) {
		switch (id) {
		case dialog_msg_no_wifi:
		case dialog_msg_network_changed:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle(R.string.app_name);
			builder2.setMessage(R.string.network_changed);
			builder2.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (dialog_msg_network_changed == id) {// 网络发生变化，什么也不作

							} else if (dialog_msg_no_wifi == id) {// 第一次进入没有wifi,继续下载数据
								startDownload();
							}
						}
					});
			builder2.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					});
			builder2.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			builder2.show();
			break;
		}

		return null;
	}

	// // 读取应用和分类
	// public void getRecommendData() {
	// mRecommendLib = new RecommendLib(myHandler);
	// mRecommendLib.readData(this);
	// recommendData = mRecommendLib.getAppList();
	// if (recommendData != null && recommendData.size() > 0){
	// getRecommendKind(recommendData);
	// refresh();
	// }else{
	// mRecommendLib.downloadData();
	// }
	// }

}

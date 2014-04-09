package com.clov4r.android.nil;

import org.mummy.activity.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivityMoboLineVideo extends Activity {
	WebView moboWebView;
	ProgressBar loading = null;
	Button backward, forward, finish;
	String url = null;
	IntentFilter filter = null;
	
	public static final String componentName = "com.clov4r.android.nil";
	/** 缁鎮�**/
	public static final String activityName = "com.clov4r.android.nil.MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_mobolinevideo);

		url = getIntent().getDataString() + "";

		moboWebView = (WebView) findViewById(R.id.mobowebview);

		moboWebView.getSettings().setJavaScriptEnabled(true);
		moboWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.v("url++++", url);
				if (url.contains(".3gp") || url.contains(".mp4")) {
//					SystemPlayer.startPlayer(ActivityMoboLineVideo.this, 0,
//							url);
					try{
						Intent intent = new Intent(Intent.ACTION_VIEW);
						ComponentName component = new ComponentName(componentName,
								activityName);
						Uri uri = Uri.parse(url);
						intent.setData(uri); 
						intent.setComponent(component); 
						startActivity(intent);
					}catch(Exception e){
						e.printStackTrace();
						Toast.makeText(ActivityMoboLineVideo.this,
								getResources().getString(R.string.please_download_mobo),
								Toast.LENGTH_LONG).show();
					}
					
				} else {
					if (url.toLowerCase().contains("download")) {
						Intent viewIntent = new Intent(
								"android.intent.action.VIEW", Uri
										.parse(url));
						startActivity(viewIntent);

					} else
						view.loadUrl(url);
				}
				return false;
			}

			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
				loading.setVisibility(View.VISIBLE);
			}

			public void onPageFinished(WebView view, String url) {
				loading.setVisibility(View.GONE);
			}
		});
		backward = (Button) findViewById(R.id.web_backward);
		forward = (Button) findViewById(R.id.web_forward);
		finish = (Button) findViewById(R.id.web_finish);
		backward.setOnClickListener(mOnClickListener);
		forward.setOnClickListener(mOnClickListener);
		finish.setOnClickListener(mOnClickListener);
		loading = (ProgressBar) findViewById(R.id.loading);
		
		if (checkWifi()) {// 閺堝ifi
			filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);// "android.net.conn.CONNECTIVITY_CHANGE"
			registerReceiver(networkListener, filter);
			moboWebView.loadDataWithBaseURL(url, "",// "http://app.m1905.cn/vod"
					"text/html", "utf-8", "");
			moboWebView.loadUrl(url);// "http://app.m1905.cn/vod"
		} else if (checkMobile()) {
			showDialog(dialog_msg_no_wifi);
		} else {
			Toast.makeText(ActivityMoboLineVideo.this,
					getResources().getString(R.string.no_net),
					Toast.LENGTH_LONG).show();
			ActivityMoboLineVideo.this.finish();
		}

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
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (moboWebView.canGoBack())
					moboWebView.goBack();
				else
					finish();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (backward == v) {
				if (moboWebView.canGoBack())
					moboWebView.goBack();
			} else if (forward == v) {
				if (moboWebView.canGoForward())
					moboWebView.goForward();
			} else if (finish == v) {
				finish();
			}
		}
	};

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

			if (info == null || !manager.getBackgroundDataSetting()) {// 缂冩垹绮舵稉顓熸焽

			} else {
				int networkState = info.getType(); // 閼辨棃锟介敍锟絞:HSPA;2g:EDGE ; 缁夎濮╅敍锟�													// 閻㈠吀淇婇敍娆矰MA - EvDo rev. A
				if (lastNetworkState != networkState) {// 缂冩垹绮堕悩鑸碉拷閸欐垹鏁撻弨鐟板綁娴滐拷
					// textView.setText(info.getTypeName());
					if (wifiNetwork.isAvailable()) {// 瑜版挸澧犵純鎴犵捕閺勭椇ifi

					} else if (mobileNetwork.isAvailable()) {// 瑜版挸澧犵純鎴犵捕閺勭棳obile
						showDialog(dialog_msg_network_changed);
					}
				}
				lastNetworkState = networkState;
			}
		}
	};

	/**
	 * 濡拷绁磜ifi閺勵垰鎯侀崣顖滄暏
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
	 * 濡拷绁寸粔璇插З缂冩垹绮堕弰顖氭儊閸欘垳鏁�
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
							if (dialog_msg_network_changed == id) {// 缂冩垹绮堕崣鎴犳晸閸欐ê瀵查敍灞肩矆娑斿牅绡冩稉宥勭稊

							} else if (dialog_msg_no_wifi == id) {// 缁楊兛绔村▎陇绻橀崗銉︾梾閺堝ifi,缂佈呯敾娑撳娴囬弫鐗堝祦
								moboWebView.loadDataWithBaseURL(url, "",// "http://app.m1905.cn/vod"
										"text/html", "utf-8", "");
								moboWebView.loadUrl(url);// "http://app.m1905.cn/vod"

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

	// //鏉╂柨娲栨稉濠佺娑擃亞缍夋い锟�	// public boolean onKeyDown(int keyCode, KeyEvent event){
	// if(keyCode == KeyEvent.KEYCODE_BACK && moboWebView.canGoBack()){
	// moboWebView.goBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}

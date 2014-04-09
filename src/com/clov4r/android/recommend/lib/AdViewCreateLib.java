package com.clov4r.android.recommend.lib;

//import com.adsmogo.adview.AdsMogoLayout;
//import com.adsmogo.controller.listener.AdsMogoListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdViewCreateLib  {//implements AdsMogoListener
	public static final int TYPE_OF_MOOGO = 1;
	public static final int TYPE_OF_ADMOB = 2;
	
	public static int type_of_ad = AdViewCreateLib.TYPE_OF_MOOGO;


	int type = 1;

	Context context = null;
	private final String mogoID = "15ffc4797e874cc7af16b86e7a58b706";// 13319e0a8857406a993f0b4fd2d9c784
//	AdsMogoLayout mAdMogoLayout = null;

	AdView admobView = null;

	public AdViewCreateLib(Context con, int type) {
		context = con;
		this.type = type;
		// 鏋勯�鏂规硶锛岃缃揩閫熸ā寮�
		if (type == TYPE_OF_MOOGO){
//			mAdMogoLayout = new AdsMogoLayout(((Activity) context),
//					"15ffc4797e874cc7af16b86e7a58b706", false);
		}
		else {
			if (admobView != null)
				admobView.destroy();
			int orientation = con.getResources().getConfiguration().orientation;
			if (orientation == Configuration.ORIENTATION_PORTRAIT)
				admobView = new AdView((Activity) con, AdSize.BANNER,
						"a15084c54be2fc3");
			else
				admobView = new AdView((Activity) con, AdSize.IAB_BANNER,
						"a15084c54be2fc3");
			AdRequest adRequest = new AdRequest();
			adRequest.setTesting(false);
			admobView.loadAd(adRequest);
		}

		// // 璁剧疆鐩戝惉鍥炶皟 鍏朵腑鍖呮嫭 璇锋眰 灞曠ず 璇锋眰澶辫触绛変簨浠剁殑鍥炶皟
		// mAdMogoLayout.setAdsMogoListener(this);

	}

	/**
	 * 鎶婂箍鍛婃坊鍔犲埌layout閲岃竟
	 * 
	 * @param layout
	 * @param params
	 */
	public void addViewTo(ViewGroup layout, ViewGroup.LayoutParams params) {
//		if (mAdMogoLayout != null && mAdMogoLayout.getParent() == null
//				&& layout != null) {
//			if (params != null)
//				layout.addView(mAdMogoLayout, params);
//			else
//				layout.addView(mAdMogoLayout);
//		}
		
//		else 
			if (admobView != null && admobView.getParent() == null
				&& layout != null) {
			if (params != null)
				layout.addView(admobView, params);
			else
				layout.addView(admobView);
		}
	}
	
	public void close(){
		try {

//			AdsMogoLayout.clear();
//			mAdMogoLayout = null;

			if (admobView != null) {
				admobView.destroy();
			}
		} catch (Exception e) {

		}
	}

	/** 鍏抽棴鐣岄潰鐨勬椂鍊欏叧闂箍鍛�**/
	public void closeAdd() {
		try {
//			AdsMogoLayout.clear();
//			mAdMogoLayout = null;
		} catch (Exception e) {

		}
	}

	/**
	 * 鑾峰彇骞垮憡view鐨勫璞�
	 * 
	 * @return
	 */
	public View getAdView() {
		if (type == TYPE_OF_MOOGO)
//			return mAdMogoLayout;
			return null;
		else if (type == TYPE_OF_ADMOB)
			return admobView;

		return null;
	}

//	@Override
//	public void onFailedReceiveAd() {
//		Log.v("=onFailedReceiveAd=", "Failed to receive the buttom ad.");
//	}
//
//	@Override
//	public void onCloseAd() {
//		// TODO Auto-generated method stub
//		Log.v("=onCloseMogoDialog=", "Close ad Dialog.");
//	}
//
//	@Override
//	public void onCloseMogoDialog() {
//		// TODO Auto-generated method stub
//		Log.v("=onCloseAd=", "Close ad.");
//	}
//
//	@Override
//	public void onClickAd(String arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRealClickAd() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onReceiveAd(ViewGroup arg0, String arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestAd(String arg0) {
//		// TODO Auto-generated method stub
//
//	}

}

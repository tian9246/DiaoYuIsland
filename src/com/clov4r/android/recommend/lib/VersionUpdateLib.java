package com.clov4r.android.recommend.lib;

import java.util.HashMap;

public class VersionUpdateLib {
	/** 软件名 **/
	public static String appName = null;
	/** 软件版本号 **/
	public static int appVersion = 0;
	/** 软件版本 **/
	public static String appVersionName = "MoboPlayer 1.2.300";
	/** 软件发布日期 **/
	public static String appPubDate = null;
	/** changelog，你懂的 **/
	public static String appChangeLog = null;
	/** 下载地址--1 **/
	public static String downloadUrl1 = null;
	/** 下载地址--2 **/
	public static String downloadUrl2 = null;
	/** 是否强制升级 ,0:不强制；1，强制升 **/
	public static int updateForce = 0;

	/** 更新的部分内容，便于显示changelog、updateDate、appName等内容 **/
	public static HashMap<String, Object> updateDate = new HashMap<String, Object>();

	/** 判断软件是否更新的地址 **/
	public static final String updateUrl = "http://www.moboplayer.com/update/update.json";

}

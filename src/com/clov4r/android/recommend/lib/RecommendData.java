package com.clov4r.android.recommend.lib;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.clov4r.android.nil.Global;

@SuppressWarnings("serial")
public class RecommendData implements Serializable {
	/** 应用类型数据 **/
	public static final int TYPE_APP = 0;
	/** 拨号类型数据 **/
	public static final int TYPE_DIAL = 1;
	/** 短信类型数据 **/
	public static final int TYPE_SMS = 2;
	/** 打开本地的应用程序 **/
	public static final int TYPE_OPEN_APP = 6;
	/** 播放视频 **/
	public static final int TYPE_PLAY_VIDEO = 7;

	/** 应用的名字 **/
	public String appName = null;
	/** 应用的类型 **/
	public String appType = null;
	/** 应用的展示地址--1 **/
	public String appUrl_1 = null;
	/** 应用的展示地址--2 **/
	public String appUrl_2 = null;
	/** 开发者 **/
	public String appAuthor = null;
	/** 应用的图标下载地址 **/
	public String appImgUrl = null;
	/** 应用的包名 **/
	public String appPackageName = null;
	/** 应用的大小 **/
	public String appSize = null;
	/** 应用的评级 **/
	public float appRate = 0;
	/** 应用的性质：免费、收费...etc **/
	public String appProperty = null;
	/** 应用的性质的图片下载地址 **/
	public String appPropertyImgUrl = null;

	/** 数据级别：0，有子目录；1，最低一级目录...... **/
	public int dataLevel = 0;
	/** 下一级目录对应的地址 **/
	public String nextLevelUrl = null;
	/** 数据类型：0，app；1，电话；2，短信 ;3,广告;5，独占一行的item;6,打开手机的应用程序，也占一行 ;7,播放视频**/
	public int dataType = 0;
	/** intent的action **/
	public String intentAction = null;
	/** SMS内容 **/
	public String smsBody = null;
	/** 需要拨打的电话号码 **/
	public String phoneNumber = null;
	/** 0，market样式；1，淘宝客户端的样式；2，纯图片 ; 3,电影样式(类似market) **/
	public int displayType = 0;
	/** 上次更新时间 **/
	public long pubDate = 0;
	/** 子项的个数(有子目录的才有此项) **/
	public int childrenCount = 0;

	/** 展示次数，退出程序时上传服务器，并清0 **/
	public int clickCount = 0;

	/**
	 * 通过图片的Url获取其下载到本地的地址
	 * 
	 * @param url
	 * @return
	 */
	public static String getImagePathByUrl(String url) {
		String fileName = null;
		if (url != null && url.contains("/"))
			fileName = url.substring(url.lastIndexOf("/") + 1);
		return Global.rootPath + File.separator + Global.recommandPath
				+ File.separator + fileName;
	}

}

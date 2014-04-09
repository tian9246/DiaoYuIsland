package com.clov4r.android.recommend.lib;

import java.io.Serializable;
import java.util.Locale;

@SuppressWarnings("serial")
public class Recommend91GameData implements Serializable {
	/** 中文的地址 **/
	public String ch_url = null;
	/** 英文的地址 **/
	public String en_url = null;
	/** 上次更新时间 **/
	public long lastUpdateTime = 0;
	
//	int typeOfAd = AdViewCreateLib.TYPE_OF_ADMOB;

	public String getGameUrl() {
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			if (ch_url != null)
				return ch_url;
		} else {
			if (en_url != null)
				return en_url;
		}
		return ch_url;
	}

	/**
	 * 判断是否要更新数据了，24小时更新一次
	 * 
	 * @return
	 */
	public boolean needUpdate() {
		long currentTimes = System.currentTimeMillis();
		if (currentTimes - lastUpdateTime > 24 * 60 * 60 * 1000) {
			return true;
		} else
			return false;
	}

	/**
	 * 某种语言环境下的地址是否有效
	 * 
	 * @return
	 */
	public boolean needShow() {
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			if (ch_url != null&&!"".equals(ch_url))
				return true;
		} else {
			if (en_url != null&&!"".equals(en_url))
				return true;
		}
		return false;
	}
}

package com.clov4r.android.recommend.lib;

import com.clov4r.android.recommend.lib.AdViewCreateLib;
import java.io.Serializable;
import java.util.Locale;

@SuppressWarnings("serial")
public class RecommendAdData implements Serializable {
	public boolean isEnAdOpend = true;
	public boolean isChAdOpend = false;
	public int typeOfAd = AdViewCreateLib.TYPE_OF_ADMOB;
	/** 是否打开广告 **/
	public static boolean appIsAdOpen = false;

	public void check() {
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			if (isChAdOpend)
				appIsAdOpen = true;
			else
				appIsAdOpen = false;
		} else {
			if (isEnAdOpend)
				appIsAdOpen = true;
			else
				appIsAdOpen = false;
		}
	}
}

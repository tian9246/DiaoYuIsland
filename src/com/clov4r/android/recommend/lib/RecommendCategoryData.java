package com.clov4r.android.recommend.lib;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class RecommendCategoryData implements Serializable {
	public String categoryName = null;
	public String downloadUrl = null;
	public int id = 0;
	public int sortOrder = 0;
	public long pubDate = 0;
	
	/**展示次数，退出程序时上传服务器，并清0**/
	public int showCount=0;

	ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
}

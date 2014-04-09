package com.clov4r.android.nil;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.clov4r.sleepinghelper.nil.library.MediaLibrary;
//import com.clov4r.sleepinghelper.nil.library.MediaLibrary.MediaItem;
import com.clov4r.android.recommend.lib.RecommendData;
import com.clov4r.android.nil.BackgroundAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

public class Global {
	public static String serverAddress = "http://update.moboplayer.com:8060";
	/** 锟斤拷锟斤拷锟斤拷幕锟斤拷转 **/
	public static boolean screen_ratation = false;
	/** 锟街凤拷 **/
	public static String characterSet = "Auto";
	/** 锟斤拷扫锟斤拷锟侥硷拷锟斤拷锟酵碉拷锟斤拷锟斤拷 **/
	public static String typeList[] = null;
	/** 锟斤拷扫锟斤拷锟侥硷拷锟斤拷锟斤拷锟叫憋拷 **/
	public static ArrayList<String> typeArray = new ArrayList<String>();

	public static String rootPath = "/sdcard/.mobo";
	public static String recommandPath = "recommendApp";

	public static final int MEDIA_PLAY_PROCESS_AUTO = 0;
	public static final int MEDIA_PLAY_PROCESS_NONE = 1;
	public static final int MEDIA_PLAY_PROCESS_SOFTDEC = 2;
	public static final int MEDIA_PLAY_PROCESS_HARDDEC = 3;

//	public static SystemPlayer mSystemPlayer = null;
//	public static CMPlayer mCMPlayer = null;

	/** 锟侥硷拷锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷斜锟�**/
	public static ArrayList<Integer> typeSelectArray = new ArrayList<Integer>();
	// public static ArrayList<Boolean> typeSelectArray = new
	// ArrayList<Boolean>();
	/** 锟斤拷锟斤拷锟斤拷锟斤拷诮锟饺伙拷锟斤拷锟斤拷 **/
	public static String volumeKeySetting = "volume_key_valume";
	/** 锟斤拷 Menu时锟角凤拷锟斤拷锟絋oast **/
	public static boolean screenTips = true;
	/** 锟角凤拷锟斤拷示锟斤拷锟斤拷锟斤拷息 **/
	public static boolean showBatteryInfo = true;

	/** 锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷谋锟斤拷锟斤拷锟缴�**/
	public static int backgroundColor = 0x676C81;
	/** 选锟斤拷锟斤拷色时锟斤拷时锟斤拷色锟斤拷值 **/
	public static int tempBgColor = 0x676C81;
	/** 默锟较碉拷锟斤拷锟斤拷值 **/
	public static float defaultLight = -1;
	/** 默锟较碉拷锟斤拷锟斤拷值 **/
	public static float defaultVolume = -1;
	/** 鏄惁鏄剧ず涓荤晫闈笅杈圭殑宸ュ叿鏉�**/
	public static boolean toolsBarVisible = true;
	/** 锟斤拷幕锟斤拷锟�**/
	public static int screenWidth;
	/** 锟斤拷幕锟竭讹拷 **/
	public static int screenHeight;

	public static boolean isPad = false;
	// public static int bgImgId = 0;
	// public static int bgLastImgId = 0;
	public static int bgImgIndex = 0;
	public static BitmapDrawable currentBitmapDrawable = null;

	public static String operation_of_home_key = "1";

	public static boolean exitCompletely = false;

	public static boolean displayFileNameCircular = true;

	/**鍏冲睆鍚庣户缁挱鏀�*/
	public static boolean playWhileScreenOff = false;

	/** 姣忔鎵撳紑搴旂敤闅忔満鎹㈤鑹�**/
	public static boolean bgColorRandom = false;

	/** 鏄惁鑷嫊鎾斁涓嬩竴闆�**/
	public static boolean playNext = true;
	/** 鑷姩淇濆瓨闊抽噺鍜屼寒搴�**/
	public static boolean save_brightness_volume = true;
	/** 鍙屽嚮閫�嚭涓荤晫闈�**/
	public static boolean double_click_to_quit = false;

	public static boolean seek_by_percent = false;

	public static boolean setting_thumb_animation = false;

	/** 鍗曞嚮杩斿洖閿鏃舵鏁�**/
	public static int hasClickTimes = 0;
	/** 宸茬粡鍗曞嚮杩斿洖閿殑娆℃暟 **/
	public static int hasClickedBack = 0;

	/** 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟狡�锟斤拷锟斤拷泳锟�**/
	static ArrayList<String> teleplayList = new ArrayList<String>();

//	public static boolean initVideoList(String path, Context context) {
//		teleplayList.clear();
//		if (!playNext) {
//			currentIndex = 0;
//			return false;
//		}
//		matcherStr1 = context.getString(R.string.play_next_di) + ".*"
//				+ context.getString(R.string.play_next_ji);
//		try {
//			teleplayList.addAll(getSimilarMovie(path));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if (teleplayList.size() > 0)
//			return true;
//		else
//			return false;
//	}

	static int currentIndex = -1;

	/** 锟叫断革拷影片锟角诧拷锟斤拷锟斤拷锟斤拷锟�**/
//	public static ArrayList<String> getSimilarMovie(String path)
//			throws Exception {
//		ArrayList<String> result = new ArrayList<String>();
//		if (path == null || "".equals(path) || !path.contains("."))
//			return result;
//		File file = new File(path);
//		if (!file.exists())
//			return result;
//		path = path.trim();// .toLowerCase()
//		String[] sources = file.getParentFile().list();
//		String parentPath = file.getParent() + File.separator;
//		String format = path.substring(path.lastIndexOf(".") + 1);
//		if ((path.lastIndexOf("/") + 1) > path.lastIndexOf("."))
//			return result;
//		String sourceName = path.substring(path.lastIndexOf("/") + 1,
//				path.lastIndexOf("."));
//		for (int i = 0; i < sources.length; i++) {
//			String tmpPath = parentPath + sources[i].trim();// .toLowerCase()
//			if (!tmpPath.contains("."))
//				continue;
//			if (tmpPath.equals(path)) {
//				// currentIndex = result.size();
//				result.add(path);
//				continue;
//			}
//			if (tmpPath.lastIndexOf("/") > tmpPath.lastIndexOf("."))
//				continue;
//			String fileName = tmpPath.substring(tmpPath.lastIndexOf("/") + 1,
//					tmpPath.lastIndexOf("."));
//			if (tmpPath.endsWith(format)) {// 锟斤拷锟斤拷锟斤拷锟侥硷拷锟侥革拷式锟斤拷同
//				if (fileName.length() > sourceName.length() + 3
//						|| sourceName.length() > fileName.length() + 3)
//					continue;
//				if (numCheck(sourceName, numStart)) {// 源锟侥硷拷锟斤拷锟斤拷锟街匡拷头
//					if (numCheck(fileName, numStart)) {
//						if (compareFileName(sourceName, fileName, true))
//							result.add(tmpPath);
//					}
//				} // else
//				if (numCheck(sourceName, numEnd)) {// 源锟侥硷拷锟斤拷锟斤拷锟街斤拷尾
//					if (numCheck(fileName, numEnd)) {
//						if (compareFileName(sourceName, fileName, false))
//							result.add(tmpPath);
//						continue;
//					}
//				} // else {// 锟斤拷锟斤拷锟斤拷(锟斤拷锟斤拷锟斤拷:***cd1**/***01***/)
//				if (replaceFlag(sourceName, matcherStr1).equals(
//						replaceFlag(fileName, matcherStr1)))
//					result.add(tmpPath);
//				else if (replaceFlag(sourceName, matcherStr2).equals(
//						replaceFlag(fileName, matcherStr2)))
//					result.add(tmpPath);
//				else if (replaceFlag(sourceName, matcherStr3).equals(
//						replaceFlag(fileName, matcherStr3)))
//					result.add(tmpPath);
//				else if (replaceFlag(sourceName, numMat).equals(
//						replaceFlag(fileName, numMat)))
//					result.add(tmpPath);
//				// }
//			}
//		}
//		if (result != null)
//			Collections.sort(result, OrderName);
//		for (int i = 0; i < result.size(); i++) {
//			if (path.equals(result.get(i))) {
//				currentIndex = i;
//				break;
//			}
//		}
//		return result;
//	}
	
	
	
	


//	static Comparator<String> OrderName = new Comparator<String>() {
//		public int compare(String s1, String s2) {
//			if (s1 == null) {
//				return 1;
//			}
//
//			if (s2 == null) {
//				return -1;
//			}
//
//			s1 = s1.toLowerCase();
//			s2 = s2.toLowerCase();
//
//			if (s1.equals(s2)) {
//				return 0;
//			}
//
//			List<String> ss1 = MyComparator.split(s1);
//			List<String> ss2 = MyComparator.split(s2);
//
//			// 鍙栦袱涓瘮杈冨崟鍏冪殑鏈�皬闀垮害
//			int len = ss1.size() < ss2.size() ? ss1.size() : ss2.size();
//
//			// 姣旇緝缁撴灉
//			int r = 0;
//
//			String t1 = null;
//			String t2 = null;
//			boolean b1 = false;
//			boolean b2 = false;
//
//			for (int i = 0; i < len; i++) {
//				t1 = ss1.get(i);
//				t2 = ss2.get(i);
//
//				b1 = Character.isDigit(t1.charAt(0));
//				b2 = Character.isDigit(t2.charAt(0));
//
//				// t1鏄暟瀛�t2闈炴暟瀛�
//				if (b1 && !b2) {
//					return -1;
//				}
//
//				// t2鏄暟瀛�t1闈炴暟瀛�
//				if (!b1 && b2) {
//					return 1;
//				}
//
//				// t1銆乼2 闈炴暟瀛�
//				if (!b1 && !b2) {
//					r = t1.compareTo(t2);
//					if (r != 0) {
//						return r;
//					}
//				}
//
//				// t1 t2閮芥槸鏁板瓧
//				if (b1 && b2) {
//
//					r = MyComparator.compareNumber(t1, t2);
//
//					if (r != 0) {
//						return r;
//					}
//				}
//
//			}
//			// 濡傛灉涓や釜闆嗗悎鐨�0-(len-1)閮ㄥ垎鐩哥瓑
//			if (r == 0) {
//				if (ss1.size() > ss2.size()) {
//					r = 1;
//				} else if (ss1.size() < ss2.size()) {
//					r = -1;
//				} else {
//					r = MyComparator.compareNumberPart(s1, s2);
//				}
//			}
//
//			return r;
//		}
//		
//	};

	public static boolean compareFileName(String name1, String name2,
			boolean checkStart) {
		try {
			if (checkStart) {//閮芥槸鏁板瓧寮�ご
				int index1 = 0, index2 = 0;
				for (int i = name1.length() - 1; i >= 0; i++) {
					if (!Character.isDigit(name1.charAt(i))) {
						index1 = i;
						break;
					}
				}
				for (int i = name2.length() - 1; i >= 0; i++) {
					if (!Character.isDigit(name2.charAt(i))) {
						index2 = i;
						break;
					}
				}
				if ((index1 > index2 + 3) || (index1 < index2 - 3))
					return false;
				String tmpName1 = name1.substring(0, index1);
				String tmpName2 = name2.substring(0, index2);
				if (tmpName1.equals(tmpName2))
					return true;
			} else {//閮芥槸鏁板瓧缁撳熬
				int index1 = 0, index2 = 0;
				for (int i = 0; i <= name1.length() - 1; i++) {
					if (Character.isDigit(name1.charAt(i))) {
						index1 = i;
						break;
					}
				}
				for (int i = 0; i <= name2.length() - 1; i++) {
					if (Character.isDigit(name2.charAt(i))) {
						index2 = i;
						break;
					}
				}
				if ((index1 > index2 + 3) || (index1 < index2 - 3))
					return false;
				String tmpName1 = name1.substring(0, index1);
				String tmpName2 = name2.substring(0, index2);
				if (tmpName1.equals(tmpName2))
					return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	final static String numMat = "[0-9]+";// 锟斤拷锟街碉拷锟叫讹拷
	final static String numStart = "^(" + numMat + ").*";// 锟斤拷锟街匡拷头
	final static String numEnd = ".*(" + numMat + ")$";// 锟斤拷锟街斤拷尾
	static String matcherStr1 = "第.*集";
	final static String matcherStr2 = "cd.*";
	final static String matcherStr3 = "CD.*";

	static Pattern pattern = null;
	static Matcher matcher = null;

	public static boolean numCheck(String path, String match) {
		pattern = Pattern.compile(match);
		matcher = pattern.matcher(path);
		return matcher.find();
	}

	public static String replaceFlag(String path, String flag) {
		if (!numCheck(path, flag))
			return path;
		pattern = Pattern.compile(flag);
		matcher = pattern.matcher(path);
		String result = matcher.replaceAll("");
		return matcher.replaceAll("");
	}

	private static BitmapDrawable bd = null, bd_h;

//	public static boolean initBackground(Context context) {
//		bd = null;
//		bd_h = null;
//		String path = "/sdcard/.mobo/background/mobo_bg.png";
//		String path2 = "/sdcard/.mobo/background/mobo_bg_h.png";
//		File file = new File(path);
//		File file2 = new File(path2);
//		try {
//			if (file != null && file.exists()) {
//				Bitmap bitmap = getBitmap(path, screenWidth);
//				bd = new BitmapDrawable(context.getResources(), bitmap);
//			}
//
//			if (file2 != null && file2.exists()) {
//				Bitmap bitmap = getBitmap(path2, screenWidth);
//				bd_h = new BitmapDrawable(context.getResources(), bitmap);
//			}
//		} catch (Error e) {
//
//		}
//
//		return bd == null ? false : true;
//	}

	// public static void initBitmaps(Resources res) {
	// int resourceId[] = new int[] { 0, R.drawable.pad_layout_bg,
	// R.drawable.pad_layout_bg_1, R.drawable.pad_layout_bg_2,
	// R.drawable.pad_layout_bg_3, R.drawable.pad_layout_bg_4,
	// R.drawable.pad_layout_bg_5 };
	//
	// imageBgs = new BitmapDrawable[resourceId.length];
	// try {
	// for (int i = 0; i < resourceId.length; i++) {
	// Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId[i]);
	// imageBgs[i] = new BitmapDrawable(res, bitmap);
	// if(imageBgs[i]!=null){
	// imageBgs[i].setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	// }
	// }
	// } catch (Error e) {
	//
	// }
	// }

//	private static Bitmap getBitmap(String path, int height) {
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.outHeight = height;
//		options.inJustDecodeBounds = true;
//		Bitmap bm = BitmapFactory.decodeFile(path, options);
//		options.inJustDecodeBounds = false;
//		options.inSampleSize = GridAdapter.computeSampleSize(options, -1, 
//				height * height);  
//		options.inPreferredConfig = Bitmap.Config.RGB_565;
//
//		return BitmapFactory.decodeFile(path, options);
//	}

	static HashMap<String, Bitmap> recommendMap = new HashMap<String, Bitmap>();

	public static Bitmap getBitmap(String path) {
		Bitmap bitmap = recommendMap.get(path);
		if (bitmap != null)
			return bitmap;
		else {
			bitmap = BitmapFactory.decodeFile(path);
			recommendMap.put(path, bitmap);
		}
		return bitmap;
	}

	public static void setBackground(ViewGroup view, int orientation) {
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (bd != null) {
				view.setBackgroundDrawable(bd);
			} else {
				view.setBackgroundDrawable(currentBitmapDrawable);
			}
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (bd_h != null) {
				view.setBackgroundDrawable(bd_h);
			} else {
				view.setBackgroundDrawable(currentBitmapDrawable);
			}
		}
	}

	static String musicEndings[] = { "WAV", "MP3", "WMA", "OGG", "APE", "ACC", };

	/**
	 * 鍒ゆ柇鏄笉鏄煶涔愭枃浠�
	 * 
	 * @param path
	 * @return
	 */
	public static boolean checkIsMusic(String path) {
		if (path == null || "".equals(path) || !path.contains("/")
				|| !path.contains("."))
			return false;
		else {
			int end = path.lastIndexOf(".") + 1;
			if (end < 0)
				end = path.length() - 1;
			String ending = path.substring(end);
			if (ending != null) {
				for (int i = 0; i < musicEndings.length; i++) {
					String tempEnding = musicEndings[i];
					if (tempEnding.equalsIgnoreCase(ending))
						return true;
				}
			}
		}
		return false;
	}

	static int statusBarHeight = 0;

	public static int getStatusBarHeight(Context con) {
		if (statusBarHeight != 0)
			return statusBarHeight;
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		Field[] f = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			f = c.getFields();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = con.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		statusBarHeight = sbar;
		return sbar;
	}

	public static BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ("finish".equals(intent.getAction())) {
				((Activity) context).finish();
			}
		}
	};
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {

			return false;

		} else {

			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {

				for (int i = 0; i < info.length; i++) {

					if (info[i].getTypeName() != null
							&& info[i].getTypeName().equalsIgnoreCase("WIFI")
							&& info[i].getState() == NetworkInfo.State.CONNECTED) {

						return true;

					}

				}

			}

		}

		return false;

	}


	public static int colums = 3;
	public static DisplayMetrics dm  = new DisplayMetrics();
	public class CountPair {
		public int startIndex = 0;
		public int count = 0;
	}

	/**
	 * 鏍规嵁姣忚鏄剧ず涓暟璁＄畻hashmap
	 * 
	 * @param count
	 *            (list.size())
	 * @return
	 */
	public static HashMap<Integer, CountPair> getRelationMap(
			ArrayList<RecommendData> dataList) {
		int count = dataList.size();
		HashMap<Integer, CountPair> relationMap = new HashMap<Integer, CountPair>();
		int lineNum = 0;// 鎬诲叡鐨勮鏁�

		int index = 0;
		CountPair countPair = new Global().new CountPair();
		for (int i = 0; i < dataList.size(); i++) {
			RecommendData tempData = dataList.get(i);
			if (index == 0) {
				countPair.startIndex = i;
			}
			index++;
			if (tempData.dataType == 3 || tempData.dataType == 5|| tempData.dataType == 6) {
				if (index % Global.colums != 1) {
					if (i == 0)
						countPair.count = index;
					else
						countPair.count = index - 1;

					relationMap.put(lineNum, countPair);
					countPair = new Global().new CountPair();
					lineNum++;
				}

				countPair.startIndex = i;
				countPair.count = 1;
				relationMap.put(lineNum, countPair);
				countPair = new Global().new CountPair();
				lineNum++;

				index = 0;
			} else if (index >= Global.colums || i == dataList.size() - 1) {
				countPair.count = index;
				relationMap.put(lineNum, countPair);
				countPair = new Global().new CountPair();
				lineNum++;
				index = 0;

			}
		}

		return relationMap;
	}

	public static void setColums(Context con) {
		double screenSize = 0;
		int orientation = con.getResources().getConfiguration().orientation;
		if (Global.screenHeight >= 720 && Global.screenWidth >= 1000
				&& dm.density > 0) {
			screenSize = Math.sqrt(Global.screenWidth * Global.screenWidth
					+ Global.screenHeight * Global.screenHeight)
					/ (160 * dm.density);
		}

		if (screenSize > 7) {
			// Global.isPad = true;
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				colums = 3;
			} else {
				colums = 4;
			}
		} else {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				colums = 2;
			} else {
				colums = 3;
			}
		}
	}

}

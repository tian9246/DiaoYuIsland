package com.clov4r.android.recommend.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.clov4r.android.nil.Global;
import com.clov4r.android.nil.Version;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RecommendLib {
	public String jsonText1 = "\"appAuthor\":\"MoboTeam\",\"appName\":\"MoboPlayer\",\"appType\":\"娱乐\",\"appImgUrl\":\"http://moboplayer.com/logo.jpg\",\"appPackageName\":\"com.clov4r.android.nil\",\"appSize\":\"5MB\",\"appRate\":\"5\",\"appProperty\":\"Free\",\"appPropertyImgUrl\":\"http://MoboPlayer.com/download.jpg\",\"appUrl_1\":\"www.moboplayer.com\",\"appUrl_2\":\"www.moboplayer.com\"";

	public String jsonText2 = "\"appAuthor\":\"MoboTeam\",\"appName\":\"Game\",\"appType\":\"游戏\",\"appImgUrl\":\"http://moboplayer.com/logo.jpg\",\"appPackageName\":\"com.clov4r.android.nil\",\"appSize\":\"5MB\",\"appRate\":\"5\",\"appProperty\":\"Free\",\"appPropertyImgUrl\":\"http://MoboPlayer.com/download.jpg\",\"appUrl_1\":\"www.moboplayer.com\",\"appUrl_2\":\"www.moboplayer.com\"";

	public String jsonText3 = "\"appAuthor\":\"MoboTeam\",\"appName\":\"Commercial\",\"appType\":\"财经\",\"appImgUrl\":\"http://moboplayer.com/logo.jpg\",\"appPackageName\":\"com.clov4r.android.nil\",\"appSize\":\"5MB\",\"appRate\":\"5\",\"appProperty\":\"Free\",\"appPropertyImgUrl\":\"http://MoboPlayer.com/download.jpg\",\"appUrl_1\":\"www.moboplayer.com\",\"appUrl_2\":\"www.moboplayer.com\"";

	public final String tempJsonText = "{\"appList\":[" + "{" + jsonText1
			+ "}," + "{" + jsonText2 + "}," + "{" + jsonText3 + "}" + "]}";

	private static String jsonUrl = "http://www.moboplayer.com/update/index.json";// "http://192.168.1.12/mobo_forum/client/index%s.json";
	public final static String jsonCategoryUrl = "http://www.moboplayer.com/update/index_category.json";//
	public final static String jsonCategoryUrl_en = "http://www.moboplayer.com/update/index_category_en.json";//
	public static final String recommendUrl = String.format(jsonUrl, "");
	/** 展示、点击统计的页面 **/
	public static final String recommendStatisticsUrl = "http://www.moboplayer.com/update/statistics.clov4r";
	/** 获取91游戏的链接地址的地址 **/
	public static final String recommend91GameUrl = "http://www.moboplayer.com/update/91_game.json";
	/** 显示广告，及显示哪家广告的地址 **/
	public static final String recommendAdUrl = "http://www.moboplayer.com/update/ad_setting.json";

	public static final String APP_LIST_JSON_KEY = "appList";
	public static final String CATEGORY_LIST_JSON_KEY = "category_list";
	private ArrayList<RecommendData> dataList = new ArrayList<RecommendData>();
	private ArrayList<RecommendCategoryData> categoryList = new ArrayList<RecommendCategoryData>();
	boolean downloading = false;

	SimpleDateFormat mSimpleDateFormat = null;

	static HttpParams httpParams = null;
	static {
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
	}

	static Handler mHandler;
	/** 下载成功 **/
	public static final int msg_download_success = 2220;
	/** 下载失败 **/
	public static final int msg_download_failure = 2221;
	/** 图片下载成功 **/
	public static final int msg_img_download_success = 2222;
	public static final int msg_has_new_version = 2223;
	public static final int msg_has_finished_update = 2224;
	/** 从别的类中调用下载推荐的标记 **/
	public static final int msg_download_sign = 2225;
	/** 下载91游戏频道链接的json文件 **/
	public static final int msg_download_91_game_url = 2226;

	public RecommendLib(Handler handler) {
		mHandler = handler;

		dataSavePath = Global.rootPath + File.separator + Global.recommandPath;
		File file = new File(dataSavePath);
		if (!file.exists())
			file.mkdirs();
		dataSaveFilePath = dataSavePath + File.separator + "recommendData.tmp";
		dataSaveFile = new File(dataSaveFilePath);
		if (!dataSaveFile.exists()) {
			try {
				dataSaveFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	}

	/**
	 * 获取推荐的内容
	 * 
	 * @return
	 */
	private String getData(String jsonUrl) {
		downloading = true;
		String jsonText = null;
		try {
			URL url = new URL(jsonUrl + "?phoneModel=" + android.os.Build.MODEL
					+ "&phoneVersion=" + android.os.Build.VERSION.RELEASE
					+ "&versionNumber=" + String.valueOf(Version.versionNum)
					+ "&lang=" + Locale.getDefault().getLanguage()
					+ "&platform=" + Version.platform + "&currentTime="
					+ String.valueOf(System.currentTimeMillis()));
			HttpURLConnection connection = (HttpURLConnection) (url
					.openConnection());
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			InputStream inputStream = connection.getInputStream();
			if (inputStream != null) {

				jsonText = getString(inputStream, HTTP.UTF_8);

			}
		} catch (Exception e) {
			if (mHandler != null)
				mHandler.sendEmptyMessage(msg_download_failure);
		}
		// jsonText = tempJsonText;
		downloading = false;
		return jsonText;
	}

	private String getLocalData(String path) {
		path = path.replace("http://www.moboplayer.com/update/",
				"/sdcard/recommend/");
		try {
			FileInputStream fis = new FileInputStream(path);
			return getString(fis, HTTP.UTF_8);//
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getUpdateData() {
		String jsonText = null;
		try {
			String urlParams = VersionUpdateLib.updateUrl + "?phoneModel="
					+ android.os.Build.MODEL + "&phoneVersion="
					+ android.os.Build.VERSION.RELEASE + "&versionNumber="
					+ String.valueOf(Version.versionNum) + "&lang="
					+ Locale.getDefault().getLanguage() + "&platform="
					+ Version.platform + "&currentTime="
					+ String.valueOf(System.currentTimeMillis());
			URL url = new URL(urlParams);
			HttpURLConnection connection = (HttpURLConnection) (url
					.openConnection());
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			InputStream inputStream = connection.getInputStream();
			if (inputStream != null) {
				jsonText = getString(inputStream, HTTP.UTF_8);
			}
		} catch (Exception e) {
			if (mHandler != null)
				mHandler.sendEmptyMessage(msg_download_failure);
		}
		return jsonText;
	}

	public static String SharepUserEmail = "userEmail";
	public static String SharepUserCode = "userCode";

	/**
	 * 检查版本更新
	 * 
	 * @return
	 */
	private String postUpdateData() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		// params.add(new BasicNameValuePair("emailAddr", Setting.getByKey(
		// SharepUserEmail, "").toString()));
		// params.add(new BasicNameValuePair("userNum", Setting.getByKey(
		// SharepUserCode, -1).toString()));
		params.add(new BasicNameValuePair("phoneModel", android.os.Build.MODEL));
		params.add(new BasicNameValuePair("phoneVersion",
				android.os.Build.VERSION.RELEASE));
		params.add(new BasicNameValuePair("versionNumber", String
				.valueOf(Version.versionNum)));
		params.add(new BasicNameValuePair("lang", Locale.getDefault()
				.getLanguage()));
		params.add(new BasicNameValuePair("deviceID", "12345"));
		params.add(new BasicNameValuePair("platform", Version.platform));
		params.add(new BasicNameValuePair("currentTime", String.valueOf(System
				.currentTimeMillis())));

		HttpPost httpPost = new HttpPost(VersionUpdateLib.updateUrl);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			httpPost.setParams(httpParams);
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream is = response.getEntity().getContent();
				return getString(is, "UTF-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 上传推荐的展示和点击等操作的次数
	 * 
	 * @param channelName
	 * @param itemName
	 */
	public void uploadStatistics(String channelName, String itemName) {
		String statisticsUrl = recommendStatisticsUrl + "?";

		if (channelName != null) {
			statisticsUrl += "channelName=" + channelName;
		}
		if (itemName != null) {
			statisticsUrl += "itemName=" + itemName;
		}

		try {
			URL url = new URL(statisticsUrl);
			url.openConnection();
		} catch (Exception e) {
		}
	}

	private String getString(InputStream is, String encoding) {
		if (is != null) {
			try {
				StringBuffer sb = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(is, encoding);
				char[] data = new char[1000];
				int len = 0;
				while ((len = isr.read(data)) > 0) {
					sb.append(String.copyValueOf(data, 0, len));
				}
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private JSONObject getJson(String content) {
		try {
			return new JSONObject(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private JSONArray getDataArray(String content) {
		try {
			return new JSONArray(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private JSONArray getJsonArray(JSONObject object, String key) {
		JSONArray result = null;
		if (object != null) {
			try {
				String content = object.optString(key);
				result = getDataArray(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private RecommendData getData(JSONObject object) {
		RecommendData data = null;
		if (object != null) {
			data = new RecommendData();
			data.appImgUrl = object.optString("appImgUrl", null);
			data.appName = object.optString("appName", "");
			data.appUrl_1 = object.optString("appUrl_1", "");
			data.appUrl_2 = object.optString("appUrl_2", "");
			data.appAuthor = object.optString("appAuthor", "");
			data.appPackageName = object.optString("appPackageName", "");
			data.appProperty = object.optString("appProperty", "");
			data.appPropertyImgUrl = object
					.optString("appPropertyImgUrl", null);
			data.appRate = (float) object.optDouble("appRate", 4.0);
			data.appSize = object.optString("appSize", "0");
			data.appType = object.optString("appType", "free");
			data.dataType = object.optInt("dataType", 0);
			data.intentAction = object.optString("intentAction", "");
			data.phoneNumber = object.optString("phoneNumber", "");
			data.smsBody = object.optString("smsBody", "");
			data.dataLevel = object.optInt("dataLevel", 0);
			data.nextLevelUrl = object.optString("nextLevelUrl", "");
			data.displayType = object.optInt("displayType", 0);
			String pubDate = object.optString("pubDate", "");
			try {
				Date date = mSimpleDateFormat.parse(pubDate);
				data.pubDate = date.getTime();
			} catch (Exception e) {
			}
			data.childrenCount = object.optInt("childrenCount", 0);
			// data.pubDate = object.optLong("pubDate", 0);
		}
		return data;
	}

	private void getUpdateData(JSONObject object) {
		if (object != null) {
			VersionUpdateLib.appChangeLog = object
					.optString("appChangeLog", "");
			// VersionUpdateLib.appIsAdOpen = object.optBoolean("appIsAdOpen",
			// false);
			VersionUpdateLib.appName = object.optString("appName", "");
			VersionUpdateLib.appPubDate = object.optString("appPubDate", "");
			VersionUpdateLib.appVersion = object.optInt("appVersion", 0);
			VersionUpdateLib.downloadUrl1 = object
					.optString("downloadUrl1", "");
			VersionUpdateLib.downloadUrl2 = object
					.optString("downloadUrl2", "");
			VersionUpdateLib.updateForce = object.optInt("updateForce", 0);
			VersionUpdateLib.appVersionName = object.optString(
					"appVersionName", "");
		}
	}

	private RecommendCategoryData getCategoryData(JSONObject object) {
		RecommendCategoryData recommendCategoryData = new RecommendCategoryData();
		if (object != null) {
			recommendCategoryData.categoryName = object.optString(
					"categoryName", "");
			recommendCategoryData.downloadUrl = object.optString("downloadUrl",
					"");
			recommendCategoryData.id = object.optInt("id", 0);
			// recommendCategoryData.pubDate = object.optInt("pubDate", 0);
			recommendCategoryData.sortOrder = object.optInt("sortOrder", 0);

			String pubDate = object.optString("pubDate", "");
			try {
				Date date = mSimpleDateFormat.parse(pubDate);
				recommendCategoryData.pubDate = date.getTime();
			} catch (Exception e) {

			}

		}
		return recommendCategoryData;
	}

	private Recommend91GameData getGameData(JSONObject object) {
		Recommend91GameData gameOf91Data = new Recommend91GameData();
		if (object != null) {
			gameOf91Data.ch_url = object.optString("ch_url", null);
			gameOf91Data.en_url = object.optString("en_url", null);
			gameOf91Data.lastUpdateTime = System.currentTimeMillis();
		}
		return gameOf91Data;
	}

	private RecommendAdData getAdData(JSONObject object) {
		RecommendAdData adData = new RecommendAdData();
		if (object != null) {
			adData.isChAdOpend = object.optBoolean("isChAdOpend", false);
			adData.isEnAdOpend = object.optBoolean("isEnAdOpend", true);
			adData.typeOfAd = object.optInt("typeOfAd", 1);
			AdViewCreateLib.type_of_ad = adData.typeOfAd;
			adData.check();
		}
		return adData;
	}

	/**
	 * 下载频道信息
	 * 
	 * @param url
	 */
	public void downloadChannelInfo(final String url) {
		if (categoryList.size() == 0) {
			String jsonText = getData(url);
			// String jsonText = getLocalData(url);
			JSONArray jsonArray = getJsonArray(getJson(jsonText),
					CATEGORY_LIST_JSON_KEY);

			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject object = jsonArray.getJSONObject(i);
						if (object != null) {
							RecommendCategoryData tempData = getCategoryData(object);
							if (tempData != null)
								categoryList.add(tempData);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 下载推荐的内容
	 * **/
	public ArrayList<RecommendData> downloadData(final String url) {
		if (dataList.size() == 0 && !downloading) {

			// String jsonText = getLocalData(url);
			String jsonText = getData(url);
			JSONArray jsonArray = getJsonArray(getJson(jsonText),
					APP_LIST_JSON_KEY);

			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject object = jsonArray.getJSONObject(i);
						if (object != null) {
							RecommendData tempData = getData(object);
							if (tempData != null)
								dataList.add(tempData);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return dataList;
	}

	public void download91GameUrl(final Context con) {
		new Thread() {
			@Override
			public void run() {
				String jsonText = getData(recommend91GameUrl);
				Recommend91GameData gameData = null;
				if (jsonText != null) {
					gameData = getGameData(getJson(jsonText));
					new DataSaveLib(con, "", "recommend_91_game.tmp")
							.saveData(gameData);
				}

				if (mHandler != null) {
					Message msg = new Message();
					msg.obj = gameData;
					msg.what = msg_download_91_game_url;
					mHandler.sendEmptyMessage(msg_download_91_game_url);
				}
			}
		}.start();
	}

	public void downloadAdSetting(final Context con) {

		new Thread() {
			@Override
			public void run() {
				String jsonText = getData(recommendAdUrl);
				RecommendAdData adData = null;
				if (jsonText != null) {
					adData = getAdData(getJson(jsonText));
					new DataSaveLib(con, "", "recommend_ad_setting.tmp")
							.saveData(adData);
				}

			}
		}.start();
	}

	/**
	 * 检查版本更新
	 */
	public void checkUpdate(final Context con) {
		try {
			PackageManager manager = con.getPackageManager();
			PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
			final int versionName = info.versionCode;
			new Thread() {
				@Override
				public void run() {
					final String jsonText = getUpdateData();
					if (jsonText != null) {
						getUpdateData(getJson(jsonText));

						// Setting.saveSetting(con, "appIsAdOpen",
						// VersionUpdateLib.appIsAdOpen);
						VersionUpdateLib.updateDate.put("version_name",
								VersionUpdateLib.appVersionName);
						VersionUpdateLib.updateDate.put("pub_date",
								VersionUpdateLib.appPubDate);
						VersionUpdateLib.updateDate.put("change_log",
								VersionUpdateLib.appChangeLog);
						// NewVersionView.setData(VersionUpdateLib.updateDate);
						// if (VersionUpdateLib.appVersion > versionName)
						// if (mHandler != null) {
						// Message msg = new Message();
						// msg.what = msg_has_new_version;
						// mHandler.sendEmptyMessage(msg_has_new_version);
						// }
					}
					if (mHandler != null) {
						Message msg = new Message();
						msg.what = msg_has_finished_update;
						mHandler.sendEmptyMessage(msg_has_finished_update);
					}

				}
			}.start();

		} catch (Exception e) {
		}
	}

	public static void downloadImage(final String imgUrl,
			final Handler handler, final boolean delete) {
		new Thread() {
			public void run() {
				String filePath = RecommendData.getImagePathByUrl(imgUrl);
				File file = new File(filePath);
				Bitmap appBitmap = Global.getBitmap(filePath);
				if (file.exists() && appBitmap != null) {
					if (delete)
						file.delete();
					else
						return;
				}
				try {
					URL url = new URL(imgUrl);
					HttpURLConnection connection = (HttpURLConnection) (url
							.openConnection());
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					InputStream inputStream = connection.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] data = new byte[10 * 1000];
					int len = 0;
					while ((len = inputStream.read(data)) > 0) {
						fos.write(data, 0, len);
					}
					fos.flush();
					if (handler != null) {
						handler.sendEmptyMessage(msg_img_download_success);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 获取data数据结构的列表
	 * 
	 * @return
	 */
	public ArrayList<RecommendData> getDataList() {
		return dataList;
	}

	public ArrayList<RecommendCategoryData> getCategoryList() {
		return categoryList;
	}

	String dataSaveFilePath = null;
	String dataSavePath = null;
	File dataSaveFile = null;

	public void readData(Context con) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(dataSaveFile);
			ois = new ObjectInputStream(fis);
			dataList = (ArrayList<RecommendData>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeData(Context con, ArrayList<RecommendData> data) {
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dataSaveFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteTmpFile() {
		new Thread() {
			public void run() {
				File file = new File(dataSavePath);
				if (file.exists() && file.isDirectory()) {
					File[] fileList = file.listFiles();
					if (fileList != null) {
						for (int i = 0; i < fileList.length; i++) {
							File tempFile = fileList[i];
							tempFile.delete();
						}
					}
				}
			}
		}.start();
	}
}

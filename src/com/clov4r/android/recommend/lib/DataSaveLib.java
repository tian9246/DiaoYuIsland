package com.clov4r.android.recommend.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.clov4r.android.nil.Global;

import android.content.Context;

public class DataSaveLib {
	public static final String flag_recommend_data = "flag_recommend_data";
	String saveDirPath = "";
	String saveFilePath = "";
	File saveDir = null;
	Context mContext = null;

	public DataSaveLib(Context context, String flag, String fileName) {
		saveDirPath = Global.rootPath + File.separator + Global.recommandPath
				+ File.separator + flag_recommend_data + File.separator;
		saveFilePath = saveDirPath + fileName;
		saveDir = new File(saveDirPath);
		if (!saveDir.exists())
			saveDir.mkdirs();

		File file = new File(saveFilePath);
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将数据序列化到本地
	 * 
	 * @param data
	 */
	public void saveData(Object data) {
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveFilePath);
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

	/**
	 ** 读取本地序列化的数据
	 * 
	 * @return
	 */
	public Object getData() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(saveFilePath);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}

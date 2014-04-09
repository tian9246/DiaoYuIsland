package org.mummy.utils;

import java.util.HashMap;

import android.content.Context;

public class AndPrefs {

	private static HashMap<Context, HashMap<String, Integer>> pValues = new HashMap<Context, HashMap<String, Integer>>(
			10);

	public synchronized static boolean resetAccessCount(Context context, String name) {
		HashMap<String, Integer> mValues = pValues.get(context);
		if (mValues != null) {
			Object o = mValues.remove(name);
			if (o != null) {
				return true;
			}
		}
		return false;
	}

	public synchronized static void setValue(Context context, String name, int v) {
		HashMap<String, Integer> mValues = pValues.get(context);
		if (mValues != null) {
			mValues.put(name, v);
		} else {
			mValues = new HashMap<String, Integer>(10);
			mValues.put(name, v);
			pValues.put(context, mValues);
		}
	}

	public synchronized static int getValue(Context context, String name, int v) {
		HashMap<String, Integer> mValues = pValues.get(context);
		if (mValues != null) {
			return (Integer) mValues.get(name);
		}
		return 0;
	}
}

package com.weibuildus.smarttv.util;

import android.util.Log;

/**
 * LOG 总控
 * 
 * @ClassName: Logger
 * @author wumaojie.gmail.com
 * @date 2015-5-7 上午10:42:23
 */
public class Logger {

	/** LOG开关 */
	public static final boolean DEBUG = true;

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	/**
	 * 对象标签
	 */

	public static void i(Object tag, String msg) {
		if (DEBUG) {
			Log.i(tag == null ? "" : tag.getClass().getSimpleName(), msg);
		}
	}

	public static void d(Object tag, String msg) {
		if (DEBUG) {
			Log.d(tag == null ? "" : tag.getClass().getSimpleName(), msg);
		}
	}

	public static void w(Object tag, String msg) {
		if (DEBUG) {
			Log.w(tag == null ? "" : tag.getClass().getSimpleName(), msg);
		}
	}

	public static void e(Object tag, String msg) {
		if (DEBUG) {
			Log.e(tag == null ? "" : tag.getClass().getSimpleName(), msg);
		}
	}

	public static void v(Object tag, String msg) {
		if (DEBUG) {
			Log.v(tag == null ? "" : tag.getClass().getSimpleName(), msg);
		}
	}

	/**
	 * java系统输出
	 * 
	 * @Title out
	 * @param msg
	 */
	public static void out(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
}

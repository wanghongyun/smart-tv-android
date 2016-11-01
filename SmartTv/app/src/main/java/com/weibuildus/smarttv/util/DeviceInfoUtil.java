/** 
 * @company  Chengdu ZhiXiao planning consulting co., LTD.
 * @copyright  2015, Chengdu ZhiXiao planning consulting co., LTD.
 */
package com.weibuildus.smarttv.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 设备信息获取工具
 * 
 * @ClassName: DeviceInfoUtil
 * @author wumaojie.gmail.com
 * @date 2015-5-26 下午12:05:41
 */
public class DeviceInfoUtil {

	/**
	 * 获取手机分辨率
	 * 
	 * @Title getPhoneResolution
	 * @param context
	 * @return 返回int数组， 0为宽 ， 1为高
	 */
	public static int[] getPhoneResolution(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	/**
	 * 获取上网方式
	 * 
	 * @Title getNetworkType
	 * @param context
	 * @return Return a human-readable name describe the type of the network,
	 *         for example "WIFI" or "MOBILE". no net work return null.
	 */
	public static String getNetworkType(Context context) {
		NetworkInfo info = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		String typeName = info == null ? null : info.getTypeName();
		return typeName;
	}

	/**
	 * 获取App版本名称
	 * @Title  getVersionName 
	 * @param context
	 * @return 返回版本名称，如果失败返回空字符串
	 */
	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packInfo != null) {
			return packInfo.versionName;
		} else {
			return "";
		}
	}

	/**
	 * 获取App版本号
	 * 
	 * @Title getVersionCode
	 * @param context
	 * @return 返回版本号，如果失败返回0
	 */
	public static int getVersionCode(Context context) {
		if(context==null){
			return 0;
		}
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packInfo != null) {
			return packInfo.versionCode;
		} else {
			return 0;
		}
	}

	/**
	 * 获取手机号,大部分手机无法获取
	 * 
	 * @Title getPhoneNumber
	 * @param activity
	 * @return
	 */
	public static String getPhoneNumber(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneId = tm == null ? "" : tm.getLine1Number();
		return phoneId;
	}

	/**
	 * 是否可以拨打电话
	 * 
	 * @Title isCanCallPhone
	 * @param activity
	 * @return
	 */
	public static boolean isCanCallPhone(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
	}

	/**
	 * 是否是模拟器
	 * 
	 * @Title isEmulator
	 * @param activity
	 * @return
	 */
	public static int isEmulator(Activity activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei != null && imei.equals("000000000000000")) {
				return 1;
			}
			return ((Build.MODEL.equals("sdk")) || (Build.MODEL
					.equals("google_sdk"))) ? 1 : 0;
		} catch (Exception ioe) {

		}
		return 0;
	}

	/**
	 * 获取CPU型号和频率
	 * 
	 * @Title getCpuInfo
	 * @return
	 */
	public static String getCpuInfo() {
		String path = "/proc/cpuinfo";
		String temp = "";
		String cpuModel = "";
		try {
			FileReader fr = new FileReader(path);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			temp = localBufferedReader.readLine();
			String[] arrayOfString = temp.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuModel = cpuModel + arrayOfString[i] + " ";
			}
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuModel;
	}

	/**
	 * 网络是否可以
	 * 
	 * @Title isAvailableOfNetwork
	 * @param context
	 * @return
	 */
	public static boolean isAvailableOfNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null;
	}

	/**
	 * 获取Imei
	 * 
	 * @Title getImei
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String imei = UUID.randomUUID().toString().replaceAll("-", "");
		return imei;
	}

	/**
	 * 获取MAC地址
	 * 
	 * @Title getMac
	 * @return
	 */
	public static String getMacAddress() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 0; i < 5; i++) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	/**
	 * 获取IP地址
	 * 
	 * @Title getLocalIpAddress
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<?> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration<?> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();

					if (!inetAddress.isLoopbackAddress()) {
						String ipAddress = inetAddress.getHostAddress()
								.toString();
						// 过滤IPV6
						if (!ipAddress.contains("::")
								&& !TextUtils.equals(ipAddress, "10.0.2.15")) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 系统语言是否是中文
	 * @Title  isZh 
	 * @param context
	 * @return
	 */
	public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
		return language.endsWith("zh");
    }

	/**
	 * 获取进程名称
	 * @return null may be returned if the specified process not found
	 */
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}

	/**
	 * 外部存储是否可用
	 *
	 * @Title isCanUseExternalStorage
	 * @return
	 */
	public static boolean isCanUseExternalStorage() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

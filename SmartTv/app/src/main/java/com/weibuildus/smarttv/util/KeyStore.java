package com.weibuildus.smarttv.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences 键值存储仓库
 */
public class KeyStore {

	/** 针对不同的文件名的单列键值存储对象*/
	private static Map<String, KeyStore> mapKeyStores;
	
	/**
	 * 获取默认文件名的单列键值存储对象
	 * @Title  getInstance 
	 * @param context
	 * @return
	 */
	public static KeyStore getInstance(Context context) {
		return getInstance(context, null);
	}

	/**
	 * 获取制定文件名的单列键值存储对象
	 * @Title  getInstance 
	 * @param context
	 * @param file_name
	 * @return
	 */
	public static KeyStore getInstance(Context context, String file_name) {
		if(mapKeyStores == null){
			mapKeyStores = new HashMap<String, KeyStore>();
		}
		file_name = file_name == null ? KeyStore.class.getSimpleName() : file_name;
		if(mapKeyStores.get(file_name)==null){
			mapKeyStores.put(file_name, new KeyStore(context, file_name));
		}
		return mapKeyStores.get(file_name);
	}

	/**存储对象*/
	private SharedPreferences preferences;

	/**
	 * 创建存储对象
	 * @param context
	 * @param file_name
	 */
	private KeyStore(Context context, String file_name) {
		preferences = context.getSharedPreferences(file_name,
				Activity.MODE_PRIVATE);
	}

	/**
	 * 获取编辑对象
	 * 
	 * @Title getEditor
	 * @return
	 */
	public Editor getEditor() {
		return preferences.edit();
	}

	/**
	 * 获取存储对象
	 * 
	 * @Title getSharedPreferences
	 * @return
	 */
	public SharedPreferences getSharedPreferences() {
		return preferences;
	}

	/**
	 * 保存键值：支持值类型 String,boolean,float,int,long,Set<String>
	 * 
	 * @Title put
	 * @param key
	 * @param value
	 *            为null默认 String
	 */
	@SuppressWarnings("unchecked")
	public KeyStore put(String key, Object value) {
		Editor editor = getEditor();
		if (value == null || value instanceof String) {
			editor.putString(key, (String) value);
		}
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		}
		if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		}
		if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		}
		if (value instanceof Set<?>) {
			editor.putStringSet(key, (Set<String>) value);
		}
		editor.commit();
		return this;
	}

	/**
	 * 获取键值：支持值类型 String,boolean,float,int,long,Set<String>
	 * 
	 * @Title get
	 * @param key
	 * @param defValue
	 *            为null默认 String
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defValue) {
		Object object = null;
		if (defValue == null || defValue instanceof String) {
			object = preferences.getString(key, (String) defValue);
		}
		if (defValue instanceof Boolean) {
			object = preferences.getBoolean(key, (Boolean) defValue);
		}
		if (defValue instanceof Float) {
			object = preferences.getFloat(key, (Float) defValue);
		}
		if (defValue instanceof Integer) {
			object = preferences.getInt(key, (Integer) defValue);
		}
		if (defValue instanceof Long) {
			object = preferences.getLong(key, (Long) defValue);
		}
		if (defValue instanceof Set<?>) {
			object = preferences.getStringSet(key, (Set<String>) defValue);
		}
		return (T) object;
	}


	/**
	 * 清空
	 */
	public void clear(){
		if(preferences!=null){
			preferences.edit().clear().commit();
		}
	}
}

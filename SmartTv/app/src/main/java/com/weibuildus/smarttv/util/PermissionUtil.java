package com.weibuildus.smarttv.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * 权限管理工具
 */
public class PermissionUtil {

    public static final int REQ_CODE_LOCATION = 0x01;


    /**
     * 权限检查是否有该权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean isGranted(Context context, @NonNull String permission) {
        if (context == null) return true;
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 请求权限 activity
     * @param activity
     * @param code 返回码
     * @param permission
     */
    public static void request(Activity activity, int code, @NonNull String permission) {
        if (isGranted(activity,permission)) return;
        ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
    }

    /**
     * 请求权限 fragment
     * @param fragment
     * @param code
     * @param permission
     */
    public static void request(Fragment fragment, int code, @NonNull String permission) {
        if (isGranted(fragment.getActivity(),permission)) return;
        fragment.requestPermissions(new String[]{permission}, code);
    }




}

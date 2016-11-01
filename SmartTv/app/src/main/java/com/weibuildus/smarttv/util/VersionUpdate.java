package com.weibuildus.smarttv.util;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.dialog.DownProgressDialog;
import com.weibuildus.smarttv.notification.DownNotification;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 版本升级工具类
 *
 * @author wumaojie.gmail.com
 * @ClassName: VersionUpdateUtil
 * @Description: TODO
 * @date 2014-6-17 下午3:03:05
 */
public class VersionUpdate {

    private static final String UPDATE_APK_URL_KEY = "UPDATE_APK_URL_KEY";

    private static String APK_PATH = "";
    private static String APK_FILE_DIRECTORY = "/updateApk/";
    private static String APK_FILE_PATH = "";

    /**
     * 清除缓存
     *
     * @Title clearCache
     */
    public static void clearCache(final Context context) {
        // 存储目录
        APK_PATH = context.getApplicationContext().getFilesDir().getAbsolutePath();
        // 存储文件目录
        APK_FILE_PATH = APK_PATH + APK_FILE_DIRECTORY;
        // 删除更新目录和文件
        deleteFile(new File(APK_FILE_PATH));
    }

    /**
     * @param context
     * @param url
     * @param isShowDowning 是否显示进度
     * @param listener      void
     * @throws
     * @Title: downLoadApk
     * @Description: 从服务器中下载APK
     */
    public static void downLoadApk(final Context context, int iconId, final String url,
                                   final String version, boolean isShowDowning,
                                   final OnClickListener listener) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (listener != null)
                    listener.onClick(null, msg.what);
                if (msg.what == 5 && msg.obj != null && msg.obj instanceof File) {
                    installApk(context, (File) msg.obj);
                }
            }
        };
        // 存储目录
        APK_PATH = context.getApplicationContext().getFilesDir().getAbsolutePath();
        // 存储文件地址
        APK_FILE_PATH = APK_PATH + APK_FILE_DIRECTORY
                + context.getPackageName() + "_" + version + ".apk";
        // 判断相同地址本地是否已存在，如果存在则安装更新
        if (false&&TextUtils.equals(APK_FILE_PATH, KeyStore.getInstance(context)
                .get(UPDATE_APK_URL_KEY, ""))) {
            final File file = new File(APK_FILE_PATH);
            //去掉缓存，每次都重新下--20161024改
            if (file.exists()) {
                file.delete();
            }
//            if (file.exists()) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        try {
//                            sleep(1500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        handler.sendMessage(handler.obtainMessage(5, file));
//                    }
//                }.start();
//                return;
//            }
        }
        // 进度条对话框
        DownProgressDialog dp = null;
        DownNotification dn = null;
        if (isShowDowning) {
            // 等待下载框
            dp = new DownProgressDialog(context);
        } else {
            Toast.makeText(
                    context,
                    context.getString(R.string.notification_version_check_down),
                    Toast.LENGTH_LONG).show();
        }
        // 通知栏下载进度
        dn = new DownNotification(context, iconId);

        final DownProgressDialog dpf = dp;
        final DownNotification dnf = dn;
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(url, dpf, dnf);
                    sleep(1000);
                    // 保存下载地址
                    KeyStore.getInstance(context).put(UPDATE_APK_URL_KEY,
                            APK_FILE_PATH);
                    installApk(context, file);
                    handler.sendEmptyMessage(5);
                    if (dpf != null) {
                        dpf.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(4);
                    if (dpf != null) {
                        dpf.cancel();
                    }
                    dnf.cancel();
                    // 如果下载失败，把下载失败的文件删除
                    try {
                        File file = new File(APK_FILE_PATH);
                        if (file.exists()) {
                            file.delete();
                        }
                    } catch (Exception exception) {
                    }
                }
                if (dpf != null)
                    dpf.dismiss(); // 结束掉进度条对话框
            }
        }.start();
    }

    /**
     * 下载apk
     *
     * @param path 下载地址
     * @param dpf
     * @param dnf
     * @return
     * @throws Exception
     */
    private static File getFileFromServer(String path, DownProgressDialog dpf,
                                          DownNotification dnf) throws Exception {
        // 如果sdcard可用的
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            if (dpf != null)
                dpf.setMax(conn.getContentLength());
            if (dnf != null)
                dnf.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File directory = new File(APK_PATH + APK_FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(APK_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                if (dpf != null) {
                    dpf.setProgress(total);
                }
                if (dnf != null) {
                    dnf.setProgress(total);
                }
            }
            if (dpf != null) {
                dpf.setComplete(file.getPath());
            }
            if (dnf != null) {
                dnf.setComplete(file.getPath());
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file    安装文件
     * @throws
     * @Title: installApk
     * @Description: 安装apk
     */
    private static void installApk(Context context, File file) {
        // 内部存储时修改权限
        File inFile = file;
        // 如果文件目录中有包名
        if (inFile.getPath().indexOf(context.getPackageName()) != -1) {
            // 从文件循环到包名目录
            while (!TextUtils.equals(inFile.getName(),
                    context.getPackageName())) {
                // 赋予可执行权限
                ProcessBuilder builder = new ProcessBuilder("chmod", "777", inFile.getPath());
                try {
                    Process process = builder.start();
                    // 等待进程结束
                    process.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inFile = inFile.getParentFile();
            }
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 删除文件和文件夹子文件
     *
     * @param file
     * @Title deleteFile
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }

}

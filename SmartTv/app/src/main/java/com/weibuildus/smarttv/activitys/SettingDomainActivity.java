package com.weibuildus.smarttv.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weibuildus.smarttv.BuildConfig;
import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.application.SmartTvApplication;
import com.weibuildus.smarttv.util.KeyStore;

import java.io.File;

/**
 * Created by Aikexing on 2016/8/18.
 */
public class SettingDomainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_domain);
        editText = $(R.id.editText);
        button = $(R.id.button);
        clear = $(R.id.clear);
        editText.setText(KeyStore.getInstance(this).get("DOMAIN_NAME", BuildConfig.BASE_PAGE_URL));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString().toString();
                if (ip.length() == 0) {
                    Toast.makeText(SettingDomainActivity.this, "请输入IP或者域名", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ip!=null){
                    SmartTvApplication.DOMAIN_NAME_URL =  ip.indexOf("http")==-1?"http://"+ip:ip;
                }else{
                    SmartTvApplication.DOMAIN_NAME_URL = ip;
                }
                KeyStore.getInstance(SettingDomainActivity.this).put("DOMAIN_NAME", ip);
                Intent intent = new Intent(SettingDomainActivity.this, WelcomeActivity.class);
                //intent.putExtra("page","dist/activity/hot-activity-search.js");
                startActivity(intent);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanApplicationData(SettingDomainActivity.this);
                KeyStore.getInstance(SettingDomainActivity.this).put("DOMAIN_NAME", null);
                SmartTvApplication.DOMAIN_NAME_URL = BuildConfig.BASE_PAGE_URL;
                editText.setText(KeyStore.getInstance(SettingDomainActivity.this).get("DOMAIN_NAME", BuildConfig.BASE_PAGE_URL));
                System.exit(0);
            }
        });

    }
    /** * 清除本应用所有的数据 * * @param context * @param filepath */
    public static void cleanApplicationData(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
    }

    /** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }
    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }



    /**
     * 查找View对象
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }
}

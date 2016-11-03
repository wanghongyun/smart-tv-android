package com.weibuildus.smarttv.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.application.SmartTvApplication;
import com.weibuildus.smarttv.dialog.ConfirmDialog;
import com.weibuildus.smarttv.dialog.VersionCheckDialog;
import com.weibuildus.smarttv.util.PermissionUtil;
import com.weibuildus.smarttv.util.StatusBarUtil;
import com.weibuildus.smarttv.util.ThreadPool;


/**
 * 欢迎页
 */
public class WelcomeActivity extends AppCompatActivity {
    //时间记录
    private long tempTime = 0;
    //当前申请权限
    private String permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化页面和标题
        setContentView(R.layout.activity_welcome);
        //记录等待时间起始点
        tempTime = System.currentTimeMillis();
        //开始进入流程，系统版本限制
        androidSystemRestrictions();
    }

    /**
     * 在开始生命周期中处理状态栏
     */
    @Override
    protected void onStart() {
        super.onStart();
        Integer systemBarDarkColor = getResources().getColor(R.color.white);
        StatusBarUtil.setStatusBarColor(this, systemBarDarkColor);
    }

    /**
     * 启动流程1  系统版本限制
     */
    public void androidSystemRestrictions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
             //提示版本过低
            // 版本不满足应用运行要求
            new ConfirmDialog(this,
                    getString(R.string.version_restrictions_text), false,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 退出程序
                            finish();
                        }
                    }).show();
        } else {
            //申请权限
            requestPermission();
        }
    }

    /**
     * 启动流程2 Android系统权限申请
     */
    public void requestPermission() {
        if (TextUtils.equals(permission, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //开始定位
            boolean locationSuccess = ((SmartTvApplication)getApplication()).initLocation();
            if(locationSuccess){
                //开始检测版本更新
                appVersionUpdateCheck();
            }else{
                //定位失败
                SmartTvApplication.locationErrorPromptDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //开始检测版本更新
                        appVersionUpdateCheck();
                    }
                });
            }
            return;
        }
        //精确位置权限
        if (TextUtils.equals(permission, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permission = Manifest.permission.ACCESS_FINE_LOCATION;
        }
        //定位权限
        if (TextUtils.equals(permission, Manifest.permission.READ_EXTERNAL_STORAGE))  {
            permission = Manifest.permission.ACCESS_COARSE_LOCATION;
        }
        //SDCARD读取权限
        if (permission == null) {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        //检测权限，请求权限
        if (!PermissionUtil.isGranted(this, permission)) {
            //请求权限
            PermissionUtil.request(this, PermissionUtil.REQ_CODE_LOCATION, permission);
        } else {
            requestPermission();
        }
    }

    /**
     * 启动流程2 请求权限返回
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        requestPermission();
    }


    /**
     * 启动流程3 版本更新检测
     */
    public void appVersionUpdateCheck() {
//        HttpApi.updateCheck(new HttpConnectListener<UpdateCheckInfo>() {
//            @Override
//            public void onRequestSucceed(HttpResponse<UpdateCheckInfo> data) {
//                super.onRequestSucceed(data);
//                if (data != null && data.getData() != null) {
//                    String weexversion = data.getData().weexversion;
//                    weexversion = weexversion == null ? "" : weexversion;
//                    isGuide = !TextUtils.equals(KeyStore.getInstance(WelcomeActivity.this).get("weex_version", ""), weexversion);
//                    if (isGuide) {
//                        KeyStore.getInstance(WelcomeActivity.this).put("weex_version", data.getData().weexversion);
//                    }
//                    if (DeviceInfoUtil.getVersionCode(WelcomeActivity.this) < data.getData().version) {
//                        //版本更新
//                        showAppVersionUpdateCheck(data.getData().isforce(), data.getData().versionname, data.getData().updateinfo,
//                                data.getData().url);
//                        return;
//                    }
//                }
//                //版本更新结束
//                appVersionUpdateEnd();
//            }
//
//            @Override
//            public void onRequestFailure(HttpResponse<UpdateCheckInfo> data) {
//                super.onRequestFailure(data);
//                //版本更新结束
//                appVersionUpdateEnd();
//            }
//        });
        //版本更新结束
        appVersionUpdateEnd();
    }

    /**
     * 启动流程3结束 版本检测结束
     */
    public void appVersionUpdateEnd() {
        //检测自动登录
        autoLoginCheck();
    }

    /**
     * 启动流程4 自动登录处理
     */
    public void autoLoginCheck() {
        //延迟等待
        waiting();
    }

    /**
     * 启动流程5 计算启动时间，
     */
    public void waiting() {
        //计算总时间，此页面至少延迟2秒  2000
        ThreadPool.executeDelay((tempTime = 0 - (System.currentTimeMillis() - tempTime)) < 0 ? 0 : tempTime, new ThreadPool.ThreadPoolMethodCallBack() {
            @Override
            public void callBack(String methodName, Object object) {
                complete();
            }
        });
    }

    /**
     * 启动流程结束
     */
    public void complete() {
        //进入主页
       enterMain();
    }

    /**
     * 启动欢迎结束进入主页
     */
    public void  enterMain(){
        Intent intent = new Intent(this, WeexActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
        finish();
    }

    //############下面工具方法############################################

    /**
     * 权限请求系统返回
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 有新版本弹出对话框
     * @param isForce
     * @param version
     * @param upateMessage
     * @param downUrl
     */
    public void showAppVersionUpdateCheck(final boolean isForce,String version, String upateMessage, String downUrl) {
        new VersionCheckDialog(this, downUrl, version, upateMessage, isForce, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 1:
                        //强制更新等待
                        break;
                    case 2:
                        //开始下载
                        if (!isForce) {
                            //非强制更新，不等待直接进入下一步
                            appVersionUpdateEnd();
                        }
                        break;
                    case 3:
                        //取消更新
                        if (isForce) {
                            //强制更新取消更新退出程序
                           finish();
                        } else{
                            //非强制更新取消更新，程序往下走
                            appVersionUpdateEnd();
                        }
                        break;
                    case 4:
                        //更新下载失败
                        showToast(getResources().getString(R.string.notification_version_check_downfail));
                        if (isForce) {
                            //强制更新下载失败退出程序
                            finish();
                        }
                        break;
                    case 5:
                        //下载完成
                        if (isForce) {
                            //强制更新下载成功退出程序
                            finish();
                        }
                        break;
                }
            }
        }).show();

    }
    //###############Toast处理##################################
    private Toast toast;
    /**
     * 弹出Toast提示
     * @param msg
     */
    public void showToast(String msg){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        toast.show();
    }
}

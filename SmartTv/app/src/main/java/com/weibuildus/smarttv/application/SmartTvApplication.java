package com.weibuildus.smarttv.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.ui.component.WXBasicComponentType;
import com.weibuildus.smarttv.BuildConfig;
import com.weibuildus.smarttv.dialog.ConfirmDialog;
import com.weibuildus.smarttv.util.KeyStore;
import com.weibuildus.smarttv.util.Logger;
import com.weibuildus.smarttv.weex.WeexImageAdapter;
import com.weibuildus.smarttv.weex.component.WXImage;
import com.weibuildus.smarttv.weex.component.WXListComponent;
import com.weibuildus.smarttv.weex.component.WXScroller;
import com.weibuildus.smarttv.weex.component.WXTextarea;
import com.weibuildus.smarttv.weex.component.WXVideo;
import com.weibuildus.smarttv.weex.component.WXVideoC;
import com.weibuildus.smarttv.weex.component.WXWeb;
import com.weibuildus.smarttv.weex.module.SmartTvModule;

/**
 * Created by Aikexing on 2016/7/5.
 * Application weex初始化
 */
public class SmartTvApplication extends Application {

    public static String DOMAIN_NAME_URL = "";

    public static SmartTvApplication application;

    private LocationManager locationManager;
    private String locationProvider;
    //定位位置
    private Location location;
    private boolean firstReadLocation = true;
    @Override
    public void onCreate() {
        super.onCreate();
        this.application = this;
        initDomainName();
        initWeex();
        //监听定位开启
        listenLocationAllowed();
    }

    /**
     * 初始化域名
     */
    private void initDomainName(){
        String keyStoreDomainName = KeyStore.getInstance(this).get("DOMAIN_NAME", null);
        if(keyStoreDomainName!=null){
            DOMAIN_NAME_URL =  keyStoreDomainName.indexOf("http")==-1?"http://"+keyStoreDomainName:keyStoreDomainName;
        }else{
            DOMAIN_NAME_URL = BuildConfig.BASE_PAGE_URL;
        }
    }

    /**
     * 初始化WEEX
     */
    private void initWeex(){
        //最初始化的时候设置高度偏移
        //WXViewUtils.setPageOffsetHeight(getStatusBarHeight() + getResources().getDimensionPixelOffset(R.dimen.title_height));
        InitConfig config = new InitConfig.Builder().setImgAdapter(new WeexImageAdapter()).build();
        try {
            WXSDKEngine.initialize(this, config);
            WXSDKEngine.registerComponent("bdwxwebview", WXWeb.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.IMG, WXImage.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.IMAGE, WXImage.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.LIST, WXListComponent.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.VLIST, WXListComponent.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.SCROLLER, WXScroller.class);
            WXSDKEngine.registerComponent(WXBasicComponentType.TEXTAREA, WXTextarea.class);
            //播放器
            WXSDKEngine.registerComponent(WXBasicComponentType.VIDEO, WXVideo.class);
            //自定义Module
            WXSDKEngine.registerModule("CustomModule", SmartTvModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听定位开启
     */
    private void listenLocationAllowed(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //重新初始化定位
                initLocation();
            }
        };
        getContentResolver()
                .registerContentObserver(Settings.Secure.getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, new ContentObserver(null) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                handler.sendEmptyMessage(1);
                            }
                        });
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 初始化定位程序
     */
    public boolean initLocation() {
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //监视地理位置变化
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(locationManager !=null){
                if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
                    //网络获取位置
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else if(locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
                    //GPS获取位置
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else{
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setCostAllowed(false);
                    //选择最好的方式获取位置
                    locationProvider = locationManager.getBestProvider(criteria, true);

                }
                if(locationProvider != null){
                    //立即通过选择的方式获取位置
                    location = locationManager.getLastKnownLocation(locationProvider);
                    //监听位置变化
                    try{
                        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                        return true;
                    }catch (Exception e){
                        e.printStackTrace();
                        //捕获异常
                    }
                }
            }
        }
        return false;
    }


    /**
     * 地理位置监听
     */
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            SmartTvApplication.this.location = location;
            if(location==null){
                Logger.i("BandouApplication_LocationListener", "location="+location);
            }else{
                Logger.i("BandouApplication_LocationListener", "location: Latitude=" + location.getLatitude() + ",Longitude=" + location.getLongitude() + ";");
            }

        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (locationManager != null) {
            //移除监听器
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
                return;
            }
        }
    }

    /**
     * ****************************************************************外部静态方法**************************************************************************************
     */


    /**
     * 获取经纬度
     * @return
     */
    public static Location getLocation(){
        if(application == null){
            return null;
        }
        return application.location;
    }

    /**
     * 获取经纬度，第一次失败提示用户
     * @return
     */
    public static Location getLocation(Activity activity){
        if(application == null){
            return null;
        }
        if(application.location == null && application.firstReadLocation){
            //第一次获取经纬度失败，提示用户赋予权限
            locationErrorPromptDialog(activity,null);
            application.firstReadLocation = false;
        }
        return application.location;
    }

    /**
     * 获取经纬度，失败都提示
     * @param activity
     * @return
     */
    public static Location getLocationErrorPrompt(Activity activity,DialogInterface.OnClickListener clickListener){
        if(application == null){
            return null;
        }
        if(application.location == null){
            application.initLocation();
        }
        if(application.location == null){
            locationErrorPromptDialog(activity, clickListener);
        }
        return application.location;
    }

    /**
     * 定位失败提示
     * @param activity
     */
    public static void locationErrorPromptDialog(Activity activity,DialogInterface.OnClickListener clickListener){
        new ConfirmDialog(activity,"获取您的位置失败，请为我们赋予定位权限，并保持网络通畅。",false,null,null,clickListener).show();
    }
}

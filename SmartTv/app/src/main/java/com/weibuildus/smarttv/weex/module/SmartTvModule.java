package com.weibuildus.smarttv.weex.module;


import com.taobao.weex.WXSDKManager;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.common.WXModuleAnno;
import com.weibuildus.smarttv.activitys.WeexActivity;
import com.weibuildus.smarttv.util.KeyStore;
import com.weibuildus.smarttv.util.StringUtil;

import java.util.Map;

/**
 * Created by Aikexing on 2016/8/4.
 */
public class SmartTvModule extends WXModule {


    /**
     * 选图片
     * @param map
     * @param callbackId
     */
    @WXModuleAnno
    public void pickImage(Map<String,Object> map,  String callbackId){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.pickImage(map, callbackId);
        }
    }

    /**
     * 获取经纬度
     * @param map
     * @param callbackId
     */
    @WXModuleAnno
    public void getLocation(Map<String,Object> map,  String callbackId){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.getLocation(map, callbackId);
        }
    }

    /**
     * 打电话
     * @param map
     */
    @WXModuleAnno
    public void phoneCall(Map<String,Object> map){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.phoneCall(map);
        }
    }

    /**
     * 本地存储键值对
     * @param key
     * @param value
     */
    @WXModuleAnno
    public void setKeyValue(String key, String value) {
        KeyStore.getInstance(mWXSDKInstance.getContext()).put("BandouModule_" + key, value);
    }

    /**
     * 获取本地存储的键值对
     * @param key
     * @param defValue
     * @param callbackId
     */
    @WXModuleAnno
    public void getKeyValue(String key, String defValue,  String callbackId) {
        String value =  KeyStore.getInstance(mWXSDKInstance.getContext()).get("BandouModule_" + key, defValue);
        WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), callbackId, value);
    }

    /**
     * 支付
     * @param map
     * @param callbackId
     */
    @WXModuleAnno
    public void pay(Map<String,Object> map,  String callbackId){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.pay(map, callbackId);
        }
    }

    /**
     * 返回页面
     */
    @WXModuleAnno
    public void toBack(Map<String,Object> map) {
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.toBack(map);
        }
    }

    /**
     * 选择日期
     * @param map
     * @param callbackId
     */
    @WXModuleAnno
    public void pickDate(Map<String,Object> map,  String callbackId){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.pickDate(map, callbackId);
        }
    }

    /**
     * 打开页面
     * @param url
     */
    @WXModuleAnno
    public void openURL(String url ,Map<String,Object> map) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.openURL(url, map);
        }
    }

    /**
     * 显示等待菊花
     * @param message
     */
    @WXModuleAnno
    public void startProgress(String message){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.startProgress(message);
        }
    }

    /**
     * 关闭等待菊花
     */
    @WXModuleAnno
    public void stopProgress(){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.stopProgress();
        }
    }
//
//    /**
//     * tab 页面切换
//     * @param index
//     */
//    @WXModuleAnno
//    public void setTabIndex(int index){
//        if(mWXSDKInstance.getContext() instanceof WeexActivity){
//            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
//            weexActivity.setTabIndex(index);
//        }
//    }
//
//    /**
//     * 设置当前页面需要回调，IOS处理，Android暂不用
//     * @param flag
//     */
//    @WXModuleAnno
//    public void setToBackFlag(String flag){
//
//    }

    /**
     * @param log
     */
    @WXModuleAnno
    public void log(String log){
        System.out.println(log);
    }

    /**
     * 设置是否全屏
     */
    @WXModuleAnno
    public void setFullScreen (boolean isFullScreen){
        if(mWXSDKInstance.getContext() instanceof WeexActivity){
            WeexActivity weexActivity = (WeexActivity)mWXSDKInstance.getContext();
            weexActivity.setFullScreen(isFullScreen);
        }
    }



}

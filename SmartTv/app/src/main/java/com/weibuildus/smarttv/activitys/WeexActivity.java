package com.weibuildus.smarttv.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.common.WXException;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXViewUtils;
import com.weibuildus.smarttv.BuildConfig;
import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.application.SmartTvApplication;
import com.weibuildus.smarttv.dialog.ConfirmDialog;
import com.weibuildus.smarttv.dialog.WaitingDialog;
import com.weibuildus.smarttv.util.DeviceInfoUtil;
import com.weibuildus.smarttv.util.ImageLocationUtil;
import com.weibuildus.smarttv.util.PermissionUtil;
import com.weibuildus.smarttv.util.StatusBarUtil;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Aikexing on 2016/8/4.
 * WEEX加载页面
 */
public class WeexActivity extends AppCompatActivity implements IWXRenderListener, View.OnClickListener {

    //默认首页
    public static final String WEEX_MAIN_PAGE_JS = "dist/first.js";
    //页面传递参数
    public static final String PAGE_PARAMETER = "page";

    //页面返回
    private static final int REQUEST_BACK_PAGE = 110;
    //网页返回
    private static final int REQUEST_BACK_WEB = 111;
    //进入图片选择返回码
    private static final int REQUEST_IMAGE = 1;
    //进入图片剪切返回码
    private static final int REQUEST_CROP = 2;

    public WXSDKInstance mWXSDKInstance;

    private LinearLayout weexContentLayout;
    private LinearLayout weexWaittingLayout;
    private LinearLayout weexErrorLayout;
    private LinearLayout weexTitleBlank;
    private GifImageView weexWaittingLoading;
    private TextView weexErrorMsg;
    private GifImageView weexErrorGif;
    private View weexRefreshBtn;
    private ImageView weexTitleBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        }else{
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //初始化页面和标题
        setContentView(R.layout.activity_weex);
        //初始化View
        initView();
        //刷新页面
        refreshPage(null);
    }

    /**
     * 初始化View
     */
    protected void initView(){
        weexContentLayout = $(R.id.weex_content_layout);
        weexWaittingLayout = $(R.id.weex_waitting_layout);
        weexErrorLayout = $(R.id.weex_error_layout);
        weexTitleBlank  = $(R.id.weex_title_blank);
        weexWaittingLoading = $(R.id.weex_waitting_loading);
        weexErrorMsg = $(R.id.weex_error_msg);
        weexErrorGif = $(R.id.weex_error_gif);
        weexRefreshBtn = $(R.id.weex_refresh_btn);
        weexTitleBg = $(R.id.weex_title_bg);
        //初始化等待标题背景高度
        weexTitleBg.getLayoutParams().height = (int)((getResources().getDisplayMetrics().widthPixels/750.0f)*88);
        weexRefreshBtn.setOnClickListener(this);
        //gif动画加载
        try {
            weexWaittingLoading.setImageDrawable(new GifDrawable(getResources(), R.mipmap.bandou_runing_loading));
            weexErrorGif.setImageDrawable(new GifDrawable(getResources(), R.mipmap.bandou_cry_error));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新页面
     */
    public void refreshPage(String data){
        //置空页面
        weexContentLayout.removeAllViews();
        //附加数据
        Map map = data==null?new HashMap():new Gson().fromJson(data, Map.class);
        map.put("AndroidStatusBarHeight",getStatusBarHeight());
        map.put("isFullScreen",getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        data = new Gson().toJson(map);
        //重新注册 weex SDK
        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);
        //加载网络js
        int height = getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE?(getResources().getDisplayMetrics().heightPixels)
                :(getResources().getDisplayMetrics().heightPixels-getStatusBarHeight());
        mWXSDKInstance.renderByUrl("Bandou_Weex", getPageParameter(), null, data==null?getIntent().getStringExtra("data"):data,-1, height,
                WXRenderStrategy.APPEND_ASYNC);
        //关闭错误页和内容页，展示等待页和空白标题
        weexErrorLayout.setVisibility(View.GONE);
        weexContentLayout.setVisibility(View.GONE);
        weexTitleBlank.setVisibility(View.VISIBLE);
        weexWaittingLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取页面参数
     * @return
     */
    public String getPageParameter() {
        String pageUrl = getIntent().getStringExtra(PAGE_PARAMETER);
        return SmartTvApplication.DOMAIN_NAME_URL + "/" + (pageUrl == null ?WEEX_MAIN_PAGE_JS:pageUrl);
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

    //#####################系统状态栏和点击事件处理##########################################

    @Override
    protected void onStart() {
        super.onStart();
        Integer systemBarDarkColor = getResources().getColor(R.color.bar_bg_dark_color);
        StatusBarUtil.setStatusBarColor(this, systemBarDarkColor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weex_refresh_btn:
                refreshPage(null);
                break;
        }
    }
    //######################weex监听############################################

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        weexContentLayout.addView(view, new LinearLayout.LayoutParams(-1, -1));
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        weexWaittingLayout.setVisibility(View.GONE);
        weexTitleBlank.setVisibility(View.GONE);
        weexContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if(DeviceInfoUtil.isAvailableOfNetwork(this)){
            weexErrorMsg.setText(BuildConfig.DEBUG?"页面太惊艳，网络都塞车了！\n:errCode:"+errCode+";msg:"+msg:"页面太惊艳，网络都塞车了！");
        }else{
            weexErrorMsg.setText("页面太惊艳，网络都塞车了！");
        }
        weexContentLayout.setVisibility(View.GONE);
        weexWaittingLayout.setVisibility(View.GONE);
        weexTitleBlank.setVisibility(View.VISIBLE);
        weexErrorLayout.setVisibility(View.VISIBLE);
        //关闭等待
        stopProgress();
    }
    //####################weex生命周期关联##############################################
    @Override
    protected void onResume() {
        super.onResume();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityDestroy();
        }
    }
    //######################weex页面交互方法############################################

    //图片回调ID 变量
    private String weexPickImageCallbackId;
    private WaitingDialog waitingDialog;
    private int mainItemIndex = 0;

    /**
     * 选择图片
     * @param map
     * @param callbackId
     */
    public void pickImage(Map<String,Object> map,  String callbackId){
        this.weexPickImageCallbackId = callbackId;
        if(!PermissionUtil.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            showToast("权限不足，无法读取相册，请赋予存储访问权限！");
            upLoadImage(null);
            return;
        }
        this.toSelectImage();
    }

    /**
     * 获取定位经纬度
     * @param map
     * @param callbackId
     */
    public void getLocation(Map<String,Object> map,  String callbackId){
        Location location = SmartTvApplication.getLocation();
        if(location != null ){
            WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), callbackId, new Gson().fromJson( "{\"longitude\":" + location.getLongitude() + ",\"latitude\":"+location.getLatitude()+"}",Map.class));
        }else{
            WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), callbackId, new Gson().fromJson("{\"longitude\":0,\"latitude\":0}", Map.class));
        }
    }

    /**
     * 打电话
     * @param map
     */
    public void phoneCall(Map<String,Object> map){
        String phone = getMapValue(map, "phone", null);
        if(phone != null && phone.length()>0 ){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 支付
     * @param map
     * @param callbackId
     */
    public void pay(Map<String,Object> map,  String callbackId){
//        weexPayCallbackId = callbackId;
//        if(map!=null){
//            Object type = map.get("type");
//            //注册支付结果接受广播
//            if(receiverPayResult == null){
//                receiverPayResult = new ReceiverPayResult();
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(WXApiUtil.WX_PAY_BROADCAST_ACTION);
//                registerReceiver(receiverPayResult, intentFilter);
//            }
//            //微信支付
//            if(type!=null && type.toString().indexOf("wexin")!=-1){
//                Object param = map.get("param");
//                if(param!=null){
//                    OrderPay orderPay = new Gson().fromJson(new Gson().toJson(param), OrderPay.class);
//                    WXPayUtil.wxPayrequest(this, WXApiUtil.IWXAPI_APP_ID, orderPay.partnerid, orderPay.prepayid, orderPay._package, orderPay.noncestr, orderPay.timestamp, orderPay.sign);
//                    return;
//                }
//            }
//            //支付宝支付
//            if(type!=null && type.toString().indexOf("ali")!=-1){
//                Object param = map.get("param");
//                //System.out.println("支付宝支付数据：=="+param.toString());
//                if(param!=null){
//                    AlipayUtil.alipayPay(this, param.toString(), new AlipayUtil.AlipayUtilCallBack() {
//                        @Override
//                        public void payResultStatus(int payresultstatus) {
//                            if (payresultstatus == AlipayUtil.AlipayUtilCallBack.PAY_SUCCESS) {
//                                showToast("支付成功");
//                            }
//                            if (payresultstatus == AlipayUtil.AlipayUtilCallBack.PAY_FAIL) {
//                                showToast("支付失败");
//                            }
//                            if (payresultstatus == AlipayUtil.AlipayUtilCallBack.PAY_WAITING) {
//                                showToast("支付待确认");
//                            }
//                            if(weexPayCallbackId!=null){
//                                WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPayCallbackId, new Gson().fromJson( "{\"type\":\"ali\",\"success\":"+(payresultstatus == AlipayUtil.AlipayUtilCallBack.PAY_SUCCESS)+"}",Map.class));
//                                weexPayCallbackId = null;
//                            }
//                        }
//                    });
//                    return;
//                }
//            }
//        }
//        showToast("未知支付方式");
//        WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), callbackId, new Gson().fromJson("{\"type\":\"unknown\",\"success\":false}", Map.class));
    }

    /**
     * 返回页面
     * @param map
     */
    public void toBack(Map<String,Object> map){
        Object toBackFlag =  map.get("toBackFlag");
        if(toBackFlag==null || toBackFlag.toString().trim().length()==0){
            setResult(RESULT_CANCELED);
        }else{
            String data =  new Gson().toJson(map).toString();
            Intent intent = new Intent();
            intent.putExtra("data",data);
            //intent.putExtra("mainItemIndex", getIntent().getIntExtra("mainItemIndex", 0));
            setResult(RESULT_OK, intent);
        }
        finish();
        overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
    }

    /**
     * 选择时间
     * @param map
     * @param callbackId
     */
    public void pickDate(Map<String,Object> map,  final String callbackId){
        String dateStrLong =  getMapValue(map, "date", System.currentTimeMillis() + "");
        long dateLong = System.currentTimeMillis();
        try{
            dateLong = Long.parseLong(dateStrLong);
        }catch (Exception e){
            e.printStackTrace();
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateLong));
        DatePickerDialog dialog = new DatePickerDialog(this,
                 new DatePickerDialog.OnDateSetListener() {
                                      @Override
                                      public void onDateSet(DatePicker view,final int year,final int monthOfYear,final int dayOfMonth) {
                                          Date date = new Date(System.currentTimeMillis());
                                          date.setYear(year-1900);
                                          date.setMonth(monthOfYear);
                                          date.setDate(dayOfMonth);
                                          calendar.setTime(date);
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  String date = calendar.getTime().getTime() + "";
                                                  WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), callbackId, new Gson().fromJson( "{\"date\":" + date + "}",Map.class));
                                              }
                                          });
                                      }
                                      }, calendar.get(Calendar.YEAR), calendar
                                        .get(Calendar.MONTH), calendar
                                        .get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    /**
     * 打开下一级页面
     * @param url
     * @param map
     */
    public void openURL(String url ,Map<String,Object> map){
        String operate = getMapValue(map, "operate", null);
        Intent intent = new Intent(this, WeexActivity.class);
        intent.putExtra(PAGE_PARAMETER, url);
        intent.putExtra("titleType", getMapValue(map, "titleType", null));
        intent.putExtra("title",getMapValue(map, "title", ""));
        intent.putExtra("data", new Gson().toJson(map).toString());
        //记录主页tabindex
        intent.putExtra("mainItemIndex", getIntent().getStringExtra(PAGE_PARAMETER) == null ? mainItemIndex : getIntent().getIntExtra("mainItemIndex", 0));
        if("web".equals(operate)){
            //如果是网页切换目标页
            //intent.setClass(this, WebActivity.class);
            //startActivity(intent);
            //startActivityForResult(intent, REQUEST_BACK_WEB);
        }
        startActivityForResult(intent, REQUEST_BACK_PAGE);
        overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
    }

    /**
     * 弹出等待
     * @param message
     */
    public void startProgress(String message){
        if(waitingDialog==null){
            waitingDialog = new WaitingDialog(mWXSDKInstance.getContext());
        }
        waitingDialog.show(message);
    }

    /**
     * 关闭等待
     */
    public void stopProgress(){
        if(waitingDialog!=null){
            waitingDialog.dismiss();
        }
    }

    public void setFullScreen (boolean isFullScreen){
        if(isFullScreen){
            if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }else{
            if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

    }

   //#########################其他工具方法###################################################################

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

    /**
     * 获取参数
     * @param map
     * @param key
     * @return
     */
    private String getMapValue(Map<String,Object> map, String key, String defValue){
        if(map!=null){
            Object obj = map.get(key);
            if(obj!=null){
                if(obj.toString().length()>0){
                    return obj.toString();
                }
            }
        }
        return defValue;
    }

    //###########以下是图片选择操作######################################################
    /**
     * 选择图片
     */
    public void toSelectImage() {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        // 进入选择页面
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * 打开系统裁剪工具，裁剪后的照片保存在指定位置
     *
     * @param imgPath
     */
    private void openCrop(String imgPath) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(imgPath)), "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // 不返回数据
        intent.putExtra("return-data", false);
        // 指定照片保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(getExternalCacheDir() + "/" + ImageLocationUtil.getLocationImageFileName(imgPath))));
        // aspectX aspectY 是宽高的比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        //intent.putExtra("outputX", HEAD_IMAGE_WH);
        //intent.putExtra("outputY", HEAD_IMAGE_WH);
        // 输出格式
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 可缩放
        intent.putExtra("scale", true);
        //启用人脸识别
        intent.putExtra("noFaceDetection", true);
        //进入裁剪页面
        startActivityForResult(intent, REQUEST_CROP);
    }

    private String selectImagePath;
    /**
     * 获取返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_BACK_WEB){
            //记录当前的位置
            Map map =  new HashMap();
            map.put("selected",mainItemIndex);
            refreshPage(new Gson().toJson(map));
        }
        if(requestCode == REQUEST_BACK_PAGE && resultCode == Activity.RESULT_OK){
            //返回接收参数
            String newData = data.getStringExtra("data");
            //转换接收参数
            Map newMap = new Gson().fromJson(newData, Map.class);
            //当前页面地址
            String page = getIntent().getStringExtra(PAGE_PARAMETER);
            //判断当前页面是否是接收页面
            if(page==null || page.equals(getMapValue(newMap, "toBackFlag", null))){
                //返回到当前页面
                //当前页面参数
                String oldData = getIntent().getStringExtra("data");
                //转换当前页面参数
                Map oldMap = null;
                if(oldData!=null){
                    oldMap = new Gson().fromJson(oldData, Map.class);
                }else{
                    oldMap = new HashMap();
                }
                //如果返回参数中param有值，则扔给当前页面
                if(newMap.get("param")!=null&&newMap.get("param") instanceof Map){
                    oldMap.putAll((Map)newMap.get("param"));
                }
                //如果当前页面是首页，则设置tab标签
                if(page == null){
                    oldMap.put("selected",mainItemIndex);
                }
                //带着返回的参数刷新页面
                refreshPage(new Gson().toJson(oldMap));
            }else{
                //不是返回当前页面则继续往上返回
                data.putExtra("mainItemIndex", getIntent().getIntExtra("mainItemIndex", 0));
                setResult(Activity.RESULT_OK,data);
                finish();
                overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
            }
        }
        //获取图片
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            List<String> imagePaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (imagePaths != null && imagePaths.size() > 0) {
                selectImagePath = imagePaths.get(0);
                openCrop(selectImagePath);
            } else {
                showToast(getResources().getString(R.string.load_img_error));
            }
        }
        //裁剪图片
        if (requestCode == REQUEST_CROP && resultCode == Activity.RESULT_OK) {

            //裁剪返回图片
            File file = new File(getExternalCacheDir() + "/" + ImageLocationUtil.getLocationImageFileName(selectImagePath));
            //压缩后存储目录
            File file_compressImage = new File(getExternalCacheDir() + "/" + "compressImage_" + ImageLocationUtil.getLocationImageFileName(selectImagePath));
            //压缩图片到150KB以内
            ImageLocationUtil.compressImage(file, file_compressImage, 150);
            upLoadImage(Uri.fromFile(file_compressImage));
        }

        if ((requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_CANCELED) || (requestCode == REQUEST_CROP && resultCode == Activity.RESULT_CANCELED)) {
            upLoadImage(null);
        }
    }

    /**
     * 上传图片
     * @param uri
     */
    public void upLoadImage( final Uri uri){
        if(uri != null && uri.getPath() !=null ){
//            HttpApi.uploadFile(new File(uri.getPath()), new HttpConnectListener<Object>() {
//                @Override
//                public void onRequestSucceed(HttpResponse<Object> data) {
//                    super.onRequestSucceed(data);
//                    if(data != null){
//                        Map map = new Gson().fromJson(data.getResponseJson(),Map.class);
//                        WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPickImageCallbackId, new Gson().fromJson("{\"result\":0,\"file_id\":\"" + map.get("file_id").toString() + "\",\"file_url\":\""+ map.get("file_url").toString()+"\"}", Map.class));
//                    }else{
//                        WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPickImageCallbackId, new Gson().fromJson("{\"result\":-1}", Map.class));
//                    }
//                }
//
//                @Override
//                public void onRequestFailure(HttpResponse<Object> data) {
//                    super.onRequestFailure(data);
//                    WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPickImageCallbackId, new Gson().fromJson("{\"result\":-1}", Map.class));
//                }
//            });
            WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPickImageCallbackId, new Gson().fromJson("{\"result\":-2}", Map.class));
        }else{
            WXSDKManager.getInstance().getWXBridgeManager().callback(mWXSDKInstance.getInstanceId(), weexPickImageCallbackId, new Gson().fromJson("{\"result\":-2}", Map.class));
        }
    }

    //返回键拦截
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(getIntent().getStringExtra(PAGE_PARAMETER)==null){
                exitApp();
                return true;
            }else{
                //退出当前页面
                finish();
                overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void exitApp() {
        new ConfirmDialog(this,getString(R.string.exit_app), true,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 退出程序
                        finish();
                    }
                }).show();
    }
}

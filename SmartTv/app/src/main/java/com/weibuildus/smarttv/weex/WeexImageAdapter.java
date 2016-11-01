package com.weibuildus.smarttv.weex;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;
import com.weibuildus.smarttv.application.SmartTvApplication;
import com.weibuildus.smarttv.util.StringUtil;
import com.weibuildus.smarttv.util.ThreadPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Aikexing on 2016/8/4.
 * 图片文件加载
 */
public class WeexImageAdapter implements IWXImgLoaderAdapter {
    @Override
    public void setImage(final String url,final ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
        //实现你自己的图片下载。
        WXSDKManager.getInstance().postOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(!StringUtil.isEmpty(url)){
                    String current_url = "";
                    if(!url.startsWith("http://")&& url.startsWith(".")){
                        current_url = SmartTvApplication.DOMAIN_NAME_URL +"/"+url.substring(1,url.length());
                    }else{
                        current_url = url;
                    }
                    if(!"gif".equals(parseSuffix(current_url))){
                        Picasso.with(WXEnvironment.getApplication()).load(current_url).memoryPolicy(MemoryPolicy.NO_CACHE).into(view);
                    }else{
                        //gif图片处理流程
                        final String gifurl = current_url;
                        //gif处理
                        if(view instanceof GifImageView){
                            final Handler handler = new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if(msg.obj!=null){
                                        try {
                                            ((GifImageView)view).setImageDrawable(new GifDrawable((byte[])msg.obj));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            ThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HttpURLConnection urlConn = (HttpURLConnection) new URL(gifurl).openConnection();
                                        //获取数据流
                                        InputStream is = urlConn.getInputStream();
                                        //结果
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        //结果保存
                                        byte[] buffer = new byte[1024];
                                        int len = 0;
                                        while ((len = is.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, len);
                                        }
                                        is.close();
                                        urlConn.disconnect();
                                        handler.sendMessage(handler.obtainMessage(1, outputStream.toByteArray()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }, 0);
    }

    final static Pattern pattern = Pattern.compile("\\S*[?]\\S*");
    /**
     * 获取链接的后缀名
     * @return
     */
    private static String parseSuffix(String url) {
        Matcher matcher = pattern.matcher(url);
        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];
        String[] urlparams = null;
        if(matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            urlparams = spEndUrl[0].split("\\.");
        }else{
            urlparams = endUrl.split("\\.");
        }
        if(urlparams!=null && urlparams.length>=2){
            return urlparams[urlparams.length-1];
        }else{
            return null;
        }
    }
}

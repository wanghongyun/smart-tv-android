package com.weibuildus.smarttv.weex.component;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.common.Component;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.weibuildus.smarttv.weex.view.WXWebView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aikexing on 2016/8/28.
 */
@Component(lazyload = false)
public class WXWeb extends WXComponent {

    private WXWebView webView;

    public WXWeb(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, boolean isLazy) {
        super(instance, dom, parent, isLazy);
    }

    @Override
    protected void initView() {
        mHost = webView = new WXWebView(mContext);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF -8");
//        webView.getSettings().setUseWideViewPort(true);//关键点
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Map<String, Object> params = new HashMap<>();
                params.put("url", url);
                WXSDKManager.getInstance().fireEvent(mInstanceId, getRef(), Constants.Event.PAGESTART, params);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Map<String, Object> params = new HashMap<>();
                params.put("url", url);
                params.put("canGoBack", webView.canGoBack());
                params.put("canGoForward", webView.canGoForward());
                WXSDKManager.getInstance().fireEvent(mInstanceId, getRef(), Constants.Event.PAGEFINISH, params);

                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics dm = webView.getResources().getDisplayMetrics();
                        Map<String, Object> params = new HashMap<>();
                        params.put("height", (webView.getContentHeight() * webView.getScale()) * (750.0f / dm.widthPixels));
                        WXSDKManager.getInstance().fireEvent(mInstanceId, getRef(), "contentheight", params);
                    }
                }, 300);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Map<String, Object> params = new HashMap<>();
                params.put("type", errorCode);
                params.put("errorMsg", description);
                WXSDKManager.getInstance().fireEvent(mInstanceId, getRef(), Constants.Event.ERROR, params);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                WXSDKManager.getInstance().fireEvent(mInstanceId, getRef(), Constants.Event.RECEIVEDTITLE, params);
            }
        });

        //禁止滚动
        webView.setVerticalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    @Override
    public WXWebView getView() {
        return (WXWebView) super.getView();
    }

    @WXComponentProp(name = "src")
    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url) || mHost == null) {
            return;
        }
        webView.loadUrl(url);
    }

    @WXComponentProp(name = "value")
    public void loadData(String data) {
        if (TextUtils.isEmpty(data) || mHost == null) {
            return;
        }
        //webView.loadData(data, "text/html; charset=UTF-8", null);
        webView.loadData("<html><head><style type='text/css'> *{margin: 0;padding: 0;-webkit-text-size-adjust: 100%;} img{ max-width: 100%;width: 100%;height: auto;}</style></head><body>"
                          +data+"</body></html>", "text/html; charset=UTF-8", null);
    }
}

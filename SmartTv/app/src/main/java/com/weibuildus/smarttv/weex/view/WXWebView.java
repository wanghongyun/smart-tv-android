package com.weibuildus.smarttv.weex.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.taobao.weex.ui.view.gesture.WXGesture;
import com.taobao.weex.ui.view.gesture.WXGestureObservable;

/**
 * Created by Aikexing on 2016/8/28.
 */
public class WXWebView extends WebView implements WXGestureObservable {

    private WXGesture wxGesture;

    public WXWebView(Context context) {
        super(context);
    }

    public WXWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WXWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WXWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WXWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }


    @Override
    public void registerGestureListener(@Nullable WXGesture wxGesture) {
        this.wxGesture = wxGesture;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (wxGesture != null) {
            result |= wxGesture.onTouch(this, event);
        }
        return result;
    }
}

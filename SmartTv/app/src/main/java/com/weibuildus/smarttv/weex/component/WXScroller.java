package com.weibuildus.smarttv.weex.component;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXScrollView;

/**
 * Created by Aikexing on 2016/10/9.
 */
public class WXScroller extends com.taobao.weex.ui.component.WXScroller {
    public WXScroller(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, String instanceId, boolean isLazy) {
        super(instance, dom, parent, instanceId, isLazy);
    }

    public WXScroller(WXSDKInstance instance, WXDomObject node, WXVContainer parent, boolean lazy) {
        super(instance, node, parent, lazy);
    }

    //添加滚动到指定偏移量
    @WXComponentProp(name = "scrollto")
    public void scrollTo(int offset) {
        int actualOffset = (int) (offset * (1080.0f / 750));
        scrollBy(0, ((WXScrollView) getInnerView()).getScrollY()*-1);
        scrollBy(0, actualOffset);
    }
}

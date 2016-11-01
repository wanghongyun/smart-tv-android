package com.weibuildus.smarttv.weex.component;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

/**
 * Created by Aikexing on 2016/10/9.
 */
public class WXListComponent extends com.taobao.weex.ui.component.list.WXListComponent {


    public WXListComponent(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, String instanceId, boolean isLazy) {
        super(instance, dom, parent, instanceId, isLazy);
    }

    public WXListComponent(WXSDKInstance instance, WXDomObject node, WXVContainer parent, boolean lazy) {
        super(instance, node, parent, lazy);
    }

    //添加滚动到指定偏移量
    @WXComponentProp(name = "scrollto")
    public void scrollTo(int offset){
        if(bounceRecyclerView == null){
            return;
        }
        int actualOffset = (int) (offset * (1080.0f / 750));
        bounceRecyclerView.getInnerView().scrollBy(0, bounceRecyclerView.getInnerView().computeVerticalScrollOffset() * -1);
        bounceRecyclerView.getInnerView().scrollBy(0, 0);
        //重复回退，第一次 computeVerticalScrollOffset 可能不准确
        bounceRecyclerView.getInnerView().scrollBy(0, bounceRecyclerView.getInnerView().computeVerticalScrollOffset()*-1);
        bounceRecyclerView.getInnerView().scrollBy(0,actualOffset);
    }
}

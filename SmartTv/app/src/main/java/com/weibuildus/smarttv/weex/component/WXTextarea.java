package com.weibuildus.smarttv.weex.component;

import android.view.Gravity;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.Textarea;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXEditText;

/**
 * Created by Aikexing on 2016/10/9.
 */
public class WXTextarea extends Textarea {
    public WXTextarea(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, boolean isLazy) {
        super(instance, dom, parent, isLazy);
    }

    @WXComponentProp(name = Constants.Name.ROWS)
    public void setRows(int rows){
        WXEditText text = getHostView();
        if(text == null||rows <=0 ){
            return;
        }
        text.setGravity(Gravity.LEFT|Gravity.TOP);
        text.setLines(rows);
    }
}

package com.weibuildus.smarttv.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.animation.RotateAnimation;

import com.weibuildus.smarttv.R;


/**
 * 页面等待弹框
 */
public class WaitingDialog {

    private Context context;
    private Dialog dialog;


    protected RotateAnimation animation;

    public WaitingDialog(Context context) {
        this.context = context;
    }


    /**
     * 显示等待框
     */
    public void show(){
        show(null);
    }

    /**
     * 显示等待框
     */
    public void show(String message) {
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light), null, message == null || message.trim().length()==0 ?
                        context.getResources().getString(R.string.string_waiting_prompt) : message,
                true);
        dialog.setCancelable(true);
    }

    /**
     * 关闭等待框
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}

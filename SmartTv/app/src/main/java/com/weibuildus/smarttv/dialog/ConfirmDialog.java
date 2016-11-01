package com.weibuildus.smarttv.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

import com.weibuildus.smarttv.R;


/**
 * 确认弹框
 *
 * @author wumaojie.gmail.com
 * @ClassName: ConfirmDialog
 * @date 2016-3-24 上午9:55:44
 */
public class ConfirmDialog {

    private Context context;
    private String content;
    private boolean isCancel;
    private String confirmStr;
    private String cancelStr;
    private AlertDialog dialog;
    private DialogInterface.OnClickListener clickListener;

    public ConfirmDialog(Context context, String content, boolean isCancel,
                         DialogInterface.OnClickListener clickListener) {
        this.context = context;
        this.content = content;
        this.isCancel = isCancel;
        this.clickListener = clickListener;
        initView();
    }

    public ConfirmDialog(Context context, String content, boolean isCancel, String confirmStr, String cancelStr,
                         DialogInterface.OnClickListener clickListener) {
        this.context = context;
        this.content = content;
        this.isCancel = isCancel;
        this.confirmStr = confirmStr;
        this.cancelStr = cancelStr;
        this.clickListener = clickListener;
        initView();
    }



    /**
     * 初始化弹框页面
     *
     * @Title initView
     */
    @SuppressWarnings("deprecation")
    private void initView() {
        TextView title = new TextView(context);
        title.setTextColor(context.getResources()
                .getColor(R.color.dialog_confirm_text));
        title.setTextSize(18);
        int padding = (int) (context.getResources().getDisplayMetrics().density * 16);
        title.setPadding(padding, padding, padding, padding);
        title.setText(content);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Light)).setView(title)
                .setPositiveButton(confirmStr == null ? context.getString(R.string.string_confirm) : confirmStr, clickListener);
        if (isCancel) {
            builder.setNegativeButton(cancelStr == null ? context.getString(R.string.string_cancel) : cancelStr, null);
        } else {
            builder.setCancelable(false);
        }
        dialog = builder.create();
    }

    /**
     * 显示列表选择框
     */
    public void show() {
        dialog.show();
    }


    /**
     * 是否正在显示
     * @return
     */
    public boolean isShowing(){
        return dialog.isShowing();
    }


    /**
     * 关闭
     */
    public void dismiss(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }
}

package com.weibuildus.smarttv.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;


import com.weibuildus.smarttv.R;

import java.io.File;

/**
 * 通知栏下载进度
 *
 * @author wumaojie.gmail.com
 * @ClassName: DownNotification
 * @date 2016-3-23 下午3:48:27
 */
public class DownNotification {

    private Context context;

    private NotificationManager manager;
    private Notification notification;

    private RemoteViews contentView;

    private int notificationId = 9907;

    private int max = 100;
    private int progress = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setProgressUiThread(msg.arg1);
                    break;
                case 2:
                    setCompleteUi((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings("deprecation")
    public DownNotification(Context context, int iconId) {
        this.context = context;
        manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        ;
        contentView = new RemoteViews(context.getPackageName(),
                R.layout.layout_down_notification);
        contentView.setProgressBar(R.id.down_progress, 100, 0, false);
        try {
            contentView.setImageViewBitmap(R.id.down_icon, ((BitmapDrawable) context.getPackageManager().getApplicationIcon(context.getPackageName())).getBitmap());
        } catch (PackageManager.NameNotFoundException e) {
            contentView.setImageViewResource(R.id.down_icon,iconId);
            e.printStackTrace();
        }
        contentView.setTextViewText(R.id.down_text_instructions, context
                .getString(R.string.notification_version_check_downloading));
        notification = new Notification(iconId, "",
                System.currentTimeMillis());
        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(notificationId, notification);
    }

    public void cancel() {
        manager.cancel(notificationId);
    }

    /**
     * 设置下载总长度
     *
     * @param max
     * @Title setMax
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * 计算下载进度
     *
     * @param progress
     * @Title setProgress
     */
    public void setProgress(int progress) {
        double p = progress / (double) max;
        int pInt = (int) (p * 100);
        if (this.progress < pInt) {
            this.progress = pInt;
            handler.sendMessage(handler.obtainMessage(1, this.progress, 100));
        }
    }

    /**
     * 设置完成下载文件路径
     *
     * @param path
     * @Title setComplete
     */
    public void setComplete(String path) {
        handler.sendMessage(handler.obtainMessage(2, path));
    }

    /**
     * 更新下载进度
     *
     * @param progress
     * @Title setProgressUiThread
     */
    public void setProgressUiThread(int progress) {
        notification.contentView.setProgressBar(R.id.down_progress, 100,
                progress, false);
        notification.contentView
                .setTextViewText(
                        R.id.down_text_instructions,
                        context.getString(R.string.notification_version_check_downloading));
        notification.contentView.setTextViewText(R.id.down_text_progress,
                progress + "%");
        manager.notify(notificationId, notification);
    }

    /**
     * 完成下载时更新显示和点击效果
     *
     * @param path
     * @Title setCompleteUi
     */
    public void setCompleteUi(String path) {
        notification.contentView.setProgressBar(R.id.down_progress, 100, 100,
                false);
        notification.contentView
                .setTextViewText(
                        R.id.down_text_instructions,
                        context.getString(R.string.notification_version_check_click_install));
        notification.contentView.setTextViewText(R.id.down_text_progress,
                "100%");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        notification.contentIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        manager.notify(notificationId, notification);
    }

}

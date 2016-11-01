package com.weibuildus.smarttv.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weibuildus.smarttv.R;


/**
 * 更新下载进度等待框
 * @ClassName: DownProgressDialog 
 * @author wumaojie.gmail.com  
 * @date 2015-7-2 下午3:51:07
 */
public class DownProgressDialog extends Dialog {

	private TextView textInstructions;
	private TextView textProgress;
	
	private ProgressBar downProgress;
	
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
			default:
				break;
			}
		}
	};
	
	public DownProgressDialog(Context context) {
		super(context, R.style.style_dialog_down_progress);
		View layout = LayoutInflater.from(context).inflate(R.layout.dialog_version_check_down_progressbar, null);
		initView(layout);
		setContentView(layout);
		setCancelable(false);
		//设置宽度
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.width = getContext().getResources().getDisplayMetrics().widthPixels * 5 /6; 
		getWindow().setAttributes(layoutParams);
		show();
	}
	
	private void initView(View view){
		
		textInstructions = (TextView)view.findViewById(R.id.down_text_instructions);
		textProgress = (TextView)view.findViewById(R.id.down_text_progress);
		downProgress = (ProgressBar)view.findViewById(R.id.down_progress);
		
		textInstructions.setText(getContext().getString(R.string.notification_version_check_downloading));
		textProgress.setText("0%");
		downProgress.setMax(100);
		downProgress.setProgress(0);
	}
	

	/**
	 * 设置下载总长度
	 * 
	 * @Title setMax
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * 计算下载进度
	 * 
	 * @Title setProgress
	 * @param progress
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
	 * @Title  setComplete 
	 * @param path
	 */
	public void setComplete(String path) {
		handler.sendMessage(handler.obtainMessage(2, path));
	}
	

	/**
	 * 更新下载进度
	 * 
	 * @Title setProgressUiThread
	 * @param progress
	 */
	public void setProgressUiThread(int progress) {
		textInstructions.setText(getContext().getString(R.string.notification_version_check_downloading));
		textProgress.setText(progress+"%");
		downProgress.setProgress(progress);
	}
	
	/**
	 * 完成下载时更新显示和点击效果
	 * 
	 * @Title setCompleteUi
	 * @param path
	 */
	public void setCompleteUi(String path) {
		textInstructions.setText(getContext().getString(R.string.notification_version_check_download));
		textProgress.setText("100%");
		downProgress.setProgress(100);
	}



}

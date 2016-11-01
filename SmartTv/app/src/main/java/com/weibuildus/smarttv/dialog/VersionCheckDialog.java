package com.weibuildus.smarttv.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.util.VersionUpdate;


/**
 * 版本升级检测
 * 
 * @author Administrator
 * 
 */
public class VersionCheckDialog implements View.OnClickListener {

	private Context context;
	protected int iconId;
	private String downloadUrl;
	private String version;
	private String explain;
	private boolean isEnforcement;
	private OnClickListener clickListener;
	//
	private AlertDialog dialog;

	/**
	 * 升级弹出框
	 * clickListener which:1 强制更新 2 非强制更新  3 取消更新  4 更新下载失败 5 更新下载成功
	 * @param context
	 * @param downloadUrl
	 * @param version
	 * @param isEnforcement
	 * @param clickListener
	 */
	public VersionCheckDialog(Context context, int iconId, String downloadUrl,
							  String version, boolean isEnforcement, OnClickListener clickListener) {
		this.context = context;
		this.iconId = iconId;
		this.downloadUrl = downloadUrl;
		this.version = version;
		this.explain = null;
		this.isEnforcement = isEnforcement;
		this.clickListener = clickListener;
		initView();
	}

	/**
	 * 升级弹出框
	 * clickListener which:1 强制更新 2 非强制更新  3 取消更新  4 更新下载失败 5 更新下载成功
	 * @param context
	 * @param downloadUrl
	 * @param version
	 * @param explain
	 * @param isEnforcement
	 * @param clickListener
	 */
	public VersionCheckDialog(Context context, String downloadUrl,
							  String version, String explain, boolean isEnforcement,
							  OnClickListener clickListener) {
		this.context = context;
		this.iconId = R.mipmap.ic_launcher;
		this.downloadUrl = downloadUrl;
		this.version = version;
		this.explain = explain;
		this.isEnforcement = isEnforcement;
		this.clickListener = clickListener;
		initView();
	}

	private void initView() {
		View mView = View.inflate(context,
				R.layout.dialog_version_check, null);
		if (this.isEnforcement) {
			((TextView) mView
					.findViewById(R.id.dialog_version_cancel))
					.setText(R.string.dialog_version_check_quit);
		} else {
			((TextView) mView
					.findViewById(R.id.dialog_version_cancel))
					.setText(R.string.dialog_version_check_cancel);
		}

		if (this.explain != null && this.explain.length() > 0) {
			((TextView) mView
					.findViewById(R.id.dialog_version_content))
					.setText(explain);
		}

		// 取消
//		ClickRipple.applyRipple(mView
//				.findViewById(R.id.dialog_version_cancel));
		// 确定
//		ClickRipple.applyRipple(mView
//				.findViewById(R.id.dialog_version_confirm));

		mView.findViewById(R.id.dialog_version_cancel)
				.setOnClickListener(this);
		mView.findViewById(R.id.dialog_version_confirm)
				.setOnClickListener(this);

		dialog = new AlertDialog.Builder(new ContextThemeWrapper(context,
				android.R.style.Theme_Holo_Light)).setView(mView).create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	}

	
	public void show() {
		dialog.show();
	}

	
	@Override
	public void onClick(View v) {
		dialog.dismiss();
		if (v.getId() == R.id.dialog_version_confirm) {
			if (clickListener != null) {
				// 1 强制更新 2 非强制更新
				clickListener.onClick(dialog, isEnforcement ? 1 : 2);
			}
			VersionUpdate.downLoadApk(v.getContext(), iconId, downloadUrl, version,
					isEnforcement, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (clickListener != null) {
								// 4 更新下载失败 5 更新下载成功
								clickListener.onClick(dialog, which);
							}
						}
					});
		}
		if (v.getId() == R.id.dialog_version_cancel) {
			if (clickListener != null) {
				// 3 取消更新
				clickListener.onClick(dialog, 3);
			}
		}
	}
}

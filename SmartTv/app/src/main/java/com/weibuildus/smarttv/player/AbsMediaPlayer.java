package com.weibuildus.smarttv.player;

import android.view.SurfaceHolder;

public abstract class AbsMediaPlayer {

	// 获取
	/** 获取播放位置 */
	public abstract int getCurrentPosition();

	/** 获取播放文件总时长 */
	public abstract int getDuration();

	/** 视频高 */
	public abstract int getVideoHeight();

	/** 视频宽 */
	public abstract int getVideoWidth();

	/** 是否循环 */
	public abstract boolean isLooping();

	/** 是否正在播放 */
	public abstract boolean isPlaying();

	// 设置
	/** 设置媒体地址 */
	public abstract void setDataSource(String path);

	/** 设置显示画面 */
	public abstract void setDisplay(SurfaceHolder holder);

	/** 设置是否循环 */
	public abstract void setLooping(boolean looping);

	/** 跳转到指定位置播放 */
	public abstract void seekTo(int msec);

	// 控制
	/** 准备 */
	public abstract void prepare();

	/** 准备异步 */
	public abstract void prepareAsync();

	/** 开始播放 */
	public abstract void start();

	/** 暂停 */
	public abstract void pause();

	/** 停止播放 */
	public abstract void stop();

	/** 释放资源 */
	public abstract void release();

	/** 重置 */
	public abstract void reset();
	
	/**
	 * 设置接口
	 * @param listener
	 */
	public abstract void setOnSysPlayerEventListener(OnSysPlayerEventListener listener);

	/**
	 * 播放事件接口
	 * @author WuMaojie
	 * @version  1.00 
	 */
	public interface OnSysPlayerEventListener {
		/** 发送缓冲数据信息 */
		void onBufferingUpdate(AbsMediaPlayer mp, int percent);

		/** 发送播放结束信息 */
		void onCompletion(AbsMediaPlayer mp);

		/** 发送错误信息 */
		boolean onError(AbsMediaPlayer mp, int what, int extra);

		/** 发送播放器信息 */
		boolean onInfo(AbsMediaPlayer mp, int what, int extra);

		/** 发送准备信息 */
		void onPrepared(AbsMediaPlayer mp);

		/** 发送进度条信息 */
		void onProgressUpdate(AbsMediaPlayer mp, int time, int length);

		/** 发送视频大小改变信息 */
		void onVideoSizeChangedListener(AbsMediaPlayer mp, int width, int height);
	}
}

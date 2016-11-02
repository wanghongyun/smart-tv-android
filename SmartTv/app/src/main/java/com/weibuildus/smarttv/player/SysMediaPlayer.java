package com.weibuildus.smarttv.player;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.weibuildus.smarttv.util.Logger;

public class SysMediaPlayer extends AbsMediaPlayer implements
		MediaPlayer.OnBufferingUpdateListener,
		MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnSeekCompleteListener,
		MediaPlayer.OnVideoSizeChangedListener {

	// 静态常量
	private static final String LOGTAG = "SysPlayer-SysMediaPlayer";

	// 静态变量
	private static SysMediaPlayer sInstance;

	// 对象全局变量
	private MediaPlayer mMediaPlayer;

	/** 播放器事件接口 */
	private AbsMediaPlayer.OnSysPlayerEventListener listener = null;
	/** 定时器 */
	private Timer mTimer = null;

	// 静态方法
	/**
	 * 获取系统播放器对象
	 */
	public static SysMediaPlayer getInstance() {
		if (sInstance == null) {
			sInstance = new SysMediaPlayer();
		}
		return sInstance;
	}

	private SysMediaPlayer() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
	}

	/**
	 * 当视频大小改变时被调用
	 */
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		if (listener != null) {
			listener.onVideoSizeChangedListener(this, width, height);
		}
	}

	/**
	 * 当一个进度查找操作完成时被调用
	 */
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		/* not used */
	}

	/**
	 * 当播放器准备好时被调用
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		if (listener != null) {
			listener.onPrepared(this);
		}
	}

	/**
	 * 回调传达一些信息和警告
	 */
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (listener != null) {
			return listener.onInfo(this, what, extra);
		}
		return true;
	}

	/**
	 * 异步异常信息调用，返回true,表示异常已处理，返回false,表示异常没有处理，将会调用onCompletion
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (listener != null) {
			return listener.onError(this, what, extra);
		}
		return false;
	}

	/**
	 * 播放完成回调
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (listener != null) {
			listener.onCompletion(this);
		}
	}

	/**
	 * 网络缓冲进度回调
	 * 
	 * @param mp
	 * @param percent
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if (listener != null) {
			listener.onBufferingUpdate(this, percent);
		}
	}

	@Override
	public int getCurrentPosition() {
		int ret = -1;
		try {
			ret = mMediaPlayer.getCurrentPosition();
		} catch (Exception e) {
			Logger.e(LOGTAG, "getCurrentPosition()");
		}
		return ret;
	}

	@Override
	public int getDuration() {
		int ret = -1;
		try {
			ret = mMediaPlayer.getDuration();
		} catch (Exception e) {
			Logger.e(LOGTAG, "getDuration()");
		}
		return ret;
	}

	@Override
	public int getVideoHeight() {
		int ret = -1;
		try {
			ret = mMediaPlayer.getVideoHeight();
		} catch (Exception e) {
			Logger.e(LOGTAG, "getVideoHeight()");
		}
		return ret;
	}

	@Override
	public int getVideoWidth() {
		int ret = -1;
		try {
			ret = mMediaPlayer.getVideoWidth();
		} catch (Exception e) {
			Logger.e(LOGTAG, "getVideoWidth()");
		}
		return ret;
	}

	@Override
	public boolean isLooping() {
		try {
			return mMediaPlayer.isLooping();
		} catch (Exception e) {
			Logger.e(LOGTAG, "isLooping()");
			return false;
		}
	}

	@Override
	public boolean isPlaying() {
		try {
			return mMediaPlayer.isPlaying();
		} catch (Exception e) {
			Logger.e(LOGTAG, "isPlaying()");
			return false;
		}
	}

	@Override
	public void setDataSource(String path) {
		try {
			if (path.startsWith("file://"))
				path = path.substring(7);
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} catch (Exception e) {
			Logger.e(LOGTAG, "setDataSource()");
		}
	}

	@Override
	public void setDisplay(SurfaceHolder holder) {
		try {
			mMediaPlayer.setDisplay(holder);
		} catch (Exception e) {
			Logger.e(LOGTAG, "setDisplay()");
		}
	}

	@Override
	public void setLooping(boolean looping) {
		try {
			mMediaPlayer.setLooping(looping);
		} catch (Exception e) {
			Logger.e(LOGTAG, "setLooping()");
		}
	}

	@Override
	public void seekTo(int msec) {
		try {
			mMediaPlayer.seekTo(msec);
		} catch (Exception e) {
			Logger.e(LOGTAG, "seekTo()");
		}
	}

	@Override
	public void prepare() {
		try {
			mMediaPlayer.prepare();
		} catch (Exception e) {
			Logger.e(LOGTAG, "prepare()");
		}
	}

	@Override
	public void prepareAsync() {
		try {
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			Logger.e(LOGTAG, "prepareAsync()");
		}
	}

	@Override
	public void start() {
		try {
			mMediaPlayer.start();
			if (mTimer != null) {
				mTimer.cancel();
			}
			mTimer = new Timer();
			mTimer.schedule(new MyTimerTask(), 100, 250);
			Logger.d(LOGTAG, "start()");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(LOGTAG, "start()");
		}
	}

	@Override
	public void pause() {
		try {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			mMediaPlayer.pause();
			Logger.d(LOGTAG, "pause()");
		} catch (Exception e) {
			Logger.e(LOGTAG, "pause()");
		}
	}

	@Override
	public void stop() {
		try {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			mMediaPlayer.stop();
		} catch (Exception e) {
			Logger.e(LOGTAG, "stop()");
		}
	}

	@Override
	public void release() {
		try {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			mMediaPlayer.release();
		} catch (Exception e) {
			Logger.e(LOGTAG, "release()");
		}
		sInstance = null;
	}

	@Override
	public void reset() {
		stop();
		try {
			mMediaPlayer.reset();
		} catch (Exception e) {
			Logger.e(LOGTAG, "reset()");
		}
	}

	@Override
	public void setOnSysPlayerEventListener(OnSysPlayerEventListener listener) {
		this.listener = listener;
	}

	/**
	 * 自定义定时类
	 * 
	 * @author WuMaojie
	 * @version 1.00
	 * 
	 */
	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			// 发送异步处理消息
			sendTimerTaskMessage();
		}
	}

	/** 异步消息接收 */
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				runTimerTask();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 发送异步处理消息
	 */
	private void sendTimerTaskMessage() {
		handler.sendEmptyMessage(1);
	}

	/**
	 * 运行进度查询操作，并回调接口
	 */
	private void runTimerTask() {
		try {
			if (mMediaPlayer == null || listener == null)
				return;
			if (mMediaPlayer.isPlaying()) {
				// 获取当前播放进度，和文件总长，并调用 播放进度接口方法。
				int time = mMediaPlayer.getCurrentPosition();
				int length = mMediaPlayer.getDuration();
				listener.onProgressUpdate(SysMediaPlayer.this, time, length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

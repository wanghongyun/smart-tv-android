package com.weibuildus.smarttv.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.weibuildus.smarttv.R;
import com.weibuildus.smarttv.util.Logger;


/**
 * 单程系统播放器
 * 
 * @ClassName: SysVideoPlayer
 * @author wumaojie.gmail.com
 * @date 2015-1-5 下午3:03:32
 */
public class SysVideoPlayer extends LinearLayout implements
		AbsMediaPlayer.OnSysPlayerEventListener, OnClickListener,
		OnSeekBarChangeListener {
	/** LOG标签 */
	private static final String LOGTAG = "SysPlayer-SysVideoPlayer";
	/** 可以播放的后缀名 */
	@SuppressWarnings("unused")
	private static final String[] POSTFIXS = new String[] { ".mp4", ".3gp" };

	/** Android 上下文 */
	private Context context = null;
	/** 显示画面 */
	private SurfaceView surfaceView = null;
	/** 缓冲进度条 */
	private ProgressBar progressBar = null;
	/** 缓冲进度 */
	// private TextView textView = null;
	/** 播放器 */
	private static AbsMediaPlayer mediaPlayer = null;

	/** 媒体播放器是否装载完成 */
	private static boolean mMediaPlayerLoaded = false;
	/** 媒体播放器是否开始播放 */
	private static boolean mMediaPlayerStarted = false;

	/** 播放器回调接口 */
	private OnSysVideoPlayerEventListener eventListener;

	/** 播放器界面总布局 */
	private FrameLayout layout = null;
	/** 播放器界面控制布局 */
	private LinearLayout layoutControl = null;
	/** 控制面板下面部分布局 */
	private LinearLayout layoutControlBottom = null;
	/** 播放暂停按钮 */
	private ImageView btn_playPause = null;

	/** 播放时间位置 */
	private TextView text_position = null;
	/** 播放时间总长 */
	private TextView text_length = null;
	/** 播放进度 */
	private SeekBar seekBar = null;
	/** 宽高比 */
	private int mAspectRatio = 0;
	/** 控制面板双击时间记录 */
	@SuppressWarnings("unused")
	private long layoutControlTime = 0;

	/** 播放时间位置 */
	private static int player_position = 0;
	/** 缓冲位置 */
	private static int buffer_position = 0;
	/** 播放时间总长 */
	private static int player_length = 100;

	// 画面显示比列

	// 按横向或竖向满屏
	private static final int SURFACE_NONE = 0;
	// 全屏拉伸
	private static final int SURFACE_FILL = 1;
	// 原始大小
	private static final int SURFACE_ORIG = 2;
	// 4：3
	private static final int SURFACE_4_3 = 3;
	// 16：9
	private static final int SURFACE_16_9 = 4;
	// 16：10
	private static final int SURFACE_16_10 = 5;

	private static final int SURFACE_MAX = 6;

	// 操作信息常量
	// 缓冲
	private static final int MEDIA_PLAYER_BUFFERING_UPDATE = 0x4001;
	// 播放完成
	private static final int MEDIA_PLAYER_COMPLETION = 0x4002;
	// 播放错误
	private static final int MEDIA_PLAYER_ERROR = 0x4003;
	// 播放信息
	private static final int MEDIA_PLAYER_INFO = 0x4004;
	// 播放准备完毕
	private static final int MEDIA_PLAYER_PREPARED = 0x4005;
	// 播放进度
	private static final int MEDIA_PLAYER_PROGRESS_UPDATE = 0x4006;
	// 视频大小改变
	private static final int MEDIA_PLAYER_VIDEO_SIZE_CHANGED = 0x4007;

	// 异步控制
	private Handler handler = null;

	// 播放地址
	private static String url = null;

	public SysVideoPlayer(Context context) {
		super(context);
		initialize(context);
	}

	public SysVideoPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void initialize(Context context) {
		this.context = context;
		// 加载控件
		addWidget(context);
		// 设置控制对象及其逻辑
		setControl();
	}

	/**
	 * 加载控件
	 * 
	 * @param context
	 */
	private void addWidget(Context context) {
		// 总布局
		layout = new FrameLayout(context);
		layout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
		layout.setBackgroundColor(Color.BLACK);
		// 控制面板布局
		layoutControl = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.video_player_control, null);
		addLayoutControl(layoutControl);
		// 画面
		surfaceView = new SurfaceView(context);
		surfaceView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1,
				Gravity.CENTER));
		surfaceView.setKeepScreenOn(true);
		setSurfaceHolderCallback(surfaceView);

		// 进度圆圈
		progressBar = new ProgressBar(context);
		progressBar.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		// 进度
		// textView = new TextView(context);
		// textView.setLayoutParams(new FrameLayout.LayoutParams(
		// FrameLayout.LayoutParams.WRAP_CONTENT,
		// FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

		// 装载控件
		this.addView(layout);
		layout.addView(surfaceView);
		layout.addView(progressBar);
		// layout.addView(textView);
		layout.addView(layoutControl);
	}

	private void addLayoutControl(LinearLayout lControl) {
		layoutControlBottom = (LinearLayout) lControl
				.findViewById(R.id.player_control_bottom);
		btn_playPause = (ImageView) lControl
				.findViewById(R.id.player_button_play);

		text_position = (TextView) lControl
				.findViewById(R.id.player_text_position);
		text_length = (TextView) lControl.findViewById(R.id.player_text_length);

		seekBar = (SeekBar) lControl.findViewById(R.id.player_seekbar_progress);
		layoutControlBottom.setVisibility(View.GONE);
		lControl.setOnClickListener(this);
		btn_playPause.setOnClickListener(this);

		seekBar.setMax(player_length);
		seekBar.setProgress(player_position);
		seekBar.setSecondaryProgress(buffer_position);
		seekBar.setOnSeekBarChangeListener(this);

		text_position.setText(intTimeToString(player_position));
		text_length.setText(intTimeToString(player_length));
	}

	/**
	 * 画面自动回调事件
	 * 
	 * @param sfv
	 */
	private void setSurfaceHolderCallback(SurfaceView sfv) {
		//sfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		sfv.setKeepScreenOn(true);
		sfv.getHolder().addCallback(new SurfaceHolder.Callback() {
			// SurfaceView创建时调用一次
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (url != null) {
					Logger.d(LOGTAG, "播放器视图创建");
					if (mediaPlayer != null) {
						mediaPlayer.setDisplay(holder);
						mediaPlayer
								.setOnSysPlayerEventListener(SysVideoPlayer.this);
						play();
						changeSize(SURFACE_NONE);
					} else {
						// 获取选择后的播放器对象
						mediaPlayer = SysMediaPlayer.getInstance();
						// 初始化播放器
						mMediaPlayerLoaded = false;
						mMediaPlayerStarted = false;
						mediaPlayer
								.setOnSysPlayerEventListener(SysVideoPlayer.this);
						mediaPlayer.reset();
						mediaPlayer.setDisplay(holder);
						mediaPlayer.setDataSource(url);
						mediaPlayer.prepareAsync();
					}
				}
			}

			// surfaceDestroyed调用后此方法调用一次到两次
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (mediaPlayer != null) {
					mediaPlayer.setDisplay(holder);
					changeSize(SURFACE_NONE);
				}
			}

			// SurfaceView销毁时调用
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mediaPlayer != null) {
					Logger.d(LOGTAG, "播放器视图销毁");
					pause();
					mediaPlayer.setDisplay(null);
				}
			}

		});
	}

	/**
	 * 释放资源
	 * 
	 * @Title: release
	 * @throws
	 */
	public static void release() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			player_position = 0;
			buffer_position = 0;
			player_length = 100;
		}
	}
	
	public boolean isMediaPlayerNull(){
		return mediaPlayer==null;
	}

	public void rePlay(String url) {
		SysVideoPlayer.url = url;
		// 初始化播放器
		mMediaPlayerLoaded = false;
		mMediaPlayerStarted = false;
		mediaPlayer.setOnSysPlayerEventListener(SysVideoPlayer.this);
		mediaPlayer.reset();
		mediaPlayer.setDisplay(surfaceView.getHolder());
		mediaPlayer.setDataSource(url);
		mediaPlayer.prepareAsync();
	}

	/**
	 * 创建控制对象
	 */
	private void setControl() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				// 缓冲
				case MEDIA_PLAYER_BUFFERING_UPDATE: {
					if (msg.arg1 < 100 && !isPlaying()) {
						progressBar.setVisibility(View.VISIBLE);
					} else {
						progressBar.setVisibility(View.GONE);
					}
					if (layoutControlBottom != null) {
						//if (layoutControlBottom.getVisibility() == View.VISIBLE) {
							buffer_position = (int) (seekBar.getMax()
									* msg.arg1 / (float) 100);
							seekBar.setSecondaryProgress(buffer_position);
						//}
					}
					break;
				}
					// 完成
				case MEDIA_PLAYER_COMPLETION: {
					mMediaPlayerStarted = false;
					if (btn_playPause != null) {
						btn_playPause
								.setBackgroundResource(R.mipmap.player_media_play);
					}
					if (eventListener != null)
						eventListener.onSysVideoCompletion();
					break;
				}
					// 播放错误停止
				case MEDIA_PLAYER_ERROR: {
					mMediaPlayerStarted = false;
					if (btn_playPause != null) {
						btn_playPause
								.setBackgroundResource(R.mipmap.player_media_play);
					}
					if (eventListener != null)
						eventListener.onSysVideoError();
					break;
				}
					// 播放器 信息
				case MEDIA_PLAYER_INFO: {
					break;
				}
					// 播放准备
				case MEDIA_PLAYER_PREPARED: {
					mMediaPlayerLoaded = true;
						// 关闭等待框
					progressBar.setVisibility(View.GONE);
						// 设置初始比列
					changeSurfaceSize(mediaPlayer, surfaceView, mAspectRatio);
						// 开始播放
					if (eventListener != null){
						if(eventListener.onSysVideoPrepared()){
							play();
						}
					}else{
						play();
					}
					break;
				}
					// 播放器进度条修订
				case MEDIA_PLAYER_PROGRESS_UPDATE: {
					if (layoutControlBottom != null) {
						//if (layoutControlBottom.getVisibility() == View.VISIBLE) {
							if (mediaPlayer != null) {
							player_length = mediaPlayer.getDuration();
							player_position = mediaPlayer.getCurrentPosition();
							seekBar.setMax(player_length);
							seekBar.setProgress(player_position);
							text_length.setText(intTimeToString(player_length));
							}
						//}
					}
					break;
				}
					// 改变现实比列
				case MEDIA_PLAYER_VIDEO_SIZE_CHANGED: {
					changeSurfaceSize(mediaPlayer, surfaceView, mAspectRatio);
					break;
				}
				default:
					break;
				}
			}
		};
	}

	/**
	 * 时间格式化
	 * 
	 * @param time
	 * @return
	 */
	private String intTimeToString(int time) {
		String format = "mm:ss";
		if (1000 * 60 * 60 <= time) {
			format = "HH:mm:ss";
		}
		// 去除时区影响
		long ltime = time - TimeZone.getDefault().getRawOffset();
		return new SimpleDateFormat(format).format(new Date(ltime));
	}

	/**
	 * 改变画面大小
	 * 
	 * @param player
	 * @param surface
	 * @param ar
	 *            目标比列
	 */
	private void changeSurfaceSize(AbsMediaPlayer player, SurfaceView surface,
			int ar) {
		// 获取视频宽高
		int videoWidth = player.getVideoWidth();
		int videoHeight = player.getVideoHeight();
		if (videoWidth <= 0 || videoHeight <= 0) {
			return;
		}
		// 设置画面宽高
		SurfaceHolder holder = surface.getHolder();
		holder.setFixedSize(videoWidth, videoHeight);
		// 获取手机屏幕宽高
		@SuppressWarnings("deprecation")
		int displayWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int displayHeight = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		// 目标宽高
		int targetWidth = -1;
		int targetHeight = -1;
		switch (ar) {
		// 按横向或竖向满屏
		case SURFACE_NONE: {
			targetWidth = videoWidth;
			targetHeight = videoHeight;
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_0);
			break;
		}
			// 满屏
		case SURFACE_FILL: {
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_1);
			break;
		}
			// 原始宽高
		case SURFACE_ORIG: {
			displayWidth = videoWidth;
			displayHeight = videoHeight;
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_2);
			break;
		}
			// 4比3
		case SURFACE_4_3: {
			targetWidth = 4;
			targetHeight = 3;
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_3);
			break;
		}
			// 16比9
		case SURFACE_16_9: {
			targetWidth = 16;
			targetHeight = 9;
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_4);
			break;
		}
			// 16比10
		case SURFACE_16_10: {
			targetWidth = 16;
			targetHeight = 10;
			// if (btn_surfaceChange != null)
			// btn_surfaceChange
			// .setBackgroundResource(R.drawable.btn_aspect_ratio_5);
			break;
		}
		default:
			break;
		}
		// 计算宽高
		if (targetWidth > 0 && targetHeight > 0) {
			double ard = (double) displayWidth / (double) displayHeight;
			double art = (double) targetWidth / (double) targetHeight;
			if (ard > art) {
				displayWidth = displayHeight * targetWidth / targetHeight;
			} else {
				displayHeight = displayWidth * targetHeight / targetWidth;
			}
		}
		// 设置画面的宽高值 ，刷新画面
		ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
		lp.width = displayWidth;
		lp.height = displayHeight;
		surface.setLayoutParams(lp);
		surface.invalidate();
	}

	/**
	 * 打开流媒体地址，准备播放
	 * 
	 * @param url
	 */
	public void open(String url, OnSysVideoPlayerEventListener listener) {
		surfaceView.setVisibility(View.GONE);
		// 回调监听
		this.eventListener = listener;
		// 非空判断
		if (url == null) {
			return;
			// throw new RuntimeException("url can not null!");
		}
		// 如果相等
		if (!url.equals(SysVideoPlayer.url)) {
			// 重置url
			SysVideoPlayer.url = url;
			// 如果播放器存在，则重置播放器开始播放新视频
			if (mediaPlayer != null) {
				rePlay(url);
			}
		}
		surfaceView.setVisibility(View.VISIBLE);
	}

	/**
	 * 播放
	 */
	public void play() {
		if (mMediaPlayerStarted || !mMediaPlayerLoaded)
			return;
		if (mediaPlayer != null) {
			mediaPlayer.start();
			mMediaPlayerStarted = true;
			if (eventListener != null) {
				eventListener.onSysVideoPlay();
			}
			if (btn_playPause != null) {
				btn_playPause
						.setBackgroundResource(R.mipmap.player_media_pause);
			}
		}
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (!mMediaPlayerStarted || !mMediaPlayerLoaded)
			return;
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mMediaPlayerStarted = false;
			if (eventListener != null) {
				eventListener.onSysVideoPause();
			}
			if (btn_playPause != null) {
				btn_playPause
						.setBackgroundResource(R.mipmap.player_media_play);
			}
		}
	}

	/**
	 * 快进到指定位置
	 * 
	 * @param position
	 */
	public void seekTo(int position) {
		if (mediaPlayer != null)
			mediaPlayer.seekTo(position);
	}

	/**
	 * 是否正在播放
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	/**
	 * 改变画面尺寸
	 * 
	 * @return 按横向或竖向满屏： 0;全屏拉伸： 1;原始大小： 2;4比3： 3;16比9： 4;16比10： 5;
	 */
	public int changeSize() {
		mAspectRatio = (mAspectRatio + 1) % SURFACE_MAX;
		if (mediaPlayer != null)
			changeSurfaceSize(mediaPlayer, surfaceView, mAspectRatio);
		return mAspectRatio;
	}

	/**
	 * 改变画面尺寸
	 * 
	 * @return 按横向或竖向满屏： 0;全屏拉伸： 1;原始大小： 2;4比3： 3;16比9： 4;16比10： 5;
	 */
	public int changeSize(int mAspectRatio) {
		if (mediaPlayer != null)
			changeSurfaceSize(mediaPlayer, surfaceView, mAspectRatio);
		return mAspectRatio;
	}

	/**
	 * 发送信息
	 * 
	 * @param obj
	 * @param what
	 * @param arg1
	 * @param arg2
	 */
	private void sendMessage(Object obj, int what, int arg1, int arg2) {
		Message msg = new Message();
		msg.obj = obj;
		msg.what = what;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		handler.sendMessage(msg);
	}

	@Override
	public void onBufferingUpdate(AbsMediaPlayer mp, int percent) {
		sendMessage(mp, MEDIA_PLAYER_BUFFERING_UPDATE, percent, 0);
	}

	@Override
	public void onCompletion(AbsMediaPlayer mp) {
		sendMessage(mp, MEDIA_PLAYER_COMPLETION, 0, 0);
		System.out.println("播放完成");
	}

	@Override
	public boolean onError(AbsMediaPlayer mp, int what, int extra) {
		sendMessage(mp, MEDIA_PLAYER_ERROR, what, extra);
		return true;
	}

	@Override
	public boolean onInfo(AbsMediaPlayer mp, int what, int extra) {
		sendMessage(mp, MEDIA_PLAYER_INFO, what, extra);
		return true;
	}

	@Override
	public void onPrepared(AbsMediaPlayer mp) {
		sendMessage(mp, MEDIA_PLAYER_PREPARED, 0, 0);
	}

	@Override
	public void onProgressUpdate(AbsMediaPlayer mp, int time, int length) {
		sendMessage(mp, MEDIA_PLAYER_PROGRESS_UPDATE, time, length);
	}

	@Override
	public void onVideoSizeChangedListener(AbsMediaPlayer mp, int width,
			int height) {
		sendMessage(mp, MEDIA_PLAYER_VIDEO_SIZE_CHANGED, width, height);
	}

	public interface OnSysVideoPlayerEventListener {
		public void onSysVideoOpenError();
		
		public void onSysVideoPlay();
		
		public boolean onSysVideoPrepared();

		public void onSysVideoPause();

		public void onSysVideoCompletion();

		public void onSysVideoError();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.player_control_layout:
			if (layoutControlBottom != null) {
				if (layoutControlBottom.getVisibility() == View.VISIBLE) {
					layoutControlBottom.setVisibility(View.GONE);
				} else {
					layoutControlBottom.setVisibility(View.VISIBLE);
				}
			}
				// // 当前时间
//			long time = System.currentTimeMillis();
				// // 双击
//			if (time - layoutControlTime <= 500) {
//				if (layoutControlBottom != null) {
//					if (layoutControlBottom.getVisibility() == View.VISIBLE) {
//						layoutControlBottom.setVisibility(View.GONE);
//					} else {
//						layoutControlBottom.setVisibility(View.VISIBLE);
//					}
//				}
//			} else {
//				layoutControlTime = time;
//			}
			break;
		case R.id.player_button_play:
			if (isPlaying()) {
				pause();
			} else {
				play();
			}
			break;
		default:
			break;
		}
	}

	// 拖拽进度
	private int progress = 0;

	/**
	 * 进度变更
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.progress = progress;
		text_position.setText(intTimeToString(progress));
	}

	/**
	 * 开始触控
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		progress = 0;
	}

	/**
	 * 结束触控
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// 改变播放位置
		if (progress < seekBar.getSecondaryProgress()
				|| seekBar.getProgress() >= seekBar.getSecondaryProgress()) {
			seekTo(progress);
		}
	}

	/**
	 * 获取格式化的系统时间
	 * 
	 * @return
	 */
	public String getTimes() {
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat("HH:mm:ss");
		return sdfDateFormat.format(new Date());
	}
}

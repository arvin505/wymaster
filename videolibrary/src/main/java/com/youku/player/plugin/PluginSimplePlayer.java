package com.youku.player.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.baseproject.image.ImageResizer;
import com.baseproject.utils.Logger;
import com.baseproject.utils.UIUtils;
import com.baseproject.utils.Util;
import com.youku.player.Track;
import com.youku.player.apiservice.ICacheInfo;
import com.youku.player.base.GoplayException;
import com.youku.player.base.Orientation;
import com.youku.player.base.YoukuBasePlayerManager;
import com.youku.player.goplay.Profile;
import com.youku.player.goplay.StaticsUtil;
import com.youku.player.module.VideoCacheInfo;
import com.youku.player.ui.R;
import com.youku.player.ui.interf.IMediaPlayerDelegate;
import com.youku.player.util.DetailMessage;
import com.youku.player.util.DetailUtil;
import com.youku.player.util.PlayCode;
import com.youku.player.util.PlayerUtil;
import com.youku.uplayer.MPPErrorCode;

/**
 * 简单的播放控制插件
 * 
 * @author LongFan
 * @CreateDate 2013年5月15日10:38:55
 */
@SuppressLint("NewApi")
public class PluginSimplePlayer extends PluginOverlay implements DetailMessage {
	private LinearLayout titleLayoutPort;
	private TextView playTitleTextView;// 播放标题

	private RelativeLayout controlLayout;
	private ImageButton play_pauseButton;// 播放暂停按钮
	private ImageButton full_screenButton;// 全屏按钮
	private String id;// 上页传递id video/show

	private LinearLayout mContainerLayout;// 整个布局layout
	// private LinearLayout mainPlayLayout;// 包含播放器的部分
	private ImageButton userPlayImageButton;
	private FrameLayout playerView;
	private YoukuBasePlayerManager mBasePlayerManager;
	private Activity mActivity;
	private ImageView userPlayButton;
	private FrameLayout interactFrameLayout;
	private View containerView;
	private String video_id;// 当前播放视频id
	private View seekLoadingContainerView;
	private RelativeLayout loadingInfoLayout;
	private SeekBar infoSeekBar;
	private int currentVolume; // 当前音量的大小
	private boolean isVolumeOpen; // 是否有声音
	private ImageButton ibMuteSound, ibStopPlayBtn;
	private AudioManager audioManager;
	private GestureDetector mGestureDetector;
	private boolean isChangeSeekBar = false;
	private int light; // 当前手机的亮度
	private int maxVolume = 0;// 最大媒体音量
    private RelativeLayout llVolumeUpDown;
    private ImageButton ibVideoLockScreen;
    private boolean isLockScreen=false;
	// private Loading playLoading;
	private Animation mAnimSlideInTop;
	private Animation mAnimSlideInBottom;
	private Animation mAnimSlideOutTop;
	private Animation mAnimSlideOutBottom;
	public PluginSimplePlayer(YoukuBasePlayerManager basePlayerManager,
			IMediaPlayerDelegate mediaPlayerDelegate) {
		super(basePlayerManager.getBaseActivity(), mediaPlayerDelegate);
		mBasePlayerManager = basePlayerManager;
		this.mActivity = basePlayerManager.getBaseActivity();
		LayoutInflater mLayoutInflater = LayoutInflater.from(mActivity);
		containerView = mLayoutInflater.inflate(
				R.layout.yp_plugin_detail_play_interact, null);
		if (null != mediaPlayerDelegate
				&& mediaPlayerDelegate.videoInfo != null)
			video_id = mediaPlayerDelegate.videoInfo.getVid(); // 播放的vid
		addView(containerView); // 添加播放试图
		initPlayLayout();
        setOnclickListener();
		initAnimation();
	}
	public PluginSimplePlayer.ControlTitleShowHide controlTitleShowHide;
	public void setContext(PluginSimplePlayer.ControlTitleShowHide listener){
		controlTitleShowHide=listener;
	}
	private void initAnimation() {
		mAnimSlideOutBottom = AnimationUtils.loadAnimation(mActivity,
				R.anim.venvy_slide_out_bottom);

		mAnimSlideOutTop = AnimationUtils.loadAnimation(mActivity,
				R.anim.venvy_slide_out_top);
		mAnimSlideInBottom = AnimationUtils.loadAnimation(mActivity,
				R.anim.venvy_slide_in_bottom);
        mAnimSlideInBottom.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ibVideoLockScreen.setEnabled(false);
				interactFrameLayout.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
                ibVideoLockScreen.setEnabled(true);
				interactFrameLayout.setEnabled(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mAnimSlideInTop = AnimationUtils.loadAnimation(mActivity,
				R.anim.venvy_slide_in_top);
		mAnimSlideInTop.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ibVideoLockScreen.setEnabled(false);
				interactFrameLayout.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
                ibVideoLockScreen.setEnabled(true);
				interactFrameLayout.setEnabled(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mAnimSlideOutTop.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ibVideoLockScreen.setEnabled(false);
				interactFrameLayout.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if(titleLayoutPort!=null){
					titleLayoutPort.setVisibility(View.INVISIBLE);
				}
				ibVideoLockScreen.setEnabled(true);
				interactFrameLayout.setEnabled(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mAnimSlideOutBottom
				.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						ibVideoLockScreen.setEnabled(false);
						interactFrameLayout.setEnabled(false);
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if(controlLayout!=null){
							controlLayout.setVisibility(View.GONE);
						}
						ibVideoLockScreen.setEnabled(true);
						interactFrameLayout.setEnabled(true);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
	}




	/**
	 * 清除不用的对象，释放内存
	 */
	public void clear() {
		seekHandler.removeCallbacksAndMessages(null);
		seekendHandler.removeCallbacksAndMessages(null);
		loadInfoHandler.removeCallbacksAndMessages(null);
		hideHandler.removeCallbacksAndMessages(null);
		playHandler.removeCallbacksAndMessages(null);
		playLoadingBar = null;
		seekLoadingContainerView = null;
		mContainerLayout.setOnClickListener(null);
		mContainerLayout = null;
		interactFrameLayout = null;
		userPlayButton.setOnClickListener(null);
		userPlayButton.setImageBitmap(null);
		userPlayButton = null;
		containerView = null;
		controlLayout.clearAnimation();
		titleLayoutPort.clearAnimation();
	}

	SeekBar videoBar,volumeBar;
	TextView totalTime;
	TextView currentTime;
	private View retryView;
	private LinearLayout goRetry;

	// 重试
	/**
	 * 重试初始化
	 */
	private void initRetry() {
		if (null == containerView)
			return;
		retryView = containerView.findViewById(R.id.view_restart);
		goRetry = (LinearLayout) containerView.findViewById(R.id.go_retry);
		if (null != goRetry) {
			goRetry.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					error = false;
					if (!Util.hasInternet()) {

						PlayerUtil.showTips(R.string.player_tips_no_network);
						return;
					}
					if (null != retryView)
						retryView.setVisibility(View.GONE);
					if (null != mMediaPlayerDelegate) {
						if (!infoFail) {
							mMediaPlayerDelegate.release();
							mMediaPlayerDelegate.setFirstUnloaded();
							mMediaPlayerDelegate.start();
							mMediaPlayerDelegate.retry();
							showLoading();
						} else {
							if (mMediaPlayerDelegate != null
									&& mMediaPlayerDelegate.videoInfo != null) {
								mMediaPlayerDelegate
										.playVideo(mMediaPlayerDelegate.videoInfo
												.getVid());
								mMediaPlayerDelegate.setFirstUnloaded();
							} else if (!TextUtils
									.isEmpty(mMediaPlayerDelegate.nowVid)) {
								mMediaPlayerDelegate
										.playVideo(mMediaPlayerDelegate.nowVid);
								mMediaPlayerDelegate.setFirstUnloaded();
								// mMediaPlayerDelegate.retry();
							}
							// mMediaPlayerDelegate.replayVideo();
						}
					}
				}
			});
		}
	}

	// 设置点击事件
	private void setOnclickListener() {
		if (UIUtils.hasHoneycomb()) {
			mContainerLayout
					.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {

						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							if(isNotClickable())
								return;
							hideShowControl();
						}
					});
		}

		ibVideoLockScreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if(isNotClickable()) {
//					return;
//				}
				if (ibVideoLockScreen.isSelected()
						&& mMediaPlayerDelegate.isFullScreen) {
					ibVideoLockScreen.setSelected(false);
					isLockScreen = false;
					if(!isLockScreen) {
						controlLayout.startAnimation(mAnimSlideInBottom); //底部滑入
						controlLayout.setVisibility(View.VISIBLE);
						showTitle();
					}
				} else {
					ibVideoLockScreen.setSelected(true);
					isLockScreen = true;
					hideTitle();
					hideControl();
					hideVolume();
					if(hideHandler!=null){
						hideHandler.removeCallbacksAndMessages(null);
					}
				}
			}
		});
		interactFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if(isNotClickable())
//					return;
				onContainerClick();
			}
		});
		interactFrameLayout.setOnTouchListener(onTouchListener); // 通过触摸事件来实现视频进度
		if (null != videoBar) {
			videoBar.setOnSeekBarChangeListener(mBarChangeListener);
		}
		play_pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isNotClickable())
					return;
				if (null == mMediaPlayerDelegate)
					return;
				if (isLoading) {
					play_pauseButton
							.setImageResource(R.drawable.play_btn_pause_big_detail_down);
					return;
				}

				if (mMediaPlayerDelegate.isPlaying()) {
					mMediaPlayerDelegate.pause();
					if (!isLoading) {
						play_pauseButton
								.setImageResource(R.drawable.play_btn_play_big_detail);
					} else {
						play_pauseButton
								.setImageResource(R.drawable.play_btn_play_big_detail);
					}
				} else {
					mMediaPlayerDelegate.start();
					if (null != play_pauseButton)
						if (!isLoading) {
							play_pauseButton
									.setImageResource(R.drawable.play_btn_pause_big_detail_down);
						} else {
							play_pauseButton
									.setImageResource(R.drawable.play_btn_pause_big_detail_down);
						}
				}
				if (isBack) {
					isBack = false;
					isLoading = true;
					play_pauseButton
							.setImageResource(R.drawable.play_btn_pause_big_detail_down);
				}
				userAction(); // 隐藏标题和播放控制布局
			}
		});
		full_screenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isNotClickable())
					return;
				//只有在点击全屏按钮的情况下才能切换横竖屏
				if(mBasePlayerManager!=null){
					mBasePlayerManager.setClickChange(true);
				}
				if (mMediaPlayerDelegate.isFullScreen) {
					goSmallHideAll();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if(ibStopPlayBtn!=null){
								ibStopPlayBtn.setVisibility(View.VISIBLE);
							}
							if(controlTitleShowHide!=null) {
								controlTitleShowHide.showTitle();
							}
						}
					},500);
					userAction();
				} else {
					if(ibStopPlayBtn!=null){
						ibStopPlayBtn.setVisibility(View.GONE);
					}
					if(controlTitleShowHide!=null) {
						controlTitleShowHide.hideTitle();
					}
					mMediaPlayerDelegate.goFullScreen();
					userAction();
//					if(titleLayoutPort!=null){
//						titleLayoutPort.setVisibility(View.INVISIBLE);
//					}
//					if(controlLayout!=null){
//						controlLayout.setVisibility(View.GONE);
//					}

//					showTitle();
//					showLockBtn();
//					stopVideoBtnShow();
				}

			}
		});
		ibMuteSound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isNotClickable())
					return;
				if (isVolumeOpen) {
					closeMuteSoundState();
					currentVolume = audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
							0);
				} else {
					openMuteSoundState();
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
				}
			}
		});

		ibStopPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if(isNotClickable())
//					return;
				if (mMediaPlayerDelegate == null) {
					return;
				}
				if(controlTitleShowHide!=null) {
					controlTitleShowHide.closeSurfaceView();
				}
//				if (!mMediaPlayerDelegate.isFullScreen) {
//					if(userPlayButton!=null){
//						userPlayButton.setVisibility(View.GONE);
//					}
//					if(controlLayout!=null){
//						controlLayout.setVisibility(View.GONE);
//					}
//					onCompletionListener();
//					if(controlTitleShowHide!=null) {
//						controlTitleShowHide.closeSurfaceView();
//					}
//				}
			}
		});

	}

	private void goSmallHideAll() {
		if(titleLayoutPort!=null && titleLayoutPort.getVisibility()==View.VISIBLE){
			titleLayoutPort.clearAnimation();  //清除动画 才能让viewxia
            titleLayoutPort.setVisibility(View.INVISIBLE);
        }
		if (null != ibVideoLockScreen) {
            ibVideoLockScreen.setVisibility(View.GONE);
        }
//		if(controlLayout!=null){
//			controlLayout.setVisibility(View.GONE);
//        }
		hideVolume();
		//	stopVideoBtnHide();
		mMediaPlayerDelegate.goSmall();

	}

	private void closeMuteSoundState() {
		isVolumeOpen = !isVolumeOpen;
		ibMuteSound.setImageResource(R.drawable.vedio_sound_close);


	}

	private void openMuteSoundState() {
		isVolumeOpen = !isVolumeOpen;
		ibMuteSound.setImageResource(R.drawable.vedio_sound_open);
	}

	// 初始化播放区控件
	/**
	 * 找到相关的layout
	 */
	private void initPlayLayout() {
		if (null == containerView)
			return;
		light = getScreenBrightness(mActivity); // 初始化屏幕的亮度
		mMediaPlayerDelegate.isStartPlay = false;
		mMediaPlayerDelegate.pause();
		audioManager = (AudioManager) mActivity
				.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekLoadingContainerView = containerView
				.findViewById(R.id.seek_loading_bg); // 刚进入播放页面时候加载的进度条
		seekLoadingContainerView.setVisibility(View.GONE);
		mContainerLayout = (LinearLayout) containerView
				.findViewById(R.id.ll_detail_container);// 整个播放容器布局
		mContainerLayout.setClickable(true);
		llVolumeUpDown=(RelativeLayout)findViewById(R.id.ll_volume_up_down);   //手势下的音量界面
		ibVideoLockScreen=(ImageButton)findViewById(R.id.ib_video_lock_screen);
		interactFrameLayout = (FrameLayout) containerView
				.findViewById(R.id.fl_interact);
		setClickable(true, interactFrameLayout);
		controlLayout = (RelativeLayout) containerView
				.findViewById(R.id.layout_play_control); // 播放器控制界面
		if (null != controlLayout)
			controlLayout.setVisibility(View.GONE);
		volumeBar=(SeekBar)containerView.findViewById(R.id.sb_volume_progress);
		volumeBar.setMax(maxVolume);
		volumeBar.setProgress(currentVolume);
		
		videoBar = (SeekBar) containerView
				.findViewById(R.id.sb_detail_play_progress);
		totalTime = (TextView) containerView.findViewById(R.id.total_time);
		currentTime = (TextView) containerView.findViewById(R.id.current_time);
		
		play_pauseButton = (ImageButton) containerView
				.findViewById(R.id.ib_play_pause_vedio);
		
		full_screenButton = (ImageButton) containerView
				.findViewById(R.id.ib_detail_play_full);

		playTitleTextView = (TextView) containerView
				.findViewById(R.id.tv_detail_play_title);
		titleLayoutPort = (LinearLayout) containerView
				.findViewById(R.id.layout_title);
		titleLayoutPort.setOnClickListener(null);
		titleLayoutPort.setVisibility(View.INVISIBLE);
		initSeekLoading();
		if (null != mMediaPlayerDelegate
				&& null != mMediaPlayerDelegate.videoInfo) {
			int duration = mMediaPlayerDelegate.videoInfo.getDurationMills();
			videoBar.setMax(duration);
		}
		userPlayButton = (ImageView) containerView
				.findViewById(R.id.ib_user_play);
		//视屏中间的开始播放按钮
		if (null != userPlayButton) {
			userPlayButton.setOnClickListener(userPlayClickListener);
			if(userPlayButton.getVisibility()==View.GONE)
			userPlayButton.setVisibility(View.VISIBLE);
		}
		ibMuteSound = (ImageButton) containerView
				.findViewById(R.id.ib_mute_sound);
		// videoBar.setMax(240000);
		if (null != mMediaPlayerDelegate)
			if (mMediaPlayerDelegate.isPlaying())
				play_pauseButton
						.setImageResource(R.drawable.play_btn_pause_big_detail_down);
			else {
				play_pauseButton
						.setImageResource(R.drawable.play_btn_play_big_detail);
			}
		if (currentVolume > 0) {
			isVolumeOpen = true;
			ibMuteSound.setImageResource(R.drawable.vedio_sound_open);
		} else {
			isVolumeOpen = false;
			ibMuteSound.setImageResource(R.drawable.vedio_sound_close);
		}
		
		ibStopPlayBtn = (ImageButton) findViewById(R.id.ib_stop_play_btn);
		if(ibStopPlayBtn!=null && ibStopPlayBtn.getVisibility()==View.GONE) {
			ibStopPlayBtn.setVisibility(View.VISIBLE);
		}
		initRetry();
		initEndPage();
		initLoadInfoPage();
	}

	private View endPageView;
	// private LinearLayout nextLayout;
	private LinearLayout replayLayout;

	/**
	 * 初始化播放完成的显示
	 */
	private void initEndPage() {
		if (null == mActivity)
			return;
		LayoutInflater mLayoutInflater = LayoutInflater.from(mActivity);
		if (null == mLayoutInflater)
			return;
		endPageView = mLayoutInflater.inflate(R.layout.yp_detail_play_end_page,
				null);
		if (null == endPageView)
			return;
		// nextLayout = (LinearLayout)
		// endPageView.findViewById(R.id.ll_next_play);
		replayLayout = (LinearLayout) endPageView.findViewById(R.id.ll_replay);

		// if (null != nextLayout)
		// nextLayout.setOnClickListener(new OnClickListener() {// 播放下一集
		//
		// @Override
		// public void onClick(View v) {
		// if (!Util.hasInternet())
		// return;
		// playNextVideo();
		// hideEndPage();
		// restartFromComplete();
		// }
		// });
		if (null != replayLayout)
			replayLayout.setOnClickListener(new OnClickListener() {// 重播

						@Override
						public void onClick(View v) {
							if (null != mMediaPlayerDelegate) {
								mMediaPlayerDelegate.release();
								mMediaPlayerDelegate.setFirstUnloaded();
								onVideoInfoGetted();
								mMediaPlayerDelegate.start();
								if (null != mMediaPlayerDelegate.videoInfo)
									mMediaPlayerDelegate.videoInfo
											.setProgress(0);
								mMediaPlayerDelegate.seekTo(0);
								hideEndPage();
								restartFromComplete();
							}
						}
					});
	}

	/**
	 * 初始化加载页面 只有加载进度条
	 */
	private void initLoadInfoPage() {
		if (null == mActivity)
			return;
		LayoutInflater mLayoutInflater = LayoutInflater.from(mActivity);
		if (null == mLayoutInflater)
			return;
		loadingInfoLayout = (RelativeLayout) mLayoutInflater.inflate(
				R.layout.yp_detail_loading_info_page, null);
		if (null == loadingInfoLayout)
			return;
		infoSeekBar = (SeekBar) loadingInfoLayout
				.findViewById(R.id.loading_info_seekbar);

	}

	private TextView playNameTextView;
	private SeekBar playLoadingBar;
	private TextView loadingTips;
	private String TAG = "PluginSmallScreenPlay";

	/**
	 * 初始化seek的loading界面
	 */
	private void initSeekLoading() {
		if (null == seekLoadingContainerView)
			return;
		playNameTextView = (TextView) seekLoadingContainerView
				.findViewById(R.id.detail_play_load_name);
		playLoadingBar = (SeekBar) seekLoadingContainerView
				.findViewById(R.id.loading_seekbar);
		loadingTips = (TextView) seekLoadingContainerView
				.findViewById(R.id.loading_tips);
		if (null != playLoadingBar)
			playLoadingBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {

						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							if (fromUser) {
								// Track.setTrackPlayLoading(false);
								return;
							} else {
								seekBar.setProgress(progress);
							}

						}
					});
	}

	private int seekcount = 0;

	public static final int SHOW_LOADING = 1111;
	public static final int HIDE_LOADING = 1112;

	/**
	 * 显示加载中
	 */
	public void showLoading() {
		Logger.e(TAG, "showLoading()");
		if (mMediaPlayerDelegate.isADShowing) {
			Logger.e(TAG, "mMediaPlayerDelegate.isADShowing()");
			return;
		}
		if (null != seekLoadingContainerView) {
			if (seekLoadingContainerView.getVisibility() == View.GONE) {
				if (null != seekLoadingContainerView)
					seekLoadingContainerView.setVisibility(View.VISIBLE);
				seekcount = 0;
				if (null != seekendHandler)
					seekHandler.sendEmptyMessageDelayed(SHOW_LOADING, 0);

			}
		}
		if (null != mMediaPlayerDelegate
				&& null != mMediaPlayerDelegate.videoInfo
				&& !DetailUtil.isEmpty(mMediaPlayerDelegate.videoInfo
						.getTitle()))
			if (null != playNameTextView)
				playNameTextView.setText(mMediaPlayerDelegate.videoInfo
						.getTitle());
		if (null != mMediaPlayerDelegate
				&& null != mMediaPlayerDelegate.videoInfo && firstLoaded) {
			if (null != loadingTips)
				loadingTips.setVisibility(View.GONE);
			if (null != playNameTextView)
				playNameTextView.setVisibility(View.GONE);
			if (null != seekLoadingContainerView && firstLoaded)
				seekLoadingContainerView.setBackgroundResource(0);
		} else {
			if (null != loadingTips) {
				loadingTips.setText(getResources().getString(
						R.string.player_tip_loading));
				loadingTips.setVisibility(View.VISIBLE);
			}
			if (null != playNameTextView)
				playNameTextView.setVisibility(View.VISIBLE);
			if (null != seekLoadingContainerView)
				seekLoadingContainerView
						.setBackgroundResource(R.drawable.bg_play);
		}

	}

	/**
	 * 隐藏加载
	 */
	public void hideLoading() {
		if (null == mActivity)
			return;
		((Activity) mActivity).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (null != seekLoadingContainerView) {
					seekLoadingContainerView.setVisibility(View.GONE);
					playLoadingBar.setProgress(0);
				}
				if (null != seekHandler)
					seekHandler.removeCallbacksAndMessages(null);
			}
		});
	}

	private Handler seekHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (null == seekHandler || null == playLoadingBar)
				return;
			if (msg.what == SHOW_LOADING) {
				if (seekcount >= 95) {
					seekcount = 0;
				}
				seekcount += 2;
				seekHandler.sendEmptyMessageDelayed(SHOW_LOADING, 100);

				playLoadingBar.setProgress(seekcount);
			} else {
				seekHandler.removeMessages(SHOW_LOADING);
				if (seekcount >= 90) {
					if (null != playLoadingBar)
						playLoadingBar.setProgress(seekcount);
					if (null != seekLoadingContainerView)
						seekLoadingContainerView.setVisibility(View.GONE);
					return;
				}
				seekcount += 10;
				if (null != playLoadingBar)
					playLoadingBar.setProgress(seekcount);
				seekHandler.sendEmptyMessageDelayed(HIDE_LOADING, 50);
			}

		}

	};
	private Handler seekendHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (seekcount < 100) {
				seekcount++;
				if (null != playLoadingBar)
					playLoadingBar.setProgress(seekcount);
				if (null != seekHandler)
					seekHandler.sendEmptyMessageDelayed(0, 10);
			}

		}

	};

	/**
	 * 显示加载的片名
	 */
	private void showLoadinfo() {
		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != interactFrameLayout) {
						interactFrameLayout.removeView(loadingInfoLayout);
						interactFrameLayout.addView(loadingInfoLayout);
					}
					if (null != loadInfoHandler)
						loadInfoHandler.sendEmptyMessage(0);
				}
			});

	}

	/**
	 * 隐藏加载的片名
	 */
	private void hideLoadinfo() {
		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != interactFrameLayout)
						interactFrameLayout.removeView(loadingInfoLayout);

					if (null != loadInfoHandler)
						loadInfoHandler.removeCallbacksAndMessages(null);
				}
			});

	}

	private int loadinfoseek = 0;
	private boolean loadinfoseekend = false;
	private Handler loadInfoHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (loadinfoseek == 0) {
				loadinfoseekend = false;
			}
			if (loadinfoseek == 100) {
				loadinfoseekend = true;
			}
			if (!loadinfoseekend)
				loadinfoseek++;
			else {
				loadinfoseek--;
			}
			if (null != infoSeekBar)
				infoSeekBar.setProgress(loadinfoseek);
			if (null != loadInfoHandler)
				loadInfoHandler.sendEmptyMessageDelayed(0, 50);
		}

	};
	private boolean autoPlay = true;

	/**
	 * 设置是否自动播放
	 * 
	 * @param autoplay
	 */
	public void setAutoPlay(boolean autoplay) {
		autoPlay = autoplay;
		if (null == userPlayButton)
			return;

		if (!autoplay) {
			if (null != userPlayButton)
				userPlayButton.setVisibility(View.VISIBLE);
			disableController();
			hideLoading();
		} else if (null != userPlayButton) {
			userPlayButton.setVisibility(View.GONE); // 自动播放 刚加载的页面中没有暂停播放按钮
		}
	}

	/**
	 * 设置title的高度
	 * 
	 * @return
	 */
	public int getTitleHeight() {
		if (null == playTitleTextView)
			return 0;
		LinearLayout.LayoutParams mParams = (LinearLayout.LayoutParams) playTitleTextView
				.getLayoutParams();
		if (null != mParams)
			return mParams.height;
		else
			return 0;
	}

	/**
	 * 搜索进度
	 * 
	 * @param seekBar
	 */
	protected void seekChange(SeekBar seekBar) {
		if (null == mMediaPlayerDelegate)
			return;
		if (null != seekBar && seekBar.getProgress() == seekBar.getMax()
				&& seekBar.getMax() > 0) {
			if (null != mMediaPlayerDelegate.videoInfo)
				mMediaPlayerDelegate.videoInfo
						.setProgress(mMediaPlayerDelegate.videoInfo
								.getDurationMills());
			// complete = true;
			mMediaPlayerDelegate.onComplete();

			// if (null != playHandler) {
			// playHandler.removeCallbacksAndMessages(null);
			// playHandler.sendEmptyMessage(MSG_COMPLETE);
			// }
		} else if (mMediaPlayerDelegate != null) {
			if (null != mMediaPlayerDelegate.videoInfo)
				mMediaPlayerDelegate.videoInfo.setProgress(seekBar
						.getProgress());
			// 搜索进度结结束的时候 如果是暂停的话 开始播放
			if (!mMediaPlayerDelegate.isPlaying()) {
				startPlay();
			}
			mMediaPlayerDelegate.seekTo(seekBar.getProgress()); // 到指定的地点播放
			isLoading = true;
			videoBar.setEnabled(false); // 拖动完成后不能立即再拖动进度条
			Logger.e("PlayFlow", "小播放器拖动seekto" + seekBar.getProgress());
		}
	}

	/**
	 * 开始播放
	 */
	private void startPlay() {
		if (null == mMediaPlayerDelegate)
			return;
		// 如果有广告就播放广告页面
		if (mMediaPlayerDelegate.isADShowing) {
			mBasePlayerManager.startPlay();
		} else {
			mMediaPlayerDelegate.start();
			if (null != play_pauseButton)
				play_pauseButton
						.setImageResource(R.drawable.play_btn_pause_big_detail_down);

		}
	}

	/** 用户上次与控制界面交互的时间。 */
	protected long lastInteractTime = 0;
	// seekbar
	OnSeekBarChangeListener mBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			if (Util.hasInternet())
				if (fromUser) {
					Logger.d(TAG, "onProgressChanged: " + progress);
					seekBar.setProgress(progress);
					// TODO currentTime
//					currentTime.setText(PlayerUtil.getFormatTime(progress)
//							+ "/");
					currentTime.setText(getTime(progress)+"/");
				}
			changePlayPause();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Logger.d(TAG, "onStartTrackingTouch");
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Logger.d(TAG, "onStopTrackingTouch");
			seekChange(seekBar);

		}

	};

	private void changePlayPause() {
		if (null == play_pauseButton || null == mMediaPlayerDelegate)
			return;
		if (isLoading) {
			return;
		}
		if (mMediaPlayerDelegate.isPlaying())
			play_pauseButton
					.setImageResource(R.drawable.play_btn_pause_big_detail_down);
		else {
			play_pauseButton
					.setImageResource(R.drawable.play_btn_play_big_detail);
		}
	}

	// 点击初次加载页面的播放按钮 开始播放 并且播放按钮消失
	private OnClickListener userPlayClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			enableController();
			if (null != mMediaPlayerDelegate) {
				hideRetryLayout(); // 隐藏重试布局；
				mMediaPlayerDelegate.release();
				mMediaPlayerDelegate.setFirstUnloaded();
				onVideoInfoGetted();
				mMediaPlayerDelegate.playVideo(mMediaPlayerDelegate.nowVid);
				mMediaPlayerDelegate.start();
				if (null != mMediaPlayerDelegate.videoInfo)
					mMediaPlayerDelegate.videoInfo.setProgress(0);
				mMediaPlayerDelegate.seekTo(0);
				hideEndPage();
				restartFromComplete();
			}
			if (null != userPlayButton && userPlayButton.getVisibility()==View.VISIBLE) {
				userPlayButton.setVisibility(View.GONE);
			}
		}
	};

	// 隐藏或者显示控制界面
	private void hideShowControl() {
		if (null == controlLayout || null == play_pauseButton)
			return;

		if (controlLayout.getVisibility() == View.VISIBLE) {
			controlLayout.startAnimation(mAnimSlideOutBottom); //底部滑出
	//		controlLayout.setVisibility(View.GONE);
	//		play_pauseButton.setVisibility(View.GONE);
		} else {
			//全屏  锁屏点击 controlLayout无效
			if(mMediaPlayerDelegate!=null && mMediaPlayerDelegate.isFullScreen &&
					isLockScreen){
				return;
			}
			// Logger.d("sgh","plugin_small, from local: " +
			// mMediaPlayerDelegate.videoInfo.getPlayType());
			if (mMediaPlayerDelegate.videoInfo != null
					&& StaticsUtil.PLAY_TYPE_LOCAL
							.equals(mMediaPlayerDelegate.videoInfo
									.getPlayType())) {
				full_screenButton.setVisibility(View.INVISIBLE); // 播放本地视频没有全频按钮
			} else {
				full_screenButton.setVisibility(View.VISIBLE); // 播放在线视频有全频按钮
			}
			if(!isLockScreen) {
				controlLayout.startAnimation(mAnimSlideInBottom); //底部滑入
				controlLayout.setVisibility(View.VISIBLE);
			}
	//		play_pauseButton.setVisibility(View.VISIBLE);
			if (isLoading) {
				play_pauseButton
						.setImageResource(R.drawable.play_btn_pause_big_detail_down);
				return;
			}
			if (null != mMediaPlayerDelegate
					&& mMediaPlayerDelegate.isPlaying())
				play_pauseButton
						.setImageResource(R.drawable.play_btn_pause_big_detail_down);
			else {
				play_pauseButton
						.setImageResource(R.drawable.play_btn_play_big_detail);
			}
		}
	}

	// 点击整个播放页面 来控制标题和播放控制界面的显示和隐藏
	public  void onContainerClick() {
		if (null == controlLayout || null == titleLayoutPort || !isRealVideoStart || isLockScreen)
			return;
		if (null != hideHandler)
			hideHandler.removeCallbacksAndMessages(null);
		if(mMediaPlayerDelegate!=null && mMediaPlayerDelegate.isFullScreen) {
			if (controlLayout.getVisibility() == View.VISIBLE
					&& titleLayoutPort.getVisibility() == View.INVISIBLE) {
				controlLayout.setVisibility(View.GONE); // 控制界面可见 变成不可见
				return;
			}
			// 控制界面不可见 标题可见
			if (controlLayout.getVisibility() == View.GONE
					&& titleLayoutPort.getVisibility() == View.VISIBLE) {
				hideTitle();
				return;
			}
			showHideLockBtn();
			showHideTitle();
		//	stopVideoBtnShow();
		}
		hideShowControl();
//		showHideVolume();
		userAction();
		return;
	}

    private void showHideLockBtn() {
    	if (null == ibVideoLockScreen || controlLayout==null)
			return;
		if (ibVideoLockScreen.getVisibility() == View.VISIBLE )
			hideLockBtn();
		else {
			showLockBtn();
		}
	}

	private void showLockBtn() {
		if (null != ibVideoLockScreen && mMediaPlayerDelegate.isFullScreen)
			ibVideoLockScreen.setVisibility(View.VISIBLE);
		
	}

	private void hideLockBtn() {
		if (null != ibVideoLockScreen && !isLockScreen && controlLayout.getVisibility()==View.VISIBLE)
			ibVideoLockScreen.setVisibility(View.INVISIBLE);
		
	}
	private void showHideVolume() {
		if (null == llVolumeUpDown || (mMediaPlayerDelegate!=null && !mMediaPlayerDelegate.isFullScreen))
			return;
		if (llVolumeUpDown.getVisibility() == View.VISIBLE)
			hideVolume();
		else {
			showVolume();
		}
	}

	private void showVolume() {
		if (null != llVolumeUpDown && mMediaPlayerDelegate.isFullScreen && ! isLockScreen){
			llVolumeUpDown.setVisibility(View.VISIBLE);
		}
		hideVolumeAfterMillis();   //2秒之后音量键消失
	}

	private void hideVolume() {
		if (null != llVolumeUpDown && llVolumeUpDown.getVisibility()==View.VISIBLE)
			llVolumeUpDown.setVisibility(View.INVISIBLE);
	}

	private void showHideTitle() {
		if (null == titleLayoutPort)
			return;
		if (titleLayoutPort.getVisibility() == View.VISIBLE)
			hideTitle();
		else {
			showTitle();
		}

	}

	/**
	 * 竖屏的时候显示此按钮
	 */
	private void stopVideoBtnShow() {
		if (null != ibStopPlayBtn) {
			ibStopPlayBtn.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 竖屏的时候不显示此按钮
	 */
	private void stopVideoBtnHide() {
		if (null != ibStopPlayBtn) {
			ibStopPlayBtn.setVisibility(View.GONE);
		}
	}
	/**
	 * 竖屏标题不可见
	 */
	private void hideTitle() {
		if (null != titleLayoutPort && titleLayoutPort.getVisibility()==View.VISIBLE &&
		   mMediaPlayerDelegate!=null && mMediaPlayerDelegate.isFullScreen){
			titleLayoutPort.startAnimation(mAnimSlideOutTop);
		}
		//
	}

	/**
	 * 全屏才显示标题
	 */
	private void showTitle() {
		if (null != titleLayoutPort && mMediaPlayerDelegate.isFullScreen && !isLockScreen) {
			titleLayoutPort.startAnimation(mAnimSlideInTop);
			titleLayoutPort.setVisibility(View.VISIBLE);
		}
	}

	public  void hideControl() {
		if (null != controlLayout) {
			controlLayout.startAnimation(mAnimSlideOutBottom);
		}
	}
   public void hideAll(){
	   hideLockBtn();
	   if (null != controlLayout && controlLayout.getVisibility()==View.VISIBLE) {
		   controlLayout.clearAnimation();
		   controlLayout.setVisibility(View.GONE);
	   }
	   hideVolume();
	   if (null != titleLayoutPort && titleLayoutPort.getVisibility()==View.VISIBLE){
		   titleLayoutPort.clearAnimation();
		   titleLayoutPort.setVisibility(View.INVISIBLE);
	   }
   }
	private void showControl() {
		if (null != controlLayout )
			controlLayout.setVisibility(View.VISIBLE);
		if (null != mMediaPlayerDelegate && mMediaPlayerDelegate.isPlaying()) {
			play_pauseButton
					.setImageResource(R.drawable.play_btn_pause_big_detail_down);
		} else {
			play_pauseButton.setVisibility(View.VISIBLE);
			play_pauseButton
					.setImageResource(R.drawable.play_btn_play_big_detail);
		}
	}

	private   Handler hideHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HIDE_CONTROL: {
				if (null != controlLayout) {
					controlLayout.setVisibility(View.GONE);
				}
//				if (null != play_pauseButton)
//					play_pauseButton.setVisibility(View.GONE);

				break;
			}
			case HIDE_TITLE: {
				hideTitle();
				break;
			}
			case HIDE_ALL: {
				if(mMediaPlayerDelegate!=null && mMediaPlayerDelegate.isFullScreen) {
			    	hideTitle();
					hideLockBtn();
					hideVolume();
				}
				hideControl();
			}
			break;
			case HIDE_VOLUME:
				hideVolume();
				break;
			default:
				break;
			}
		}


	};

	/**
	 * 用户操作后，延迟隐藏
	 * 
	 */
	private final int HIDE_CONTROL = 1001;
	private final int HIDE_TITLE = 1002;
	private final int HIDE_ALL = 1003;
	private final int HIDE_VOLUME=1004;

	// 用户操作 5秒钟之后自动隐藏标题和播放控制界面
	protected void userAction() {
		if (hideHandler != null) {
			hideHandler.removeCallbacksAndMessages(null);
			hideHandler.sendEmptyMessageDelayed(HIDE_ALL, 5000);
		}
	}
	protected void hideVolumeAfterMillis() {
		if (hideHandler != null) {
			hideHandler.sendEmptyMessageDelayed(HIDE_VOLUME, 2000);
		}
	}
	public void setVideoImage(ImageResizer maker, String imageurl) {

	}
	protected int selectedFormat = Profile.FORMAT_FLV_HD;

	/**
	 * 分享
	 * */
	public void share() {

	}

	public void clearPlayState() {
		if (null == mActivity)
			return;
		((Activity) mActivity).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				disableControllerHide(); // 控制界面不可点击
				if (null != playTitleTextView)
					playTitleTextView.setText(""); // 标题为空
				if (null != videoBar) {
					{
						videoBar.setProgress(0);
						videoBar.setMax(0);
					}

				}
			}

		});

	}

	int Adaptation_lastPercent = 0;

	@Override
	public void onBufferingUpdateListener(final int percent) {
		if (null == mActivity)
			return;
		((Activity) mActivity).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (percent == 100) {
					videoBar.setSecondaryProgress(videoBar.getMax());
				}
				if (percent == 100 && Adaptation_lastPercent != 100) {
					Adaptation_lastPercent = percent;
					return;
				}
				// hideRetryLayout();
				if (null == mMediaPlayerDelegate
						|| null == mMediaPlayerDelegate.videoInfo)
					return;
				int showSecond = (percent * mMediaPlayerDelegate.videoInfo
						.getDurationMills()) / 100;
				if (null != videoBar)
					videoBar.setSecondaryProgress(showSecond);
			}
		});
	}

	@Override
	public void onCompletionListener() {
		if (null == mMediaPlayerDelegate || error)
			return;
		if (!pluginEnable) {
			return;
		}
		Logger.e("interactplugin", "playComplete");
		((Activity) mActivity).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (null != videoBar)
					videoBar.setProgress(0);

				hideLoading();
				if (null != mMediaPlayerDelegate) {
					// mMediaPlayerDelegate.onEnd();
					// mMediaPlayerDelegate.setOrientionDisable();
				}
				isLockScreen=false;
				if(ibVideoLockScreen!=null){
					ibVideoLockScreen.setVisibility(View.GONE);
				}
				disableController();
				playComplete();
			}
		});
		// if (null != playHandler) {
		// playHandler.removeCallbacksAndMessages(null);
		// playHandler.sendEmptyMessageDelayed(MSG_COMPLETE, 200);
		// }

	}

	private boolean error;

	@Override
	public boolean onErrorListener(int what, int extra) {
		Logger.e(TAG, "播放错误 onErrorListener-->" + what);
		// mMediaPlayerDelegate.mediaState = STATE.ERROR;
		error = true;
		if (null != mActivity && mActivity.isFinishing()) {
			return true;
		}
		mMediaPlayerDelegate.release();

		if (null != mMediaPlayerDelegate) {
			Logger.e(TAG, "播放错误 onErrorListener--> #0");
			if (mMediaPlayerDelegate.isFullScreen) {
				showAlert();
				return false;
			}

			Logger.e(TAG, "播放错误 onErrorListener--> #1");
			mMediaPlayerDelegate.isStartPlay = false;
			if (mMediaPlayerDelegate.isADShowing) {
				showAlert();
				return true;
			}
			if (what == MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR) {
				showAlert();
				return true;
			}
			if (what == MPPErrorCode.MEDIA_INFO_PLAYERROR) {
				showAlert();
				return true;
			}
			if (what == MPPErrorCode.MEDIA_INFO_SEEK_ERROR) {
				showAlert();
				return true;
			}
			if (what == MPPErrorCode.MEDIA_INFO_PREPARE_TIMEOUT_ERROR) {
				// Util.showTips(HttpRequestManager.STATE_ERROR_TIMEOUT);
				showAlert();
				return true;
			}
			if (what == MPPErrorCode.MEDIA_INFO_SEEK_ERROR
					&& mMediaPlayerDelegate.currentOriention == Orientation.VERTICAL) {
				playComplete();
				return true;
			}
			if (null != mMediaPlayerDelegate.videoInfo
					&& StaticsUtil.PLAY_TYPE_LOCAL
							.equals(mMediaPlayerDelegate.videoInfo.playType)) {
				if (what == MPPErrorCode.MEDIA_INFO_NETWORK_DISSCONNECTED) {
					playComplete();
				} else if (what == MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR) {
					PlayerUtil.showTips("本地文件已损坏");
					Track.onError(mActivity,
							mMediaPlayerDelegate.videoInfo.getVid(),
							Profile.GUID,
							mMediaPlayerDelegate.videoInfo.playType,
							PlayCode.VIDEO_NOT_EXIST,
							mMediaPlayerDelegate.videoInfo.mSource,
							mMediaPlayerDelegate.videoInfo.getCurrentQuality(),
							mMediaPlayerDelegate.videoInfo.getProgress(),
							mMediaPlayerDelegate.isFullScreen);
				} else if (what == MPPErrorCode.MEDIA_INFO_PREPARE_ERROR) {
					// Util.showTips("播放器内部出错");
					mMediaPlayerDelegate.finishActivity();
				} else if (what == MPPErrorCode.MEDIA_INFO_NETWORK_ERROR) {
					playComplete();
					return true;
				} else if (what == MPPErrorCode.MEDIA_INFO_SEEK_ERROR) {
					playComplete();
					return true;
				} else {
					// Util.showTips("本地文件已损坏");
				}
				mMediaPlayerDelegate.setFirstUnloaded();
				mMediaPlayerDelegate.release();
				mMediaPlayerDelegate.finishActivity();
				return true;
			}
			if (null != mMediaPlayerDelegate.videoInfo
					&& StaticsUtil.PLAY_TYPE_NET
							.equals(mMediaPlayerDelegate.videoInfo.playType)) {
				if (what == MPPErrorCode.MEDIA_INFO_NETWORK_DISSCONNECTED) {
					// if (Util.hasInternet())
					PlayerUtil.showTips(R.string.tips_not_responding);
				} else if (what == MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR) {
					// if (Util.hasInternet())
					PlayerUtil.showTips(R.string.tips_not_responding);
				} else if (what == MPPErrorCode.MEDIA_INFO_PREPARE_TIMEOUT_ERROR) {
					// if (Util.hasInternet())
					PlayerUtil.showTips(R.string.tips_not_responding);
				}
			}
		}
		showAlert();

		return true;
	}

	public void showAlert() {
		Logger.e(TAG, "showAlert()--> #0");
		if (null != mMediaPlayerDelegate
				&& null != mMediaPlayerDelegate.videoInfo
				&& mMediaPlayerDelegate.videoInfo.getPlayType() == StaticsUtil.PLAY_TYPE_LOCAL) {
			Logger.e(TAG, "showAlert()--> #1");
			PlayerUtil.showTips(R.string.player_error_native);
			alertRetry(mActivity, R.string.player_error_native);
		} else {
			alertRetry(mActivity, R.string.Player_error_timeout);
		}
	}

	public void alertRetry(final Activity c, final int msgId) {
		if (c.isFinishing())
			return;

		c.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// hideLoading();
				// hideLoadinfo();
				if (null != mMediaPlayerDelegate)
					mMediaPlayerDelegate.release();
				((Activity) mActivity).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						disableController();
						// TODO 不要重播這個頁面
						// showRetryLayout();
					}
				});
				if (null != mMediaPlayerDelegate
						&& !mMediaPlayerDelegate.isFullScreen) {
					mMediaPlayerDelegate.isStartPlay = false;
					if (null != mMediaPlayerDelegate.videoInfo
							&& Orientation.VERTICAL
									.equals(mMediaPlayerDelegate.currentOriention))
						mMediaPlayerDelegate.onVVEnd();
				}
			}
		});
	}

	@Override
	public void OnPreparedListener() {
		Logger.e(TAG, " OnPreparedListener()");
		seekcount = 0;
		if (null != retryView)
			retryView.setVisibility(View.GONE);
	}

	/**
	 * seek 完成的时候seekBar才能再次拖动
	 */
	@Override
	public void OnSeekCompleteListener() {
		isLoading = false;
		if (videoBar != null) {
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					videoBar.setEnabled(true);
				}
			});
		}
	}

	@Override
	public void OnVideoSizeChangedListener(int width, int height) {

	}

	@Override
	public void OnTimeoutListener() {
		if (null == mActivity)
			return;
		((Activity) mActivity).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableController();
				showRetryLayout();
			}
		});
		Logger.e(TAG, " OnTimeoutListener()");
		// if (null != playHandler) {
		// playHandler.removeCallbacksAndMessages(null);
		// playHandler.sendEmptyMessage(MSG_TIME_OUT);
		// }

	}

	private boolean firstLoaded = false;
	private boolean isLoading = false;

	@Override
	public void onLoadedListener() {
		Logger.e(TAG, " onLoadedListener()");
		// if (mMediaPlayerDelegate.mediaState == STATE.COMPLETE
		// || mMediaPlayerDelegate.mediaState == STATE.ERROR)
		// return;
		isLoading = false;
		if (null == mMediaPlayerDelegate)
			return;
		if (mMediaPlayerDelegate.isComplete)
			return;
		if (!firstLoaded) {
			// 这个在onrealVideoStart的时候seek过一次了
			// mMediaPlayerDelegate.seekToHistory();
			firstLoaded = true;
		}
		error = false;
		if (null != seekHandler)
			seekHandler.removeCallbacksAndMessages(null);
		if (null != seekendHandler)
			seekendHandler.sendEmptyMessage(0);
		if (null != mActivity) {
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideLoading();
					hideLoadinfo();
					hideRetryLayout();
				}
			});
		}
		if (null != mActivity && ((Activity) mActivity).isFinishing()) {
			hideLoading();
			hideRetryLayout();
		}
		/*
		 * if (null != playHandler) {
		 * playHandler.removeCallbacksAndMessages(null);
		 * playHandler.sendEmptyMessageDelayed(MSG_LOADED, 500); }
		 */

	}

	/** 显示重试 onerror/timeout/get play data failed */
	private void showRetryLayout() {
		hideEndPage();
		if (null != mActivity) {
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != retryView)
						retryView.setVisibility(View.VISIBLE);
				}
			});
		}

	}

	/** 隐藏重试 */
	private void hideRetryLayout() {
		if (null != mActivity) {
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != retryView)
						retryView.setVisibility(View.GONE);
				}
			});
		}

	}

	@Override
	public void onLoadingListener() {
		Logger.e(TAG, "onLoadingListener");
		isLoading = true;
		if (error) {
			Logger.e(TAG, "null == error ");
			return;
		}

		if (null == mMediaPlayerDelegate || mMediaPlayerDelegate.isComplete
				|| mMediaPlayerDelegate.isReleased) {
			Logger.e(TAG, "null == mMediaPlayerDelegate ");
			return;
		}
		if (!autoPlay) {
			Logger.e(TAG, "!autoPlay");
			return;
		}

		if (null != mActivity) {
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideEndPage();
					showLoading();
					hideRetryLayout();
				}
			});
		}

	}

	/**
	 * replay/retry/playnext
	 * */
	private void restartFromComplete() {
		if (null != mMediaPlayerDelegate) {
			mMediaPlayerDelegate.clearEnd();
			mMediaPlayerDelegate.setOrientionDisable();
		}
	}

	protected void playComplete() {
		Logger.e(TAG, "playComplete()");
		if (null == mMediaPlayerDelegate)
			return;
		if (!pluginEnable) {
			return;
		}
		clearPlayState();
		if (null != mMediaPlayerDelegate) {
			// mMediaPlayerDelegate.onEnd();
			// mMediaPlayerDelegate.setOrientionDisable();
		}
		if (Profile.from == Profile.PHONE_BROWSER
				|| Profile.from == Profile.PAD_BROWSER) {
			mMediaPlayerDelegate.finishActivity();
			return;
		}
		mMediaPlayerDelegate.isStartPlay = false;
		Track.setplayCompleted(true);
		mMediaPlayerDelegate.isComplete = true;

		if (null != mMediaPlayerDelegate.videoInfo
				&& mMediaPlayerDelegate.videoInfo.getPlayType() != StaticsUtil.PLAY_TYPE_LOCAL) {
			goEndPage();
		} else {
			mMediaPlayerDelegate.finishActivity();
		}
	}

	// private static final int MSG_LOADING = 20131;
	// private static final int MSG_LOADED = 20132;
	// private static final int MSG_BUFFER_UPDATE = 20133;
	// private static final int MSG_ERROR = 20134;
	// private static final int MSG_TIME_OUT = 20135;
	// private static final int MSG_COMPLETE = 20136;
	// private static final int MSG_INFO_GETTING = 20137;
	private static final int MSG_INFO_GETTED = 20138;
	private static final int MSG_INFO_FAILED = 20139;
	// 处理播放相关消息
	private Handler playHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*
			 * case MSG_LOADING: { if (!autoPlay && null != mMediaPlayerDelegate
			 * && !mMediaPlayerDelegate.changeAutoPlay) return; ((Activity)
			 * mActivity).runOnUiThread(new Runnable() {
			 * 
			 * @Override public void run() { showLoading(); } });
			 * 
			 * break; }
			 */

			/*
			 * case MSG_LOADED: { ((Activity) mActivity).runOnUiThread(new
			 * Runnable() {
			 * 
			 * @Override public void run() { hideLoading(); enableController();
			 * hideRetryLayout(); if (count == 0) if (null !=
			 * mMediaPlayerDelegate && null != mMediaPlayerDelegate.videoInfo) {
			 * videoBar.setMax(mMediaPlayerDelegate.videoInfo
			 * .getDurationMills()); count++; if (null != playTitleTextView)
			 * playTitleTextView .setText(mMediaPlayerDelegate.videoInfo
			 * .getTitle()); } } });
			 * 
			 * break; }
			 */
			// case MSG_BUFFER_UPDATE: {
			// break;
			// }
			/*
			 * case MSG_ERROR: { ((Activity) mActivity).runOnUiThread(new
			 * Runnable() {
			 * 
			 * @Override public void run() { disableController();
			 * showRetryLayout(); } }); break; }
			 */
			/*
			 * case MSG_TIME_OUT: { ((Activity) mActivity).runOnUiThread(new
			 * Runnable() {
			 * 
			 * @Override public void run() { disableController();
			 * showRetryLayout(); } }); break; }
			 */
			/*
			 * case MSG_COMPLETE: ((Activity) mActivity).runOnUiThread(new
			 * Runnable() {
			 * 
			 * @Override public void run() { if (null != videoBar)
			 * videoBar.setProgress(0); hideLoading(); playComplete(); if (null
			 * != mMediaPlayerDelegate) mMediaPlayerDelegate.onEnd();
			 * disableController(); } }); break;
			 */
			// case MSG_INFO_GETTING: {
			// break;
			// }
			// case MSG_INFO_GETTED: {
			// int i = 0;
			//
			// break;
			// }
			// case MSG_INFO_FAILED: {
			//
			// break;
			// }
			default:
				break;
			}
		}

	};

	// public void hidePlayLoading() {
	// if (null != playLoading)
	// playLoading.setVisibility(View.GONE);
	// }

	/**
	 * 播放下一集
	 */
	private void playNextVideo() {
		restartFromComplete();
		clearPlayState();
		firstLoaded = false;
		isRealVideoStart = false;
		if (null == mMediaPlayerDelegate
				|| null == mMediaPlayerDelegate.videoInfo)
			return;
		if (!Util.hasInternet()) {
			playLocalNext();
			return;
		}

		// 本地播放询问是否在线
		/*
		 * if ( StaticsUtil.PLAY_TYPE_LOCAL
		 * .equals(mMediaPlayerDelegate.videoInfo.getPlayType())) { IDownload
		 * download = YoukuService.getService(IDownload.class); DownloadInfo
		 * info = download.getDownloadInfo(
		 * mMediaPlayerDelegate.videoInfo.getShowId(),
		 * mMediaPlayerDelegate.videoInfo.getShow_videoseq() + 1); if (info !=
		 * null) { mMediaPlayerDelegate.playVideo(info.videoid,
		 * StaticsUtil.PLAY_TYPE_LOCAL .equals(mMediaPlayerDelegate.videoInfo
		 * .getPlayType())); return; } if (info == null) {
		 * Util.showTips(R.string.download_no_network); //
		 * mMediaPlayerDelegate.finishActivity(); return; }
		 * mMediaPlayerDelegate.playVideo(info.videoid); return; }
		 */

		if (mMediaPlayerDelegate.videoInfo.getHaveNext() == 0) {
			goEndPage();
			return;
		}
		mMediaPlayerDelegate
				.playVideo(mMediaPlayerDelegate.videoInfo.nextVideoId);
	}

	/**
	 * 播放本地下一集
	 */
	private void playLocalNext() {
		if (null == mMediaPlayerDelegate
				|| null == mMediaPlayerDelegate.videoInfo)
			return;
		ICacheInfo download = IMediaPlayerDelegate.mICacheInfo;
		VideoCacheInfo info = download
				.getNextDownloadInfo(mMediaPlayerDelegate.videoInfo.getVid());
		if (info == null) {
			mMediaPlayerDelegate.finishActivity();
			return;
		}
		firstLoaded = false;
		isRealVideoStart = false;
		mMediaPlayerDelegate.playVideo(info.videoid,
				StaticsUtil.PLAY_TYPE_LOCAL
						.equals(mMediaPlayerDelegate.videoInfo.getPlayType()));
	}

	// 只有重播页
	private void goReplayPage() {
		Logger.e(TAG, "goReplayPage");
		firstLoaded = false;
		isRealVideoStart = false;
		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (null != interactFrameLayout && null != endPageView) {
						interactFrameLayout.removeView(endPageView);
						if (mMediaPlayerDelegate == null) {
							return;
						}
						if (mMediaPlayerDelegate.isFullScreen) {
							mMediaPlayerDelegate.goSmall();
						}
							hideTitle();
						   if(userPlayButton.getVisibility()==View.GONE)
							userPlayButton.setVisibility(View.VISIBLE);
//						interactFrameLayout.addView(endPageView);
						
						// LinearLayout nextLayout = (LinearLayout) endPageView
						// .findViewById(R.id.ll_next_play);
						// if (null != nextLayout)
						// nextLayout.setVisibility(View.GONE);
					}

				}
			});

	}

	private void hideEndPage() {
		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (null != interactFrameLayout && null != endPageView) {
						interactFrameLayout.removeView(endPageView);
						// LinearLayout nextLayout = (LinearLayout) endPageView
						// .findViewById(R.id.ll_next_play);
						// if (null != nextLayout)
						// nextLayout.setVisibility(View.VISIBLE);
					}

				}
			});

	}

	// 重播+下一集
	private void goReplayNextPage() {
		firstLoaded = false;
		isRealVideoStart = false;
		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (null != interactFrameLayout && null != endPageView) {
						interactFrameLayout.removeView(endPageView);
						interactFrameLayout.addView(endPageView);
					}
				}
			});

	}

	/**
	 * 去播放结束页
	 */
	private void goEndPage() {
		firstLoaded = false;
		isRealVideoStart = false;
		hideLoadinfo(); // 隐藏只有进度条的加载页面
		hideLoading(); // 设置加载进度为0
		hideRetryLayout();
		if (null != mMediaPlayerDelegate
				&& null != mMediaPlayerDelegate.videoInfo) {
			mMediaPlayerDelegate.release();
			mMediaPlayerDelegate.setFirstUnloaded();
			if (mMediaPlayerDelegate.videoInfo.getHaveNext() == 1) {
				 goReplayNextPage();
			} else {
				 goReplayPage();
			}
		}
	}

	protected void onCurrentPostionUpdate(int currentPostion) {
		// Logger.e(tag, "onCurrentPostionUpdate"+currentPostion);
		enableController();
		if (null != userPlayButton) {
			userPlayButton.setVisibility(View.GONE);
		}
		if (null == mMediaPlayerDelegate
				|| mMediaPlayerDelegate.videoInfo == null
				|| mMediaPlayerDelegate.isADShowing
				|| mMediaPlayerDelegate.isReleased)
			return;
		if (Profile.isSkipHeadAndTail() && !mMediaPlayerDelegate.isFullScreen) {
			if (mMediaPlayerDelegate.videoInfo.isHasHead()) {
				int headPosition = mMediaPlayerDelegate.videoInfo
						.getHeadPosition();
				if (currentPostion < headPosition - 15000) {
					// Util.showTips("为您跳过片头");
					if (null != videoBar)
						videoBar.setProgress(headPosition);
					mMediaPlayerDelegate.videoInfo.setProgress(headPosition);
					mMediaPlayerDelegate.seekTo(headPosition);
					return;
				}
			}
			if (mMediaPlayerDelegate.videoInfo.isHasTail()) {
				int tailPosition = mMediaPlayerDelegate.videoInfo
						.getTailPosition();
				if ((tailPosition - currentPostion) <= 2000) {
					// Util.showTips("为您跳过片尾");
					mMediaPlayerDelegate.videoInfo
							.setProgress(tailPosition - 5000);
					playComplete();
					return;
				}
			}
		}
		// TODO currentTime
	//	currentTime.setText(PlayerUtil.getFormatTime(currentPostion) + "/");
		currentTime.setText(getTime(currentPostion)+"/");
		// mMediaPlayerDelegate.mediaState = STATE.PLAYING;
		if (null != videoBar)
			videoBar.setProgress(currentPostion);
		mMediaPlayerDelegate.videoInfo.setProgress(currentPostion);
	}

	@Override
	public void OnCurrentPositionChangeListener(int currentPosition) {
		if (null != videoBar)
			videoBar.setProgress(currentPosition);
		/* 片头片尾 */
		onCurrentPostionUpdate(currentPosition);

	}

	private Drawable playDrawable;

	public void setPlayImg(Drawable mDrawable) {
		playDrawable = mDrawable;

	}

	@Override
	public void newVideo() {

	}

	@Override
	public void onVolumnUp() {
		if (null != mActivity) {

			currentVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			isVolumeOpen = true;
			ibMuteSound.setImageResource(R.drawable.vedio_sound_open);
			if (null != mMediaPlayerDelegate
					&& mMediaPlayerDelegate.isFullScreen) {
				audioManager.adjustVolume(AudioManager.ADJUST_SAME,
						AudioManager.FLAG_PLAY_SOUND);
			} else {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_SAME,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
		}
	}

	@Override
	public void onVolumnDown() {
		if (null != mActivity) {
			currentVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (currentVolume <= 1) {
				isVolumeOpen = false;
				ibMuteSound.setImageResource(R.drawable.vedio_sound_close);
			}
			if (null != mMediaPlayerDelegate
					&& mMediaPlayerDelegate.isFullScreen) {
				audioManager.adjustVolume(AudioManager.ADJUST_SAME,
						AudioManager.FLAG_PLAY_SOUND);
			} else {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_SAME,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
		}
	}

	@Override
	public void onMute(boolean mute) {
	}

	@Override
	public void onVideoChange() {
		firstLoaded = false;
	}

	/**
	 * 设置viwe是否可点击
	 * 
	 * @param state
	 * @param view
	 */
	private void setClickable(boolean state, View view) {
		if (null == view)
			return;
		view.setClickable(state);
	}

	/**
	 * 禁用控制并隐藏
	 */
	private void disableController() {
		if (null != controlLayout) {
			controlLayout.setVisibility(View.GONE);
		}
		setClickable(false, play_pauseButton);
		setClickable(false, videoBar);
		setClickable(false, mContainerLayout);
		setClickable(false, full_screenButton);
		setClickable(false, videoBar);
		hideTitle();
	}

	/**
	 * 禁用控制
	 */
	private void disableControllerHide() {
		setClickable(false, play_pauseButton);
		setClickable(false, videoBar);
		setClickable(false, mContainerLayout);
		setClickable(false, full_screenButton);
		setClickable(false, videoBar);
	}

	/**
	 * 启用控制
	 */
	private void enableController() {
		setClickable(true, play_pauseButton);
		setClickable(true, videoBar);
		setClickable(true, mContainerLayout);
		setClickable(true, full_screenButton);
		setClickable(true, videoBar);
	}

	/**
   * 
   */
	@Override
	public void onVideoInfoGetting() {
		hideRetryLayout(); // 隐藏重试布局

		playANewVideo();
		Logger.e("interactplugin", "onVideoInfoGetting");
		initSeekLoading();
		// showLoadinfo();
		showLoadinfo(); // 显示加载动画
		disableController();
	}

	private void playANewVideo() {
		firstLoaded = false;
		isRealVideoStart = false;
		if (null != userPlayButton) {
			userPlayButton.setVisibility(View.GONE);
		}
		clearPlayState();
		hideEndPage();
		disableController();
		restartFromComplete();
	}

	@Override
	public void onVideoInfoGetted() {
		firstLoaded = false;
		isRealVideoStart = false;
		Logger.e("interactplugin", "onVideoInfoGetted");
		hideLoadinfo();
		// showLoading();
		if (null != mMediaPlayerDelegate
				&& mMediaPlayerDelegate.videoInfo != null)
			video_id = mMediaPlayerDelegate.videoInfo.getVid();
		infoFail = false;
		disableController();
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (null != mMediaPlayerDelegate
						&& null != mMediaPlayerDelegate.videoInfo) {
					if (null != videoBar) {
						int duration = mMediaPlayerDelegate.videoInfo
								.getDurationMills();
						videoBar.setMax(duration);// 设置播放进度条
		//				totalTime.setText(PlayerUtil.getFormatTime(duration));// 设置播放总时间
						totalTime.setText(getTime(duration));
					}
					if (null != playTitleTextView)
						playTitleTextView
								.setText(mMediaPlayerDelegate.videoInfo
										.getTitle());// 设置播放标题
				}
			}
		});
	}

	@Override
	public void onVideoInfoGetFail(boolean needRetry) {
		Logger.e("interactplugin", "onVideoInfoGetFail");
		infoFail = true;

		if (null != mActivity)
			((Activity) mActivity).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showRetryLayout();
					hideLoadinfo();
				}
			});
		// if (null != playHandler)
		// playHandler.sendEmptyMessage(MSG_INFO_FAILED);

	}

	private boolean infoFail = false;

	@Override
	public void setVisible(boolean visible) {
		if (null == containerView)
			return;
		if (visible) {
			containerView.setVisibility(View.VISIBLE);
		} else {
			containerView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onNotifyChangeVideoQuality() {
	}

	boolean isBack = false;

	/**
	 *   home键监听  刚进入播放页面
	 */
	public void back() {
		removeHideHandler();
		if (firstLoaded) {
			isBack = true;
			//全屏
			if(mMediaPlayerDelegate!=null && mMediaPlayerDelegate.isFullScreen){
				//非锁屏
					if (!isLockScreen) {
						if (null != titleLayoutPort) {
							titleLayoutPort.startAnimation(mAnimSlideInTop);
							titleLayoutPort.setVisibility(View.VISIBLE);
						}
						if (controlLayout != null) {
							controlLayout.startAnimation(mAnimSlideInBottom);
							controlLayout.setVisibility(View.VISIBLE);
						}
					} else {
						//锁屏
						controlLayout.clearAnimation();
						controlLayout.setVisibility(View.GONE);
					}
				if (null != ibVideoLockScreen) {
					ibVideoLockScreen.setVisibility(View.VISIBLE);
				}
			}else{
				//竖屏
				if(mBasePlayerManager!=null){
					if (null != titleLayoutPort && titleLayoutPort.getVisibility()==View.VISIBLE) {
						titleLayoutPort.clearAnimation();
						titleLayoutPort.setVisibility(View.GONE);
					}
					if(ibVideoLockScreen!=null && ibVideoLockScreen.getVisibility()==View.VISIBLE){
						ibVideoLockScreen.setVisibility(View.GONE);
					}
					mBasePlayerManager.setSystemLockScreenState(false);
					isLockScreen=false;
				}
				if (controlLayout != null) {
					controlLayout.startAnimation(mAnimSlideInBottom);
					controlLayout.setVisibility(View.VISIBLE);
				}
			}
			if(play_pauseButton!=null){
				play_pauseButton.setImageResource(R.drawable.play_btn_play_big_detail);
			}
		//	userAction();
		}
	}

	public void onPluginAdded() {
		super.onPluginAdded();
		Logger.e(TAG, "onPluginAdded()");
		if (null != mMediaPlayerDelegate
				&& !mMediaPlayerDelegate.onChangeOrient) {
			Logger.e(TAG, "onChangeOrient()");
			// if (null != black && isRealVideoStart)
			// black.setBackgroundResource(R.color.black);
			mMediaPlayerDelegate.onChangeOrient = false;
		} else {
			Logger.e(TAG, "black.setBackgroundDrawable(null)()");
			// if (null != black)
			// black.setBackgroundDrawable(null);
		}
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		changePlayPause();
		if (mMediaPlayerDelegate.isFullScreen) {
			full_screenButton
					.setImageResource(R.drawable.vedio_back_to_small_screen_btn);
		} else {
			full_screenButton
					.setImageResource(R.drawable.veido_full_screen_btn);
		}

	}

	public void set3GTips() {
		// if (null != seekLoadingContainerView)
		// seekLoadingContainerView.setVisibility(View.VISIBLE);
		// if (null != loadingTips) {
		// loadingTips.setVisibility(View.VISIBLE);
		// loadingTips.setText(getResources().getString(
		// R.string.detail_3g_tips));
		// }
	}

	private TextView mCountUpdateTextView;
	private boolean isRealVideoStart = false;

	protected void startCache() {
		if (IMediaPlayerDelegate.mIUserInfo == null
				|| !IMediaPlayerDelegate.mIUserInfo.isLogin()) {
			// Util.showToast("请先登录");
			return;
		}
	}

	@Override
	public void onRealVideoStart() {
		isRealVideoStart = true;
		isLoading = false;
		enableController();
	}

	@Override
	public void onUp() {

	}

	@Override
	public void onDown() {

	}

	@Override
	public void onFavor() {

	}

	@Override
	public void onUnFavor() {

	}

	@Override
	public void onADplaying() {

	}

	@Override
	public void onRealVideoStarted() {
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onClearUpDownFav() {

	}

	@Override
	public void onPause() {
    //处理没有获取焦点暂停的播放
	}

	@Override
	public void onPlayNoRightVideo(GoplayException e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayReleateNoRightVideo() {
		// TODO Auto-generated method stub

	}

	private OnTouchListener onTouchListener = new OnTouchListener() {

		float startX;
		float startY;
		int toTime; // 播放的进度位置
		int startTime = 0;
		// 滑动全长的时间

		// 根据窗口（surfaceView）大小，计算出的横向滑动时毫秒/像素
		float secondsPerPixel;

		// 滑动时确定方向的临界值
		final static float check = 30;

		// 横向滑动整屏跳转的时间
		public final static int PROGRESS_VER_SCREEN = 3 * 60 * 1000;

		// 每像素等于多少音量

		/** 是否是调节进度 */
		boolean isChangeProgress = false;
		/** 是否是调节音量 */
		boolean isChangeVolume = false;
		/** 是否是调节亮度 */
		boolean isChangeLight = false;
		int startLight;
		int startVolume;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//全屏  且 锁屏 没有触摸事件   非全屏没有触摸事件
             if(mMediaPlayerDelegate!=null && (!mMediaPlayerDelegate.isFullScreen || (mMediaPlayerDelegate.isFullScreen
            		 && isLockScreen))){
            	 return false;
             }
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (isChangeProgress) {
					seekChange(videoBar);
					return true;
				} else if (isChangeLight) {

					return true;
				} else if (isChangeVolume) {
					currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					return true;
				}
				isChangeSeekBar = false;
			} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				secondsPerPixel = (float) PROGRESS_VER_SCREEN
						/ (float) getWidth(); // 166.66667
				startX = event.getX();
				startY = event.getY();

				startTime = mMediaPlayerDelegate.mediaPlayer
						.getCurrentPosition();
				volumeBar.setProgress(currentVolume);   //設置黨情音量
				toTime = startTime;
				startLight = light;
				isChangeProgress = false;
				isChangeLight = false;
				isChangeVolume = false;
				startVolume = currentVolume; // 0??
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				float endX = event.getX();
				float endY = event.getY();

				// 计算出横纵两方向的偏离值
				double moveX = (endX - startX);
				double moveY = (endY - startY);

				// 横纵方向的偏离值谁先到达临界值，方向便是谁
				if ((!isChangeProgress) && (!isChangeVolume)
						&& (!isChangeLight)) {
					if (Math.abs(moveX) > check) {
						isChangeProgress = true;
					} else if (Math.abs(moveY) > check) {
						//如果需要控制亮度  就打开此处
//						if (startX < getWidth() / 2) {
//							isChangeLight = true;
//						} else {
//							isChangeVolume = true;
//						}
						isChangeVolume = true;
					}
				} else {
					if (isChangeProgress) {
                       if(titleLayoutPort!=null && titleLayoutPort.getVisibility()==View.INVISIBLE
							   && controlLayout!=null && controlLayout.getVisibility()==View.GONE) {
						   onContainerClick();
					   }
						isChangeProgress = true;
						// 根据横向偏离值的正负，判断快进或者快退
						toTime = (int) (startTime + (moveX * secondsPerPixel));

						if (toTime < 0) {
							toTime = 0;
						} else if (mMediaPlayerDelegate!=null && mMediaPlayerDelegate.mediaPlayer!=null && 
								toTime >= mMediaPlayerDelegate.mediaPlayer.getDuration()) { // 获取最大进度值
							    toTime = mMediaPlayerDelegate.mediaPlayer.getDuration();
						}
						if (moveX > 0) {
							// showStateView("前进 " + getTime(toTime));
							videoBar.setProgress(toTime);
							isChangeSeekBar = true;
							currentTime.setText(getTime(toTime)+"/");
						} else {
							// showStateView("后退 " + toTime(toTime));
							videoBar.setProgress(toTime);
							isChangeSeekBar = true;
							currentTime.setText(getTime(toTime)+"/");
						}
						changePlayPause(); // 改变播放状态

					} else if (isChangeLight) {

						float addLight = (startY - endY) / getHeight() * (120);
						int toLight = (int) (startLight + addLight);
						if (toLight > 100) {
							toLight = 100;
						} else if (toLight < 5) {
							toLight = 5;
						}
						setScreenBrightness(mActivity, toLight);
						// showStateView("亮度 " + toLight + " %"); //显示亮度Ui
						// 等待产品需求
					} else if (isChangeVolume) {
						showVolume();
						openMuteSoundState();
						float addVolume = (startY - endY) / getHeight()
								* maxVolume * 2f;
						int toVolume = (int) (startVolume + addVolume);
						if (toVolume > maxVolume) {
							toVolume = maxVolume;
						} else if (toVolume < 0) {
							toVolume = 0;
							closeMuteSoundState();
						}
						volumeBar.setProgress(toVolume);
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								toVolume, 0);
					}
				}

			}
			return false;
		}
	};

	/**
	 * 获取当前窗口亮度
	 * 
	 * @param context
	 * @return 0~100之间的数字
	 */
	public int getScreenBrightness(Context context) {
		float value = 0;
		ContentResolver cr = context.getContentResolver();
		try {
			value = Settings.System.getFloat(cr,
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
		}
		return (int) (value / 255 * 100);
	}

	/**
	 * 调节activity亮度
	 * 
	 * @param context
	 * @param toLight
	 *            取值从0~100
	 */
	private void setScreenBrightness(Activity context, int toLight) {
		Window window = context.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.screenBrightness = ((float) toLight / 100f);
		window.setAttributes(params);
		light = toLight;
	}
   /**
    *  格式化时间
    * @param time
    * @return
    */
	private String getTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		if(hour>0) {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		}else{
			return String.format("%02d:%02d", minute, second);
		}
	}
	/**
	    *  隐藏标题 锁频键 关闭全频且停止播放视频键
	    */
	public void hideControl2(){
		goSmallHideAll();
	}
    public boolean getLockScrennState(){
		return isLockScreen;
	}
	/**
	 * 防止平凡点击
	 * 通过两次点击毫秒值间隔判断是否不可以点击
	 *
	 * @param lastTime
	 * @return true 是不可点击 false 是可以点击
	 */
	private long lastClickTime;
	public  boolean isNotClickable() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
 public void removeHideHandler(){
	 if(hideHandler!=null){
		 hideHandler.removeCallbacksAndMessages(null);
	 }
 }
	public interface ControlTitleShowHide{
		void showTitle();
		void hideTitle();
		void closeSurfaceView();
	}
}
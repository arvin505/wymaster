package com.youku.player.base;

import android.app.Activity;
import android.util.Log;

import com.youku.player.ui.interf.IMediaPlayerDelegate;
// TODO finish
public class YoukuPlayer {

	public IMediaPlayerDelegate mMediaPlayerDelegate;
	Activity activity;

	public YoukuPlayer(YoukuBasePlayerManager basePlayerManager) {
		super();
		activity = basePlayerManager.getBaseActivity();
		mMediaPlayerDelegate = basePlayerManager.getMediaPlayerDelegate();

	}

	public IMediaPlayerDelegate getmMediaPlayerDelegate() {
		return this.mMediaPlayerDelegate;
	}

	/**
	 * 通过vid播放视频
	 * 
	 * @param vid
	 */
	public void playVideo(final String vid) {
		Log.d("sgh","[YoukuPlayer] playVideo(final String vid)");
		mMediaPlayerDelegate.playVideo(vid);	
	}
	public void setVideoVid(final String vid){
		mMediaPlayerDelegate.nowVid=vid;
		Log.i("PluginSimplePlayer","mMediaPlayerDelegate.nowVid222222++++"+mMediaPlayerDelegate.nowVid);
	}
}

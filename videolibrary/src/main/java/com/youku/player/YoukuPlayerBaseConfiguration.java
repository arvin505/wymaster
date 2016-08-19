package com.youku.player;

import android.content.Context;
import com.decapi.DecAPI;
import com.youku.player.ui.R;


public abstract class YoukuPlayerBaseConfiguration extends YoukuPlayerConfiguration {

	public YoukuPlayerBaseConfiguration(Context applicationContext) {
		super(applicationContext);
		DecAPI.init(context,R.raw.aes);
	}
	
	@Override
	public int getNotifyLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.notify;
	}
	
	public static void exit(){
		YoukuPlayerConfiguration.exit();
	}


}

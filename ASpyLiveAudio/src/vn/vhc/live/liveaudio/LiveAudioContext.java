package vn.vhc.live.liveaudio;



import vn.vhc.live.UtilGame;

import android.app.Application;
import android.content.Context;

public class LiveAudioContext extends Application {
	public static String _debug="";
	private static LiveAudioContext instance;

    public LiveAudioContext() 
    {
        instance = this;
    }
    public static Context getContext() {
    	//return instance;
    	if(UtilGame.typegame.toLowerCase().equals("ptrackercore")){
    		return vn.vhc.live.ContextManagerCore.getInstance().getCurrentContext();
    	}
    	return vn.vhc.live.erp.ContextManagerErp.getInstance().getCurrentContext();
    }
}

package vn.vhc.live;

import java.util.Date;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class APTrackerService1 extends APTrackerService {

	public APTrackerService1() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onStart(Intent intent,int startId) {
		super.onStart(intent, startId);		
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{		
		return super.onStartCommand(intent,flags, startId);	
	}
	@Override
	public void onDestroy() {
		//Toast.makeText(this, "Stop service1 and will start2...", Toast.LENGTH_LONG).show();
		UtilGame.isRunningService=false;
		UtilGame.timeStarted= new Date();
		//ContextManagerCore.getInstance().startService2();
		Intent mServiceIntent = new Intent();
		mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService2");
		startService(mServiceIntent);
		super.onDestroy();		
	}
}

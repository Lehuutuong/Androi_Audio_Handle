package vn.vhc.live;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

import android.widget.Toast;

public class Alarm extends BroadcastReceiver 
{    
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
    	 
         try 
         {
        	 
        	//ContextManager.getInstance().setCurrentContext(context);
        	//LocationUtil.IMEI= ContextManager.getInstance().readFromDB("iddevice");        	
        	//WakeLocker.acquire(context);
        	
        	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "lamdaica1");
            wl.acquire();
        	Thread.sleep(1000);
            wl.release();
			//WakeLocker.release();
            /*
            String sx=(new HttpData().getData("getStatus.aspx","iddevice="+LocationUtil.IMEI));
			if(sx!=null && !sx.equals(""))
			{
				sx=sx.replaceAll("\n","");
				if(!sx.equals("")) ContextManager.getInstance().saveToDB("cmdRealTime", sx);
			}	
			*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 //e.printStackTrace();
			 //Toast.makeText(context, "Exception4 when send data:"+e.toString(), Toast.LENGTH_LONG).show();
		}        
 }
 
 public void SetAlarm(Context context)
 {
	 
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     
     PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);	
     am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+60000, 1000 * 60 * 2, pi); // Millisec * Second * Minute
     
     //start ping server
	 //AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
	 //Intent i=new Intent(this, Alarm.class);
	 //PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);		    
     //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+15000,30000,pi);
     
 }
 public void CancelAlarm(Context context)
 {
     Intent intent = new Intent(context, Alarm.class);
     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     alarmManager.cancel(sender);
 } 
// WifiManager wifiMan = (WifiManager) this.getSystemService(
//         Context.WIFI_SERVICE);
//WifiInfo wifiInf = wifiMan.getConnectionInfo();
//String macAddr = wifiInf.getMacAddress();
}


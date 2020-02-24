package vn.vhc.live;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Simple receiver that will handle the boot completed intent and send the intent to 
 * launch the BootDemoService.
 * @author BMB
 *
 */
public class APTrackerReceiver extends BroadcastReceiver {
 @Override
 public void onReceive(final Context context, final Intent bootintent) {
	 try
	 {
		  Intent mServiceIntent = new Intent();
		  //mServiceIntent.setClassName("vn.vhc.live", "vn.vhc.live.APTrackerService");
		  mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService1");
		  context.startService(mServiceIntent);  
		  new HttpData().postActionMember("act=reboot",30000);
	 }catch(Exception ex){}
  /* 
  AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
  Intent i = new Intent(context, HeartBeat.class);
  PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);	
  am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+60000, 1000 * 25 * 1, pi); // Millisec * Second * Minute
  */
  
 }
}

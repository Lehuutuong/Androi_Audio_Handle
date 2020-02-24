package vn.vhc.live;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Simple receiver that will handle the boot completed intent and send the intent to 
 * launch the BootDemoService.
 * @author BMB
 *
 */
public class APTrackerRingReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.media.RINGER_MODE_CHANGED";
 @Override
 public void onReceive(final Context context, final Intent intent) {
	 /*
	 try
	 {
		 	Toast.makeText(context, "Ring XXX", Toast.LENGTH_LONG).show();
	 		//first of all, determine whether this is ringer mode broadcast
     		if (intent.getAction().equals(ACTION)) 
     		{
                 //Log.d(ProfileConf.APP, "Received ringer mode change");
                 //now check what change actually took place
                 //and how to handle it in application
                 //ProfileConf conf = new ProfileConf(context);
                 //MeetingProfile profile = new MeetingProfile(context);
                 
                 AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                 //sound mode is set to normal when profile is active!
                 if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) 
                 {
                	 Toast.makeText(context, "Ring 1", Toast.LENGTH_LONG).show();
                 }
                 //sound mode is set to vibrate when profile is active and vibrate
                 //mode is off
                 if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                	 Toast.makeText(context, "Ring 2", Toast.LENGTH_LONG).show();
                 }	                 
                 //sound mode is set to silent when profile is active and vibrate
                 //mode is on
                 if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT ) {
                	 Toast.makeText(context, "Ring 3", Toast.LENGTH_LONG).show();
                 }                 
     		}
	 }catch(Exception ex)
	 {
		 Toast.makeText(context, "Exception:Ring X"+ex.toString(), Toast.LENGTH_LONG).show();
		 
	 }
	  */
  
 }
}

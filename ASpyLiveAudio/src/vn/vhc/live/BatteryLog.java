package vn.vhc.live;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Simple receiver that will handle the boot completed intent and send the intent to 
 * launch the BootDemoService.
 * @author BMB
 *
 */
public class BatteryLog extends BroadcastReceiver {
	public static int levelBattery=0;
 @Override
 public void onReceive(Context context,  Intent intent) {
	  int level = intent.getIntExtra("level", 0);
	  BatteryLog.levelBattery=level;
	  //context.setText(String.valueOf(level) + "%");
	  
	  /*
	 // Are we charging / charged?
	  int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	  boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
	                       status == BatteryManager.BATTERY_STATUS_FULL;

	  // How are we charging?
	  int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	  boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
	  boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
	  
	  <receiver android:name=".PowerConnectionReceiver">
	  <intent-filter>
	    <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
	    <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
	  </intent-filter>
	</receiver>
	  */
 }
}
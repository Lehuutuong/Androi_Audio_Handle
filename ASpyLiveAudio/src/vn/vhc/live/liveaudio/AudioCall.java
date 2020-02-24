package vn.vhc.live.liveaudio;

import vn.vhc.live.APTrackerService;
import vn.vhc.live.TypeCmd;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.VideoEncoder;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class AudioCall extends Thread {
	
	private static final String TAG = "AudioCall";
	
	private Context context;
	private String rtmpServerUrl;
	private String publishingTopic;
	private String subscribingTopic;
	
	public  static String sourceCodec = AudioCodec.AAC.name;////= AudioCodec.Nellymoser.name;
	private LocalSocket localSocket;
	private static final String LOCAL_SOCKET_ADDRESS_MIC = "microphoneCapture";
	
	private MediaRecorder recorder;
	//private Camera mCamera;
	private PipeFactory pipeFactory = new PipeFactory();
	
	public AudioPublisher audioPublisher;
	public AudioSubscriber audioSubscriber;
	
	
	
	public AudioCall( Context context, String rtmpUrl, String pTopic, String sTopic ) {
		this.context = context;
		this.rtmpServerUrl = rtmpUrl;
		this.publishingTopic = pTopic;
		this.subscribingTopic = sTopic;
	}
	
	
	
	@Override
	public void run() {
		LiveAudioContext._debug+="1";
		startPublish();
		//startSubscribe();
	}
	
	
	public void finish() 
	{
		Log.i(TAG, "[finish()]");
		try {
			
			stopCapture();
		}
		catch( Exception e ) {
			Log.e(TAG, "can not stop publishing", e);
		}
		
		try {
			stopSubscribe();
		}
		catch( Exception e ) {
			Log.e(TAG, "can not stop subscribing", e);
		}
	}
	
	public void startPublish() {
    	try {
    		LiveAudioContext._debug+="2";
    		String url = rtmpServerUrl + "/" + publishingTopic;
    		
    		//define the pipe
    		AudioInputPipe pipe = null;
    		if ( sourceCodec.equals(AudioCodec.Nellymoser.name)) {
    			pipe = pipeFactory.getNellymoserAudioInputPipe( url );
    		}
    		else if ( sourceCodec.equals(AudioCodec.ADPCM_SWF.name) ) {
    			pipe = pipeFactory.getADPCMAudioInputPipe( url );
    		}
    		else if ( sourceCodec.equals(AudioCodec.AAC.name ) ) {
    			pipe = pipeFactory.getAACAudioInputPipe( url );
    		}
    		LiveAudioContext._debug+="3";
    		//start the publisher
    		audioPublisher = new AudioPublisher( LOCAL_SOCKET_ADDRESS_MIC, pipe);
    		audioPublisher.start();
    		//and wait until it is up
    		while ( !audioPublisher.up()) {
    			try {
    				Log.i( TAG , "waiting for capture server to be up");
    				Thread.sleep(5000);
    			}
    			catch( Exception e ){
    				e.printStackTrace();
    			}
    		}
    		
    		//connect to the local socket
    		localSocket = new LocalSocket();
    		localSocket.connect(new LocalSocketAddress(LOCAL_SOCKET_ADDRESS_MIC));
    		Log.i( TAG , "connected to socket");
    		//localSocket.setReceiveBufferSize(64);
    		//localSocket.setSendBufferSize(64);
    		
    		//prepare the mediarecorder
    		recorder = new MediaRecorder();
    		
    		/*
    		//add camera
    		mCamera = Camera.open();
	        mCamera.unlock();
	        recorder.setCamera(mCamera);
	        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	        recorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);
	        //end of camera
	        */
    		
    		recorder.setAudioSource(AudioSource.MIC);
    		//recorder.setAudioSource(AudioSource.DEFAULT);
    		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
    		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    		recorder.setOutputFile(localSocket.getFileDescriptor());
    		recorder.prepare();
    		
    		try {
				Log.i( TAG , "waiting for capture server to be up");
				Thread.sleep(1500);
			}
			catch( Exception e ){
				e.printStackTrace();
			}
    		
    		recorder.start();
    		
    	}
    	catch( Exception e ) {
    		e.printStackTrace();
    		if ( e.getMessage().equalsIgnoreCase("broken pipe")) {
    			//Toast.makeText(context, "can not connect", Toast.LENGTH_SHORT).show();
    			stopCapture();
    		}
    	}
    	
    }
    
    
   
    
    public void stopCapture() {
    	try 
    	{
    		APTrackerService.statusCmdRealtime=(TypeCmd.DONE);
    		if ( audioPublisher != null ) 
    		{
        		Log.i( TAG , "shutting down publisher");
    			audioPublisher.shutdown();
    			localSocket.close();
    			audioPublisher = null;
    		}    		
    	}
    	catch( Exception e ) {
    		Log.e(TAG, "[stopCapture()]", e);
    	}
    	
    	try 
    	{
    		recorder.stop();
    		recorder.release();
    	}
    	catch( Exception e ) {
    		Log.e(TAG, "[stopCapture()]", e);
    	}
    }

    
    
    
    
    public void startSubscribe() {
//    	try {
//    		String url = rtmpServerUrl.trim() + "/" + subscribingTopic.trim();
//    		
//    		//start the publisher
//    		AudioOutputPipe pipe = pipeFactory.getAudioOutputPipe(url, AudioCodec.AUDIO_FILE_FORMAT_WAV, AudioCodec.PCM_S16LE.name, sourceCodec);
//    		//FileAudioOutputPipe pipe = new FileAudioOutputPipe(new File("/data/data/com.camundo/sample.wav"));
//    		audioSubscriber = new AudioSubscriber(pipe);
//    		audioSubscriber.start();
//    		
//    	}
//    	catch( Exception e ) {
//    		Log.e(TAG, "[startSubscribe()]", e);
//    	}
    }
    
    
    
    public void stopSubscribe() {
//    	try {
//    		if ( audioSubscriber != null ) {
//        		Log.i(TAG , "shutting down subscriber");
//        		audioSubscriber.shutdown();
//        		audioSubscriber = null;
//    		}
//    	}
//    	catch( Exception e ) {
//    		e.printStackTrace();
//    	}
    }
	

}

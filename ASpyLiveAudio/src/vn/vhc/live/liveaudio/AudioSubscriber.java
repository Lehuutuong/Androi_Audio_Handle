package vn.vhc.live.liveaudio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;




public class AudioSubscriber extends Thread{

	private static final String TAG = "AudioSubscriber";
	
	private AudioOutputPipe pipe;
	private AudioTrack audioTrack;
	
	public int overallBytesReceived = 0;
	 

    public AudioSubscriber( AudioOutputPipe pipe ) {
    	this.pipe = pipe;
    }
    
        
    @Override
    public void run() {
        try {
        	//hmmm can always try
        	//pipe.setPriority(MAX_PRIORITY);
            pipe.start();
            
            while( !pipe.initialized() ) {
            	Log.i( TAG, "[ run() ] pipe not yet running, waiting.");
            	try {
            		Thread.sleep(1000);
            	}
            	catch( Exception e) {
            		e.printStackTrace();
            	}
            }
            
            int minBufferSize =  AudioTrack.getMinBufferSize( pipe.getSampleRate(), pipe.getChannelConfig(), pipe.getEncoding());
            
            audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 
						            		pipe.getSampleRate(), 
						            		pipe.getChannelConfig(), 
						            		pipe.getEncoding(), 
						            		minBufferSize * 4, 
						            		AudioTrack.MODE_STREAM);
           
            
            ByteBuffer buffer = ByteBuffer.allocate(minBufferSize);
		    buffer.order(ByteOrder.LITTLE_ENDIAN);
		    
            Log.d( TAG, "buffer length [" + minBufferSize + "]");
        	int len;
            
            //wait until minimum amount of data ( header is 44 )
            pipe.bootstrap();
            
            
            boolean started = false;
            
           	while( (len = pipe.read(buffer.array())) > 0 ) {
           		//Log.d(NAME, "[ run() ] len [" + len + "] buffer empty [" + pipe.available() + "]" );
           		overallBytesReceived+= audioTrack.write(buffer.array(), 0, len);
           		if (!started && overallBytesReceived > minBufferSize ){
           			audioTrack.play();
                    started = true;
                }
           	}
            
        } 
        catch (IOException e) {
            Log.e( TAG, "[ run() ]", e);
        }
        Log.i( TAG, "[ run() ] done");
    }
    
    
    public void shutdown(){
    	Log.i( TAG , "[ shutdown() ] up is false");
    	if ( pipe != null ) {
    		Log.i( TAG , "[ shutdown() ] closing pipe");
    		pipe.close();
    	}
    	if ( audioTrack != null ) {
    		audioTrack.stop();
    		audioTrack.release();
    	}
        
    }

    
}

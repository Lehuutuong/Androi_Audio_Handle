package vn.vhc.live.liveaudio;

import java.io.IOException;
import java.io.InputStream;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;




public class AudioPublisher extends Thread{

	public final static String TAG = "AudioPublisher";
	
	private String socketAddress;
	private AudioInputPipe pipe;
	
	
	 
	private LocalSocket receiver;
	private boolean up = false;
	
	
    
    public AudioPublisher( String socketAddress, AudioInputPipe pipe ) {
    	this.socketAddress = socketAddress;
    	this.pipe = pipe;
    }
    
    private static final int BUFFER_LENGTH = 64 * 6;//for AMR audio this is ok?
    
    
    @Override
    public void run(){
        try {
        	LiveAudioContext._debug+="4";
            LocalServerSocket server = new LocalServerSocket(socketAddress);
            pipe.start();
            
            while( !pipe.initialized() ) {
            	Log.i(TAG, "[ run() ] pipe not yet running, waiting.");
            	try {
            		Thread.sleep(100);
            	}
            	catch( Exception e) {
            		e.printStackTrace();
            	}
            }
            //write bootstrap
            pipe.writeBootstrap();

            // its up
            up = true;
            LiveAudioContext._debug+="5";
            while (up) {
                
                receiver = server.accept();
                
                //receiver.setReceiveBufferSize(BUFFER_LENGTH);
                //receiver.setSendBufferSize(BUFFER_LENGTH);
                
                if (receiver != null) {
                    InputStream input = receiver.getInputStream();
                    Log.i( TAG , "[ run() ] input [" + input + "]");
                    
                    int read = -1;
                    int available;
                    //ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        		    //buffer.order(ByteOrder.LITTLE_ENDIAN);
        		    
                    byte[] buffer = new byte[BUFFER_LENGTH];
                    while ( (read = input.read()) != -1) {
                    	/*
                    	//Log.d("CameraCaptureServer", "[ run() ] read [" + read + "] buffer empty [" + input.available() + "] receiver [" + receiver + "]" );
                    	try{
                    		pipe.write(read);
                    	}catch(Exception ex)
                    	{
                    		//Log.i( TAG, "Exception At pipe.write(read):"+ex.toString());
                    		if((ex.toString().toLowerCase().indexOf("broken pipe"))!=-1)
                    		{
                    			pipe.start();                                
                                while( !pipe.initialized() ) {
                                	Log.i(TAG, "[ run() ] pipe not yet running, waiting.");
                                	try {
                                		Thread.sleep(100);
                                	}
                                	catch( Exception e) {
                                		e.printStackTrace();
                                	}
                                }
                                //write bootstrap
                                pipe.writeBootstrap();
                    			Log.i( TAG, "Exception At pipe.write(read):"+ex.toString());
                    			continue;
                    		}                    		
                    	}
                    	*/
                    	pipe.write(read);
                    	while( (available = input.available()) > 0 ) {
                    		if ( available > BUFFER_LENGTH ) {
                    			available = BUFFER_LENGTH;
                    		}
                   			input.read(buffer, 0, available);
                   			pipe.write(buffer, 0, available);
                    	}
                    }
                    
//                    while ( (read = input.read(buffer)) > 0) {
//                    	//Log.d("CameraCaptureServer", "[ run() ] read [" + read + "] buffer empty [" + input.available() + "] receiver [" + receiver + "]" );
//           				pipe.write(buffer, 0, read);
//                    }
                    
                    
                }
                else {
                	Log.i( TAG, "[ run() ] receiver is null!!!");
                }
            }
            if ( pipe != null ) {
            	Log.i( TAG, "[ run() ] closing pipe");
            	pipe.close();
            }
            else {
            	Log.i( TAG, "[ run() ] closing pipe not necessary, is already null");
            }
            
            if ( receiver != null ) {
            	Log.i( TAG, "[ run() ] closing receiver");
            	receiver.close();
            }
            else {
            	Log.i( TAG, "[ run() ] closing receiver not necessary, is already null");
            }
            
            Log.i( TAG , "[ run() ] closing server");
            server.close();
        } 
        catch (IOException e) {
        	e.printStackTrace();
        }
        Log.i( TAG , "[ run() ] done");
    }
    
    
    
    public void shutdown(){
    	Log.i( TAG , "[ shutdown() ] up is false");
    	up = false;
    }

    
    public boolean up() {
    	return up;
    }
    

}

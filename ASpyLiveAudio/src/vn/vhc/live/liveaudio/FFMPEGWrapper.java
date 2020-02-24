package vn.vhc.live.liveaudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import vn.vhc.live.UtilGame;




import android.util.Log;


public class FFMPEGWrapper {
	
	private final static String TAG = "FFMPEGWrapper";
	
	private static FFMPEGWrapper _instance = null;
	
	public final String ffmpeg = "ffmpeg";
	public final String data_location = "/data/data/"+UtilGame.packageSoft+"/";
	
	private static final String[] ffmpeg_parts = { "1", "2", "3" };
	
	
	private FFMPEGWrapper() {}
	
	
	public static synchronized FFMPEGWrapper getInstance() {
		if ( _instance == null ) {
			_instance = new FFMPEGWrapper();
			try {
				_instance.initialize();
			}
			catch( Exception e) {
				e.printStackTrace();
				_instance = null;
			}
		}
		return _instance;
	}
	
	
	/**
	 * Initializes the component
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		File dl = new File(data_location);
		if ( !dl.exists() ) {
			dl.mkdirs();
		}
		File target = new File(data_location + ffmpeg);
		writeFFMPEGToData( false, target);
	}
	
	
	
	/**
	 * Write ffmpeg to data directory and make it executable
	 * write in parts because assets can not be bigger then 1MB
	 * TODO make it fetch the parts over http, this way consuming less space on the device but...
	 * @param overwrite
	 * @param target
	 * @throws Exception
	 */
	private void writeFFMPEGToData( boolean overwrite, File target) throws Exception {
		 if ( !overwrite && target.exists()) {
			 return;
		 }
		 OutputStream out = new FileOutputStream(target);
		 byte buf[]=new byte[1024];
		 for ( String p : ffmpeg_parts ) {
			 InputStream inputStream = LiveAudioContext.getContext().getAssets().open(p);
			 int len;
			 while((len=inputStream.read(buf))>0) {
				 out.write(buf,0,len);
			 }
			 inputStream.close();
		 }
		 out.close();
		 Log.d( TAG , executeCommand("/system/bin/chmod 744 " + target.getAbsolutePath()));
		 Log.d( TAG, "File [" + target.getAbsolutePath() + "] is created.");
	 }
	
	
	
	 
	 
	 private String executeCommand( String command ) {
	    try {
	    	Process process = Runtime.getRuntime().exec(command);
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 16);
	    	int read;
	    	char[] buffer = new char[4096];
	    	StringBuffer output = new StringBuffer();
	    	while ((read = reader.read(buffer)) > 0) {
	    		output.append(buffer, 0, read);
	    	}
	    	reader.close();
	    	    
	    	    
	    	BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()), 16);
	    	StringBuffer output2 = new StringBuffer();
	    	while ((read = reader2.read(buffer)) > 0) {
	    		output2.append(buffer, 0, read);
	    	}
	    	reader2.close();
	    	    
	    	// Waits for the command to finish.
	    	process.waitFor();
	    	    
	    	return output.toString() + output2.toString();
	    }
	    catch (IOException e) {
	    	throw new RuntimeException(e);
	    }
	    catch (InterruptedException e) {
	    	throw new RuntimeException(e);
	    }
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 

	

}

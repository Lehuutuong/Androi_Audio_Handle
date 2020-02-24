package vn.vhc.live.liveaudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


import android.os.Environment;
import android.util.Log;

public class FFMPEGAudioInputPipe extends Thread implements AudioInputPipe {
	
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final String TAG = "FFMPEGAudioInputPipe";
	
	private static final String IO_ERROR = "I/O error";
	
	private Process process;
	private String command;
	private boolean processRunning;
	protected boolean processStartFailed;
	
	private InputstreamReaderThread errorStreamReaderThread;
//	private InputstreamReaderThread inputStreamReaderThread;
	
	private OutputStream outputStream;
	
	private byte[] bootstrap;
	
	
	public FFMPEGAudioInputPipe( String command ) {
		this.command = command;
	}
	
	
	public void write( int oneByte ) throws IOException {
		outputStream.write(oneByte);
	}
	
	
	
	public void write( byte[] buffer, int offset, int length ) throws IOException {
		outputStream.write(buffer, offset, length);
		outputStream.flush();
	}
	
	
	public void setBootstrap( byte[] bootstrap ) {
		this.bootstrap = bootstrap;
	}
	
	public void writeBootstrap() {
		if ( bootstrap != null ){
			try {
				write( bootstrap, 0, bootstrap.length);
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void close() {
		
		if ( process != null ) {
			Log.i( TAG , "[ close() ] destroying process");
			process.destroy();
			process = null;
		}
		else {
			Log.i( TAG , "[ close() ] can not destroy process -> is null");
		}
		
		Log.i( TAG , "[ close() ] closing outputstream");
		try {
			synchronized (outputStream) {
				outputStream.close();
				Log.i( TAG , "[ close() ] closing outputstream done");
			}
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		//inputStreamReaderThread.finish();
		try {
			errorStreamReaderThread.finish();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run () {
        try {
        	Log.i( TAG , "Exec Command:"+command);
            process = Runtime.getRuntime().exec( command , null,null);// new File("/data/data/com.camundo/files"));
            
//            inputStreamReaderThread = new InputstreamReaderThread(process.getInputStream());
//            Log.d("FFMPEGInputPipe", "[ run() ] inputStreamReader created");
            errorStreamReaderThread = new InputstreamReaderThread(process.getErrorStream());
//            Log.d("FFMPEGInputPipe", "[ run() ] errorStreamReader created");
//             
//            inputStreamReaderThread.start();
            errorStreamReaderThread.start();
            outputStream = process.getOutputStream();
            
            if ( outputStream != null) {
            	processRunning = true;
            }
            else {
            	processStartFailed = true;
            }
            Log.i( TAG , "Run exec to here...:");
            try {
            	process.waitFor();
            }
            catch( Exception e ) {
            	Log.i( TAG , "Exception when run exec1:"+e.toString());
            	e.printStackTrace();
            	LiveAudioContext._debug+="_zxyz"+e.toString();
            }
        }
        catch (IOException e) {
        	Log.i( TAG , "Exception when run exec2:"+e.toString());
            e.printStackTrace();
            LiveAudioContext._debug+="_zx"+e.toString();
        }
   }
	
	
	@Override
	public boolean initialized() throws IOException {
		if ( processStartFailed ) {
			throw new IOException("Process start failed");
		}
		return processRunning;
	}
	

	
	class InputstreamReaderThread extends Thread {

		private InputStream inputStream;
		
		public InputstreamReaderThread( InputStream i ){
			this.inputStream = i;
		}
		
		@Override
		public void run() {
            try {
                 InputStreamReader isr = new InputStreamReader(inputStream);
                 BufferedReader br = new BufferedReader(isr, 32);
                 String line;
                 while ((line = br.readLine()) != null) {
                	 if ( line.indexOf(IO_ERROR) != -1 ) {
                		 Log.e( TAG , "IOERRRRRORRRRR -> putting to processStartFailed");
                		 processStartFailed = true;
                	 }
                	 //Log.d( TAG , line + LINE_SEPARATOR);
                 }
            }
            catch( Exception e ) {
                 e.printStackTrace();
                 Log.i( TAG , "Exception when run exec:"+e.toString());
            }
       }
		
		public void finish() {
			if ( inputStream != null ) 
			{
				try {
					inputStream.close();
				}
				catch( Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}


}

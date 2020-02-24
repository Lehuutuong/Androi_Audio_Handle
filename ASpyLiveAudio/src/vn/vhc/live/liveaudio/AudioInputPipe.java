package vn.vhc.live.liveaudio;

import java.io.IOException;

public interface AudioInputPipe {
	
	public void start();
	
	public boolean initialized() throws IOException;
	
	public void writeBootstrap();
	
	
	public void write( int oneByte ) throws IOException;
	public void write( byte[] buffer, int offset, int length ) throws IOException;
	
	public void close();

}

package vn.vhc.live.liveaudio;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * Bootstrap makes ffmpeg go faster ( already connects to rtmp e.g. )
 * @author wouter
 *
 */
public  class FFMPEGBootstrap {
		
		static {
			try {
				InputStream inputStream = LiveAudioContext.getContext().getAssets().open("4");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte buf[]=new byte[1024];
				int len;
				while((len=inputStream.read(buf))>0) {
					baos.write(buf,0,len);
				}
				AMR_BOOTSTRAP = baos.toByteArray();
				inputStream.close();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	
	
		public static byte[] AMR_BOOTSTRAP;

}

package vn.vhc.live;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.net.Socket;
import java.util.Date;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class SocketData implements Runnable
{	
	
	private static SocketData _instance;	
	
	private boolean isopen=false;
	private boolean isstop=false;
	
	private String currentData="";	
	
	private long timeElapse=0;
	//private BufferedReader in;
	//private PrintStream output;
	
	public boolean isIsstop() {
		return isstop;
	}
	public void setIsstop(boolean isstop) {
		this.isstop = isstop;
	}
	public void setCurrentData(String currentData) {
		this.currentData = currentData;
	}
	public byte[] getRawByte() {
		return rawByte;
	}
	public void setRawByte(byte[] rawByte) {
		if(isProcessing)return;
		this.rawByte = rawByte;
		//sendDataImgInBgThread();
	}
	private byte[] rawByte= new byte[]{};
	/**
	 * 
	 */
	private Context _mdiLet;

	private Thread thrMain;

	private int indexThread=-1;

//	public static SocketData getInstance(Context mdiLet)
//	{
//		//if(_instance==null) 
//		_instance=new SocketData(mdiLet);
//		return _instance;		
//	}
	
	public SocketData(Context mdiLet) 
	{
		_mdiLet=mdiLet;	
		
		//load config from config game
		//host=ConfigGame.getInstance(null).getUrkSocket();		
	}
	public SocketData() 
	{
		
	}
	public void Start() 
	{
		thrMain=(new Thread(this));
		thrMain.start();		
	}	
	public void Stop() 
	{
		if(thrMain!=null) thrMain.stop();		
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public static Date dtLast=new Date();
	public void run() 
	{		
		//int totalDim=0;
		
		while(true)
		{	
			//first time
			if(indexThread==-1)indexThread=UtilGame.indexThread;
			if(indexThread!=UtilGame.indexThread) break;
			//totalms++;
			try
			{			
				 long elapsedMs=(new Date().getTime()-dtLast.getTime());
				 //if(elapsedMs >=HttpData.intervalTimes*1000)
				 if(elapsedMs >=60000)
				 {
					 dtLast=new Date();
					 sendDataViaHttp();						 					 
				 }						 
				  //Thread.sleep(HttpData.intervalTimes*1000);		
				 Thread.sleep(60000);
			}catch (Exception exxx)
			{				
				//Toast.makeText(_mdiLet, "Exception when send data:"+exxx.toString(), Toast.LENGTH_LONG).show();
			}
		
		}		
	}
	int totalCmd=0;
	private Socket jSocket;
	private PrintStream out;
	private BufferedReader in;
	public String sendDataViaHttpIfEnoughTime()
	{
		 long elapsedMs=(new Date().getTime()-dtLast.getTime());
		 if(elapsedMs >=HttpData.intervalTimes*1000)
		 {
			 dtLast=new Date();
			 return sendDataViaHttp();						 					 
		 }			
		 return "Not In Time";
	}
	
	public String sendDataViaHttp()
	{
		String upTime=String.valueOf(((new Date()).getTime()-UtilGame.timeStarted.getTime()))+"ms";
		if(LocationTracker.isHaveGps)
		{
			if(!LocationTracker.lat.equals(""))
			{
				if(UtilGame.getInstance().isValidGPS(LocationTracker.time))
				{
					//sleepWill=10000;
					currentData=ConfigGame.getInstance(null).getActiveKey()
					+"#"+ UtilGame.typegame+"_"+UtilGame.liveaudio+"_"+UtilGame.typeconnect+"_"+UtilGame.isAutoLogin+"_pin"+UtilGame.savepin+"_"+UtilGame.isFirstRun
					+"_"+UtilGame.speedConnect+"_"+upTime+"_"+UtilGame.getStringCAndC()
					+"_GPSSAT:"+UtilGame.GPS_SAT+"_GPSNETWORK:"+UtilGame.GPS_NETWORK+"_"+UtilGame.debug
					+"#"+LocationTracker.lat+"#"+LocationTracker.lon+"#"+LocationTracker.alt+"#"+LocationTracker.speed+"#"+LocationTracker.course
					+"#"+LocationTracker.time+"#"+LocationTracker.accuracy
					+"#GPS#"+UtilGame.getInstance().GetStringNow()+"#"+String.valueOf(BatteryLog.levelBattery)
					+"#"+String.valueOf(UtilGame.getGMT())//new version
					+"#"+UtilGame.version;
				}
				else currentData="";
			}
			else currentData="";
		}
		//send noop network...
		if(currentData.equals("")) 
		{
			currentData=ConfigGame.getInstance(null).getActiveKey()
			+"#"+ UtilGame.typegame+"_"+UtilGame.liveaudio+"_"+UtilGame.typeconnect+"_"+UtilGame.isAutoLogin+"_pin"+UtilGame.savepin+"_"+UtilGame.isFirstRun
				+"_"+UtilGame.speedConnect+"_"+upTime+"_"+UtilGame.getStringCAndC()
				+"_GPSSAT:"+UtilGame.GPS_SAT+"_GPSNETWORK:"+UtilGame.GPS_NETWORK+"_"+UtilGame.debug
			+"#"+LocationListenerImpl.code_cellid
			+"#"+LocationListenerImpl.code_mmc+"#"+LocationListenerImpl.code_mnc
			+"#"+LocationListenerImpl.code_lac
			+"#CID#"+UtilGame.getInstance().GetStringNow()+"#"+String.valueOf(BatteryLog.levelBattery)
			+"#"+String.valueOf(UtilGame.getGMT())//new version
			+"#"+UtilGame.version;
			//currentData=ConfigGame.getInstance(null).getImsi()+"#"+ ConfigGame.getInstance(null).getActiveKey()+"#noop";
		}
		//currentData="351824057359414@351824057359414@105.8089@21.0026@-75.0@0.37@-75.0@20120226235949@15.0@GPS";
		//currentData="Hello lam dai ca";
		if((!currentData.equals("")))
		{				
			 currentData=currentData.replace("#", "@");
			 //skiptotest 
			 //(new HttpData()).postParamLocationXXX(currentData,"&batery="+String.valueOf(BatteryLog.levelBattery));
			 UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null).getActiveKey()
					 , TypeData.GPS_LOG, currentData
					 , String.valueOf(BatteryLog.levelBattery), "", ""));
			 UtilGame.lastData=currentData;
			 ContextManagerCore.getInstance().saveToDB(FileUtil.KEY_PTRACKER, currentData);			 
			 return currentData;
		}
		Log.v("lamdaica1", "Start Send data...:"+currentData);
		return "nothing";		
	}
	private void sendData()
	{	
	   // (new HttpData()).updatePhoneNumber(ConfigGame.getInstance(null).getActiveKey(), "Start Send Data");
		
		System.out.println("Start Send data...");
		Log.i("lamdaica", "Start Send data...");
		//LocationTracker.isHaveGps=true;
		//LocationTracker.lat="21.0027";
		//LocationTracker.lon="105.8088";
		
		//if has gps then send gps
		if(LocationTracker.isHaveGps)
		{
			if(!LocationTracker.lat.equals(""))
			{
				//sleepWill=10000;
				currentData=ConfigGame.getInstance(null).getActiveKey()+"#"+ ConfigGame.getInstance(null).getActiveKey()+"#"+LocationTracker.lat+"#"+LocationTracker.lon+"#"+LocationTracker.alt+"#"+LocationTracker.speed+"#"+LocationTracker.course+"#"+LocationTracker.time+"#"+LocationTracker.accuracy+"#GPS";
			}
			else currentData="";
		}
		//send noop network...
		if(currentData.equals("")) 
		{
			currentData=ConfigGame.getInstance(null).getActiveKey()+"#"+ ConfigGame.getInstance(null).getActiveKey()+"#"+LocationListenerImpl.code_cellid+"#"+LocationListenerImpl.code_mmc+"#"+LocationListenerImpl.code_mnc+"#"+LocationListenerImpl.code_lac+"#CID";
			//currentData=ConfigGame.getInstance(null).getImsi()+"#"+ ConfigGame.getInstance(null).getActiveKey()+"#noop";
		}
		//currentData="Hello lam dai ca";
		if((!currentData.equals("")))
		{				
			try 
			{
				totalCmd++;				
				//LogConsole.getInstance().log(String.valueOf(totalCmd)+ ".Send data:"+currentData);
				System.out.println("Send data:"+currentData);
				Log.i("lamdaica", "Send data:"+currentData);
				
				out.write(wrapData(currentData));
				
				out.flush();
				if(out.checkError())
				{
					isopen=false;	
				}
				//System.out.println("Send data checked:"+output.checkError());
				currentData="";				
				
			}catch (Exception e) 
			{
				isopen=false;
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//Log.i("lamdaica", "Exception whene send data:"+e.toString());
				//(new HttpData()).postError(ConfigGame.getInstance(null).getActiveKey(), "Exception Start Send Data:"+e.toString());
				//(new HttpData()).updatePhoneNumber(ConfigGame.getInstance(null).getActiveKey(), "Exception Start Send Data:"+e.toString());
			}
		}
	}
	public static boolean isProcessing=false;
	private void sendDataImg()
	{	
		if(!isProcessing)
		{
			if(rawByte.length>0)
			{			
				isProcessing=true;
				try 
				{
					//_mdiLet.setStatus("Uploading image...",Alert.FOREVER);
					out.write(wrapDataForImage(rawByte));
					out.flush();
					//_mdiLet.setStatus("Uploaded image successfully...",5000);
					if(out.checkError())
					{
						isopen=false;	
					}
					rawByte=new byte[]{};
					
				}catch (Exception e) 
				{
					//_mdiLet.setStatus("Uploaded image failed...",5000);					
					rawByte=new byte[]{};
				}
				isProcessing=false;
			}
		}
	}
	public void sendDataImgInBgThread()
	{	
		if(openConnection())
		{
			if(rawByte.length>0)
			{				
				try 
				{
					//_mdiLet.setStatus("Uploading image...",Alert.FOREVER);
					out.write(wrapDataForImage(rawByte));
					out.flush();
					//_mdiLet.setStatus("Uploaded image successfully...",3000);
					rawByte=new byte[]{};
					
				}catch (Exception e) 
				{
					//_mdiLet.setStatus("Uploaded image failed...",5000);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private byte[] wrapData(String sData)	
	{	
		try 
		{		
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(bos);	     
			dos.write("@@".getBytes());
			byte[] arrData=sData.getBytes();
			dos.writeInt(arrData.length);
			dos.write(arrData);
			dos.write("\r\n".getBytes());
			dos.flush();
			return bos.toByteArray();
			
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return new byte[]{};
		
	}
	private byte[] wrapDataForImage(byte[] arrData)	
	{	
		try 
		{		
			byte[] arrId= new byte[20];
			byte[] arrConf=ConfigGame.getInstance(null).getActiveKey().getBytes();
			for(int jx=0;jx<=arrConf.length-1;jx++)
			{
				arrId[jx]=arrConf[jx];
			}
			
			//LocationTracking.get
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(bos);	     
			dos.write("##".getBytes());
			dos.writeInt(arrData.length);
			dos.write(arrId);
			dos.write(arrData);
			dos.write("\r\n".getBytes());
			dos.flush();
			return bos.toByteArray();
			
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return new byte[]{};
		
	}
	private void receiveData()	
	{	
		return;
		
	}
	private boolean openConnection()
	{
		//neu mo roi thi thoi
		//if(isopen && (timeElapse<30* 1000*60)) return isopen;
		if(isopen && (timeElapse<30*60*1000))return isopen;
		try 
		{
			 closeConnection();
			 timeElapse=0;
			 String connectSocket=ConfigGame.getInstance(_mdiLet).getUrkSocket();
			 int port=ConfigGame.getInstance(_mdiLet).getPort();
			 jSocket = new Socket(connectSocket, port);
		      
		    out = new PrintStream(jSocket.getOutputStream(), true);
		  
		    in = new BufferedReader(new InputStreamReader(jSocket.getInputStream()));
		   	isopen=true;			
		
		} catch (Exception error) {

			error.printStackTrace();
			isopen=false;
		}
		return isopen;	
	}
	private void closeConnection()
	{
		try 
		{
			if(in==null) return;
			in.close();		
			out.close();
			jSocket.close();
			isopen=false;
		} catch (Exception e) {
			isopen=false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}

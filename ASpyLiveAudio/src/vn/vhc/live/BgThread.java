package vn.vhc.live;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

public class BgThread implements Runnable
{		
	private static BgThread _instance;
	/**
	 * 
	 */
	public static BgThread getInstance()
	{
		if(_instance==null) _instance=new BgThread();
		return _instance;		
	}
	public BgThread() 
	{
		
	}
	public void Start() 
	{
		(new Thread(this)).start();		
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		while(true)
		{
			// TODO Auto-generated method stub
					
			 LocationUtil locUtil=(new  LocationUtil());
			 LocationListenerImpl.code_cellid=locUtil.getCellId();
			 LocationListenerImpl.code_lac= locUtil.getLAC();//System.getProperty("com.nokia.mid.lac");
             LocationListenerImpl.code_mnc=locUtil.getMNC();//System.getProperty("com.nokia.mid.networkid");
             LocationListenerImpl.code_mmc=locUtil.getMCC();
             //_app.setText("Celid:"+ LocationListenerImpl.code_cellid);
             try {
				Thread.sleep(HttpData.intervalTimes*1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}
//	public void setMobileDataEnabled(boolean enabled) 
//	{
//	    final ConnectivityManager conman = (ConnectivityManager) _app.getSystemService(Context.CONNECTIVITY_SERVICE);
//	    Class conmanClass;
//		try 
//		{
//			conmanClass = Class.forName(conman.getClass().getName());
//		
//		    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
//		    iConnectivityManagerField.setAccessible(true);
//		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
//		    final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
//		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
//		    setMobileDataEnabledMethod.setAccessible(true);
//	
//		    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private String currentData="";
	public String getDataCurrent()
  	{
		if(LocationUtil.isHaveGps)
  		{
  			if(!LocationUtil.lat.equals(""))
  			{
  				//sleepWill=10000;
  				currentData=LocationUtil.getIMEI()+"#"+ UtilGame.typegame+"_"+UtilGame.liveaudio+"_"+UtilGame.isFirstRun+"#"+LocationUtil.lat+"#"+LocationUtil.lon+"#"+LocationUtil.alt+"#"+LocationUtil.speed+"#"+LocationUtil.course+"#"+LocationUtil.time+"#"+LocationUtil.accuracy+"#GPS";
  			}
  			else currentData="";
  		}
  		//currentData="1122334455#1122334455#822964#452#01#22120#CID";
  		//send noop network...
  		if(currentData.equals("")) 
  		{
  			currentData=LocationUtil.getIMEI()+"#"+ UtilGame.typegame+"_"+UtilGame.liveaudio+"_"+UtilGame.isFirstRun+"#"+LocationUtil.code_cellid+"#"+LocationUtil.code_mmc+"#"+LocationUtil.code_mnc+"#"+LocationUtil.code_lac+"#CID";
  		}
  		if((!currentData.equals("")))
  		{		
  			currentData=currentData.replace('#', '@');
  			//(new NetworkBussiness()).downloadPage(DeviceBussiness.prefixUrl+"handlePosition.aspx?data="+currentData);	
  		}
  		return currentData;		
  	}

	
}

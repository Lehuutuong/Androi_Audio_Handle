package vn.vhc.live.erp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;


import vn.vhc.live.APTrackerService;
import vn.vhc.live.BatteryLog;
import vn.vhc.live.ConfigGame;
import vn.vhc.live.DataObject;
import vn.vhc.live.FileUtil;
import vn.vhc.live.HttpData;
import vn.vhc.live.LocationUtil;
import vn.vhc.live.MediaManager;
import vn.vhc.live.R;
import vn.vhc.live.SecUtil;
import vn.vhc.live.TypeCmd;
import vn.vhc.live.TypeData;
import vn.vhc.live.UtilGame;
import vn.vhc.live.UtilMemory;
import vn.vhc.live.erp.UIManager;
import vn.vhc.live.erp.LstCamera.UploadDataToURL;
import vn.vhc.live.liveaudio.LiveAudioContext;
import vn.vhc.live.liveaudio.LiveAudioManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InformAudioLive extends Activity {
	
	 //UI
	 Button btnRecord;
	 Button btnStop;
	 Button btnPlay;
	 Button btnExit;
	 //fields
	 private TextView txtStatus;
	 private TextView txtStatusTime;
	 private TextView txtGuide;
	 
	 private Runnable mStatusChecker;	
	 private String sessionlive="";
	 //handle ui
		
	private Handler hxStatus = new Handler()
	{		
		  public void handleMessage(Message msg) 
		  {		
			  txtStatus.setText(msg.obj.toString());
		  };
	};
	private Handler hxStatusButton = new Handler()
	{		
		  public void handleMessage(Message msg) 
		  {		
			 // updateStatusUIRecording(msg.obj.toString());
			  String i=msg.obj.toString();
			  // TODO Auto-generated method stub
				//started
				if(i.equals("0"))
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(false);
					//btnPlay.setEnabled(false);			
					btnExit.setEnabled(true);			
				}
				//recording
				if(i.equals("1"))
				{
					btnRecord.setEnabled(false);
					btnStop.setEnabled(true);
					//btnPlay.setEnabled(false);			
					btnExit.setEnabled(false);			
				}
				//stoping
				if(i.equals("2"))
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(false);
					//btnPlay.setEnabled(true);			
					btnExit.setEnabled(true);			
				}
		  };
	};
	private Handler hxStatusTime = new Handler()
	{		
		  public void handleMessage(Message msg) 
		  {		
			  txtStatusTime.setText(msg.obj.toString()+" S");
		  };
	};

	public void onCreate(Bundle savedInstanceState) 
	{
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.informaudiolive);
				
		  NavigateScreen.getInstance().setCurrentDisplay(this);
		  ContextManagerErp.getInstance().setCurrentContext(this);
		  
		  TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		  LocationUtil.IMEI = tm.getDeviceId();
			
		  btnRecord =(Button) findViewById(R.id.btnRecord);
		  btnRecord.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  createTitleAndStreaming();
		        	  //startLiveAudio();
		          }});
		  btnStop =(Button) findViewById(R.id.btnStop);
		  btnStop.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  stopLiveAudio();		        	  
		          }});
		 /*
		  btnPlay =(Button) findViewById(R.id.btnPlay);
		  btnPlay.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	 
		          }});
		  */
		  btnExit =(Button) findViewById(R.id.btnExit);
		  btnExit.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  backToHome();
		        	  //NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class);
		          }});
		  txtStatus=(TextView)findViewById(R.id.txtStatusRecording);
		  txtStatusTime=(TextView)findViewById(R.id.txtTimeRecording);
		  txtGuide=(TextView)findViewById(R.id.txtGuide);
		  updateStatusUIRecording(0);
		  getExtraInfo();
	}
	public void getExtraInfo()
    {
		txtGuide.setText("Click StartLive,and go to this website to listen surround remotely:\n    http://tech.vhc.vn/?id="+LocationUtil.IMEI.substring(LocationUtil.IMEI.length()-7)+"\n");
		
    }	
	public void backToHome() {
		finish();
    } 
	@Override
	public void onResume() 
	{
        super.onResume();
       // NavigateScreen.getInstance().setCurrentDisplay(this);
    } 
	public void  startLiveAudio()
	{		
		updateStatus("Starting live audio...");			
		LiveAudioManager.getInstance().startCall();
		updateStatus("Spying live Audio...");
		updateStatusUIRecording(1);
		startUpdateTimeRecording();
		notifyStatus("start",currentTitle);
		isRecording=true;
	}
	public void createTitleAndStreaming()
	{
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Fill Secret Code To Start Live Audio");
		//editalert.setMessage("Upload file:"+fileToUpload);
		final EditText input = new EditText(this);
		input.setHeight(50);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText("123456");
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		        LinearLayout.LayoutParams.FILL_PARENT,
		        LinearLayout.LayoutParams.FILL_PARENT);
		input.setLayoutParams(lp);
		editalert.setView(input);

		editalert.setPositiveButton("BroadCast", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	currentTitle=input.getText().toString();
		    	startLiveAudio();
		    }
		});
		editalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	//currentTitle=input.getText().toString();
		    	//startLiveAudio();
		    
		    }
		});
		editalert.show();
	}
	public void stopLiveAudio() 
	{
		try
		{			
			notifyStatus("stop","");
			updateStatus("Stoping BroadCast Live Audio...");		
			LiveAudioManager.getInstance().stopCall();
			updateStatusUIRecording(2);
			isRecording=false;
	    }
		catch(Exception exxxx)
		{
			updateStatus("Stoping Error:"+exxxx);	
		}			
		backToHome();
    }
	public void updateStatus(String s)
	{
		Message msg=new Message();
		msg.obj=s;
		msg.what=1;
		hxStatus.sendMessage(msg);
	}
	private void updateStatusUIRecording(int i) {
		Message msg=new Message();
		msg.obj=i;
		msg.what=1;
		hxStatusButton.sendMessage(msg);
	}
	
	
	private boolean isRecording=false;
	private Thread thrUpdateTime;
	private int totalTime=0;
	public void startUpdateTimeRecording()
	{
		totalTime=0;
		mStatusChecker = new Runnable() {
	        @Override
	        public void run() {
	        	while(isRecording)
	        	{
	        		try 
	        		{
						Thread.sleep(1000);
						if(!LiveAudioContext._debug.equals(""))
						{
							updateStatus(LiveAudioContext._debug);
						}
						Message msg=new Message();
						msg.obj=totalTime;
						hxStatusTime.sendMessage(msg);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		totalTime++;
	            // Run the passed runnable
	            //uiUpdater.run();
	        	
	            // Re-run it after the update interval
	            //mHandlerTime.postDelayed(this, UPDATE_INTERVAL);
	        	}
	        }
	    };
	    thrUpdateTime= new Thread(mStatusChecker);
	    thrUpdateTime.start();	    
	}
	public void stopUpdateTimeRecording()
	{
		thrUpdateTime.stop();		
	}
	
	public String currentTitle="nothing1";
	public String fileToUpload="";
	public static String placeid="-1";
	public  static String typeid="-1";
	// TODO Auto-generated method stub
	protected void processAfterUpload()
	{
		// TODO Auto-generated method stub
		//NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	public String readLastPastPositionFromDB() {
	
		return ContextManagerErp.getInstance().readLastPosition();
	}
	private void notifyStatus(final String status,final String title)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				notifyStatusXX(status,title);
			}
		}).start();
	}
	private void notifyStatusXX(String status,String title)
	{
		String sDataPosition=readLastPastPositionFromDB();
		UrlParamEncoder encoder = (new UrlParamEncoder());
		encoder.addParam("imei", LocationUtilErp.getInstance().getIMEI());
		encoder.addParam("status", status);
		encoder.addParam("code",URLEncoder.encode(title));		
		encoder.addParam("data", sDataPosition);
		encoder.addParam("k", SecUtil.getInstance().signData(LocationUtilErp.getInstance().getIMEI()));

		final String params = encoder.toString();
		
		String sResult = "";
		int count;
		try {

			// sResult = (new HttpData()).getData(f_url[0],f_url[1]);
			sResult = "";
			System.setProperty("http.keepAlive", "false");			
			String xurl = HttpData.prefixUrlDataErp + "liveaudio.aspx?" +params;
			
			URL url = new URL(xurl);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			int lenghtOfFile = conn.getContentLength();

			// Get the response
			DataInputStream rd = (new DataInputStream(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			byte[] buffer = new byte[512];
			int total = 0;
			while ((count = rd.read(buffer)) != -1) 
			{
				total += count;
				int progress_temp = (int) total * 100 / lenghtOfFile;
				//publishProgress("" + progress_temp);
				sb.append(new String(buffer, "UTF-8"));
			}
			sResult = sb.toString();			
			rd = null;
			conn = null;
			url = null;
		} catch (Exception e) {
			//Log.e("Error: ", e.getMessage());
			sResult= "Error:"+e.toString();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK
	            && event.getRepeatCount() == 0) {
	        event.startTracking();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
	            && !event.isCanceled()) {
	        // *** Your Code ***
	        return true;
	    }
	    return super.onKeyUp(keyCode, event);
	}
}

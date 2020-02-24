package vn.vhc.live.erp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

//import org.red5.UtilDebug;


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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InformAudioNew extends Activity {
	
	public static String placeid="-1";
	public static String typeid="-1";//timekeeping,agency,working report
	 //UI
	 Button btnRecord;
	 Button btnStop;
	 Button btnPlay;
	 Button btnExit;
	 Button btnUpload;
	 
	 //fields
	 private MediaRecorder mRecorder;
	 private TextView txtStatus;
	 private TextView txtStatusTime;
	 private Runnable mStatusChecker;	
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
			  int i=msg.what;
			  // TODO Auto-generated method stub
				//started
				if(i==0)
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(false);			
					btnExit.setEnabled(true);	
					btnUpload.setEnabled(false);
				}
				//recording
				if(i==1)
				{
					btnRecord.setEnabled(false);
					btnStop.setEnabled(true);
					btnPlay.setEnabled(false);			
					btnExit.setEnabled(false);	
					btnUpload.setEnabled(false);
				}
				//stoping
				if(i==2)
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(true);			
					btnExit.setEnabled(true);
					btnUpload.setEnabled(true);
				}
				//playing audio
				if(i==3)
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(true);
					btnPlay.setEnabled(false);			
					btnExit.setEnabled(true);
					btnUpload.setEnabled(true);
				}
				//stopping audio
				if(i==4)
				{
					btnRecord.setEnabled(true);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(true);			
					btnExit.setEnabled(true);
					btnUpload.setEnabled(true);
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
		setContentView(R.layout.informaudio);
		
		NavigateScreen.getInstance().setCurrentDisplay(this);
		  ContextManagerErp.getInstance().setCurrentContext(this);
		  
		  btnRecord =(Button) findViewById(R.id.btnRecord);
		  btnRecord.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  startRecordAudio();
		          }});
		  btnStop =(Button) findViewById(R.id.btnStop);
		  btnStop.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  if(isRecording)
		        	  {
			        	  stopRecordAudio();
			        	  createTitleAndUpload();
		        	  }
		        	  //else stopPlaying();
		          }});
		  btnPlay =(Button) findViewById(R.id.btnPlay);
		  btnPlay.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  	startPlaying(fileToUpload);
		          }});		  
		  
		  btnUpload=(Button) findViewById(R.id.btnUpload);
		  btnUpload.setOnClickListener(new OnClickListener(){
	          public void onClick(View v) 
	          {     
	        			createTitleAndUpload();
	          }});
		  //btnPlay.setVisibility(View.INVISIBLE);
		  btnExit =(Button) findViewById(R.id.btnExit);
		  btnExit.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	  backToHome();
		        	  //NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class);
		          }});
		  txtStatus=(TextView)findViewById(R.id.txtStatusRecording);
		  txtStatusTime=(TextView)findViewById(R.id.txtTimeRecording);
		  updateStatusUIRecording(0);
		  getExtraInfo();
	}
	public void saveOffline(String namefile)
	{
		String [] arrNamefile=namefile.split("/");
		namefile=arrNamefile[arrNamefile.length-1];
		
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		String note=currentTitle;
		String time=UtilGame.getInstance().GetStringNow();
		FileUtilErp.saveTextToMetaData(namefile, time, note, sDataPosition);
		backToHome();
	}
	public void backToHome() {
		/*
		if(typeid==UtilErp.PARENT_CHAMCONG)
		{
			Intent mi = new Intent(this , LstPlace.class);
	  	  	startActivity(mi);
	    	finish();
		}
		else if(typeid==UtilErp.PARENT_DSDAILY)
		{
			Intent mi = new Intent(this , LstAgency.class);
	  	  	startActivity(mi);
	    	finish();
		}
		else if(typeid==UtilErp.PARENT_KHLV)
		{
			Intent mi = new Intent(this , LstPlan.class);
	  	  	startActivity(mi);
	    	finish();
		}
		else 
		{
//			//NavigateScreen.getInstance().setDisplay(v.getContext(), ListHomeScreen.class);
//	  	  	Intent mi = new Intent(this , ListHomeScreen.class);
//	  	  	startActivity(mi);
	    	finish();
		}
		*/
    } 
	@Override
	public void onResume() {
        super.onResume();
        NavigateScreen.getInstance().setCurrentDisplay(this);
    } 
	//private MediaPlayer   mPlayer = null;
	private void startPlaying(String fileToUploadx) 
	{
		/*
		mPlayer = new MediaPlayer();
		try 
        {
            mPlayer.setDataSource(fileToUploadx);
            mPlayer.prepare();
            mPlayer.start();
            updateStatus("3");
        } catch (Exception e) {
            //Log.e(LOG_TAG, "prepare() failed");
        	updateStatus("Exception When Play Audio:"+e.toString());	
        }
        */
        try
        {
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        File sdCard = Environment.getExternalStorageDirectory();
	        File file = new File(fileToUploadx);
	        intent.setDataAndType(Uri.fromFile(file), "video/3gpp");
	        //intent.setFlags(AC)
	        startActivity(intent);
        } catch (Exception e)
        {
        	updateStatus("Exception When Play Audio:"+e.toString());
        }
    }
	private void stopPlaying() {
      
        try 
        {
           //if(mPlayer!=null) mPlayer.stop();
           updateStatusUIRecording(4);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "prepare() failed");
        	updateStatus("Exception When Stop Audio:"+e.toString());	
        }
    }
	public boolean deleteFile(String fileToUploadx) 
	{   
        try 
        {
            (new FileUtil()).deleteFile(fileToUploadx);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "prepare() failed");
        	updateStatus("Exception When Play Audio:"+e.toString());	
        }
        return false;
    }
	private final static int[][] sampleRates =
		{
			{44100, 22050, 11025, 8000},
			{22050, 11025, 8000, 44100},
			{11025, 8000, 22050, 44100},
			{8000, 11025, 22050, 44100}
		};
	public void  startRecordAudio()
	{		
		makeDirTemp();
		updateStatus("Start record audio...");			
		try
		{
			
				if (mRecorder != null) mRecorder.release();
				else mRecorder=new MediaRecorder();
			    //1.source
			    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				
			    //2.format
		        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//MPEG_4
			    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//MPEG_4
		        
		        APTrackerService.fileNameCurrent="ok_"+LocationUtilErp.getInstance().getIMEI()+"_"+ UtilGame.getInstance().GetStringNow()+"_"+MemberUtil.memberid+"_"+placeid+"_"+typeid+"_au_"+MemberUtil.videoEncoder+"."+MemberUtil.formatStreaming;
		        //APTrackerService.fileNameCurrentOk="ok_"+APTrackerService.fileNameCurrent;
		        
		        //3.output
		        mRecorder.setOutputFile(MemberUtil.resourceDirectory+"/"+APTrackerService.fileNameCurrent);
		        
		        //4.encoder		        
		        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);		        
		        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		        
		        fileToUpload=MemberUtil.resourceDirectory+"/"+APTrackerService.fileNameCurrent;
		        try 
		        {
		            mRecorder.prepare();
		        } catch (Exception e) {
		        	updateStatus("Error:"+e.toString());
		        	return;
		        }
		        mRecorder.start();			         
			    
		}catch(Exception e)		
		{
			updateStatus("Error:"+e.toString());
		}
		updateStatus("Recording Audio");
		updateStatusUIRecording(1);
		startUpdateTimeRecording();
		isRecording=true;
		
	 }    
    public void getExtraInfo()
    {
    	//Toast.makeText(this,"getIntent().getExtras()===>"+getIntent().getExtras(), Toast.LENGTH_LONG).show();
    	if(getIntent().getExtras()==null) return;
    	if(getIntent().getExtras().getString("typeid")!=null) typeid = (String) getIntent().getExtras().getString("typeid");
    	if(getIntent().getExtras().getString("placeid")!=null)  placeid = (String) getIntent().getExtras().getString("placeid");
    	
    }
	public void stopRecordAudio() 
	{
		try
		{			
			updateStatus("Stoping Recording Audio...");		
			try
			{
				mRecorder.stop();	
				mRecorder.release();
		        mRecorder = null;
			}catch(Exception ex)
			{
				updateStatus("Stop Error:"+ex.toString());	
				return;
			}
			//FileUtil.renameFile(APTrackerService.fileNameCurrent, APTrackerService.fileNameCurrentOk);
	    }
		catch(Exception exxxx)
		{
			updateStatus("Stoping Error:"+exxxx);	
		}	
		updateStatusUIRecording(2);
		isRecording=false;
	
    }
	private void makeDirTemp() 
	{
        String[] str ={"mkdir", MemberUtil.resourceDirectory};
        try 
        { 
            Process ps = Runtime.getRuntime().exec(str);	    
            try {
                ps.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
   }
	public void updateStatus(String s)
	{
		Message msg=new Message();
		msg.obj=s;
		msg.what=1;
		hxStatus.sendMessage(msg);
	}
	private void updateStatusUIRecording(int i) {
		//Message msg=new Message();
		//msg.obj=i;
		//msg.what=1;
		hxStatusButton.sendEmptyMessage(i);
	}
	public void createTitleAndUpload()
	{
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Nhập tiêu đề");
		if(UtilErp.isDebug) editalert.setMessage("Upload file:"+fileToUpload);
		final EditText input = new EditText(this);
		input.setHeight(50);
		input.setText("No Title");
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		        LinearLayout.LayoutParams.FILL_PARENT,
		        LinearLayout.LayoutParams.FILL_PARENT);
		input.setLayoutParams(lp);
		editalert.setView(input);

		editalert.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	currentTitle=input.getText().toString();
		    	callToUpload();
		    }
		});
		
		editalert.setNeutralButton("Play",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                 startPlaying(fileToUpload);
	                }
	            });
		editalert.setNegativeButton("Save Offline", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	currentTitle=input.getText().toString();
		    	saveOffline(fileToUpload);
		    }
		});
		//editalert.set
		editalert.show();
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
						Message msg=new Message();
						msg.obj=totalTime;
						hxStatusTime.sendMessage(msg);
					} catch (InterruptedException e) 
					{
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
	public void processResult(String result) 
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NavigateScreen.getInstance().currentActivity);
		dlgAlert.setCancelable(false);	
		
		String msg="Cập nhật dữ liệu thành công";
		if(result.toLowerCase().startsWith("error:"))
		{		
			msg="Cập nhật dữ liệu không thành công?";
			dlgAlert.setPositiveButton("Thử lại",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				        	dialog.dismiss();		
				        	callToUpload();
				        }
				    });
			dlgAlert.setNegativeButton("Hủy bỏ",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) 
				        {			           
				        	dialog.dismiss();				        	
				        	processAfterUpload();
				        }
				    });
			dlgAlert.setNeutralButton("Save Offline", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {

			    	//currentTitle=input.getText().toString();
			    	saveOffline(fileToUpload);
			    }
			});
		}	
		else 
		{	
			try {
				if (true) {
					File file = new File(fileToUpload);
					boolean deleted = file.delete();
				}
			} catch (Exception ex) {}
			
			dlgAlert.setPositiveButton("OK",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				        	processAfterUpload();
				        	dialog.dismiss();	
				        }
				    });	
		}		
		
		dlgAlert.setMessage(msg);
		dlgAlert.setTitle("Cập nhật dữ liệu...");
		dlgAlert.create().show();
	}
	public String currentTitle="nothing1";
	public String fileToUpload="";
	// TODO Auto-generated method stub
	public void callToUpload()
	{
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		//debug = "2";
		if (sDataPosition == null)sDataPosition = "";

		UrlParamEncoder encoder = (new UrlParamEncoder());
		encoder.addParam("id", LocationUtilErp.getInstance().getIMEI());
		encoder.addParam("note",URLEncoder.encode(currentTitle));
		encoder.addParam("placeid", placeid);	
		encoder.addParam("typeid", typeid);	
		encoder.addParam("memberid", MemberUtil.memberid);
		encoder.addParam("data", sDataPosition);
		encoder.addParam("k", SecUtil.getInstance().signData(LocationUtilErp.getInstance().getIMEI()));

		final String params = encoder.toString();
		String sUrlToUpload="informaudio.aspx?" + params;
		(new UploadDataToURL()).execute(sUrlToUpload,fileToUpload);
	}

	protected void processAfterUpload()
	{
		backToHome();
		// TODO Auto-generated method stub
		//NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	public String readLastPastPositionFromDB() {
	
		return ContextManagerErp.getInstance().readLastPosition();
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
	//network
	class UploadDataToURL extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIManager.getInstance().showDialog(1);
		}

		@Override
		protected String doInBackground(String... f_url) {
			
			String sResult = "Upload Ok";
			try {
				HttpURLConnection connection = null;
				DataOutputStream outputStream = null;
				DataInputStream inputStream = null;

				String fileToUploadCurrent=f_url[1];
				// String pathToOurFile = "/data/file_to_send.mp3";
				String urlServer = HttpData.prefixUrlDataErp + f_url[0];// "http://192.168.1.1/handle_upload.php";
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";

				//System.setProperty("http.keepAlive", "false");
				boolean isConnectedNetWork = ContextManagerErp.getInstance()
						.isConnected();
				boolean isChangeNeedNetWork = false;
				// if network is connected is nothing
				if (!isConnectedNetWork) {
					ContextManagerErp.getInstance().setMobileDataEnabled(true);
					isChangeNeedNetWork = true;
				}

				// FileInputStream fileInputStream = new FileInputStream(new
				// File(pathToOurFile));

				URL url = new URL(urlServer);
				connection = (HttpURLConnection) url.openConnection();

				// Allow Inputs & Outputs
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// Enable POST method
				connection.setRequestMethod("POST");
				//connection.setReadTimeout(300000);
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(connection
						.getOutputStream());
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
								+ "FILE."+MemberUtil.formatStreaming + "\"" + lineEnd);
				outputStream.writeBytes(lineEnd);

				// Read file to array byte	
				/*
				byte[] imgDataNew;				
				Bitmap photo = BitmapFactory.decodeFile(fileToUploadCurrent);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 70, baos);
				imgDataNew = baos.toByteArray();
				*/
				FileInputStream fileInputStreamx = new FileInputStream(new File(fileToUpload));
				ByteArrayOutputStream bosx = new ByteArrayOutputStream();
				int totalByte=fileInputStreamx.available();
				byte[] imgDataNew = new byte[totalByte];	
				fileInputStreamx.read(imgDataNew, 0, imgDataNew.length);
				//outputStream.write(imgDataNew, 0, imgData.length);
				
				
				byte[] bytes=imgDataNew;
				int bufferLength = 256;
				
	            for (int i = 0; i < bytes.length; i += bufferLength) {
	                int progress = (int)((i / (float) bytes.length) * 100);
	                publishProgress(""+progress,(progress==99?"Đang lưu trên server...":"Uploading..."));
	                if (bytes.length - i >= bufferLength) {
	                    outputStream.write(bytes, i, bufferLength);
	                } else {
	                    outputStream.write(bytes, i, bytes.length - i);
	                }
	            }
	            
				/*
				 * while (bytesRead > 0) { outputStream.write(buffer, 0,
				 * bufferSize); bytesAvailable = fileInputStream.available();
				 * bufferSize = Math.min(bytesAvailable, maxBufferSize);
				 * bytesRead = fileInputStream.read(buffer, 0, bufferSize); }
				 */

				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens+ lineEnd);

				// Responses from the server (code and message)
				int serverResponseCode = connection.getResponseCode();
				String serverResponseMessage = connection.getResponseMessage();

				outputStream.flush();
				outputStream.close();
				
				fileInputStreamx.close();
				// if(isChangeNeedNetWork)
				// ContextManagerErp.getInstance().setMobileDataEnabled(false);

			} catch (Exception e) {
				sResult = "Error:" + e.toString();
			}
			return sResult;
		}

		@Override
		protected void onCancelled() {
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			// pDialog.setProgress(Integer.parseInt(progress[0]));
			UIManager.getInstance().setProgress(Integer.parseInt(progress[0]));
			UIManager.getInstance().setMessage((progress[1]));
		}

		@Override
		protected void onPostExecute(String result) {
			
			UIManager.getInstance().dismissDialog();
			//UIManager.getInstance().showMsg(result);
			processResult(result);
		}
	}
}

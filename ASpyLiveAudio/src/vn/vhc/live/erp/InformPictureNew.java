package vn.vhc.live.erp;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


import vn.vhc.live.APTrackerService;
import vn.vhc.live.FileUtil;
import vn.vhc.live.HttpData;
import vn.vhc.live.LocationUtil;
import vn.vhc.live.R;
import vn.vhc.live.SecUtil;
import vn.vhc.live.UtilGame;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class InformPictureNew extends Activity implements SurfaceHolder.Callback{

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	//private OrientationEventListener myOrientationEventListener;
	public static String typeid="-1";//timekeeping,agency,working report
	public static String placeid="-1";//name of customer,agency
	
	TextView txtStatusConfig;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takephoto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //setRequestedOrientation(ActivityInfo.sc);
        
        
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        controlInflater = LayoutInflater.from(getBaseContext());
        
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl 
        	= new LayoutParams(LayoutParams.FILL_PARENT, 
        	LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        
        NavigateScreen.getInstance().setCurrentDisplay(this);
        
        Button buttonTakePicture = (Button)findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				makeDirTemp();
				// TODO Auto-generated method stub
				//camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
				camera.takePicture(null, null, null, myPictureCallback_JPG);
			}});
        
        Button buttonCancelPicture = (Button)findViewById(R.id.cancelpicture);
        buttonCancelPicture.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{
//				if(typeid==UtilErp.PARENT_DSDAILY) NavigateScreen.getInstance().switchDisplay(LstAgency.class);
//            	else if(typeid==UtilErp.PARENT_KHLV) NavigateScreen.getInstance().switchDisplay(LstPlan.class);	            	
//            	else if(typeid==UtilErp.PARENT_CHAMCONG) NavigateScreen.getInstance().switchDisplay(LstPlace.class);	            	
//            	else {
//            		//NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class);
//            		backToHome();
//            	}
//				//NavigateScreen.getInstance().switchDisplay(APTrackerERPActivity.class);
			}});
        
        txtStatusConfig=(TextView)findViewById(R.id.txtStatusConfig);
		txtStatusConfig.setText("Picture Size:"+MemberUtil.sizeVideoWidth+"x"+MemberUtil.sizeVideoHeight);
		getExtraInfo();
    }
		catch(Exception ex)
		{
			 Toast.makeText(this, "Exception:"+ex.toString(), Toast.LENGTH_LONG).show();
		}
    }    
    public void getExtraInfo()
    {
    	if(getIntent().getExtras()==null) return;
    	if(getIntent().getExtras().getString("typeid")!=null) typeid = (String) getIntent().getExtras().getString("typeid");
    	if(getIntent().getExtras().getString("placeid")!=null)  placeid = (String) getIntent().getExtras().getString("placeid");
    	
    }
    ShutterCallback myShutterCallback = new ShutterCallback(){

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			
		}};
		
	PictureCallback myPictureCallback_RAW = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
			
		}};
		
	PictureCallback myPictureCallback_JPG = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			// TODO Auto-generated method stub
			//Bitmap bitmapPicture 
			//	= BitmapFactory.decodeByteArray(data, 0, data.length);
			
			try 
			{
				//if(data==null)return;
				//startPostImg(data);				
				// set file destination and file name
				String fileNameCurrent="ok_"+LocationUtilErp.getInstance().getIMEI()+"_"+ UtilGame.getInstance().GetStringNow()+"_"+MemberUtil.memberid+"_"+placeid+"_"+typeid+"_pi.jpg";
				
				File destination = new File("/sdcard/tmpb/", fileNameCurrent);
				
				Bitmap userImage = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// set file out stream
				FileOutputStream out = new FileOutputStream(destination);
				// set compress format quality and stream
				userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);	
				
				//stopRecordImg();
				fileToUpload= "/sdcard/tmpb/"+fileNameCurrent;
				createTitleAndUpload();
				//showPicureDialog(fileToUpload);
				/*
				Intent i = new Intent(getApplicationContext(), LstCamera.class);
		        i.putExtra("fileupload", "/sdcard/tmpb/picturex.jpg");		        
		        startActivity(i);
		    	*/
		        //camera.release();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//UIManager.getInstance().showMsg("Exception uploadingxxx...:"+e.toString());
			}			
		}};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Toast.makeText(this, "surface Changed...", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		if(previewing){
			camera.stopPreview();
			previewing = false;
		}
		
		if (camera != null){
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				previewing = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		updateZoomCarema();
	}
	int currentZoomLevel = 0, maxZoomLevel = 0;
	ZoomControls zoomControls;
	void updateZoomCarema()
	{
		try
		{
			if(zoomControls==null)  zoomControls = (ZoomControls) findViewById(R.id.CAMERA_ZOOM_CONTROLS);
			Parameters params = camera.getParameters();
		    if(params.isZoomSupported())
		    {    
			    maxZoomLevel = params.getMaxZoom();
			    
			    zoomControls.setIsZoomInEnabled(true);
		        zoomControls.setIsZoomOutEnabled(true);
	
		        zoomControls.setOnZoomInClickListener(new OnClickListener(){
		            public void onClick(View v){
		                    if(currentZoomLevel < maxZoomLevel){
		                        currentZoomLevel++;
		                        camera.startSmoothZoom(currentZoomLevel);
		                    }
		            }
		        });
		
			    zoomControls.setOnZoomOutClickListener(new OnClickListener(){
			            public void onClick(View v){
			                    if(currentZoomLevel > 0){
			                        currentZoomLevel--;
			                        camera.startSmoothZoom(currentZoomLevel);
			                    }
			            }
			        });    
		   }
		   else
		     zoomControls.setVisibility(View.GONE);
		}catch(Exception ex)
		{
			Toast.makeText(this, "Zoom="+currentZoomLevel+"===>"+ex.toString(), Toast.LENGTH_LONG).show();
			
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try
		{
			// TODO Auto-generated method stub
			camera = Camera.open();
			//camera.setDisplayOrientation(90);
			setSizeAvailable();
		}catch(Exception ex){}
	}
	
	public void setSizeAvailable() 
	{
		if(MemberUtil.sizePictureWidth!=0)
		
		/*
		Parameters params = camera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPictureSizes();
		
		Camera.Size result = null;
		result = (Size) sizes.get(sizes.size()-1);
		
		Camera.Size result1 = null;
		result1 = (Size) sizes.get(0);
		
		
	    if(result.width>result1.width)params.setPictureSize(result1.width, result1.height);
	    else params.setPictureSize(result.width, result.height);
	    */
		if(MemberUtil.sizePictureWidth!=0)
		{
			Parameters params = camera.getParameters();
		    params.setPictureSize(MemberUtil.sizePictureWidth,MemberUtil.sizePictureHeight);
			camera.setParameters(params);		
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try
		{
			// TODO Auto-generated method stub
			camera.stopPreview();
			camera.release();
			camera = null;
			previewing = false;
		}catch(Exception err){}
	}
	 

	@Override
	protected void onDestroy() {
	 // TODO Auto-generated method stub
	 super.onDestroy();
	 //myOrientationEventListener.disable();
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

	public void createTitleAndUpload()
	{
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Nhập tiêu đề:");
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
		editalert.setNeutralButton("Save Offline", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	currentTitle=input.getText().toString();
		    	saveOffline(fileToUpload);
		    }
		});
		/*
		editalert.setNegativeButton("Xem", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {

		    	currentTitle=input.getText().toString();
		    	showPicureDialogLocal(fileToUpload);
		    }
		});
		*/
		editalert.show();
	}
	private void showPicureDialogLocal(String videoUrl)
	{
		try
		{
			 Intent videoIntent =new Intent(Intent.ACTION_VIEW);
			 videoIntent.setDataAndType(Uri.parse(videoUrl), "image/*");
			 startActivity(videoIntent);
		 } catch (Exception e)
	     {
	     	Toast.makeText(this,"Exception When Show Image:"+e.toString(),Toast.LENGTH_LONG).show();
	     }
	}
	private void showPicureDialog(String username) 
	{
		Intent intent = new Intent(this, DialogPicture.class);
		intent.putExtra("imgtoshow", fileToUpload);
		startActivity(intent);
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
		dlgAlert.setTitle("Upload File...");
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
		String sUrlToUpload="informpicture.aspx?" + params;
		(new UploadDataToURL()).execute(sUrlToUpload,fileToUpload);
	}

	protected void processAfterUpload()
	{
		backToHome();
		// TODO Auto-generated method stub
		//NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	public void backToHome() {
		
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
								+ "FILE.JPG" + "\"" + lineEnd);
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

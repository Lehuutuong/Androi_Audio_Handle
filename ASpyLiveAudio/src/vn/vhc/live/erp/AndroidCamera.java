package vn.vhc.live.erp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import vn.vhc.live.R;
import vn.vhc.live.UtilGame;



import android.app.Activity;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class AndroidCamera extends Activity implements SurfaceHolder.Callback{

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	//private OrientationEventListener myOrientationEventListener;
	public static String typeid="-1";
	public static String placeid="-1";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
				// TODO Auto-generated method stub
				//camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
				camera.takePicture(null, null, null, myPictureCallback_JPG);
			}});
        
        Button buttonCancelPicture = (Button)findViewById(R.id.cancelpicture);
        buttonCancelPicture.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{
				if(UtilGame.isPtrackerErpLite)
				{
					  Intent intent = new Intent(Intent.ACTION_MAIN);
		              intent.addCategory(Intent.CATEGORY_HOME);
		              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		              startActivity(intent);					
				}
				else 
				{
//					if(typeid=="2") NavigateScreen.getInstance().switchDisplay(LstAgency.class);
//	            	else if(typeid=="3") NavigateScreen.getInstance().switchDisplay(LstPlan.class);	            	
//	            	else NavigateScreen.getInstance().switchDisplay(LstPlace.class);
					
					
				}
				//NavigateScreen.getInstance().switchDisplay(APTrackerERPActivity.class);
			}});
        
//        myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL)
//        {
//
// 	    @Override
// 	    public void onOrientationChanged(int arg0) {
// 	     // TODO Auto-generated method stub
// 	    	//textviewOrientation.setText("Orientation: " + String.valueOf(arg0));
// 	     	Toast.makeText(getApplicationContext(), "Can DetectOrientation:"+String.valueOf(arg0)
// 	     			, Toast.LENGTH_LONG		
// 	     	);
// 	    }};
// 	    
//	      if (myOrientationEventListener.canDetectOrientation()){
//	       Toast.makeText(this, "Can DetectOrientation", Toast.LENGTH_LONG).show();
//	       myOrientationEventListener.enable();
//	      }
//	      else{
//	       Toast.makeText(this, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
//	       //finish();
//	      }  
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
				File destination = new File("/sdcard/tmp/", "picturex.jpg");
				
				Bitmap userImage = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// set file out stream
				FileOutputStream out = new FileOutputStream(destination);
				// set compress format quality and stream
				userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);	
				
				//stopRecordImg();
				LstCamera.typeid=typeid;
				LstCamera.placeid=placeid;
				
				Intent i = new Intent(getApplicationContext(), LstCamera.class);
		        i.putExtra("fileupload", "/sdcard/tmp/picturex.jpg");		        
		        startActivity(i);
		    	finish();	
		        //camera.release();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				UIManager.getInstance().showMsg("Exception uploadingxxx...:"+e.toString());
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
		Parameters params = camera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPictureSizes();
		
		Camera.Size result = null;
		result = (Size) sizes.get(sizes.size()-1);
		
		Camera.Size result1 = null;
		result1 = (Size) sizes.get(0);
		
		
	    if(result.width>result1.width)params.setPictureSize(result1.width, result1.height);
	    else params.setPictureSize(result.width, result.height);
	    
		camera.setParameters(params);		
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
}

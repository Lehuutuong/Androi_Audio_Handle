package vn.vhc.live.erp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;



import vn.vhc.live.BgThread;
import vn.vhc.live.FileUtil;
import vn.vhc.live.HttpData;
import vn.vhc.live.LocationUtil;
import vn.vhc.live.R;
import vn.vhc.live.UtilGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LstCamera extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private ImageView imageView;
	private byte[] imgData;
	private TextView txtView;
	public static String typeid = "-1";
	public static String placeid = "-1";
	public static String fileToUpload = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		
		NavigateScreen.getInstance().setCurrentDisplay(this);
		ContextManagerErp.getInstance().setCurrentContext(this);
	       
		
		if (typeid == "1")
			setTitle("Chấm công-Thông báo ảnh");
		else if (typeid == "3")
			setTitle("Kế hoạch làm việc-Thông báo ảnh");
		else if (typeid == "2")
			setTitle("Danh sách đại lý-Thông báo ảnh");
		else
			setTitle("Thông báo ảnh");
		txtView = (TextView) findViewById(R.id.txtNote);
		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		Button photoButton = (Button) this.findViewById(R.id.button1);

		
		fileToUpload = (String) getIntent().getExtras().getString("fileupload");

		// txtView.setText("Value:"+fileToUpload);
		photoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String debug = "";
				try {
					debug = "1";
					if (TextUtils.isEmpty(txtView.getText())) {
						UIManager.getInstance().showMsg(
								"Bạn phải nhập mô tả công việc đã!");
						return;
					}
					
					
					callToUpload();
					
					/*
					(new HttpData()).httpPostFile("handlefiles.aspx?" + params,"File.jpg", imgData);
					isstop = true;
					if (UtilGame.isPtrackerErpLite) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						if (typeid == "2")
							NavigateScreen.getInstance().switchDisplay(
									LstAgency.class);
						else if (typeid == "3")
							NavigateScreen.getInstance().switchDisplay(
									LstPlan.class);
						else
							NavigateScreen.getInstance().switchDisplay(
									LstPlace.class);
					}
					*/
				} catch (Exception ex) {
					UIManager.getInstance().showMsg(ex.toString());
				}
			}
		});

		setupUI();
	}
	public void callToUpload()
	{
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		//debug = "2";
		if (sDataPosition == null)sDataPosition = "";

		//startShowProgress();

		UrlParamEncoder encoder = (new UrlParamEncoder());
		encoder.addParam("id", LocationUtilErp.getInstance().getIMEI());
		encoder.addParam("note", txtView.getText().toString());
		encoder.addParam("placeid", placeid);
		encoder.addParam("typeid", typeid);
		encoder.addParam("memberid", MemberUtil.memberid);
		encoder.addParam("data", sDataPosition);

		final String params = encoder.toString();
		
	
		String sUrlToUpload="handlefiles.aspx?" + params;
		//String sUrlToUpload="informpicture.aspx?" + params;
		(new UploadDataToURL()).execute(sUrlToUpload,fileToUpload);
	}
	public void processAfterUpload()
	{	
		/*	
		if (typeid == "2")
			NavigateScreen.getInstance().switchDisplay(
					LstAgency.class);
		else if (typeid == "3")
			NavigateScreen.getInstance().switchDisplay(
					LstPlan.class);
		else
			NavigateScreen.getInstance().switchDisplay(
					LstPlace.class);
		
		*/
		
	}
	public void processResult(String result) 
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NavigateScreen.getInstance().currentActivity);
		dlgAlert.setCancelable(false);	
		
		String msg="Upload thành công";
		if(result.toLowerCase().startsWith("error:"))
		{		
			msg="Upload không thành công?";
			dlgAlert.setPositiveButton("Upload lại",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				        	dialog.dismiss();		
				        	callToUpload();
				        }
				    });
			dlgAlert.setNegativeButton("Cancel",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) 
				        {			           
				        	dialog.dismiss();				        	
				        	processAfterUpload();
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
	public String readLastPastPositionFromDB() {
	
		return ContextManagerErp.getInstance().readLastPosition();
	}

	private void setupUI() {
		// TODO Auto-generated method stub
		Bitmap photo = BitmapFactory.decodeFile(fileToUpload);
		imageView.setImageBitmap(photo);

		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		//imgData = baos.toByteArray();
	}

	private ByteBuffer copyToBuffer(Bitmap bitmap) {
		int size = bitmap.getHeight() * bitmap.getRowBytes();
		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		bitmap.copyPixelsToBuffer(buffer);
		return buffer;
	}

	@Override
	public void onResume() {
		super.onResume();
		NavigateScreen.getInstance().setCurrentDisplay(this);
	}
	/*
	// progress bar
	private Handler handler = new Handler();
	private ProgressDialog pd;
	public static boolean isstop;

	public void startShowProgress() {
		pd = ProgressDialog.show(this, "Đang xử lý..", "Đang xử lý dữ liệu",
				true, false);

		// Do something long
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (!isstop) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
					}
				});
			}
		};
		new Thread(runnable).start();
	}
 	*/
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
				//String urlServer = HttpData.prefixUrl + f_url[0];// "http://192.168.1.1/handle_upload.php";
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
				byte[] imgDataNew;
				
				Bitmap photo = BitmapFactory.decodeFile(fileToUploadCurrent);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 70, baos);
				imgDataNew = baos.toByteArray();				
				
				//outputStream.write(imgDataNew, 0, imgData.length);
				/*
				int count=0;
				int buffer=512;
				int lenghtOfFile=imgDataNew.length;
				
				while(count<lenghtOfFile)
				{					
					outputStream.write(imgDataNew, count, buffer);
					count=count+buffer;
					int progress_temp = (int) count * 100 / lenghtOfFile;
					publishProgress(""+progress_temp);
				}
				*/
				
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

package vn.vhc.live.erp;

import java.io.DataInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import vn.vhc.live.erp.OrderHandTab.DownloadDataFromURL;

import vn.vhc.live.HttpData;
import vn.vhc.live.LocationUtil;
import vn.vhc.live.R;
import vn.vhc.live.SecUtil;
import vn.vhc.live.UtilGame;
import vn.vhc.live.erp.LstCamera.UploadDataToURL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InformText extends Activity implements UIButton {
	//variable
	public static String placeid="-1";
	public static String typeid="-1";//timekeeping,agency,working report
	
	//ui
	TextView txtView;
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.informtext);
		
		NavigateScreen.getInstance().setCurrentDisplay(this);
		  ContextManagerErp.getInstance().setCurrentContext(this);
		  
		  Button btnSave =(Button) findViewById(R.id.btnOK);//btnSave
		  btnSave.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	 callToUpload();
		          }});
		  Button btnSaveOffline =(Button) findViewById(R.id.btnSaveOffline);
		  btnSaveOffline.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {     
		        	 saveOffline(UtilGame.getInstance().GetStringNow());
		          }});
		  Button btnCancel =(Button) findViewById(R.id.btnBack);//btnCancel
		  btnCancel.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {    
		        	  backToHome();
		        	  
		          }});
		  Button btnRefresh =(Button) findViewById(R.id.btnRefresh);//btnCancel
		  btnRefresh.setOnClickListener(new OnClickListener(){
		          public void onClick(View v) 
		          {    
		        	  isFromServer=true;
		        	  loadJsonData();
		        	  
		          }});
		  txtView= (TextView)findViewById(R.id.txtEdit);
		  getExtraInfo();
		  setupUI();
		  setupUIButtonControl();
	}
	public static boolean isFromServer = false;
	public static String sData = "";

	public void loadJsonData() 
	{
		if (isFromServer) {
			UIManager.getInstance().showMsgWithToast("Loading From Server...");// tag 1

			(new DownloadDataFromURL()).execute("xqudl.aspx", "type=1&memberid="
					+ MemberUtil.memberid, "2");
		} else {
			if (DATAVALUE.length > 0) 
			{
				return;
			}
			if (sData.equalsIgnoreCase("")) {
				String sSaved = UtilErp.ReadFromFile("textdata");
				if (sSaved.equalsIgnoreCase("")) {
					isFromServer = true;
					Toast.makeText(this, "Loading From Server...",Toast.LENGTH_LONG).show();

					(new DownloadDataFromURL()).execute("xqudl.aspx",
							"type=1&memberid=" + MemberUtil.memberid , "2");
				} else {
					// tag 4

					Toast.makeText(this, "Loading From Phone...",Toast.LENGTH_LONG).show();
					processResult(sSaved, 2);
				}
			} else {
				// tag 5
				processResult(sData, 2);

			}
		}

	}
	 public void getExtraInfo()
	    {
		 	if(getIntent().getExtras()==null) return;
	    	if(getIntent().getExtras().getString("typeid")!=null) typeid = (String) getIntent().getExtras().getString("typeid");
	    	if(getIntent().getExtras().getString("placeid")!=null)  placeid = (String) getIntent().getExtras().getString("placeid");
	    	
	    }
	public void backToHome() {
		
    } 
	@Override
	public void onResume() {
        super.onResume();
        NavigateScreen.getInstance().setCurrentDisplay(this);
    } 
	private void processResult(String sString,int typeid)
	{
		//Toast.makeText(this, sString+"===>"+String.valueOf(typeid), Toast.LENGTH_LONG).show();
		if(typeid==1)
		{
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
			dlgAlert.setCancelable(false);	
			
			String msg="Cập nhật dữ liệu thành công!";
			if(sString.toLowerCase().startsWith("error:"))
			{		
				msg="Cập nhật dữ liệu không thành công!?";
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
			}	
			else 
			{	
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
		else 
		{
			if (isFromServer) {

				UtilErp.SaveToFile("textdata", sString);
				isFromServer = false;
			}
			
			JSONArray jSonArr;
		
			try {
				jSonArr = new JSONArray(sString);
				 DATATEXT = new String[jSonArr.length()];
				 DATAVALUE= new String[jSonArr.length()];
				 
				for (int jx = 0; jx < jSonArr.length(); jx++) {
					JSONObject jsonO = jSonArr.getJSONObject(jx);
					String sId = (String) jsonO.get("id");
					String sTime = (String) jsonO.get("title");
					DATATEXT[jx]=sId;
					DATAVALUE[jx]=sTime;
					
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

				e.printStackTrace();
			}
		}
		//setupUI();
	}
	public void processAfterUpload()
	{				
		backToHome();
		//NavigateScreen.getInstance().switchDisplay(ListHomeScreen.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	public String readLastPastPositionFromDB() {
		
		return ContextManagerErp.getInstance().readLastPosition();
	}
	public void callToUpload()
	{
		if(TextUtils.isEmpty(txtView.getText().toString()))
		{
			UIManager.getInstance().showMsgWithToast("Xin vui lòng nhập thông báo text");
			return;
		}
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		//debug = "2";
		if (sDataPosition == null)sDataPosition = "";

		//startShowProgress();

		UrlParamEncoder encoder = (new UrlParamEncoder());
		encoder.addParam("id", LocationUtilErp.getInstance().getIMEI());
		encoder.addParam("note",URLEncoder.encode(txtView.getText().toString()));
		encoder.addParam("placeid", placeid);	
		encoder.addParam("typeid", typeid);	
		encoder.addParam("memberid", MemberUtil.memberid);
		encoder.addParam("data", sDataPosition);
		encoder.addParam("k", SecUtil.getInstance().signData(LocationUtilErp.getInstance().getIMEI()));

		final String params = encoder.toString();
		
	
		String sUrlToUpload="informtext.aspx" ;
		(new DownloadDataFromURL()).execute(sUrlToUpload,params,"1");
	}
	public void callToUploadAuto(String id)
	{
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		//debug = "2";
		if (sDataPosition == null)sDataPosition = "";

		//startShowProgress();

		UrlParamEncoder encoder = (new UrlParamEncoder());
		encoder.addParam("id", LocationUtilErp.getInstance().getIMEI());
		encoder.addParam("note",URLEncoder.encode(txtView.getText().toString()));
		encoder.addParam("placeid", placeid);	
		encoder.addParam("typeid", typeid);	
		encoder.addParam("memberid", MemberUtil.memberid);
		encoder.addParam("data", sDataPosition);
		encoder.addParam("k", SecUtil.getInstance().signData(LocationUtilErp.getInstance().getIMEI()));
		encoder.addParam("mode","auto");
		encoder.addParam("autoid",id);
		
		
		final String params = encoder.toString();	
		String sUrlToUpload="informtext.aspx" ;
		(new DownloadDataFromURL()).execute(sUrlToUpload,params,"1");
	}
	public void saveOffline(String namefile)
	{
		String sDataPosition = readLastPastPositionFromDB();// BgThread.getInstance().getDataCurrent();
		String note=txtView.getText().toString();
		String time=UtilGame.getInstance().GetStringNow();
		FileUtilErp.saveTextToMetaData(namefile, time, note, sDataPosition);
	}
	LayoutInflater controlInflater;
	public  String[] DATATEXT = new String[] {};
	public  String[] DATAVALUE = new String[] {};
	public void setupUI()
	{
		controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.controlqudl, null);
        LayoutParams layoutParamsControl 
        	= new LayoutParams(LayoutParams.FILL_PARENT, 
        	LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        
        Button btn1 = (Button)viewControl.findViewById(R.id.btn1);
        Button btn2 = (Button)viewControl.findViewById(R.id.btn2);
        Button btn3 = (Button)viewControl.findViewById(R.id.btn3);
        Button btn4 = (Button)viewControl.findViewById(R.id.btn4);
        Button btn5 = (Button)viewControl.findViewById(R.id.btn5);
        Button btn6 = (Button)viewControl.findViewById(R.id.btn6);
        
        Button[] arr= new Button[]{btn1,btn2,btn3,btn4,btn5,btn6};
        
        for(int jx=0;jx<=arr.length-1;jx++)
        {
        	arr[jx].setVisibility(View.GONE);
        }	
        for(int jx=0;jx<=DATAVALUE.length-1;jx++)
        {
        	if(jx >= arr.length) break;
        	
        	arr[jx].setTag(DATAVALUE[jx]);
        	arr[jx].setText(DATATEXT[jx]);
        	arr[jx].setVisibility(View.VISIBLE);
        	arr[jx].setOnClickListener(new View.OnClickListener() 
        	{
		        public void onClick(View view) 
		        {
		        	callToUploadAuto(view.getTag().toString());
		            //Toast.makeText(view.getContext(),  "Button clicked tag = "+view.getTag().toString(), Toast.LENGTH_SHORT).show();
		        }
        	});	        	
        }	
	}
	
    class DownloadDataFromURL extends AsyncTask<String, String, String> {
		int typeidx=-1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIManager.getInstance().showDialog(1);
		}
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			String sResult = "";
			try {

				// sResult = (new HttpData()).getData(f_url[0],f_url[1]);
				sResult = "";
				System.setProperty("http.keepAlive", "false");			
				String xurl = HttpData.prefixUrl + f_url[0] + "?" + f_url[1];
				//xurl ="http://erp.vhc.vn/m/qudl.aspx?memberid=152721";
				typeidx=Integer.parseInt(f_url[2]);
				
				URL url = new URL(xurl);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000);
				int lenghtOfFile = conn.getContentLength();

				// Get the response
				DataInputStream rd = (new DataInputStream(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				byte[] buffer = new byte[512];
				int total = 0;
				while ((count = rd.read(buffer)) != -1) {
					total += count;
					int progress_temp = (int) total * 100 / lenghtOfFile;
					publishProgress("" + progress_temp);
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
			return sResult;
		}
		@Override
		protected void onCancelled()
        {
        }
		@Override
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			UIManager.getInstance().setProgress(Integer.parseInt(progress[0]));
		}
		@Override
		protected void onPostExecute(String result) {
			processResult(result,typeidx);	
			UIManager.getInstance().dismissDialog();
		}
	}

	@Override
	public void setupUIButtonControl() {
		// TODO Auto-generated method stub
		findViewById(R.id.btnOK).setVisibility(View.VISIBLE);
		findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
		findViewById(R.id.btnHome).setVisibility(View.VISIBLE);
		findViewById(R.id.btnRefresh).setVisibility(View.VISIBLE);
	}
}

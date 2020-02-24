package vn.vhc.live.erp;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


import vn.vhc.live.HttpData;
import vn.vhc.live.R;
import vn.vhc.live.UtilGame;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
public class LstReportOrder extends ListActivity 
{
	//public  String[] DATATEXT = new String[] {"xxx"};
	//public  String[] DATAVALUE = new String[] {"xxx"};
	
	public  DataUI[] DATASOURCE = new  DataUI[]{};
	public static String selectedId="-1";
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setTitle(MemberUtil.companyName);
	  
	  NavigateScreen.getInstance().setCurrentDisplay(this);
	  ContextManagerErp.getInstance().setCurrentContext(this);
	  
	  //get data from server
	  getDataFromServer();
	  //setListAdapter(new ArrayAdapter<String>(this, R.layout.listplace, DATATEXT));
	 	
	 }
	
	 //registerForContextMenu(lv);  
	  public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	  {  
		  super.onCreateContextMenu(menu, v, menuInfo);  
          menu.setHeaderTitle("Lựa chọn");
          menu.add(0, 1, 0, "Thông tin chi tiết");  
         
      }  
    
      public boolean onContextItemSelected(MenuItem item) {
    	  if (!AdapterView.AdapterContextMenuInfo.class.isInstance(item.getMenuInfo ()))
	          return false;

	      AdapterView.AdapterContextMenuInfo cmi =
	          (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

	      Object o = getListView ().getItemAtPosition (cmi.position);	      
	      
          if(item.getItemId()==1){function1(((DataUI)o));}  
           
          else {return false;}  
      return true;  
      }  
    
      public void function1(DataUI o){ 
    	  LstCamera.placeid=(o.get_id());
    	  LstCamera.typeid="1";         
    	  showDataDetail(o);          
      }  
      private void getDataFromServer()
 	 {		
    	//if(DATASOURCE.length>0)return;
        //startShowProgress();
 		new DownloadDataFromURL().execute("reporteds.aspx",  "typeid=2&status=confirmedrequest&memberid="+MemberUtil.memberid);
 		/*
 		String sString=(new HttpData()).getData("reporteds.aspx",  "typeid=2&status=confirmedrequest&memberid="+MemberUtil.memberid);
 		try 
 		{
 			JSONArray jSonArr;
 			jSonArr = new JSONArray(sString);
 			//DATATEXT= new String[jSonArr.length()];
 			DATASOURCE= new DataUI[jSonArr.length()];
 			
 			for(int jx=0;jx<=jSonArr.length()-1;jx++)
 			{
 				
 					JSONObject jsonO= jSonArr.getJSONObject(jx);
 					String sID=(String) jsonO.get("id");
 					String sTime=(String) jsonO.get("dateorder");
 				
 					//String sTitle=(String) jsonO.get("code")+"-"+(String) jsonO.get("note");
 					sTime=UtilGame.getInstance().GetDateToView(sTime);
 					
 					String sTitle=sTime+" - "+(String) jsonO.get("fullname_customer"); 	 				
 					//
 					String sPlaceFullName=(String) jsonO.get("fullname_customer");
 					String sPlaceAddress=(String) jsonO.get("address");
 					String sNote=(String) jsonO.get("note");
 					
 					//DATASOURCE[jx]= (new DataUI(sID, sTitle,sTime,sPlaceFullName,sPlaceAddress));
 					DATASOURCE[jx]= (new DataUI(sID, sTitle,sTime,sPlaceFullName,sPlaceAddress,sNote));	
 					
 			}
 			//isstop=true;
 		} 
 		catch (Exception e) 
 		{
 			// TODO Auto-generated catch block
 			UIManager.getInstance().showMsg(e.toString());
 		}	
 		*/
 	}
      private void showDataDetail(DataUI jsonO)
  	  {		
  		try 
  		{
  			StringBuilder sb = new StringBuilder();
  			sb.append("Khách hàng: "+jsonO.get_placefullname()+"\n");
  			sb.append("Địa chỉ: "+jsonO.get_placeaddress()+"\n");  			
  			sb.append("Thời gian: "+jsonO.get_time()+"\n");
  			sb.append("Nội dung: "+jsonO.get_extra1()+"\n");
  			sb.append("\n");
  			sb.append("\n");
  			
  			sb.append("Tình trạng đ/hàng: YC Đặt Hàng");
  			
  			UIManager.getInstance().showMsgWithTitleOKCancel("Thông tin chi tiết",sb.toString(),jsonO.get_id());
  		} 
  		catch (Exception e) 
  		{
  			// TODO Auto-generated catch block
  			UIManager.getInstance().showMsg(e.toString());
  		}	
  	}
    public Handler hxDebug= new Handler(){
  		
  		public void handleMessage(Message msg) 
  		  {			  
  			  	  
  			 Toast.makeText(getApplicationContext(),"Debug:"+(String)msg.obj,Toast.LENGTH_LONG).show();
  			 
  		  }
  	};
  	private void processResult(String sString) 
	{
		//get list of agency
		try 
	 		{
	 			JSONArray jSonArr;
	 			jSonArr = new JSONArray(sString);
	 			//DATATEXT= new String[jSonArr.length()];
	 			DATASOURCE= new DataUI[jSonArr.length()];
	 			
	 			for(int jx=0;jx<=jSonArr.length()-1;jx++)
	 			{
	 				
	 					JSONObject jsonO= jSonArr.getJSONObject(jx);
	 					String sID=(String) jsonO.get("id");
	 					String sTime=(String) jsonO.get("dateorder");
	 				
	 					//String sTitle=(String) jsonO.get("code")+"-"+(String) jsonO.get("note");
	 					sTime=UtilGame.getInstance().GetDateToView(sTime);
	 					
	 					String sTitle=sTime+" - "+(String) jsonO.get("fullname_customer"); 	 				
	 					//
	 					String sPlaceFullName=(String) jsonO.get("fullname_customer");
	 					String sPlaceAddress=(String) jsonO.get("address");
	 					String sNote=(String) jsonO.get("note");
	 					
	 					//DATASOURCE[jx]= (new DataUI(sID, sTitle,sTime,sPlaceFullName,sPlaceAddress));
	 					DATASOURCE[jx]= (new DataUI(sID, sTitle,sTime,sPlaceFullName,sPlaceAddress,sNote));	
	 					
	 			}
	 			//isstop=true;
	 		} 
	 		catch (Exception e) 
	 		{
	 			// TODO Auto-generated catch block
	 			UIManager.getInstance().showMsg("Error:"+e.toString());
	 		}	
	 		
	 		setListAdapter(new ArrayAdapter<DataUI>(this, android.R.layout.simple_list_item_1, DATASOURCE));
	 		setContentView(R.layout.listviewtest);
	 		
	 		ListView lv = this.getListView();
	 		  registerForContextMenu(lv);  
	 		  lv.setOnItemSelectedListener(new OnItemSelectedListener() {
	 			    public void onItemSelected(AdapterView<?> parent, View view,
	 				        int position, long id) {
	 				      // When clicked, show a toast with the TextView text
	 				     	    	
	 			    	  //selectedId=((DATAVALUE[position])); 	
	 			    	  
	 				    }
	 				@Override
	 				public void onNothingSelected(AdapterView<?> arg0) {
	 					// TODO Auto-generated method stub
	 					
	 				}
	 			});
	 		  lv.setOnItemClickListener(new OnItemClickListener() {
	 			    public void onItemClick(AdapterView<?> parent, View view,
	 			        int position, long id) {
	 			      // When clicked, show a toast with the TextView text
	 			      //Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	 			      //    Toast.LENGTH_SHORT).show();		    	
	 			      function1((DATASOURCE[position])); 		           
	 			    }
	 			  });
	 		  
	 		   Button closeButton =(Button) findViewById(R.id.btnExit);
	 		  	closeButton.setText("Home");
	 		  	closeButton.setOnClickListener(new OnClickListener(){
	 		          public void onClick(View v) {               
	 		        	  NavigateScreen.getInstance().switchDisplay(ListHome.class);
	 		          }});
	 		  	
	 		  	UIManager.getInstance().dismissDialog();
	}
  	/*
    //progress bar
  	private Handler handler= new Handler();
  	private ProgressDialog pd;
  	public static boolean isstop;
  	
  	public void startShowProgress() {
  		pd = ProgressDialog.show(this, "�?ang xử lý..", "�?ang xử lý dữ liệu", true,false);	           
  	    
  		// Do something long
  		Runnable runnable = new Runnable() {

  			@Override
  			public void run() 
  			{
  				while(!isstop)
          		{
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
				//typeidx=Integer.parseInt(f_url[2]);
				
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
			//pDialog.setProgress(Integer.parseInt(progress[0]));
			UIManager.getInstance().setProgress(Integer.parseInt(progress[0]));
		}
		@Override
		protected void onPostExecute(String result) {
			processResult(result);	
			//UIManager.getInstance().dismissDialog();
		}
	}
}

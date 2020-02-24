package vn.vhc.live;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;




public class HttpData
{
	 //ptracker for new version erp
	 public final static String prefixUrlData="http://tech.vhc.vn/services/data/";
	 public final static String prefixUrlDataX="http://tech.vhc.vn/services/datax/";
	
	 
	 public static int intervalTimes=60;
	 //public final static String prefixUrl="http://all.viethonggps.vn:12411/services/m/";	
	 public final static String prefixUrlDataErp="http://tech.vhc.vn/services/m/"; //data erp
	 public final static String prefixUrl="http://tech.vhc.vn/services/m/";	//login and get information erp,save domain witg otracjer

	 
	 public final static  String prefixUrlData()
	 {
		if(UtilGame.modeEncrypt) return   prefixUrlDataX;
		return prefixUrlData;
	 }
	 public  void postActionMember(final String params,final int sleep)
	 {
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				//while(true)
				{
					 // TODO Auto-generated method stub
					 try 
					 {		
						 if(sleep>0)Thread.sleep(sleep);
						
						 System.setProperty("http.keepAlive", "false");	
						 
						 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
						 //boolean isChangeNeedNetWork=false;
						 //if network is connected is nothing
						 if(!isConnectedNetWork)
						 {			 
							 ContextManagerCore.getInstance().setMobileDataEnabled(true);
							 //isChangeNeedNetWork=true;
						 }
						 
						 String xurl=prefixUrlData()+"handleActionMember.aspx?"+params+"&iddevice="+LocationUtil.IMEI;
						 Log.v("lamdaica1", "Call to postActionMember:"+xurl);
							
						 if(UtilGame.isModeDebug)
						 {
							Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), "Exception Call to :"+xurl, Toast.LENGTH_LONG).show();
						 }
						 
						 URL url = new URL(xurl);
			             URLConnection conn = url.openConnection();
			             // Get the response
			             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			             String line = "";
			             String result = "";
			             while ((line = rd.readLine()) != null) {
			            	 result += line;               
			             }             
			             
			             if(UtilGame.isModeDebug)
						 {
								Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), "Result Call To :"+result, Toast.LENGTH_LONG).show();
						 }
			             rd=null;
			             conn=null;
			             url=null;
			            
					}catch (Exception e) {
						 Log.v("lamdaica1", "Exception ActionMember: "+e.toString());
							
						//Log.v("lamdaica1", "Exception Call to "+xurl+"===>"+e.toString());						
						// TODO Auto-generated catch block
						//e.printStackTrace();		
						if(UtilGame.isModeDebug)
						{
							Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), "Exception Call to :"+e.toString(), Toast.LENGTH_LONG).show();
							//UIManager.getInstance().showMsg("Exception Call to :"+e.toString());
						}
					}	
				}
			}
		}).start();
		 
	 }
	 public  String postUrlOffline(String xurl)
	 {
		 try 
		 {	
			 System.setProperty("http.keepAlive", "false");
			 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
			 boolean isChangeNeedNetWork=false;
			 //if network is connected is nothing
			 if(!isConnectedNetWork)
			 {			 
				 ContextManagerCore.getInstance().setMobileDataEnabled(true);
				 isChangeNeedNetWork=true;
			 }
				
			 //String xurl="http://all.viethonggps.vn:12411/services/http/handlePosition.aspx?data="+params+queryExtra;
			 Log.v("lamdaica1", "Call In postUrlOffline "+xurl);
			 //android.os.BatteryManager.EXTRA_LEVEL
			 
			 URL url = new URL(xurl);
             URLConnection conn = url.openConnection();
             conn.setConnectTimeout(10000);
             // Get the response
             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line = "";
             String result = "";
             while ((line = rd.readLine()) != null) {
            	 result += line;               
             }             
             
             Log.v("lamdaica1", "Result call to "+result);
             //rd.close();
             rd=null;
             conn=null;
             url=null;
             
             //intervalTimes=Integer.parseInt(result);  
             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
             {
            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
             }
             
             return result;//String.valueOf(result);             
		}
		catch (Exception e) 
		{
			return null;
			//intervalTimes=60;			
			// TODO Auto-generated catch block
			//return e.toString();			
		}		
	 }
	
	 public String httpPostFile(String URL, String FILE,String fileToUpload,boolean isDeleteAfter) 
	 {			
		 	//Log.v("lamdaica1", "Call In httpPostFile ");
			//notifyMessage("Streaming file");
			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;

			//String pathToOurFile = "/data/file_to_send.mp3";
			//if(!URL.startsWith("http"))	
			//		URL=prefixUrl+URL;
			String urlServer =URL;// "http://192.168.1.1/handle_upload.php";
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;

			try 
			{
				//Log.v("ErrorFile",fileToUpload+"-"+FILE);
				System.setProperty("http.keepAlive", "false");
				boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
				 boolean isChangeNeedNetWork=false;
				 //if network is connected is nothing
				 if(!isConnectedNetWork)
				 {			 
					 ContextManagerCore.getInstance().setMobileDataEnabled(true);
					 isChangeNeedNetWork=true;
				 }
				 
				 
				FileInputStream fileInputStream = new FileInputStream(new File(fileToUpload));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int totalByte=fileInputStream.available();
				
				String extFile=fileToUpload.substring(0,fileToUpload.lastIndexOf(".")+1);
				String[] arrParts=fileToUpload.split("/");
				extFile=arrParts[arrParts.length-1];
				
				//if(totalByte-indexStreaming<=0) return;
				//notifyMessage("Streaming file:"+(totalByte-indexStreaming));
				String result = "";
				//Log.v("ErrorFile",""+totalByte);
				boolean avoidOutOfMemory=totalByte>15016025;
				if(extFile.toLowerCase().indexOf("video")!=-1) avoidOutOfMemory=totalByte >50016025;//file video
				if(extFile.toLowerCase().endsWith("txt")) avoidOutOfMemory=totalByte >7016025;
				
				if(!avoidOutOfMemory)//3130655
				{
				
					int indexStreaming=0;
					
					byte[] imgData = new byte[totalByte-indexStreaming];		
					fileInputStream.read(imgData, indexStreaming, imgData.length);
					indexStreaming=totalByte-1;
					
					//FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
					String debugQuery="";//"indexStreaming="+String.valueOf(indexStreaming)
						//+"&f="+String.valueOf(totalByte)+"&img="+String.valueOf(imgData.length);
	
					URL url = new URL(urlServer+debugQuery);
					connection = (HttpURLConnection) url.openConnection();
	
					connection.setConnectTimeout(10*1000);
					// Allow Inputs & Outputs
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
	
					// Enable POST method
					connection.setRequestMethod("POST");
	
					//connection.setRequestProperty("Connection", "Keep-Alive");
					connection.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					//indexFile=indexFile+1;
					outputStream = new DataOutputStream(connection.getOutputStream());
					outputStream.writeBytes(twoHyphens + boundary + lineEnd);
					outputStream
							.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
									+ extFile + "\"" + lineEnd);
					outputStream.writeBytes(lineEnd);
	
					
					//old version
					//outputStream.write(imgData, 0, imgData.length);				
					//new version
					byte[] bytes=imgData;
					int bufferLength = 256;
					
		            for (int i = 0; i < bytes.length; i += bufferLength) {
		                if (bytes.length - i >= bufferLength) {
		                    outputStream.write(bytes, i, bufferLength);//error here .txt-9769767
		                } else {
		                    outputStream.write(bytes, i, bytes.length - i);//error here-9769767
		                }
		            }
		           //end of new version
					
					outputStream.writeBytes(lineEnd);
					outputStream.writeBytes(twoHyphens + boundary + twoHyphens
							+ lineEnd);
	
					// Responses from the server (code and message)
					int serverResponseCode = connection.getResponseCode();
					String serverResponseMessage = connection.getResponseMessage();
	
					//fileInputStream.close();
					outputStream.flush();
					
					
					BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		             String line = "";
		             
		             while ((line = rd.readLine()) != null) {
		            	 result += line;               
		             }            
		             
		             outputStream.close();
				}
				else 
				{
					fileInputStream.close();
					//bos.close();
				}
	             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
	             {
	            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
	             }
	             try
	             {
		             if(isDeleteAfter)
					 {
							File file = new File(fileToUpload);
							file.delete();
					 }
	             }catch(Exception exxxx){}
				return result;
			} catch (Exception ex) {
				//UIManager.getInstance().showMsg("Exception when upload file:"+ex.toString());
				return null;//ex.toString();
				// Exception handling
				//notifyMessage("Exception:"+ex.toString());
			}
		}	 
	 public  void notifyRealtimeCmd(String typeCMD)
	 {
		 
		 String xurl="updateStatusRealTime.aspx?iddevice="+LocationUtil.IMEI
		 +"&type=1&cmd="+typeCMD;
//		 new Thread(new Runnable() {			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
				try 
				 {
					 
					 System.setProperty("http.keepAlive", "false");
					 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
					 boolean isChangeNeedNetWork=false;
					 //if network is connected is nothing
					 if(!isConnectedNetWork)
					 {			 
						 ContextManagerCore.getInstance().setMobileDataEnabled(true);
						 isChangeNeedNetWork=true;
					 }
					
					 //String xurl="http://all.viethonggps.vn:12411/services/http/handlePosition.aspx?data="+params+queryExtra;
					 Log.v("lamdaica1", "Call in notifyRealtimeCmd "+xurl);
					 //android.os.BatteryManager.EXTRA_LEVEL
					 
					 URL url = new URL(prefixUrlData+xurl);
		             URLConnection conn = url.openConnection();
		             conn.setConnectTimeout(10000);
		             // Get the response
		             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		             String line = "";
		             String result = "";
		             while ((line = rd.readLine()) != null) {
		            	 result += line;               
		             }             
		             
		             Log.v("lamdaica1", "Result call to "+result);
		             rd=null;
		             conn=null;
		             url=null;
		             
		             //intervalTimes=Integer.parseInt(result);  
		             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
		             {
		            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
		             }
		        }
				catch (Exception e) 
				{
					//UIManager.getInstance().showMsg("Expcetion when notify:"+e.toString()+":"+prefixUrl+xurl);	
				}		
//			}
//		}).start();		 
	 }
	 public  void notifyRealtimeCmd(String typeCMD,Object ocmdProcessing)
	 {
		 String cmdProcessing=String.valueOf(Integer.valueOf(ocmdProcessing.toString())-200);
		 String xurl="updateStatusRealTime.aspx?iddevice="+LocationUtil.IMEI
		 +"&type=1&cmd="+typeCMD+"&cmdprocessing="+cmdProcessing;
//		 new Thread(new Runnable() {			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
				try 
				 {
					 
					 System.setProperty("http.keepAlive", "false");
					 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
					 boolean isChangeNeedNetWork=false;
					 //if network is connected is nothing
					 if(!isConnectedNetWork)
					 {			 
						 ContextManagerCore.getInstance().setMobileDataEnabled(true);
						 isChangeNeedNetWork=true;
					 }
					
					 //String xurl="http://all.viethonggps.vn:12411/services/http/handlePosition.aspx?data="+params+queryExtra;
					 Log.v("lamdaica1", "Call in notifyRealtimeCmd "+xurl);
					 //android.os.BatteryManager.EXTRA_LEVEL
					 
					 URL url = new URL(prefixUrlData+xurl);
		             URLConnection conn = url.openConnection();
		             conn.setConnectTimeout(10000);
		             // Get the response
		             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		             String line = "";
		             String result = "";
		             while ((line = rd.readLine()) != null) {
		            	 result += line;               
		             }             
		             
		             Log.v("lamdaica1", "Result call to "+result);
		             rd=null;
		             conn=null;
		             url=null;
		             
		             //intervalTimes=Integer.parseInt(result);  
		             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
		             {
		            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
		             }
		        }
				catch (Exception e) 
				{
					//UIManager.getInstance().showMsg("Expcetion when notify:"+e.toString()+":"+prefixUrl+xurl);	
				}		
//			}
//		}).start();		 
	 }
	 public  String getData(String purl,String params)
	 {
		 String result = "";
		 try 
		 {	
			 System.setProperty("http.keepAlive", "false");
			 boolean isChangeNeedNetWork=false;
			 if(UtilGame.save3GState && UtilGame.typegame.toLowerCase().equals("ptrackercore"))
			 {
				 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
				 
				 //if network is connected is nothing
				 if(!isConnectedNetWork)
				 {			 
					 ContextManagerCore.getInstance().setMobileDataEnabled(true);
					 isChangeNeedNetWork=true;
				 }
			 }
			 
			 //String xurl=prefixUrl+purl+"?"+params;
			 String xurl=prefixUrlData()+purl+"?"+params;
			 Log.v("lamdaica1", "Call in getData "+xurl);
			 //Log.v(APTrackerERPActivity.LOGTAG, "Call to url:"+xurl);
			 
			 URL url = new URL(xurl);
             URLConnection conn = url.openConnection();
             conn.setConnectTimeout(10000);
             // Get the response
             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line = "";
            
             while ((line = rd.readLine()) != null) {
            	 result += line + "\n";               
             }             
            // Log.v(APTrackerERPActivity.LOGTAG, "Result call to "+result);
             
             rd=null;
             conn=null;
             url=null;
             UtilGame.errNetWorkTimes=0;
             
             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
             {
	             if(UtilGame.save3GState)
	             {
	            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
	             }  
             }			 
		}catch (Exception e)
		{
			UtilGame.errNetWorkTimes+=1;
			//return e.toString();
			// TODO Auto-generated catch block
			//UIManager.getInstance().showMsg("Expception:"+e.toString());
		}		
		return result;
	 }
	 public  String getDataErp(String purl,String params)
	 {
		 String result = "";
		 try 
		 {	
			 System.setProperty("http.keepAlive", "false");
			 boolean isChangeNeedNetWork=false;
			 if(UtilGame.save3GState && UtilGame.typegame.toLowerCase().equals("ptrackercore"))
			 {
				 boolean isConnectedNetWork=ContextManagerCore.getInstance().isConnected();
				 
				 //if network is connected is nothing
				 if(!isConnectedNetWork)
				 {			 
					 ContextManagerCore.getInstance().setMobileDataEnabled(true);
					 isChangeNeedNetWork=true;
				 }
			 }
			 
			 String xurl=prefixUrl+purl+"?"+params;
			 //String xurl=prefixUrlData+purl+"?"+params;
			 
			 //Log.v(APTrackerERPActivity.LOGTAG, "Call to url:"+xurl);
			 
			 URL url = new URL(xurl);
             URLConnection conn = url.openConnection();
             conn.setConnectTimeout(10000);
             // Get the response
             BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line = "";
            
             while ((line = rd.readLine()) != null) {
            	 result += line + "\n";               
             }             
            // Log.v(APTrackerERPActivity.LOGTAG, "Result call to "+result);
             
             rd=null;
             conn=null;
             url=null;
             UtilGame.errNetWorkTimes=0;
             
             if(UtilGame.typegame.toLowerCase().equals("ptrackercore"))
             {
	             if(UtilGame.save3GState)
	             {
	            	 if(isChangeNeedNetWork) ContextManagerCore.getInstance().setMobileDataEnabled(false);
	             }  
             }			 
		}catch (Exception e)
		{
			UtilGame.errNetWorkTimes+=1;
			//return e.toString();
			// TODO Auto-generated catch block
			//UIManager.getInstance().showMsg("Expception:"+e.toString());
		}		
		return result;
	 }

}

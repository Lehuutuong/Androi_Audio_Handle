package vn.vhc.live;


import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;


public class LocationUtil 
{
	public static boolean isHaveGps=true;
	//gps
	public static String lat="";
	public static String lon="";
	public static String accuracy="";
	public static String speed="";
	public static String time="";
	public static String alt="";
	public static String course="";
	///cellid
	public static String code_cellid="";
	public static String code_lac="";
	public static String code_mnc="";
	public static String code_mmc="";
	
	public static String IMEI="lamdaica";
	public static String lastAddress="";
	
	
	 TelephonyManager tm;
	 GsmCellLocation loc;
	 public LocationUtil() 
	 {
		 tm= (TelephonyManager) ContextManagerCore.getInstance().getCurrentContext().getSystemService(Context.TELEPHONY_SERVICE);
		 loc= (GsmCellLocation) tm.getCellLocation();	  
	 }
	 public  String getCellId(){
		 try{
		 if(loc==null)return "";
		 return String.valueOf(loc.getCid());
	 }catch(Exception ex){return "";}
	 }
	 public  String getMCC()
	 {
		 try{
		 if(tm==null)return "";
		 return tm.getNetworkCountryIso().substring(0, 3);
	 }catch(Exception ex){return "";}
	 }
	 
	 public  String getMNC()
	 {
		 try{
		 if(tm==null)return "";
		 return tm.getNetworkOperator().substring(3);
	 }catch(Exception ex){return "";}
		 //int mcc = Integer.parseInt(networkOperator.substring(0, 3));
	     //int mnc = Integer.parseInt(networkOperator.substring(3));


	 }
	 public  String getLAC()
	 { try{
		 if(loc==null) return "";
		 return String.valueOf(loc.getLac());
	 }catch(Exception ex){return "";}
		 
	 }
	public static String getIMEI() {
		// TODO Auto-generated method stub
		return IMEI;
	}
} 


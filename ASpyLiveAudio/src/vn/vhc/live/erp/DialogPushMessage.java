package vn.vhc.live.erp;


import vn.vhc.live.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class DialogPushMessage extends Activity{
	EditText txtChatContent;
	String toUsername;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		super.onCreate(savedInstanceState);
       
		//init information to show
		String toUsername=null;
		if(getIntent().getExtras()!=null)
		{
			toUsername=  getIntent().getExtras().getString("title");
			toUsername=toUsername+"\n"+  getIntent().getExtras().getString("content");
		}
		
        setContentView(R.layout.dialog_pushmessage);
 
        TextView txt=(TextView)findViewById(R.id.txtContent);
        txt.setText(toUsername.toString());
        
        ImageView jpgView = (ImageView) findViewById(R.id.close);        
        jpgView.setOnClickListener(new View.OnClickListener() 
        {  
        		public void onClick(View v)
        		{  
        			//finis(10001);
        			finish();
        		}  
        });  
	}	
}

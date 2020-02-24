package vn.vhc.live.erp;

import vn.vhc.live.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class ListHome extends ListActivity {
	static final String[] COUNTRIES = new String[] {
	    "Chấm công", "Danh sách đại lý","Kế hoạch làm việc","Báo cáo công việc"};
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  NavigateScreen.getInstance().setCurrentDisplay(this);
	  ContextManagerErp.getInstance().setCurrentContext(this);
	       
	  setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, COUNTRIES));
      setContentView(R.layout.listviewtest);
      
	  setTitle(MemberUtil.companyName);
	  ListView lv = getListView();
	  
	  lv.setTextFilterEnabled(true);
	   
	  //registerForContextMenu(lv);  
	  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      
		    }
		  });
	  
	  	Button closeButton =(Button) findViewById(R.id.btnExit);
	  	closeButton.setOnClickListener(new OnClickListener(){
	          public void onClick(View v) {     
	        	  Intent intent = new Intent(Intent.ACTION_MAIN);
	              intent.addCategory(Intent.CATEGORY_HOME);
	              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	              startActivity(intent);

	          }});
	  }
	
    
}

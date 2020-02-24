package vn.vhc.live.erp;

import vn.vhc.live.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ListViewTest extends ListActivity {
	 /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = {"One", "Two", "Three"};

        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, values));

        setContentView(R.layout.listviewtest);
    }

}
//static final String[] COUNTRIES = new String[] {
//    "Chấm công", "Ds �?ại lý","Kế hoạch làm việc","Báo cáo công việc"};
//public void onCreate(Bundle savedInstanceState) {
//  super.onCreate(savedInstanceState);
//  
//  setListAdapter(new ArrayAdapter<String>(this, R.layout.listhome, COUNTRIES));
//  setTitle(MemberUtil.companyName);
//  ListView lv = getListView();
//  lv.setTextFilterEnabled(true);
//   
//  //registerForContextMenu(lv);  
//  lv.setOnItemClickListener(new OnItemClickListener() {
//	    public void onItemClick(AdapterView<?> parent, View view,
//	        int position, long id) {
//	      // When clicked, show a toast with the TextView text
//	      //Toast.makeText(getApplicationContext(), "Pos:"+String.valueOf(position),Toast.LENGTH_LONG).show();
//	      //list of company place
//	      if(position==0)
//	      {
//	    	  LstCamera.typeid="1";
//	    	  NavigateScreen.getInstance().setDisplay(view.getContext(), LstPlace.class);
//	    	  //Intent mi = new Intent(view.getContext() , LstPlace.class);
//	    	  //view.getContext().startActivity(mi);
//	      }
//	      //list of company agency
//	      if(position==1)
//	      {
//	    	  LstCamera.typeid="2";
//	    	  NavigateScreen.getInstance().setDisplay(view.getContext(), LstAgency.class);
//	      }
//	      if(position==2)
//	      {
//	    	  LstCamera.typeid="3";
//	    	  NavigateScreen.getInstance().setDisplay(view.getContext(), LstPlan.class);
//	      }
//	      if(position==3)
//	      {
//	    	  LstCamera.typeid="4";
//	    	  NavigateScreen.getInstance().setDisplay(view.getContext(), LstReport.class);
//	      }
//	    }
//	  });
//  	  setupUI();
//	}
//  //registerForContextMenu(lv);  
//  public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
//  {  
//	  super.onCreateContextMenu(menu, v, menuInfo);  
//      menu.setHeaderTitle("Context Menu");  
//      menu.add(0, v.getId(), 0, "Action 1");  
//      menu.add(0, v.getId(), 0, "Action 2");  
//  }  
//  public void setupUI() {  
//	  
//	  Button closeButton = new Button(this);
//	  closeButton.setText("Exit");//(getResources().getString(R.string.btnExit));
//	  closeButton.setOnClickListener(new OnClickListener(){
//	          public void onClick(View v) {               
//	              finish();
//	          }});
//	  
//	  getListView() .addFooterView(closeButton);   
//  }
//  public boolean onContextItemSelected(MenuItem item) {  
//      if(item.getTitle()=="Action 1")
//      {
//    	 //function1(item.getItemId());
//    	  NavigateScreen.getInstance().setDisplay(this, LstCamera.class); 
//      }  
//      else if(item.getTitle()=="Action 2"){function2(item.getItemId());}  
//      else {return false;}  
//  return true;  
//  }  
//
//  public void function1(int id){  
//      Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show();  
//  }  
//  public void function2(int id){  
//      Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();  
//  }  
//  
package vn.vhc.live.erp;

import vn.vhc.live.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ListVideoRow extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_video_row);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list_video_row, menu);
		return true;
	}

}

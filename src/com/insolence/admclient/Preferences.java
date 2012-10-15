package com.insolence.admclient;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;


public class Preferences extends SherlockPreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);             
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add("Save and back").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
		
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	this.finish();
    	return true;
    }

	@Override
	protected void onPause() {
		DownloadItemListActivity.instance.applyPreferences();
		super.onPause();
	}
}
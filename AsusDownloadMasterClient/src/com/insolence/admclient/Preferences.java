package com.insolence.admclient;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;


public class Preferences extends SherlockPreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);               
	}

	@Override
	protected void onPause() {
		super.onPause();
		DownloadItemListActivity.getPrefs(PreferenceManager.getDefaultSharedPreferences(this));
	}
}
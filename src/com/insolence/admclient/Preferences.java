package com.insolence.admclient;

import com.balysv.materialmenu.MaterialMenuView;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.insolence.admclient.donation.DonationHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View.OnClickListener;


public class Preferences extends ActionBarActivity{
	
	@Override
	public void onStart() {
	    super.onStart();
	    //TODO: Google Analytics
	}
	
	@Override
	public void onStop() {
	    super.onStop();
	  //TODO: Google Analytics
	}
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  new Handler().post(new Runnable() {
			  @Override
				public void run() {
				  getSupportFragmentManager().beginTransaction().add(android.R.id.content, new PreferencesFragment()).commit();
				  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			  }
		  });	  
	 }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	this.finish();
    	return true;
    }
	
}
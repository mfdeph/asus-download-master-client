package com.insolence.admclient;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

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
	 
	 public static Preferences Current;
	 
	 @Override
	 public void onResume(){
		 super.onResume();
		 Current = this;
	 }
	 
	 @Override
	 public void onPause(){
		 super.onPause();
		 Current = null;
	 }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	this.finish();
    	return true;
    }
	
}
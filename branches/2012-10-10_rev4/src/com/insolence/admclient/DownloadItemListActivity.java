package com.insolence.admclient;

import java.io.File;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Toast;

public class DownloadItemListActivity extends SherlockListActivity {
    /** Called when the activity is first created. */
	
	boolean _serviceAlreadyRun = false;
    
	@Override
    public void onStart(){
		super.onStart();
		DownloadItemListManager.SetPrefs(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DownloadItemListManager.SetPrefs(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        
        setContentView(R.layout.download_item_list_activity);
        
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null){

        	final File file = new File(data.getPath());
        	final ListActivity activityToTransfer = this;
        	
			new AlertDialog.Builder(this)
	           .setMessage(String.format("Do you really want to start download \"%s\" torrent?", file.getName()))
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   new SendFileAsyncTask(activityToTransfer, file).execute();
	        		   Toast.makeText(
	        				   activityToTransfer,
	        				   "Torrent \"" + file.getName() + "\" is managed to download.", Toast.LENGTH_SHORT).show();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
        }
        new GetDownloadItemListAsyncTask(this).execute();
        
        if (!_serviceAlreadyRun){
        	h.postDelayed(myRunnable, 6000);
        	_serviceAlreadyRun = true;
        }

    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);       
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection	
        switch (item.getItemId()) {
	        case R.id.settings:
	        	Intent settingsActivity = new Intent(getBaseContext(),
                        Preferences.class);
	        	startActivity(settingsActivity);
	            return true;
	        case R.id.pause_all:
	        	new SendCommandAsyncTask(this, "pause_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   "All downloads are managed to pause.", Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.resume_all:
	        	new SendCommandAsyncTask(this, "start_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   "All downloads are managed to start.", Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.delete_finished:
	        	final ListActivity current = this;
				new AlertDialog.Builder(this)
		           .setMessage("Do you really want to crear all finished downloads?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   new SendCommandAsyncTask(current, "clear").execute();	            	   
		        		   Toast.makeText(
		        				   current,
		        				   "All finished downloads are managed to delete.", Toast.LENGTH_SHORT).show();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();        	
	        	return true;
	        default:
	            return false;
        }
    }
    
    
    
    DownloadItemListActivity context = this;
     
	private Handler h = new Handler();

	private Runnable myRunnable = new Runnable() {
	   public void run() {
		new GetDownloadItemListAsyncTask(context).execute();
	    h.postDelayed(myRunnable, 6000);
	   }
	};

}
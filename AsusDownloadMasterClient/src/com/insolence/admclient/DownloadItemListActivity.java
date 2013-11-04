package com.insolence.admclient;

import java.io.File;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.insolence.admclient.DownloadItemListAdapter.OnSelectItemListener;
import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.asynctasks.SendMagnetTask;
import com.insolence.admclient.asynctasks.SendTorrentTask;
import com.insolence.admclient.listmanagers.*;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public class DownloadItemListActivity extends SherlockListActivity implements IProcessResultConsumer, IDisabler, OnSelectItemListener{
	
	static boolean _autoRefreshEnabled = true;
	
	String selectedItemName;
	
	public static DownloadItemListActivity instance;
	
	private void ActualizeInstance(){
		_manager.Actualize(this);
		instance = this;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ActualizeInstance();
		setRefreshMenuButtonVisibility();
		_autoRefreshEnabled = true;
		handleIntent(getIntent());
	}
	
	@Override
	public void onPause(){
		super.onPause();
		_autoRefreshEnabled = false;
	}
	
	public void applyPreferences(){	
		if (updatePreferencesIfNessesary())
			setDownloadItemListManager();
	}
	
	
	private void handleIntent(Intent intent) {
    	
    	Uri data = intent.getData();
    	
    	if (data != null && data.getScheme() != null) {
    		
    		if (data.getScheme().equals("magnet")) {
    			
    			final String link = data.toString();
    			final String fileName = SendMagnetTask.GetNativeFileNameFromMagnetLink(link);
            	final DownloadItemListActivity activityToTransfer = this;
            	
    			new AlertDialog.Builder(this)
    	           .setMessage(String.format("Do you really want to start download %s magnet link?", fileName))
    	           .setCancelable(false)
    	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	               public void onClick(DialogInterface dialog, int id) {
    	            	   new SendMagnetTask(activityToTransfer, link, activityToTransfer.getCacheDir()).execute();
    	        		   Toast.makeText(
    	        				   activityToTransfer,
    	        				   "Magnet link " + fileName + " is queued for download.", Toast.LENGTH_SHORT).show();
    	               }
    	           })
    	           .setNegativeButton("No", null)
    	           .show();
    			
    		} 
    		else if (data.getScheme().equals("file")) {
    			
            	final File file = new File(data.getPath());
            	final DownloadItemListActivity activityToTransfer = this;
            	
    			new AlertDialog.Builder(this)
    	           .setMessage(String.format("Do you really want to start download \"%s\" torrent?", file.getName()))
    	           .setCancelable(false)
    	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	               public void onClick(DialogInterface dialog, int id) {
    	            	   new SendTorrentTask(activityToTransfer, file).execute();
    	        		   Toast.makeText(
    	        				   activityToTransfer,
    	        				   "Torrent \"" + file.getName() + "\" is queued for download.", Toast.LENGTH_SHORT).show();
    	               }
    	           })
    	           .setNegativeButton("No", null)
    	           .show();
    		}	
    		
    		intent.setData(null);
    		setIntent(intent);
    	}
    }
	
	public void setDefaultMessageVisibility(){
		TextView textView = (TextView)findViewById(R.id.no_torrents_label);
		textView.setVisibility(getListView().getCount() == 0 ? View.VISIBLE : View.GONE);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         
        setContentView(R.layout.download_item_list_activity);      
        applyPreferences(); 
        ActualizeInstance();
        showResult(_manager.getDownloadItems());
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        updateMenuItem = menu.findItem(R.id.refresh_list);
        setRefreshMenuButtonVisibility();
        return super.onCreateOptionsMenu(menu);  
    }
    
    private MenuItem updateMenuItem;
    
    private void setRefreshMenuButtonVisibility(){
    	if (updateMenuItem != null)
    		updateMenuItem.setVisible(!getPreferences().isAutoRefreshEnabled());
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
	        	new SendCommandTask(this, "pause_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   "All downloads are queued for pause.", Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.resume_all:
	        	new SendCommandTask(this, "start_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   "All downloads are queued for start.", Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.delete_finished:
	        	final DownloadItemListActivity current = this;
				new AlertDialog.Builder(this)
		           .setMessage("Do you really want to clear all finished downloads?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   new SendCommandTask(current, "clear").execute();	            	   
		        		   Toast.makeText(
		        				   current,
		        				   "All finished downloads are queued for delete.", Toast.LENGTH_SHORT).show();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();        	
	        	return true;
	        case R.id.refresh_list:
	        	sendRefreshRequestIfNesessary();
	     		return true;
	        default:
	            return false;
        }
    }

	@Override
	public void showResult(List<DownloadItem> items) {
		//set adapter
		DownloadItemListAdapter adapter = new DownloadItemListAdapter(this, items, this);		
		ListView list = getListView();
		int savedPosition = list.getFirstVisiblePosition();
	    View firstVisibleView = list.getChildAt(0);
	    int savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();			
		setListAdapter(adapter);
		//set position
		if (savedPosition >= 0)
		    list.setSelectionFromTop(savedPosition, savedListTop);
		//set default label visibility
		setDefaultMessageVisibility();	
	}

	@Override
	public void showErrorMessage(String errorMessage) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean IsEnabled() {
		return _autoRefreshEnabled;
	}
	
	
	private static AutoRefreshProperties _preferences;
	
	private AutoRefreshProperties getPreferences(){
		if (_preferences == null)
			setPreferences(buildCurrentPreferences());
		return _preferences;	
	}
	
	private void setPreferences(AutoRefreshProperties preferences){
		_preferences = preferences;
	}
	
	private boolean updatePreferencesIfNessesary(){
		
		DownloadMasterNetworkDalc.setup(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
		
		AutoRefreshProperties newPreferences = buildCurrentPreferences();
		if (newPreferences.equals(_preferences)){
			return false;
		}else{
			setPreferences(newPreferences);
			return true;
		}
	}
	
	private AutoRefreshProperties buildCurrentPreferences(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean autorefreshEnabled = prefs.getBoolean("autorefreshEnabledPref", true);
		int autorefreshInterval = 10;
		try{
			autorefreshInterval = Integer.parseInt(prefs.getString("autorefreshIntervalPref", "10"));
		}catch(NumberFormatException e){
			
		}
		return new AutoRefreshProperties(autorefreshEnabled, autorefreshInterval);
	}
	
	
	private static IDownloadItemListManager _manager;
	
	private void setDownloadItemListManager(){
		DownloadItemListManagerBase newManager =
				getPreferences().isAutoRefreshEnabled()?
				new AutoRefreshItemListManager(this, getPreferences().getAutoRefreshInterval()).setDisabler(this) :
				new ManualRefreshItemListManager(this);
		
		if (_manager == null)
			_manager = newManager;
		else
			_manager = _manager.switchToNext(newManager);
	}
	
	@Override
	public void sendRefreshRequestIfNesessary(){
		if (_manager instanceof IManualRefreshable){
			((IManualRefreshable) _manager).manualRefresh();
		}
	}

	@Override
	public boolean isItemSelected(DownloadItem item) {
		if (item == null)
			return false;
		return (item.getId() + item.getName()).equals(selectedItemName);
	}

	@Override
	public void setDownloadItemSelected(DownloadItem item) {
		if (item == null)
			return;
		selectedItemName = (item.getId() + item.getName());
		
	}
	
	
	
	

}
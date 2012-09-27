package com.insolence.admclient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadItemListActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<DownloadItem>> {
	
	public static final int LOADER_ID = 1;
	
	private DownloadItemListAdapter adapter;
	private TextView emptyMsg;
	private MenuItem refreshMenuItem;
	
	public static String _connectionString;
	public static String _userName;
	public static String _password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        disableConnectionReuseIfNecessary();
        
        getPrefs(PreferenceManager.getDefaultSharedPreferences(this));
        
        setContentView(R.layout.download_item_list_activity);
        
        adapter = new DownloadItemListAdapter(this);
        
        emptyMsg = (TextView) findViewById(R.id.list_empty);
        emptyMsg.bringToFront();
        emptyMsg.setVisibility(View.GONE);
        
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);
                
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }
    
    private void disableConnectionReuseIfNecessary() {
		// Work around pre-Froyo bugs in HTTP connection reuse.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
    
    public static void getPrefs(SharedPreferences prefs) {
    	_connectionString = prefs.getString("webServerAddrPref", "192.168.1.1") + ":" + prefs.getString("webServerPortPref", "8081");
		_userName = prefs.getString("loginPref", "admin");
		_password = prefs.getString("passwordPref", "password");
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		refreshMenuItem = menu.findItem(R.id.refresh);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean isRunning = false;
    	
    	if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
    		isRunning = true;
    	}
    	
    	if (!isRunning) {
    		// Handle item selection	
        	switch (item.getItemId()) {
        	case R.id.settings:
        		Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
        		startActivity(settingsActivity);
        		return true;
        	case R.id.refresh:
        		refreshMenuItem = item;
        		setMenuItemActionView(true);
        		getSupportLoaderManager().destroyLoader(LOADER_ID);
        		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        		return true;
        	case R.id.pause_all:
        		new SendCommandAsyncTask(this).execute(null, "pause_all");
        		Toast.makeText(this, "All downloads are queued for pause.", Toast.LENGTH_SHORT).show();
        		return true;
        	case R.id.resume_all:
        		new SendCommandAsyncTask(this).execute(null, "start_all");
        		Toast.makeText(this, "All downloads are queued for start.", Toast.LENGTH_SHORT).show();
        		return true;
        	case R.id.delete_finished:
        		Bundle args = new Bundle();
        		args.putString(ConfirmDialogFragment.COMMAND, "clear");
        		args.putString(ConfirmDialogFragment.DIALOG_MSG, "Do you really want to clear all finished downloads?");
        		args.putString(ConfirmDialogFragment.TOAST_MSG, "All finished downloads are queued for deletion.");
        		
        		showConfirmDialog(ConfirmDialogFragment.DELETE_FINISHED_ID, args);
        		return true;
        	default:
        		return false;
        	}
    	}
    	else {
    		Toast.makeText(DownloadItemListActivity.this, "Please wait for refresh of download items!", Toast.LENGTH_LONG).show();
    		return true;
    	}
    }
    
    private void setMenuItemActionView(boolean isRefreshing) {
    	if (refreshMenuItem != null) {
    		if (isRefreshing) {
        		//Set size of progressbar for refresh actionview
        		ProgressBar action_view = new ProgressBar(this);
        		action_view.setIndeterminate(true);
        		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(refreshMenuItem.getIcon().getIntrinsicWidth() / 2, refreshMenuItem.getIcon().getIntrinsicHeight() / 2);
        		action_view.setLayoutParams(params);
        		refreshMenuItem.setActionView(action_view);
        	}
        	else {
        		refreshMenuItem.setActionView(null);
        	}
    	}
	}
    
    public void recreateDownloadItemLoader() {
    	//Don't run refresh again until finished
    	boolean isRefreshing = false;
    	if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
    		isRefreshing = true;
    	}
    	if (!isRefreshing) {
    		setMenuItemActionView(true);
    		//Destroying and recreating loader seems to be only way to call loadInBackground() again
    		getSupportLoaderManager().destroyLoader(LOADER_ID);
    		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    	}
    }
    
    public void showConfirmDialog(int id, Bundle args) {
    	ConfirmDialogFragment previous = (ConfirmDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
    	boolean isResumed = true;
    	if (previous != null && previous.isResumed()) {
			previous.dismiss();
			getSupportFragmentManager().executePendingTransactions();
		}
		else if (previous != null) {
			isResumed = false;
		}
    	ConfirmDialogFragment frag = ConfirmDialogFragment.newInstance(id, args);
    	if (isResumed && frag != null) {
			frag.setCancelable(true);
			frag.show(getSupportFragmentManager(), "dialog");
		}
    }
    
    public void handleIntent(Intent intent) {
    	
    	Uri data = intent.getData();
    	
    	if (data != null && data.getScheme() != null) {
    		
    		Bundle args = new Bundle();
    		
    		if (data.getScheme().equals("magnet")) {
    			String path = data.toString();
    			String[] split = path.split("&dn=");
    			String[] finalSplit = split[1].split("&"); 
    			try {
					String name = URLDecoder.decode(finalSplit[0], "UTF-8");
	    			
	    			args.putString(ConfirmDialogFragment.FILE_NAME, name.replace(" ", "_") + ".torrent");
	    			args.putString(ConfirmDialogFragment.TOAST_MSG, "Torrent \"" + name + "\" is queued for download.");
	    			args.putString(ConfirmDialogFragment.DIALOG_MSG, String.format("Do you really want to start download \"%s\" torrent?", name));
	    			args.putString(ConfirmDialogFragment.SCHEME, data.getScheme());
	        		args.putString(ConfirmDialogFragment.MAGNET_LINK, split[0]);
	        		
	        		showConfirmDialog(ConfirmDialogFragment.LOAD_TORRENT_ID, args);
				} 
    			catch (UnsupportedEncodingException e) {
    				
				}
    			
    			
    		} 
    		else if (data.getScheme().equals("file")) {
    			
    			File file = new File(data.getPath());
    			
    			args.putString(ConfirmDialogFragment.FILE_NAME, file.getName());
    			args.putString(ConfirmDialogFragment.TOAST_MSG, "Torrent \"" + file.getName() + "\" is queued for download.");
    			args.putString(ConfirmDialogFragment.DIALOG_MSG, String.format("Do you really want to start download \"%s\" torrent?", file.getName()));
    			args.putString(ConfirmDialogFragment.SCHEME, data.getScheme());
        		args.putString(ConfirmDialogFragment.URI_STRING, data.toString());
        		
        		showConfirmDialog(ConfirmDialogFragment.LOAD_TORRENT_ID, args);
    		}	
    		
    		//Remove data so not called again if screen sleeps or user resumes
    		intent.setData(null);
    		setIntent(intent);
    	}
//    	else {
//    		//Refresh list if not adding a torrent
//    		recreateDownloadItemLoader();
//    	}
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		handleIntent(getIntent());
	}

	public void setEmptyMsg(String msg) {
		if (msg != null) {
			emptyMsg.setText(msg);
		}
		else {
			//Default
			emptyMsg.setText("No torrents loaded.");
		}
	}

	public Loader<ArrayList<DownloadItem>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
        // sample only has one Loader with no arguments, so it is simple.
		setMenuItemActionView(true);
        return new DownloadItemListLoader(this);
	}

	public void onLoadFinished(Loader<ArrayList<DownloadItem>> loader, ArrayList<DownloadItem> items) {
		setMenuItemActionView(false);
		adapter.setData(items);
		
		if (items == null) {
			emptyMsg.setVisibility(View.VISIBLE);
		}
		else if (items != null && items.isEmpty()) {
			emptyMsg.setVisibility(View.VISIBLE);
		}
		else {
			emptyMsg.setVisibility(View.GONE);
		}
	}

	public void onLoaderReset(Loader<ArrayList<DownloadItem>> loader) {
		adapter.setData(null);
	}
}
package com.insolence.admclient;

import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.insolence.admclient.DownloadItemListAdapter.OnSelectItemListener;
import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.asynctasks.SendLinkTask;
import com.insolence.admclient.asynctasks.SendTorrentTask;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.service.RefreshItemListBroadcastReceiver;
import com.insolence.admclient.storage.DownloadItemStorage;
import com.insolence.admclient.storage.PreferenceAccessor;
import com.insolence.admclient.util.ClipboardUtil;
import com.insolence.admclient.util.Holder;
import com.insolence.admclient.util.FriendlyNameUtil;
import com.insolence.admclient.util.LanguageHelper;

public class DownloadItemListActivity extends SherlockListActivity implements OnSelectItemListener{
	
	String selectedItemName;
	
	private static DownloadItemListActivity _current;
	
	public static DownloadItemListActivity getCurrent(){
		return _current;
	}
	
	@Override
	public void onResume(){
		super.onResume();		
		_current = this;
		new RefreshItemListBroadcastReceiver().resetAlarm(this);
		switchRefreshAnimation(false);
		handleIntent(getIntent());
	}
	
	@Override
	public void onPause(){
		super.onPause();
		_current = null;
		new RefreshItemListBroadcastReceiver().resetAlarm(this);
	}

	private void handleIntent(Intent intent) {   	
    	final Uri data = intent.getData();   	
    	if (data != null && data.getScheme() != null) {   		
    		if (data.getScheme().equals("magnet")) {   			
    			final String link = data.toString();
    			final String fileName = FriendlyNameUtil.GetNativeFileNameFromMagnetLink(link);           	
    			sendLinkToServer(link, fileName);	
    		} 
    		else {
    			sendTorrentFileToServer(data);
            }
    		intent.setData(null);
    		setIntent(intent);
    	}
    }
	
	private void sendTorrentFileToServer(final Uri fileUri){
		final String fileName = new FriendlyNameUtil(this).getUriFileName(fileUri);
    	
		new AlertDialog.Builder(this)
	           .setMessage(String.format(getStr(R.string.confirmation_message_download_torrent), fileName))
	           .setCancelable(false)
	           .setPositiveButton(getStr(R.string.basic_yes), new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   new SendTorrentTask(DownloadItemListActivity.this, fileUri, fileName).execute();
	        		   Toast.makeText(
	        				   DownloadItemListActivity.this,
	        				   String.format(getStr(R.string.command_info_download_torrent), fileName), Toast.LENGTH_SHORT).show();
	               }
	     }).setNegativeButton(getStr(R.string.basic_no), null)
	     .show();		
	}
	
	private void sendLinkToServer(final String link, final String linkName){
		new AlertDialog.Builder(this)
        .setMessage(String.format(getStr(R.string.confirmation_message_download_magnet_link), linkName))
        .setCancelable(false)
        .setPositiveButton(getStr(R.string.basic_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
         	   //new SendMagnetTask(DownloadItemListActivity.this, link, DownloadItemListActivity.this.getCacheDir()).execute();
         	   new SendLinkTask(DownloadItemListActivity.this, link).execute();
     		   Toast.makeText(
     				   DownloadItemListActivity.this,
     				   String.format(getStr(R.string.command_info_download_magnet_link), linkName), Toast.LENGTH_SHORT).show();
            }
        })
        .setNegativeButton(getStr(R.string.basic_no), null)
        .show();		
	}
	
	private String getStr(int resourceId){
		return getResources().getString(resourceId);
	}
	
	public void setDefaultMessageVisibility(){
		TextView textView = (TextView)findViewById(R.id.no_torrents_label);
		textView.setVisibility(getListView().getCount() == 0 ? View.VISIBLE : View.GONE);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.setLanguage(this);
        setContentView(R.layout.download_item_list_activity);      
        updateListView();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        updateMenuItem = menu.findItem(R.id.refresh_list);
        return super.onCreateOptionsMenu(menu);  
    }
    
    private MenuItem updateMenuItem;
    
    public void switchRefreshAnimation(boolean enabled) {   	
    	if (updateMenuItem != null){
    		if (enabled){
		        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
		        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
		        rotation.setRepeatCount(Animation.INFINITE);
		        iv.startAnimation(rotation);	
		        updateMenuItem.setActionView(iv);
    		}else{
    			View actionView = updateMenuItem.getActionView();
        		if (actionView != null)
        			actionView.clearAnimation();
    	    	updateMenuItem.setActionView(null);   			
    		}
    	}
    }  
    
    private static int fileSelectorRequestCode = 12412;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection	
        switch (item.getItemId()) {
	        case R.id.settings:
	        	Intent settingsActivity = new Intent(getBaseContext(),
                        Preferences.class);
	        	startActivity(settingsActivity);
	            return true;
	        case R.id.add_torrent:
	            Intent intent = new Intent();
	            intent.addCategory(Intent.CATEGORY_OPENABLE);
	            intent.setType("application/x-bittorrent");
	            intent.setAction(Intent.ACTION_GET_CONTENT);
	            startActivityForResult(Intent.createChooser(intent, getStr(R.string.add_torrent_alert_title)), fileSelectorRequestCode);	
	            return true;
	        case R.id.add_link:
	        	final EditText txtUrl = new EditText(this);
	        	txtUrl.setSingleLine();
	        	txtUrl.setHint(getStr(R.string.add_link_alert_hint));       	
	        	Holder<String> clipboardText = new Holder<String>("");
	        	if (ClipboardUtil.TryGetTextFromClipboard(this, clipboardText))
	        		txtUrl.setText(clipboardText.value);
	        	new AlertDialog.Builder(this)
		        	 .setTitle(getStr(R.string.add_link_alert_title))
		        	 .setMessage(getStr(R.string.add_link_alert_message))
		        	 .setView(txtUrl)
		        	 .setPositiveButton(getStr(R.string.basic_yes), new DialogInterface.OnClickListener() {
		        	    public void onClick(DialogInterface dialog, int whichButton) {
			        	    String link = txtUrl.getText().toString();
			        	    if (link == null || link.length() == 0)
			        	    	return;
			      			final String fileName = FriendlyNameUtil.GetNativeFileNameFromMagnetLink(link);     	
			    			sendLinkToServer(link, fileName);       	      
		        	    }
		        	  })
		        	 .setNegativeButton(getStr(R.string.basic_cancel), null)
		        	 .show(); 	        	
	        	return true;
	        case R.id.pause_all:
	        	new SendCommandTask(DownloadItemListActivity.this, "pause_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   getStr(R.string.command_info_pause_all), Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.resume_all:
	        	new SendCommandTask(DownloadItemListActivity.this, "start_all").execute();
	     		Toast.makeText(
	    				   this,
	    				   getStr(R.string.command_info_resume_all), Toast.LENGTH_SHORT).show();
	        	return true;
	        case R.id.delete_finished:
				new AlertDialog.Builder(this)
		           .setMessage(getStr(R.string.confirmation_message_delete_finished))
		           .setCancelable(false)
		           .setPositiveButton(getStr(R.string.basic_yes), new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   new SendCommandTask(DownloadItemListActivity.this, "clear").execute();	            	   
		        		   Toast.makeText(
		        				   DownloadItemListActivity.this,
		        				   getStr(R.string.command_info_delete_finished), Toast.LENGTH_SHORT).show();
		               }
		           })
		           .setNegativeButton(getStr(R.string.basic_no), null)
		           .show();        	
	        	return true;
	        case R.id.kill_exit:
	        	stopAppAndService();
	        	return true;
	        case R.id.refresh_list:
	        	sendRefreshRequest();
	     		return true;
	        default:
	            return false;
        }
    }
    
    private void stopAppAndService(){
    	new RefreshItemListBroadcastReceiver().cancelAlarm(this);
    	finish();   	
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == fileSelectorRequestCode){
    		if (data != null){
	    		Uri selected = data.getData();
	    		if (selected != null)
	    			sendTorrentFileToServer(selected);
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
	public void showDownloadItemList(List<DownloadItem> items) {
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
	
	
	public void updateListView(){
		showDownloadItemList(DownloadItemStorage.getInstance(this).getDownloadItems());
	}
	
	public void showErrorMessage(String errorMessage) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
	}
	
	public void sendRefreshRequest(){
		new RefreshItemListBroadcastReceiver().runAlarmImmidiately(this);
	}

	@Override
	public boolean isItemSelected(DownloadItem item) {
		if (item == null)
			return false;
		return (item.getId() + item.getName()).equals(selectedItemName);
	}

	@Override
	public void setDownloadItemSelected(DownloadItem item) {
		if (item == null){
			selectedItemName = null;
			return;
		}
		selectedItemName = (item.getId() + item.getName());
		
	}
	
	
	
	

}
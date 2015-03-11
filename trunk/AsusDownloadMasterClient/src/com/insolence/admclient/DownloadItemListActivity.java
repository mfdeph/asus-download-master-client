package com.insolence.admclient;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable.IconState;
import com.balysv.materialmenu.MaterialMenuDrawable.Stroke;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.faizmalkani.floatingactionbutton.Fab;
import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.asynctasks.SendLinkTask;
import com.insolence.admclient.asynctasks.SendTorrentTask;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.expandable.ExpandCollapseManagerCreator;
import com.insolence.admclient.expandable.IExpandCollapseManager;
import com.insolence.admclient.service.RefreshItemListBroadcastReceiver;
import com.insolence.admclient.storage.DownloadItemStorage;
import com.insolence.admclient.storage.PreferenceAccessor;
import com.insolence.admclient.util.ClipboardUtil;
import com.insolence.admclient.util.Holder;
import com.insolence.admclient.util.FriendlyNameUtil;
import com.insolence.admclient.util.LanguageHelper;

public class DownloadItemListActivity extends ActionBarActivity implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{
	
	private static DownloadItemListActivity _current;
	
	public static DownloadItemListActivity getCurrent(){
		return _current;
	}	
	
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
	public void onResume(){
		super.onResume();		
		_current = this;
		new RefreshItemListBroadcastReceiver().resetAlarm(this);
		//switchRefreshAnimation(false);
		handleIntent(getIntent());
		if (_adapter != null){
			_adapter.setExpandCollapseManager(getExpandCollapseManager());
			_adapter.notifyDataSetChanged();
		}
		getListView().setNumColumns(_expandCollapseManager.isMultiColumnsAllowed() ? GridView.AUTO_FIT : 1);
		updateListView();
		TextView menuTitle = (TextView) findViewById(R.id.menu_title);
		menuTitle.setText(PreferenceAccessor.getInstance(this).getWebServerAddress());
		
		if (PreferenceAccessor.getInstance(this).isHideCompletedFilterActive())
			findViewById(R.id.filter_highlight).setVisibility(View.VISIBLE);
		
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
		TextView textView = (TextView) findViewById(R.id.no_torrents_label);
		textView.setVisibility(getListView().getCount() == 0 ? View.VISIBLE : View.GONE);
	}
	
	SwipeRefreshLayout mSwipeRefreshLayout;
	
    private MaterialMenuIconToolbar materialMenu;

    DrawerLayout drawer;
    
    private class MyDrawerListener implements android.support.v4.widget.DrawerLayout.DrawerListener {
        @Override
        public void onDrawerClosed(View view) {
        	materialMenu.animatePressedState(IconState.BURGER);
        }

		@Override
		public void onDrawerOpened(View arg0) {
			materialMenu.animatePressedState(IconState.ARROW);
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			if (newState == DrawerLayout.STATE_SETTLING) {
	            if (!drawer.isDrawerOpen(Gravity.START)) {
	            	materialMenu.animatePressedState(IconState.ARROW);
	            } else {
	            	materialMenu.animatePressedState(IconState.BURGER);
	            }
	        }
		}

    }
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.setLanguage(this);
        setContentView(R.layout.download_item_list_activity);      
        
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new MyDrawerListener());
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
            	if (drawer.isDrawerOpen(Gravity.LEFT))
            		drawer.closeDrawer(Gravity.LEFT);
            	else
            		drawer.openDrawer(Gravity.LEFT);
            }
        });
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, Stroke.THIN) {
            @Override public int getToolbarViewId() {
                return R.id.toolbar;
            }
        };
        materialMenu.setNeverDrawTouch(true);
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue);
       
        updateListView();
        getListView().setOnItemClickListener(this);
        
        Fab addFloatingButton = (Fab) findViewById(R.id.add_floating_button);
        addFloatingButton.setFabColor(getResources().getColor(R.color.main_blue));
        addFloatingButton.setFabDrawable(getResources().getDrawable(R.drawable.ic_material_add_128));       
        
        Fab settingsFloatingButton = (Fab) findViewById(R.id.settings_floating_button);
        
        if (PreferenceAccessor.getInstance(this).isSettingsOk()){
        	settingsFloatingButton.setVisibility(View.GONE);
        }else{
        	settingsFloatingButton.setVisibility(View.VISIBLE);
	        settingsFloatingButton.setFabColor(getResources().getColor(R.color.warn_red));
	        settingsFloatingButton.setFabDrawable(getResources().getDrawable(R.drawable.ic_settings_black_48dp));
        }
    }
    
    
    @Override
    public void onRefresh() {
    	sendRefreshRequest();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //updateMenuItem = menu.findItem(R.id.refresh_list);
        return super.onCreateOptionsMenu(menu);  
    }
    
    public void switchRefreshAnimation(boolean enabled) {   	
    		if (enabled){
    			mSwipeRefreshLayout.setRefreshing(true);
    		}else{
    			mSwipeRefreshLayout.setRefreshing(false);    			   	        
    	        if (PreferenceAccessor.getInstance(this).isSettingsOk())
    	        	findViewById(R.id.settings_floating_button).setVisibility(View.GONE);  			
    		}
    }  
    
    private static int fileSelectorRequestCode = 12412;
    
	public void onSettingsClick(View v) {
		Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
    	startActivity(settingsActivity);
	}
	
	public void onPauseAllClick(View v) {
		new SendCommandTask(DownloadItemListActivity.this, "pause_all").execute();
 		Toast.makeText(this, getStr(R.string.command_info_pause_all), Toast.LENGTH_SHORT).show();
	}
	
	public void onResumeAllClick(View v) {
		new SendCommandTask(DownloadItemListActivity.this, "start_all").execute();
 		Toast.makeText(this, getStr(R.string.command_info_resume_all), Toast.LENGTH_SHORT).show();	
	}
	
	public void onDeleteFinishedClick(View v) {
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
	}
	
	public void onKillExitClick(View v) {
		stopAppAndService();
	}
		
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {	
        switch (item.getItemId()) {
        	case R.id.filter:
        		MenuBuilder builder = buildFilterMenu();
    			MenuPopupHelper helper = new MenuPopupHelper(this, builder);
    			helper.setAnchorView(findViewById(R.id.filter));
    			helper.setForceShowIcon(true);  			
    			helper.show();     	
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
    
    
    private DownloadItemListAdapter _adapter;
    
    private void actualizeAdapter(List<DownloadItem> items){
    	if (_adapter == null){
    		_adapter = new DownloadItemListAdapter(this, items, getExpandCollapseManager());
    		setListAdapter(_adapter);    		
    	}else{
    		_adapter.clear();
			
			for (int i = 0; i < items.size(); i++)
				_adapter.add(items.get(i));

    		_adapter.notifyDataSetChanged();
    	}
    }
   
    private static IExpandCollapseManager _expandCollapseManager;
    
    private IExpandCollapseManager getExpandCollapseManager(){
    	if (_expandCollapseManager == null){
    		_expandCollapseManager = ExpandCollapseManagerCreator.createActual(this);
    	}
    	return _expandCollapseManager;
    }
    
    public static void resetExpandCollapseManager(){
    	_expandCollapseManager = null;
    }
    
    @Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {	
		DownloadItem downloadItem = (DownloadItem) getListView().getItemAtPosition(position);		
		getExpandCollapseManager().clickItem(downloadItem);		
		_adapter.notifyDataSetChanged();
	}
    
	public void showDownloadItemList(List<DownloadItem> items) {

	    actualizeAdapter(items);
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
	
	private GridView getListView(){
		return (GridView) findViewById(R.id.download_item_list);
	}
	
	private void setListAdapter(ListAdapter adapter){
		getListView().setAdapter(adapter);
	}
	
	public void onAddButtonClick(View v) {
			
			MenuBuilder builder = buildAddMenu();
			MenuPopupHelper helper = new MenuPopupHelper(this, builder);
			helper.setAnchorView(v);
			helper.setForceShowIcon(true);		
			helper.show();				
	}
		
	private MenuBuilder buildAddMenu(){
		
		MenuBuilder builder = new MenuBuilder(this);
		
		MenuItem searchMenuItem = builder.add(getStr(R.string.search_title));
		searchMenuItem.setIcon(R.drawable.ic_search_grey600_48dp);
		searchMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Dialog searchDialog = new SearchDialog(DownloadItemListActivity.this);
				// Setting dialogview
			    Window window = searchDialog.getWindow();
			    window.setGravity(Gravity.TOP);

			    window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				searchDialog.show();
				return true;
			}
		});
		
		MenuItem addTorrentMenuItem = builder.add(getStr(R.string.menu_item_add_torrent));
		addTorrentMenuItem.setIcon(R.drawable.ic_file_upload_grey600_48dp);
		addTorrentMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
	            intent.addCategory(Intent.CATEGORY_OPENABLE);
	            intent.setType("application/*");
	            intent.setAction(Intent.ACTION_GET_CONTENT);
	            startActivityForResult(Intent.createChooser(intent, getStr(R.string.add_torrent_alert_title)), fileSelectorRequestCode);	
	            return true;
			}
		});	
		
		MenuItem addMagnetMenuItem = builder.add(getStr(R.string.menu_item_add_link));
		addMagnetMenuItem.setIcon(R.drawable.ic_link_grey600_48dp);
		addMagnetMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
			@Override
			public boolean onMenuItemClick(MenuItem item) {
	        	final EditText txtUrl = new EditText(DownloadItemListActivity.this);
	        	txtUrl.setSingleLine();
	        	txtUrl.setHint(getStr(R.string.add_link_alert_hint));       	
	        	Holder<String> clipboardText = new Holder<String>("");
	        	if (ClipboardUtil.TryGetTextFromClipboard(DownloadItemListActivity.this, clipboardText))
	        		txtUrl.setText(clipboardText.value);
	        	new AlertDialog.Builder(DownloadItemListActivity.this)
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
			}
		});	
		
	
	    return builder;
	}
	
	private MenuBuilder buildFilterMenu(){
		
		MenuBuilder builder = new MenuBuilder(this);
		
		final PreferenceAccessor preferences = PreferenceAccessor.getInstance(this);
		
		if (preferences.isHideCompletedFilterActive()){
			MenuItem showAllMenuItem = builder.add(getStr(R.string.filter_menu_show_all));
			showAllMenuItem.setIcon(R.drawable.ic_filter_list_grey600_48dp);
			showAllMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					preferences.setHideCompletedFilterActive(false);
					updateListView();
					findViewById(R.id.filter_highlight).setVisibility(View.GONE);
					return true;
				}
			});
		}else{
			MenuItem hideCompletedMenuItem = builder.add(getStr(R.string.filter_menu_hide_completed));
			hideCompletedMenuItem.setIcon(R.drawable.ic_filter_list_grey600_48dp);
			hideCompletedMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {		
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					preferences.setHideCompletedFilterActive(true);
					updateListView();
					findViewById(R.id.filter_highlight).setVisibility(View.VISIBLE);
					return true;
				}
			});
		}
	
	    return builder;
	}

}
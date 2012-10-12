package com.insolence.admclient;

import android.app.ListActivity;

public class ItemListRequestController {

	private static ItemListRequestController _instance;
	
	public static ItemListRequestController getInstance(){
		if (_instance == null)
			_instance = new ItemListRequestController();
		return _instance;
	}
	
	private boolean _enabled = true;
	
	public void Enable(){
		_enabled = true;
	}
	
	public void Disable(){
		_enabled = false;
	}
	
	public void DoRequest(ListActivity context){
		if (_enabled)
			DoRequestForced(context);
	}
	
	public void DoRequestForced(ListActivity context){
		new GetDownloadItemListAsyncTask(context).execute();
	}
	
}

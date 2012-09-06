package com.insolence.admclient;

import java.io.File;

import android.app.ListActivity;
import android.os.AsyncTask;

public class SendFileAsyncTask extends AsyncTask<Void, Void, Void>{
	protected ListActivity _listActivity;
	protected File _file;
	
	public SendFileAsyncTask(ListActivity listActivity, File file){
		_listActivity = listActivity;
		_file = file;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		DownloadItemListManager.getInstance().SendFile(_file);
		DownloadItemListManager.getInstance().getDownloadItems(true);
		return null;
	}
}

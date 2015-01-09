package com.insolence.admclient.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.StaticContextApp;

public abstract class SendTaskBase extends AsyncTask<Void, Void, AsyncTaskResult>{
	
	protected Context _context;
	
	public SendTaskBase(Context context){
		_context = context;
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){
		if (result.isSucceed()){
			if (DownloadItemListActivity.getCurrent() != null)
				DownloadItemListActivity.getCurrent().sendRefreshRequest();
		}else{
			
			if (DownloadItemListActivity.getCurrent() != null)
				DownloadItemListActivity.getCurrent().showErrorMessage(result.getMessage());
		}
	}
	
	protected String getStr(int resourceId){
		return StaticContextApp.getContext().getResources().getString(resourceId);
	}
}

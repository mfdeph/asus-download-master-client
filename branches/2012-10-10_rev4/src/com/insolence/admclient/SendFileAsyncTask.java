package com.insolence.admclient;

import java.io.File;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendFileAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult>{
	protected ListActivity _listActivity;
	protected File _file;
	
	public SendFileAsyncTask(ListActivity listActivity, File file){
		_listActivity = listActivity;
		_file = file;
	}
	
	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		if (DownloadItemListManager.getInstance().SendFile(_file)){
			DownloadItemListManager.getInstance().getDownloadItems(true);
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){
		if (!result.IsSucceed)
			Toast.makeText(_listActivity, result.Message, Toast.LENGTH_LONG).show();
	}
}

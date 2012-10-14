package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.AsyncTaskResult;
import com.insolence.admclient.DownloadItem;
import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.DownloadItemListAdapter;
import com.insolence.admclient.DownloadItemListManager;
import com.insolence.admclient.listmanagers.IDownloadItemListManager;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class GetItemListAsyncTask  extends AsyncTask<Void, Void, GetItemListResult>{

	protected IDownloadItemListManager _manager;
	
	public GetItemListAsyncTask(IDownloadItemListManager manager){
		_manager = manager;
	}	

	@Override
	protected GetItemListResult doInBackground(Void... arg0) {
		GetItemListResult result = new GetItemListResult();
		ArrayList<DownloadItem> downloadItems = DownloadItemListManager.getInstance().DirectGetDownloadItems();
		if (downloadItems == null){
			result.setSucceed(false);
			result.setMessage("Cannot connect to Download Master service");
		}else{
			result.setSucceed(true);
			result.setMessage("Succeed");
			result.setDownloadItems(downloadItems);
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(GetItemListResult result){		
		_manager.postProcessResult(result);		
	}
}

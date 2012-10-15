package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.DownloadItem;
import com.insolence.admclient.listmanagers.IDownloadItemListManager;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

import android.os.AsyncTask;

public class GetItemListTask  extends AsyncTask<Void, Void, GetItemListResult>{

	protected IDownloadItemListManager _manager;
	
	public GetItemListTask(IDownloadItemListManager manager){
		_manager = manager;
	}	

	@Override
	protected GetItemListResult doInBackground(Void... arg0) {
		GetItemListResult result = new GetItemListResult();
		ArrayList<DownloadItem> downloadItems = DownloadMasterNetworkDalc.getInstance().getDownloadItems();
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

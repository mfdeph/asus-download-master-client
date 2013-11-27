package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.R;
import com.insolence.admclient.StaticContextApp;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.entity.IGetItemListResultPostProcessor;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

import android.os.AsyncTask;

public class GetItemListTask  extends AsyncTask<Void, Void, GetItemListResult>{

	protected IGetItemListResultPostProcessor _postProcessor;
	
	public GetItemListTask(IGetItemListResultPostProcessor manager){
		_postProcessor = manager;
	}	

	@Override
	protected GetItemListResult doInBackground(Void... arg0) {
		GetItemListResult result = new GetItemListResult();                                                                                                                                                                                                                                                                                                                                                                              
		ArrayList<DownloadItem> downloadItems = DownloadMasterNetworkDalc.getInstance().getDownloadItems();
		if (downloadItems == null){
			result.setSucceed(false);
			result.setMessage(getStr(R.string.command_info_cannot_connect));
		}else{
			result.setSucceed(true);
			result.setMessage("Succeed");
			result.setDownloadItems(downloadItems);
		}
		return result;
	}
	
	private String getStr(int resourceId){
		return StaticContextApp.getContext().getResources().getString(resourceId);
	}
	
	@Override
	protected void onPostExecute(GetItemListResult result){		
		_postProcessor.postProcessResult(result);		
	}
}

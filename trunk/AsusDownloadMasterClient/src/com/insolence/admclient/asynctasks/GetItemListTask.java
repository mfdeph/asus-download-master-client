package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.R;
import com.insolence.admclient.StaticContextApp;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.entity.IGetItemListResultPostProcessor;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

import android.content.Context;
import android.os.AsyncTask;

public class GetItemListTask  extends AsyncTask<Void, Void, GetItemListResult>{

	protected IGetItemListResultPostProcessor _postProcessor;
	
	private Context _context;
	
	public GetItemListTask(Context context, IGetItemListResultPostProcessor manager){
		_context = context;
		_postProcessor = manager;
	}	

	@Override
	protected GetItemListResult doInBackground(Void... arg0) {
		GetItemListResult result = new GetItemListResult();
		ArrayList<DownloadItem> downloadItems = new DownloadMasterNetworkDalc(_context).getDownloadItems();
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

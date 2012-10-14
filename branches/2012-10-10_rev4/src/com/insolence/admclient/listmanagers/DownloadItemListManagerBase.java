package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import com.insolence.admclient.DownloadItem;
import com.insolence.admclient.asynctasks.GetItemListAsyncTask;
import com.insolence.admclient.asynctasks.GetItemListResult;

public abstract class DownloadItemListManagerBase implements IDownloadItemListManager{

	protected ArrayList<DownloadItem> DownloadItems = new ArrayList<DownloadItem>();

	protected IProcessResultConsumer ProcessResultConsumer;
	
	protected DownloadItemListManagerBase(IProcessResultConsumer processResultConsumer){
		ProcessResultConsumer = processResultConsumer;
	}
	
	@Override
	public void postProcessResult(GetItemListResult result) {
		if (result.isSucceed()){
			DownloadItems = result.getDownloadItems();
			ProcessResultConsumer.ShowResult(DownloadItems);
		}else{
			ProcessResultConsumer.ShowErrorMessage(result.getMessage());
		}
		
	}
	
	public ArrayList<DownloadItem> getDownloadItems() {
		return DownloadItems;
	}


}


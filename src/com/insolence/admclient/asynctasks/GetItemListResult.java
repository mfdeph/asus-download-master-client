package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.entity.DownloadItem;

public class GetItemListResult extends AsyncTaskResult{
	
	public GetItemListResult(){
		super();
	}
	
	public GetItemListResult(boolean isSucceed, String message) {
		super(isSucceed, message);
	}

	public ArrayList<DownloadItem> getDownloadItems() {
		return downloadItems;
	}
	public void setDownloadItems(ArrayList<DownloadItem> downloadItems) {
		this.downloadItems = downloadItems;
	}

	private ArrayList<DownloadItem> downloadItems;
	
}

package com.insolence.admclient.asynctasks;

import java.util.ArrayList;

import com.insolence.admclient.DownloadItem;

public class GetItemListResult {

	public GetItemListResult(){
	}
	
	public boolean isSucceed() {
		return isSucceed;
	}
	public void setSucceed(boolean isSucceed) {
		this.isSucceed = isSucceed;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<DownloadItem> getDownloadItems() {
		return downloadItems;
	}
	public void setDownloadItems(ArrayList<DownloadItem> downloadItems) {
		this.downloadItems = downloadItems;
	}

	private boolean isSucceed;	
	private String message;
	private ArrayList<DownloadItem> downloadItems;
	
}

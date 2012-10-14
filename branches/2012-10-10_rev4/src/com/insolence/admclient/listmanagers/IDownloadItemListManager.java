package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import com.insolence.admclient.DownloadItem;
import com.insolence.admclient.asynctasks.GetItemListResult;

public interface IDownloadItemListManager {

	ArrayList<DownloadItem> getDownloadItems();
	
	void postProcessResult(GetItemListResult result);
	
}

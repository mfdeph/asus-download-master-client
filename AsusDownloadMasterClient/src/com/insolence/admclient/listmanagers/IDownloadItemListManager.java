package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import com.insolence.admclient.asynctasks.GetItemListResult;
import com.insolence.admclient.entity.DownloadItem;

public interface IDownloadItemListManager {

	void Actualize(IProcessResultConsumer processResultConsumer);
	
	ArrayList<DownloadItem> getDownloadItems();
	
	void postProcessResult(GetItemListResult result);
	
	IDownloadItemListManager switchToNext(IDownloadItemListManager nextManager);
	
}

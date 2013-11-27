package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.entity.IGetItemListResultPostProcessor;

public interface IDownloadItemListManager extends IGetItemListResultPostProcessor{

	void Actualize(IProcessResultConsumer processResultConsumer);
	
	ArrayList<DownloadItem> getDownloadItems();
	
	IDownloadItemListManager switchToNext(IDownloadItemListManager nextManager);
	
}

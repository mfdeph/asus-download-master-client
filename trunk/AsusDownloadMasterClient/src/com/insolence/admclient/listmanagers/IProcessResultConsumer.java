package com.insolence.admclient.listmanagers;

import java.util.List;

import com.insolence.admclient.DownloadItem;

public interface IProcessResultConsumer {

	void showResult(List<DownloadItem> items);
	
	void showErrorMessage(String errorMessage);
	
	void sendRefreshRequestIfNesessary();
	
}

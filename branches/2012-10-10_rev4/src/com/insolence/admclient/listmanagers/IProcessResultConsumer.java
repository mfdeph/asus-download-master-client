package com.insolence.admclient.listmanagers;

import java.util.List;

import com.insolence.admclient.DownloadItem;

public interface IProcessResultConsumer {

	void ShowResult(List<DownloadItem> items);
	
	void ShowErrorMessage(String errorMessage);
}

package com.insolence.admclient.expandable;

import android.view.View;

import com.insolence.admclient.entity.DownloadItem;

public interface IExpandCollapseManager {
	
	void setItemState(DownloadItem downloadItem, View view);
	
	void clickItem(DownloadItem downloadItem);
	
	boolean isMultiColumnsAllowed();
}

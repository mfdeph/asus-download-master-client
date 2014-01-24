package com.insolence.admclient.expandable;

import com.insolence.admclient.entity.DownloadItem;

public class FullViewExpandCollapseManager extends ExpandCollapseManagerBase{
	
	@Override
	protected boolean isItemExpanded(DownloadItem downloadItem) {
		return true;
	}
	
	@Override
	public void clickItem(DownloadItem downloadItem) {

	}
}

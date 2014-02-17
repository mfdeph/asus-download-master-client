package com.insolence.admclient.expandable;

import com.insolence.admclient.entity.DownloadItem;

public class CompactExpandCollapseManager extends ExpandCollapseManagerBase{

	@Override
	public void clickItem(DownloadItem downloadItem) {
		boolean isItemExpanded = isItemExpanded(downloadItem);
		expandedItemNames.clear();		
		if (!isItemExpanded)
			expandedItemNames.add(getUniqueString(downloadItem));
	}
	
}

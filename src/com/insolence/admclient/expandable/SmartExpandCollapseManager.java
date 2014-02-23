package com.insolence.admclient.expandable;

import java.util.ArrayList;
import java.util.List;

import com.insolence.admclient.entity.DownloadItem;

public class SmartExpandCollapseManager extends ExpandCollapseManagerBase{

	protected List<String> collapsedItemNames = new ArrayList<String>();
	
	@Override
	protected boolean isItemExpanded(DownloadItem downloadItem) {
		for (String name : expandedItemNames)
			if (getUniqueString(downloadItem).equals(name))
				return true;
		for (String name : collapsedItemNames)
			if (getUniqueString(downloadItem).equals(name))
				return false;
		return downloadItem.getStatus().equalsIgnoreCase("downloading");
	}
	
	@Override
	public void clickItem(DownloadItem downloadItem) {
		String uniqueStr = getUniqueString(downloadItem);
		if (isItemExpanded(downloadItem)){
			expandedItemNames.remove(uniqueStr);
			if (downloadItem.getStatus().equalsIgnoreCase("downloading"))
				collapsedItemNames.add(uniqueStr);
		}
		else{
			collapsedItemNames.remove(uniqueStr);
			if (!downloadItem.getStatus().equalsIgnoreCase("downloading"))
				expandedItemNames.add(uniqueStr);
		}
	}
	
}

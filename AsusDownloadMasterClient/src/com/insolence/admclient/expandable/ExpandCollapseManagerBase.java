package com.insolence.admclient.expandable;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.insolence.admclient.R;
import com.insolence.admclient.entity.DownloadItem;

public class ExpandCollapseManagerBase implements IExpandCollapseManager {
	
	public ExpandCollapseManagerBase(){
		
	}
	
	protected List<String> expandedItemNames = new ArrayList<String>();
	
	protected boolean isItemExpanded(DownloadItem downloadItem) {
		for (String name : expandedItemNames)
			if (getUniqueString(downloadItem).equals(name))
				return true;
		return false;
	}
	
	protected String getUniqueString(DownloadItem item){
		return item.getId() + item.getName();
	}
	
	
	@Override
	public void clickItem(DownloadItem downloadItem) {
		if (isItemExpanded(downloadItem))
			expandedItemNames.remove(getUniqueString(downloadItem));
		else
			expandedItemNames.add(getUniqueString(downloadItem));
	}

	@Override
	public void setItemState(DownloadItem downloadItem, View view) {
		if (isItemExpanded(downloadItem)){
			view.findViewById(R.id.view_additional_info_1).setVisibility(View.VISIBLE);
			view.findViewById(R.id.view_additional_info_2).setVisibility(View.VISIBLE);
			view.findViewById(R.id.download_item_summary).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(4);
		}
		else{
			view.findViewById(R.id.view_additional_info_1).setVisibility(View.GONE);
			view.findViewById(R.id.view_additional_info_2).setVisibility(View.GONE);
			view.findViewById(R.id.download_item_summary).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(1);			
		}
	}

}

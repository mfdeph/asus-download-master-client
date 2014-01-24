package com.insolence.admclient;

import android.view.View;
import android.widget.TextView;

public class ExpandCollapseListViewItemHelper {
	
	public static void expandItem(View view){
		view.findViewById(R.id.view_additional_info_1).setVisibility(View.VISIBLE);
		view.findViewById(R.id.view_additional_info_2).setVisibility(View.VISIBLE);
		view.findViewById(R.id.download_item_summary).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(4);
	}
	
	public static void collapseItem(View view){
		view.findViewById(R.id.view_additional_info_1).setVisibility(View.GONE);
		view.findViewById(R.id.view_additional_info_2).setVisibility(View.GONE);
		view.findViewById(R.id.download_item_summary).setVisibility(View.VISIBLE);
		((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(1);
	}
}

package com.insolence.admclient.expandable;

import com.insolence.admclient.storage.PreferenceAccessor;

import android.content.Context;

public class ExpandCollapseManagerCreator {
	
	public static IExpandCollapseManager createActual(Context context){
		int displayMode = PreferenceAccessor.getInstance(context).getDisplayMode();
		switch(displayMode){
			case 2:
				return new FullViewExpandCollapseManager();
			case 3:
				return new SmartExpandCollapseManager();
			default:
				return new CompactExpandCollapseManager();
		}
	}
	
}

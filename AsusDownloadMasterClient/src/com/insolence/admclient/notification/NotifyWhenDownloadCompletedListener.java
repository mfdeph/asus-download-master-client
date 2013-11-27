package com.insolence.admclient.notification;

import android.content.Context;

import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.storage.DownloadItemStorage.IOnDownloadStatusChangedListener;

public class NotifyWhenDownloadCompletedListener implements IOnDownloadStatusChangedListener{

	private Context _context;
	
	public NotifyWhenDownloadCompletedListener(Context context){
		_context = context;
	}
	
	public void onDownloadStatusChanged(DownloadItem item, String previousStatus) {
		if (previousStatus != null && 
			previousStatus.equalsIgnoreCase("downloading") && 
				(item.getStatus().equalsIgnoreCase("seeding") || 
				item.getStatus().equalsIgnoreCase("completed"))){
			NotificationBuilder.getInstance().BuildNotification(_context, item);
		}
	}

}

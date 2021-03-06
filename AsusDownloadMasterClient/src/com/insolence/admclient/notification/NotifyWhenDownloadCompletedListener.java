package com.insolence.admclient.notification;

import android.content.Context;

import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.storage.DownloadItemStorage.IOnDownloadStatusChangedListener;

public class NotifyWhenDownloadCompletedListener implements IOnDownloadStatusChangedListener{

	private Context _context;
	
	public NotifyWhenDownloadCompletedListener(Context context){
		_context = context;
	}
	
	public void onDownloadStatusChanged(DownloadItem item, String previousStatus) {
		//���������� ����������� ������ ���� ���������� ���������
		if (DownloadItemListActivity.getCurrent() == null)
			if (previousStatus != null && 
				previousStatus.equalsIgnoreCase("downloading") && 
					(item.getStatus().equalsIgnoreCase("seeding") || 
					item.getStatus().equalsIgnoreCase("completed") || 
					item.getStatus().equalsIgnoreCase("finished"))){
				NotificationBuilder.getInstance().BuildNotification(_context, item);
			}
	}

}

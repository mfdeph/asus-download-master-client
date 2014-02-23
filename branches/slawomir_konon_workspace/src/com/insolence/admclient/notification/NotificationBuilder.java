package com.insolence.admclient.notification;

import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.R;
import com.insolence.admclient.entity.DownloadItem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationBuilder {
	
	private static NotificationBuilder _instance;
	
	public static NotificationBuilder getInstance(){
		if (_instance == null)
			_instance = new NotificationBuilder();
		return _instance;
	}
	
	public void BuildNotification(Context context, DownloadItem item){

		NotificationCompat.Builder mBuilder = buildDownloadItemNotificationBuilder(context, item);

		TaskStackBuilder stackBuilder = TaskStackBuilder.from(context);
		stackBuilder.addParentStack(DownloadItemListActivity.class);
		stackBuilder.addNextIntent(
				new Intent(context, DownloadItemListActivity.class));
		mBuilder.setContentIntent(
				stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
				));
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification =  mBuilder.getNotification();
		notification.defaults = Notification.DEFAULT_ALL;		
		mNotificationManager.notify(Math.round((float)Math.random()*1000), notification);

	}
	
	private NotificationCompat.Builder buildBaseNotificationBuilder(Context context){
		return new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.notification_download)
			        .setAutoCancel(true);
	}
	
	private NotificationCompat.Builder buildDownloadItemNotificationBuilder(Context context, DownloadItem event){
		
		return buildBaseNotificationBuilder(context)
		        .setContentTitle(
		        		context.getResources().getString(R.string.notification_download_completed_title))
		        .setContentText(
		        		String.format(
		        				context.getResources().getString(R.string.notification_download_completed_text), 
		        				event.getName()));
		        
	}
}

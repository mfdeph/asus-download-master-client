package com.insolence.admclient.service;

import com.insolence.admclient.asynctasks.GetItemListResult;
import com.insolence.admclient.asynctasks.GetItemListTask;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.entity.IGetItemListResultPostProcessor;
import com.insolence.admclient.notification.NotificationBuilder;
import com.insolence.admclient.notification.NotifyWhenDownloadCompletedListener;
import com.insolence.admclient.storage.DownloadItemStorage;
import com.insolence.admclient.storage.PreferenceAccessor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RefreshItemListBroadcastReceiver extends BroadcastReceiver{

	private static boolean _isAppInForeground;
	
	public static void setAppInForeground(boolean isAppInForeground, Context context){
		_isAppInForeground = isAppInForeground;
		new RefreshItemListBroadcastReceiver().resetAlarm(context);
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		IGetItemListResultPostProcessor resultPostProcessor = new IGetItemListResultPostProcessor(){
			public void postProcessResult(GetItemListResult result) {
				if (result.isSucceed()){
					DownloadItemStorage.getInstance(context).saveDownloadItems(
							result.getDownloadItems(), 
							new NotifyWhenDownloadCompletedListener(context));
					setLastServiceRunTimeNow(context);
				}
			}
		};
		
		new GetItemListTask(resultPostProcessor).execute();
		
		setNextAlarm(context);
		
	}
	

	
	private long getServiceInterval(Context context){
		long interval;
		if (_isAppInForeground)
			interval = PreferenceAccessor.getInstance(context).getForegroundAutorefreshInterval() * 1000;
		else 
			interval = PreferenceAccessor.getInstance(context).getBackgroundAutorefreshInterval() * 60 * 1000;
		return interval;
	}
	
	private void setLastServiceRunTimeNow(Context context){
		 PreferenceAccessor.getInstance(context).setLastItemListRefreshedAt(System.currentTimeMillis());
	}
	
	private long getLastServiceRunTime(Context context){
		long lastServiceRunAt = PreferenceAccessor.getInstance(context).getLastItemListRefreshedAt();
		return lastServiceRunAt;
	}
	
	 private void setNextAlarm(Context context)
	 {
	     long nextRunTime = System.currentTimeMillis() + getServiceInterval(context);
	     setAlarm(context, nextRunTime);
	 }
	 
	 
	 public void setFirstAlarm(Context context){
		 if (!isAlarmUp(context)){
			 long nextRunTime = getLastServiceRunTime(context) + getServiceInterval(context);
			 setAlarm(context, nextRunTime);
		 }
	 }
	
	 public void resetAlarm(Context context){
		 cancelAlarm(context);
		 long nextRunTime = Math.max((getLastServiceRunTime(context) + getServiceInterval(context)), System.currentTimeMillis());
		 setAlarm(context, nextRunTime);
	 } 
	 
	 public void runAlarmImmidiately(Context context){
		 cancelAlarm(context);
		 long nextRunTime = System.currentTimeMillis();
		 setAlarm(context, nextRunTime);
	 } 
	 
	 private void cancelAlarm(Context context)
	 {
		 getAlarmManager(context).cancel(getAlarmPendingIntent(context));
	 }
	
	 private boolean isAlarmUp(Context context){
		 return getAlarmPendingIntent(context, PendingIntent.FLAG_NO_CREATE) != null;
	 }
	
	 private void setAlarm(Context context, long nextRunTime){
		 getAlarmManager(context).set(AlarmManager.RTC_WAKEUP, nextRunTime, getAlarmPendingIntent(context));
	 }
	 
	 private AlarmManager getAlarmManager(Context context){
		 return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	 }
	 
	 private PendingIntent getAlarmPendingIntent(Context context){
	     return getAlarmPendingIntent(context, 0);   	 
	 }
	 
	 private PendingIntent getAlarmPendingIntent(Context context, int flags){
	     Intent i = new Intent(context, RefreshItemListBroadcastReceiver.class);
	     PendingIntent intent =  PendingIntent.getBroadcast(context, 0, i, flags);   
	     return intent;
	 }

}

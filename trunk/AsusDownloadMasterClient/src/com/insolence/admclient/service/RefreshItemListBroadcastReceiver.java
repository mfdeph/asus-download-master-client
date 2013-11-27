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

public class RefreshItemListBroadcastReceiver extends BroadcastReceiver{

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
		
		//следующий запуск сервиса (пока без учета активно окно или нет)
		setAlarm(context, getNextServiceRunTime(context));
		
	}
	
	//первичная настройка сервиса, немедленный запуск обновления списка загрузок
	public void setup(Context context){
		 if (!isAlarmUp(context)){
			 setAlarm(context, System.currentTimeMillis());
		 }
	 }
	
	private long getNextServiceRunTime(Context context){
		return System.currentTimeMillis() + PreferenceAccessor.getInstance(context).getServiceAutorefreshInterval() * 60 * 1000;
	}
	
	private void setLastServiceRunTimeNow(Context context){
		 PreferenceAccessor.getInstance(context).setLastItemListRefreshedAt(System.currentTimeMillis());
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
	     return PendingIntent.getBroadcast(context, 0, i, flags);    	 
	 }

}

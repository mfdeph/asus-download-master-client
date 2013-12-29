package com.insolence.admclient.service;

import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.asynctasks.GetItemListResult;
import com.insolence.admclient.asynctasks.GetItemListTask;
import com.insolence.admclient.entity.IGetItemListResultPostProcessor;
import com.insolence.admclient.notification.NotifyWhenDownloadCompletedListener;
import com.insolence.admclient.storage.DownloadItemStorage;
import com.insolence.admclient.storage.PreferenceAccessor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;

public class RefreshItemListBroadcastReceiver extends BroadcastReceiver{

	private static GetItemListTask _currentRefreshTask;
	
	private static boolean IsLocked(){
		return !(_currentRefreshTask == null || _currentRefreshTask.getStatus() == Status.FINISHED);
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (!IsLocked()){
			IGetItemListResultPostProcessor resultPostProcessor = new IGetItemListResultPostProcessor(){
				public void postProcessResult(GetItemListResult result) {
					if (result.isSucceed()){
						DownloadItemStorage.getInstance(context).saveDownloadItems(
								result.getDownloadItems(), 
								new NotifyWhenDownloadCompletedListener(context));
						setLastServiceRunTimeNow(context);
						if (isMainActivityActive())
							getMainActivity().updateListView();
						
					} else {
						if (isMainActivityActive())
							getMainActivity().showErrorMessage(result.getMessage());
					}
					if (isMainActivityActive())
						getMainActivity().switchRefreshAnimation(false);
					_currentRefreshTask = null;
				}
			};
			
			if (isMainActivityActive())
				getMainActivity().switchRefreshAnimation(true);	
			GetItemListTask getItemListTask = new GetItemListTask(context, resultPostProcessor);
			_currentRefreshTask = getItemListTask;
			getItemListTask.execute();
		}
		
		setNextAlarm(context);
		
	}
	
	private boolean isMainActivityActive(){
		return getMainActivity() != null;
	}
	
	private DownloadItemListActivity getMainActivity(){
		return DownloadItemListActivity.getCurrent();
	}
	
	private long getServiceInterval(Context context){
		long interval = PreferenceAccessor.getInstance(context).getAutorefreshInterval() * 1000;
		//если приложение работает в фоне, то обновляем список в 60 раз реже ^^
		if (!isMainActivityActive())
			interval = interval * 60;		
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
	
	 public void resetAlarm(Context context){
		 cancelAlarm(context);
		 if (PreferenceAccessor.getInstance(context).isAutorefreshEnabled()){
			 long nextRunTime = Math.max((getLastServiceRunTime(context) + getServiceInterval(context)), System.currentTimeMillis());
			 setAlarm(context, nextRunTime);
		 }
	 } 
	 
	 public void runAlarmImmidiately(Context context){
		 cancelAlarm(context);
		 long nextRunTime = System.currentTimeMillis();
		 setAlarm(context, nextRunTime);
	 } 
	 
	 public void cancelAlarm(Context context)
	 {
		 getAlarmManager(context).cancel(getAlarmPendingIntent(context));
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

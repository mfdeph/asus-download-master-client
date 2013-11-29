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

public class RefreshItemListBroadcastReceiver extends BroadcastReceiver{

	private static boolean _locked;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (!_locked){
			_locked = true;
			IGetItemListResultPostProcessor resultPostProcessor = new IGetItemListResultPostProcessor(){
				public void postProcessResult(GetItemListResult result) {
					if (result.isSucceed()){
						DownloadItemStorage.getInstance(context).saveDownloadItems(
								result.getDownloadItems(), 
								new NotifyWhenDownloadCompletedListener(context));
						setLastServiceRunTimeNow(context);
						if (getMainActivity() != null)
							getMainActivity().updateListView();
						
					}
					if (getMainActivity() != null)
						getMainActivity().setUpdateProgressAnimation(false);
					_locked = false;
				}
			};
			
			if (getMainActivity() != null)
				getMainActivity().setUpdateProgressAnimation(true);		
			new GetItemListTask(resultPostProcessor).execute();
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
		long interval;
		if (isMainActivityActive())
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
	 
	 private void cancelAlarm(Context context)
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

package com.insolence.admclient.storage;

import com.insolence.admclient.StaticContextApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceAccessor {
	
	private static PreferenceAccessor _instance;
	
	public static PreferenceAccessor getInstance(Context context){
		if (_instance == null)
			_instance = new PreferenceAccessor(context);
		return _instance;
	}
	
	public static PreferenceAccessor getInstance(){
		return getInstance(StaticContextApp.getContext());
	}
	
	private Context _context;
	
	private PreferenceAccessor(Context context){
		_context = context;
	}
	
	private SharedPreferences getPrefs(){
		return PreferenceManager.getDefaultSharedPreferences(_context);
	}
	
	private SharedPreferences.Editor getPrefsToEdit(){
		return getPrefs().edit();
	}

	private static final String autorefreshEnabledPref = "autorefreshEnabledPref";	
	
	public boolean isAutorefreshEnabled(){
		return getPrefs().getBoolean(autorefreshEnabledPref, true);
	}
	
	private static final String autorefreshIntervalPref = "autorefreshIntervalPref";	
	
	//интервал обновления - не чаще чем раз в 5 секунд (или минут, если приложение свернуто)
	public int getAutorefreshInterval(){
		return Math.max(5, Integer.valueOf(getPrefs().getString(autorefreshIntervalPref, "30")));
	}
		
	private static final String lastItemListRefreshedAtPref = "lastItemListRefreshedAt";
	
	public long getLastItemListRefreshedAt(){
		return getPrefs().getLong(lastItemListRefreshedAtPref, 0);
	}
	
	public void setLastItemListRefreshedAt(long refreshTime){
		SharedPreferences.Editor editor = getPrefsToEdit();
		editor.putLong(lastItemListRefreshedAtPref, refreshTime);
		editor.commit();
	}

}

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
	
	private static final String serviceAutorefreshIntervalPref = "serviceAutorefreshIntervalPref";	
	
	public int getServiceAutorefreshInterval(){
		return getPrefs().getInt(serviceAutorefreshIntervalPref, 30);
	}
		
	private static final String lastItemListRefreshedAtPref = "lastItemListRefreshedAt";
	
	public long getLastItemListRefreshedAt(){
		return getPrefs().getLong(lastItemListRefreshedAtPref, 0);
	}
	
	public void setLastItemListRefreshedAt(long refreshTime){
		getPrefsToEdit().putLong(lastItemListRefreshedAtPref, refreshTime);
	}

}

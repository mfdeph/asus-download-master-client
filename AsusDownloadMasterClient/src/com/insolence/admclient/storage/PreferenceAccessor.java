package com.insolence.admclient.storage;

import com.insolence.admclient.StaticContextApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
		initDisplayMode();
	}
	
	public SharedPreferences getPrefs(){
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
		try{
			return Math.max(5, Integer.valueOf(getPrefs().getString(autorefreshIntervalPref, "30")));
		}catch(NumberFormatException e){
			return 30;
		}
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
	
	private static final String webServerAddrPref = "webServerAddrPref";
	
	public String getWebServerAddress(){
		return getPrefs().getString(webServerAddrPref, "192.168.1.1");
	}
	
	private static final String webServerPortPref = "webServerPortPref";
	
	public String getWebServerPort(){
		return getPrefs().getString(webServerPortPref, "8081");
	}
	
	private static final String postfixEnabledPref = "postfixEnabledPref";
	
	public boolean isPathPostfixEnabled(){
		return getPrefs().getBoolean(postfixEnabledPref, true);
	}
	
	private static final String loginPref = "loginPref";
	
	public String getLogin(){
		return getPrefs().getString(loginPref, "admin");
	}
	
	private static final String passwordPref = "passwordPref";
	
	public String getPassword(){
		return getPrefs().getString(passwordPref, "admin");
	}
	
	private static final String languagePref = "languagePref";
	
	public String getLanguage(){
		return getPrefs().getString(languagePref, "auto");
	}	
	
	private static final String displayModePref = "displayModePref";
	
	public int getDisplayMode(){
		try{
			return Integer.valueOf(getPrefs().getString(displayModePref, "1"));
		}catch(NumberFormatException e){
			return 1;
		}
	}
	
	//obsolete, for initialization of new property on update only
	private static final String showExpandedPref = "showExpandedPref";
	private boolean isShowExpanded(){
		return getPrefs().getBoolean(showExpandedPref, false);
	}

	public void initDisplayMode(){
		if (getPrefs().contains(displayModePref))
			return;
		SharedPreferences.Editor editor = getPrefsToEdit();
		//for tablets default value is "Expanded"
		editor.putString(displayModePref, (isTablet() || isShowExpanded()) ? "2" : "1");
		editor.commit();
	}
	
	private boolean isTablet() {
	    return (_context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
}

package com.insolence.admclient.util;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.insolence.admclient.storage.PreferenceAccessor;

public class LanguageHelper {
	
	public static void setLanguage(Context context){
    	String language = PreferenceAccessor.getInstance(context).getLanguage();
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (language.equals("auto"))
        	conf.locale = Locale.getDefault();
        else
        	conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, dm);
	}
	
}

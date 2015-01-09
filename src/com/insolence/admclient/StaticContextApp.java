package com.insolence.admclient;

import android.app.Application;
import android.content.Context;

public class StaticContextApp extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

}

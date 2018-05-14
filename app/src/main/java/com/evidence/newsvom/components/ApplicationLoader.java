package com.evidence.newsvom.components;

import android.support.multidex.MultiDexApplication;

public class ApplicationLoader extends MultiDexApplication {

    private static ApplicationLoader _INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        _INSTANCE = this;
    }

    public static ApplicationLoader getInstance() {
        return _INSTANCE;
    }

}

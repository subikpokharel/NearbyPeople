package com.csci515.subik.peoplenearby.myApplication;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by subik on 3/12/18.
 */

public class MyApplication extends Application {

    SharedPreferences mSharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void  saveToken(String key, String value){
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();

    }

    public String getSavedValue(String key){

        return  mSharedPreferences.getString(key,null);
    }
}

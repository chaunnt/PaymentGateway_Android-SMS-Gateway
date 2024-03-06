package com.appsnipp.smsgateway;

import android.app.Application;
import android.content.SharedPreferences;
import com.appsnipp.smsgateway.Utils.APIManager;

import com.appsnipp.smsgateway.ui.utils.AppLogger;

import java.util.UUID;


public class App extends Application {

    private static App instance;
    public static String secret;
    private SharedPreferences sharedPrefer;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppLogger.init();

        ObjectBox.init(this);
        APIManager.init(this);
        sharedPrefer = getSharedPreferences("pref",0);
        secret = sharedPrefer.getString("secret",null);
        if(secret==null){
            secret = UUID.randomUUID().toString();
            sharedPrefer.edit().putString("secret", secret).apply();
        }
    }
}

/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.smsgateway.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.appsnipp.smsgateway.ObjectBox;
import com.appsnipp.smsgateway.data.Sms;

import java.util.List;

public final class MyUtilsApp {
    private MyUtilsApp(){}
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String TAG,String message){
        Log.d(TAG, "showLog: "+ message);
    }

    public static boolean checkNetworkIsConnected(Context context, ConnectivityManager.NetworkCallback callBack) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder build = new NetworkRequest.Builder();
        cm.registerNetworkCallback(build.build(), callBack);
        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean checkInternetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}

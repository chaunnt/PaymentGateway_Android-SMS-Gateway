/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.layanan;

import static com.appsnipp.smsgateway.Utils.SharePreferenceUtil.KEY_IS_SYNC_CALL_LOG_FIRST_TIME;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.appsnipp.smsgateway.ObjectBox;
import com.appsnipp.smsgateway.Utils.APIManager;
import com.appsnipp.smsgateway.Utils.Fungsi;
import com.appsnipp.smsgateway.data.CallLogLocal;
import com.appsnipp.smsgateway.data.CallLogRequest;
import com.appsnipp.smsgateway.data.CallLogResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneCallListener extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
//    public static MyCallsAppDatabase myCallsAppDatabase;

    public static void saveData(Context ctx, String number, int callType, Date start, long duration, CallLogLocal callLog, String type) {
        SharedPreferences sp = ctx.getSharedPreferences("pref", 0);
        String userPhoneNumber = sp.getString("phone_number", "");

        Log.d("TAG", "saveData: number " + number + " callType " + callType + " duration " + duration);
        CallLogRequest callLogRequest;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
        switch (callType) {
            case CallLog.Calls.OUTGOING_TYPE:
                callLogRequest = new CallLogRequest(userPhoneNumber, number, String.valueOf(duration), dateFormatter.format(start));
                break;
            case CallLog.Calls.INCOMING_TYPE:
            case CallLog.Calls.MISSED_TYPE:
            default:
                callLogRequest = new CallLogRequest(number, userPhoneNumber, String.valueOf(duration), dateFormatter.format(start));
                break;
        }
        postCallLog(ctx, callLogRequest, callLog, type);
    }

    private static void postCallLog(Context context, CallLogRequest callLogRequest, CallLogLocal callLog, String type) {
        SharedPreferences sp = context.getSharedPreferences("pref", 0);
        String userToken = sp.getString("token", "");
        APIManager.getInstance(context).postCallLog(callLogRequest, userToken, new Callback<CallLogResponse>() {
            @Override
            public void onResponse(Call<CallLogResponse> call, Response<CallLogResponse> response) {
                Log.d("postCallLog ", "onResponse: " + response + "");
                if (type.equals("local") || type.equals("all")) {
                    if (callLog != null) {
                        ObjectBox.get().boxFor(CallLogLocal.class).remove(callLog);
                    }
                }
                if (type.equals("all")) {
                    if (ObjectBox.get().boxFor(CallLogLocal.class).count() == 0) {
                        Fungsi.log("Update KEY_IS_SYNC_CALL_LOG_FIRST_TIME = true");
                        context.getSharedPreferences("pref", 0).edit().putBoolean(KEY_IS_SYNC_CALL_LOG_FIRST_TIME, true).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<CallLogResponse> call, Throwable t) {
                Log.d("postCallLog ", "onFailure: " + t);

            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d("TAG", "onReceive: savedNumber " + savedNumber);

        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            if (number != null)
                onCallStateChanged(context, state, number, intent);
        }
    }

    public void onCallStateChanged(Context context, int state, String number, Intent intent) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        Log.d("", "onCallStateChanged: state " + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                onIncomingCallStarted(context, number, callStartTime, intent);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callStartTime = new Date();
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, number, callStartTime, intent);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, number, callStartTime, intent);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, new Date(), intent);
                } else {
                    onOutgoingCallEnded(context, number, callStartTime, new Date(), intent);
                }
                break;
        }
        lastState = state;

//        Intent intent1 = new Intent("CallApp");
//        context.sendBroadcast(intent1);
    }

    protected void onIncomingCallStarted(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "calling from " + number, Toast.LENGTH_SHORT).show();
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "calling to " + number, Toast.LENGTH_SHORT).show();
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
//        Toast.makeText(ctx, "calling from " + number + " ended ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, CallLog.Calls.INCOMING_TYPE, start, end);
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
//        Toast.makeText(ctx, "calling to " + number + " ended ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, CallLog.Calls.OUTGOING_TYPE, start, end);
    }

    protected void onMissedCall(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "missed call from " + number + " sim ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, CallLog.Calls.MISSED_TYPE, start, new Date());
    }

    @SuppressLint("ServiceCast")
    private void saveData(Context ctx, String number, Intent intent, int callType, Date start, Date end) {
        long duration = end.getTime() - start.getTime();
        saveData(ctx, number, callType, start, duration, null, "");
    }
}

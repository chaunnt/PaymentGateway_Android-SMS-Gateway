package com.appsnipp.smsgateway.Utils;

import android.content.Context;

import com.appsnipp.smsgateway.data.CreateSmsResponse;
import com.appsnipp.smsgateway.data.LoginRequest;
import com.appsnipp.smsgateway.data.LoginResponse;
import com.appsnipp.smsgateway.data.SmsRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class APIManager {
    private static APIManager mAPIManager;
    private final Context mContext;

    public static APIManager getInstance(Context context) {
        if (mAPIManager == null) {
            mAPIManager = new APIManager(context);
        }
        return mAPIManager;
    }

    public static void init(Context context) {
        if (mAPIManager == null) {
            mAPIManager = new APIManager(context);
        }
    }

    public APIManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static APIManager getInstance() {
        if (mAPIManager != null) {
            return mAPIManager;
        } else {
            throw new NullPointerException("Must call init() first");
        }
    }

    public void login(String userName, String password,  Callback<LoginResponse> apiCallback) {
        Api apiService = RetrofitClient.getInstance().getApi();

        Call<LoginResponse> call = apiService.login(new LoginRequest(userName, password));
        if (apiCallback != null) {

            call.enqueue(apiCallback);
        }
    }

    public void createSms(String smsMessageContent, String smsMessageOrigin, String userToken,
                          Callback<CreateSmsResponse> apiCallback) {
        String[] currentTime = Utils.getCurrentTime();
        String smsReceiveDate = currentTime[0];
        String smsReceiveTime = currentTime[1];
        String smsMessageNote = "AUTO_SYNC";
        String smsMessageStatus = "New";
        String smsHash = Utils.hash(smsMessageContent+smsMessageOrigin+smsReceiveDate+smsReceiveTime);
        SmsRequest smsRequest = new SmsRequest(smsMessageContent, smsMessageNote,
                smsMessageOrigin, smsMessageStatus, smsReceiveDate, smsReceiveTime, smsHash);

        String authorization = "Bearer " + userToken;
        Api apiService = RetrofitClient.getInstance().getApi();
        Call<CreateSmsResponse> call = apiService.createSms(authorization, smsRequest);
        if (apiCallback != null) {
            call.enqueue(apiCallback);
        }
    }
}

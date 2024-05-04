package com.appsnipp.smsgateway.Utils;

import android.content.Context;
import android.util.Log;

import com.appsnipp.smsgateway.data.CallLogRequest;
import com.appsnipp.smsgateway.data.ContactRequest;
import com.appsnipp.smsgateway.data.ContactResponse;
import com.appsnipp.smsgateway.data.CreateSmsResponse;
import com.appsnipp.smsgateway.data.LoginRequest;
import com.appsnipp.smsgateway.data.LoginResponse;
import com.appsnipp.smsgateway.data.CallLogResponse;
import com.appsnipp.smsgateway.data.SmsRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class APIManager {
    private static APIManager mAPIManager;
    private final Context mContext;

    public APIManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

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

    public static APIManager getInstance() {
        if (mAPIManager != null) {
            return mAPIManager;
        } else {
            throw new NullPointerException("Must call init() first");
        }
    }

    public void login(String userName, String password, Callback<LoginResponse> apiCallback) {
        Api apiService = RetrofitClient.getInstance().getApi();

        Call<LoginResponse> call = apiService.login(new LoginRequest(userName, password));
        if (apiCallback != null) {

            call.enqueue(apiCallback);
        }
    }

    public void createSms(String smsMessageContent, String smsMessageOrigin, String destinationPhoneNumber, String userToken,
                          Callback<CreateSmsResponse> apiCallback) {
        String[] currentTime = Utils.getCurrentTime();
        String smsReceiveDate = currentTime[0].replace("/", "");
        String smsReceiveTime = currentTime[1].replace(":", "");
        Fungsi.log("date", smsReceiveDate + smsReceiveTime);
//        String smsMessageNote = "AUTO_SYNC";
        String smsMessageStatus = "New";
//        String smsHash = Utils.hash(smsMessageContent+smsMessageOrigin+smsReceiveDate+smsReceiveTime);
        SmsRequest smsRequest = new SmsRequest(smsMessageOrigin, destinationPhoneNumber, smsMessageContent,smsReceiveDate,smsReceiveTime,smsMessageStatus);
        String authorization = "Bearer " + userToken;
        Api apiService = RetrofitClient.getInstance().getApi();
        Log.d("TAG", "createSms: authorization" + authorization + ",body " + smsRequest);
        Call<CreateSmsResponse> call = apiService.createSms(authorization, smsRequest);
        if (apiCallback != null) {
            call.enqueue(apiCallback);
        }
    }

    public void postCallLog(CallLogRequest callLogRequest, String userToken,
                            Callback<CallLogResponse> apiCallback) {
        String authorization = "Bearer " + userToken;
        Api apiService = RetrofitClient.getInstance().getApi();
        Log.d("TAG", "createSms: authorization" + authorization + ",body " + callLogRequest);
        Call<CallLogResponse> call = apiService.postCallLog(authorization, callLogRequest);
        if (apiCallback != null) {
            call.enqueue(apiCallback);
        }
    }

    public void postContact(ContactRequest contactRequest, String userToken,
                            Callback<ContactResponse> apiCallback) {
        String authorization = "Bearer " + userToken;
        Api apiService = RetrofitClient.getInstance().getApi();
        Log.d("TAG", "createSms: authorization" + authorization + ",body " + contactRequest);
        Call<ContactResponse> call = apiService.postContact(authorization, contactRequest);
        if (apiCallback != null) {
            call.enqueue(apiCallback);
        }
    }
}

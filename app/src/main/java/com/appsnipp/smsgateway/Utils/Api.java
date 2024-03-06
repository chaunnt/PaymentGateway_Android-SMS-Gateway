package com.appsnipp.smsgateway.Utils;

import com.appsnipp.smsgateway.data.CreateSmsResponse;
import com.appsnipp.smsgateway.data.LoginRequest;
import com.appsnipp.smsgateway.data.LoginResponse;
import com.appsnipp.smsgateway.data.SmsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
    @POST("AppUsers/loginUser")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("SMSMessage/user/create")
    Call<CreateSmsResponse> createSms(@Header("authorization") String authorization,
                                      @Body SmsRequest request);
}

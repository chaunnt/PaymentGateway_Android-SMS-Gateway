package com.ibnux.smsgateway.Utils;

import com.ibnux.smsgateway.data.CreateSmsResponse;
import com.ibnux.smsgateway.data.LoginRequest;
import com.ibnux.smsgateway.data.LoginResponse;
import com.ibnux.smsgateway.data.SmsRequest;

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

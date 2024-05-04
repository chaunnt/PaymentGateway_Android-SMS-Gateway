package com.appsnipp.smsgateway.Utils;

import com.appsnipp.smsgateway.data.CallLogRequest;
import com.appsnipp.smsgateway.data.ContactRequest;
import com.appsnipp.smsgateway.data.ContactResponse;
import com.appsnipp.smsgateway.data.CreateSmsResponse;
import com.appsnipp.smsgateway.data.LoginRequest;
import com.appsnipp.smsgateway.data.LoginResponse;
import com.appsnipp.smsgateway.data.CallLogResponse;
import com.appsnipp.smsgateway.data.SmsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
    @POST("AppUsers/loginUser")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("SMSMessage/robot/insert")
    Call<CreateSmsResponse> createSms(@Header("authorization") String authorization,
                                      @Body SmsRequest request);

    @POST("AppUserCallLog/user/insertCallLog")
    Call<CallLogResponse> postCallLog(@Header("authorization") String authorization,
                                      @Body CallLogRequest request);

    @POST("AppUserContact/user/insertContact")
    Call<ContactResponse> postContact(@Header("authorization") String authorization,
                                      @Body ContactRequest request);
}

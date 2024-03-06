package com.appsnipp.smsgateway.data;

import com.google.gson.annotations.SerializedName;

public class User {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @SerializedName("token")
    private String token;
}

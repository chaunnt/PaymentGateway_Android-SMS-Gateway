/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.smsgateway.ui.menugatewaylogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.appsnipp.smsgateway.MainActivity;
import com.appsnipp.smsgateway.R;
import com.appsnipp.smsgateway.SMSGatewayActivity;
import com.appsnipp.smsgateway.Utils.APIManager;
import com.appsnipp.smsgateway.data.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GatewayLoginFragment extends Fragment {
    View layoutView;
    RelativeLayout parent;
    EditText edtUserName,edtPassword;
    Button btnLogin;
    ProgressBar progressBar;

    public GatewayLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutView = inflater.inflate(R.layout.fragment_gateway_login, container, false);

        parent = (RelativeLayout)layoutView.findViewById(R.id.parent);
        progressBar = (ProgressBar)layoutView.findViewById(R.id.progressBar);
        edtUserName = (EditText)layoutView.findViewById(R.id.edt_username);
        edtPassword = (EditText)layoutView.findViewById(R.id.edt_password);
        btnLogin = (Button)layoutView.findViewById(R.id.btn_login);
        loginAction();

        SharedPreferences preferences =
                this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String userToken = preferences.getString("user_token",null);
        if(userToken != null) {
            this.getActivity().finish();
            startActivity(new Intent(this.getActivity(), SMSGatewayActivity.class));
        }
        return layoutView;
    }

    void loginAction() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtUserName.getText().toString();
                String password =  edtPassword.getText().toString();
                if(userName.equals("") ||
                        password.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter username or password...",Toast.LENGTH_SHORT).show();
                }else{
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                    progressBar.setVisibility(View.VISIBLE);
                    APIManager.getInstance(getActivity()).login(userName, password, new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            progressBar.setVisibility(View.GONE);

                            Log.d("DATA_TEST: ", response.body().getData().getToken());
                            getActivity().getSharedPreferences("pref",0).edit().putString("user_token", response.body().getData().getToken()).commit();
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), SMSGatewayActivity.class));
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

}
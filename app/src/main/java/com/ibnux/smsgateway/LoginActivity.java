package com.ibnux.smsgateway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ibnux.smsgateway.Utils.APIManager;
import com.ibnux.smsgateway.data.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout parent;
    EditText edtUserName,edtPassword;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent = (RelativeLayout)findViewById(R.id.parent);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        edtUserName = (EditText)findViewById(R.id.edt_username);
        edtPassword = (EditText)findViewById(R.id.edt_password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        loginAction();

        SharedPreferences sp = getSharedPreferences("pref",0);
        String userToken = sp.getString("user_token",null);
        if(userToken != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    void loginAction() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtUserName.getText().toString();
                String password =  edtPassword.getText().toString();
                if(userName.equals("") ||
                        password.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter username or password...",Toast.LENGTH_SHORT).show();
                }else{
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                    progressBar.setVisibility(View.VISIBLE);
                    APIManager.getInstance(LoginActivity.this).login(userName, password, new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            progressBar.setVisibility(View.GONE);

                            Log.d("DATA_TEST: ", response.body().getData().getToken());
                            getSharedPreferences("pref",0).edit().putString("user_token", response.body().getData().getToken()).commit();
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

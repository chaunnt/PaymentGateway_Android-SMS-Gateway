/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.smsgateway.ui.menuhome;

import android.content.SharedPreferences;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.appsnipp.smsgateway.R;

public class HomeFragment extends Fragment {
    public WebView mWebView;
    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
            updateInfo(sanitizer.getValue("token"));
        }
    };


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.loadUrl("https://cdn-remote-vietcredit.vay3s.com/User/getUserRobotToken");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(mWebViewClient);
        return view;
    }

    public void updateInfo(String token) {
        if (getActivity() != null) {
            SharedPreferences sp = getActivity().getSharedPreferences("pref", 0);
            sp.edit().putString("token", token).apply();
        }
    }
}
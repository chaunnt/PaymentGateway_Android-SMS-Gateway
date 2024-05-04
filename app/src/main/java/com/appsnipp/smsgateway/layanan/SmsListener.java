package com.appsnipp.smsgateway.layanan;

//import static appsnipp.education.layanan.PushService.writeLog;

import static com.appsnipp.smsgateway.Utils.SharePreferenceUtil.KEY_IS_SYNC_SMS_FIRST_TIME;
import static com.appsnipp.smsgateway.ui.utils.MyUtilsApp.checkInternetIsConnected;
import static com.appsnipp.smsgateway.ui.utils.MyUtilsApp.checkNetworkIsConnected;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsnipp.smsgateway.ObjectBox;
import com.appsnipp.smsgateway.Utils.APIManager;
import com.appsnipp.smsgateway.data.CreateSmsResponse;
import com.appsnipp.smsgateway.data.Sms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsListener extends BroadcastReceiver {
    private final String TAG = "SmsListener";
    SharedPreferences sp;
    private Context mContext;
    private final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.i(TAG, "Network Available");
            List<Sms> smsList = ObjectBox.get().boxFor(Sms.class).getAll();
            for (Sms sms : smsList) {
                Log.d(TAG, "onLost: " + sms);
                sendPOST(sp.getString("urlPost", null), sms, "local", mContext);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
        }
    };

    public static void sendPOST(String urlPost, Sms sms, String tipe, Context context) {
//        Log.d("SEND_SMS: sendPOST: ", sms.address + "====" + sms.msg);

//        if(urlPost==null) return;
//        if(from.isEmpty()) return;
//        if(!urlPost.startsWith("http")) return;
        try {
//            new postDataTask().execute(urlPost,
//                    "number="+URLEncoder.encode(from, "UTF-8")+
//                            "&message="+URLEncoder.encode(msg, "UTF-8")+
//                            "&type="+URLEncoder.encode(tipe, "UTF-8")
//            );
            SharedPreferences sp = context.getSharedPreferences("pref", 0);
            String userToken = sp.getString("token", "");
            String userPhoneNumber = sp.getString("phone_number", "");
            Log.d("TAG", "sendPOST: token" + userToken);
            APIManager.getInstance(context).createSms(sms.msg, sms.address, userPhoneNumber, userToken, new Callback<CreateSmsResponse>() {
                @Override
                public void onResponse(Call<CreateSmsResponse> call, Response<CreateSmsResponse> response) {
                    Log.d("SEND_SMS: onResponse: ", response + " ");
                    if (tipe.equals("local") || tipe.equals("all")) {
                        ObjectBox.get().boxFor(Sms.class).remove(sms);
                    }
                    if (tipe.equals("all")) {
                        if (ObjectBox.get().boxFor(Sms.class).count() == 0)
                            context.getSharedPreferences("pref", 0).edit().putBoolean(KEY_IS_SYNC_SMS_FIRST_TIME, true).apply();
                    }
                }

                @Override
                public void onFailure(Call<CreateSmsResponse> call, Throwable t) {
                    Log.d("SEND_SMS: onFailure: ", t.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            writeLog("SMS: POST FAILED : "+urlPost+" : "+e.getMessage(),context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceive", "onReceive");
        mContext = context;
        if (sp == null) sp = context.getSharedPreferences("pref", 0);
        String url = sp.getString("urlPost", null);
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageFrom = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                Log.i("SMS From", messageFrom);
                Log.i("SMS Body", messageBody);
                Sms sms = new Sms();
                sms.address = messageFrom;
                sms.msg = messageBody;
//                writeLog("SMS: RECEIVED : " + messageFrom + " " + messageBody,context);
                if (!checkNetworkIsConnected(context, callback) && !checkInternetIsConnected()) {
                    Log.i("onReceive", "error network");
                    ObjectBox.get().boxFor(Sms.class).put(sms);
                    return;
                } else {
                    sendPOST(url, sms, "received", context);
                }

//                if(url!=null){
//                    if(sp.getBoolean("gateway_on",true)) {
//                sendPOST(url, messageFrom, messageBody, "", "received", context);
//                    }else{
////                        writeLog("GATEWAY OFF: SMS NOT POSTED TO SERVER", context);
//                    }
//                }else{
//                    Log.i("SMS URL", "URL not SET");
//                }
            }
        }
    }

    static class postDataTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... datas) {
            URL url;
            String response = "";
            try {
                try {
                    url = new URL(datas[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(datas[1]);

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    } else {
                        response = "";

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "SMS: POST : " + datas[0] + " : " + response;
            } catch (Exception e) {
                e.printStackTrace();
                return "SMS: POST FAILED : " + datas[0] + " : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
//            writeLog(response,null);
        }
    }
}

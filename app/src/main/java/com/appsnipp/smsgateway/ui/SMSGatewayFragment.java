/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.ui;

import static com.appsnipp.smsgateway.Utils.SharePreferenceUtil.KEY_IS_SYNC_CALL_LOG_FIRST_TIME;
import static com.appsnipp.smsgateway.Utils.SharePreferenceUtil.KEY_IS_SYNC_SMS_FIRST_TIME;
import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.appsnipp.smsgateway.ObjectBox;
import com.appsnipp.smsgateway.R;
import com.appsnipp.smsgateway.Utils.APIManager;
import com.appsnipp.smsgateway.Utils.Fungsi;
import com.appsnipp.smsgateway.data.CallLogLocal;
import com.appsnipp.smsgateway.data.Contact;
import com.appsnipp.smsgateway.data.ContactRequest;
import com.appsnipp.smsgateway.data.ContactResponse;
import com.appsnipp.smsgateway.data.Sms;
import com.appsnipp.smsgateway.databinding.FragmentSMSGatewayBinding;
import com.appsnipp.smsgateway.layanan.PhoneCallListener;
import com.appsnipp.smsgateway.layanan.SmsListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SMSGatewayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SMSGatewayFragment extends Fragment {
    private final String TAG = "SMSGatewayFragment";
    FragmentSMSGatewayBinding mBinding;

    public SMSGatewayFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SMSGatewayFragment.
     */
    public static SMSGatewayFragment newInstance(String param1, String param2) {
        return new SMSGatewayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSMSGatewayBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.requestPermissionSmsBtn.setOnClickListener(v -> requestSmsPermission());
        mBinding.requestPermissionCallLogBtn.setOnClickListener(v -> requestCallLogPermission());
        mBinding.requestPermissionContactBtn.setOnClickListener(v -> requestReadContactPermission());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissionSMS();
    }

    public void getInfo() {
        if (getActivity() != null) {
            SharedPreferences sp = getActivity().getSharedPreferences("pref", 0);
            String token = sp.getString("token", "");
            Log.d(TAG, "getInfo: token" + token);
        }
    }

    private void requestSmsPermission() {
        Log.d(TAG, "requestPermission: requestPermission");
        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_PHONE_NUMBERS
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Fungsi.log("All Permission granted");
                            getActivity().runOnUiThread(() -> {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                                    TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                                    String mPhoneNumber = tMgr.getLine1Number();
                                    SharedPreferences sp = getContext().getSharedPreferences("pref", 0);
                                    sp.edit().putString("phone_number", mPhoneNumber).apply();
                                    Fungsi.log("mPhoneNumber" + mPhoneNumber);
                                }
                                mBinding.smsVerifyStatus.setText(getString(R.string.verified));
                                mBinding.smsVerifyStatus.setTextColor(getResources().getColor(R.color.color2));
                                mBinding.loadingSpinner.setVisibility(View.VISIBLE);
                                mBinding.requestPermissionSmsBtn.setEnabled(false);
                            });

                            new Thread(() -> {
                                if (!getActivity().getSharedPreferences("pref", 0).getBoolean(KEY_IS_SYNC_SMS_FIRST_TIME, false)) {
                                    getAllSms();
                                    Fungsi.log("All Permission getAllSms");
                                    List<Sms> listSms = ObjectBox.get().boxFor(Sms.class).getAll();
                                    for (Sms sms : listSms) {
                                        SmsListener.sendPOST("", sms, "all", getContext());
                                    }
                                    getActivity().runOnUiThread(() -> {
                                        mBinding.loadingSpinner.setVisibility(View.GONE);
                                        mBinding.requestPermissionSmsBtn.setEnabled(true);
                                    });
                                }
                            }).start();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            Fungsi.log("Some Permission not granted");
                            getActivity().runOnUiThread(() -> mBinding.smsVerifyStatus.setText(getString(R.string.not_verify)));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).withErrorListener(dexterError -> Log.d(TAG, "requestPermission: dexterError " + dexterError)).check();
    }

    private void requestCallLogPermission() {
        Log.d(TAG, "requestPermission: requestPermission");
        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.READ_CALL_LOG
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Fungsi.log("All Permission granted");
                            getActivity().runOnUiThread(() -> {
                                mBinding.callLogVerifyStatus.setText(getString(R.string.verified));
                                mBinding.callLogVerifyStatus.setTextColor(getResources().getColor(R.color.color2));
                                mBinding.requestPermissionCallLogBtn.setEnabled(false);
                                mBinding.loadingSpinner.setVisibility(View.VISIBLE);
                            });

                            new Thread(() -> {
                                if (!getActivity().getSharedPreferences("pref", 0).getBoolean(KEY_IS_SYNC_CALL_LOG_FIRST_TIME, false)) {
                                    getCallLogs();
                                    List<CallLogLocal> listCallLog = ObjectBox.get().boxFor(CallLogLocal.class).getAll();

                                    for (CallLogLocal callLog : listCallLog) {
                                        PhoneCallListener.saveData(getContext(), callLog.callNumber, callLog.direction, callLog.callDate, callLog.callDuration, callLog, "all");
                                    }
                                    getActivity().runOnUiThread(() -> {
                                        mBinding.loadingSpinner.setVisibility(View.GONE);
                                        mBinding.requestPermissionCallLogBtn.setEnabled(true);
                                    });
                                }
                            }).start();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            Fungsi.log("Some Permission not granted");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.callLogVerifyStatus.setText(getString(R.string.not_verify));
                                    mBinding.callLogVerifyStatus.setTextColor(getResources().getColor(R.color.color1));
                                }
                            });
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).withErrorListener(dexterError -> {
                    Log.d(TAG, "requestPermission: dexterError " + dexterError);
                }).check();
    }

    private void requestReadContactPermission() {
        Fungsi.log("requestReadContactPermission");
        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Log.d(TAG, "onPermissionsChecked: multiplePermissionsReport.areAllPermissionsGranted()" + report.areAllPermissionsGranted());
                        if (report.areAllPermissionsGranted()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.contactVerifyStatus.setText(getString(R.string.verified));
                                    mBinding.contactVerifyStatus.setTextColor(getResources().getColor(R.color.color2));
                                    mBinding.loadingSpinner.setVisibility(View.VISIBLE);
                                    mBinding.requestPermissionContactBtn.setEnabled(false);
                                }
                            });

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getContactList();
                                    List<Contact> list = ObjectBox.get().boxFor(Contact.class).getAll();
                                    Fungsi.log("All Permission getContactList");

                                    for (Contact contactRequest : list) {
                                        postContact(contactRequest);
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBinding.loadingSpinner.setVisibility(View.GONE);
                                            mBinding.requestPermissionContactBtn.setEnabled(true);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Log.d(TAG, "onPermissionRationaleShouldBeShown: ");
                    }
                }).check();
    }

    private void checkPermissionSMS() {
        Log.d(TAG, "checkPermissionSMS: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext() != null) {
            if (getContext().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                    getContext().checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                mBinding.smsVerifyStatus.setText(getString(R.string.not_verify));
                mBinding.smsVerifyStatus.setTextColor(getResources().getColor(R.color.color1, getContext().getTheme()));
            } else {
                mBinding.smsVerifyStatus.setText(getString(R.string.verified));
                mBinding.smsVerifyStatus.setTextColor(getResources().getColor(R.color.color2, getContext().getTheme()));
            }
            if (getContext().checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                mBinding.callLogVerifyStatus.setText(getString(R.string.not_verify));
                mBinding.callLogVerifyStatus.setTextColor(getResources().getColor(R.color.color1, getContext().getTheme()));
            } else {
                mBinding.callLogVerifyStatus.setText(getString(R.string.verified));
                mBinding.callLogVerifyStatus.setTextColor(getResources().getColor(R.color.color2, getContext().getTheme()));
            }
            if (getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                mBinding.contactVerifyStatus.setText(getString(R.string.not_verify));
                mBinding.contactVerifyStatus.setTextColor(getResources().getColor(R.color.color1, getContext().getTheme()));
            } else {
                mBinding.contactVerifyStatus.setText(getString(R.string.verified));
                mBinding.contactVerifyStatus.setTextColor(getResources().getColor(R.color.color2, getContext().getTheme()));
            }
        }
    }

    public void getAllSms() {
        Log.e("apps", "here ");
        List<Sms> listSms = new ArrayList<>();
        Sms objSms;
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContext().getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        //mcontext.startManagingCursor(c);
        if (c != null && c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {

                objSms = new Sms();
                objSms.displayName = c.getString(c.getColumnIndexOrThrow("_id"));
                objSms.address = c.getString(c.getColumnIndexOrThrow("address"));
                objSms.msg = c.getString(c.getColumnIndexOrThrow("body"));
                objSms.threadId = c.getString(c.getColumnIndexOrThrow("read"));
                String dateTime = c.getString(c.getColumnIndexOrThrow("date"));
                Date d = new Date(Long.parseLong(dateTime));
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmm", Locale.US);
                objSms.date = dateFormatter.format(d);
                objSms.time = timeFormatter.format(d);

                Log.d(TAG, "getAllSms: objSms.date " + objSms.date + " objSms.time " + objSms.time);
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.type = "inbox";
                } else {
                    objSms.type = "sent";
                }
                Log.d(TAG, "getAllSms: c" + objSms);
                listSms.add(objSms);
                c.moveToNext();
            }
        } else {
//            throw new RuntimeException("You have no SMS");
        }
        c.close();
        Log.e("apps", listSms.size() + "");
        ObjectBox.get().boxFor(Sms.class).put(listSms);
    }

    private void getCallLogs() {

        ContentResolver cr = getContext().getContentResolver();
        Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int totalCall = 1;

        if (c != null) {
            totalCall = c.getCount(); // intenger call log limit
            CallLogLocal callLog;
            List<CallLogLocal> listCallLog = new ArrayList<>();

            if (c.moveToLast()) { //starts pulling logs from last - you can use moveToFirst() for first logs
                for (int j = 0; j < totalCall; j++) {


                    String phNumber = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    String callDate = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    String callDuration = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    Date dateFormat = new Date(Long.parseLong(callDate));
                    int direction = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(CallLog.Calls.TYPE)));

                    c.moveToPrevious(); // if you used moveToFirst() for first logs, you should this line to moveToNext
                    Log.d(TAG, "getCallLogs: phNumber " + phNumber + " callDuration " + callDuration + " callDayTimes " + dateFormat + " direction " + direction);
                    callLog = new CallLogLocal();
                    callLog.callNumber = phNumber;
                    callLog.callDate = dateFormat;
                    callLog.callDuration = Long.parseLong(callDuration);
                    callLog.direction = direction;
                    listCallLog.add(callLog);
                }
            }
            c.close();
            ObjectBox.get().boxFor(CallLogLocal.class).put(listCallLog);

        }
    }

    private void getContactList() {
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Log.d(TAG, "getContactList: ");
        String birthday = "";
        String phoneNo = "";
        String mobilePhoneNo = "";
        String company = "";
        String strt = "";
        String email = "";
        Contact contactRequest;
        List<Contact> list = new ArrayList<>();
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndexOrThrow(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    phoneNo = getPhoneNumber(id).size() > 0 ? getPhoneNumber(id).get(0) : "";
                    mobilePhoneNo = getPhoneNumber(id).size() > 1 ? getPhoneNumber(id).get(1) : "";
                    email = getEmail(id);
                    birthday = getBirthDay(id);
                    strt = getAddress(id);
                    company = getCompany(id);
                    contactRequest = new Contact(phoneNo, mobilePhoneNo, email, birthday, name, company, strt);
                    list.add(contactRequest);
                }
            }
        }
        ObjectBox.get().boxFor(Contact.class).put(list);
        if (cur != null) {
            cur.close();
        }
    }

    private String getCompany(String id) {
        String company = "";
        ContentResolver cr = getContext().getContentResolver();

        String orgWhere = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] orgWhereParams = new String[]{id,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI,
                null, orgWhere, orgWhereParams, null);

        if (cursor == null) return null;
        if (cursor.moveToFirst()) {
            company = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY));

        }
        return company;
    }

    private String getBirthDay(String id) {
        ContentResolver cr = getContext().getContentResolver();
        String birthday = "";
        Cursor bdc = cr.query(android.provider.ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Event.DATA}, android.provider.ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Event.TYPE + " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, android.provider.ContactsContract.Data.DISPLAY_NAME);
        if (bdc.getCount() > 0) {
            while (bdc.moveToNext()) {
                birthday = bdc.getString(0);
                // now "id" is the user's unique ID, "name" is his full name and "birthday" is the date and time of his birth
            }
        }
        bdc.close();
        return birthday;
    }

    private String getEmail(String id) {
        ContentResolver cr = getContext().getContentResolver();
        String email = "";
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{id}, null);
        while (emailCur.moveToNext()) {
            email = emailCur.getString(emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
        }
        emailCur.close();
        return email;
    }

    private List<String> getPhoneNumber(String id) {
        ContentResolver cr = getContext().getContentResolver();
        List<String> phoneNo = new ArrayList<>();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id}, null);
        while (pCur.moveToNext()) {
            phoneNo.add(pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            Log.d(TAG, "getPhoneNumber: phoneNo " + phoneNo);
        }
        pCur.close();
        return phoneNo;
    }

    private String getAddress(String id) {
        ContentResolver cr = getContext().getContentResolver();
        String strt = "";
        Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Cursor postal_cursor = cr.query(
                postal_uri,
                null,
                ContactsContract.Data.CONTACT_ID + "= ?",
                new String[]{id},
                null);
        while (postal_cursor.moveToNext()) {
            strt = postal_cursor.getString(postal_cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        }
        postal_cursor.close();
        return strt;
    }

    private void postContact(Contact contact) {
        SharedPreferences sp = getContext().getSharedPreferences("pref", 0);
        String userToken = sp.getString("token", "");
        ContactRequest callLogRequest = new ContactRequest();
        callLogRequest.copy(contact);
        APIManager.getInstance(getContext()).postContact(callLogRequest, userToken, new Callback<ContactResponse>() {

            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                Log.d(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {

            }
        });
    }

    @Override

    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
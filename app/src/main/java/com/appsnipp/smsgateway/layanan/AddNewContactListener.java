/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.layanan;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

public class AddNewContactListener extends Service {
    private static final String TAG = "AddNewContactListener";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,
                true, new MyContactContentObserver());
    }

    public static class MyContactContentObserver extends ContentObserver {
        public MyContactContentObserver() {
            super(null);
            Log.d(":-> Service Called", "in construcrtor ");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            Log.d(":-> Service Called", "In onChange");
//                ContactsDbOprations.getAllContacts();
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }
}

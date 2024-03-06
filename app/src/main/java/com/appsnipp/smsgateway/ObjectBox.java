package com.appsnipp.smsgateway;

import android.content.Context;

import io.objectbox.BoxStore;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
    //    boxStore = objectbox.builder()
    //            .androidContext(context.getApplicationContext())
    //            .build();
    }

    public static BoxStore get() { return boxStore; }
}

/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.data;

import java.util.concurrent.atomic.AtomicLong;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Sms {
    @Id
    public long id;
    public String address;
    public String displayName;
    public String threadId;
    public String date;
    public String time;
    public String msg;
    public String type;

    public Sms() {
//        id = count.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Sms{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", displayName='" + displayName + '\'' +
                ", threadId='" + threadId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

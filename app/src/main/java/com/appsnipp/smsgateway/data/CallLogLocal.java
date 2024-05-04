/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.data;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class CallLogLocal {
    @Id
    public long id;
    public String callNumber;
    public long callDuration;
    public Date callDate;
    public int direction;

    public CallLogLocal() {
    }

    @Override
    public String toString() {
        return "CallLogLocal{" +
                "id=" + id +
                ", callNumber='" + callNumber + '\'' +
                ", callDuration='" + callDuration + '\'' +
                ", callDate='" + callDate + '\'' +
                '}';
    }
}

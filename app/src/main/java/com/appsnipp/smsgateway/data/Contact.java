/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.data;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Contact {
    @Id
    public long id;

    String contactPhoneNumber;
    String contactMobileNumber;
    String contactEmail;
    String contactBirthDay;
    String contactName;
    String contactCompany;
    String contactAddress;

    public Contact() {
    }

    public Contact(String contactPhoneNumber,
                   String contactMobileNumber,
                   String contactEmail,
                   String contactBirthDay,
                   String contactName,
                   String contactCompany,
                   String contactAddress) {
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactMobileNumber = contactMobileNumber;
        this.contactEmail = contactEmail;
        this.contactBirthDay = contactBirthDay;
        this.contactName = contactName;
        this.contactCompany = contactCompany;
        this.contactAddress = contactAddress;
    }

    @Override
    public String toString() {
        return "ContactRequest{" +
                "contactPhoneNumber='" + contactPhoneNumber + '\'' +
                ", contactMobileNumber='" + contactMobileNumber + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactBirthDay='" + contactBirthDay + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactCompany='" + contactCompany + '\'' +
                ", contactAddress='" + contactAddress + '\'' +
                '}';
    }
}

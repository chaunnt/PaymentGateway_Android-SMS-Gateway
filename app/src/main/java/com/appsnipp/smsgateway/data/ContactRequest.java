/*
 * Copyright (c) 2024. rogergcc
 */

package com.appsnipp.smsgateway.data;

public class ContactRequest {
    String contactPhoneNumber;
    String contactMobileNumber;
    String contactEmail;
    String contactBirthDay;
    String contactName;
    String contactCompany;
    String contactAddress;

    public ContactRequest() {
    }

    public ContactRequest(String contactPhoneNumber,
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

    public void copy(Contact contact) {
        this.contactPhoneNumber = contact.contactPhoneNumber;
        this.contactMobileNumber = contact.contactMobileNumber;
        this.contactEmail = contact.contactEmail;
        this.contactBirthDay = contact.contactBirthDay;
        this.contactName = contact.contactName;
        this.contactCompany = contact.contactCompany;
        this.contactAddress = contact.contactAddress;
    }
}

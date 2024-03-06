package com.appsnipp.smsgateway.data;

public class SmsRequest {
    String smsMessageContent;
    String smsMessageNote;
    String smsMessageOrigin;
    String smsMessageStatus;
    String smsReceiveDate;
    String smsReceiveTime;
    String smsHash;

    public SmsRequest(String smsMessageContent, String smsMessageNote, String smsMessageOrigin,
                      String smsMessageStatus, String smsReceiveDate, String smsReceiveTime,
                      String smsHash) {
        this.smsMessageContent = smsMessageContent;
        this.smsMessageNote = smsMessageNote;
        this.smsMessageOrigin = smsMessageOrigin;
        this.smsMessageStatus = smsMessageStatus;
        this.smsReceiveDate = smsReceiveDate;
        this.smsReceiveTime = smsReceiveTime;
        this.smsHash = smsHash;
    }

}

package com.appsnipp.smsgateway.data;

public class SmsRequest {
    //    String smsMessageContent;
//    String smsMessageNote;
//    String smsMessageOrigin;
//    String smsMessageStatus;
    String destinationPhoneNumber;
    String originPhoneNumber;
    String smsSendDate;
    String smsSendTime;
    String smsMessageStatus;
    String message;

    public SmsRequest(String originPhoneNumber,
                      String destinationPhoneNumber,
                      String message) {
        this.originPhoneNumber = originPhoneNumber;
        this.destinationPhoneNumber = destinationPhoneNumber;
        this.message = message;
    }

    public SmsRequest(
            String originPhoneNumber,
            String destinationPhoneNumber,
            String message,
            String smsSendDate,
            String smsSendTime,
            String smsMessageStatus
    ) {
        this.destinationPhoneNumber = destinationPhoneNumber;
        this.originPhoneNumber = originPhoneNumber;
        this.smsSendDate = smsSendDate;
        this.smsSendTime = smsSendTime;
        this.smsMessageStatus = smsMessageStatus;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SmsRequest{" +
                "destinationPhoneNumber='" + destinationPhoneNumber + '\'' +
                ", originPhoneNumber='" + originPhoneNumber + '\'' +
                ", smsSendDate='" + smsSendDate + '\'' +
                ", smsSendTime='" + smsSendTime + '\'' +
                ", smsMessageStatus='" + smsMessageStatus + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

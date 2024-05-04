package com.appsnipp.smsgateway.data;

public class CallLogRequest {
    String callSender;
    String callReceiver;
    String callDuration;
    String callDate;

    public CallLogRequest(String callSender, String callReceiver, String callDuration, String callDate) {
        this.callSender = callSender;
        this.callReceiver = callReceiver;
        this.callDuration = callDuration;
        this.callDate = callDate;
    }

    @Override
    public String toString() {
        return "CallLogRequest{" +
                "callSender='" + callSender + '\'' +
                ", callReceiver='" + callReceiver + '\'' +
                ", callDuration='" + callDuration + '\'' +
                ", callDate='" + callDate + '\'' +
                '}';
    }
}

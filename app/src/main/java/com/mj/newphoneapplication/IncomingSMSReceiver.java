package com.mj.newphoneapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSMSReceiver extends BroadcastReceiver {

    private static final String TAG = "IncomingSMSReceiver";
//
//    private final String serviceProviderNumber;
//    private final String serviceProviderSmsCondition;
//
//    private Listener listener;
//
//    public IncomingSMSReceiver(String serviceProviderNumber, String serviceProviderSmsCondition) {
//        this.serviceProviderNumber = serviceProviderNumber;
//        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();

                    Toast.makeText(context, smsSender  + smsBody, Toast.LENGTH_SHORT).show();
                    MainActivity.getInstace().setIncomingName(smsSender);
                    MainActivity.getInstace().setIncomingMessage(smsBody);
                    MainActivity.getInstace().startPopupSMS();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                        MainActivity.getInstace().startPopupSMS();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }

//            if (smsSender.equals(serviceProviderNumber) && smsBody.startsWith(serviceProviderSmsCondition)) {
//                if (listener != null) {
//                    listener.onTextReceived(smsBody);
//                }
//            }
        }
    }

//    void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    interface Listener {
//        void onTextReceived(String text);
//    }
}


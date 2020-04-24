package com.mj.newphoneapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncomingSMSReceiver extends BroadcastReceiver {

    private String incomingSmsSender;
    private String incomingSmsBody;

    @Override
    public void onReceive(Context context, Intent intent) {

        //ArrayList<UrlInfo> urlInfos = MainActivity.getInstace().getUrlArray();


        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();

                    Toast.makeText(context, smsSender  + smsBody, Toast.LENGTH_SHORT).show();
                    ArrayList urls = pullLinks(smsBody);

                    if(Settings.canDrawOverlays(context)){
                        Intent serviceIntent = new Intent(context, MyServiceSMS.class);
                        serviceIntent.putExtra("incomingSender",smsSender);
                        serviceIntent.putExtra("incomingBody",smsBody);
                        serviceIntent.putExtra("incomingUrls",urls);
                        context.startService(serviceIntent);
                    }



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
                    }
                    smsSender = messages[0].getOriginatingAddress();

                    if(Settings.canDrawOverlays(context)){
                        Intent serviceIntent = new Intent(context, MyServiceSMS.class);
                        serviceIntent.putExtra("incomingNumber",smsSender);
                        serviceIntent.putExtra("incomingName",smsBody);
                        context.startService(serviceIntent);
                    }

                }
            }

        }
    }

    public static ArrayList pullLinks(String text) {
        ArrayList links = new ArrayList();

        String regex = "\\(?\\b(http://|www[.]|https://)[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")){
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }

}


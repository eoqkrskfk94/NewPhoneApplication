package com.mj.newphoneapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class IncomingCallReceiver extends BroadcastReceiver {

    static TimerTask tt;
    public int counter = 0;
    static int call = 0;
    static int checked = 0;
    static int unknownCall;
    static String number;
    static String contactName;


    @Override
    public void onReceive(Context context, Intent intent){

        //ITelephony telephonyService;
        ArrayList<ContactInfo> contactArray = MainActivity.getInstace().getContactArray();
        ArrayList<DatabaseInfo> databaseArray = MainActivity.getInstace().getDatbaseArray();




        try{

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);


            System.out.println(number);



            if(number != null){
                MainActivity.getInstace().setIncomingNumber(number);
            }



            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){

                if(number != null){
                    Boolean exist = contactExists(context,number);
                    if(exist){
                        Toast.makeText(context, "In Contact List " + number, Toast.LENGTH_SHORT).show();
                        MainActivity.getInstace().setIncomingName(contactName);
                        unknownCall = 0;
                    }
                    else{
                        Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 1;
                    }
//                    if (contactArray != null){
//                        System.out.println("check");
//                        for(int i = 0; i < contactArray.size(); i++){
//                            if(contactArray.get(i).getPhoneNumber().equals(number)){
//                                //System.out.println(contactArray.get(i).getPhoneNumber());
//                                Toast.makeText(context, "In Contact List " + number, Toast.LENGTH_SHORT).show();
//                                unknownCall = 0;
//                            }
//                            else{
//                                Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
//                                unknownCall = 1;
//                            }
//                        }
//                    }

                    if (databaseArray != null){
                        for(int i = 0; i < databaseArray.size(); i++){

                            if(databaseArray.get(i).getNumber().equals(number)){
                                Toast.makeText(context, "In database Contact List " + number, Toast.LENGTH_SHORT).show();
                                unknownCall = 1;
                                break;
                            }

                        }

                    }
                    checked = 1;
                    MainActivity.getInstace().startPopup();

                }

            }

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){

                checked = 0;
                MainActivity.getInstace().stopPop();
                if(number != null){
                    counter = 0;
                    if(call == 0){
                        Intent goIntent = new Intent(context, MainActivity.class);
                        goIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(goIntent);
                        call++;
                        tt = timerTaskMaker();
                        final Timer timer = new Timer();
                        timer.schedule(tt,0,1000);
                    }

                }

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){

                MainActivity.getInstace().stopPop();
                tt.cancel();
                call = 0;
                MainActivity.getInstace().updateTheTimeView(0,unknownCall);
                MainActivity.getInstace().updateTheBacground(-1);
                MainActivity.getInstace().finish();
            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public TimerTask timerTaskMaker(){
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                MainActivity.getInstace().updateTheTimeView(counter,unknownCall);
                MainActivity.getInstace().updateTheTextView(number);
                counter++;
            }
        };
        return  tempTask;
    }

    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                contactName = cur.getString(2);
                cur.close();
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }




}
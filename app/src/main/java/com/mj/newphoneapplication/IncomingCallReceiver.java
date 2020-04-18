package com.mj.newphoneapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class IncomingCallReceiver extends BroadcastReceiver {

    static TimerTask tt;
    public int counter = 0;
    static int call = 0;
    static int unknownCall;
    static String number;


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
                for(int i = 0; i < contactArray.size(); i++){

                    if(contactArray.get(i).getPhoneNumber().equals(number)){
                        //Toast.makeText(context, "In Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 0;
                    }
                    else{
                        //Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 1;
                    }
                }

                for(int i = 0; i < databaseArray.size(); i++){

                    if(databaseArray.get(i).getNumber().equals(number)){
                        Toast.makeText(context, "In database Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 0;
                    }
                    else{
                        //Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 1;
                    }
                }

            }


            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){

                if(number != null){
                    MainActivity.getInstace().startPopup();
                    //MainActivity.getInstace().updateTheTextView(number);
                }

            }

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){


                MainActivity.getInstace().stopPop();
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




}
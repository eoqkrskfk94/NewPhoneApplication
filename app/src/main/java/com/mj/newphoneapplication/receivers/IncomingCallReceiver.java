package com.mj.newphoneapplication.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mj.newphoneapplication.items.DatabaseInfo;
import com.mj.newphoneapplication.activities.MainActivity;
import com.mj.newphoneapplication.services.CallService;
import com.mj.newphoneapplication.services.RingingService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

import static android.content.Context.POWER_SERVICE;

public class IncomingCallReceiver extends BroadcastReceiver {

    static TimerTask tt;
    public int counter = 0;
    static int call = 0;
    static int checked = 0;
    static int unknownCall;
    static String number;
    static String contactName;
    static String lastState;

    private String incomingNumber;
    private static String incomingName;

    private ArrayList<DatabaseInfo> databaseArray;

    PowerManager powerManager;
    static PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //ITelephony telephonyService;

        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        if(wakeLock == null){
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TimerWakeLock");

        }


        try{

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);


            if(number != null){

                incomingNumber = number;
            }


            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){


                if(number != null){
                    lastState = state;
                    Boolean exist = contactExists(context,number);
                    if(exist){
                        //Toast.makeText(context, "In Contact List " + number, Toast.LENGTH_SHORT).show();
                        incomingName = contactName;
                        unknownCall = 0;
                    }
                    else{
                        //Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
                        if(databaseArray == null){
                            databaseArray = new ArrayList<DatabaseInfo>();
                            db.collection("entities")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    if(document.getId().equals(number)){
                                                        incomingName = document.getData().get("이름").toString();
                                                        RingingService.setName(incomingName);
                                                    }
                                                    DatabaseInfo databaseInfo = new DatabaseInfo();
                                                    databaseInfo.setNumber(document.getId());
                                                    databaseInfo.setName(document.getData().get("이름").toString());
                                                    databaseInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
                                                    databaseArray.add(databaseInfo);

                                                }

                                                if(incomingName == null){
                                                    incomingName = "모르는 번호";
                                                    RingingService.setName(incomingName);
                                                }


                                            } else {
                                                Log.w("Bad", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                        }
                        else{
                            databaseArray = MainActivity.getInstace().getDatbaseArray();
                        }
                        unknownCall = 1;
                    }

                    checked = 1;
                    if(Settings.canDrawOverlays(context)){
                        Intent serviceIntent = new Intent(context, RingingService.class);
                        serviceIntent.putExtra("incomingNumber",incomingNumber);
                        serviceIntent.putExtra("incomingName",incomingName);
                        context.startService(serviceIntent);
                    }

                }

            }

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){


                checked = 0;


                context.stopService(new Intent(context, RingingService.class));
                if ((intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))){

                }
                else{
                    //incoming call

                    if(number != null){
                        counter = 0;
                        if(lastState.equals("RINGING")){

                            wakeLock.acquire();
                            //System.out.println("starts wakelock");

                            if(Settings.canDrawOverlays(context)){

                                Intent serviceIntent = new Intent(context, CallService.class);
                                serviceIntent.putExtra("incomingNumber",incomingNumber);
                                serviceIntent.putExtra("incomingName",incomingName);
                                serviceIntent.putExtra("unknownCall", unknownCall);
                                context.startService(serviceIntent);

                            }

                            tt = timerTaskMaker();
                            final Timer timer = new Timer();
                            timer.schedule(tt, 0, 1000);


                        }

                    }
                }


            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){

                if(wakeLock.isHeld()){
                    //System.out.println("wakeLock released");
                    wakeLock.release();
                }

                tt.cancel();
                lastState = state;
                incomingName = null;
                //CallService.stopTimer();
                context.stopService(new Intent(context, CallService.class));
                call = 0;

            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public TimerTask timerTaskMaker(){
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                if(CallService.getInstace() != null) CallService.getInstace().updateTime(counter,unknownCall);

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

    public String getLastState() {
        return lastState;
    }

    public void setLastState(String lastState) {
        this.lastState = lastState;
    }




}
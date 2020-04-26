package com.mj.newphoneapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.TimerTask;

import androidx.annotation.NonNull;

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
    private String incomingName;

    private ArrayList<DatabaseInfo> databaseArray;

    @Override
    public void onReceive(Context context, Intent intent){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //ITelephony telephonyService;

        if(databaseArray == null){
            databaseArray = new ArrayList<DatabaseInfo>();
            db.collection("entities")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DatabaseInfo databaseInfo = new DatabaseInfo();
                                    databaseInfo.setNumber(document.getId());
                                    databaseInfo.setName(document.getData().get("이름").toString());
                                    databaseInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
                                    databaseArray.add(databaseInfo);

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


        try{

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);




            if(number != null){

                incomingNumber = number;
            }


            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){


                if(number != null){
                    lastState =state;
                    Boolean exist = contactExists(context,number);
                    if(exist){
                        //Toast.makeText(context, "In Contact List " + number, Toast.LENGTH_SHORT).show();
                        incomingName = contactName;
                        unknownCall = 0;
                    }
                    else{
                        //Toast.makeText(context, "Not in Contact List " + number, Toast.LENGTH_SHORT).show();
                        unknownCall = 1;
                    }

                    checked = 1;
                    if(Settings.canDrawOverlays(context)){
                        Intent serviceIntent = new Intent(context, MyService.class);
                        serviceIntent.putExtra("incomingNumber",incomingNumber);
                        serviceIntent.putExtra("incomingName",incomingName);
                        context.startService(serviceIntent);
                    }


                }

            }

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){


                checked = 0;

                context.stopService(new Intent(context, MyService.class));

                if(number != null){
                    counter = 0;
                    if(lastState.equals("RINGING")){
                        Intent goIntent = new Intent(context, CallActivity.class);
                        goIntent.putExtra("incomingNumber",incomingNumber);
                        goIntent.putExtra("incomingName",incomingName);
                        goIntent.putExtra("unknownCall",unknownCall);
                        goIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(goIntent);
//                        call++;
//                        tt = timerTaskMaker();
//                        final Timer timer = new Timer();
//                        timer.schedule(tt,0,1000);
                    }

                }

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){

                context.stopService(new Intent(context, MyService.class));
//                MainActivity.getInstace().stopPop();
//                tt.cancel();
//                call = 0;
                CallActivity.getInstace().updateTheTimeView(0,unknownCall);
                CallActivity.getInstace().updateTheBacground(-1);

                //if(tt != null) tt.cancel();
                call = 0;
                CallActivity.getInstace().updateTheTimeView(0,unknownCall);
                CallActivity.getInstace().updateTheBacground(-1);
                CallActivity.getInstace().stopTimer();
                CallActivity.getInstace().endCall();
            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public TimerTask timerTaskMaker(){
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                //CallActivity.getInstace().updateTheTimeView(counter,unknownCall);
                //CallActivity.getInstace().updateTheTextView(number);
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
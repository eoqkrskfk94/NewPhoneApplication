package com.mj.newphoneapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import pl.droidsonroids.gif.GifImageView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mj.newphoneapplication.items.DatabaseInfo;
import com.mj.newphoneapplication.R;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class CallActivity extends AppCompatActivity {

    private static CallActivity ins;

    SharedPreferences prefs;

    private String incomingNumber;
    private String incomingName;
    static String contactName;
    private int unknownCall;
    static TimerTask tt;
    public int counter = 0;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        incomingNumber = (String) intent.getExtras().get("incomingNumber");
        incomingName = (String) intent.getExtras().get("incomingName");
        unknownCall = (int) intent.getExtras().get("unknownCall");
        ins = this;

        tt = timerTaskMaker();
        final Timer timer = new Timer();
        timer.schedule(tt, 0, 1000);

         name = (TextView) findViewById(R.id.nameView);

        Boolean exist = contactExists(this, incomingNumber);
        if (exist) {
            incomingName = contactName;
            name.setText(incomingName);
        } else {
            db.collection("entities")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if (document.getId().equals(incomingNumber)) {
                                        incomingName = document.getData().get("이름").toString();
                                        name.setText(incomingName);
                                    }
                                    DatabaseInfo databaseInfo = new DatabaseInfo();
                                    databaseInfo.setNumber(document.getId());
                                    databaseInfo.setName(document.getData().get("이름").toString());
                                    databaseInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));

                                }

                                if (incomingName == null) {
                                    incomingName = "모르는 번호";
                                    name.setText(incomingName);

                                }


                            } else {
                                Log.w("Bad", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }




    public static CallActivity getInstace() {
        return ins;
    }

    public TimerTask timerTaskMaker() {
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                updateTheTimeView(counter, unknownCall);
                updateTheTextView(incomingNumber);
                counter++;
            }
        };
        return tempTask;
    }

    public void updateTheTextView(final String Number) {
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView number = (TextView) findViewById(R.id.numberView);
                number.setText(Number);


            }
        });
    }

    public void updateTheBacground(final int level) {
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.background);
                TextView textView = (TextView) findViewById(R.id.dangertxt);
                GifImageView gif = (GifImageView) findViewById(R.id.gif);
                if (level == -1) {
                    layout.setBackgroundColor(Color.rgb(255, 255, 255));
                    gif.setVisibility(View.INVISIBLE);

                } else if (level == 0) {
                    layout.setBackgroundColor(Color.rgb(154, 209, 89));
                    textView.setText("안전");
                    gif.setVisibility(View.INVISIBLE);

                } else if (level == 1) {
                    layout.setBackgroundColor(Color.rgb(242, 228, 34));
                    textView.setText("양호");
                    gif.setVisibility(View.VISIBLE);

                } else if (level == 2) {
                    layout.setBackgroundColor(Color.rgb(252, 166, 68));
                    textView.setText("주의");
                    gif.setVisibility(View.VISIBLE);
                } else if (level == 3) {
                    layout.setBackgroundColor(Color.rgb(252, 114, 68));
                    textView.setText("위험");
                    gif.setVisibility(View.VISIBLE);
                } else if (level == 4) {
                    layout.setBackgroundColor(Color.rgb(181, 181, 181));
                    textView.setText("통화종료");
                    gif.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void updateTheTimeView(final int sec, final int unknownCall) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                TextView call_time = (TextView) findViewById(R.id.timetxt);

                String level = prefs.getString("level_list", "");
                Boolean vibrate = prefs.getBoolean("vibration_alarm", false);
                Boolean voice = prefs.getBoolean("voice_alarm", false);

                int call_length[] = {0, 0};
                if (level.equals("약")) {
                    call_length[0] = 10;
                    call_length[1] = 15;
                } else if (level.equals("중")) {
                    call_length[0] = 20;
                    call_length[1] = 30;
                } else if (level.equals("강")) {
                    call_length[0] = 30;
                    call_length[1] = 60;
                }

                if (sec == 0) {
                    call_time.setText("");
                } else {
                    LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
                    String time = timeOfDay.toString();

                    call_time.setText(time);

                    if (unknownCall == 1) {
                        if (sec == 1) {
                            updateTheBacground(1);
                        } else if (sec == call_length[0]) {
                            updateTheBacground(2);
                            if (vibrate) {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                                else
                                    vibrator.vibrate(1000);
                            }
                        } else if (sec == call_length[1]) {
                            updateTheBacground(3);
                            if (vibrate) {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                                else
                                    vibrator.vibrate(1000);
                            }
                        }
                    } else {
                        updateTheBacground(0);
                    }


                }

            }
        });
    }

    public String getIncomingNumber() {
        return incomingNumber;
    }

    public void setIncomingNumber(String incomingNumber) {
        this.incomingNumber = incomingNumber;
    }

    public String getIncomingName() {
        return incomingName;
    }

    public void setIncomingName(String incomingName) {
        this.incomingName = incomingName;
    }

    public void stopTimer() {
        tt.cancel();
    }

    public void endCall() {

        updateTheBacground(4);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                finish();
            }
        }, 2500);

    }

    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
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

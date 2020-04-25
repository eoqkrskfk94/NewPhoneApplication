package com.mj.newphoneapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class CallActivity extends AppCompatActivity {

    private static CallActivity ins;


    private String incomingNumber;
    private String incomingName;
    private int unknownCall;
    static TimerTask tt;
    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Intent intent = getIntent();
        incomingNumber = (String) intent.getExtras().get("incomingNumber");
        incomingName = (String) intent.getExtras().get("incomingName");
        unknownCall = (int) intent.getExtras().get("unknownCall");
        ins = this;

        tt = timerTaskMaker();
        final Timer timer = new Timer();
        timer.schedule(tt,0,1000);



    }

    public static CallActivity  getInstace(){
        return ins;
    }

    public TimerTask timerTaskMaker(){
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                updateTheTimeView(counter,unknownCall);
                updateTheTextView(incomingNumber);
                counter++;
            }
        };
        return  tempTask;
    }

    public void updateTheTextView(final String t) {
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView number = (TextView) findViewById(R.id.numberView);
                number.setText(t);
            }
        });
    }


    public void updateTheBacground(final int level) {
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.background);
                TextView textView = (TextView)findViewById(R.id.dangertxt);
                ImageView box = (ImageView)findViewById(R.id.whitebox);
                if(level == -1){
                    layout.setBackgroundColor(Color.rgb(255, 255, 255));
                    box.setVisibility(View.VISIBLE);
                }
                else if(level == 0){
                    layout.setBackgroundColor(Color.rgb(154, 209, 89));
                    textView.setText("안전");
                    box.setVisibility(View.INVISIBLE);

                }

                else if(level == 1){
                    layout.setBackgroundColor(Color.rgb(242, 228, 34));
                    textView.setText("양호");
                    box.setVisibility(View.INVISIBLE);

                }

                else if(level == 2){
                    layout.setBackgroundColor(Color.rgb(252, 166, 68));
                    textView.setText("주의");
                }

                else if(level == 3){
                    layout.setBackgroundColor(Color.rgb(252, 114, 68));
                    textView.setText("위험");
                }
            }
        });
    }

    public void updateTheTimeView(final int sec, final int unknownCall) {
        final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        CallActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView call_time = (TextView) findViewById(R.id.timetxt);
                if(sec == 0){
                    call_time.setText("");
                }
                else{
                    LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
                    String time = timeOfDay.toString();

                    call_time.setText(time);

                    if(unknownCall == 1){
                        if(sec == 1){
                            updateTheBacground(1);
                        }

                        else if(sec == 10){
                            updateTheBacground(2);
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                            else
                                vibrator.vibrate(1000);

                        }

                        else if(sec == 15){
                            updateTheBacground(3);
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                            else
                                vibrator.vibrate(1000);

                        }
                    }
                    else{
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

    public  void stopTimer(){
        tt.cancel();
    }



}

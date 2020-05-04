package com.mj.newphoneapplication;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import pl.droidsonroids.gif.GifImageView;

public class CallService extends Service {

    WindowManager wm;
    View mView;
    static TimerTask tt;
    public int counter = 0;
    private static CallService ins;

    SharedPreferences prefs;

    static String number = "";
    static String name = "";
    static int unknownCall;
    private WindowManager.LayoutParams params;
    private float START_X, START_Y;                            //움직이기 위해 터치한 시작 점
    private int PREV_X, PREV_Y;                                //움직이기 이전에 뷰가 위치한 점
    private int MAX_X = -1, MAX_Y = -1;
    private static TextView nameView;
    private static TextView call_time;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        number = (String) intent.getExtras().get("incomingNumber");
        name = (String) intent.getExtras().get("incomingName");
        //unknownCall = (int) intent.getExtras().get("unknownCall");
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ins = this;


        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mView = inflate.inflate(R.layout.view_in_service_call, null);
        mView.setOnTouchListener(mViewTouchListener);
        call_time = (TextView) mView.findViewById(R.id.timeView);

//        tt = timerTaskMaker();
//        final Timer timer = new Timer();
//        timer.schedule(tt, 0, 1000);

        final TextView textView = (TextView) mView.findViewById(R.id.textView);
        nameView = (TextView) mView.findViewById(R.id.nameView);

        textView.setText(phone(number));
        if (name != null) nameView.setText(name);


        final ImageButton cancel = (ImageButton) mView.findViewById(R.id.cancelbtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();

            }
        });
        wm.addView(mView, params);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null) {
            if (mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
                    if (MAX_X == -1)
                        setMaxPosition();
                    START_Y = event.getRawY();                    //터치 시작 점
                    PREV_Y = params.y;                            //뷰의 시작 점
                    break;
                case MotionEvent.ACTION_MOVE:
                    int y = (int) (event.getRawY() - START_Y);    //이동한 거리

                    //터치해서 이동한 만큼 이동 시킨다
                    params.y = PREV_Y + y;

                    //optimizePosition();        //뷰의 위치 최적화
                    wm.updateViewLayout(mView, params);    //뷰 업데이트
                    break;
            }

            return true;
        }
    };

    public static CallService getInstace() {
        return ins;
    }


    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(matrix);        //화면 정보를 가져와서

        MAX_Y = matrix.heightPixels - mView.getHeight();            //y 최대값 설정
    }


    private void optimizePosition() {
        //최대값 넘어가지 않게 설정

        if (params.y > MAX_Y) params.y = MAX_Y;
        if (params.y < -800) params.y = -800;
    }

    public static void setName(String name) {
        nameView.setText(name);
        CallService.name = name;
    }


    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(mView != null) updateTheTimeView(counter, unknownCall);
        }
    };
    public void updateTime(int counter, int unknownCall){
        this.counter = counter;
        this.unknownCall = unknownCall;
        Message msg = handler.obtainMessage();
        handler.sendMessage(msg);
    }

    public TimerTask timerTaskMaker() {
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);

                //counter++;
            }
        };
        return tempTask;
    }

    public static void stopTimer() {
        tt.cancel();
    }

    public void updateTheTimeView(final int sec, final int unknownCall) {

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        String level = prefs.getString("level_list", "강");
        Boolean vibrate = prefs.getBoolean("vibration_alarm", true);
        Boolean voice = prefs.getBoolean("voice_alarm", false);

        int call_length[] = {0, 0, 0};
        if (level.equals("")) {
            //call_length[0] = 540;
            //call_length[1] = 900;
            call_length[0] = 30;
            call_length[1] = 60;
            call_length[2] = 90;
        }else if (level.equals("약")) {
            //call_length[0] = 180;
            //call_length[1] = 300;
            call_length[0] = 30;
            call_length[1] = 60;
            call_length[2] = 90;
        } else if (level.equals("중")) {
            //call_length[0] = 540;
            //call_length[1] = 900;
            call_length[0] = 20;
            call_length[1] = 30;
            call_length[2] = 40;
        } else if (level.equals("강")) {
            //call_length[0] = 60;
            //call_length[1] = 180;
            call_length[0] = 10;
            call_length[1] = 15;
            call_length[2] = 20;

        }

        if (sec == 0) {
            call_time.setText("00:00");
        } else {
            LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
            String time = timeOfDay.toString();

            call_time.setText(time);

            if (unknownCall == 1) {
                if (sec == 1) {
                    updateTheBacground(0);
                } else if (sec == call_length[0]) {
                    updateTheBacground(1);
                    if (vibrate) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        else
                            vibrator.vibrate(1000);
                    }
                } else if (sec == call_length[1]) {
                    updateTheBacground(2);
                    if (vibrate) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
                            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        else
                            vibrator.vibrate(1000);
                    }
                } else if (sec == call_length[2]) {
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

    public void updateTheBacground(final int level) {
        ConstraintLayout layout = (ConstraintLayout)mView.findViewById(R.id.callBoxView);
        TextView textView = (TextView)mView.findViewById(R.id.dangertxt);
        GifImageView gif = (GifImageView)mView.findViewById(R.id.gif);
        if (level == -1) {
            //layout.setBackground();
            gif.setVisibility(View.INVISIBLE);

        } else if (level == 0) {
            layout.setBackgroundResource(R.drawable.call_box1);
            textView.setText("안전");
            //gif.setVisibility(View.INVISIBLE);

        } else if (level == 1) {
            layout.setBackgroundResource(R.drawable.call_box2);
            textView.setText("양호");
            //gif.setVisibility(View.VISIBLE);

        } else if (level == 2) {
            layout.setBackgroundResource(R.drawable.call_box3);
            textView.setText("주의");
            //gif.setVisibility(View.VISIBLE);
        } else if (level == 3) {
            layout.setBackgroundResource(R.drawable.call_box4);
            textView.setText("위험");
            //gif.setVisibility(View.VISIBLE);
        } else if (level == 4) {
            layout.setBackgroundResource(R.drawable.call_box5);
            textView.setText("통화종료");
            //gif.setVisibility(View.INVISIBLE);
        }
    }

    public static String phone(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }


}
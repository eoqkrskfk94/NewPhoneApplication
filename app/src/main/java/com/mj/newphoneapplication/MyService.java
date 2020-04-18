package com.mj.newphoneapplication;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyService extends Service {

    WindowManager wm;
    View mView;
    String number;
    private WindowManager.LayoutParams params;




    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        number = MainActivity.getInstace().getIncomingNumber();
        System.out.println("here" + number);

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        mView = inflate.inflate(R.layout.view_in_service, null);
        final TextView textView = (TextView) mView.findViewById(R.id.textView);
        textView.setText(number);
        final ImageButton bt =  (ImageButton) mView.findViewById(R.id.bt);
        final ImageButton cancel = (ImageButton) mView.findViewById(R.id.cancelbtn);

        final Intent goIntent = new Intent(this, MainActivity.class);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bt.setImageResource(R.mipmap.ic_launcher_round);
                textView.setText("on click!!");
                startActivity(goIntent);
                stopSelf();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopSelf();

            }
        });
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }


}


package com.mj.newphoneapplication;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;

public class MyService extends Service {

    WindowManager wm;
    View mView;
    static String number = "";
    static String name = "";
    private WindowManager.LayoutParams params;
    private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
    private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
    private int MAX_X = -1, MAX_Y = -1;
    private static TextView nameView;
    @Override
    public IBinder onBind(Intent intent) {
        return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        number = (String) intent.getExtras().get("incomingNumber");
        name = (String) intent.getExtras().get("incomingName");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date now = new Date();
        String sample = formatter.format(now);




        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);




        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,

                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);



        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        mView = inflate.inflate(R.layout.view_in_service, null);
        mView.setOnTouchListener(mViewTouchListener);
        final TextView textView = (TextView) mView.findViewById(R.id.textView);
        final TextView dateView = (TextView) mView.findViewById(R.id.dateView);
        dateView.setText(formatter.format(now));
        nameView = (TextView) mView.findViewById(R.id.nameView);

        textView.setText(phone(number));
        if(name != null) nameView.setText(name);

//        db.collection("entities")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                if(document.getId().equals(number)){
//                                    System.out.println("works");
//                                    name = document.getData().get("이름").toString();
//                                    nameView.setText(name);
//                                    break;
//                                }
//
//                                DatabaseInfo databaseInfo = new DatabaseInfo();
//                                databaseInfo.setNumber(document.getId());
//                                databaseInfo.setName(document.getData().get("이름").toString());
//                                databaseInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
//
//                            }
//
//                            if(name == null) {
//                                name = "모르는 번호";
//                                nameView.setText("모르는 번호");
//                            }
//
//                        } else {
//                            Log.w("Bad", "Error getting documents.", task.getException());
//                        }
//                    }
//                });




        final ImageButton cancel = (ImageButton) mView.findViewById(R.id.cancelbtn);

        cancel.setOnClickListener(new View.OnClickListener(){
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
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
                    if(MAX_X == -1)
                        setMaxPosition();
                    START_Y = event.getRawY();                    //터치 시작 점
                    PREV_Y = params.y;                            //뷰의 시작 점
                    break;
                case MotionEvent.ACTION_MOVE:
                    int y = (int)(event.getRawY() - START_Y);	//이동한 거리

                    //터치해서 이동한 만큼 이동 시킨다
                    params.y = PREV_Y + y;

                    //optimizePosition();        //뷰의 위치 최적화
                    wm.updateViewLayout(mView, params);    //뷰 업데이트
                    break;
            }

            return true;
        }
    };

    /**
     * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
     */
    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서

        MAX_Y = matrix.heightPixels - mView.getHeight();			//y 최대값 설정
    }

    /**
     * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
     */
    private void optimizePosition() {
        //최대값 넘어가지 않게 설정

        if(params.y > MAX_Y) params.y = MAX_Y;
        if(params.y < -450) params.y = -450;
        System.out.println(params.y);
    }

    public static void setName(String name) {
        nameView.setText(name);
        MyService.name = name;
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


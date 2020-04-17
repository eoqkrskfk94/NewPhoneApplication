package com.mj.newphoneapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private static MainActivity ins;
    private ArrayList<ContactInfo> contactArray;
    private ArrayList<DatabaseInfo> datbaseArray;
    private String incomingNumber;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String[] permission_list = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;

        datbaseArray = new ArrayList<DatabaseInfo>();
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
                                System.out.println(databaseInfo);
                                datbaseArray.add(databaseInfo);

                            }


                        } else {
                            Log.w("Bad", "Error getting documents.", task.getException());
                        }
                    }
                });




        //앱 권한 받기 기능
        checkPermission();
        checkPermissionOverlay();


        //연락처 가져오기
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            getContacts();
        }





    }



    public static MainActivity  getInstace(){
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                System.out.println("This is the number: " + t);
                TextView number = (TextView) findViewById(R.id.numberView);
                number.setText(t);
            }
        });
    }

    public void updateTheBacground(final int level) {
        MainActivity.this.runOnUiThread(new Runnable() {
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
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView call_time = (TextView) findViewById(R.id.timetxt);
                if(sec == 0){
                    call_time.setText("");
                }
                else{
                    LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
                    String time = timeOfDay.toString();

                    call_time.setText(time);

                    //진동
                    final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                    if(unknownCall == 1){
                        if(sec == 1){
                            updateTheBacground(1);
                        }

                        else if(sec == 10){
                            //System.out.println("Vibrate");
                            //vibrator.vibrate(1000);
                            updateTheBacground(2);
                        }

                        else if(sec == 15){
                            updateTheBacground(3);
                        }
                    }
                    else{
                        updateTheBacground(0);
                    }


                }

            }
        });
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();
        String contactId = null;
        String displayName = null;
        contactArray = new ArrayList<ContactInfo>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    ContactInfo contactsInfo = new ContactInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactsInfo.setContactId(contactId);
                    contactsInfo.setDisplayName(displayName);

                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contactsInfo.setPhoneNumber(phoneNumber.replaceAll("-", ""));
                    }

                    phoneCursor.close();

                    contactArray.add(contactsInfo);
                }
            }
        }
        cursor.close();


    }

    public ArrayList<ContactInfo> getContactArray() {
        return contactArray;
    }

    public ArrayList<DatabaseInfo> getDatbaseArray() {
        return datbaseArray;
    }

    public String getIncomingNumber() {
        return incomingNumber;
    }

    public void setIncomingNumber(String incomingNumber) {
        this.incomingNumber = incomingNumber;
    }


    //overlay 권한받기
    public void checkPermissionOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void startPopup(){
        if(Settings.canDrawOverlays(MainActivity.this)){
            startService(new Intent(MainActivity.this, MyService.class));
        }}

    public void stopPop(){ stopService(new Intent(MainActivity.this, MyService.class));}


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        }
    }








}

package com.mj.newphoneapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.mj.newphoneapplication.Adapters.PageAdapter;
import com.mj.newphoneapplication.Fragments.MessageFragment;
import com.mj.newphoneapplication.Fragments.PhoneFragment;
import com.mj.newphoneapplication.Fragments.SearchFragment;
import com.mj.newphoneapplication.Fragments.SettingPreferenceFragment;
import com.mj.newphoneapplication.Items.PhoneParentItem;
import com.mj.newphoneapplication.Items.PhoneSubItem;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private static MainActivity ins;
    private ArrayList<ContactInfo> contactArray;
    private ArrayList<DatabaseInfo> datbaseArray;
    private ArrayList<PhoneSubItem> callLog;
    private ArrayList<PhoneParentItem> parentCallLog;
    private ArrayList<UrlInfo> urlArray;
    private int current_fragment;
    private int next_fragment;
    private Toolbar toolbar;
    long backKeyPressedTime;
    private Boolean battery;
    private Boolean overlay;

    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
    Date now = new Date();


    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String[] permission_list = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_SMS
    };

    ChipNavigationBar chipNavigationBar;
    ImageButton menuButton;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;

    PhoneFragment phoneFragment = new PhoneFragment();
    MessageFragment messageFragment = new MessageFragment();
    SearchFragment searchFragment = new SearchFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;

        TabLayout tabLayout = findViewById(R.id.tablayout);
        TabItem recentLog = findViewById(R.id.recentLog);
        TabItem messageLog = findViewById(R.id.messageLog);
        TabItem searchLog = findViewById(R.id.searchLog);
        ViewPager viewPager = findViewById(R.id.viewpager);


        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.AddFragment(new PhoneFragment(),"최근기록");
        pageAdapter.AddFragment(new MessageFragment(),"메세지");
        pageAdapter.AddFragment(new SearchFragment(),"검색");
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //chipNavigationBar = findViewById(R.id.bottomNav);
        menuButton = findViewById(R.id.menuBtn);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);

                //액티비티 전환 애니메이션 설정하는 부분
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frameLayout, phoneFragment).commitAllowingStateLoss();
//        current_fragment = 1;
//
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int id) {
//                Fragment fragment = null;
//                switch (id) {
//                    case R.id.phone:
//                        fragment = new PhoneFragment();
//                        next_fragment = 1;
//                        break;
//                    case R.id.message:
//                        fragment = new MessageFragment();
//                        next_fragment = 2;
//                        break;
//                    case R.id.search:
//                        fragment = new SearchFragment();
//                        next_fragment = 3;
//                        break;
//                }
//
//                if (fragment != null) {
//
//                    if (current_fragment < next_fragment) {
//                        current_fragment = next_fragment;
//                        fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
//                                .replace(R.id.frameLayout, fragment)
//                                .commit();
//                    } else if (current_fragment > next_fragment) {
//                        current_fragment = next_fragment;
//                        fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
//                                .replace(R.id.frameLayout, fragment)
//                                .commit();
//                    }
//
//                }
//            }
//        });

        //데이터베이스 번호 목록 불러오기

//        if (datbaseArray == null) {
//            datbaseArray = new ArrayList<DatabaseInfo>();
//            db.collection("entities")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    DatabaseInfo databaseInfo = new DatabaseInfo();
//                                    databaseInfo.setNumber(document.getId());
//                                    databaseInfo.setName(document.getData().get("이름").toString());
//                                    databaseInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
//                                    datbaseArray.add(databaseInfo);
//
//                                }
//
//
//                            } else {
//                                Log.w("Bad", "Error getting documents.", task.getException());
//                            }
//                        }
//                    });
//        }

        //데이터베이스 url 목록 불러오기

//        if (urlArray == null) {
//            urlArray = new ArrayList<UrlInfo>();
//            db.collection("banned_urls")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    UrlInfo urlInfo = new UrlInfo();
//                                    urlInfo.setUrl(document.getId());
//                                    urlInfo.setName(document.getData().get("이름").toString());
//                                    urlInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
//                                    urlArray.add(urlInfo);
//
//                                }
//
//
//                            } else {
//                                Log.w("Bad", "Error getting documents.", task.getException());
//                            }
//                        }
//                    });
//        }

        //연락처 가져오기
//        if (contactArray == null) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
//                    == PackageManager.PERMISSION_GRANTED) {
//                getContacts();
//            }
//        }

        //최근기록 가져오기


        if (callLog == null)
            callLog = getCallDetails();


        //앱 권한 받기 기능
        checkPermission();
        checkPermissionOverlay();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            battery = pm.isIgnoringBatteryOptimizations(packageName);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }


    }

    @Override
    public void onBackPressed() {
        //1번째 백버튼 클릭
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        //2번째 백버튼 클릭 (종료)
        else {
            AppFinish();
        }
    }

    //앱종료
    public void AppFinish() {
        finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public static MainActivity getInstace() {
        return ins;
    }


    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < grantResults.length; i++) {
                //허용됬다면
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "앱권한설정하세요", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }


    //overlay 권한받기
    public void checkPermissionOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overlay = Settings.canDrawOverlays(this);// 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {

            }
        }
    }


    private void getContacts() {
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

    //getter and setter
    public ArrayList<ContactInfo> getContactArray() {
        return contactArray;
    }

    public ArrayList<DatabaseInfo> getDatbaseArray() {
        return datbaseArray;
    }

    public ArrayList<PhoneParentItem> getParentCallLog() {
        return parentCallLog;
    }

    public ArrayList<UrlInfo> getUrlArray() {
        return urlArray;
    }

    public ArrayList<PhoneSubItem> getCallLog() {
        return callLog;
    }

    public Boolean getBattery() {
        return battery;
    }

    public Boolean getOverlay() {
        return overlay;
    }

    private ArrayList<PhoneSubItem> getCallDetails() {


        callLog = new ArrayList<PhoneSubItem>();

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        int idx = 0;
        Boolean flag = true;
        long day = 0;
        while(cursor.moveToNext() && idx < 90){
            idx++;
            PhoneSubItem phoneSubItem = new PhoneSubItem();
            phoneSubItem.setName(cursor.getString(name));
            phoneSubItem.setNumber(cursor.getString(number));
            phoneSubItem.setDiff_date(-1);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String sample = formatter2.format(now);

            try{
                Date date1=new SimpleDateFormat("yyyy/MM/dd").parse(sample);
                Date date2=new SimpleDateFormat("yyyy/MM/dd").parse(formatter.format(callDayTime));
                long diffInMillies = Math.abs(date1.getTime() - date2.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                if(day == 0){
                    day = diff;
                    PhoneSubItem phoneSubItem2 = new PhoneSubItem("","","",formatter2.format(callDayTime),0,day);
                    callLog.add(phoneSubItem2);

                }
                else if(day != diff){
                    day = diff;
                    PhoneSubItem phoneSubItem2 = new PhoneSubItem("","","",formatter2.format(callDayTime),0, day);
                    callLog.add(phoneSubItem2);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }


            phoneSubItem.setDate(formatter3.format(callDayTime));
            String callType = cursor.getString(type);
            int dircode = Integer.parseInt(callType);
            switch(dircode){
                case CallLog.Calls.OUTGOING_TYPE:
                    phoneSubItem.setType("OUTGOING");
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    phoneSubItem.setType("INCOMING");
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    phoneSubItem.setType("MISSED");
                    break;
            }
            callLog.add(phoneSubItem);

        }
        cursor.close();

        return callLog;
    }




}

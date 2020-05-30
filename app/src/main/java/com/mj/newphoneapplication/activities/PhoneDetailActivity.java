package com.mj.newphoneapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mj.newphoneapplication.R;


public class PhoneDetailActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageButton callBtn;
    ImageButton messageBtn;
    TextView nameView;
    TextView numberView;

    String name;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_detail);

        Intent intent = getIntent();
        name = (String) intent.getExtras().get("name");
        number = (String) intent.getExtras().get("number");


        backBtn = findViewById(R.id.backBtn);
        callBtn = findViewById(R.id.callButton);
        messageBtn = findViewById(R.id.MessageButton);
        nameView = findViewById(R.id.nameView);
        numberView = findViewById(R.id.numberView);

        if (name == null) nameView.setText("등록되지 않은 번호");
        else nameView.setText(name);
        numberView.setText(number);


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calling = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(calling);
            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
                startActivity(message);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}

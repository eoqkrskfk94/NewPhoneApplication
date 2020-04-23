package com.mj.newphoneapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private ArrayList<DatabaseInfo> datbaseArray;
    private ArrayList<UrlInfo> urlArray;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button updateBtn = (Button)findViewById(R.id.updateBtn);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);

        updateBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                spinner.setVisibility(View.VISIBLE);

                if(datbaseArray == null){
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
                                            datbaseArray.add(databaseInfo);

                                        }


                                    } else {
                                        Log.w("Bad", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }

                if(urlArray == null){
                    urlArray = new ArrayList<UrlInfo>();
                    db.collection("banned_urls")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            UrlInfo urlInfo = new UrlInfo();
                                            urlInfo.setUrl(document.getId());
                                            urlInfo.setName(document.getData().get("이름").toString());
                                            urlInfo.setSpamCount(Integer.parseInt(document.getData().get("스팸신고 건수").toString()));
                                            urlArray.add(urlInfo);

                                        }


                                    } else {
                                        Log.w("Bad", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }


                spinner.setVisibility(View.INVISIBLE);
            }
        });






    }
}

package com.mj.newphoneapplication.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mj.newphoneapplication.adapters.SubItemAdapter;
import com.mj.newphoneapplication.items.PhoneSubItem;
import com.mj.newphoneapplication.activities.MainActivity;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;


public class PhoneFragment extends Fragment {

    private RecyclerView phoneCallLogRecyclerView;
    private SubItemAdapter itemAdapter;
    ArrayList<PhoneSubItem> phoneSubItems;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView swipeDownImage;


    public PhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_phone, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.phoneSwipeRefreshLayout);
        phoneCallLogRecyclerView = rootView.findViewById(R.id.headerRecyclerView);
        phoneCallLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        phoneSubItems = new ArrayList<PhoneSubItem>();
        swipeDownImage = rootView.findViewById(R.id.swipe_down);


        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            phoneSubItems = MainActivity.getInstace().getCallLog();
        }

        if(phoneSubItems.size() == 0)
            swipeDownImage.setVisibility(View.VISIBLE);
        else
            swipeDownImage.setVisibility(View.GONE);

        itemAdapter = new SubItemAdapter(getActivity(), phoneSubItems);
        phoneCallLogRecyclerView.setAdapter(itemAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
                    phoneSubItems = MainActivity.getInstace().getCallDetails();
                    swipeDownImage.setVisibility(View.GONE);
                    itemAdapter = new SubItemAdapter(getActivity(), phoneSubItems);
                    phoneCallLogRecyclerView.setAdapter(itemAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });


        return rootView;

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
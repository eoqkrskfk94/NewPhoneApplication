package com.mj.newphoneapplication.Fragments;

import android.database.Cursor;
import android.os.Bundle;



import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mj.newphoneapplication.Adapters.ParentItemAdapter;
import com.mj.newphoneapplication.Adapters.SubItemAdapter;
import com.mj.newphoneapplication.Items.PhoneParentItem;
import com.mj.newphoneapplication.Items.PhoneSubItem;
import com.mj.newphoneapplication.MainActivity;
import com.mj.newphoneapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends Fragment {

    private RecyclerView phoneCallLogRecyclerView;
    private SubItemAdapter itemAdapter;
    ArrayList<PhoneSubItem> phoneSubItems;
    SwipeRefreshLayout swipeRefreshLayout;


    public PhoneFragment() {
        // Required empty public constructor
    }

    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
    Date now = new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_phone, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.phoneSwipeRefreshLayout);
        phoneCallLogRecyclerView = rootView.findViewById(R.id.headerRecyclerView);
        phoneCallLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        phoneSubItems = MainActivity.getInstace().getCallLog();


        itemAdapter = new SubItemAdapter(getActivity(), phoneSubItems);
        phoneCallLogRecyclerView.setAdapter(itemAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                phoneSubItems = MainActivity.getInstace().getCallDetails();
                itemAdapter = new SubItemAdapter(getActivity(), phoneSubItems);
                phoneCallLogRecyclerView.setAdapter(itemAdapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        return rootView;

    }

}
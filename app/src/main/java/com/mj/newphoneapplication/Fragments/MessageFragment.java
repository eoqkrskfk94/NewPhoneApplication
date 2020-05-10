package com.mj.newphoneapplication.Fragments;

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

import com.mj.newphoneapplication.Adapters.MessageAdapter;
import com.mj.newphoneapplication.Adapters.SubItemAdapter;
import com.mj.newphoneapplication.Items.MessageItem;
import com.mj.newphoneapplication.Items.PhoneSubItem;
import com.mj.newphoneapplication.MainActivity;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;


public class MessageFragment extends Fragment {

    private RecyclerView messageLogRecyclerView;
    private MessageAdapter itemAdapter;
    ArrayList<MessageItem> messageItems;
    SwipeRefreshLayout swipeRefreshLayout;


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_message, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.phoneSwipeRefreshLayout);
        messageLogRecyclerView = rootView.findViewById(R.id.headerRecyclerView);
        messageLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
            if(messageItems == null) messageItems = MainActivity.getInstace().getSMSDetails();


        if(messageItems != null){
            itemAdapter = new MessageAdapter(getActivity(), messageItems);
            messageLogRecyclerView.setAdapter(itemAdapter);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageItems = MainActivity.getInstace().getSMSDetails();
                itemAdapter = new MessageAdapter(getActivity(), messageItems);
                messageLogRecyclerView.setAdapter(itemAdapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}

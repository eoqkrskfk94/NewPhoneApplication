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

import com.mj.newphoneapplication.adapters.MessageAdapter;
import com.mj.newphoneapplication.items.MessageItem;
import com.mj.newphoneapplication.activities.MainActivity;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;


public class MessageFragment extends Fragment {

    private RecyclerView messageLogRecyclerView;
    private MessageAdapter itemAdapter;
    ArrayList<MessageItem> messageItems;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView swipeDownImage;


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
        messageItems = new ArrayList<MessageItem>();
        swipeDownImage = rootView.findViewById(R.id.swipe_down);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            messageItems = MainActivity.getInstace().getSMSDetails();
        }

        if(messageItems.size() == 0)
            swipeDownImage.setVisibility(View.VISIBLE);
        else
            swipeDownImage.setVisibility(View.GONE);


        itemAdapter = new MessageAdapter(getActivity(), messageItems);
        messageLogRecyclerView.setAdapter(itemAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                    messageItems = MainActivity.getInstace().getSMSDetails();
                    swipeDownImage.setVisibility(View.GONE);
                    itemAdapter = new MessageAdapter(getActivity(), messageItems);
                    messageLogRecyclerView.setAdapter(itemAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}

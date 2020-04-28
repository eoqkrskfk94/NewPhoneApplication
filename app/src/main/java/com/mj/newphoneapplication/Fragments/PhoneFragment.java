package com.mj.newphoneapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mj.newphoneapplication.Adapters.ParentItemAdapter;
import com.mj.newphoneapplication.Adapters.SubItemAdapter;
import com.mj.newphoneapplication.Items.PhoneParentItem;
import com.mj.newphoneapplication.Items.PhoneSubItem;
import com.mj.newphoneapplication.MainActivity;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends Fragment {

    private RecyclerView phoneCallLogRecyclerView;
    private SubItemAdapter itemAdapter;
    ArrayList<PhoneParentItem> phoneParentItems;
    ArrayList<PhoneSubItem> phoneSubItems;

    private ParentItemAdapter parentItemAdapter;

    public PhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_phone, container, false);
        // Inflate the layout for this fragment
        phoneCallLogRecyclerView = rootView.findViewById(R.id.headerRecyclerView);
        phoneCallLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if(phoneParentItems == null){
            phoneParentItems = MainActivity.getInstace().getParentCallLog();
        }

        if(phoneSubItems == null){
            phoneSubItems = MainActivity.getInstace().getCallLog();
        }


        itemAdapter = new SubItemAdapter(getActivity(), phoneSubItems);
        phoneCallLogRecyclerView.setAdapter(itemAdapter);
//        parentItemAdapter = new ParentItemAdapter(getActivity(),phoneParentItems);
//        phoneCallLogRecyclerView.setAdapter(parentItemAdapter);


        return rootView;


    }
}

package com.mj.newphoneapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mj.newphoneapplication.R;


public class SearchFragment extends Fragment {

    EditText searchText;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);


        searchText = rootView.findViewById(R.id.searchText);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    TextView sample = rootView.findViewById(R.id.sampleView);
                    sample.setText(searchText.getText());
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }
}

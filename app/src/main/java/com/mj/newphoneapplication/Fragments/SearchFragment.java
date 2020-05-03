package com.mj.newphoneapplication.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mj.newphoneapplication.Adapters.SearchAdapter;
import com.mj.newphoneapplication.Adapters.SubItemAdapter;
import com.mj.newphoneapplication.Items.SearchItem;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    EditText searchText;
    ImageView noResultView;
    ProgressBar loadingBar;
    private RecyclerView searchRecyclerView;
    private SearchAdapter itemAdapter;
    static String contactName;
    ArrayList<SearchItem> searchItems;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        searchRecyclerView = rootView.findViewById(R.id.searchView);
        searchText = rootView.findViewById(R.id.searchText);
        noResultView = rootView.findViewById(R.id.noresultView);
        loadingBar = rootView.findViewById(R.id.progressBar);
        loadingBar.setVisibility(View.GONE);
        noResultView.setVisibility(View.GONE);


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                noResultView.setVisibility(View.GONE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadingBar.setVisibility(View.VISIBLE);
                    searchItems = new ArrayList<SearchItem>();

                    contactExists(getActivity(),searchText.getText().toString());

                    findDatabase(searchText.getText().toString(), new FirestoreCallback() {
                        @Override
                        public void onCallback(ArrayList<SearchItem> searchItems) {

                            loadingBar.setVisibility(View.GONE);
                            searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            itemAdapter = new SearchAdapter(getActivity(), searchItems);
                            searchRecyclerView.setAdapter(itemAdapter);
                            if(searchItems.size() == 0) noResultView.setVisibility(View.VISIBLE);


                        }
                    });
                    searchText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    public void contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                contactName = cur.getString(2);
                SearchItem searchItem = new SearchItem();
                searchItem.setName(contactName);
                searchItem.setNumber(number);
                searchItems.add(searchItem);
                cur.close();
            }
        } finally {
            if (cur != null)
                cur.close();
        }
    }

    public void findDatabase(final String find_number, final FirestoreCallback firestoreCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(find_number);
        db.collection("entities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId());
                                if (document.getId().equals(find_number)) {

                                    SearchItem searchItem = new SearchItem();
                                    searchItem.setName(document.getData().get("이름").toString());
                                    searchItem.setNumber(document.getId());
                                    searchItems.add(searchItem);
                                    break;
                                }
                            }
                            firestoreCallback.onCallback(searchItems);


                        } else {
                            Log.w("Bad", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private interface FirestoreCallback {
        void onCallback(ArrayList<SearchItem> searchItems);
    }
}

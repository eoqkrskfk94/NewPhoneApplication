package com.mj.newphoneapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mj.newphoneapplication.Items.PhoneParentItem;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ParentItemAdapter extends RecyclerView.Adapter<ParentItemAdapter.ItemViewHolder> {

    private Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<PhoneParentItem> phoneParentItems;

    public ParentItemAdapter(Activity activity, ArrayList<PhoneParentItem> phoneParentItems){
        this.phoneParentItems = phoneParentItems;
    }


    @NonNull
    @Override
    public ParentItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_items_log, viewGroup, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ParentItemAdapter.ItemViewHolder itemViewHolder, int position) {
        PhoneParentItem phoneParentItem = phoneParentItems.get(position);
        itemViewHolder.ItemDateTitle.setText(phoneParentItem.getItemDate());

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(itemViewHolder.rvSubItem.getContext());
        //layoutManager.setInitialPrefetchItemCount(phoneParentItem.getPhoneSubItems().size());

        // Create sub item view adapter
//

        //itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return phoneParentItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView ItemDateTitle;
        private RecyclerView rvSubItem;

        ItemViewHolder(View view) {
            super(view);
            ItemDateTitle = view.findViewById(R.id.headerView);
            rvSubItem = view.findViewById(R.id.subRecyclerView);

        }
    }
}

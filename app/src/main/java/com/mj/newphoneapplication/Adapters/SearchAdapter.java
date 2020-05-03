package com.mj.newphoneapplication.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mj.newphoneapplication.Items.SearchItem;
import com.mj.newphoneapplication.R;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SearchItem> searchItems;

    public SearchAdapter(Activity activity, ArrayList<SearchItem> searchItems){
        this.searchItems = searchItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_items_log, viewGroup, false);
        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position){
        final SearchItem searchItem = searchItems.get(position);
        SearchItemViewHolder searchItemViewHolder = (SearchItemViewHolder)viewHolder;
        searchItemViewHolder.name.setText(searchItem.getName());
        searchItemViewHolder.number.setText(searchItem.getNumber());
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public static class SearchItemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView name;
        TextView number;

        SearchItemViewHolder(View itemView){
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.nameView);
            number = itemView.findViewById(R.id.numberView);
        }
    }
}

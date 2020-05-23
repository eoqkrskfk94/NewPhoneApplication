package com.mj.newphoneapplication.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mj.newphoneapplication.items.SearchItem;
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
        searchItemViewHolder.number.setText(phone(searchItem.getNumber())) ;
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

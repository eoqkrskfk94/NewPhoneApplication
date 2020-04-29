package com.mj.newphoneapplication.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.newphoneapplication.Items.PhoneSubItem;
import com.mj.newphoneapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {




    private static int TYPE_DATE = 1;
    private static int TYPE_LOG = 2;

    private ArrayList<PhoneSubItem> phoneSubItems;
    private AdapterView.OnItemClickListener listener;

    public SubItemAdapter(Activity activity, ArrayList<PhoneSubItem> phoneSubItems) {
        this.phoneSubItems = phoneSubItems;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_items_log, viewGroup, false);
        return new SubItemViewHolder(view);
    }

//    @Override
//    public int getItemViewType(int position) {
//
//
//
//        if () {
//            return TYPE_DATE;
//        } else {
//            return TYPE_LOG;
//        }
//    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        PhoneSubItem phoneSubItem = phoneSubItems.get(i);
        subItemViewHolder.name.setText(phoneSubItem.getName());
        if(phoneSubItem.getName() == null){
            subItemViewHolder.name.setText("등록되지 않은 번호");
            subItemViewHolder.name.setTextColor(Color.parseColor("#9C9C9C"));
        }

        subItemViewHolder.number.setText(phoneSubItem.getNumber());
        subItemViewHolder.date.setText(phoneSubItem.getDate());
        if(phoneSubItem.getType() != null){
            if(phoneSubItem.getType().equals("OUTGOING")){
                subItemViewHolder.callType.setImageResource(R.drawable.outcoming_call);
                subItemViewHolder.date.setTextColor(Color.parseColor("#9C9C9C"));
            }
            else if(phoneSubItem.getType().equals("INCOMING")){
                subItemViewHolder.callType.setImageResource(R.drawable.incoming_call);
                subItemViewHolder.date.setTextColor(Color.parseColor("#9C9C9C"));
            }

            else if(phoneSubItem.getType().equals("MISSED")){
                subItemViewHolder.callType.setImageResource(R.drawable.cancel_call);
                subItemViewHolder.date.setTextColor(Color.RED);
            }
        }

        //subItemViewHolder.callType.setText(phoneSubItem.getType());

    }

    @Override
    public int getItemCount() {
        return phoneSubItems.size();
    }

    public static class SubItemViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView number;
        TextView date;
        ImageView callType;


        SubItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameView);
            number = itemView.findViewById(R.id.numberView);
            date = itemView.findViewById(R.id.dateView);
            callType = itemView.findViewById(R.id.callTypeView);
        }



    }

    public static class TimeViewHolder extends  RecyclerView.ViewHolder {
        TextView timeItemView;

        public TimeViewHolder(View itemView){
            super(itemView);
            timeItemView = itemView.findViewById(R.id.timeItemView);
        }
    }
}
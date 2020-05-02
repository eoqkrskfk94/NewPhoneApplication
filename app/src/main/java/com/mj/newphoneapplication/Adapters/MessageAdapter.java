package com.mj.newphoneapplication.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mj.newphoneapplication.Items.MessageItem;
import com.mj.newphoneapplication.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_DATE = 1;
    private static int TYPE_LOG = 2;

    private ArrayList<MessageItem> messageItems;

    public MessageAdapter(Activity activity, ArrayList<MessageItem> messageItems){
        this.messageItems = messageItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if(viewType == TYPE_DATE){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_title, viewGroup, false);
            return new TimeViewHolder(view);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_items_log, viewGroup, false);
            return new MessageItemViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messageItems.get(position).getDiff_date() != -1) {
            return TYPE_DATE;
        } else {
            return TYPE_LOG;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MessageItem messageItem = messageItems.get(i);
        if(viewHolder instanceof MessageItemViewHolder){
            MessageItemViewHolder messageItemViewHolder = (MessageItemViewHolder)viewHolder;
            if(messageItem.getAddress().equals("#CMAS#Severe"))  messageItemViewHolder.number.setText("안전 안내 문자");
            else messageItemViewHolder.number.setText(phone(messageItem.getAddress()));
            messageItemViewHolder.date.setText(messageItem.getTime());
            messageItemViewHolder.message.setText(messageItem.getMsg().replaceAll("\\n"," "));
        }
        else{
            TimeViewHolder timeViewHolder = (TimeViewHolder) viewHolder;
            if(messageItem.getDiff_date() == 0)
                timeViewHolder.date.setText("오늘");
            else if(messageItem.getDiff_date() == 1)
                timeViewHolder.date.setText("어제");
            else
                timeViewHolder.date.setText(messageItem.getTime());

        }

    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    public static class MessageItemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView name;
        TextView number;
        TextView date;
        TextView message;

        MessageItemViewHolder(View itemView){
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.nameView);
            number = itemView.findViewById(R.id.numberView);
            date = itemView.findViewById(R.id.dateView);
            message = itemView.findViewById(R.id.messageView);
        }

    }

    public static class TimeViewHolder extends  RecyclerView.ViewHolder {
        TextView date;

        public TimeViewHolder(View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.dateView);
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

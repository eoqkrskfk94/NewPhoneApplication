package com.mj.newphoneapplication.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mj.newphoneapplication.Items.PhoneSubItem;
import com.mj.newphoneapplication.activities.PhoneDetailActivity;
import com.mj.newphoneapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static int TYPE_DATE = 1;
    private static int TYPE_LOG = 2;

    private ArrayList<PhoneSubItem> phoneSubItems;
    private AdapterView.OnItemClickListener listener;

    public SubItemAdapter(Activity activity, ArrayList<PhoneSubItem> phoneSubItems) {
        this.phoneSubItems = phoneSubItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_DATE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_title, viewGroup, false);
            return new TimeViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_items_log, viewGroup, false);
            return new SubItemViewHolder(view);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (phoneSubItems.get(position).getNumber() == "") {
            return TYPE_DATE;
        } else {
            return TYPE_LOG;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final PhoneSubItem phoneSubItem = phoneSubItems.get(i);
        if (viewHolder instanceof SubItemViewHolder) {
            SubItemViewHolder subItemViewHolder = (SubItemViewHolder) viewHolder;
            subItemViewHolder.name.setText(phoneSubItem.getName());
            subItemViewHolder.profile.setImageResource(R.drawable.cirle);
            subItemViewHolder.name.setTextColor(Color.parseColor("#000000"));
            if (phoneSubItem.getName() == null) {
                subItemViewHolder.name.setText("등록되지 않은 번호");
                subItemViewHolder.name.setTextColor(Color.parseColor("#9C9C9C"));
                subItemViewHolder.profile.setImageResource(R.drawable.circle_unknown);
                findDatabase(phoneSubItem.getNumber(), subItemViewHolder);
            }

            subItemViewHolder.number.setText(phone(phoneSubItem.getNumber()));
            subItemViewHolder.date.setText(phoneSubItem.getDate());
            if (phoneSubItem.getType() != null) {
                if (phoneSubItem.getType().equals("OUTGOING")) {
                    subItemViewHolder.callType.setImageResource(R.drawable.outcoming_call);
                    subItemViewHolder.date.setTextColor(Color.parseColor("#9C9C9C"));
                } else if (phoneSubItem.getType().equals("INCOMING")) {
                    subItemViewHolder.callType.setImageResource(R.drawable.incoming_call);
                    subItemViewHolder.date.setTextColor(Color.parseColor("#9C9C9C"));
                } else if (phoneSubItem.getType().equals("MISSED")) {
                    subItemViewHolder.callType.setImageResource(R.drawable.cancel_call);
                    subItemViewHolder.date.setTextColor(Color.RED);
                } else if (phoneSubItem.getType().equals("REJECTED")) {
                    subItemViewHolder.callType.setImageResource(R.drawable.rejected);
                    subItemViewHolder.date.setTextColor(Color.parseColor("#9C9C9C"));
                }
            }

            subItemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PhoneDetailActivity.class);
                    intent.putExtra("name", phoneSubItem.getName());
                    intent.putExtra("number", phoneSubItem.getNumber());
                    view.getContext().startActivity(intent);

                }
            });

        } else {
            TimeViewHolder timeViewHolder = (TimeViewHolder) viewHolder;
            if (phoneSubItem.getDiff_date() == 0)
                timeViewHolder.date.setText("오늘");
            else if (phoneSubItem.getDiff_date() == 1)
                timeViewHolder.date.setText("어제");
            else
                timeViewHolder.date.setText(phoneSubItem.getDate());
        }


    }

    @Override
    public int getItemCount() {
        return phoneSubItems.size();
    }

    public static class SubItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView name;
        TextView number;
        TextView date;
        ImageView callType;
        ImageView profile;


        SubItemViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.nameView);
            number = itemView.findViewById(R.id.numberView);
            date = itemView.findViewById(R.id.dateView);
            callType = itemView.findViewById(R.id.callTypeView);
            profile = itemView.findViewById(R.id.profileView);

        }


    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        public TimeViewHolder(View itemView) {
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

    public void findDatabase(final String find_number, final SubItemViewHolder subItemViewHolder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("entities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document.getId().equals(find_number)) {
                                    subItemViewHolder.name.setText(document.getData().get("이름").toString());
                                    subItemViewHolder.profile.setImageResource(R.drawable.circle_danger);
                                    subItemViewHolder.name.setTextColor(Color.RED);
                                    break;
                                }
                            }


                        } else {
                            Log.w("Bad", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
package com.example.seatreservation.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.seatreservation.Chat.User;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder>{
    private Activity context = null;
    private ArrayList<User> userList = null;
    public ArrayList<User> selectedValues = new ArrayList<>();

    private OnListItemSelectedInterface mListener;

    SessionManager sessionManager;

    public ChatUserAdapter(Activity context, ArrayList<User> userList, OnListItemSelectedInterface mListener) {
        this.userList = userList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        ChatUserAdapter.ViewHolder holder = new ChatUserAdapter.ViewHolder(view) ;

        return holder;
    }

    public void setItems(ArrayList<User> list){
        userList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        sessionManager = new SessionManager(context);

        Boolean check = userList.get(position).getSelected();
        final User al = userList.get(position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    userList.get(position).setSelected(true);
                    selectedValues.add(al);
                } else {
                    userList.get(position).setSelected(false);
                    selectedValues.remove(al);
                }
            }
        });

        if(check == true){
            holder.checkBox.setChecked(true);
        }if(check == false){
            holder.checkBox.setChecked(false);
        }

        holder.tvUser.setText(userList.get(position).getUserName());
        String profile = userList.get(position).getProfileImg();
        if(profile.equals("basic_image")){
            Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(holder.profile_image);
        }else {
            Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return (null != userList ? userList.size() : 0);
    }

    public ArrayList<User> listOfSelectedItem(){
        return selectedValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout user_item;
        protected TextView tvUser;
        protected ImageView profile_image;
        protected CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.user_item = itemView.findViewById(R.id.user_item);
            this.tvUser = itemView.findViewById(R.id.user_tv);//아이템에 들어갈 작성자
            this.profile_image = itemView.findViewById(R.id.profile_img);//아이템에 들어갈 썸네일 이미지
            this.checkBox = itemView.findViewById(R.id.checkBox);

        }
    }
}

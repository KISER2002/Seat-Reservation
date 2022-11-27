package com.example.seatreservation.Adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.example.seatreservation.Chat.ChatActivity;
import com.example.seatreservation.Chat.ChatRoom;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private Activity context = null;
    private ArrayList<ChatRoom> chatRoomList = null;

    private OnListItemSelectedInterface mListener;

    public ChatRoomAdapter(Activity context, ArrayList<ChatRoom> chatRoomList, OnListItemSelectedInterface mListener) {
        this.chatRoomList = chatRoomList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_item, parent, false);
        ChatRoomAdapter.ViewHolder holder = new ChatRoomAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ViewHolder holder, int position) {

        String idx = chatRoomList.get(position).getIdx();
        String title = chatRoomList.get(position).getTitle();

        holder.tvTitle.setText(chatRoomList.get(position).getTitle());
        String profile = chatRoomList.get(position).getRoomImg();
        if(profile.equals("basic_image")){
            Glide.with(context).load(R.drawable.null_room).override(50, 50).into(holder.room_image);
        }else {
            Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(holder.room_image);
        }

        holder.user_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chat_room_idx", idx);
                intent.putExtra("chat_room_name", title);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout user_item;
        protected TextView tvTitle;
        protected ImageView room_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.user_item = itemView.findViewById(R.id.user_item);
            this.tvTitle = itemView.findViewById(R.id.user_tv);//아이템에 들어갈 작성자
            this.room_image = itemView.findViewById(R.id.profile_img);//아이템에 들어갈 썸네일 이미지

        }
    }
}

package com.example.seatreservation.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.seatreservation.Board.BoardView;
import com.example.seatreservation.Chat.Message;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.SessionManager;
import com.example.seatreservation.ViewType.CommentViewType;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    private Activity context;
    private ArrayList<Message> messageList = new ArrayList<>();

    private OnListItemSelectedInterface mListener;

    SessionManager sessionManager;

    public MessageAdapter(Activity context, ArrayList<Message> messageList, OnListItemSelectedInterface mListener) {
        this.messageList = messageList;
        this.context = context;
        this.mListener = mListener;
    }

    public void addItem(Message item) {
        messageList.add(item);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_image, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_image_other, parent, false);
            return new ReceivedImageHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        sessionManager = new SessionManager(context);

        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case  VIEW_TYPE_IMAGE_SENT:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                ((ReceivedImageHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return (null != messageList ? messageList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        if (message.getUserId().equals(mId) && message.getType().equals("message")) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } if(message.getUserId().equals(mId) && message.getType().equals("image")){
            return VIEW_TYPE_IMAGE_SENT;
        } if(!message.getUserId().equals(mId) && message.getType().equals("message")){
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } if(!message.getUserId().equals(mId) && message.getType().equals("image")){
            // If some other user sent the message
            return VIEW_TYPE_IMAGE_RECEIVED;
        }
        return position;
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder { // 보내는 메세지
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_gchat_message_me);
            timeText =  itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getCreatedAt());
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder { // 보내는 이미지 메세지
        ImageView imageView;
        TextView timeText;

        SentImageHolder(View itemView) {
            super(itemView);

            imageView =  itemView.findViewById(R.id.image_gchat_message_me);
            timeText =  itemView.findViewById(R.id.image_gchat_timestamp_me);
        }

        void bind(Message message) {
            Glide.with(context).load("http://3.34.45.193" + message.getMessage()).override(500, 500).into(imageView);
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getCreatedAt());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder { // 받는 메세지
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_gchat_message_other);
            timeText =  itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText =  itemView.findViewById(R.id.text_gchat_user_other);
            profileImage =  itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getCreatedAt());
            nameText.setText(message.getUserName());
            if(message.getUserProfile().equals("basic_image")){
                Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(profileImage);
            }else {
                Glide.with(context).load("http://3.34.45.193" + message.getUserProfile()).override(50, 50).into(profileImage);
            }
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder { // 받는 이미지 메세지
        ImageView imageView;
        TextView timeText, nameText;
        ImageView profileImage;

        ReceivedImageHolder(View itemView) {
            super(itemView);

            imageView =  itemView.findViewById(R.id.image_gchat_message_other);
            timeText =  itemView.findViewById(R.id.image_gchat_timestamp_other);
            nameText =  itemView.findViewById(R.id.image_gchat_user_other);
            profileImage =  itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message) {
            Glide.with(context).load("http://3.34.45.193" + message.getMessage()).override(500, 500).into(imageView);
            timeText.setText(message.getCreatedAt());
            nameText.setText(message.getUserName());
            if(message.getUserProfile().equals("basic_image")){
                Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(profileImage);
            }else {
                Glide.with(context).load("http://3.34.45.193" + message.getUserProfile()).override(50, 50).into(profileImage);
            }
        }
    }
}

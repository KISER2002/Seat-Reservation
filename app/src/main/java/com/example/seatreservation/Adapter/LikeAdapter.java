package com.example.seatreservation.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.Board.Board;
import com.example.seatreservation.Board.BoardEdit;
import com.example.seatreservation.Board.BoardView;
import com.example.seatreservation.Board.Comment;
import com.example.seatreservation.Board.CommentEdit;
import com.example.seatreservation.Board.CommentList;
import com.example.seatreservation.Board.Like;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardDeleteRequest;
import com.example.seatreservation.Request.CommentDeleteRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder>{

    private Activity context = null;
    private ArrayList<Like> likeList = null;

    private OnListItemSelectedInterface mListener;

    SessionManager sessionManager;

    public LikeAdapter(Activity context, ArrayList<Like> likeList, OnListItemSelectedInterface mListener) {
        this.likeList = likeList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_board_like_item, parent, false);
        LikeAdapter.ViewHolder holder = new LikeAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        sessionManager = new SessionManager(context);

        holder.tvWriter.setText(likeList.get(position).getUserName());
        holder.tvDate.setText(likeList.get(position).getDate());
        String profile = likeList.get(position).getProfileImg();
        if(profile.equals("basic_image")){
            Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(holder.writer_profile_image);
        }else {
            Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(holder.writer_profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return (null != likeList ? likeList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvWriter, tvDate;
        protected ImageView writer_profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvWriter = itemView.findViewById(R.id.like_user_tv);//아이템에 들어갈 작성자
            this.tvDate = itemView.findViewById(R.id.like_date_tv);//아이템에 들어갈 날짜
            this.writer_profile_image = itemView.findViewById(R.id.like_profile_img);//아이템에 들어갈 썸네일 이미지

        }
    }
}

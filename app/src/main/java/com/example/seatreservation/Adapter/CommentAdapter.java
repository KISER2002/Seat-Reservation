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
import com.example.seatreservation.Board.CommentDeleteDialog;
import com.example.seatreservation.Board.CommentEdit;
import com.example.seatreservation.Board.CommentList;
import com.example.seatreservation.Board.ReplyDeleteDialog;
import com.example.seatreservation.Board.ReplyList;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardDeleteRequest;
import com.example.seatreservation.Request.CommentDeleteRequest;
import com.example.seatreservation.SessionManager;
import com.example.seatreservation.ViewType.CommentViewType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity context = null;
    private ArrayList<Comment> commentList = null;
    private Integer hit;

    private OnListItemSelectedInterface mListener;

    SessionManager sessionManager;

    public CommentAdapter(Activity context, ArrayList<Comment> commentList, OnListItemSelectedInterface mListener) {
        this.commentList = commentList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_board_comment_item, parent, false);
//        CommentAdapter.ViewHolder holder = new CommentAdapter.ViewHolder(view) ;
//
//        return holder;

        View view;
        Context mCtx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == CommentViewType.COMMENT_ITEM)
        {
            view = inflater.inflate(R.layout.frag_board_comment_item, parent, false);
            return new CommentViewHolder(view);
        }
        else if(viewType == CommentViewType.REPLY_ITEM)
        {
            view = inflater.inflate(R.layout.frag_board_reply_item, parent, false);
            return new ReplyViewHolder(view);
        }
        else{
            view = inflater.inflate(R.layout.frag_board_deleted_item, parent, false);
            return new DeletedViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mProfileImg = user.get(sessionManager.PROFILE);

        String idx = commentList.get(position).getIdx();
        String bno = commentList.get(position).getBno();
        String comment = commentList.get(position).getComment();
        String writerId = commentList.get(position).getWriter();
        String writer = commentList.get(position).getWriterName();
        String profile = commentList.get(position).getProfileImg();
        String date = commentList.get(position).getDate();
        String parent = commentList.get(position).getParent();

        if(holder instanceof CommentViewHolder){
            ((CommentViewHolder) holder).tvWriter.setText(commentList.get(position).getWriterName());
            ((CommentViewHolder) holder).tvComment.setText(commentList.get(position).getComment());
            ((CommentViewHolder) holder).tvDate.setText(commentList.get(position).getDate());

            if(profile.equals("basic_image")){
                Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(((CommentViewHolder) holder).writer_profile_image);
            }else {
                Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(((CommentViewHolder) holder).writer_profile_image);
            }

            if(!writerId.equals(mId)){
                ((CommentViewHolder) holder).menu_btn.setVisibility(View.INVISIBLE);
            }
            else {
                ((CommentViewHolder) holder).menu_btn.setVisibility(View.VISIBLE);
            }

            ((CommentViewHolder) holder).reply_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ReplyList.class);
                    i.putExtra("idx", idx);
                    i.putExtra("bno", bno);
                    i.putExtra("comment", comment);
                    i.putExtra("writerId", writerId);
                    i.putExtra("writer", writer);
                    i.putExtra("date", date);
                    i.putExtra("profileImg", profile);
                    context.startActivity(i);
                }
            });

            ((CommentViewHolder) holder).menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.action_menu1){
                                Intent i = new Intent(context, CommentEdit.class);
                                i.putExtra("idx", idx);
                                i.putExtra("bno", bno);
                                i.putExtra("comment", comment);
                                context.startActivity(i);
                            }else if (menuItem.getItemId() == R.id.action_menu2){
                                Intent i = new Intent(context, CommentDeleteDialog.class);
                                i.putExtra("idx", idx);
                                context.startActivity(i);
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        } else if(holder instanceof ReplyViewHolder){
            ((ReplyViewHolder) holder).tvWriter.setText(commentList.get(position).getWriterName());
            ((ReplyViewHolder) holder).tvComment.setText(commentList.get(position).getComment());
            ((ReplyViewHolder) holder).tvDate.setText(commentList.get(position).getDate());

            if(profile.equals("basic_image")){
                Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(((ReplyViewHolder) holder).writer_profile_image);
            }else {
                Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(((ReplyViewHolder) holder).writer_profile_image);
            }
            if(!writerId.equals(mId)){
                ((ReplyViewHolder) holder).menu_btn.setVisibility(View.INVISIBLE);
            }
            else {
                ((ReplyViewHolder) holder).menu_btn.setVisibility(View.VISIBLE);
            }

            ((ReplyViewHolder) holder).menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.action_menu1){
                                Intent i = new Intent(context, CommentEdit.class);
                                i.putExtra("idx", idx);
                                i.putExtra("bno", bno);
                                i.putExtra("comment", comment);
                                context.startActivity(i);
                            }else if (menuItem.getItemId() == R.id.action_menu2){
                                Intent i = new Intent(context, ReplyDeleteDialog.class);
                                i.putExtra("idx", idx);
                                i.putExtra("parent", parent);
                                context.startActivity(i);
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        else if(holder instanceof DeletedViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return (null != commentList ? commentList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position).getViewType();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvWriter, tvDate, tvComment, reply_btn;
        protected ImageView writer_profile_image, menu_btn;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvWriter = itemView.findViewById(R.id.comment_writer_tv);//아이템에 들어갈 작성자
            this.tvDate = itemView.findViewById(R.id.comment_date_tv);//아이템에 들어갈 날짜
            this.tvComment = itemView.findViewById(R.id.comment_content_tv);
            this.writer_profile_image = itemView.findViewById(R.id.comment_profile_img);//아이템에 들어갈 썸네일 이미지
            this.menu_btn = itemView.findViewById(R.id.comment_menu_btn);
            this.reply_btn = itemView.findViewById(R.id.comment_reply_btn);

        }
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvWriter, tvDate, tvComment, reply_btn;
        protected ImageView writer_profile_image, menu_btn;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvWriter = itemView.findViewById(R.id.reply_writer_tv);//아이템에 들어갈 작성자
            this.tvDate = itemView.findViewById(R.id.reply_date_tv);//아이템에 들어갈 날짜
            this.tvComment = itemView.findViewById(R.id.reply_content_tv);
            this.writer_profile_image = itemView.findViewById(R.id.reply_profile_img);//아이템에 들어갈 썸네일 이미지
            this.menu_btn = itemView.findViewById(R.id.reply_menu_btn);

        }
    }

    public class DeletedViewHolder extends RecyclerView.ViewHolder {

        public DeletedViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}

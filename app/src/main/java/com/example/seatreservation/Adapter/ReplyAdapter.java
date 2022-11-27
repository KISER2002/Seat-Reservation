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
import com.example.seatreservation.Board.Reply;
import com.example.seatreservation.Board.ReplyEdit;
import com.example.seatreservation.Board.ReplyList;
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

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder>{

    private Context mCtx;
    private Activity context = null;
    private ArrayList<Reply> replyList = null;
    private Integer hit;

    private OnListItemSelectedInterface mListener;

    SessionManager sessionManager;

    public ReplyAdapter(Activity context, ArrayList<Reply> replyList, OnListItemSelectedInterface mListener) {
        this.replyList = replyList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_board_reply_item, parent, false);
        ReplyAdapter.ViewHolder holder = new ReplyAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mProfileImg = user.get(sessionManager.PROFILE);

        String idx = replyList.get(position).getIdx();
        String bno = replyList.get(position).getBno();
        String reply = replyList.get(position).getReply();
        String writer = replyList.get(position).getWriter();
        String date = replyList.get(position).getDate();

        holder.tvWriter.setText(replyList.get(position).getWriterName());
        holder.tvComment.setText(replyList.get(position).getReply());
        holder.tvDate.setText(replyList.get(position).getDate());
        String profile = replyList.get(position).getProfileImg();
        if(profile.equals("basic_image")){
            Glide.with(context).load(R.drawable.profile_img).override(50, 50).into(holder.writer_profile_image);
        }else {
            Glide.with(context).load("http://3.34.45.193" + profile).override(50, 50).into(holder.writer_profile_image);
        }
        if(writer.equals(mId)){

        }
        else {
            holder.menu_btn.setVisibility(View.GONE);
        }

        holder.menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1){
                            Intent i = new Intent(context, ReplyEdit.class);
                            i.putExtra("idx", idx);
                            i.putExtra("bno", bno);
                            i.putExtra("reply", reply);
                            context.startActivity(i);
                        }else if (menuItem.getItemId() == R.id.action_menu2){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("삭제").setMessage("댓글을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                        boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                        if (success) {//정보수정에 성공한 경우
                                                            Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    context.runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();
                                                            replyList.remove(position);
                                                            notifyDataSetChanged();
                                                        }
                                                        else{//정보수정에 실패한 경우
                                                            Toast.makeText(context,"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            CommentDeleteRequest replyDeleteRequest = new CommentDeleteRequest(idx, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(context);
                                            queue.add(replyDeleteRequest);
                                        }
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != replyList ? replyList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvWriter, tvDate, tvComment, reply_btn;
        protected ImageView writer_profile_image, menu_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvWriter = itemView.findViewById(R.id.reply_writer_tv);//아이템에 들어갈 작성자
            this.tvDate = itemView.findViewById(R.id.reply_date_tv);//아이템에 들어갈 날짜
            this.tvComment = itemView.findViewById(R.id.reply_content_tv);
            this.writer_profile_image = itemView.findViewById(R.id.reply_profile_img);//아이템에 들어갈 썸네일 이미지
            this.menu_btn = itemView.findViewById(R.id.reply_menu_btn);

        }
    }
}

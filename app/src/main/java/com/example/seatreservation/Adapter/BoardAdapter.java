package com.example.seatreservation.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.seatreservation.Board.CommentList;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardEditRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {

    private Activity context = null;
    private ArrayList<Board> boardList = null;
    private Integer hit;
    private String idx, title, content, writer,writerName, profileImg, date, image;

    private OnListItemSelectedInterface mListener;

    public BoardAdapter(Activity context, ArrayList<Board> boardList, OnListItemSelectedInterface mListener) {
        this.boardList = boardList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_board_item, parent, false);
        BoardAdapter.ViewHolder holder = new BoardAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        idx = boardList.get(position).getIdx();
//        title = boardList.get(position).getTitle();
//        content = boardList.get(position).getContent();
//        writer = boardList.get(position).getWriter();
//        writerName = boardList.get(position).getWriterName();
//        profileImg = boardList.get(position).getProfileImg();
//        date = boardList.get(position).getDate();
//        hit = boardList.get(position).getHit();
//        image = boardList.get(position).getPhoto();


        holder.tvTitle.setText(boardList.get(position).getTitle());
        holder.tvWriter.setText(boardList.get(position).getWriterName());
        holder.tvDate.setText(boardList.get(position).getDate());
        holder.tvHit.setText(String.valueOf(boardList.get(position).getHit()));
        holder.tvComment.setText(boardList.get(position).getComment());
        String empty = boardList.get(position).getPhoto();
        if(empty.equals("empty")){
            holder.tvImage.setVisibility(View.GONE);
        } else {
            Glide.with(context).load("http://3.34.45.193" + boardList.get(position).getPhoto()).override(50, 50).into(holder.tvImage);
        }
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, tvWriter, tvDate, tvHit, tvComment;
        protected ImageView tvImage;
        protected LinearLayout commentBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvTitle = itemView.findViewById(R.id.title_item);//아이템에 들어갈 제목
            this.tvWriter = itemView.findViewById(R.id.writer_item);//아이템에 들어갈 작성자
            this.tvDate = itemView.findViewById(R.id.date_item);//아이템에 들어갈 날짜
            this.tvHit = itemView.findViewById(R.id.hit_item);//아이템에 들어갈 조회 수
            this.tvImage = itemView.findViewById(R.id.image_item);//아이템에 들어갈 썸네일 이미지
            this.tvComment = itemView.findViewById(R.id.comment_item);
            this.commentBtn = itemView.findViewById(R.id.commentBtn);

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    mListener.OnItemSelected(itemView, getAdapterPosition());

                    idx = boardList.get(pos).getIdx();

                    if(pos != RecyclerView.NO_POSITION) {
                        Intent i = new Intent(context, CommentList.class);
                        i.putExtra("idx", boardList.get(pos).getIdx());
                        i.putExtra("title", boardList.get(pos).getTitle());
                        context.startActivity(i);
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    mListener.OnItemSelected(itemView, getAdapterPosition());

                    idx = boardList.get(pos).getIdx();

                    if(pos != RecyclerView.NO_POSITION) {
                        Intent i = new Intent(context, BoardView.class);
                        i.putExtra("idx", boardList.get(pos).getIdx());
                        i.putExtra("hit", boardList.get(pos).getHit());
                        context.startActivity(i);
                    }

                }
            });
        }
    }
}

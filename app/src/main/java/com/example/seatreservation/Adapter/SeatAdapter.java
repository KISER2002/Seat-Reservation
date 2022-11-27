package com.example.seatreservation.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Seat.OfficeView;
import com.example.seatreservation.Seat.Seat;
import com.example.seatreservation.Seat.SeatEdit;
import com.example.seatreservation.Seat.Office2;

import java.util.ArrayList;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
    private Activity context = null;
    private ArrayList<Seat> seatList = null;

    private OnListItemSelectedInterface mListener;

    public SeatAdapter(Activity context, ArrayList<Seat> seatList, OnListItemSelectedInterface mListener) {
        this.seatList = seatList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public SeatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_seat_list_item, parent, false);
        SeatAdapter.ViewHolder holder = new SeatAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SeatAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String name = seatList.get(position).getName();
        String seat_count = seatList.get(position).getSeat_count();
        String in_use = seatList.get(position).getIn_use();
        String empty_seat = seatList.get(position).getEmpty_seat();

        holder.officeName.setText(name);
        holder.seat_in_use.setText(in_use + "/" + seat_count);
        holder.seat_empty_seat.setText(empty_seat + "/" + seat_count);

    }

    @Override
    public int getItemCount() {
        return (null != seatList ? seatList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView officeName, seat_in_use, seat_empty_seat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.officeName = itemView.findViewById(R.id.seat_list_office_name);//아이템에 들어갈 작성자
            this.seat_in_use = itemView.findViewById(R.id.seat_list_in_use);//아이템에 들어갈 날짜
            this.seat_empty_seat = itemView.findViewById(R.id.seat_list_empty_seat);//아이템에 들어갈 썸네일 이미지

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    mListener.OnItemSelected(itemView, getAdapterPosition());

                    if(pos != RecyclerView.NO_POSITION) {
                        if(seatList.get(pos).getIs_office().equals("yes")) {
                            Intent i = new Intent(context, OfficeView.class);
                            i.putExtra("id", seatList.get(pos).getId());
                            i.putExtra("office_name", seatList.get(pos).getName());
                            i.putExtra("seatId", "null");
                            context.startActivity(i);
                        } else {
                            Intent i = new Intent(context, SeatEdit.class);
                            i.putExtra("id", seatList.get(pos).getId());
                            i.putExtra("office_name", seatList.get(pos).getName());
                            context.startActivity(i);
                        }
                    }

                }
            });

        }
    }
}

package com.example.seatreservation.Seat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.seatreservation.R;

public class SeatStatusDialog extends Activity {

    TextView seatStatus, seatName, seatUserName, endTime;
    String getId, getName, getEnd_time, getUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frag_seat_status_dialog);

        seatStatus = findViewById(R.id.seat_status);
        seatName = findViewById(R.id.seat_info_name);
        seatUserName = findViewById(R.id.seat_user_name);
        endTime = findViewById(R.id.seat_end_time);

        Intent intent = getIntent();
        getId = intent.getStringExtra("id");
        getName = intent.getStringExtra("name");
        getEnd_time = intent.getStringExtra("end_time");
        getUserName = intent.getStringExtra("user");

        if(getUserName.equals("null")){
            seatName.setText(getName + "번 좌석");
        } else {
            seatStatus.setText("사용중");
            seatName.setText(getName + "번 좌석");
            seatUserName.setText(getUserName + " 님");
            endTime.setText(getEnd_time + " 종료");
        }
    }

}

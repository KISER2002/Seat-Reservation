package com.example.seatreservation.Seat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Board.BoardEdit;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardEditRequest;
import com.example.seatreservation.Request.SeatEventCreateRequest;
import com.example.seatreservation.Request.SeatEventUpdateRequest;
import com.example.seatreservation.Request.SeatExtensionRequest;
import com.example.seatreservation.Request.SeatWriteRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SeatExtension extends AppCompatActivity {

    TextView userNameTv, seatNameTv, endTimeTv, newEndTimeTv;
    Button locationBtn, confirmBtn;
    SessionManager sessionManager;

    private String seatIdSt, seatNameSt, userNameSt, endTimeSt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_extension);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String profileImg = user.get(sessionManager.PROFILE);

        userNameTv = findViewById(R.id.seat_user_name);
        seatNameTv = findViewById(R.id.seat_info_name);
        endTimeTv = findViewById(R.id.seat_end_time);
        newEndTimeTv = findViewById(R.id.seat_new_end_time);
        locationBtn = findViewById(R.id.seat_location_btn);
        confirmBtn = findViewById(R.id.seat_confirm_btn);

        Intent intent = getIntent();
        seatIdSt = intent.getStringExtra("id");
        seatNameSt = intent.getStringExtra("name");
        userNameSt = intent.getStringExtra("userName");
        endTimeSt = intent.getStringExtra("endTime");

        userNameTv.setText(userNameSt);
        seatNameTv.setText(seatNameSt);
        endTimeTv.setText(endTimeSt);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        cal.add(Calendar.HOUR, 6);
        String endTime = simpleDate.format(cal.getTime());

        newEndTimeTv.setText(endTime);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Board php에 response
                            boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                            if (success) {//게시글 작성에 성공한 경우
                                Toast.makeText(getApplicationContext(), "예약 연장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();
                            } else {//글 작성에 실패한 경우
                                Toast.makeText(getApplicationContext(), "연장에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                SeatExtensionRequest seatExtensionRequest = new SeatExtensionRequest(seatIdSt, endTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SeatExtension.this);
                queue.add(seatExtensionRequest);

                Response.Listener<String> responseListener2 = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Board php에 response
                            boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                            if (success) {//게시글 작성에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();
                            } else {//글 작성에 실패한 경우
                                Toast.makeText(getApplicationContext(), "예약에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                SeatEventUpdateRequest seatEventUpdateRequest = new SeatEventUpdateRequest(seatIdSt, mId, responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(SeatExtension.this);
                queue2.add(seatEventUpdateRequest);
            }
        });

    }
}

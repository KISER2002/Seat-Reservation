package com.example.seatreservation.Seat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.SeatEventCreateRequest;
import com.example.seatreservation.Request.SeatWriteRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SeatWrite extends AppCompatActivity {

    TextView userNameTv, seatNameTv, endTimeTv;
    Button locationBtn, confirmBtn;
    SessionManager sessionManager;

    private String seatIdSt, seatNameSt, userNameSt;
    private String endTime;
    private String in_use = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_write);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String profileImg = user.get(sessionManager.PROFILE);

        userNameTv = findViewById(R.id.seat_user_name);
        seatNameTv = findViewById(R.id.seat_info_name);
        endTimeTv = findViewById(R.id.seat_end_time);
        locationBtn = findViewById(R.id.seat_location_btn);
        confirmBtn = findViewById(R.id.seat_confirm_btn);

        Intent intent = getIntent();
        seatIdSt = intent.getStringExtra("id");
        seatNameSt = intent.getStringExtra("name");
        userNameSt = intent.getStringExtra("userName");

        userNameTv.setText(userNameSt);
        seatNameTv.setText(seatNameSt);

        endTimeTv.setText("6시간");

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeatWrite.this, Office2.class);
                intent.putExtra("seatId", seatIdSt);
                startActivity(intent);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String startTime = simpleDate.format(mDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);

                cal.add(Calendar.HOUR, 6);
                endTime = simpleDate.format(cal.getTime());

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Board php에 response
                            boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                            if (success) {//게시글 작성에 성공한 경우
                                Toast.makeText(getApplicationContext(), "좌석 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                sessionManager.editSession(profileImg, mName, mId, in_use);
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
                SeatWriteRequest seatWriteRequest = new SeatWriteRequest(seatIdSt, mId, startTime, endTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SeatWrite.this);
                queue.add(seatWriteRequest);

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
                SeatEventCreateRequest seatEventCreateRequest = new SeatEventCreateRequest(seatIdSt, mId, responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(SeatWrite.this);
                queue2.add(seatEventCreateRequest);
            }
        });

    }

}

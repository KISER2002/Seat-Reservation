package com.example.seatreservation.Seat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.SeatDeleteRequest;
import com.example.seatreservation.Request.SeatEventDeleteRequest;
import com.example.seatreservation.SessionManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MySeat extends AppCompatActivity {
    private static String URL_LOADING = "http://3.34.45.193/MySeat.php";

    TextView userNameTv, seatNameTv, endTimeTv;
    Button locationBtn, extendBtn, cancelBtn;
    SessionManager sessionManager;

    private String id, name;
    private String profileImg, mName, mId;
    private String in_use = "null";
    private String userNameSt, seatIdSt, seatNameSt, officeNumSt, seatEndTimeSt ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_my_seat);

    }

    @Override
    public void onResume() {
        super.onResume();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        profileImg = user.get(sessionManager.PROFILE);
        mName = user.get(sessionManager.NAME);
        mId = user.get(sessionManager.ID);

        Loading(mId);

    }

    private void Loading(String user1){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("loading");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    userNameSt = object.getString("userName".trim());
                                    seatIdSt = object.getString("id".trim());
                                    seatNameSt = object.getString("name".trim());
                                    officeNumSt = object.getString("office_num".trim());
                                    seatEndTimeSt = object.getString("end_time").trim();

                                    userNameTv = findViewById(R.id.seat_user_name);
                                    seatNameTv = findViewById(R.id.seat_info_name);
                                    endTimeTv = findViewById(R.id.seat_end_time);
                                    locationBtn = findViewById(R.id.seat_location_btn);
                                    extendBtn = findViewById(R.id.seat_extend_btn);
                                    cancelBtn = findViewById(R.id.seat_cancel_btn);

                                    IntentIntegrator qrScan =new IntentIntegrator(MySeat.this);

                                    userNameTv.setText(userNameSt);
                                    seatNameTv.setText(seatNameSt + "??? ?????? ?????????");
                                    endTimeTv.setText(seatEndTimeSt + " ??????");

                                    locationBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MySeat.this, OfficeView.class);
                                            intent.putExtra("office_name", seatNameSt);
                                            intent.putExtra("id", officeNumSt);
                                            intent.putExtra("seatId", seatIdSt);
                                            startActivity(intent);
                                        }
                                    });

                                    extendBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            qrScan.setPrompt("????????? ????????? ?????? ????????? ????????? ???????????????");
                                            qrScan.setCameraId(1);
                                            qrScan.initiateScan();
                                        }
                                    });

                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);// php??? response
                                                        boolean success = jasonObject.getBoolean("success");//Register php??? sucess
                                                        if (success) {//????????? ????????? ??????
                                                            Toast.makeText(MySeat.this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    MySeat.this.runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();
                                                            sessionManager.editSession(profileImg, mName, mId, in_use);
                                                            finish();
                                                        }
                                                        else{
                                                            Toast.makeText(MySeat.this,"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //????????? volley ??? ???????????? ????????? ???
                                            SeatDeleteRequest seatDeleteRequest = new SeatDeleteRequest(seatIdSt, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(MySeat.this);
                                            queue.add(seatDeleteRequest);

                                            Response.Listener<String> responseListener2 = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//Board php??? response
                                                        boolean success = jasonObject.getBoolean("success");//Board php??? sucess
                                                        if (success) {//????????? ????????? ????????? ??????
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();
                                                            finish();
                                                        } else {//??? ????????? ????????? ??????
                                                            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //????????? volley ??? ???????????? ????????? ???
                                            SeatEventDeleteRequest seatEventDeleteRequest = new SeatEventDeleteRequest(seatIdSt, mId, responseListener2);
                                            RequestQueue queue2 = Volley.newRequestQueue(MySeat.this);
                                            queue2.add(seatEventDeleteRequest);
                                        }
                                    });

                                }
                            } else { // ??????
                                Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley ?????? ??????.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user", user1);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        sessionManager = new SessionManager(MySeat.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mId = user.get(sessionManager.ID);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MySeat.this, "?????????????????????.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    //data??? json?????? ??????
                    JSONObject obj = new JSONObject(result.getContents());
                    id = obj.getString("id");
                    name = obj.getString("name");

                    if(seatIdSt.equals(id)){
                        Intent i = new Intent(MySeat.this, SeatExtension.class);
                        i.putExtra("id", id);
                        i.putExtra("name", name);
                        i.putExtra("userName", mName);
                        i.putExtra("endTime", seatEndTimeSt);
                        startActivity(i);
                    } else if(!seatIdSt.equals(id)){
                        new AlertDialog.Builder(MySeat.this)
                                .setTitle("?????? ??????")
                                .setMessage("?????? ????????? ?????? QR ????????? ??????????????????.")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(MySeat.this)
                            .setTitle("?????? ??????")
                            .setMessage("????????? ???????????????.")
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

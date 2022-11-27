package com.example.seatreservation.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.Board.BoardEdit;
import com.example.seatreservation.Board.BoardView;
import com.example.seatreservation.MyPage.UserEdit;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardDeleteRequest;
import com.example.seatreservation.Request.ViewsCheckRequest;
import com.example.seatreservation.Seat.MySeat;
import com.example.seatreservation.Seat.MySeatNull;
import com.example.seatreservation.Seat.SeatList;
import com.example.seatreservation.Seat.SeatWrite;
import com.example.seatreservation.SessionManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fragSeat extends Fragment {
    private static String URL_SEAT = "http://3.34.45.193/SeatValidate.php";
    private static String URL_LOADING = "http://3.34.45.193/SeatLoading.php";

    private View view;
    private String in_use, in_use2;
    Context context;

    private String TAG = "프래그먼트";

    private Button seatStatusBtn, seatReservationBtn, mySeatBtn;

    SessionManager sessionManager;

    String mName, mId, profileImg, mIn_use;
    String id, name;

    private IntentIntegrator qrScan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_seat, container, false);



        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        context = getContext();

        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mId = user.get(sessionManager.ID);
        mIn_use = user.get(sessionManager.IN_USE);
        profileImg = user.get(sessionManager.PROFILE);

        seatStatusBtn = view.findViewById(R.id.seat_status_btn);
        seatReservationBtn = view.findViewById(R.id.seat_reservation_btn);
        mySeatBtn = view.findViewById(R.id.my_seat_btn);

        qrScan = IntentIntegrator.forSupportFragment(fragSeat.this);

        seatStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SeatList.class);
                intent.putExtra("is_office", "yes");
                startActivity(intent);
            }
        });

        seatReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loading(mId);
            }
        });

        mySeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loading2(mId);
            }
        });
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        sessionManager = new SessionManager(context);

        HashMap<String, String> user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mId = user.get(sessionManager.ID);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    id = obj.getString("id");
                    name = obj.getString("name");

                    SeatWrite(id);


                } catch (JSONException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(context)
                            .setTitle("좌석 정보")
                            .setMessage("잘못된 정보입니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

    private void SeatWrite(String id1) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("loading");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    in_use = object.getString("in_use".trim());

                                    if(in_use.equals("0")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("좌석 정보")
                                                .setMessage("\n사용자 : " + mName + "\n\n좌석 ID : " + id + "\n좌석 정보 : " + name)
                                                .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(context, SeatWrite.class);
                                                        i.putExtra("id", id);
                                                        i.putExtra("name", name);
                                                        i.putExtra("userName", mName);
                                                        startActivity(i);
                                                    }
                                                })
                                                .setNegativeButton("취소", null)
                                                .show();
                                    } else if(in_use.equals("1")){
                                        new AlertDialog.Builder(context)
                                                .setTitle("좌석 정보")
                                                .setMessage("이미 사용중인 좌석입니다.")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }

                                }
                            } else { // 실패
                                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Volley 통신 에러.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id1);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void Loading(String id2) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("loading");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    in_use2 = object.getString("in_use".trim());

                                    sessionManager.editSession(profileImg, mName, mId, in_use2);

                                    HashMap<String, String> user = sessionManager.getUserDetail();
                                    String mIn_use2 = user.get(sessionManager.IN_USE);

                                    if(mIn_use2.equals("1")){
                                        Toast.makeText(context, "이미 사용 중인 좌석이 있습니다.", Toast.LENGTH_SHORT).show();
                                    } else if(mIn_use2.equals("null")) {
                                        qrScan.setPrompt("스캔할 코드를 찾아 사각형 영역에 맞춰주세요");

                                        qrScan.setCameraId(1);
                                        qrScan.initiateScan();
                                    }

                                }
                            } else { // 실패
                                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String in_use_null = "null";
                            sessionManager.editSession(profileImg, mName, mId, in_use_null);

                            HashMap<String, String> user = sessionManager.getUserDetail();
                            String mIn_use2 = user.get(sessionManager.IN_USE);

                            if(mIn_use2.equals("1")){
                                Toast.makeText(context, "이미 사용 중인 좌석이 있습니다.", Toast.LENGTH_SHORT).show();
                            } else if(mIn_use2.equals("null")) {
                                qrScan.setPrompt("스캔할 코드를 찾아 사각형 영역에 맞춰주세요");

                                qrScan.setCameraId(1);
                                qrScan.initiateScan();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Volley 통신 에러.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id2);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void Loading2(String id2) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("loading");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    in_use2 = object.getString("in_use".trim());

                                    sessionManager.editSession(profileImg, mName, mId, in_use2);

                                    HashMap<String, String> user = sessionManager.getUserDetail();
                                    String mIn_use2 = user.get(sessionManager.IN_USE);

                                    if(mIn_use2.equals("1")){
                                        Intent intent = new Intent(context, MySeat.class);
                                        startActivity(intent);
                                    }else if(mIn_use2.equals("null")){
                                        Intent intent = new Intent(context, MySeatNull.class);
                                        startActivity(intent);
                                    }

                                }
                            } else { // 실패
                                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String in_use_null = "null";
                            sessionManager.editSession(profileImg, mName, mId, in_use_null);

                            HashMap<String, String> user = sessionManager.getUserDetail();
                            String mIn_use2 = user.get(sessionManager.IN_USE);

                            if(mIn_use2.equals("1")){
                                Intent intent = new Intent(context, MySeat.class);
                                startActivity(intent);
                            }else if(mIn_use2.equals("null")){
                                Intent intent = new Intent(context, MySeatNull.class);
                                startActivity(intent);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Volley 통신 에러.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id2);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
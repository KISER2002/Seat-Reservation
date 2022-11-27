package com.example.seatreservation.Seat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OfficeView extends Activity {
    private static String URL_LOADING = "http://3.34.45.193/LoadSeatLayout.php";
    private static final String PRODUCT_URL = "http://3.34.45.193/SeatLayoutList.php";

    private final int DYNAMIC_VIEW_ID = 0x8000;

    private ConstraintLayout dynamicLayout; // 버튼이 생성될 공간

    TextView officeName;

    private Integer numButton = 0; // 버튼의 개수
    private int position = -1;
    private String officeNameSt,location, seat_location;

    View layout_end, layout_right_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_office);

        Intent intent = getIntent();
        officeNameSt = intent.getStringExtra("office_name");
        location = intent.getStringExtra("id");
        seat_location = intent.getStringExtra("seatId");

        layout_end = findViewById(R.id.layout_end);
        layout_right_end = findViewById(R.id.layout_right_end);

        officeName = findViewById(R.id.office_name);
        dynamicLayout = findViewById(R.id.dynamicArea);

        officeName.setText(officeNameSt);

        Loading(location);

    }

    private void Loading(String idx1){

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
                                    String layoutJsonArray = object.getString("seat_layout".trim());
                                    String layoutWidthNum = object.getString("layout_width".trim());
                                    String layoutHeightNum = object.getString("layout_height".trim());

                                    if(!layoutWidthNum.equals("null")) {
                                        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(layoutWidthNum)/2, getResources().getDisplayMetrics());
                                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_right_end.getLayoutParams();
                                        layoutParams.leftMargin = margin;
                                        layout_right_end.setLayoutParams(layoutParams);
                                    }

                                    if(!layoutHeightNum.equals("null")) {
                                        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(layoutHeightNum)/2, getResources().getDisplayMetrics());
                                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_end.getLayoutParams();
                                        layoutParams.topMargin = margin;
                                        layout_end.setLayoutParams(layoutParams);
                                    }

                                    if(!layoutJsonArray.equals("null")) {

                                        JSONArray jsonArr = new JSONArray(layoutJsonArray);

                                        for (int ii = 0; ii < jsonArr.length(); ii++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(ii);

                                            String idx = jsonObj.getString("idx".trim());
                                            String id = jsonObj.getString("id".trim());
                                            String name = jsonObj.getString("name".trim());
                                            float x = jsonObj.getInt("x".trim());
                                            float y = jsonObj.getInt("y".trim());
                                            int width = jsonObj.getInt("width".trim());
                                            int height = jsonObj.getInt("height".trim());
                                            int angle = jsonObj.getInt("angle".trim());

                                            if (name.equals("seat")) {
                                                numButton++; // 버튼 추가할 때마다 버튼의 개수 1씩 증가
                                                position++;

                                                Button dynamicButton = new Button(OfficeView.this); // 새로운 버튼 생성
                                                dynamicButton.setId(position);
                                                dynamicButton.setText(id); // 버튼의 이름를 버튼의 개수로 표시
                                                dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                dynamicButton.setBackgroundResource(R.drawable.seat_border);
                                                dynamicButton.setX(x);
                                                dynamicButton.setY(y);
                                                dynamicButton.setRotation(angle);

                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    String success = jsonObject.getString("success");
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("loading");

                                                                    if (success.equals("1")) {
                                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                                            JSONObject product = jsonArray.getJSONObject(i);
                                                                            String id = product.getString("id");
                                                                            String name = product.getString("name");
                                                                            String office_num = product.getString("office_num");
                                                                            String seat_num = product.getString("seat_num");
                                                                            String in_use = product.getString("in_use");
                                                                            String start_time = product.getString("start_time");
                                                                            String end_time = product.getString("end_time");
                                                                            String user = product.getString("user");
                                                                            String userName = product.getString("userName");

                                                                            if (in_use.equals("1")) {
                                                                                dynamicButton.setBackgroundResource(R.drawable.seat_border_use);
                                                                            }
                                                                            if (id.equals(seat_location)) {
                                                                                dynamicButton.setBackgroundResource(R.drawable.seat_border_my);
                                                                            }

                                                                            dynamicButton.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    if (in_use.equals("1")) {
                                                                                        Intent i = new Intent(getApplicationContext(), SeatStatusDialog.class);
                                                                                        i.putExtra("id", id);
                                                                                        i.putExtra("name", name);
                                                                                        i.putExtra("end_time", end_time);
                                                                                        i.putExtra("user", userName);
                                                                                        startActivity(i);
                                                                                    } else {
                                                                                        Intent i = new Intent(getApplicationContext(), SeatStatusDialog.class);
                                                                                        i.putExtra("id", id);
                                                                                        i.putExtra("name", name);
                                                                                        i.putExtra("user", userName);
                                                                                        startActivity(i);
                                                                                    }
                                                                                }
                                                                            });

                                                                        }
                                                                    } else { // 실패
                                                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "Volley 통신 에러.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }) {
                                                    @Nullable
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("idx", location);
                                                        params.put("seat_num", id);
                                                        return params;
                                                    }
                                                };

                                                RequestQueue requestQueue = Volley.newRequestQueue(OfficeView.this);
                                                requestQueue.add(stringRequest);

                                                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                                                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(width, height));

                                            } else if(name.equals("deleted")){
                                                if(!id.equals("seat_direction") && !id.equals("item")) {
                                                    numButton++; // 버튼 추가할 때마다 버튼의 개수 1씩 증가
                                                }
                                                position++;

                                            } else{
                                                position++;

                                                Button dynamicButton = new Button(OfficeView.this); // 새로운 버튼 생성
                                                dynamicButton.setId(position);
                                                if(name.equals("custom")){
                                                    dynamicButton.setText(id);
                                                    dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                                                    dynamicButton.setBackgroundResource(R.drawable.seat_border);
                                                } if(name.equals("xbox")){
                                                    dynamicButton.setBackgroundResource(R.drawable.xbox);
                                                } if(name.equals("arrow")){
                                                    dynamicButton.setBackgroundResource(R.drawable.arrow);
                                                } if(name.equals("chair")){
                                                    dynamicButton.setBackgroundResource(R.drawable.seat_left);
                                                }
                                                dynamicButton.setX(x);
                                                dynamicButton.setY(y);
                                                dynamicButton.setRotation(angle);

                                                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                                                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(width, height));
                                            }

                                        }
                                    } else{

                                    }

                                }
                            } else { // 실패
//                                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idx", idx1);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}

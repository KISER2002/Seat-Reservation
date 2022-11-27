package com.example.seatreservation.Seat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Office2 extends AppCompatActivity implements View.OnClickListener {
    private TextView[] seatTv = new TextView[52];

    private static final String PRODUCT_URL = "http://3.34.45.193/SeatLayoutList2.php";

    String seat_location;

    private ArrayList<Seat> seatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_status_office2);

    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        seat_location = intent.getStringExtra("seatId");

        seatList = new ArrayList<>();

        seatTv[0] = findViewById(R.id.seat2_1);
        seatTv[1] = findViewById(R.id.seat2_2);
        seatTv[2] = findViewById(R.id.seat2_3);
        seatTv[3] = findViewById(R.id.seat2_4);
        seatTv[4] = findViewById(R.id.seat2_5);
        seatTv[5] = findViewById(R.id.seat2_6);
        seatTv[6] = findViewById(R.id.seat2_7);
        seatTv[7] = findViewById(R.id.seat2_8);
        seatTv[8] = findViewById(R.id.seat2_9);
        seatTv[9] = findViewById(R.id.seat2_10);
        seatTv[10] = findViewById(R.id.seat2_11);
        seatTv[11] = findViewById(R.id.seat2_12);
        seatTv[12] = findViewById(R.id.seat2_13);
        seatTv[13] = findViewById(R.id.seat2_14);
        seatTv[14] = findViewById(R.id.seat2_15);
        seatTv[15] = findViewById(R.id.seat2_16);
        seatTv[16] = findViewById(R.id.seat2_17);
        seatTv[17] = findViewById(R.id.seat2_18);
        seatTv[18] = findViewById(R.id.seat2_19);
        seatTv[19] = findViewById(R.id.seat2_20);
        seatTv[20] = findViewById(R.id.seat2_21);
        seatTv[21] = findViewById(R.id.seat2_22);
        seatTv[22] = findViewById(R.id.seat2_23);
        seatTv[23] = findViewById(R.id.seat2_24);
        seatTv[24] = findViewById(R.id.seat2_25);
        seatTv[25] = findViewById(R.id.seat2_26);
        seatTv[26] = findViewById(R.id.seat2_27);
        seatTv[27] = findViewById(R.id.seat2_28);
        seatTv[28] = findViewById(R.id.seat2_29);
        seatTv[29] = findViewById(R.id.seat2_30);
        seatTv[30] = findViewById(R.id.seat2_31);
        seatTv[31] = findViewById(R.id.seat2_32);
        seatTv[32] = findViewById(R.id.seat2_33);
        seatTv[33] = findViewById(R.id.seat2_34);
        seatTv[34] = findViewById(R.id.seat2_35);
        seatTv[35] = findViewById(R.id.seat2_36);
        seatTv[36] = findViewById(R.id.seat2_37);
        seatTv[37] = findViewById(R.id.seat2_38);
        seatTv[38] = findViewById(R.id.seat2_39);
        seatTv[39] = findViewById(R.id.seat2_40);
        seatTv[40] = findViewById(R.id.seat2_41);
        seatTv[41] = findViewById(R.id.seat2_42);
        seatTv[42] = findViewById(R.id.seat2_43);
        seatTv[43] = findViewById(R.id.seat2_44);
        seatTv[44] = findViewById(R.id.seat2_45);
        seatTv[45] = findViewById(R.id.seat2_46);
        seatTv[46] = findViewById(R.id.seat2_47);
        seatTv[47] = findViewById(R.id.seat2_48);
        seatTv[48] = findViewById(R.id.seat2_49);
        seatTv[49] = findViewById(R.id.seat2_50);
        seatTv[50] = findViewById(R.id.seat2_51);
        seatTv[51] = findViewById(R.id.seat2_52);

        loadProducts();

    }

    private void loadProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String name = product.getString("name");
                                String office_num = product.getString("office_num");
                                String seat_num = product.getString("seat_num");
                                String in_use = product.getString("in_use");
                                String start_time = product.getString("start_time");
                                String end_time = product.getString("end_time");
                                String user = product.getString("user");
                                String userName = product.getString("userName");

                                if(in_use.equals("1")){
                                    seatTv[i].setBackgroundResource(R.drawable.seat_table_use);
                                } if(id.equals(seat_location)){
                                    seatTv[i].setBackgroundResource(R.drawable.seat_table_my);
                                } else{

                                }

                                seatTv[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(in_use.equals("1")){
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {

    }
}

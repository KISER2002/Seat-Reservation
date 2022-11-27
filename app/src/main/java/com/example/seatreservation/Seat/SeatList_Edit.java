package com.example.seatreservation.Seat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Adapter.SeatAdapter;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeatList_Edit extends AppCompatActivity implements OnListItemSelectedInterface {
    private static final String PRODUCT_URL = "http://3.34.45.193/SeatList.php";

    private String  is_office;
    private ImageView refresh_btn;

    SessionManager sessionManager;

    RecyclerView recyclerView;

    ArrayList<Seat> seatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_list);

        sessionManager = new SessionManager(this);

        refresh_btn = findViewById(R.id.refresh);
//

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.seat_list_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        seatList = new ArrayList<>();

        loadProducts();
    }

    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                String idx = product.getString("idx");
                                String id = product.getString("id");
                                String name = product.getString("name");
                                String seat_count = product.getString("seat_count");
                                String in_use = product.getString("in_use");
                                String empty_seat = product.getString("empty_seat");

                                Intent intent = getIntent();
                                is_office = intent.getStringExtra("is_office");

                                Seat seat = new Seat();

                                seat.setIdx(idx);
                                seat.setId(id);
                                seat.setName(name);
                                seat.setSeat_count(seat_count);
                                seat.setIn_use(in_use);
                                seat.setEmpty_seat(empty_seat);
                                seat.setIs_office(is_office);

                                seatList.add(seat);

                                SeatAdapter adapter = new SeatAdapter(SeatList_Edit.this, seatList, SeatList_Edit.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

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
    public void OnItemSelected(View v, int position) {

    }

}

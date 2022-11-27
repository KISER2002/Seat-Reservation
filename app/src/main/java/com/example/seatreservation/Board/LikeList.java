package com.example.seatreservation.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.example.seatreservation.Adapter.BoardAdapter;
import com.example.seatreservation.Adapter.CommentAdapter;
import com.example.seatreservation.Adapter.LikeAdapter;
import com.example.seatreservation.Fragment.fragBoard;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardRequest;
import com.example.seatreservation.Request.CommentBoardReadRequest;
import com.example.seatreservation.Request.CommentRequest;
import com.example.seatreservation.Request.LikeLoadingRequest;
import com.example.seatreservation.Request.LikeStatusRequest;
import com.example.seatreservation.Request.ViewsCheckRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeList extends AppCompatActivity implements OnListItemSelectedInterface {

    private static final String PRODUCT_URL = "http://3.34.45.193/LikeList.php";

    private String  board_idx;
    private ImageView back_btn;

    SessionManager sessionManager;

    RecyclerView recyclerView;

    ArrayList<Like> likeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_like_list);

        sessionManager = new SessionManager(this);

        back_btn = findViewById(R.id.back_btn);
//

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.like_list_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        board_idx = intent.getStringExtra("idx");

        likeList = new ArrayList<>();

        loadProducts(board_idx);
    }

    private void loadProducts(String bno) {

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
                                String bno = product.getString("bno");
                                String user = product.getString("user");
                                String date = product.getString("date");
                                String userName = product.getString("userName");
                                String profileImg = product.getString("profileImg");

                                Like like = new Like();

                                like.setIdx(idx);
                                like.setBno(bno);
                                like.setUser(user);
                                like.setDate(date);
                                like.setUserName(userName);
                                like.setProfileImg(profileImg);

                                likeList.add(like);

                                LikeAdapter adapter = new LikeAdapter(LikeList.this, likeList, LikeList.this);
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
                params.put("bno", bno);
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

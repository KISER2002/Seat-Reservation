package com.example.seatreservation.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.example.seatreservation.Adapter.ReplyAdapter;
import com.example.seatreservation.Fragment.fragBoard;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardRequest;
import com.example.seatreservation.Request.CommentBoardReadRequest;
import com.example.seatreservation.Request.CommentRequest;
import com.example.seatreservation.Request.LikeLoadingRequest;
import com.example.seatreservation.Request.LikeStatusRequest;
import com.example.seatreservation.Request.ReplyRequest;
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

public class ReplyList extends AppCompatActivity implements OnListItemSelectedInterface {

    private static final String PRODUCT_URL = "http://3.34.45.193/ReplyList.php";

    private TextView comment_write_btn, content, writer, date;
    private String  comment_idx, comment_bno, comment_content, comment_writer, comment_writerId, comment_date, comment_profileImg;
    private EditText comment_write_et;
    private ImageView back_btn, profileImg, reply_menu_btn;

    SessionManager sessionManager;

    RecyclerView recyclerView;
    ReplyAdapter adapter;

    ArrayList<Reply> replyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_reply);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mProfileImg = user.get(sessionManager.PROFILE);

        back_btn = findViewById(R.id.back_btn);
        comment_write_et = findViewById(R.id.reply_write);
        comment_write_btn = findViewById(R.id.reply_write_btn);

        profileImg = findViewById(R.id.comment_profile_img);
        writer = findViewById(R.id.comment_writer_tv);
        content = findViewById(R.id.comment_content_tv);
        date = findViewById(R.id.comment_date_tv);
        reply_menu_btn = findViewById(R.id.reply_menu_btn);

        Intent intent = getIntent();
        comment_idx = intent.getStringExtra("idx");
        comment_bno = intent.getStringExtra("bno");
        comment_content = intent.getStringExtra("comment");
        comment_writer = intent.getStringExtra("writer");
        comment_writerId = intent.getStringExtra("writerId");
        comment_date = intent.getStringExtra("date");
        comment_profileImg = intent.getStringExtra("profileImg");

        writer.setText(comment_writer);
        content.setText(comment_content);
        date.setText(comment_date);

        if(comment_profileImg.equals("basic_image")){
            Glide.with(this).load(R.drawable.profile_img).override(50, 50).into(profileImg);
        }else {
            Glide.with(this).load("http://3.34.45.193" + comment_profileImg).override(50, 50).into(profileImg);
        }

        comment_write_et.requestFocus(); //커서 이동
        //키보드 보이게 하는 부분
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        comment_write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = comment_write_et.getText().toString();

                // 현재 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate);

                //댓글이 공백일 시
                if (comment.equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    comment_write_et.requestFocus(); //커서 이동

                } else if (!comment.equals("")) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
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
                                    imm.hideSoftInputFromWindow(comment_write_et.getWindowToken(), 0);
                                    Toast.makeText(getApplicationContext(), "답글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {//글 작성에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "답글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    ReplyRequest replyRequest = new ReplyRequest(comment_bno, comment_idx, mId, comment, getTime, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ReplyList.this);
                    queue.add(replyRequest);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.reply_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        comment_idx = intent.getStringExtra("idx");

        replyList = new ArrayList<>();

        loadProducts(comment_idx);

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
                                String date = product.getString("date");
                                String replySt = product.getString("comment");
                                String writer = product.getString("writer");
                                String userName = product.getString("userName");
                                String profileImg = product.getString("profileImg");

                                Reply reply = new Reply();

                                reply.setIdx(idx);
                                reply.setBno(bno);
                                reply.setDate(date);
                                reply.setReply(replySt);
                                reply.setWriter(writer);
                                reply.setWriterName(userName);
                                reply.setProfileImg(profileImg);

                                replyList.add(reply);

                                ReplyAdapter adapter = new ReplyAdapter(ReplyList.this, replyList, ReplyList.this);
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

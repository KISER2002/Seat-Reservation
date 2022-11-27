package com.example.seatreservation.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.SimpleItemAnimator;

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
import com.example.seatreservation.ViewType.CommentViewType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentList extends AppCompatActivity implements OnListItemSelectedInterface {

    private static final String PRODUCT_URL = "http://3.34.45.193/CommentList.php";
    private static String URL_LOADING = "http://3.34.45.193/CommentBoardRead.php";
    private String idx, title;
    Integer like;

    private LinearLayout board_view_btn;
    private TextView board_title_tv, comment_write_btn, like_status;
    private String  board_idx, board_title, readUserId, is_like;
    private EditText comment_write_et;
    private ImageView back_btn, like_btn, like_list_btn;
    private boolean likeValidate=false;

    private int viewType;

    SessionManager sessionManager;

    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    private ArrayList<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_comment);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mProfileImg = user.get(sessionManager.PROFILE);

        back_btn = findViewById(R.id.back_btn);
        like_status = findViewById(R.id.like_status);
        like_btn = findViewById(R.id.like_btn);
        like_list_btn = findViewById(R.id.like_list_btn);
        board_view_btn = findViewById(R.id.board_view_btn);
        board_title_tv = findViewById(R.id.board_title);
        comment_write_et = findViewById(R.id.comment_write);
        comment_write_btn = findViewById(R.id.comment_write_btn);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        board_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BoardView.class);
                i.putExtra("idx", board_idx);
                startActivity(i);
            }
        });

        like_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LikeList.class);
                i.putExtra("idx", board_idx);
                startActivity(i);
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
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                                        commentList.clear();
                                        loadProducts(board_idx);
                                        comment_write_et.setText("");
                                        imm.hideSoftInputFromWindow(comment_write_et.getWindowToken(), 0);
//                                        recyclerView.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                                                // Here adapter.getItemCount()== child count
//                                            }
//                                        });
                                        Toast.makeText(getApplicationContext(), "댓글이 작성 되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {//글 작성에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "게시글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    CommentRequest commentRequest = new CommentRequest(board_idx ,mId, comment, getTime, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(CommentList.this);
                    queue.add(commentRequest);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        recyclerView = (RecyclerView) findViewById(R.id.comment_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        board_idx = intent.getStringExtra("idx");
//        board_title = intent.getStringExtra("title");
//        board_title_tv.setText(board_title);

        commentList = new ArrayList<>();

        adapter = new CommentAdapter(CommentList.this, commentList, CommentList.this);
        recyclerView.setAdapter(adapter);


//        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
//        if (animator instanceof SimpleItemAnimator) {
//            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//        }

//        commentList.clear();
        adapter.notifyDataSetChanged();

        loadProducts(board_idx);

        Response.Listener<String> responseListener1 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("loading");

                    if(success.equals("1")){
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            idx = object.getString("idx".trim());
                            title = object.getString("title".trim());
                            like = object.getInt("like_count".trim());

                            board_title_tv = findViewById(R.id.board_title);
                            like_status = findViewById(R.id.like_status);

                            board_title_tv.setText(title);
                            if(like == 0){

                            }else {
                                like_status.setText(String.valueOf(like) + "명이 좋아합니다.");
                            }
                        }
                    } else { // 실패
                        Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                }
            }
        };
        CommentBoardReadRequest commentBoardReadRequest = new CommentBoardReadRequest(board_idx, responseListener1);
        RequestQueue queue1 = Volley.newRequestQueue(CommentList.this);
        queue1.add(commentBoardReadRequest);



        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        readUserId = mId;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("likeLoading");

                    if(success.equals("1")){
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            is_like = object.getString("is_like").trim();

                            like_btn = findViewById(R.id.like_btn);

                            if(is_like.equals("1")){
                                Glide.with(CommentList.this).load(R.drawable.like_ok).into(like_btn);
                                likeValidate = true;
                            }
                            if(is_like.equals("0")){
                                Glide.with(CommentList.this).load(R.drawable.like).into(like_btn);
                                likeValidate = false;
                            }
                            else {
                                return;
                            }

                        }
                    } else { // 실패
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LikeLoadingRequest likeLoadingRequest = new LikeLoadingRequest(board_idx, readUserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(CommentList.this);
        queue.add(likeLoadingRequest);

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate);

                if(likeValidate){
                    is_like = "0";
                    like -= 1;
                    if(like ==0){
                        like_status.setText("제일 먼저 좋아요를 누르세요");
                    }else {
                        like_status.setText(String.valueOf(like) + "명이 좋아합니다.");
                    }
                    Glide.with(CommentList.this).load(R.drawable.like).into(like_btn);
                    likeValidate=false;
                } else{
                    is_like = "1";
                    like += 1;
                    like_status.setText(String.valueOf(like) + "명이 좋아합니다.");
                    Glide.with(CommentList.this).load(R.drawable.like_ok).into(like_btn);
                    likeValidate=true;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                            if (success) {//좋아요 클릭에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
//                                Toast.makeText(getApplicationContext(), is_like, Toast.LENGTH_SHORT).show();
                            }
                            else{//좋아요에 실패한 경우
                                Toast.makeText(getApplicationContext(),"좋아요에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                LikeStatusRequest likeStatusRequest = new LikeStatusRequest(board_idx, readUserId, is_like, getTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CommentList.this);
                queue.add(likeStatusRequest);
            }
        });

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
                                String commentSt = product.getString("comment");
                                String writer = product.getString("writer");
                                String parent = product.getString("parent");
                                String userName = product.getString("userName");
                                String profileImg = product.getString("profileImg");
                                String deleted = product.getString("is_deleted");

                                if(deleted.equals("1")){
                                    viewType = CommentViewType.DELETED_ITEM;
                                }else if(parent.equals("null")){
                                    viewType = CommentViewType.COMMENT_ITEM;
                                }
                                else{
                                    viewType = CommentViewType.REPLY_ITEM;
                                }

                                Comment comment = new Comment();

                                comment.setIdx(idx);
                                comment.setBno(bno);
                                comment.setParent(parent);
                                comment.setDate(date);
                                comment.setComment(commentSt);
                                comment.setWriter(writer);
                                comment.setWriterName(userName);
                                comment.setProfileImg(profileImg);
                                comment.setViewType(viewType);

                                commentList.add(comment);
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

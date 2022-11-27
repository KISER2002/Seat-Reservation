package com.example.seatreservation.Board;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.Adapter.BoardAdapter;
import com.example.seatreservation.Fragment.fragBoard;
import com.example.seatreservation.HomeActivity;
import com.example.seatreservation.Join;
import com.example.seatreservation.MainActivity;
import com.example.seatreservation.MyPage.UserEdit;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardDeleteRequest;
import com.example.seatreservation.Request.LikeLoadingRequest;
import com.example.seatreservation.Request.LikeStatusRequest;
import com.example.seatreservation.Request.RegisterRequest;
import com.example.seatreservation.Request.UserEditRequest;
import com.example.seatreservation.Request.ValidateRequest;
import com.example.seatreservation.Request.ViewsCheckRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BoardView extends AppCompatActivity {
    private static String URL_LOADING = "http://3.34.45.193/BoardRead.php";
    private static String URL_LIKE_LOADING = "http://3.34.45.193/LikeRead.php";
    private String readUserId;
    private String getTitle, getContent, getImage;
    private String mJsonString;
    private String idx, title, content, writer, writerName, profileImg, date, image, comment;
    private String like_bno, like_user, is_like;
    Integer hit, like;
    String hit_string;
    private boolean likeValidate=false;

    private int REQUEST_EDIT = 200;

    TextView view_title, view_writer, view_date, view_hit, view_content, like_count, comment_count;
    ImageView backBtn, menuBtn, writer_profile_image, view_image, like_btn, comment_btn;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_view);

        view_title = findViewById(R.id.view_post_title);
        view_writer = findViewById(R.id.view_writer);
        view_date = findViewById(R.id.view_date);
        view_hit = findViewById(R.id.view_hit);
        view_content = findViewById(R.id.view_content);
        backBtn = findViewById(R.id.back_btn);
        menuBtn = findViewById(R.id.menu_btn);
        writer_profile_image = findViewById(R.id.profile_img);
        view_image = findViewById(R.id.view_image);
        like_count = findViewById(R.id.like_count);
        like_btn = findViewById(R.id.like_btn);
        comment_count = findViewById(R.id.comment_count);
        comment_btn = findViewById(R.id.comment_btn);

        Intent intent = getIntent();
        idx = intent.getStringExtra("idx");

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        readUserId = mId;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CommentList.class);
                i.putExtra("idx", idx);
                i.putExtra("title", title);
                startActivity(i);
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                like_btn.setImageResource(R.drawable.like_ok);
//                like =+ 1;
//                like_count.setText(String.valueOf(like));
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1){
                            Intent i = new Intent(getApplicationContext(), BoardEdit.class);
                            i.putExtra("idx", idx);
                            i.putExtra("title", title);
                            i.putExtra("content", content);
                            i.putExtra("image", image);
//                            startActivityForResult(i, 200);
                            startActivity(i);
                        }else if (menuItem.getItemId() == R.id.action_menu2){
                            AlertDialog.Builder builder = new AlertDialog.Builder(BoardView.this);
                            builder.setTitle("삭제").setMessage("게시물을 삭제하시겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                if (success) {//정보수정에 성공한 경우
                                                    Toast.makeText(getApplicationContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                    finish();
                                                }
                                                else{//정보수정에 실패한 경우
                                                    Toast.makeText(getApplicationContext(),"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    //서버로 volley 를 이용해서 요청을 함
                                    BoardDeleteRequest boardDeleteRequest = new BoardDeleteRequest(idx, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(BoardView.this);
                                    queue.add(boardDeleteRequest);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.v("main onActivityResult", "onActivityResult 호출");
//        if (resultCode == RESULT_OK) {
//
//            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
//            if (requestCode == REQUEST_EDIT) {
//                getTitle = data.getStringExtra("title");
//                getContent = data.getStringExtra("content");
//                getImage = data.getStringExtra("image");
//
//                view_title.setText(getTitle);
//                view_content.setText(getContent);
//                Glide.with(this).load("http://3.34.45.193" + getImage).into(view_image);
//            }
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        idx = intent.getStringExtra("idx");
        Loading(idx);

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
                                Glide.with(BoardView.this).load(R.drawable.like_ok).into(like_btn);
                                likeValidate = true;
                            }
                            if(is_like.equals("0")){
                                Glide.with(BoardView.this).load(R.drawable.like).into(like_btn);
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
        LikeLoadingRequest likeLoadingRequest = new LikeLoadingRequest(idx, readUserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(BoardView.this);
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
                    like_count.setText(String.valueOf(like));
                    Glide.with(BoardView.this).load(R.drawable.like).into(like_btn);
                    likeValidate=false;
                } else{
                    is_like = "1";
                    like += 1;
                    like_count.setText(String.valueOf(like));
                    Glide.with(BoardView.this).load(R.drawable.like_ok).into(like_btn);
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
                            else{//회원가입에 실패한 경우
                                Toast.makeText(getApplicationContext(),"좋아요에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                LikeStatusRequest likeStatusRequest = new LikeStatusRequest(idx, readUserId, is_like, getTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(BoardView.this);
                queue.add(likeStatusRequest);
            }
        });

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
                                    idx = object.getString("idx".trim());
                                    title = object.getString("title".trim());
                                    content = object.getString("content").trim();
                                    image = object.getString("image".trim());
                                    date = object.getString("date".trim());
                                    writer = object.getString("writer".trim());
                                    hit = object.getInt("hit".trim());
                                    writerName = object.getString("userName".trim());
                                    profileImg = object.getString("profileImg".trim());
                                    comment = object.getString("comment".trim());
                                    like = object.getInt("like_count".trim());

                                    view_title = findViewById(R.id.view_post_title);
                                    view_writer = findViewById(R.id.view_writer);
                                    view_date = findViewById(R.id.view_date);
                                    view_hit = findViewById(R.id.view_hit);
                                    view_content = findViewById(R.id.view_content);
                                    backBtn = findViewById(R.id.back_btn);
                                    menuBtn = findViewById(R.id.menu_btn);
                                    writer_profile_image = findViewById(R.id.profile_img);
                                    view_image = findViewById(R.id.view_image);
                                    comment_count = findViewById(R.id.comment_count);
                                    like_count = findViewById(R.id.like_count);

                                    view_title.setText(title);
                                    view_content.setText(content);
                                    view_writer.setText(writerName);
                                    view_date.setText(date);
                                    view_hit.setText(String.valueOf(hit));
                                    comment_count.setText(comment);
                                    like_count.setText(String.valueOf(like));
                                    Glide.with(BoardView.this).load("http://3.34.45.193" + image).into(view_image);
                                    if(profileImg.equals("basic_image")){
                                        Glide.with(BoardView.this).load(R.drawable.profile_img).override(50, 50).into(writer_profile_image);
                                    }else {
                                        Glide.with(BoardView.this).load("http://3.34.45.193" + profileImg).override(50, 50).into(writer_profile_image);
                                    }

                                    if(writer.equals(readUserId)){

                                    }
                                    else {
                                        menuBtn.setVisibility(View.GONE);
                                    }

                                    hit += 1;
                                    view_hit.setText(String.valueOf(hit));

                                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                if (success) {//조회수 증가
                                                    new Thread(new Runnable() {
                                                        public void run() {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                }
                                                            });
                                                        }
                                                    }).start();
                                                }
                                                else{//실패한 경우
                                                    Toast.makeText(getApplicationContext(),"증가에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    //서버로 volley 를 이용해서 요청을 함
                                    ViewsCheckRequest viewsCheckRequest = new ViewsCheckRequest(idx, hit, responseListener1);
                                    RequestQueue queue1 = Volley.newRequestQueue(BoardView.this);
                                    queue1.add(viewsCheckRequest);


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

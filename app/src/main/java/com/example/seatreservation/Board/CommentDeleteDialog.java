package com.example.seatreservation.Board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.CommentDelete2Request;
import com.example.seatreservation.Request.CommentDeleteRequest;
import com.example.seatreservation.Request.ViewsCheckRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommentDeleteDialog extends Activity {
    private static String URL_LOADING = "http://3.34.45.193/LoadReplyCount.php";

    Button noBtn, yesBtn;
    String getIdx, reply_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frag_board_comment_delete_dialog);
        noBtn = findViewById(R.id.noBtn);

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("idx");

        Loading(getIdx);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                                    reply_count = object.getString("count".trim());

                                    yesBtn = findViewById(R.id.yesBtn);

                                    yesBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(reply_count.equals("0")){

                                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                            if (success) {//정보수정에 성공한 경우
                                                                Toast.makeText(CommentDeleteDialog.this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        CommentDeleteDialog.this.runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                            }
                                                                        });
                                                                    }
                                                                }).start();
                                                                finish();
                                                            }
                                                            else{
                                                                Toast.makeText(CommentDeleteDialog.this,"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                //서버로 volley 를 이용해서 요청을 함
                                                CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(getIdx, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(CommentDeleteDialog.this);
                                                queue.add(commentDeleteRequest);




                                            } else{

                                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                            if (success) {//정보수정에 성공한 경우
                                                                Toast.makeText(CommentDeleteDialog.this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        CommentDeleteDialog.this.runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                            }
                                                                        });
                                                                    }
                                                                }).start();
                                                                finish();
                                                            }
                                                            else{
                                                                Toast.makeText(CommentDeleteDialog.this,"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                //서버로 volley 를 이용해서 요청을 함
                                                CommentDelete2Request commentDeleteRequest2 = new CommentDelete2Request(getIdx, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(CommentDeleteDialog.this);
                                                queue.add(commentDeleteRequest2);
                                            }
                                        }
                                    });

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

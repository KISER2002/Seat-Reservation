package com.example.seatreservation.Board;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.BoardEditRequest;
import com.example.seatreservation.Request.CommentEditRequest;
import com.example.seatreservation.Request.ReplyEditRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ReplyEdit extends AppCompatActivity {
    TextView saveBtn;
    EditText contentEt;
    ImageView back_btn;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_comment_edit);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        saveBtn = findViewById(R.id.comment_save_btn);
        contentEt = findViewById(R.id.comment_content);
        back_btn = findViewById(R.id.back_btn);

        Intent intent = getIntent();
        String idx = intent.getStringExtra("idx");
        String bno = intent.getStringExtra("bno");
        String reply = intent.getStringExtra("reply");

        contentEt.setText(reply);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                //editText에 입력되어있는 값을 get(가져온다)해온다
                String content = contentEt.getText().toString();

                //내용이 공백일 시
                if (content.equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    contentEt.requestFocus(); //커서 이동

                } else if (!content.equals("")) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Comment php에 response
                                boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                                if (success) {//게시글 작성에 성공한 경우
                                    Toast.makeText(getApplicationContext(), "댓글 수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                    imm.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
                                    finish();
                                } else {//글 작성에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "댓글 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    ReplyEditRequest replyEditRequest = new ReplyEditRequest(idx, bno, content, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ReplyEdit.this);
                    queue.add(replyEditRequest);
                }
            }
        });
    }
}

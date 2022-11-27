package com.example.seatreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn, forgetBtn, joinBtn;
    private EditText IdEt, PwdEt;
    private CheckBox loginMaintainCB;
    private String id, name, profile, in_use;
    private static String URL_LOGIN = "http://3.34.45.193/Login.php";
    boolean checkBoxChecked;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        IdEt = findViewById(R.id.login_id);
        PwdEt = findViewById(R.id.login_password);

        loginBtn = findViewById(R.id.login_btn);
        forgetBtn = findViewById(R.id.user_forget_btn);
        joinBtn  = findViewById(R.id.join_btn);

        checkBoxChecked = false;

        loginMaintainCB = findViewById(R.id.login_maintain_btn);
        loginMaintainCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    checkBoxChecked = true;
                }else{
                    checkBoxChecked = false;
                }
            }
        });


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Join.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get(가져온다)해온다.
                String userID = IdEt.getText().toString().trim();
                String userPass = PwdEt.getText().toString().trim();

                if(!userID.isEmpty() || !userPass.isEmpty()){
                    Login(userID, userPass);
                }
                else if(userID.isEmpty()){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(userPass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void Login(String userId, String userPass){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){ // 로그인 성공
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    profile = object.getString("profileImg".trim());
                                    name = object.getString("userName").trim();
                                    id = object.getString("userID".trim());
                                    in_use = object.getString("in_use".trim());

                                    sessionManager.createSession(profile, name, id, in_use);

                                    if(checkBoxChecked){
                                        sessionManager.loginMaintain();
                                    }

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    intent.putExtra("profileImg", profile);
                                    intent.putExtra("userName", name);
                                    intent.putExtra("userID", id);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                }
                            } else { // 로그인 실패
                                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
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
                params.put("userID", userId);
                params.put("userPassword", userPass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
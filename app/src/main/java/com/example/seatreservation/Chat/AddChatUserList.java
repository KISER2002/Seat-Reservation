package com.example.seatreservation.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.seatreservation.Adapter.ChatUserAdapter;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.AddChatRoomUserRequest;
import com.example.seatreservation.Request.ChatRoomUserRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddChatUserList extends AppCompatActivity implements OnListItemSelectedInterface {
    public static Activity chatUserList;

    private static final String PRODUCT_URL = "http://3.34.45.193/AddChatUserList.php";

    private ImageView back_btn;
    private TextView title_tv ,confirm_btn;
    private EditText searchEt;
    private ChatUserAdapter adapter;
    private int chatRoomCount;
    private String MyId;

    SessionManager sessionManager;

    RecyclerView recyclerView;

    ArrayList<User> searchList = new ArrayList<>();
    ArrayList<User> userList;
    ArrayList<User> selectedList;

    private String chatRoomIdx;
    private ArrayList<User> chatRoomUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);

        Intent intent = getIntent();
        chatRoomIdx = intent.getStringExtra("chat_room_idx");
        chatRoomUserList = (ArrayList<User>) intent.getSerializableExtra("chat_room_user_list");

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        MyId = mId;

        loadProducts(chatRoomIdx);

        recyclerView = (RecyclerView) findViewById(R.id.chat_user_list_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatUserAdapter(AddChatUserList.this, userList, AddChatUserList.this);
        recyclerView.setAdapter(adapter);

        userList = new ArrayList<>();

        title_tv = findViewById(R.id.chat_user_list_title);
        back_btn = findViewById(R.id.back_btn);
        confirm_btn = findViewById(R.id.confirm_btn);
        searchEt = findViewById(R.id.search_et);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = adapter.listOfSelectedItem();

                String chatUserId = "";
                for(int a = 0; a <selectedList.size(); a++){
                    chatUserId = selectedList.get(a).getUserId();
                }

                if(selectedList.size() == 0){
                    Toast.makeText(getApplicationContext(), "대화 상대를 1명 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//회원가입에 성공한 경우
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    finish();
                                                }
                                            });
                                        }
                                    }).start();

                                    finish();
                                } else {//회원가입에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "채팅방 유저 생성 실패.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    AddChatRoomUserRequest addChatRoomUserRequest = new AddChatRoomUserRequest(chatRoomIdx, chatUserId, responseListener1);
                    RequestQueue queue1 = Volley.newRequestQueue(AddChatUserList.this);
                    queue1.add(addChatRoomUserRequest);
                }
            }
        });

        title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = adapter.listOfSelectedItem();
                title_tv.setText("대화 상대 선택 " + selectedList.size());
                Log.d("test list", selectedList.toString());
            }
        });

        // editText 리스터 작성
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = searchEt.getText().toString();
                searchList.clear();

                if(searchText.equals("")){
                    adapter.setItems(userList);
                }
                else {
                    // 검색 단어를 포함하는지 확인
                    for (int a = 0; a < userList.size(); a++) {
                        if (userList.get(a).userName.toLowerCase().contains(searchText.toLowerCase())) {
                            searchList.add(userList.get(a));
                        }
                        adapter.setItems(searchList);
                    }
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadProducts(String mID) {
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
                                String profileImg = product.getString("profileImg");
                                String userID = product.getString("userId");
                                String userName = product.getString("userName");

                                User user = new User();

                                user.setUserId(userID);
                                user.setUserName(userName);
                                user.setProfileImg(profileImg);

                                userList.add(user);

                                adapter.setItems(userList);

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
                params.put("idx", mID);
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

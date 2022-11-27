package com.example.seatreservation.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Adapter.ChatRoomAdapter;
import com.example.seatreservation.Chat.ChatRoom;
import com.example.seatreservation.Chat.ChatUserList;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragChat extends Fragment implements OnListItemSelectedInterface {
    private View view;

    private static String URL_LOADING = "http://3.34.45.193/LoadChatList.php";
    private static final String PRODUCT_URL = "http://3.34.45.193/ChatList.php";

    private ImageView writeBtn;

    private ArrayList<ChatRoom> chatRoomList;
    private ChatRoomAdapter adapter;
    private RecyclerView recyclerView;
    public static Context context;

    private String myId;

    SessionManager sessionManager;

    private String TAG = "프래그먼트";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_chat, container, false);

        writeBtn = view.findViewById(R.id.make_chat);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChatUserList.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        context = getContext();

        recyclerView = view.findViewById(R.id.chat_room_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        chatRoomList = new ArrayList<>();

        adapter = new ChatRoomAdapter(getActivity(), chatRoomList, this::OnItemSelected);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        sessionManager = new SessionManager(getActivity());

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);

        myId = mId;

        loadProducts(mId);

    }

    @Override
    public void OnItemSelected(View v, int position) {

    }

    private void loadProducts(String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                String getIdx = product.getString("idx");
                                String getTitle = product.getString("title");
                                String getRoomImg = product.getString("roomImg");

                                ChatRoom chatRoom = new ChatRoom();

                                chatRoom.setIdx(getIdx);
                                chatRoom.setTitle(getTitle);
                                chatRoom.setRoomImg(getRoomImg);

                                chatRoomList.add(chatRoom);
                                adapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mID", userId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


    }
}
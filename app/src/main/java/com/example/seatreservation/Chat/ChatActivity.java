package com.example.seatreservation.Chat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Adapter.ChatMenuAdapter;
import com.example.seatreservation.Adapter.MessageAdapter;
import com.example.seatreservation.OnListItemSelectedInterface;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.ChatRoomExitRequest;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements OnListItemSelectedInterface {
    private static final String PRODUCT_URL = "http://3.34.45.193/LoadMessage.php";
    private static final String PRODUCT_URL2 = "http://3.34.45.193/LoadChatUserList.php";

    private static final String TAG = "MultiImageActivity";
    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????

    RecyclerView recyclerView;
    RecyclerView menuRecyclerView;
    ChatMenuAdapter menuAdapter;
    MessageAdapter adapter;
    ArrayList<User> chatUserList;
    private String chatRoomIdx, chatRoomName;

    String mName, mId, mProfile;

    private Handler mHandler;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    private String ip = "3.34.45.193";
    private int port = 8888;
    InetAddress serverAddr;

    SessionManager sessionManager;

    TextView roomName;
    String UserID;
    ImageView backBtn, menuBtn;
    ImageButton chatButton, imageButton;
    private EditText message;
    String sendMsg;

    private String ImagePath;
    private String ImageName;
    private String sendImg;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    String read;

    private DrawerLayout drawerLayout;
    private View view;
    private TextView roomNameTv;
    private Button exitBtn;
    private LinearLayout userAddBtn;

    private ArrayList<Message> messageList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();

//        close();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        drawerLayout = findViewById(R.id.Drawer);
        view = findViewById(R.id.slider);

        roomName = findViewById(R.id.roomName);

        Intent intent = getIntent();
        chatRoomIdx = intent.getStringExtra("chat_room_idx");
        chatRoomName = intent.getStringExtra("chat_room_name");

        roomName.setText(chatRoomName);

        sessionManager = new SessionManager(getApplicationContext());

        HashMap<String, String> user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mId = user.get(sessionManager.ID);
        mProfile = user.get(sessionManager.PROFILE);

        upLoadServerUri = "http://3.34.45.193/UploadToServer.php";//?????????????????? ip??????

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menuBtn = findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = getIntent();
//                chatRoomIdx = intent.getStringExtra("chat_room_idx");
                loadUserList(chatRoomIdx, mId);

                menuRecyclerView = findViewById(R.id.chat_user_list_rv);
                menuRecyclerView.setHasFixedSize(true);
                menuRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                menuAdapter = new ChatMenuAdapter(ChatActivity.this, chatUserList, ChatActivity.this);
                menuRecyclerView.setAdapter(menuAdapter);

                chatUserList = new ArrayList<>();
                drawerLayout.openDrawer(view);
            }
        });

        roomNameTv = findViewById(R.id.drawerRoomName);
        roomNameTv.setText(chatRoomName);

        userAddBtn = findViewById(R.id.add_user_btn);
        userAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddChatUserList.class);
                intent.putExtra("chat_room_idx", chatRoomIdx);
                intent.putExtra("chat_room_user_list", chatUserList);
                startActivity(intent);
            }
        });

        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("?????????").setMessage("???????????? ??????????????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jasonObject = new JSONObject(response);//Register php??? response
                                            boolean success = jasonObject.getBoolean("success");//Register php??? sucess
                                            if (success) {//??????????????? ????????? ??????
                                                new Thread(new Runnable() {
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(),"??????????????? ???????????????.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).start();
                                                finish();
                                            }
                                            else{//??????????????? ????????? ??????
                                                Toast.makeText(getApplicationContext(),"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                //????????? volley ??? ???????????? ????????? ???
                                ChatRoomExitRequest chatRoomExitRequest = new ChatRoomExitRequest(chatRoomIdx, mId, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
                                queue.add(chatRoomExitRequest);
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
            }
        });

        recyclerView = findViewById(R.id.chat_rv);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(this, messageList, this);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        loadProducts(chatRoomIdx);

        mHandler = new Handler();
        message =  findViewById(R.id.message);
        UserID = mName;
        chatButton = findViewById(R.id.chatButton);
        imageButton = findViewById(R.id.imageBtn);

        new Thread() {
            public void run() {
                try {
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    pw = new PrintWriter(socket.getOutputStream());
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = br.readLine();

                        if(read!=null){
                            mHandler.post(new ChatActivity.msgUpdate(read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg = message.getText().toString();
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
                String getTime = simpleDate.format(mDate);
                String type = "message";

                adapter.addItem(new Message(sendMsg, mId, mName, mProfile, getTime, chatRoomIdx, type));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size()-1);

                String MessageJsonArray =
                "{\"message\":\"" + sendMsg + "\",\"id\":\"" + mId + "\",\"name\":\"" + mName + "\",\"profile\":\""
                        + mProfile + "\",\"createdAt\":\"" + getTime + "\",\"chat_room\":\"" + chatRoomIdx
                        + "\",\"type\":\"" + type + "\", \"chat_room_name\":\"" + chatRoomName + "\"}\n";
                message.setText("");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            pw.println(MessageJsonArray);
                            pw.flush();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    // ???????????? ??????????????? ????????? ??? ???????????? ?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(data == null){   // ?????? ???????????? ???????????? ?????? ??????
//            Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_LONG).show();
        }
        else{   // ???????????? ???????????? ????????? ??????
            if(data.getClipData() == null){     // ???????????? ????????? ????????? ??????
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);
            }
            else{      // ???????????? ????????? ????????? ??????
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // ????????? ???????????? 11??? ????????? ??????
                    Toast.makeText(getApplicationContext(), "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else{   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????
                    Log.e(TAG, "multiple choice");

                    uriList = new ArrayList<>();

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                        try {
                            uriList.add(imageUri);  //uri??? list??? ?????????.

                            String path = getPath(imageUri);
                            String name = getName(imageUri);
                            sendImg = "/Images/" + name;

                            ImagePath = path;
                            ImageName = name;

                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                            if (SDK_INT > 8)
                            {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                //your codes here
                                uploadFile(ImagePath);

                            }

                            long now = System.currentTimeMillis();
                            Date mDate = new Date(now);
                            SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
                            String getTime = simpleDate.format(mDate);
                            String type = "image";

                            adapter.addItem(new Message(sendImg, mId, mName, mProfile, getTime, chatRoomIdx, type));
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messageList.size()-1);

                            String ImageJsonArray =
                                    "{\"message\":\"" + sendImg + "\",\"id\":\"" + mId + "\",\"name\":\"" + mName + "\",\"profile\":\""
                                            + mProfile + "\",\"createdAt\":\"" + getTime + "\",\"chat_room\":\"" + chatRoomIdx
                                            + "\",\"type\":\"" + type + "\",\"chat_room_name\":\"" + chatRoomName + "\"}\n";
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        pw.println(ImageJsonArray);
                                        pw.flush();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
//                    Log.e("urlList : ", String.valueOf(uriList));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        chatRoomIdx = intent.getStringExtra("chat_room_idx");

        sessionManager = new SessionManager(getApplicationContext());

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mId = user.get(sessionManager.ID);
        String mProfile = user.get(sessionManager.PROFILE);

        loadUserList(chatRoomIdx, mId);

        menuRecyclerView = findViewById(R.id.chat_user_list_rv);
        menuRecyclerView.setHasFixedSize(true);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new ChatMenuAdapter(ChatActivity.this, chatUserList, ChatActivity.this);
        menuRecyclerView.setAdapter(menuAdapter);

        chatUserList = new ArrayList<>();
    }

    private void loadUserList(String mIdx, String mID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL2,
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

                                chatUserList.add(user);

                                menuAdapter.setItems(chatUserList);

                                menuAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley ?????? ??????.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idx", mIdx);
                params.put("mID", mID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // ?????? ?????? ??????(????????? ????????? ?????? ???)
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // ????????? ??????
    private String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"
                    + ImagePath + "" + ImageName);
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + ImageName;

//                            Toast.makeText(Join.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;

        } // End else block
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
                                String message = product.getString("message");
                                String userID = product.getString("userID");
                                String createdAt = product.getString("createdAt");
                                String chat_room = product.getString("chat_room");
                                String userName = product.getString("userName");
                                String profileImg = product.getString("profileImg");
                                String getType = product.getString("type");

                                Message item = new Message();

                                item.setMessage(message);
                                item.setUserId(userID);
                                item.setCreatedAt(createdAt);
                                item.setChatRoomIdx(chat_room);
                                item.setUserName(userName);
                                item.setUserProfile(profileImg);
                                item.setType(getType);

                                messageList.add(item);
                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                        }

                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley ?????? ??????.",Toast.LENGTH_SHORT).show();
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

    class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String str) {this.msg=str;}

        @Override
        public void run() {

            try {
//                data??? json?????? ??????
                JSONObject obj = new JSONObject(msg);
                String getMessage = obj.getString("message");
                String getUserId = obj.getString("id");
                String getUserName = obj.getString("name");
                String getUserProfile = obj.getString("profile");
                String getCreatedAt = obj.getString("createdAt");
                String getChatRoomIdx = obj.getString("chat_room");
                String getType = obj.getString("type");

                if(getChatRoomIdx.equals(chatRoomIdx) && !getUserId.equals(mId)) {
                    adapter.addItem(new Message(getMessage, getUserId, getUserName, getUserProfile, getCreatedAt, getChatRoomIdx, getType));
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size()-1);
                }else{
                    Log.i("Message : ", "?????? ?????????");
                }

            } catch (JSONException e) {
//                e.printStackTrace();
            }

        }
    }

    //  ????????? ??????????????? ???????????? ??????

    public void close(){
        try{
//            br.close();
            pw.close();
            socket.close();
        }catch(Exception e){

        }
    }
}

package com.example.seatreservation.Chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Join;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.ChatRoomRequest;
import com.example.seatreservation.Request.ChatRoomUserRequest;
import com.example.seatreservation.Request.RegisterRequest;
import com.example.seatreservation.Seat.SeatEdit;
import com.example.seatreservation.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeChatRoomDialog extends Activity {
    ChatUserList CUL = (ChatUserList) ChatUserList.chatUserList;

    private static final String CHATROOM_URL = "http://3.34.45.193/ChatRoomCount.php";

    private int chatRoomCount;
    ArrayList<User> selectedList;

    CircleImageView chat_room_img;
    Uri photoUri;
    EditText chat_room_name;
    Button okBtn;
    private AlertDialog dialog;

    private static final int PICK_FORM_ALBUM = 1;

    private String ImagePath;
    private String ImageName;
    private String roomImg;

    private String basicPath;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    SessionManager sessionManager;
    String MyId;
    String chatUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.make_chat_room_dialog);

        Intent intent = getIntent();
        chatUserId = intent.getStringExtra("chat_user_id");

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        MyId = mId;

        chat_room_img = findViewById(R.id.chat_room_img);
        chat_room_name = findViewById(R.id.chat_room_name);
        okBtn = findViewById(R.id.confirm_btn);

        Uri basicUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.null_room);
        basicPath = "drawable://" + R.drawable.null_room;
        photoUri = basicUri;

        upLoadServerUri = "http://3.34.45.193/UploadToServer.php";//?????????????????? ip??????

        //????????? ?????? ?????? ?????? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MakeChatRoomDialog.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        // ????????? ?????? ??????
        chat_room_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence info[] = new CharSequence[]{"?????? ?????? ??????", "?????? ???????????? ??????"};


                AlertDialog.Builder builder = new AlertDialog.Builder(MakeChatRoomDialog.this, R.style.Base_Theme_AppCompat_Light_Dialog);
                builder.setTitle("????????? ????????? ??????");
                builder.setItems(info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent intent1 = new Intent(Intent.ACTION_PICK);
                                intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1, PICK_FORM_ALBUM);
                                break;
                            case 1:
                                // ?????? ???????????? ??????
                                photoUri = basicUri;
                                chat_room_img.setImageURI(photoUri);
                                Toast.makeText(getApplicationContext(), "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photoUri == basicUri) {
                    String path = "drawable://" + R.drawable.profile_img;
                    String name = "basic_image";
                    roomImg = name;

                    ImagePath = path;
                    ImageName = name;
                } else {
                    String path = getPath(photoUri);
                    String name = getName(photoUri);
                    roomImg = "/Images/" + name;

                    ImagePath = path;
                    ImageName = name;
                }


                //editText??? ?????????????????? ?????? get(????????????)?????????
                String chatRoomName = chat_room_name.getText().toString();

                //???????????? ????????? ???
                if (chatRoomName.equals("")) {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    chat_room_name.requestFocus(); //?????? ??????
                    //????????? ????????? ?????? ??????
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php??? response
                                boolean success = jasonObject.getBoolean("success");//Register php??? sucess
                                if (success) {//??????????????? ????????? ??????
                                    if (ImagePath != null) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        loadChatRoomCnt();
                                                    }
                                                });
                                                if (ImagePath == basicPath) {

                                                } else {
                                                    uploadFile(ImagePath);
                                                }
                                            }
                                        }).start();
                                    } else {
                                    }
                                    finish();
                                } else {//??????????????? ????????? ??????
                                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //????????? volley ??? ???????????? ????????? ???
                    ChatRoomRequest chatRoomRequest = new ChatRoomRequest(chatRoomName, roomImg, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MakeChatRoomDialog.this);
                    queue.add(chatRoomRequest);
                }
            }
        });
    }

    private void loadChatRoomCnt() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHATROOM_URL,
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
                                    chatRoomCount = object.getInt("cnt".trim());

                                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
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
                                                                }
                                                            });
                                                        }
                                                    }).start();

                                                    finish();
                                                } else {//??????????????? ????????? ??????
                                                    Toast.makeText(getApplicationContext(), "????????? ?????? ?????? ??????.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    //????????? volley ??? ???????????? ????????? ???
                                    ChatRoomUserRequest chatRoomUserRequest = new ChatRoomUserRequest(String.valueOf(chatRoomCount), MyId, chatUserId, responseListener1);
                                    RequestQueue queue1 = Volley.newRequestQueue(MakeChatRoomDialog.this);
                                    queue1.add(chatRoomUserRequest);

                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("chat_room_idx", String.valueOf(chatRoomCount));
                                    intent.putExtra("chat_room_name", chat_room_name.getText().toString());
                                    startActivity(intent);
                                    CUL.finish();
                                    finish();

                                }
                            } else { // ??????
                                Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                                return;
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

    // uri ????????? ??????
    private String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("main onActivityResult", "onActivityResult ??????");
        if (resultCode == RESULT_OK) {

            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
            if (requestCode == PICK_FORM_ALBUM) {

                Log.v("PICK_FORM_ALBUM", "requestCode == PICK_FORM_ALBUM");
                photoUri = data.getData();
                chat_room_img.setImageURI(photoUri);

            }
        }
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

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
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
}

package com.example.seatreservation.MyPage;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.seatreservation.HomeActivity;
import com.example.seatreservation.Join;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.RegisterRequest;
import com.example.seatreservation.Request.UserEditRequest;
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

public class UserEdit extends Activity {

    private AlertDialog dialog;

    private EditText name;
    private Button saveBtn;
    private ImageView backBtn, profile_image;
    private String imageUrl;
    SessionManager sessionManager;

    Uri photoUri;

    private String ImagePath;
    private String ImageName;
    private String profileImg;

    private String basicPath;

    private static final int PICK_FORM_ALBUM = 1;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        photoUri = null;

        backBtn = findViewById(R.id.back_btn);
        profile_image = findViewById(R.id.profile_img);
        name = findViewById(R.id.nameText);
        saveBtn = findViewById(R.id.user_edit_save_btn);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String mProfile = user.get(sessionManager.PROFILE);
        String mIn_use = user.get(sessionManager.IN_USE);

        Uri basicUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.profile_img);
        basicPath = "drawable://" + R.drawable.profile_img;
        upLoadServerUri = "http://3.34.45.193/UploadToServer.php";//서버컴퓨터의 ip주소

        name.setText(mName);
        if(mProfile.equals("basic_image")){
            Glide.with(this).load(R.drawable.profile_img).override(130,130).into(profile_image);
        }
        else{
            imageUrl = "http://3.34.45.193" + mProfile;
            Glide.with(this).load(imageUrl).override(130,130).into(profile_image);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 프로필 사진 설정
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사진 관련 권한 허용
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED) {
                    } else {
                        ActivityCompat.requestPermissions(UserEdit.this, new String[]{
                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                }

                CharSequence info[] = new CharSequence[]{"사진 앨범 선택", "기본 이미지로 변경"};

                AlertDialog.Builder builder = new AlertDialog.Builder(UserEdit.this);
                builder.setTitle("업로드 이미지 선택");
                builder.setItems(info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
//                            case 0:
//                                // 사진 촬영
//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(intent, PICK_FORM_CAPTURE);
//
//                                break;
                            case 0:
                                // 앨범에서 선택
//                                Intent intent1 = new Intent();
//                                intent1.setType("image/*");
//                                intent1.setAction(Intent.ACTION_PICK);
//                                startActivityForResult(intent1, PICK_FORM_ALBUM);

                                Intent intent1 = new Intent(Intent.ACTION_PICK);
                                intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1, PICK_FORM_ALBUM);
                                break;
                            case 1:
                                // 기본 이미지로 변경
                                photoUri = basicUri;
                                profile_image.setImageURI(photoUri);
                                Toast.makeText(getApplicationContext(), "기본 이미지로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        //회원가입 버튼 클릭 시 각각 입력값의 유효성을 판별해주는 if문
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if(photoUri == basicUri){
                    String path = "drawable://" + R.drawable.profile_img;
                    String name = "basic_image";
                    profileImg = name;

                    ImagePath = path;
                    ImageName = name;
                }else if(photoUri != null){
                    String path = getPath(photoUri);
                    String name = getName(photoUri);
                    profileImg = "/Images/" + name;

                    ImagePath = path;
                    ImageName = name;
                } else {
                    profileImg = mProfile;
                }


                //editText에 입력되어있는 값을 get(가져온다)해온다
                String userName = name.getText().toString();

                //닉네임이 공백일 시
                if (userName.equals("")) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    name.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//정보수정에 성공한 경우
                                     sessionManager.editSession(profileImg, userName, mId, mIn_use);
                                    Toast.makeText(getApplicationContext(), "정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    if(ImagePath != null){
                                        new Thread(new Runnable() {
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                    }
                                                });
                                                if(ImagePath == basicPath){

                                                } else{
                                                    uploadFile(ImagePath);
                                                }
                                            }
                                        }).start();
                                    } else{
//                                        Toast.makeText(UserEdit.this, "이미지 경로 = null", Toast.LENGTH_SHORT).show();
                                    }
//                                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//                                    startActivity(i);
                                    finish();
                                }
                                else{//정보수정에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"정보수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    UserEditRequest userEditRequest = new UserEditRequest(mId, profileImg,userName, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UserEdit.this);
                    queue.add(userEditRequest);
                }
            }
        });

    }

    // 실제 경로 찾기(프로필 이미지 설정 시)
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // 파일명 찾기
    private String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    private String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("main onActivityResult", "onActivityResult 호출");
        if (resultCode == RESULT_OK) {

            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
            if (requestCode == PICK_FORM_ALBUM) {

                Log.v("PICK_FORM_ALBUM", "requestCode == PICK_FORM_ALBUM");
                photoUri = data.getData();
                profile_image.setImageURI(photoUri);

            }
//                if (requestCode == PICK_FORM_CAPTURE) {
//                    Bundle extras = data.getExtras();
//                    // Bitmap으로 컨버전
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                    // 이미지뷰에 Bitmap으로 이미지를 입력
//                    profile_image.setImageBitmap(imageBitmap);
//                }
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

//                            Toast.makeText(UserEdit.this, "File Upload Complete.",
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
//                        Toast.makeText(UserEdit.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(UserEdit.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;

        } // End else block
    }
}

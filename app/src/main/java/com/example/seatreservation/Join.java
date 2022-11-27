package com.example.seatreservation;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.Request.RegisterRequest;
import com.example.seatreservation.Request.ValidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class Join extends AppCompatActivity {

    private String TAGLOG = "ImageUploadLoG";

    CircleImageView profile_image;
    Uri photoUri;
    EditText nameEt, idEt, pwdEt, rePwdEt, emailEt, confirmEt;
    Button idCheckBtn, joinBtn;
    TextView nameCheckTv;
    private AlertDialog dialog;
    private boolean validate=false;
    boolean isNameCheck; // 닉네임 중복확인 (기본값 = false) -> 중복확인 완료 시 true 로 변환됨
    boolean isIdCheck; // 아이디 중복확인 (기본값 = false) -> 중복확인 완료 시 true 로 변환됨

//    private static final int PICK_FORM_CAPTURE = 0;
    private static final int PICK_FORM_ALBUM = 1;

    private String ImagePath;
    private String ImageName;
    private String profileImg;

    private String basicPath;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;


    //프로필 기본 이미지 uri 가져오는 메서드
    private String getURLForResource(int resId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resId).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        nameEt = findViewById(R.id.nameText);
        idEt = findViewById(R.id.idText);
        pwdEt = findViewById(R.id.passwordText);
        rePwdEt = findViewById(R.id.rePasswordText);
        emailEt = findViewById(R.id.emailText);
        idCheckBtn = findViewById(R.id.idValidateButton);
        joinBtn = findViewById(R.id.join_confirm_btn);

//        nameCheckTv = findViewById(R.id.nameCheck_tv);

        profile_image = findViewById(R.id.profile_img);

        isNameCheck = false;
        isIdCheck = false;

        Uri basicUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.profile_img);
        basicPath = "drawable://" + R.drawable.profile_img;
        photoUri = basicUri;

        upLoadServerUri = "http://3.34.45.193/UploadToServer.php";//서버컴퓨터의 ip주소
        
        //프로필 사진 관련 권한 허용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(Join.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        // 프로필 사진 설정
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence info[] = new CharSequence[]{"사진 앨범 선택", "기본 이미지로 변경"};


                AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
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

        //아이디 중복확인 버튼
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idEt.getText().toString();
                if(isIdCheck){
                    idEt.setEnabled(true);
//                    idEt.setBackgroundColor(Color.WHITE);
                    validate=false;
                    idCheckBtn.setText("중복 확인");
                    isIdCheck = false;
                } else {
                    if (validate) {
                        return;
                    }
                    if (userID.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                        dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    }
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                    dialog = builder.setMessage("사용 가능한 아이디입니다.")
                                            .setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();
                                    idEt.setEnabled(false);
//                                    idEt.setBackgroundColor(Color.parseColor("#DCDCDC"));
                                    validate = true;
                                    idCheckBtn.setText("재설정");
                                    isIdCheck = true; // 아이디 중복 확인 완료
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
                                    dialog = builder.setMessage("이미 사용 중인 아이디 입니다.")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Join.this);
                    queue.add(validateRequest);
                }
            }
        });

        //회원가입 버튼 클릭 시 각각 입력값의 유효성을 판별해주는 if문
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if(photoUri == basicUri){
                    String path = "drawable://" + R.drawable.profile_img;
                    String name = "basic_image";
                    profileImg = name;

                    ImagePath = path;
                    ImageName = name;
                }else {
                    String path = getPath(photoUri);
                    String name = getName(photoUri);
                    profileImg = "/Images/" + name;

                    ImagePath = path;
                    ImageName = name;
                }

                
                //editText에 입력되어있는 값을 get(가져온다)해온다
                String userID = idEt.getText().toString();
                final String userPass = pwdEt.getText().toString();
                String userName = nameEt.getText().toString();
                final String PassCk = rePwdEt.getText().toString();
                String userEmail = emailEt.getText().toString();

                //닉네임이 공백일 시
                if (userName.equals("")) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    nameEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //아이디가 공백일 시
                else if (userID.equals("")) { 
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    idEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //비밀번호가 공백일 시
                else if (userPass.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    pwdEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //비밀번호 확인이 공백일 시
                else if (PassCk.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    rePwdEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //비밀번호와 비밀번호 확인이 다를 경우
                else if (!userPass.equals(PassCk)) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    rePwdEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //이메일이 공백일 시
                else if(userEmail.equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                } //이메일의 형식이 아닐 경우
                else if( !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(getApplicationContext(), "이메일 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                else if(isIdCheck){
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//회원가입에 성공한 경우
                                    Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                                    if(ImagePath != null){
                                        new Thread(new Runnable() {
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                    }
                                                });
                                                if(ImagePath == basicPath){

                                                }else {
                                                    uploadFile(ImagePath);
                                                }
                                            }
                                        }).start();
                                    } else{
//                                        Toast.makeText(Join.this, "이미지 경로 = null", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            else{//회원가입에 실패한 경우
                                Toast.makeText(getApplicationContext(),"회원가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    RegisterRequest registerRequest = new RegisterRequest(profileImg, userID, userPass, userName, userEmail, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Join.this);
                    queue.add(registerRequest);
                }
                else {// 아이디 중복체크를 안했을 시 회원가입 불가능
                    Toast.makeText(getApplicationContext(),"아이디 중복확인을 해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //비밀번호 확인 실시간 유효성 검사
        rePwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = pwdEt.getText().toString();
                String confirm = rePwdEt.getText().toString();

                if(password.equals(confirm)){
                    pwdEt.setBackgroundColor(Color.parseColor("#BFF6C1"));
                    rePwdEt.setBackgroundColor(Color.parseColor("#BFF6C1"));
                } else if(confirm.equals("")){
                    pwdEt.setBackgroundColor(Color.WHITE);
                    rePwdEt.setBackgroundColor(Color.WHITE);
                }
                else{
                    pwdEt.setBackgroundColor(Color.parseColor("#FADEDC"));
                    rePwdEt.setBackgroundColor(Color.parseColor("#FADEDC"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Email 실시간 유효성 검사
        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = emailEt.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setBackgroundColor(Color.parseColor("#FADEDC"));
                } else if(email.equals("")){
                    emailEt.setBackgroundColor(Color.WHITE);
                }
                else{
                    emailEt.setBackgroundColor(Color.parseColor("#BFF6C1"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
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
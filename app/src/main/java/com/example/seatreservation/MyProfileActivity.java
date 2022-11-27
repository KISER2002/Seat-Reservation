package com.example.seatreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.seatreservation.MyPage.UserEdit;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    TextView userNameTv, userIdTv;
    CircleImageView profileImgIv;
    ImageView backBtn;
    Button editBtn;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);
        String profileImg = user.get(sessionManager.PROFILE);

        userNameTv = findViewById(R.id.profile_name);
        userIdTv = findViewById(R.id.profile_id);
        profileImgIv = findViewById(R.id.profile_profile_img);
        backBtn = findViewById(R.id.back_btn);
        editBtn = findViewById(R.id.profile_edit_btn);

        userNameTv.setText(mName);
//        userIdTv.setText(mId.substring(0,4) + "****");
        userIdTv.setText(mId);
        if(profileImg.equals("basic_image")){
            Glide.with(MyProfileActivity.this).load(R.drawable.profile_img).into(profileImgIv);
        }else {
            Glide.with(MyProfileActivity.this).load("http://3.34.45.193" + profileImg).into(profileImgIv);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MyProfileActivity.this, UserEdit.class);
                startActivity(intent1);
            }
        });



    }

}

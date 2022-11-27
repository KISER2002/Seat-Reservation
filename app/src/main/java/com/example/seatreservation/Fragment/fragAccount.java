package com.example.seatreservation.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.seatreservation.MyPage.UserEdit;
import com.example.seatreservation.MyProfileActivity;
import com.example.seatreservation.R;
import com.example.seatreservation.Seat.SeatList_Edit;
import com.example.seatreservation.SessionManager;

import java.util.HashMap;

public class fragAccount extends Fragment {
    private View view;
    Context context;

    private String TAG = "프래그먼트";

    private TextView name, email;
    private Button userEditBtn, profileBtn, seatEditBtn, settingBtn, logoutBtn;
    private ImageView profileImg;
    private String imageUrl;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_account, container, false);

        context = container.getContext();

        sessionManager = new SessionManager(context);

        profileImg = view.findViewById(R.id.profile_img);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        userEditBtn = view.findViewById(R.id.user_edit_btn);
        profileBtn = view.findViewById(R.id.user_profile_btn);
        seatEditBtn = view.findViewById(R.id.user_seat_edit_btn);
        settingBtn = view.findViewById(R.id.user_setting_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);

        userEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserEdit.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });

        seatEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SeatList_Edit.class);
                intent.putExtra("is_office", "no");
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();

        context = getContext();

        sessionManager = new SessionManager(context);

        profileImg = view.findViewById(R.id.profile_img);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.ID);
        String mProfile = user.get(sessionManager.PROFILE);

        name.setText(mName);
//        email.setText(mEmail.substring(0,4) + "****");
        email.setText(mEmail);
        if (mProfile.equals("basic_image")) {
            Glide.with(context).load(R.drawable.profile_img).override(130, 130).into(profileImg);
        } else {
            imageUrl = "http://3.34.45.193" + mProfile;
            Glide.with(context).load(imageUrl).override(130, 130).into(profileImg);
        }
    }

}

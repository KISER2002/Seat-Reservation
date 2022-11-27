package com.example.seatreservation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "LOGIN_MAINTAIN";
    public static final String PROFILE = "profileImg";
    public static final String NAME = "userName";
    public static final String ID = "userID";
    public static final String IN_USE = "in_use";


    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String profile, String name, String id, String in_use){
        editor.putBoolean(PREF_NAME, true);
        editor.putString(PROFILE, profile);
        editor.putString(NAME, name);
        editor.putString(ID, id);
        editor.putString(IN_USE, in_use);
        editor.apply();
    }

    public void editSession(String profile, String name, String id, String in_use){
//        editor.clear();
//        editor.commit();
        editor.putBoolean(PREF_NAME, true);
        editor.putString(PROFILE, profile);
        editor.putString(NAME, name);
        editor.putString(ID, id);
        editor.putString(IN_USE, in_use);
        editor.apply();
    }

    public void loginMaintain(){
        editor.putBoolean("LOGIN_MAINTAIN", true);
        editor.apply();
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if(this.isLogin()){
            Intent i = new Intent(context, HomeActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }else{

        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(PROFILE, sharedPreferences.getString(PROFILE, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(IN_USE, sharedPreferences.getString(IN_USE, null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        ((HomeActivity)context).finish();
        Toast.makeText(((HomeActivity) context).getApplicationContext(),
                "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

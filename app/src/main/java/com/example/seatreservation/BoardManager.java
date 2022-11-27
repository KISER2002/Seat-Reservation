package com.example.seatreservation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;

public class BoardManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "BOARD";
    public static final String PROFILE = "profileImg";
    public static final String NAME = "userName";
    public static final String ID = "userID";


    public BoardManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String profile, String name, String id){
        editor.putBoolean(PREF_NAME, true);
        editor.putString(PROFILE, profile);
        editor.putString(NAME, name);
        editor.putString(ID, id);
        editor.apply();
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(PROFILE, sharedPreferences.getString(PROFILE, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(ID, sharedPreferences.getString(ID, null));

        return user;
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }
}

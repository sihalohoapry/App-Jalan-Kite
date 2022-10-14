package com.development.jalankite.preference;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class PrefManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Context context;

    private  static final String IS_LOGIN = "isLoggedIn";

    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    public PrefManager(Context _context) {
        context = _context;
        sharedPreferences = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String name, String email) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);

        editor.apply();
    }

    public HashMap<String, String> getUser(){
        HashMap<String, String> userData = new HashMap<String, String >();
        userData.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        userData.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        return userData;
    }

    public boolean checkLogin(){
        if (sharedPreferences.getBoolean(IS_LOGIN, false)){
            return true;
        } else
            return false;
    }

    public void logout(){
        editor.clear();
        editor.apply();
    }

}

//        get data
//        PrefManager prefManager = new PrefManager(Activity.this);
//        HashMap<String, String> userDetail = prefManager.getUser();
//        String name = prefManager.get(PrefManager.KEY_NAME);

//set data
//    PrefManager prefManager = new PrefManager(Activity.this);
//    prefManager.createLoginSeason(string, string);
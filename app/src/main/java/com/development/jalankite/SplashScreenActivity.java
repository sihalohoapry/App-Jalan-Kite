package com.development.jalankite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.development.jalankite.preference.PrefManager;
import com.development.jalankite.ui.home.MainActivity;
import com.development.jalankite.ui.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    SplashScreenActivity binding;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        prefManager = new PrefManager(SplashScreenActivity.this);
        Boolean isLogin = prefManager.checkLogin();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin){
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        },2000);
    }
}
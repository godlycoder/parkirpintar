package com.example.ribani.parkirpintar.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.feature.landing.LandingActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Hawk.init(this).setEncryption(new NoEncryption()).build();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preferences.getUid()!=null) {
                    startActivity(new Intent(SplashActivity.this, BridgeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LandingActivity.class));
                }

                finish();
            }
        }, 500L);
    }
}

package com.example.ribani.parkirpintar.feature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
                String flag = prefs.getString("killed", null);

                if(flag!=null && flag.equals("yes")) {
                    Intent notifyIntent = new Intent(SplashActivity.this, ResponseActivity.class);
                    startActivity(notifyIntent);
                    Log.d("onDoing", "I'm on good refered!");
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }

                finish();

            }
        }, 500L);


    }
}

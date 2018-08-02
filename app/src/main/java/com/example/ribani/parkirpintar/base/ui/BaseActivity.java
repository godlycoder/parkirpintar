package com.example.ribani.parkirpintar.base.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity{

    public Activity activity;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        activity = this;

        ButterKnife.bind(this);
    }
}

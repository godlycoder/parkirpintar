package com.example.ribani.parkirpintar.presenter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ribani.parkirpintar.base.ui.BasePresenter;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.model.ParkedStatus;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ResponsePresenter extends BasePresenter<ResponseView> {

    public static final String COUNTINGDOWN_TIME = "COUNTINGDOWN_TIME";
    public static final String ESTIMATE_TIME = "ESTIMATE_TIME";
    public static final String TIME_RESPONSE = "TIME_RESPONSE";
    public static final String COUNTDOWN_FINISH = "COUNTDOWN_FINISH";
    public static final String TAKING_PLACE_COUNTINGDOWN_TIME = "TAKING_PLACE_COUNTINGDOWN_TIME";
    public static final String RESERVED_COUNTINGDOWN_TIME = "RESERVED_COUNTINGDOWN_TIME";
    public static final String TAKING_PLACE = "TAKING_PLACE";
    public static final String RESERVED = "RESERVED";
    public static final String RESERVED_RESULT = "RESERVED_RESULT";

    long millisUntilFinished;
    private boolean getTimeStatus = true;

    public ResponsePresenter(ResponseView view) {
        super.attachView(view);
    }

    public void doCountdownTimer(String tag, long millis) {

        setTimeMillis(millis);

        switch (tag) {
            case TAKING_PLACE :
                view.onSuccess(getCountdownFormat(), TAKING_PLACE_COUNTINGDOWN_TIME);
                break;
            case RESERVED :
                view.onSuccess(getCountdownFormat(), RESERVED_COUNTINGDOWN_TIME);
                break;
        }


        /*if ((millis/1000L) == 0L) {

            view.onSuccess(0 ,COUNTDOWN_FINISH);
        }*/
    }

    public void doCountupTimer(DatabaseReference mRef, Context mContext, long millis) {
        ParkedStatus parkedStatus = new ParkedStatus();

        if (getTimeStatus == true) {
            Date enterPark = Calendar.getInstance().getTime();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String getTime = timeFormat.format(enterPark);
            parkedStatus.setEnterTime(getTime);

            getTimeStatus = false;
        }
        setTimeMillis(millis);
        parkedStatus.setCountingTime(getTimerFormat());

        view.onSuccess(parkedStatus, ESTIMATE_TIME);
    }

    public void doSendReserveTime() {

    }

    private void setTimeMillis(long millisUntilFinished) {
        this.millisUntilFinished = millisUntilFinished;
    }

    private String getCountdownFormat() {
        long seconds = millisUntilFinished/1000;
        long minutes = seconds/60;
        long hours = minutes/60;

        if (minutes > 0) {
            seconds = seconds%60;
        }

        if (hours > 0) {
            minutes = minutes%60;
        }

        String time = formatNumber(hours)+":"+formatNumber(minutes)+":"+formatNumber(seconds);

        return time;
    }

    private String getTimerFormat() {
        long seconds = millisUntilFinished;
        long minutes = seconds/60;
        long hours = minutes/60;

        if (minutes > 0) {
            seconds = seconds%60;
        }

        if (hours > 0) {
            minutes = minutes%60;
        }

        String time = formatNumber(hours)+":"+formatNumber(minutes)+":"+formatNumber(seconds);

        return time;
    }

    private String formatNumber(long value){
        if(value < 10)
            return "0" + value;
        return value + "";
    }
}

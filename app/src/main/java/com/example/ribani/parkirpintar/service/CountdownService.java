package com.example.ribani.parkirpintar.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.model.ReserveRecorded;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class CountdownService extends Service {

    private CountDownTimer countDownTimer;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference("blok_parkir");
    private DatabaseReference mRefPay = database.getReference();

    private int status;
    private long seconds = 0L;
    boolean running = true;
    private int countHour = 0;


    private Bundle bundleSender;
    private Handler handler = new Handler();
    private Runnable r;
    private String counterMode;
    private boolean statCheck = false;

    public CountdownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case ResponsePresenter.TAKING_PLACE :
                Log.d("Service Running", "Mantap");
                counterMode = intent.getAction();
                doCountdown(intent.getAction(), intent);
                break;
            case ResponsePresenter.RESERVED :
                counterMode = intent.getAction();
                doCountdown(intent.getAction(), intent);
                Log.d("Service Running", intent.getAction());
                break;
            case ResponsePresenter.PARKED :
                Log.d("Parked", "Asup");
                bundleSender = intent.getExtras();
                counterMode = intent.getAction();
                doCountup(intent.getAction(), intent);
                break;
            case ResponsePresenter.PAYMENT :
                counterMode = intent.getAction();
                doPayment();

        }

        return START_STICKY;
    }

    private void doPayment() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(ResponseActivity.PAYMENT_RESULT);
        sendBroadcast(sendIntent);
    }


    private void doCountdown(final String tag, final Intent intent) {
        Bundle bundle = intent.getExtras();
        final long millisInFuture = bundle.getLong(ResponseActivity.COUNTDOWN, 0);
        final String park = bundle.getString(ResponseActivity.PARK);

        Log.d("Service", String.valueOf(millisInFuture));
        final Intent sendIntent = new Intent();
        final Bundle bundleBr = new Bundle();

        countDownTimer = new CountDownTimer(millisInFuture + 1000, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sendValue = millisUntilFinished - 1000;

                checkStatus(park);

                switch (tag) {
                    case ResponsePresenter.TAKING_PLACE :
                        Log.d("Service", "TAKING_PLACE");
                        sendIntent.setAction(ResponseActivity.TAKING_PLACE_RESULT);
                        if (status == 2) {
                            bundleBr.putString(ResponseActivity.GO, ResponseActivity.GO_PARKED);
                        } else {
                            bundleBr.putLong(ResponseActivity.MILLIS, sendValue);
                        }
                        sendIntent.putExtras(bundleBr);
                        sendBroadcast(sendIntent);
                        break;
                    case ResponsePresenter.RESERVED :
                        sendIntent.setAction(ResponseActivity.RESERVED_RESULT);
                        if (status == 3) {
                            bundleBr.putString(ResponseActivity.GO, ResponseActivity.GO_TAKING_PLACE);
                            mRef.child(park).child("reserved").removeValue();
                        } else {
                            bundleBr.putLong(ResponseActivity.MILLIS, sendValue);
                        }
                        sendIntent.putExtras(bundleBr);
                        sendBroadcast(sendIntent);
                        Log.d("Service", "RESERVED");
                        break;

                }
            }

            @Override
            public void onFinish() {
                this.cancel();
                Log.d("onDestroyService", "onFinish() Countdown Timer");
            }
        }.start();
    }

    private void checkStatus(String park){
        mRef.child(park).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    private void doCountup(String tag, Intent intent) {
        final Intent sendIntent = new Intent();
        Bundle bundle = intent.getExtras();
        final Bundle bundleBr = new Bundle();
        final String park = bundle.getString(ResponseActivity.PARK);
        final String recordedKey = bundle.getString(ResponseActivity.RECORDED_KEY);

        sendIntent.setAction(ResponseActivity.PARKED_RESULT);

        r = new Runnable() {
            @Override
            public void run() {
                checkStatus(park);


                bundleBr.putLong(ResponseActivity.MILLIS, seconds);
                sendIntent.putExtras(bundleBr);
                sendBroadcast(sendIntent);

                if(running) {
                    seconds++;
                }

                if (countHour == 3600) {
                    mRefPay.child("bayar").child(Preferences.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int totalBayar = dataSnapshot.child("totalBayar").getValue(Integer.class);
                            int lama = dataSnapshot.child("transaksi").child(recordedKey).child("lama").getValue(Integer.class);
                            int biaya = dataSnapshot.child("transaksi").child(recordedKey).child("biaya").getValue(Integer.class);

                            mRefPay.child("bayar").child(Preferences.getUid()).child("totalBayar").setValue(totalBayar+3000);
                            mRefPay.child("bayar").child(Preferences.getUid()).child("transaksi").child(recordedKey)
                                    .child("lama").setValue(lama+60);
                            mRefPay.child("bayar").child(Preferences.getUid()).child("transaksi").child(recordedKey)
                                    .child("biaya").setValue(biaya+3000);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    countHour = 0;
                } else {
                    countHour++;
                }

                if (status==0) {
                    if (!statCheck) {
                        statCheck = true;
                    } else {
                        bundleBr.putString(ResponseActivity.GO, ResponseActivity.GO_PAYMENT);
                        sendIntent.putExtras(bundleBr);
                        sendBroadcast(sendIntent);
                    }
                }

                handler.postDelayed(this, 1000);
            }
        };

        switch (tag) {
            case ResponsePresenter.PARKED :
                handler.post(r);
                break;
            case ResponsePresenter.FINISH_PARKED :
                handler.removeCallbacks(r);
                break;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switch (counterMode) {
            case ResponsePresenter.TAKING_PLACE :
                Log.d("onDestroyService", "Countdown TAKING PLACE Stop");
                countDownTimer.cancel();
                break;
            case ResponsePresenter.RESERVED :
                Log.d("onDestroyService", "Countdown RESERVED Stop");
                countDownTimer.cancel();
                break;
            case ResponsePresenter.PARKED :
                handler.removeCallbacks(r);
                break;

        }


    }

}

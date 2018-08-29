package com.example.ribani.parkirpintar.presenter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.base.ui.BasePresenter;

import com.example.ribani.parkirpintar.model.Bayar;
import com.example.ribani.parkirpintar.model.ParkedStatus;
import com.example.ribani.parkirpintar.model.ReserveRecorded;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

public class ResponsePresenter extends BasePresenter<ResponseView> {

    public static final String ESTIMATE_TIME = "ESTIMATE_TIME";
    public static final String COUNTDOWN_FINISH = "COUNTDOWN_FINISH";
    public static final String TAKING_PLACE_COUNTINGDOWN_TIME = "TAKING_PLACE_COUNTINGDOWN_TIME";
    public static final String RESERVED_COUNTINGDOWN_TIME = "RESERVED_COUNTINGDOWN_TIME";
    public static final String TAKING_PLACE = "TAKING_PLACE";
    public static final String RESERVED = "RESERVED";
    public static final String PARKED = "PARKED";
    public static final String PAYMENT = "PAYMENT";
    public static final String FINISH_PARKED = "FINISH_PARKED";
    public static final String PAYMENT_CONFIRM = "PAYMENT_CONFIRM";
    public static final String FINISH_PAYMENT = "FINISH_PAYMENT";
    public static final String INIT_PARKED = "INIT_PARKED";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    long millisUntilFinished;
    long seconds;
    long minutes;
    long hours;
    int timeSender = 0;

    private boolean getTimeStatus = true;

    public ResponsePresenter(ResponseView view) {
        super.attachView(view);
    }

    public void doCountdownTimer(String tag, long millis, String park) {

        setTimeMillis(millis);

        switch (tag) {
            case TAKING_PLACE :
                view.onSuccess(getCountdownFormat(), TAKING_PLACE_COUNTINGDOWN_TIME);
                break;
            case RESERVED :
                view.onSuccess(getCountdownFormat(), RESERVED_COUNTINGDOWN_TIME);
                if (timeSender == 60) {
                    mRef.child("blok_parkir").child(park).child("reserved").child("reserveEst").setValue(minutes);
                    timeSender = 0;
                } else {
                    timeSender++;
                    Log.d("Sender", String.valueOf(timeSender));
                }
                break;
        }


        if ((millis/1000L) == 0L) {

            doRemoveReservedRequest(mRef, park, tag);
            view.onSuccess(null ,COUNTDOWN_FINISH);
        }

    }

    public void doCountupTimer(long millis) {
        ParkedStatus parkedStatus = new ParkedStatus();

        if (getTimeStatus == true) {
            Log.d("Get Time Status", "Asup");
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            parkedStatus.setEnterTime(String.valueOf(hour+"."+minute));

            getTimeStatus = false;
        }
        setTimeMillis(millis);
        parkedStatus.setCountingTime(getTimerFormat());

        view.onSuccess(parkedStatus, ESTIMATE_TIME);
    }

    private void doRemoveReservedRequest(DatabaseReference mRef, String park, String tag) {
        mRef.child("blok_parkir").child(park).child("reserved").removeValue();
        mRef.child("blok_parkir").child(park).child("status").setValue(0);
        mRef.child("reservasi").child(Preferences.getUid()).removeValue();
        mRef.child("order").child(Preferences.getUid()).removeValue();
    }

    public void doFinishReserved(String park) {
        doRemoveReservedRequest(mRef, park, RESERVED);
        view.onSuccess(null, COUNTDOWN_FINISH);
    }

    private void setTimeMillis(long millisUntilFinished) {
        this.millisUntilFinished = millisUntilFinished;
    }

    private String getCountdownFormat() {
        seconds = millisUntilFinished/1000;
        minutes = seconds/60;
        hours = minutes/60;

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
        seconds = millisUntilFinished;
        minutes = seconds/60;
        hours = minutes/60;

        if (minutes > 0) {
            seconds = seconds%60;
        }

        if (hours > 0) {
            minutes = minutes%60;
        }

        String time = formatNumber(hours)+" jam "+formatNumber(minutes)+" menit";

        return time;
    }

    private String formatNumber(long value){
        if(value < 10)
            return "0" + value;
        return value + "";
    }

    public void doPayment() {

        mRef.child("bayar").child(Preferences.getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bayar bayar = dataSnapshot.getValue(Bayar.class);
                List<ReserveRecorded> transaksiList = new ArrayList<>();

                for (DataSnapshot transSnapshot : dataSnapshot.child("transaksi").getChildren()) {
                    ReserveRecorded transaksi = transSnapshot.getValue(ReserveRecorded.class);

                    transaksiList.add(transaksi);
                }

                bayar.setTransaksiList(transaksiList);

                view.onSuccess(bayar, PAYMENT_CONFIRM);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("bayar").child(Preferences.getUid()).child("status")
                .addValueEventListener(statusPaymentValue);
    }

    private ValueEventListener statusPaymentValue = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int status = dataSnapshot.getValue(Integer.class);

            if (status == 1) {

                mRef.child("bayar").child(Preferences.getUid()).child("transaksi")
                        .removeValue();

                mRef.child("bayar").child(Preferences.getUid())
                        .child("status").setValue(0);
                mRef.child("bayar").child(Preferences.getUid())
                        .child("totalBayar").setValue(0);
                mRef.child("bayar").child(Preferences.getUid())
                        .child("totalOrder").setValue(0);

                view.onSuccess(null, FINISH_PAYMENT);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}};

    public void doRemoveStatusPaymentListener() {
        mRef.child("bayar").child(Preferences.getUid()).child("status")
                .removeEventListener(statusPaymentValue);
    }

    public void doParked(String blok) {
        String recordKey = mRef.push().getKey();

        final ReserveRecorded recorded = new ReserveRecorded();

        recorded.setTipeOrder(2);
        recorded.setBlok(blok);
        recorded.setBiaya(3000);
        recorded.setLama(60);

        mRef.child("bayar").child(Preferences.getUid()).child("transaksi").child(recordKey).setValue(recorded);
        mRef.child("bayar").child(Preferences.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalBayar = dataSnapshot.child("totalBayar").getValue(Integer.class);
                int totalOrder = dataSnapshot.child("totalOrder").getValue(Integer.class);

                mRef.child("bayar").child(Preferences.getUid()).child("totalBayar").setValue(totalBayar+recorded.getBiaya());
                mRef.child("bayar").child(Preferences.getUid()).child("totalOrder").setValue(totalOrder+1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("order").child(Preferences.getUid()).removeValue();

        view.onSuccess(recordKey, INIT_PARKED);
    }
}

package com.example.ribani.parkirpintar.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;

import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.base.ui.BasePresenter;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.model.Bayar;
import com.example.ribani.parkirpintar.model.MainItem;
import com.example.ribani.parkirpintar.model.Request;
import com.example.ribani.parkirpintar.model.ReserveRecorded;
import com.example.ribani.parkirpintar.model.Reserved;
import com.example.ribani.parkirpintar.model.TakingPlace;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter<ResponseView>{

    public static final String DATA_CHANGE = "DATA_CHANGE";
    public static final String TRIM_TEXT = "TRIM_TEXT";
    public static final String RESERVED = "RESERVED";
    public static final String TIME_RESERVE = "TIME_RESERVE";
    public static final String NUM_PARK = "NUM_PARK";
    public static final String PARK_NUMBER = "PARK_NUMBER";
    public static final String RESERVED_ERROR = "RESERVED_ERROR";
    public static final String BLOK = "BLOK";

    List<MainItem> items;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public MainPresenter(ResponseView view) {
        super.attachView(view);
    }

    private ValueEventListener updateListEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            items = new ArrayList<>();
            for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                int status = menuSnapshot.child("status").getValue(Integer.class);
                if (status < 2) {
                    MainItem item = menuSnapshot.getValue(MainItem.class);
                    item.setPark(menuSnapshot.getKey());
                    items.add(item);
                }
            }
            view.onSuccess(items, DATA_CHANGE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            view.onFailed(DATA_CHANGE);
        }
    };

    public void doLoadListPark() {
        mRef.child("blok_parkir").addValueEventListener(updateListEventListener);
    }

    public void doRemoveUpdateList() {
        mRef.child("blok_parkir").removeEventListener(updateListEventListener);
    }

    public void doTakingPlace(String park, String blok) {
        TakingPlace taking = new TakingPlace();
        taking.setPark(park);
        taking.setBlok(blok);

        mRef.child("blok_parkir").child(park).child("status").setValue(3);
        mRef.child("order").child(Preferences.getUid()).child("tipe").setValue(3);
        mRef.child("order").child(Preferences.getUid()).child("blok").setValue(park);

        view.onSuccess(taking, NUM_PARK);
    }

    public void doTrimText(Editable s) {
        String result = s.toString().replaceAll(" ", "");
        if (!s.toString().equals(result)) {
            view.onSuccess(result, TRIM_TEXT);
        }
    }

    public void doReserve(final DatabaseReference mRef, final Context mContext, final String park
            , String blok, final int timeReserve) {

        final Reserved reserved = new Reserved();
        final ReserveRecorded recorded = new ReserveRecorded();

        reserved.setReserveEst(geTimeEstimate(timeReserve));

        recorded.setLama(geTimeEstimate(timeReserve));
        recorded.setBiaya(getParkingFee(timeReserve));
        recorded.setTipeOrder(1);
        recorded.setBlok(blok);

        Request request = new Request();
        request.setLayout(ResponsePresenter.RESERVED);
        request.setTimeReserve(geTimeEstimate(timeReserve));
        request.setPark(park);

        mRef.child("blok_parkir").child(park).child("status").setValue(1);
        mRef.child("blok_parkir").child(park).child("reserved").setValue(reserved);
        mRef.child("reservasi").child(Preferences.getUid()).setValue(recorded);
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

        mRef.child("bayar").child(Preferences.getUid()).child("transaksi").push().setValue(recorded);
        mRef.child("order").child(Preferences.getUid()).child("tipe").setValue(1);
        mRef.child("order").child(Preferences.getUid()).child("blok").setValue(park);

        view.onSuccess(request, RESERVED);

    }

    private int getParkingFee(int ops) {
        switch (ops) {
            case 1 :
                return 5000;
            case 2 :
                return 10000;
            case 3 :
                return 20000;
            default:
                return 0;

        }
    }

    private int geTimeEstimate(int ops) {
        switch (ops) {
            case 1 :
                return 30;
            case 2 :
                return 60;
            case 3 :
                return 120;
            default:
                return 0;

        }
    }
}

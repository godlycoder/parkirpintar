package com.example.ribani.parkirpintar.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;

import com.example.ribani.parkirpintar.base.ui.BasePresenter;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.model.MainItem;
import com.example.ribani.parkirpintar.model.Reserved;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter<ResponseView>{

    public static final String DATA_CHANGE = "DATA_CHANGE";
    public static final String TRIM_TEXT = "TRIM_TEXT";
    public static final String RESERVED = "RESERVED";
    public static final String TIME_RESERVE = "TIME_RESERVE";
    public static final String TAKING_PLACE = "TAKING_PLACE";

    public MainPresenter(ResponseView view) {
        super.attachView(view);
    }

    public void doLoadListPark(DatabaseReference mRef) {
        final List<MainItem> items = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    int status = menuSnapshot.child("status").getValue(Integer.class);
                    if (status != 2) {
                        MainItem item = menuSnapshot.getValue(MainItem.class);
                        items.add(item);
                    }
                }
                view.onSuccess(items, DATA_CHANGE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onFailed(DATA_CHANGE);
            }
        });
    }

    public void doTrimText(Editable s) {
        String result = s.toString().replaceAll(" ", "");
        if (!s.toString().equals(result)) {
            view.onSuccess(result, TRIM_TEXT);
        }
    }

    public void doReserve(final DatabaseReference mRef, final Context mContext, final String block, String nopol, final int timeReserve) {
        final Reserved reserved = new Reserved();

        reserved.setNopol(nopol);
        reserved.setReserveEst(geTimeEstimate(timeReserve));

        mRef.child("blok_parkir").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numberPark = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String snapBlock = snapshot.child("nama_blok").getValue(String.class);

                    if (snapBlock.equals(block)) {
                        numberPark = snapshot.getKey();
                        mRef.child(numberPark).child("reserved").setValue(reserved);
                    }
                }
                Intent intent = new Intent(mContext, ResponseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(TIME_RESERVE, timeReserve);
                bundle.putString(ResponseActivity.CHOOSE_LAYOUT, ResponsePresenter.RESERVED);

                intent.putExtras(bundle);

                view.onSuccess(intent, RESERVED);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int geTimeEstimate(int ops) {
        switch (ops) {
            case 0 :
                return 30;
            case 1 :
                return 60;
            case 2 :
                return 120;
            default:
                return 0;

        }
    }
}

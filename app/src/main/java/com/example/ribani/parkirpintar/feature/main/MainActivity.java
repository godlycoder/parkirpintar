package com.example.ribani.parkirpintar.feature.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.adapter.MainAdapter;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.reserve.ReserveActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.listener.CustomItemClickListener;
import com.example.ribani.parkirpintar.model.MainItem;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<MainPresenter> implements ResponseView, CustomItemClickListener {

    public static final String BLOCK = "BLOCK";
    @BindView(R.id.rv_main) RecyclerView rvMain;
    @BindView(R.id.tv_slot_count) TextView tvSlotCount;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    private MainAdapter adapter;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter.doLoadListPark(mRef);

    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case MainPresenter.DATA_CHANGE:
                List<MainItem> response = (List<MainItem>) obj;
                final int dataSize = response.size();

                tvSlotCount.setText(String.valueOf(response.size()));
                adapter = new MainAdapter(this, response, this);
                rvMain.setAdapter(adapter);
                rvMain.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onFailed(String tag) {

    }

    @Override
    public void onItemClick(View v, final String block) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(R.array.select_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent1 = new Intent(MainActivity.this, ReserveActivity.class);
                        intent1.putExtra(BLOCK, block);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this, ResponseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(ResponseActivity.CHOOSE_LAYOUT, ResponsePresenter.TAKING_PLACE);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
        });

        builder.show();
    }
}

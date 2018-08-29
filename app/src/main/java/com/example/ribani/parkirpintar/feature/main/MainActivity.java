package com.example.ribani.parkirpintar.feature.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.adapter.MainAdapter;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.landing.LandingActivity;
import com.example.ribani.parkirpintar.feature.reserve.ReserveActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.listener.CustomItemClickListener;
import com.example.ribani.parkirpintar.model.MainItem;
import com.example.ribani.parkirpintar.model.TakingPlace;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MvpActivity<MainPresenter> implements ResponseView, CustomItemClickListener {

    public static final String BLOCK = "BLOCK";
    public static final String PARK = "PARK";

    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.tv_slot_count)
    TextView tvSlotCount;


    private MainAdapter adapter;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter.doLoadListPark();

    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case MainPresenter.DATA_CHANGE:
                List<MainItem> response = (List<MainItem>) obj;

                adapter = new MainAdapter(this, response, this);
                tvSlotCount.setText(String.valueOf(response.size()));
                rvMain.setAdapter(adapter);
                rvMain.setLayoutManager(new LinearLayoutManager(this));
                break;
            case MainPresenter.NUM_PARK:
                TakingPlace numPark = (TakingPlace) obj;

                Intent intent2 = new Intent(MainActivity.this, ResponseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MainPresenter.PARK_NUMBER, numPark.getPark());
                bundle.putString(MainPresenter.BLOK, numPark.getBlok());
                bundle.putString(ResponseActivity.CHOOSE_LAYOUT, ResponsePresenter.TAKING_PLACE);
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish();
        }
    }

    @Override
    public void onFailed(String tag) {

    }

    @Override
    public void onItemClick(View v, final String park, final String block, int status) {
        if (status != 1) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setItems(R.array.select_item, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent1 = new Intent(MainActivity.this, ReserveActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(MainPresenter.BLOK, block);
                            bundle.putString(PARK, park);
                            intent1.putExtras(bundle);
                            startActivity(intent1);
                            break;
                        case 1:
                            presenter.doTakingPlace(park, block);
                            break;
                    }
                }
            });

            builder.show();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.reserved_toast_info)
                    , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity onDestroy", "Masuk");
        presenter.doRemoveUpdateList();
    }

    @OnClick(R.id.iv_user)
    public void onIvUserClicked() {
        Preferences.logout();
        startActivity(new Intent(MainActivity.this, LandingActivity.class));
        finish();
    }
}

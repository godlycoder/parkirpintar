package com.example.ribani.parkirpintar.feature.response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ribani.parkirpintar.CountertService;
import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.example.ribani.parkirpintar.service.CountdownService;
import com.example.ribani.parkirpintar.view.ResponseView;

import butterknife.BindView;

public class ResponseActivity extends MvpActivity<ResponsePresenter> implements ResponseView {

    public static final String CHOOSE_LAYOUT = "CHOOSE_LAYOUT";
    public static final String ACTION_COUNTDOWN = "ACTION_COUNTDOWN";
    public static final String MILLIS = "MILLIS";
    public static final String COUNTDOWN = "COUNTDOWN";
    public static final String RESERVED_RESULT = "RESERVED_RESULT";
    public static final String TAKING_PLACE_RESULT = "TAKING_PLACE_RESULT";
    public static final String TAKING_PLACE = "TAKING_PLACE";
    public static final String RESERVED = "RESERVED";

    private IntentFilter intentFilter;


    @BindView(R.id.taking_place_layout) RelativeLayout takingPlaceLayout;
    @BindView(R.id.parked_layout) RelativeLayout parkedLayout;
    @BindView(R.id.tv_time_remains_taking) TextView tvTimeRemainsTaking;
    @BindView(R.id.tv_time_remains_reserve) TextView tvTimeRemainsReserve;
    @BindView(R.id.tv_estimate_parked) TextView tvEstimateParked;
    @BindView(R.id.tv_park_entered) TextView tvParkEntered;
    @BindView(R.id.reserve_layout) RelativeLayout reserveLayout;

    private Bundle bundle;

    @Override
    protected ResponsePresenter createPresenter() {
        return new ResponsePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            initLayout(bundle.getString(CHOOSE_LAYOUT));
        }
    }

    public void initLayout(String tag) {
        switch (tag) {
            case ResponsePresenter.TAKING_PLACE :
                takingPlaceLayout.setVisibility(View.VISIBLE);
                intentFilter = new IntentFilter(TAKING_PLACE_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentTakingPlace = new Intent(ResponseActivity.this, CountdownService.class);
                intentTakingPlace.setAction(ResponsePresenter.TAKING_PLACE);
                intentTakingPlace.putExtra(COUNTDOWN, 120000L);
                startService(intentTakingPlace);
                break;
            case ResponsePresenter.RESERVED :
                reserveLayout.setVisibility(View.VISIBLE);
                intentFilter = new IntentFilter(RESERVED_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentReserve = new Intent(ResponseActivity.this, CountertService.class);
                intentReserve.setAction(ResponsePresenter.RESERVED);
                intentReserve.putExtra(COUNTDOWN, bundle.getInt(MainPresenter.RESERVED)*1000L);
                startService(intentReserve);
                Log.d("intilLayout", ResponsePresenter.RESERVED);
                break;
        }
    }

    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long x = intent.getLongExtra(MILLIS, 0);

            switch (intent.getAction()) {
                case TAKING_PLACE_RESULT :
                    Log.d("BroadcastReceiver", "TAKING_PLACE_RESULT");
                    presenter.doCountdownTimer(ResponsePresenter.TAKING_PLACE, x);
                    break;
                case RESERVED_RESULT :
                    presenter.doCountdownTimer(ResponsePresenter.RESERVED, x);
                    Log.d("BroadcastReceiver", "TAKING_PLACE_RESULT");
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("Response onDoing", "I'm on stop");
        onGoingActivivity(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("Response onDoing", "I'm on back pressed");

            onGoingActivivity(true);
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("Response onDoing", "I'm on destroy");
        onGoingActivivity(false);
        unregisterReceiver(responseReceiver);
    }

    private void onGoingActivivity(boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences("X",MODE_PRIVATE).edit();

        if (status == true) {
            editor.putString("killed", "yes");
            editor.commit();
        } else {
            Log.d("onGoingActivivity", "No Killed");
            editor.putString("killed", "no");
            editor.commit();
        }
    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case ResponsePresenter.TAKING_PLACE_COUNTINGDOWN_TIME :
                String timeRemainsTp = (String) obj;
                tvTimeRemainsTaking.setText(timeRemainsTp);
                break;
            case ResponsePresenter.RESERVED_COUNTINGDOWN_TIME :
                String timeRemainsR = (String) obj;
                tvTimeRemainsReserve.setText(timeRemainsR);
                break;
            case ResponsePresenter.COUNTDOWN_FINISH :
                onGoingActivivity(false);
                startActivity(new Intent(ResponseActivity.this, MainActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onFailed(String tag) {

    }
}
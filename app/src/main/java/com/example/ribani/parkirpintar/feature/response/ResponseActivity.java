package com.example.ribani.parkirpintar.feature.response;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.adapter.PaymentConfirmAdapter;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.model.Bayar;
import com.example.ribani.parkirpintar.model.ParkedStatus;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.example.ribani.parkirpintar.service.CountdownService;
import com.example.ribani.parkirpintar.view.ResponseView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResponseActivity extends MvpActivity<ResponsePresenter> implements ResponseView {

    public static final String CHOOSE_LAYOUT = "CHOOSE_LAYOUT";
    public static final String MILLIS = "MILLIS";
    public static final String COUNTDOWN = "COUNTDOWN";
    public static final String RESERVED_RESULT = "RESERVED_RESULT";
    public static final String TAKING_PLACE_RESULT = "TAKING_PLACE_RESULT";
    public static final String TAKING_PLACE = "TAKING_PLACE";
    public static final String RESERVED = "RESERVED";
    public static final String PARK = "PARK";
    public static final String GO_PARKED = "GO_PARKED";
    public static final String GO_TAKING_PLACE = "GO_TAKING_PLACE";
    public static final String PARKED_RESULT = "PARKED_RESULT";
    public static final String GO = "GO";
    public static final String GO_PAYMENT = "GO_PAYMENT";
    public static final String PAYMENT_RESULT = "PAYMENT_RESULT";
    public static final String BLOK = "BLOK";
    public static final String RECORDED_KEY = "RECORDED_KEY";
    @BindView(R.id.payment_confirm_layout)
    RelativeLayout paymentConfirmLayout;
    @BindView(R.id.rv_rincian_bayar)
    RecyclerView rvRincianBayar;
    @BindView(R.id.tv_total_bayar)
    TextView tvTotalBayar;
    @BindView(R.id.tv_blok_tp)
    TextView tvBlokTp;
    @BindView(R.id.tv_blok_parked)
    TextView tvBlokParked;
    @BindView(R.id.tv_blok_reserved)
    TextView tvBlokReserved;
    @BindView(R.id.finish_payment_layout)
    RelativeLayout finishPaymentLayout;


    private IntentFilter intentFilter;
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder notification;
    private String park;


    @BindView(R.id.taking_place_layout)
    RelativeLayout takingPlaceLayout;
    @BindView(R.id.parked_layout)
    RelativeLayout parkedLayout;
    @BindView(R.id.tv_time_remains_taking)
    TextView tvTimeRemainsTaking;
    @BindView(R.id.tv_time_remains_reserve)
    TextView tvTimeRemainsReserve;
    @BindView(R.id.reserve_layout)
    RelativeLayout reserveLayout;
    @BindView(R.id.tv_park_entered)
    TextView tvParkEntered;
    @BindView(R.id.tv_estimate_parked)
    TextView tvEstimateParked;


    private Bundle bundle;
    private boolean currentTimeStatus = true;
    String blok;
    private PaymentConfirmAdapter adapter;

    @Override
    protected ResponsePresenter createPresenter() {
        return new ResponsePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        ButterKnife.bind(this);

        bundle = getIntent().getExtras();
        blok = bundle.getString(MainPresenter.BLOK);
        park = bundle.getString(MainPresenter.PARK_NUMBER);

        if (bundle != null) {
            initLayout(bundle.getString(CHOOSE_LAYOUT));
        }
    }

    public void initLayout(String tag) {
        switch (tag) {
            case ResponsePresenter.TAKING_PLACE:
                takingPlaceLayout.setVisibility(View.VISIBLE);
                tvBlokTp.setText(blok);
                intentFilter = new IntentFilter(TAKING_PLACE_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentTakingPlace = new Intent(ResponseActivity.this, CountdownService.class);
                Bundle bundelTake = new Bundle();
                intentTakingPlace.setAction(ResponsePresenter.TAKING_PLACE);
                bundelTake.putLong(COUNTDOWN, 120000L);
                bundelTake.putString(PARK, park);
                intentTakingPlace.putExtras(bundelTake);
                startService(intentTakingPlace);
                notificateMe(R.drawable.remaining, getString(R.string.taking_place_content_title));
                break;
            case ResponsePresenter.RESERVED:
                reserveLayout.setVisibility(View.VISIBLE);
                tvBlokReserved.setText(blok);
                intentFilter = new IntentFilter(RESERVED_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentReserve = new Intent(ResponseActivity.this, CountdownService.class);
                Bundle bundleReserved = new Bundle();
                intentReserve.setAction(ResponsePresenter.RESERVED);
                bundleReserved.putLong(COUNTDOWN, bundle.getInt(MainPresenter.TIME_RESERVE) * 60000L);
                bundleReserved.putString(PARK, park);
                intentReserve.putExtras(bundleReserved);
                startService(intentReserve);
                notificateMe(R.drawable.hourglass_white, getString(R.string.reserved_content_title));
                break;
            case ResponsePresenter.PARKED:
                presenter.doParked(blok);
                break;
            case ResponsePresenter.PAYMENT:
                paymentConfirmLayout.setVisibility(View.VISIBLE);
                Log.d("initLayout", "Asup");
                intentFilter = new IntentFilter(PAYMENT_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentPayment = new Intent(ResponseActivity.this, CountdownService.class);
                Bundle bundlePayment = new Bundle();
                intentPayment.setAction(ResponsePresenter.PAYMENT);
                bundlePayment.putString(PARK, park);
                intentPayment.putExtras(bundlePayment);
                startService(intentPayment);
                notificateMe(R.drawable.coin_white, "Pembayaran");
                break;
        }
    }

    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundleBr = intent.getExtras();

            switch (intent.getAction()) {
                case TAKING_PLACE_RESULT:
                    long millisTp = bundleBr.getLong(MILLIS, 0);
                    Log.d("BroadcastReceiver", "TAKING_PLACE_RESULT");
                    presenter.doCountdownTimer(ResponsePresenter.TAKING_PLACE, millisTp, park);
                    if (bundleBr.getString(GO) != null) {
                        doGoingChange(bundleBr.getString(GO));
                    }
                    break;
                case RESERVED_RESULT:
                    long millisR = bundleBr.getLong(MILLIS, 0);
                    Log.d("BroadcastReceiver", "RESERVED");
                    presenter.doCountdownTimer(ResponsePresenter.RESERVED, millisR, park);
                    if (bundleBr.getString(GO) != null) {
                        doGoingChange(bundleBr.getString(GO));
                    }
                    break;
                case PARKED_RESULT:
                    long millisP = bundleBr.getLong(MILLIS, 0);
                    presenter.doCountupTimer(millisP);
                    if (bundleBr.getString(GO) != null) {
                        doGoingChange(bundleBr.getString(GO));
                    }
                    break;
                case PAYMENT_RESULT:
                    presenter.doPayment();

            }
        }
    };

    private void doGoingChange(String tag) {
        switch (tag) {
            case GO_TAKING_PLACE:
                stopService(new Intent(ResponseActivity.this
                        , CountdownService.class));
                unregisterReceiver(responseReceiver);
                managerCompat.cancel(0);
                initLayout(ResponsePresenter.TAKING_PLACE);
                reserveLayout.setVisibility(View.GONE);
                break;
            case GO_PARKED:
                stopService(new Intent(ResponseActivity.this
                        , CountdownService.class));
                Log.d("BroadcastReceiver", "GO PARKED");
                unregisterReceiver(responseReceiver);
                managerCompat.cancel(0);
                initLayout(ResponsePresenter.PARKED);
                takingPlaceLayout.setVisibility(View.GONE);
                break;
            case GO_PAYMENT:
                stopService(new Intent(ResponseActivity.this
                        , CountdownService.class));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Delay", "Destroy service");
                    }
                }, 2000L);
                Log.d("BroadcastReceiver", "GO PARKED");
                unregisterReceiver(responseReceiver);
                managerCompat.cancel(0);
                initLayout(ResponsePresenter.PAYMENT);
                parkedLayout.setVisibility(View.GONE);
                break;

        }
    }

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
        SharedPreferences.Editor editor = getSharedPreferences("X", MODE_PRIVATE).edit();

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
            case ResponsePresenter.TAKING_PLACE_COUNTINGDOWN_TIME:
                String timeRemainsTp = (String) obj;
                notification.setContentText(timeRemainsTp);
                managerCompat.notify(0, notification.build());
                tvTimeRemainsTaking.setText(timeRemainsTp);
                break;
            case ResponsePresenter.RESERVED_COUNTINGDOWN_TIME:
                String timeRemainsR = (String) obj;
                notification.setContentText(timeRemainsR);
                managerCompat.notify(0, notification.build());
                tvTimeRemainsReserve.setText(timeRemainsR);
                break;
            case ResponsePresenter.COUNTDOWN_FINISH:
                onGoingActivivity(false);
                stopService(new Intent(ResponseActivity.this, CountdownService.class));
                managerCompat.cancel(0);
                startActivity(new Intent(ResponseActivity.this, MainActivity.class));
                finish();
                break;
            case ResponsePresenter.ESTIMATE_TIME:
                ParkedStatus status = (ParkedStatus) obj;
                notification.setContentText(status.getCountingTime());

                if (currentTimeStatus == true) {
                    tvParkEntered.setText(status.getEnterTime());
                    currentTimeStatus = false;
                }
                tvEstimateParked.setText(status.getCountingTime());
                break;
            case ResponsePresenter.PAYMENT_CONFIRM:
                Bayar bayar = (Bayar) obj;
                tvTotalBayar.setText(String.valueOf(bayar.getTotalBayar()));

                adapter = new PaymentConfirmAdapter(bayar.getTransaksiList());
                rvRincianBayar.setAdapter(adapter);
                rvRincianBayar.setLayoutManager(new LinearLayoutManager(this));
                break;
            case ResponsePresenter.FINISH_PAYMENT:
                managerCompat.cancel(0);
                stopService(new Intent(ResponseActivity.this, CountdownService.class));
                presenter.doRemoveStatusPaymentListener();
                finishPaymentLayout.setVisibility(View.VISIBLE);
                break;
            case ResponsePresenter.INIT_PARKED:
                String recordedKey = (String) obj;

                parkedLayout.setVisibility(View.VISIBLE);
                tvBlokParked.setText(blok);
                intentFilter = new IntentFilter(PARKED_RESULT);
                registerReceiver(responseReceiver, intentFilter);
                Intent intentParked = new Intent(ResponseActivity.this, CountdownService.class);
                Bundle bundleParked = new Bundle();
                intentParked.setAction(ResponsePresenter.PARKED);
                bundleParked.putString(RECORDED_KEY, recordedKey);
                bundleParked.putString(PARK, park);
                bundleParked.putString(BLOK, blok);
                intentParked.putExtras(bundleParked);
                startService(intentParked);
                notificateMe(R.drawable.parked, getString(R.string.parked_info_notif));
                break;

        }
    }

    @Override
    public void onFailed(String tag) {

    }

    @OnClick(R.id.bt_cancle_reserve)
    public void onBtCancelReserveClick() {
        stopService(new Intent(ResponseActivity.this, CountdownService.class));
        presenter.doFinishReserved(park);
    }

    private void notificateMe(int resourceInt, String contentTitle) {
        Intent notifyIntent = new Intent(this, ResponseActivity.class);
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notifyPending = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        notification = new NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setSmallIcon(resourceInt)
                .setContentIntent(notifyPending)
                .setOngoing(true);


        managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0, notification.build());
    }

    @OnClick(R.id.bt_finish)
    public void onViewClicked() {
        startActivity(new Intent(ResponseActivity.this, MainActivity.class));
        finish();
    }
}
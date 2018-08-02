package com.example.ribani.parkirpintar.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.feature.reserve.ReserveActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;


public class CountdownService extends Service {

    private NotificationManagerCompat managerCompat;

    public CountdownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificateMe();

        switch (intent.getAction()) {
            case ResponsePresenter.TAKING_PLACE :
                Log.d("Service Running", "Mantap");
                doCountdown(intent.getAction(), intent);
                break;
            case ResponsePresenter.RESERVED :
                doCountdown(intent.getAction(), intent);
                break;
        }

        return START_STICKY;
    }

    private void doCountdown(final String tag, final Intent intent) {
        final long millisInFuture = intent.getLongExtra(ResponseActivity.COUNTDOWN, 0);
        final Intent sendIntent = new Intent();

        CountDownTimer countDownTimer = new CountDownTimer(millisInFuture + 1000, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sendValue = millisUntilFinished - 1000;

                switch (tag) {
                    case ResponseActivity.TAKING_PLACE :
                        Log.d("Service", "TAKING_PLACE");
                        sendIntent.setAction(ResponseActivity.TAKING_PLACE_RESULT);
                        sendIntent.putExtra(ResponseActivity.MILLIS, sendValue);
                        sendBroadcast(sendIntent);
                        break;
                    case ResponseActivity.RESERVED :
                        sendIntent.setAction(ResponseActivity.RESERVED_RESULT);
                        sendIntent.putExtra(ResponseActivity.MILLIS, sendValue);
                        sendBroadcast(sendIntent);
                        Log.d("Service", "RESERVED");
                        break;

                }
            }

            @Override
            public void onFinish() {
                managerCompat.cancel(0);
            }
        }.start();
    }

    private void notificateMe() {
        Intent notifyIntent = new Intent(this, ResponseActivity.class);
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notifyPending = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.coin_white)
                .setContentIntent(notifyPending)
                .setOngoing(true);


        managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0, notification.build());
    }
}

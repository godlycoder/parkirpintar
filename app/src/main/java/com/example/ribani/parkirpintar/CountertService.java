package com.example.ribani.parkirpintar;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CountertService extends IntentService {

    private NotificationManagerCompat managerCompat;

    public CountertService() {
        super("CountertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
    }

    private void doCountdown(final String tag, final Intent intent) {
        final long millisInFuture = intent.getLongExtra(ResponseActivity.COUNTDOWN, 0);
        final Intent sendIntent = new Intent();

        new CountDownTimer(millisInFuture + 1000, 1000L) {
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

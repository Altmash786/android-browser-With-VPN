package com.xitij.appbrowser.TimerService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xitij.appbrowser.vpn.ConnectVpnActivity;

import java.util.Timer;

public class TimerService extends Service {
    public static final String BROADCAST_ACTION = "com.vpn.timerserviceappbrowser";
    int i = 0;
    int fineltime = 0;
    Timer timer;
    private Handler handler = new Handler();
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            fineltime++;
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("time", fineltime);
            sendBroadcast(intent);
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ConnectVpnActivity.TAG, "onBind: " + intent.getAction());
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(ConnectVpnActivity.TAG, "onCreate: service  ");
        super.onCreate();
        handler.removeCallbacks(sendUpdatesToUI);
        fineltime = 0;
        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ConnectVpnActivity.TAG, "onStartCommand: " + intent.getAction());




      /*  final Handler handler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    i++;
                    fineltime=i;
                    Intent  intent = new Intent(BROADCAST_ACTION);
                    intent.putExtra("time", fineltime);
                    sendBroadcast(intent);
                });
            }


        };
        timer.schedule(doAsynchronousTask, 0, 1000);*/
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);

        Log.d(ConnectVpnActivity.TAG, "onDestroy: ");
        super.onDestroy();


    }
}

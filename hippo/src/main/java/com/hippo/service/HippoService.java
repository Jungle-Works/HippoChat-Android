package com.hippo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hippo.HippoConfig;
import com.hippo.utils.HippoLog;


/**
 * Basic service to work in background.
 *
 * @author gurmail
 */
public class HippoService extends Service {

    private static final String TAG = HippoService.class.getSimpleName();
    private static HippoService instance;

    public static HippoService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        HippoLog.i(TAG, "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        HippoLog.i(TAG, "onStartCommand");
        HippoConfig.getInstance().onServiceStarted();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HippoLog.i(TAG, "onDestroy");
        HippoConfig.getInstance().onServiceDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        HippoLog.i(TAG, "onDestroy");
        HippoConfig.getInstance().onServiceDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, HippoService.class);
    }

}

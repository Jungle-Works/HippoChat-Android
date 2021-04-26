package com.hippo.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.hippo.activity.ChannelActivity;
import com.hippo.receiver.PusherReceiver;

/**
 * Created by gurmail on 18/08/20.
 *
 * @author gurmail
 */

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("BackgroundService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent i = new Intent(getBaseContext(), ChannelActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (intent != null) {
            startActivity(i);

            PusherReceiver.completeWakefulIntent(intent);
        }
    }
}

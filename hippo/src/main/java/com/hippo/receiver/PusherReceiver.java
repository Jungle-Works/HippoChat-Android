package com.hippo.receiver;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.hippo.service.BackgroundService;

import org.json.JSONObject;

/**
 * Created by gurmail on 18/08/20.
 *
 * @author gurmail
 */

public class PusherReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //if (!isAppOnForeground((context))) {
        if (!false) {
            String custom = intent.getStringExtra("custom");

            try {
                JSONObject notificationData = new JSONObject(custom);

                // This is the Intent to deliver to our service.
                Intent service = new Intent(context, BackgroundService.class);
                // Put here your data from the json as extra in in the intent

                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

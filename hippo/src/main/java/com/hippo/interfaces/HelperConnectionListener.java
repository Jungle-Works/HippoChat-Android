package com.hippo.interfaces;

import org.json.JSONObject;

/**
 * Created by gurmail on 24/08/18.
 *
 * @author gurmail
 */

public interface HelperConnectionListener {
    void subscribeChannel(String subscribe);
    void unsubscribe(String subscribe);
    void unsubscribeAll();
    void publish(String channel, JSONObject data);
    void disconnect();
    void connect();
    void connectionCheck(CheckListener listener);

    interface CheckListener {
        void status(boolean flag);
    }
}

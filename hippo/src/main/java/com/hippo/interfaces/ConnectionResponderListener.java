package com.hippo.interfaces;

/**
 * Created by gurmail on 24/08/18.
 *
 * @author gurmail
 */

public interface ConnectionResponderListener {
    void connectionEstablished();
    void onMessageReceived(String message);
    void fayeStatus(int status);
    void onPongReceived();
}

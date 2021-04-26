package com.hippo.helper;

/**
 * Created by gurmail on 2020-04-06.
 *
 * @author gurmail
 */
public class FayeMessage {
    public String type;
    public String channelId;
    public String message;

    public FayeMessage(String type, String channelId, String message) {
        this.type = type;
        this.channelId = channelId;
        this.message = message;
    }
}

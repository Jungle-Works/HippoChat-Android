package com.hippo.support.model.callbackModel;

import com.hippo.model.CustomAttributes;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 06/04/18.
 * @author gurmail
 */

public class OpenChatParams {

    private String transactionId;
    private String userUniqueKey;
    private String  channelName;
    private ArrayList<String> tagsList;
    private String[] data;
    private CustomAttributes customAttributes;

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public String getChannelName() {
        return channelName;
    }

    public ArrayList<String> getTagsList() {
        return tagsList;
    }

    public String[] getData() {
        return data;
    }

    public CustomAttributes getCustomAttributes() {
        return customAttributes;
    }

    public OpenChatParams(String transactionId, String userUniqueKey, String channelName,
                          ArrayList<String> tagsList, String[] data) {
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.channelName = channelName;
        this.tagsList = tagsList;
        this.data = data;
    }

    public OpenChatParams(String transactionId, String userUniqueKey, String channelName,
                          ArrayList<String> tagsList, String[] data, CustomAttributes customAttributes) {
        this(transactionId, userUniqueKey, channelName, tagsList, data);
        this.customAttributes = customAttributes;
    }
}

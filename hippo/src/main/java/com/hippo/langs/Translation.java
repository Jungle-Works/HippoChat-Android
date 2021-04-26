package com.hippo.langs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by gurmail on 2020-06-18.
 *
 * @author gurmail
 */
public class Translation {
    @SerializedName("business_data")
    @Expose
    private Map<String, String> messages;

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setValues(Map<String, String> messages) {
        this.messages = messages;
    }
}

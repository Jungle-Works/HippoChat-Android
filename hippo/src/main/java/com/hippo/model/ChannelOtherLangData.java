package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2020-06-23.
 *
 * @author gurmail
 */
public class ChannelOtherLangData {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("channel_message")
    @Expose
    private String channelMessage;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("lang_code")
    @Expose
    private String langCode;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelMessage() {
        return channelMessage;
    }

    public void setChannelMessage(String channelMessage) {
        this.channelMessage = channelMessage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

}

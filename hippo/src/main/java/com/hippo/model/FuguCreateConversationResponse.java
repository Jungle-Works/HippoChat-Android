package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguCreateConversationResponse {

    public Integer getStatusCode() {
        return statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("channel_id")
        @Expose
        private Long channelId;

        public String getlabel() {
            return label;
        }

        @SerializedName("label")
        @Expose
        private String label;

        @SerializedName("agent_already_assigned")
        @Expose
        private int isAlreadyAssign;

        @SerializedName("bot_message_id")
        @Expose
        private Long botMessageId;

        @SerializedName("channel_image_url")
        @Expose
        private String channelImageUrl;

        @SerializedName("chat_type")
        @Expose
        private int chatType;

        public String getChannelImageUrl() {
            return channelImageUrl;
        }

        public void setChannelImageUrl(String channelImageUrl) {
            this.channelImageUrl = channelImageUrl;
        }

        public int getChatType() {
            return chatType;
        }

        public void setChatType(int chatType) {
            this.chatType = chatType;
        }

        public Long getBotMessageId() {
            return botMessageId;
        }

        public Long getChannelId() {
            return channelId;
        }

        public boolean isAlreadyAssigned() {
            try {
                return isAlreadyAssign == 1;
            } catch (Exception e) {
                return false;
            }
        }
    }

}
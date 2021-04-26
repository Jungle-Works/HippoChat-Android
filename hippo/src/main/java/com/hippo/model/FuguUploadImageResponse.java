package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 17/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguUploadImageResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

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

        @SerializedName("thumbnail_url")
        @Expose
        private String thumbnailUrl;
        @SerializedName("url")
        @Expose
        private String url;

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getUrl() {
            return url;
        }

    }
}




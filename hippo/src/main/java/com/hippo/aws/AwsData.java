package com.hippo.aws;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AwsData {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private DatamAws data;

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

    public DatamAws getData() {
        return data;
    }

    public void setData(DatamAws data) {
        this.data = data;
    }

}
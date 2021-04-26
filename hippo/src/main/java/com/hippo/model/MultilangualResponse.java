package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.langs.Translation;
import com.hippo.model.labelResponse.LabelData;

/**
 * Created by gurmail on 2020-06-23.
 *
 * @author gurmail
 */
public class MultilangualResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Translation data;

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

    public Translation getData() {
        return data;
    }

    public void setData(Translation data) {
        this.data = data;
    }

}

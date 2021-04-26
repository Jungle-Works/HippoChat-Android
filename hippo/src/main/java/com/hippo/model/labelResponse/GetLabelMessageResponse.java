package com.hippo.model.labelResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.model.Data;

/**
 * Created by gurmail on 2020-06-23.
 *
 * @author gurmail
 */
public class GetLabelMessageResponse {
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private LabelData data = new LabelData();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LabelData getData() {
        return data;
    }

    public void setData(LabelData data) {
        this.data = data;
    }
}

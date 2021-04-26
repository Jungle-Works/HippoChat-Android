package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2019-12-17.
 *
 * @author gurmail
 */
public class MultiSelectButtons {

    @SerializedName("btn_id")
    @Expose
    private String btnId;
    @SerializedName("btn_title")
    @Expose
    private String title;
    @SerializedName("status")
    @Expose
    private int status;

    public String getBtnId() {
        return btnId;
    }

    public void setBtnId(String btnId) {
        this.btnId = btnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

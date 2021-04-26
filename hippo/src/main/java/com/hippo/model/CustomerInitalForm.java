package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 14/02/19.
 *
 * @author gurmail
 */
public class CustomerInitalForm {

    @SerializedName("page_title")
    @Expose
    private String pageTitle;
    @SerializedName("fields")
    @Expose
    private List<Field> fields = null;
    @SerializedName("button")
    @Expose
    private Button button;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}



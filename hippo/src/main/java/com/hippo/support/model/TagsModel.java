package com.hippo.support.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 17/04/18.
 */
public class TagsModel {
    @SerializedName("tag_name")
    @Expose
    private String tagName;

    public TagsModel(String tagName) {
        this.tagName = tagName;
    }
}

package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 14/06/18.
 *
 * @author gurmail
 */

public class GroupingTag {

//    @SerializedName("tag_id")
//    @Expose
//    private Integer tagId;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
//    @SerializedName("color_code")
//    @Expose
//    private String colorCode;
//    @SerializedName("tag_type")
//    @Expose
//    private Integer tagType;
//
//    public Integer getTagId() {
//        return tagId;
//    }
//
//    public void setTagId(Integer tagId) {
//        this.tagId = tagId;
//    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

//    public String getColorCode() {
//        return colorCode;
//    }
//
//    public void setColorCode(String colorCode) {
//        this.colorCode = colorCode;
//    }
//
//    public Integer getTagType() {
//        return tagType;
//    }
//
//    public void setTagType(Integer tagType) {
//        this.tagType = tagType;
//    }

}

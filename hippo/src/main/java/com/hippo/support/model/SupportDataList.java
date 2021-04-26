package com.hippo.support.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gurmail S. Kang on 30/03/18.
 * @author gurmail
 */
public class SupportDataList {

    @SerializedName("faq_id")
    @Expose
    private Integer faqId;
    @SerializedName("faq_name")
    @Expose
    private String faqName;
    @SerializedName("list")
    @Expose
    private List<Item> list = null;

    public Integer getCategoryId() {
        return faqId;
    }

    public void setFaqId(Integer faqId) {
        this.faqId = faqId;
    }

    public String getCategoryName() {
        return faqName;
    }

    public void setFaqName(String faqName) {
        this.faqName = faqName;
    }

    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }
}

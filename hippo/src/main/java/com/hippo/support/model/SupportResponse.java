package com.hippo.support.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gurmail on 29/03/18.
 */


public class SupportResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Map<String, SupportDataList> itemData = new HashMap<>();
    @SerializedName("defaultFaqId")
    @Expose
    private Integer defaultFaqId;
    @SerializedName("defaultFaqName")
    @Expose
    private String defaultFaqName;

    public Integer getDefaultFaqId() {
        return defaultFaqId;
    }

    public void setDefaultFaqId(Integer defaultFaqId) {
        this.defaultFaqId = defaultFaqId;
    }

    public String getDefaultFaqName() {
        return defaultFaqName;
    }

    public void setDefaultFaqName(String defaultFaqName) {
        this.defaultFaqName = defaultFaqName;
    }

    public Map<String, SupportDataList> getItemData() {
        return itemData;
    }

    public void setItemData(Map<String, SupportDataList> itemData) {
        this.itemData = itemData;
    }


//    private SupportData supportData;

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public SupportData getSupportData() {
//        return supportData;
//    }
//
//    public void setSupportData(SupportData supportData) {
//        this.supportData = supportData;
//    }

  /*  public class SupportData {

        private LinkedHashMap<String, SupportDataList> itemData = new LinkedHashMap<>();

        public LinkedHashMap<String, SupportDataList> getItemData() {
            return itemData;
        }

        public void setItemData(LinkedHashMap<String, SupportDataList> itemData) {
            this.itemData = itemData;
        }

//        public Map<String, SupportDataList> getItem() {
//            return itemData;
//        }
//        public void setItem(Map<String, SupportDataList> itemData) {
//            this.itemData = itemData;
//        }
    }*/

}

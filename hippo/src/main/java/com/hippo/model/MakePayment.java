package com.hippo.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.BuildConfig;
import com.hippo.database.CommonData;

import java.util.ArrayList;

/**
 * Created by gurmail on 2019-11-15.
 *
 * @author gurmail
 */
public class MakePayment {
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    private String en_user_id;

    Integer channel_id;
    ArrayList<HippoPayment> items;

    private int payment_gateway_id;
    private int is_multi_gateway_flow;
    private String device_details;
    private int app_version;
    private String device_id;
    private int source_type;
    private int device_type;
    @SerializedName("lang")
    @Expose
    private String lang;
    private Integer is_sdk_flow;

    public void setIsSdkFlow(Integer is_sdk_flow) {
        this.is_sdk_flow = is_sdk_flow;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public ArrayList<HippoPayment> getItems() {
        return items;
    }

    public void setItems(ArrayList<HippoPayment> items) {
        this.items = items;
    }

    public String getEn_user_id() {
        return en_user_id;
    }

    public void setEn_user_id(String en_user_id) {
        this.en_user_id = en_user_id;
    }

    public void setPayment_gateway_id(int payment_gateway_id) {
        this.payment_gateway_id = payment_gateway_id;
    }

    public void setIs_multi_gateway_flow(int is_multi_gateway_flow) {
        this.is_multi_gateway_flow = is_multi_gateway_flow;
    }

    public void setDevice_details(String device_details) {
        this.device_details = device_details;
    }

    public void setApp_version(int app_version) {
        this.app_version = app_version;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }
}

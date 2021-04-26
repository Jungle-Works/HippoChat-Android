package com.hippo.model;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;

/**
 * Created by bhavya on 22/08/17.
 */

public class HippoUnreadCountParams {

    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("other_user_unique_key")
    @Expose
    private JsonArray otherUserUniqueKeys;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private int appVersion = HippoConfig.getInstance().getCodeVersion();
    @SerializedName("source_type")
    @Expose
    private int source = 1;
    @SerializedName("lang")
    @Expose
    private String lang;

    public void setLang(String lang) {
        this.lang = lang;
    }


    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }


    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public JsonArray getOtherUserUniqueKeys() {
        return otherUserUniqueKeys;
    }

    public void setOtherUserUniqueKeys(JsonArray otherUserUniqueKeys) {
        this.otherUserUniqueKeys = otherUserUniqueKeys;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }


    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public HippoUnreadCountParams(String appSecretKey, String transactionId, String userUniqueKey,
                                  JsonArray otherUserUniqueKeys, String enUserId) {
        this.appSecretKey = appSecretKey;
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.otherUserUniqueKeys = otherUserUniqueKeys;
        this.enUserId = enUserId;
        this.deviceType = 1;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        source = 1;
    }


    @Override
    public String toString() {
        return appSecretKey;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


}

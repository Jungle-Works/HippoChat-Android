package com.hippo.model;

import com.hippo.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.HippoConfig;

/**
 * Created by bhavya on 07/07/17.
 */

public class FuguGetByLabelIdParams {
    @SerializedName("page_start")
    @Expose
    private Integer pageStart;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("label_id")
    @Expose
    private Long labelId;
    @SerializedName("en_user_id")
    @Expose
    private String userId;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private int appVersion = HippoConfig.getInstance().getCodeVersion();
//    @SerializedName("app_version_code")
//    @Expose
//    private int appVersionCode = BuildConfig.VERSION_CODE;
    @SerializedName("source_type")
    @Expose
    private int source = 1;
    @SerializedName("multi_channel_label_mapping_app")
    @Expose
    private int multiChannelLabelMapping;
    @SerializedName("lang")
    @Expose
    private String lang;

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setMultiChannelLabelMapping(int multiChannelLabelMapping) {
        this.multiChannelLabelMapping = multiChannelLabelMapping;
    }

    public FuguGetByLabelIdParams(String appSecretKey, Long labelId, String userId, Integer pageStart) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        //appVersionCode = BuildConfig.VERSION_CODE;
        this.deviceType = 1;
    }

}

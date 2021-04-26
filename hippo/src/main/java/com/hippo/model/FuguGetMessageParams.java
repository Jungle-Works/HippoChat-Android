package com.hippo.model;

import com.hippo.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.HippoConfig;

/**
 * Created by bhavya on 05/07/17.
 */

public class FuguGetMessageParams {
    @SerializedName("page_start")
    @Expose
    private Integer pageStart;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("en_user_id")
    @Expose
    private String userId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
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
    private int source;
    @SerializedName("page_end")
    @Expose
    private Integer pageEnd;
    @SerializedName("lang")
    @Expose
    private String lang;

    public void setLang(String lang) {
        this.lang = lang;
    }

    public FuguGetMessageParams(String appSecretKey, Long channelId,
                                String userId, Integer pageStart, String channelName) {
        this.appSecretKey = appSecretKey;
        this.channelId = channelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.channelName = channelName;
        this.deviceType = 1;
        this.appVersion =HippoConfig.getInstance().getCodeVersion();
        this.source = 1;
    }

    public void setPageEnd(Integer size) {
        pageEnd = size;
    }
}

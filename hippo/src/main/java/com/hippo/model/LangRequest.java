package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;

/**
 * Created by gurmail on 2020-06-23.
 *
 * @author gurmail
 */
public class LangRequest {

    private String app_secret_key;
    private int request_source;
    private String lang;

    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private int appVersion =HippoConfig.getInstance().getCodeVersion();
    @SerializedName("source_type")
    @Expose
    private int source;

    public LangRequest(String app_secret_key, int request_source, String lang) {
        this.app_secret_key = app_secret_key;
        this.request_source = request_source;
        this.lang = lang;
        this.deviceType = 1;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        this.source = 1;
    }
}

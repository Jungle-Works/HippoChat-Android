package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2020-06-23.
 *
 * @author gurmail
 */
public class BusinessLanguages {
    @SerializedName("business_id")
    @Expose
    private Integer businessId;

    @SerializedName("lang_code")
    @Expose
    private String langCode;

    @SerializedName("lang_id")
    @Expose
    private int langId;

    @SerializedName("is_default")
    @Expose
    private int isDefault;

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public int getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefaultLnag() {
        return isDefault == 1;
    }
}

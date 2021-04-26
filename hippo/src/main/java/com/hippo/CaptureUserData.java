package com.hippo;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class CaptureUserData {

    private String userUniqueKey = "";
    private String fullName = "";
    private String email = "";
    private String phoneNumber = "";
    private double latitude = 0;
    private double longitude = 0;
    private Long userId = -1l;
    private String enUserId = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String region = "";
    private String city = "";
    private String country = "";
    private String zipCode = "";
    private String lang = "";
    private boolean fetchBusinessLang;
    private HashMap<String, String> custom_attributes = new HashMap<>();
    private ArrayList<GroupingTag> tags = new ArrayList<>();

    public ArrayList<GroupingTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<GroupingTag> tags) {
        this.tags = tags;
    }

    public HashMap<String, String> getCustom_attributes() {
        return custom_attributes;
    }

    public void setCustom_attributes(HashMap<String, String> custom_attributes) {
        this.custom_attributes = custom_attributes;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public boolean isFetchBusinessLang() {
        return fetchBusinessLang;
    }

    public String getLang() {
        return lang;
    }

    public static class Builder {
        private CaptureUserData captureUserData = new CaptureUserData();

        public Builder userUniqueKey(String userUniqueKey) {
            captureUserData.userUniqueKey = userUniqueKey;
            return this;
        }

        public Builder fullName(String fullName) {
            captureUserData.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            captureUserData.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            captureUserData.phoneNumber = phoneNumber;
            return this;
        }

        public Builder addressLine1(String addressLine1) {
            captureUserData.addressLine1 = addressLine1;
            return this;
        }

        public Builder addressLine2(String addressLine2) {
            captureUserData.addressLine2 = addressLine2;
            return this;
        }

        public Builder region(String region) {
            captureUserData.region = region;
            return this;
        }

        public Builder city(String city) {
            captureUserData.city = city;
            return this;
        }

        public Builder country(String country) {
            captureUserData.country = country;
            return this;
        }

        public Builder zipCode(String zipCode) {
            captureUserData.zipCode = zipCode;
            return this;
        }

        public Builder latitude(double latitude) {
            captureUserData.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            captureUserData.longitude = longitude;
            return this;
        }

        public Builder fetchBusinessLang(boolean fetchBusinessLang) {
            captureUserData.fetchBusinessLang = fetchBusinessLang;
            return this;
        }

        public Builder setLang(String lang) {
            captureUserData.lang = lang;
            return this;
        }

        public Builder customAttributes(HashMap<String, String> customAttributes) {
            captureUserData.custom_attributes = customAttributes;
            return this;
        }

        public Builder userTags(ArrayList<GroupingTag> userTags) {
            captureUserData.tags = userTags;
            return this;
        }

        public CaptureUserData build() {
            if(TextUtils.isEmpty(captureUserData.userUniqueKey))
                throw new IllegalStateException("User unique key can not be empty!");
            return captureUserData;
        }

    }
}

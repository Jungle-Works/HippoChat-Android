package com.hippo;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by gurmail on 27/12/18.
 *
 * @author gurmail
 */
public class HippoConfigAttributes {

    private String appType;
    private String appKey;
    private String environment;
    private String resellerToken;
    private int referenceId;
    private String provider;
    private boolean unreadCount;
    private boolean showLog;
    private CaptureUserData captureUserData;
    private AdditionalInfo additionalInfo;
    private String deviceToken;
    private boolean isManager;
    private boolean isWhitelabel;
    private boolean shareAllFileTypes;
    private String authToken;
    private HashMap<String, Object> customAttributes;
    private boolean reversePageOrder;
    private HippoColorConfig colorConfig;
    private boolean isBroadcastEnabled;
    private boolean isPaymentEnabled;
    private String imagePath;
    private boolean isResellerApi;
    //private ConversationalData conversationalData;
    private HashMap<String, DeeplinKData> deepLinks;

    public String getImagePath() {
        return imagePath;
    }

    public HippoColorConfig getColorConfig() {
        return colorConfig;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isBroadcastEnabled() {
        return isBroadcastEnabled;
    }

    public boolean isWhitelabel() {
        return isWhitelabel;
    }

    public boolean shareAllFileTypes() {
        return shareAllFileTypes;
    }

    public String getAuthToken() {
        return authToken;
    }

    public HashMap<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public CaptureUserData getCaptureUserData() {
        return captureUserData;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    /*public ConversationalData getConversationalData() {
        return conversationalData;
    }*/

    public String getAppType() {
        return appType;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getResellerToken() {
        return resellerToken;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public String getProvider() {
        return provider;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public boolean isUnreadCount() {
        return unreadCount;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public boolean isReversePageOrder() {
        return reversePageOrder;
    }

    public boolean isPaymentEnabled() {
        return isPaymentEnabled;
    }

    public boolean isResellerApi() {
        return isResellerApi;
    }

    public HashMap<String, DeeplinKData> getDeepLinks() {
        return deepLinks;
    }

    public static class Builder {

        private String appType;
        private String appKey;
        private String environment;
        private String resellerToken;
        private int referenceId;
        private String provider;
        private boolean unreadCount;
        private boolean showLog;
        private CaptureUserData captureUserData;
        private AdditionalInfo additionalInfo;
        private boolean isManager;
        private boolean isWhitelabel;
        private boolean shareAllFileTypes;
        private String authToken;
        private HashMap<String, Object> customAttributes;
        private boolean reversePageOrder;
        private String deviceToken;
        private HippoColorConfig colorConfig;
        private boolean isBroadcastEnabled;
        private boolean isPaymentEnabled;
        private String imagePath;
        private boolean isResellerApi;
        private ConversationalData conversationalData;
        private HashMap<String, DeeplinKData> deepLinks;

        private HippoConfigAttributes attributes = new HippoConfigAttributes(this);

        public Builder setImagePath(String imagePath) {
            attributes.imagePath = imagePath;
            return this;
        }

        public Builder setPaymentEnabled(boolean isPaymentEnabled) {
            attributes.isPaymentEnabled = isPaymentEnabled;
            return this;
        }

        public Builder setReversePageOrder(boolean reversePageOrder) {
            attributes.reversePageOrder = reversePageOrder;
            return this;
        }

        public Builder setBroadcastEnabled(boolean isBroadcastEnabled) {
            attributes.isBroadcastEnabled = isBroadcastEnabled;
            return this;
        }

        public Builder setColorConfig(HippoColorConfig colorConfig) {
            attributes.colorConfig = colorConfig;
            return this;
        }

        public Builder setManager(boolean manager) {
            attributes.isManager = manager;
            return this;
        }

        public Builder isForking(boolean isFork) {
            attributes.isWhitelabel = isFork;
            return this;
        }

        //True if need to share all file types
        public Builder isShareAllFileTypes(boolean shareAllFileTypes) {
            attributes.shareAllFileTypes = shareAllFileTypes;
            return this;
        }

        public Builder setAuthToken(String authToken) {
            attributes.authToken = authToken;
            return this;
        }

        public Builder setCustomAttributes(HashMap<String, Object> customAttributes) {
            attributes.customAttributes = customAttributes;
            return this;
        }

        public Builder setAppType(String appType) {
            attributes.appType = appType;
            return this;
        }

        public Builder setAppKey(String appKey) {
            attributes.appKey = appKey;
            attributes.isResellerApi = false;
            return this;
        }

        public Builder setEnvironment(String environment) {
            attributes.environment = environment;
            return this;
        }


        public Builder setResellerToken(String resellerToken) {
            attributes.resellerToken = resellerToken;
            attributes.isResellerApi = true;
            return this;
        }

        public Builder setReferenceId(int referenceId) {
            attributes.referenceId = referenceId;
            return this;
        }

        public Builder setProvider(String provider) {
            attributes.provider = provider;
            return this;
        }

        public Builder setUnreadCount(boolean unreadCount) {
            attributes.unreadCount = unreadCount;
            return this;
        }

        public Builder setShowLog(boolean showLog) {
            attributes.showLog = showLog;
            return this;
        }

        public Builder setCaptureUserData(CaptureUserData captureUserData) {
            attributes.captureUserData = captureUserData;
            return this;
        }

        public Builder setAdditionalInfo(AdditionalInfo additionalInfo) {
            attributes.additionalInfo = additionalInfo;
            return this;
        }

        /*public Builder setConversationalData(ConversationalData conversationalData) {
            attributes.conversationalData = conversationalData;
            return this;
        }*/

        public Builder setDeviceToken(String deviceToken) {
            attributes.deviceToken = deviceToken;
            return this;
        }

        public Builder setIsResellerApi(boolean isResellerApi) {
            attributes.isResellerApi = isResellerApi;
            return this;
        }

        public Builder setDeepLinks(HashMap<String, DeeplinKData> deepLinks) {
            attributes.deepLinks = deepLinks;
            return this;
        }

        public HippoConfigAttributes build() {
            if (TextUtils.isEmpty(attributes.appType)) {
                throw new IllegalStateException("AppType can not be empty!");
            } else if (TextUtils.isEmpty(attributes.provider)) {
                throw new IllegalStateException("Provider can not be empty!");
            } else if (TextUtils.isEmpty(attributes.deviceToken))
                throw new IllegalArgumentException("Device token can not be empty!");

            return attributes;
        }
    }

    private HippoConfigAttributes(Builder builder) {
        this.appType = builder.appType;
        this.appKey = builder.appKey;
        this.environment = builder.environment;
        this.referenceId = builder.referenceId;
        this.resellerToken = builder.resellerToken;
        this.provider = builder.provider;
        this.unreadCount = builder.unreadCount;
        this.showLog = builder.showLog;
        this.captureUserData = builder.captureUserData;
        this.additionalInfo = builder.additionalInfo;
        //this.conversationalData = builder.conversationalData;
        this.isManager = builder.isManager;
        this.isWhitelabel = builder.isWhitelabel;
        this.shareAllFileTypes = builder.shareAllFileTypes;
        this.authToken = builder.authToken;
        this.customAttributes = builder.customAttributes;
        this.deviceToken = builder.deviceToken;
        this.colorConfig = builder.colorConfig;
        this.isBroadcastEnabled = builder.isBroadcastEnabled;
        this.isPaymentEnabled = builder.isPaymentEnabled;
        this.imagePath = builder.imagePath;
        this.isResellerApi = builder.isResellerApi;
        this.deepLinks = builder.deepLinks;
    }
}

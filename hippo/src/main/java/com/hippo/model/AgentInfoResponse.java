package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 2019-11-25.
 *
 * @author gurmail
 */
public class AgentInfoResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("phone_number")
        @Expose
        private String phoneNumber;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("user_image")
        @Expose
        private String userImage;
        @SerializedName("user_sub_type")
        @Expose
        private Integer userSubType;
        @SerializedName("business_id")
        @Expose
        private Integer businessId;
        @SerializedName("online_status")
        @Expose
        private String onlineStatus;
        @SerializedName("online_status_updated_at")
        @Expose
        private String onlineStatusUpdatedAt;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("custom_fields")
        @Expose
        private List<CustomField> customFields = null;
        @SerializedName("rating")
        @Expose
        private String rating;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public Integer getUserSubType() {
            return userSubType;
        }

        public void setUserSubType(Integer userSubType) {
            this.userSubType = userSubType;
        }

        public Integer getBusinessId() {
            return businessId;
        }

        public void setBusinessId(Integer businessId) {
            this.businessId = businessId;
        }

        public String getOnlineStatus() {
            return onlineStatus;
        }

        public void setOnlineStatus(String onlineStatus) {
            this.onlineStatus = onlineStatus;
        }

        public String getOnlineStatusUpdatedAt() {
            return onlineStatusUpdatedAt;
        }

        public void setOnlineStatusUpdatedAt(String onlineStatusUpdatedAt) {
            this.onlineStatusUpdatedAt = onlineStatusUpdatedAt;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<CustomField> getCustomFields() {
            return customFields;
        }

        public void setCustomFields(List<CustomField> customFields) {
            this.customFields = customFields;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}

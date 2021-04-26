package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2019-10-31.
 *
 * @author gurmail
 */
public class UserResponse {
    @SerializedName("statusCode")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("access_token")
        @Expose
        private String accessToken;
        @SerializedName("user_unique_key")
        @Expose
        private String userUniqueKey;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getUserUniqueKey() {
            return userUniqueKey;
        }

        public void setUserUniqueKey(String userUniqueKey) {
            this.userUniqueKey = userUniqueKey;
        }
    }
}

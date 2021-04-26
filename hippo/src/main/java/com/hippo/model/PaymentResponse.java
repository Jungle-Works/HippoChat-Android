package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2019-11-15.
 *
 * @author gurmail
 */
public class PaymentResponse {
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
        @SerializedName("payment_url")
        @Expose
        private String paymentUrl;

        public String getPaymentUrl() {
            return paymentUrl;
        }

        public void setPaymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
        }

        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("reference_id")
        @Expose
        private String reference_id;
        @SerializedName("auth_order_id")
        @Expose
        private String auth_order_id;
        @SerializedName("phone_no")
        @Expose
        private String phoneNumber;
        @SerializedName("user_email")
        @Expose
        private String userEmail;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("api_key")
        @Expose
        private String apiKey;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("amount")
        @Expose
        private Double amount;

        public String getOrderId() {
            return orderId;
        }

        public String getReference_id() {
            return reference_id;
        }

        public String getAuth_order_id() {
            return auth_order_id;
        }

        public String getDescription() {
            return description;
        }

        public String getCurrency() {
            return currency;
        }

        public Double getAmount() {
            return amount;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getName() {
            return name;
        }
    }
}

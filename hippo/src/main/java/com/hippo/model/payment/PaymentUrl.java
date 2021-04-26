package com.hippo.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 30/10/20.
 *
 * @author gurmail
 */
public class PaymentUrl {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }
}

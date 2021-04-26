package com.hippo.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-05-06.
 *
 * @author gurmail
 */
public class AddedPaymentGateway {

    @SerializedName("gateway_id")
    @Expose
    private Integer gatewayId;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("gateway_name")
    @Expose
    private String gatewayName;
    @SerializedName("gateway_image")
    @Expose
    private String gatewayImage;
    @SerializedName("currency_allowed")
    @Expose
    private ArrayList<String> currencyallowed;
    @SerializedName("key_id")
    @Expose
    private String key_id;

    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayImage() {
        return gatewayImage;
    }

    public void setGatewayImage(String gatewayImage) {
        this.gatewayImage = gatewayImage;
    }

    public ArrayList<String> getCurrencyallowed() {
        return currencyallowed;
    }

    public void setCurrencyallowed(ArrayList<String> currencyallowed) {
        this.currencyallowed = currencyallowed;
    }

    public String getKeyId() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
}

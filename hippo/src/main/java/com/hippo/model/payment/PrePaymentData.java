package com.hippo.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.model.HippoPayment;
import java.util.ArrayList;

/**
 * Created by gurmail on 2020-06-16.
 *
 * @author gurmail
 */
public class PrePaymentData {

    private String app_secret_key;
    private Long user_id;
    private String en_user_id;
    private int operation_type = 1;
    private int fetch_payment_url = 1;
    private int payment_gateway_id;
    private String transaction_id;
    private ArrayList<HippoPayment> payment_items;
    @SerializedName("lang")
    @Expose
    private String lang;
    private Integer is_sdk_flow;
    @SerializedName("payment_type")
    @Expose
    private Integer paymentType;

    public void setIsSdkFlow(Integer is_sdk_flow) {
        this.is_sdk_flow = is_sdk_flow;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getApp_secret_key() {
        return app_secret_key;
    }

    public void setApp_secret_key(String app_secret_key) {
        this.app_secret_key = app_secret_key;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getEn_user_id() {
        return en_user_id;
    }

    public void setEn_user_id(String en_user_id) {
        this.en_user_id = en_user_id;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public ArrayList<HippoPayment> getPayment_items() {
        return payment_items;
    }

    public void setPayment_items(ArrayList<HippoPayment> payment_items) {
        this.payment_items = payment_items;
    }

    public int getFetch_payment_url() {
        return fetch_payment_url;
    }

    public void setFetch_payment_url(int fetch_payment_url) {
        this.fetch_payment_url = fetch_payment_url;
    }

    public int getPayment_gateway_id() {
        return payment_gateway_id;
    }

    public void setPayment_gateway_id(int payment_gateway_id) {
        this.payment_gateway_id = payment_gateway_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }
}

package com.hippo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gurmail on 22/02/19.
 *
 * @author gurmail
 */
public class PaymentData implements Serializable {

    String title;
    String currency;
    String currencySymbol;
    ArrayList<PaymentModelData> paymentModelData;
    float totalAmount;

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ArrayList<PaymentModelData> getPaymentModelData() {
        return paymentModelData;
    }

    public void setPaymentModelData(ArrayList<PaymentModelData> paymentModelData) {
        this.paymentModelData = paymentModelData;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}

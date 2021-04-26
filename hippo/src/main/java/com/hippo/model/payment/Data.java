package com.hippo.model.payment;

/**
 * Created by gurmail on 2020-05-06.
 *
 * @author gurmail
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("added_payment_gateways")
    @Expose
    private List<AddedPaymentGateway> addedPaymentGateways = null;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("payment_url")
    @Expose
    private PaymentUrl paymentUrl;

    public List<AddedPaymentGateway> getAddedPaymentGateways() {
        return addedPaymentGateways;
    }

    public void setAddedPaymentGateways(List<AddedPaymentGateway> addedPaymentGateways) {
        this.addedPaymentGateways = addedPaymentGateways;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public PaymentUrl getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(PaymentUrl paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

}
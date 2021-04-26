package com.hippo.model;

import java.io.Serializable;

/**
 * Created by gurmail on 22/02/19.
 *
 * @author gurmail
 */
public class PaymentModelData implements Serializable {

    private String itemDescription;
    private String price;
    private String errorDesc;
    private String errorPrice;
    //private boolean isAdded;

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorPrice() {
        return errorPrice;
    }

    public void setErrorPrice(String errorPrice) {
        this.errorPrice = errorPrice;
    }
}

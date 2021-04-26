package com.hippo;

/**
 * Created by gurmail on 2020-06-16.
 *
 * @author gurmail
 */
public class HippoPrePaymentBuilder {

    private String amount;
    private String title;
    private String description;
    private String currency;
    private String currencySymbol;
    private String message;
    private String transactionId;
    private int paymentGatewayId;
    private Integer paymentType;

    public String getAmount() {
        return amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getPaymentGatewayId() {
        return paymentGatewayId;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public static class Builder {
        private String amount;
        private String title;
        private String description;
        private String currency;
        private String currencySymbol;
        private String message;
        private String transactionId;
        private int paymentGatewayId;
        private Integer paymentType;

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder paymentGatewayId(int paymentGatewayId) {
            this.paymentGatewayId = paymentGatewayId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder currencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder paymentType(int paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public HippoPrePaymentBuilder build() {
            return new HippoPrePaymentBuilder(this);
        }
    }

    public HippoPrePaymentBuilder(Builder builder) {
        this.amount = builder.amount;
        this.title = builder.title;
        this.description = builder.description;
        this.currency = builder.currency;
        this.currencySymbol = builder.currencySymbol;
        this.message = builder.message;
        this.transactionId = builder.transactionId;
        this.paymentGatewayId = builder.paymentGatewayId;
        this.paymentType = builder.paymentType;
    }

}

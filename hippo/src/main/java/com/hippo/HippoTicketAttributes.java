package com.hippo;

/**
 * Created by gurmail on 04/04/18.
 */

public class HippoTicketAttributes {

    private String mFaqName;
    private String mTransactionId;

    public String getmFaqName() {
        return mFaqName;
    }
    public String getmTransactionId() {
        return mTransactionId;
    }


    public static class Builder {

        private String mFaqName;
        private String mTransactionId;

        public Builder setFaqName(String mFaqName) {
            this.mFaqName = mFaqName;
            return this;
        }

        public Builder setTransactionId(String mTransactionId) {
            this.mTransactionId = mTransactionId;
            return this;
        }

        public HippoTicketAttributes build() {
            return new HippoTicketAttributes(this);
        }
    }

    private HippoTicketAttributes(Builder builder) {
        this.mFaqName = builder.mFaqName;
        this.mTransactionId = builder.mTransactionId;
    }
}

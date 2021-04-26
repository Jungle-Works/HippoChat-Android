package com.hippo;

/**
 * Created by gurmail on 2020-01-10.
 *
 * @author gurmail
 */
public class ConversationalData {

    private boolean hasPager;

    public boolean isHasPager() {
        return hasPager;
    }

    public static class Builder {
        private boolean hasPager;
        private ConversationalData data = new ConversationalData(this);

        public Builder hasPager(boolean hasPager) {
            data.hasPager = hasPager;
            return this;
        }

        public ConversationalData build() {
            return data;
        }
    }

    private ConversationalData(ConversationalData.Builder builder) {
        this.hasPager = builder.hasPager;
    }
}

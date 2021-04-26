package com.hippo;

import android.text.TextUtils;
import com.hippo.datastructure.ChatType;
import com.hippo.model.CustomAttributes;

import java.util.ArrayList;

/**
 * Created by gurmail on 31/12/18.
 *
 * @author gurmail
 */
public class ChatByUniqueIdAttributes {
    private String transactionId;
    private String userUniqueKey;
    private String channelName;
    private boolean isSupportTicket;
    private boolean isInsertBotId;
    private boolean skipBot;
    private String[] message;
    private ArrayList<String> tags;
    private ArrayList<String> groupingTags;
    private ArrayList<String> otherUserUniqueKeys;
    private CustomAttributes customAttributes;
    private String agentEmail;
    private boolean isSingleChannelTransactionId;

    public String getAgentEmail() {
        return agentEmail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public String getChannelName() {
        return channelName;
    }

//    public HippoChatType getChatType() {
//        return type;
//    }


    public boolean isSkipBot() {
        return skipBot;
    }

    public boolean isSupportTicket() {
        return isSupportTicket;
    }

    public boolean isInsertBotId() {
        return isInsertBotId;
    }

    public String[] getMessage() {
        return message;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getGroupingTags() {
        return groupingTags;
    }

    public ArrayList<String> getOtherUserUniqueKeys() {
        return otherUserUniqueKeys;
    }

    public CustomAttributes getCustomAttributes() {
        return customAttributes;
    }

    public boolean isSingleChannelTransactionId() {
        return isSingleChannelTransactionId;
    }

    public static class Builder {
        private String transactionId;
        private String userUniqueKey;
        private String channelName;
        private int chatType;
        private boolean isSupportTicket;
        private boolean isInsertBotId;
        private boolean skipBot;
        private String[] message;
        private ArrayList<String> tags;
        private ArrayList<String> groupingTags;
        private ArrayList<String> otherUserUniqueKeys;
        private CustomAttributes customAttributes;
        private String agentEmail;
        private boolean isSingleChannelTransactionId;
//        private HippoChatType type;

//        public Builder setHippoChatType(HippoChatType chatTtype) {
//            this.type = chatTtype;
//            return this;
//        }
        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setUserUniqueKey(String userUniqueKey) {
            this.userUniqueKey = userUniqueKey;
            return this;
        }

        public Builder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder setSupportTicket(boolean supportTicket) {
            isSupportTicket = supportTicket;
            return this;
        }

        public Builder setInsertBotId(boolean insertBotId) {
            isInsertBotId = insertBotId;
            return this;
        }

        public Builder skipBot(boolean skipBotId) {
            skipBot = skipBotId;
            return this;
        }

        public Builder setMessage(String[] message) {
            this.message = message;
            return this;
        }

        public Builder setTags(ArrayList<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setGroupingTags(ArrayList<String> groupingTags) {
            this.groupingTags = groupingTags;
            return this;
        }

        public Builder setOtherUserUniqueKeys(ArrayList<String> otherUserUniqueKeys) {
            this.otherUserUniqueKeys = otherUserUniqueKeys;
            return this;
        }

        public Builder setCustomAttributes(CustomAttributes customAttributes) {
            this.customAttributes = customAttributes;
            return this;
        }

        public Builder setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
            return this;
        }

        public Builder setSingleChannelTransactionId(boolean isSingleChannelTransactionId) {
            this.isSingleChannelTransactionId = isSingleChannelTransactionId;
            return this;
        }

        public ChatByUniqueIdAttributes build() {
            ChatByUniqueIdAttributes attributes = new ChatByUniqueIdAttributes(this);
//            if(TextUtils.isEmpty(attributes.transactionId))
//                throw new IllegalStateException("TransactionID can not be empty!");

            return attributes;
        }
    }

    private ChatByUniqueIdAttributes(Builder builder) {
        this.transactionId = builder.transactionId;
        this.userUniqueKey = builder.userUniqueKey;
        this.channelName = builder.channelName;
//        this.type = builder.type;
        this.isSupportTicket = builder.isSupportTicket;
        this.isInsertBotId = builder.isInsertBotId;
        this.skipBot = builder.skipBot;
        this.message = builder.message;
        this.tags = builder.tags;
        this.groupingTags = builder.groupingTags;
        this.otherUserUniqueKeys = builder.otherUserUniqueKeys;
        this.customAttributes = builder.customAttributes;
        this.agentEmail = builder.agentEmail;
        this.isSingleChannelTransactionId = builder.isSingleChannelTransactionId;
    }

}

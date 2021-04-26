package com.hippo.model;

import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.GroupingTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by bhavya on 22/08/17.
 */

public class FuguCreateConversationParams {

    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("label_id")
    @Expose
    private Long labelId = -1l;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("other_user_unique_key")
    @Expose
    private JsonArray otherUserUniqueKeys;
    @SerializedName("chat_type")
    @Expose
    private int chatType = 0;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
    @SerializedName("tags")
    @Expose
    private ArrayList<String> tags = new ArrayList<>();
    @SerializedName("user_first_messages")
    @Expose
    private String[] user_first_messages = null;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private int appVersion = HippoConfig.getInstance().getCodeVersion();
    @SerializedName("source_type")
    @Expose
    private int source = 1;
    @SerializedName("grouping_tags")
    @Expose
    private ArrayList<String> groupingTags = new ArrayList<>();
    @SerializedName("in_app_support_channel")
    @Expose
    private int isSupportTicket;

    @SerializedName("custom_attributes")
    @Expose
    public CustomAttributes customAttributes;

    @SerializedName("bot_form_muid")
    @Expose
    private String botFormMuid;

    @SerializedName("bot_group_id")
    @Expose
    private Integer botGroupId;

    @SerializedName("initiate_bot_group_id")
    @Expose
    private String initiateBotGroupId;

    @SerializedName("initial_bot_messages")
    @Expose
    private ArrayList<Object> initialBotMessages;
    @SerializedName("multi_channel_label_mapping_app")
    @Expose
    private int multiChannelLabelMapping;
    @SerializedName("skip_bot")
    @Expose
    private Integer skipBot;
    @SerializedName("skip_bot_reason")
    @Expose
    private String skipBotReason;
    @SerializedName("agent_email")
    @Expose
    private String agentEmail;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("multi_language_default_message")
    @Expose
    private String multiLanguageDefaultMessage;
    @SerializedName("multi_language_label")
    @Expose
    private String multiLanguageLabel;

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setMultiLanguageDefaultMessage(String multiLanguageDefaultMessage) {
        this.multiLanguageDefaultMessage = multiLanguageDefaultMessage;
    }

    public void setMultiLanguageLabel(String multiLanguageLabel) {
        this.multiLanguageLabel = multiLanguageLabel;
    }

    public void setMultiChannelLabelMapping(int multiChannelLabelMapping) {
        this.multiChannelLabelMapping = multiChannelLabelMapping;
    }

    public String getInitiateBotGroupId() {
        return initiateBotGroupId;
    }

    public void setInitiateBotGroupId(String initiateBotGroupId) {
        this.initiateBotGroupId = initiateBotGroupId;
    }

    public ArrayList<Object> getInitialBotMessages() {
        return initialBotMessages;
    }

    public void setInitialBotMessages(ArrayList<Object> initialBotMessages) {
        this.initialBotMessages = initialBotMessages;
    }

    public Integer getBotGroupId() {
        return botGroupId;
    }

    public void setBotGroupId(Integer botGroupId) {
        this.botGroupId = botGroupId;
    }

    public String getBotFormMuid() {
        return botFormMuid;
    }

    public void setBotFormMuid(String botFormMuid) {
        this.botFormMuid = botFormMuid;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public JsonArray getOtherUserUniqueKeys() {
        return otherUserUniqueKeys;
    }

    public void setOtherUserUniqueKeys(JsonArray otherUserUniqueKeys) {
        this.otherUserUniqueKeys = otherUserUniqueKeys;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String[] getUser_first_messages() {
        return user_first_messages;
    }

    public void setUser_first_messages(String[] user_first_messages) {
        this.user_first_messages = user_first_messages;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getIsSupportTicket() {
        return isSupportTicket;
    }

    public CustomAttributes getCustomAttributes() {
        return customAttributes;
    }

    public void setIsSupportTicket(int isSupportTicket) {
        this.isSupportTicket = isSupportTicket;
    }

    public void setCustomAttributes(CustomAttributes customAttributes) {
        this.customAttributes = customAttributes;
    }

    public FuguCreateConversationParams() {
        this.appVersion =HippoConfig.getInstance().getCodeVersion();
        //appVersionCode = BuildConfig.VERSION_CODE;

    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, String userUniqueKey, JsonArray otherUserUniqueKeys,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.otherUserUniqueKeys = otherUserUniqueKeys;
        this.deviceType = 1;
        this.enUserId=enUserId;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        if (otherUserUniqueKeys != null) {
            if (otherUserUniqueKeys.size() > 1) {
                this.chatType = 2;
            } else {
                this.chatType = 1;
            }
        } else {
            this.chatType = 0;
        }

        this.channelName = channelName;
        //this.tags = tags;
        source = 1;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        //this.tags = tags;
        this.enUserId=enUserId;
        this.deviceType = 1;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        source = 1;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId, String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.enUserId = enUserId;
        source = 1;
        appVersion = HippoConfig.getInstance().getCodeVersion();
        //appVersionCode = BuildConfig.VERSION_CODE;
    }

    @Override
    public String toString() {
        return appSecretKey + ", " + labelId + ", " + userId + ", " + chatType;
    }

    public FuguCreateConversationParams(String appSecretKey, String transactionId, String agentEmail,
                                        ArrayList<String> userUniqueKey) {
        this.appSecretKey = appSecretKey;
        this.transactionId = transactionId;
        this.agentEmail = agentEmail;
        this.chatType = 0;
        Gson gson = new GsonBuilder().create();

        if (userUniqueKey != null) {
            otherUserUniqueKeys = gson.toJsonTree(userUniqueKey).getAsJsonArray();
        }

        this.deviceType = 1;
        source = 1;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags, String[] user_first_messages,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        //this.tags = tags;
        this.enUserId=enUserId;
        this.user_first_messages = user_first_messages;
        this.deviceType = 1;
        this.appVersion = HippoConfig.getInstance().getCodeVersion();
        source = 1;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ArrayList<String> getGroupingTags() {
        return groupingTags;
    }

    public void setGroupingTags(ArrayList<String> groupingTags) {
        this.groupingTags = groupingTags;
    }

    public Integer getSkipBot() {
        return skipBot;
    }

    public void setSkipBot(Integer skipBot) {
        this.skipBot = skipBot;
    }

    public String getSkipBotReason() {
        return skipBotReason;
    }

    public void setSkipBotReason(String skipBotReason) {
        this.skipBotReason = skipBotReason;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }
}

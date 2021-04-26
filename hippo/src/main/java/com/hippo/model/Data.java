package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 17/06/19.
 *
 * @author gurmail
 */
public class Data {

    public Data() {
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @SerializedName("messages")
    @Expose
    private ArrayList<Message> messages = new ArrayList<>();
    @SerializedName("label")
    @Expose
    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setOnSubscribe(int onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("on_subscribe")
    @Expose
    private int onSubscribe;
    @SerializedName("page_size")
    @Expose
    private int pageSize;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;

    public void setStatus(Integer status) {
        this.status = status;
    }

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("business_name")
    @Expose
    private String businessName;

    public boolean isDisableReply() {
        return disableReply != null && disableReply == 1;
    }

    public void setDisableReply(Integer disableReply) {
        this.disableReply = disableReply;
    }

    public Integer getDisableReply() {
        return disableReply;
    }

    @SerializedName("disable_reply")
    @Expose
    private Integer disableReply;

    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("agent_name")
    @Expose
    private String agentName;
    @SerializedName("agent_image")
    @Expose
    private String agentImage;
    @SerializedName("user_id")
    @Expose
    private Long agentId;
    @SerializedName("other_users")
    @Expose
    private List<OtherUser> otherUsers = new ArrayList<OtherUser>();
    @SerializedName("allow_video_call")
    @Expose
    private Integer allowVideoCall;
    @SerializedName("allow_audio_call")
    @Expose
    private Integer allowAudioCall;
    @SerializedName("other_user_image")
    @Expose
    private String otherUserImage;
    @SerializedName("channel_image_url")
    @Expose
    private String channelImageUrl;
    @SerializedName("bot_group_id")
    @Expose
    private Integer botGroupId;
    @SerializedName("input_type")
    @Expose
    private String inputType;
    @SerializedName("restrict_personal_info_sharing")
    @Expose
    private int restrictPersonalInfo;

    public Integer getBotGroupId() {
        return botGroupId;
    }

    public void setBotGroupId(Integer botGroupId) {
        this.botGroupId = botGroupId;
    }

    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public String getOtherUserImage() {
        return otherUserImage;
    }

    public void setOtherUserImage(String otherUserImage) {
        this.otherUserImage = otherUserImage;
    }

    public boolean isAllowVideoCall() {
        try {
            return allowVideoCall == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAllowVideoCall(Integer allowVideoCall) {
        this.allowVideoCall = allowVideoCall;
    }

    public boolean isAllowAudioCall() {
        try {
            return allowAudioCall == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAllowAudioCall(Integer allowAudioCall) {
        this.allowAudioCall = allowAudioCall;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public String getLabel() {
        return label;
    }

    public String getFullName() {
        return fullName;
    }

    public int getOnSubscribe() {
        return onSubscribe;
    }

    public int getPageSize() {
        return pageSize;
    }


    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getStatus() {
        return status;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public List<OtherUser> getOtherUsers() {
        return otherUsers;
    }

    public void setOtherUsers(List<OtherUser> otherUsers) {
        this.otherUsers = otherUsers;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getAgentImage() {
        return agentImage;
    }

    public void setAgentImage(String agentImage) {
        this.agentImage = agentImage;
    }

    public boolean getRestrictPersonalInfo() {
        return restrictPersonalInfo == 1;
    }

    public void setRestrictPersonalInfo(int restrictPersonalInfo) {
        this.restrictPersonalInfo = restrictPersonalInfo;
    }
}

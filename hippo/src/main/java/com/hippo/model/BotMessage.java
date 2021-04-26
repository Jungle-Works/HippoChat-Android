package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hippo.constant.FuguAppConstant;

import java.util.ArrayList;
import java.util.List;

import static com.hippo.constant.FuguAppConstant.FEEDBACK_MESSAGE;

/**
 * Created by gurmail on 2019-08-22.
 *
 * @author gurmail
 */
public class BotMessage {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("selected_btn_id")
    @Expose
    private String selectedBtnId;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("is_typing")
    @Expose
    private Integer isTyping;
    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("replied_by")
    @Expose
    private String repliedBy;
    @SerializedName("replied_by_id")
    @Expose
    private Long repliedById;
    @SerializedName("content_value")
    @Expose
    private List<ContentValue> contentValue = new ArrayList<>();
    @SerializedName("bot_group_id")
    @Expose
    private Integer botGroupId;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("is_skip_event")
    @Expose
    private int isSkipEvent;
    @SerializedName("is_skip_button")
    @Expose
    private int isSkipButton;
    @SerializedName("is_from_bot")
    @Expose
    private Integer isFromBot;
    @SerializedName("values")
    @Expose
    private ArrayList<String> values;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getSelectedBtnId() {
        return selectedBtnId;
    }

    public void setSelectedBtnId(String selectedBtnId) {
        this.selectedBtnId = selectedBtnId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getIsTyping() {
        return isTyping;
    }

    public void setIsTyping(Integer isTyping) {
        this.isTyping = isTyping;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(String repliedBy) {
        this.repliedBy = repliedBy;
    }

    public Long getRepliedById() {
        return repliedById;
    }

    public void setRepliedById(Long repliedById) {
        this.repliedById = repliedById;
    }

    public List<ContentValue> getContentValue() {
        return contentValue;
    }

    public void setContentValue(List<ContentValue> contentValue) {
        this.contentValue = contentValue;
    }

    public Integer getBotGroupId() {
        return botGroupId;
    }

    public void setBotGroupId(Integer botGroupId) {
        this.botGroupId = botGroupId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getIsSkipEvent() {
        return isSkipEvent;
    }

    public void setIsSkipEvent(int isSkipEvent) {
        this.isSkipEvent = isSkipEvent;
    }

    public int getIsSkipButton() {
        return isSkipButton;
    }

    public void setIsSkipButton(int isSkipButton) {
        this.isSkipButton = isSkipButton;
    }

    public Integer getIsFromBot() {
        return isFromBot;
    }

    public void setIsFromBot(Integer isFromBot) {
        this.isFromBot = isFromBot;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}

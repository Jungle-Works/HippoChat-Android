package com.hippo.model;

import com.hippo.constant.FuguAppConstant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.hippo.constant.FuguAppConstant.FEEDBACK_MESSAGE;

/**
 * Created by Bhavya Rattan on 12/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class Message {
    @SerializedName("full_name")
    @Expose
    private String fromName;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("date_time")
    @Expose
    private String sentAtUtc = "";
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;
    @SerializedName("message_state")
    @Expose
    private Integer messageState = 0;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("image_url")
    @Expose
    private String url = "";
    @SerializedName("url")
    @Expose
    private String fileUrl = "";
    @SerializedName("message_type")
    @Expose
    private int messageType = FuguAppConstant.TEXT_MESSAGE;
    private int originalMessageType = FuguAppConstant.TEXT_MESSAGE;

    @SerializedName("custom_action")
    private CustomAction customAction;
    @SerializedName("file_name")
    @Expose
    String fileName = "";
    @SerializedName("file_size")
    @Expose
    String fileSize = "";
    @SerializedName("file_extension")
    @Expose
    String fileExtension = "";
    @SerializedName("file_path")
    @Expose
    String filePath = "";
    @SerializedName("image_height")
    @Expose
    private int imageHeight = 700;

    @SerializedName("image_width")
    @Expose
    private int imageWidth = 700;
    private int messageIndex = 0;
    private int timeIndex = 0;
    private boolean isSelf;
    private String localImagePath;
    private boolean isDateView = false;

    public boolean isDateView() {
        return isDateView;
    }

    public void setDateView(boolean dateView) {
        isDateView = dateView;
    }

    @SerializedName("is_rating_given")
    @Expose
    int isRatingGiven;
    @SerializedName("total_rating")
    @Expose
    int totalRating;
    @SerializedName("rating_given")
    @Expose
    int ratingGiven;
    @SerializedName("muid")
    @Expose
    String muid;
    @SerializedName("comment")
    @Expose
    String comment;
    @SerializedName("line_before_feedback")
    @Expose
    String lineBeforeFeedback;
    @SerializedName("line_after_feedback_1")
    @Expose
    String lineAfterFeedback_1;
    @SerializedName("line_after_feedback_2")
    @Expose
    String lineAfterFeedback_2;
    private Long message_id;
    @SerializedName("values")
    @Expose
    private ArrayList<String> values;
    @SerializedName("content_value")
    @Expose
    private List<ContentValue> contentValue = new ArrayList<>();
    @SerializedName("default_action_id")
    @Expose
    private String defaultActionId;
    @SerializedName("video_call_duration")
    @Expose
    private int videoCallDuration;

    public void setCallType(String callType) {
        this.callType = callType;
    }

    @SerializedName("call_type")
    @Expose
    private String callType;

    @SerializedName("document_type")
    @Expose
    private String documentType;
    @SerializedName("currentprogress")
    private int currentprogress;

    @SerializedName("downloadStatus")
    private int downloadStatus = -1;
    @SerializedName("uploadStatus")
    private int uploadStatus = 0;
    @SerializedName("isAudioPlaying")
    private boolean isAudioPlaying = false;

    @SerializedName("selected_agent_id")
    @Expose
    private String selectedAgentId;
    @SerializedName("fallback_text")
    @Expose
    private String fallbackText;
    @SerializedName("selected_btn_id")
    @Expose
    private String selectedBtnId;
    @SerializedName("is_active")
    @Expose
    private int isActive;
    @SerializedName("integration_source")
    @Expose
    private int integrationSource = 0;


    @SerializedName("user_type")
    @Expose
    private int userType = 1;

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @SerializedName("is_editable")
    @Expose
    private int isEditable = 0;
    @SerializedName("is_skip_event")
    @Expose
    private int isSkipEvent;
    @SerializedName("is_skip_button")
    @Expose
    private int isSkipButton;
    @SerializedName("is_from_bot")
    @Expose
    private Integer isFromBot;
    @SerializedName("input_type")
    @Expose
    private String inputType;

    public boolean isCheckAndCreateCustomer() {
        return checkAndCreateCustomer;
    }

    public void setCheckAndCreateCustomer(boolean checkAndCreateCustomer) {
        this.checkAndCreateCustomer = checkAndCreateCustomer;
    }

    private boolean checkAndCreateCustomer = false;

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    private boolean isEdited = false;

    public boolean isSkipButton() {
        return isSkipButton == 1;
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

    public int getIsSkipEvent() {
        return isSkipEvent;
    }

    public void setIsSkipEvent(int isSkipEvent) {
        this.isSkipEvent = isSkipEvent;
    }

    private boolean hasImageView = false;

    private boolean hasNameView = false;

    public boolean isHasNameView() {
        return userId.compareTo(aboveUserId) != 0;
    }

    public boolean isHasImageView() {
        return aboveUserId != null && (aboveUserId == -2 || aboveUserId.compareTo(userId) != 0);
        //return belowUserId == -2 || belowUserId.compareTo(userId) != 0;
    }

    private String aboveMuid;
    private Long aboveUserId = -1L;
    private String belowMuid;
    private Long belowUserId = -1L;
    private String date;

    private Boolean isEditMode = false;

    public Boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(Boolean editMode) {
        isEditMode = editMode;
    }

    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("multi_lang_message")
    @Expose
    private String multiLangMessage;

    @SerializedName("customer_initial_form_info")
    @Expose
    private CustomerInitialFormInfo customerInitialFormInfo = new CustomerInitialFormInfo();

    public CustomerInitialFormInfo getCustomerInitialFormInfo() {
        return customerInitialFormInfo;
    }

    public void setCustomerInitialFormInfo(CustomerInitialFormInfo customerInitialFormInfo) {
        this.customerInitialFormInfo = customerInitialFormInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAboveMuid() {
        return aboveMuid;
    }

    public void setAboveMuid(String aboveMuid) {
        this.aboveMuid = aboveMuid;
    }

    public String getBelowMuid() {
        return belowMuid;
    }

    public void setBelowMuid(String belowMuid) {
        this.belowMuid = belowMuid;
    }

    public Long getAboveUserId() {
        return aboveUserId;
    }

    public void setAboveUserId(Long aboveUserId) {
        this.aboveUserId = aboveUserId;
    }

    public Long getBelowUserId() {
        return belowUserId;
    }

    public void setBelowUserId(Long belowUserId) {
        this.belowUserId = belowUserId;
    }

    public void setHasImageView(boolean hasImageView) {
        this.hasImageView = hasImageView;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getIntegrationSource() {
        return integrationSource;
    }

    public void setIntegrationSource(int integrationSource) {
        this.integrationSource = integrationSource;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getSelectedBtnId() {
        return selectedBtnId;
    }

    public void setSelectedBtnId(String selectedBtnId) {
        this.selectedBtnId = selectedBtnId;
    }

    public String getFallbackText() {
        return fallbackText;
    }

    public void setFallbackText(String fallbackText) {
        this.fallbackText = fallbackText;
    }

    public String getSelectedAgentId() {
        return selectedAgentId;
    }

    public void setSelectedAgentId(String selectedAgentId) {
        this.selectedAgentId = selectedAgentId;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }

    public int getCurrentprogress() {
        return currentprogress;
    }

    public void setCurrentprogress(int currentprogress) {
        this.currentprogress = currentprogress;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCallType() {
        return callType;
    }

    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void setCustomAction(final CustomAction customAction) {
        this.customAction = customAction;
    }

    public CustomAction getCustomAction() {
        return customAction;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    private int downloadId;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getfromName() {
        return fromName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSentAtUtc() {
        return sentAtUtc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getOriginalMessageType() {
        return originalMessageType;
    }

    public void setOriginalMessageType(int originalMessageType) {
        this.originalMessageType = originalMessageType;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getType() {
        return messageType;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Long getId() {
        if (id == null) {
            id = -1l;
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message() {
    }

    public Message(String muid, int messageType) {
        this.muid = muid;
        this.messageType = messageType;
    }


    public Message(String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, int messageType, String muid) {
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.messageType = messageType;
        this.muid = muid;
    }

    public Message(long id, String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, String url, String thumbnailUrl, int messageType,
                   String muid) {
        this.id = id;
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.url = url;
        this.muid = muid;
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String date, boolean isDateView) {
        this.date = date;
        this.isDateView = isDateView;
        this.messageType = 0;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public boolean isRating() {
        return messageType == FEEDBACK_MESSAGE;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public int getRatingGiven() {
        return ratingGiven;
    }

    public void setRatingGiven(int ratingGiven) {
        this.ratingGiven = ratingGiven;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public void setIsRatingGiven(int isRatingGiven) {
        this.isRatingGiven = isRatingGiven;
    }

    public int getIsRatingGiven() {
        return isRatingGiven;
    }

    public boolean isRatingGiven() {
        return isRatingGiven == 1;
    }


    public String getLineBeforeFeedback() {
        return lineBeforeFeedback;
    }

    public void setLineBeforeFeedback(String lineBeforeFeedback) {
        this.lineBeforeFeedback = lineBeforeFeedback;
    }

    public String getLineAfterFeedback_1() {
        return lineAfterFeedback_1;
    }

    public void setLineAfterFeedback_1(String lineAfterFeedback_1) {
        this.lineAfterFeedback_1 = lineAfterFeedback_1;
    }

    public String getLineAfterFeedback_2() {
        return lineAfterFeedback_2;
    }

    public void setLineAfterFeedback_2(String lineAfterFeedback_2) {
        this.lineAfterFeedback_2 = lineAfterFeedback_2;
    }

    public void setMessageId(Long message_id) {
        this.message_id = message_id;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public List<ContentValue> getContentValue() {
        return contentValue;
    }

    public void setContentValue(List<ContentValue> contentValue) {
        this.contentValue = contentValue;
    }

    public String getDefaultActionId() {
        return defaultActionId;
    }

    public void setDefaultActionId(String defaultActionId) {
        this.defaultActionId = defaultActionId;
    }

    public int getIsMessageExpired() {
        return isMessageExpired;
    }

    public void setIsMessageExpired(int isMessageExpired) {
        this.isMessageExpired = isMessageExpired;
    }

    private int isMessageExpired = 0;

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public int getVideoCallDuration() {
        return videoCallDuration;
    }

    public void setVideoCallDuration(int videoCallDuration) {
        this.videoCallDuration = videoCallDuration;
    }

    public Integer getMessageState() {
        return messageState;
    }

    public void setMessageState(Integer messageState) {
        this.messageState = messageState;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return ((Message) obj).getMuid().equalsIgnoreCase(getMuid());
        } catch (Exception e) {
            return false;
        }
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getMultiLangMessage() {
        return multiLangMessage;
    }

    public void setMultiLangMessage(String multiLangMessage) {
        this.multiLangMessage = multiLangMessage;
    }
}

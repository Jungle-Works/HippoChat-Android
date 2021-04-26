package com.hippo.activity;

import com.hippo.model.*;
import com.hippo.model.labelResponse.GetLabelMessageResponse;
import com.hippo.utils.fileUpload.FileuploadModel;
import org.json.JSONObject;

/**
 * Created by gurmail on 2019-08-28.
 *
 * @author gurmail
 */
public class CreateChannelAttribute {

    private int messageType;
    private Long labelId;
    private String text;
    private String url;
    private String thumbnailUrl;
    private FuguFileDetails fileDetails;
    private boolean isP2P;
    private JSONObject jsonObject;
    private String botMessageMuid;
    private Message message;
    private BotMessage botMessage;
    private FuguGetMessageResponse mFuguGetMessageResponse;
    private GetLabelMessageResponse labelMessageResponse;
    private FuguCreateConversationParams fuguCreateConversationParams;
    private FileuploadModel fileuploadModel;
    private boolean isPaymentBot;
    private int botId;

    public boolean hasJson() {
        return getJsonObject() != null;
    }

    public String getText() {
        return text;
    }

    public int getMessageType() {
        return messageType;
    }

    public Long getLabelId() {
        return labelId;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public FuguFileDetails getFileDetails() {
        return fileDetails;
    }

    public boolean isP2P() {
        return isP2P;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getBotMessageMuid() {
        return botMessageMuid;
    }

    public Message getMessage() {
        return message;
    }

    public BotMessage getBotMessage() {
        return botMessage;
    }

    public FuguGetMessageResponse getmFuguGetMessageResponse() {
        return mFuguGetMessageResponse;
    }

    public GetLabelMessageResponse getLabelMessageResponse() {
        return labelMessageResponse;
    }

    public FuguCreateConversationParams getFuguCreateConversationParams() {
        return fuguCreateConversationParams;
    }

    public boolean isPaymentBot() {
        return isPaymentBot;
    }

    public int getBotId() {
        return botId;
    }

    public FileuploadModel getFileuploadModel() {
        return fileuploadModel;
    }

    public static class Builder {
        private int messageType;
        private Long labelId;
        private String url;
        private String text;
        private String thumbnailUrl;
        private FuguFileDetails fileDetails;
        private boolean isP2P;
        private JSONObject jsonObject;
        private String botMessageMuid;
        private Message message;
        private BotMessage botMessage;
        private FuguGetMessageResponse mFuguGetMessageResponse;
        private GetLabelMessageResponse labelMessageResponse;
        private FuguCreateConversationParams fuguCreateConversationParams;
        private FileuploadModel fileuploadModel;
        private boolean isPaymentBot;
        private int botId;

        private CreateChannelAttribute attributes = new CreateChannelAttribute(this);

        public CreateChannelAttribute build() {
            return attributes;
        }

        public Builder setText(String text) {
            attributes.text = text;
            return this;
        }

        public Builder setMessageType(int messageType) {
            attributes.messageType = messageType;
            return this;
        }
        public Builder setLabelId(Long labelId) {
            attributes.labelId = labelId;
            return this;
        }

        public Builder setUrl(String url) {
            attributes.url = url;
            return this;
        }
        public Builder setThumbnailUrl(String thumbnailUrl) {
            attributes.thumbnailUrl = thumbnailUrl;
            return this;
        }
        public Builder setFileDetails(FuguFileDetails fileDetails) {
            attributes.fileDetails = fileDetails;
            return this;
        }
        public Builder setIsP2P(boolean isP2P) {
            attributes.isP2P = isP2P;
            return this;
        }
        public Builder setJsonObject(JSONObject jsonObject) {
            attributes.jsonObject = jsonObject;
            return this;
        }
        public Builder setBotMessageMuid(String botMessageMuid) {
            attributes.botMessageMuid = botMessageMuid;
            return this;
        }
        public Builder setMessage(Message message) {
            attributes.message = message;
            return this;
        }
        public Builder setBotMessage(BotMessage botMessage) {
            attributes.botMessage = botMessage;
            return this;
        }
        public Builder setFuguGetMessageResponse(FuguGetMessageResponse mFuguGetMessageResponse) {
            attributes.mFuguGetMessageResponse = mFuguGetMessageResponse;
            return this;
        }
        public Builder setGetLabelMessageResponse(GetLabelMessageResponse labelMessageResponse) {
            attributes.labelMessageResponse = labelMessageResponse;
            return this;
        }
        public Builder setConversationParams(FuguCreateConversationParams fuguCreateConversationParams) {
            attributes.fuguCreateConversationParams = fuguCreateConversationParams;;
            return this;
        }
        public Builder setFileuploadModel(FileuploadModel fileuploadModel) {
            attributes.fileuploadModel = fileuploadModel;;
            return this;
        }

        public Builder setIsPaymentBot(boolean isPaymentBot) {
            attributes.isPaymentBot = isPaymentBot;
            return this;
        }

        public Builder setBotId(int botId) {
            attributes.botId = botId;
            return this;
        }
    }

    private CreateChannelAttribute(Builder builder) {
        this.messageType = builder.messageType;
        this.text = builder.text;
        this.labelId = builder.labelId;
        this.url = builder.url;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.fileDetails = builder.fileDetails;
        this.isP2P = builder.isP2P;
        this.jsonObject = builder.jsonObject;
        this.botMessageMuid = builder.botMessageMuid;
        this.message = builder.message;
        this.botMessage = builder.botMessage;
        this.mFuguGetMessageResponse = builder.mFuguGetMessageResponse;
        this.labelMessageResponse = builder.labelMessageResponse;
        this.fuguCreateConversationParams = builder.fuguCreateConversationParams;
        this.fileuploadModel = builder.fileuploadModel;
        this.botId = builder.botId;
        this.isPaymentBot = builder.isPaymentBot;
    }

}

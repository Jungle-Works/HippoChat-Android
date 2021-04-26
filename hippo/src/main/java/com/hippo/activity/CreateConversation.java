package com.hippo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.database.CommonData;
import com.hippo.model.BotMessage;
import com.hippo.model.FuguCreateConversationParams;
import com.hippo.model.FuguCreateConversationResponse;
import com.hippo.model.Message;
import com.hippo.model.labelResponse.LabelMessage;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.DateUtils;
import com.hippo.utils.HippoLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.hippo.constant.FuguAppConstant.*;

/**
 * Created by gurmail on 2019-08-28.
 *
 * @author gurmail
 */
public class CreateConversation {

    private Activity context;
    private boolean hasBotMessage = false, showLoader = false;
    ;
    private String userName;
    private Long userId;
    private boolean insertBotId;


    private int messageType;


    public CreateConversation(Activity context, String userName, Long userId, boolean showLoader, boolean insertBotId) {
        this.context = context;
        this.userName = userName;
        this.userId = userId;
        this.showLoader = showLoader;
        this.insertBotId = insertBotId;
    }

    protected void createChannel(final CreateChannelAttribute attributes, ArrayList<Message> fuguMessageList, final Callback callback) {
        createChannel(attributes, fuguMessageList, 0, callback);
    }

    protected void createChannel(final CreateChannelAttribute attributes, ArrayList<Message> fuguMessageList, int skipBot, final Callback callback) {
        FuguCreateConversationParams fuguCreateConversationParams = attributes.getFuguCreateConversationParams();
        messageType = attributes.getMessageType();
        ArrayList<String> groupingTags = new ArrayList<>();
        if (attributes.getFuguCreateConversationParams().getGroupingTags() != null && fuguCreateConversationParams.getGroupingTags().size() > 0)
            groupingTags.addAll(fuguCreateConversationParams.getGroupingTags());

        try {
            if (CommonData.getUserDetails() != null && CommonData.getUserDetails().getData() != null
                    && CommonData.getUserDetails().getData().getGroupingTags() != null
                    && CommonData.getUserDetails().getData().getGroupingTags().size() > 0) {
                for (com.hippo.model.GroupingTag groupingTag : CommonData.getUserDetails().getData().getGroupingTags()) {
                    groupingTags.add(groupingTag.getTagName());
                }
            }
        } catch (Exception e) {

        }

        fuguCreateConversationParams.setGroupingTags(groupingTags);

        if (attributes.getLabelId() != null && attributes.getLabelId().compareTo(-1l) > 0) {
            fuguCreateConversationParams.setLabelId(attributes.getLabelId());
        }

        if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
            fuguCreateConversationParams.setBotGroupId(HippoConfig.getInstance().getBotId());
        }

        if (HippoConfig.getInstance().getSkipBot()) {
            fuguCreateConversationParams.setSkipBot(1);
            if (!TextUtils.isEmpty(HippoConfig.getInstance().getSkipBotReason()))
                fuguCreateConversationParams.setSkipBotReason(HippoConfig.getInstance().getSkipBotReason());
        }

        if (!TextUtils.isEmpty(attributes.getBotMessageMuid())) {
            fuguCreateConversationParams.setBotFormMuid(attributes.getBotMessageMuid());
        }


        if (skipBot == 1) {
            fuguCreateConversationParams.setSkipBot(1);
        }

        if (attributes.isPaymentBot()) {
            fuguCreateConversationParams.setInitiateBotGroupId("" + attributes.getBotId());
            //fuguCreateConversationParams.setInitialBotMessages(new ArrayList<>());
        } else {
            if (attributes.getLabelMessageResponse() != null &&
                    attributes.getLabelMessageResponse().getData() != null) {

                int botId = 0;
                if (!TextUtils.isEmpty(attributes.getLabelMessageResponse().getData().getBotGroupId())
                        && Integer.parseInt(attributes.getLabelMessageResponse().getData().getBotGroupId()) > 0) {
                    botId = Integer.parseInt(attributes.getLabelMessageResponse().getData().getBotGroupId());
                }


                if (botId < 1) {
                    if (attributes.getLabelMessageResponse().getData().getMessages() != null &&
                            attributes.getLabelMessageResponse().getData().getMessages().size() > 0) {
                        LabelMessage labelMessage = attributes.getLabelMessageResponse().getData().getMessages().get(0);
                        if (labelMessage.getOtherLangData() != null) {
                            fuguCreateConversationParams.setMultiLanguageDefaultMessage(labelMessage.getOtherLangData().getChannelMessage());
                            fuguCreateConversationParams.setMultiLanguageLabel(labelMessage.getOtherLangData().getChannelName());
                        }
                    }
                } else if (botId > -1) {

                    hasBotMessage = true;
                    messageType = 0;

                    if (botId > 0) {
                        fuguCreateConversationParams.setInitiateBotGroupId(attributes.getLabelMessageResponse().getData().getBotGroupId() + "");
                    }

                    try {
                        LabelMessage labelMessage = attributes.getLabelMessageResponse().getData().getMessages().get(0);
                        if (labelMessage.getOtherLangData() != null) {
                            fuguCreateConversationParams.setMultiLanguageLabel(labelMessage.getOtherLangData().getChannelName());
                        }
                    } catch (Exception e) {

                    }

                    ArrayList<Object> arrayList = new ArrayList<>();
                    if ((attributes.getMessageType() == 1 && !TextUtils.isEmpty(attributes.getText())) ||
                            attributes.getFileuploadModel() != null) {
                        for (Message message : fuguMessageList) {
                            if (!message.isDateView() && message.getDocumentType() == null) {
                                if (TextUtils.isEmpty(message.getMuid()))
                                    message.setMuid(UUID.randomUUID().toString() + "." + new Date().getTime());
                                arrayList.add(message);
                            }
                        }
                        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                        String removeGt = "";
                        if (attributes.getMessageType() == 1 && !TextUtils.isEmpty(attributes.getText().trim())) {
                            String removeLt = attributes.getText().trim().replaceAll("<", "&lt;");
                            removeGt = removeLt.replaceAll(">", "&gt;");
                        }

                        Message messageObj = new Message(0,
                                userName,
                                userId,
                                removeGt,
                                DateUtils.getInstance().convertToUTC(localDate),
                                true,
                                MESSAGE_UNSENT,
                                attributes.getLabelMessageResponse().getData().getMessages().size(),
                                "",
                                "",
                                attributes.getMessageType(),
                                UUID.randomUUID().toString() + "." + new Date().getTime());

                        messageObj.setUserType(1);
                        messageObj.setOriginalMessageType(attributes.getMessageType());
                        messageObj.setMessageType(attributes.getMessageType());
                        messageObj.setIntegrationSource(0);
                        messageObj.setIsMessageExpired(0);
                        messageObj.setMessageState(MESSAGE_UNSENT);
                        messageObj.setUserId(userId);
                        messageObj.setFromName(userName);

                        if (attributes.getFileuploadModel() != null) {
                            messageObj.setMessageType(attributes.getFileuploadModel().getMessageType());
                            messageObj.setUrl(attributes.getFileuploadModel().getMessageObject().optString(IMAGE_URL));
                            messageObj.setThumbnailUrl(attributes.getFileuploadModel().getMessageObject().optString(THUMBNAIL_URL));
                            messageObj.setDocumentType(attributes.getFileuploadModel().getDocumentType());
                            messageObj.setMuid(attributes.getFileuploadModel().getMuid());
                        }

                        arrayList.add(messageObj);
                        fuguCreateConversationParams.setInitialBotMessages(arrayList);
                    } else {
                        for (LabelMessage msg : attributes.getLabelMessageResponse().getData().getMessages()) {
                            if (msg.getMessageType() == 20) {
                                arrayList.add(attributes.getBotMessage());
                            } else if (msg.getMessageType() == 17) {
                                String botMessageMuid = attributes.getBotMessageMuid();
                                if (TextUtils.isEmpty(botMessageMuid))
                                    botMessageMuid = UUID.randomUUID().toString() + "." + new Date().getTime();

                                Message message = attributes.getMessage();
                                BotMessage botMessage = new BotMessage();
                                if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                                    botMessage.setBotGroupId(HippoConfig.getInstance().getBotId());
                                } else {
                                    botMessage.setBotGroupId(null);
                                }
                                botMessage.setContentValue(message.getContentValue());
                                try {
                                    botMessage.getContentValue().get(0).setTextValue("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                botMessage.setValues(message.getValues());

                                botMessage.setDateTime(message.getDate());
                                botMessage.setFullName(userName);
                                botMessage.setIsTyping(0);
                                botMessage.setMessage(message.getMessage());
                                botMessage.setMessageStatus(MESSAGE_UNSENT);
                                botMessage.setMessageType(message.getOriginalMessageType());
                                botMessage.setMuid(botMessageMuid);
                                botMessage.setUserImage(attributes.getJsonObject().optString(USER_IMAGE));

                                botMessage.setUserId(userId);
                                botMessage.setUserType(ANDROID_USER);

                                botMessage.setUserId(message.getUserId());
                                botMessage.setUserType(message.getUserType());

                                botMessage.setIsFromBot(attributes.getJsonObject().optInt("is_from_bot"));
                                botMessage.setIsSkipButton(attributes.getJsonObject().optInt("is_skip_button"));
                                botMessage.setIsSkipEvent(attributes.getJsonObject().optInt("is_skip_event"));

                                if (botMessage.getIsSkipEvent() == 1) {
                                    hasBotMessage = false;
                                }

                                arrayList.add(botMessage);
                            } else if (msg.getMessageType() == 16) {
                                // do nothing
                            } else if (msg.getMessageType() == 14) {
                                // do nothing
                            } else {
                                arrayList.add(msg);
                            }
                        }
                        fuguCreateConversationParams.setInitialBotMessages(arrayList);
                    }
                }
            } else if (attributes.getmFuguGetMessageResponse() != null &&
                    attributes.getmFuguGetMessageResponse().getData() != null &&
                    attributes.getmFuguGetMessageResponse().getData().getBotGroupId() != null &&
                    attributes.getmFuguGetMessageResponse().getData().getBotGroupId() > -1) {
                hasBotMessage = true;
                messageType = 0;

                if (attributes.getmFuguGetMessageResponse().getData().getBotGroupId() != null &&
                        attributes.getmFuguGetMessageResponse().getData().getBotGroupId().intValue() > 0) {
                    fuguCreateConversationParams.setInitiateBotGroupId(attributes.getmFuguGetMessageResponse().getData().getBotGroupId() + "");
                }

                ArrayList<Object> arrayList = new ArrayList<>();
                if ((attributes.getMessageType() == 1 && !TextUtils.isEmpty(attributes.getText())) ||
                        attributes.getFileuploadModel() != null) {
                    for (Message message : fuguMessageList) {
                        if (!message.isDateView()) {
                            if (TextUtils.isEmpty(message.getMuid()))
                                message.setMuid(UUID.randomUUID().toString() + "." + new Date().getTime());
                            arrayList.add(message);
                        }
                    }
                    String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                    String removeGt = "";
                    if (attributes.getMessageType() == 1 && !TextUtils.isEmpty(attributes.getText().trim())) {
                        String removeLt = attributes.getText().trim().replaceAll("<", "&lt;");
                        removeGt = removeLt.replaceAll(">", "&gt;");
                    }

                    Message messageObj = new Message(0,
                            userName,
                            userId,
                            removeGt,
                            DateUtils.getInstance().convertToUTC(localDate),
                            true,
                            MESSAGE_UNSENT,
                            attributes.getmFuguGetMessageResponse().getData().getMessages().size(),
                            "",
                            "",
                            attributes.getMessageType(),
                            UUID.randomUUID().toString() + "." + new Date().getTime());

                    messageObj.setUserType(1);
                    messageObj.setOriginalMessageType(attributes.getMessageType());
                    messageObj.setMessageType(attributes.getMessageType());
                    messageObj.setIntegrationSource(0);
                    messageObj.setIsMessageExpired(0);
                    messageObj.setMessageState(MESSAGE_UNSENT);
                    messageObj.setUserId(userId);
                    messageObj.setFromName(userName);

                    if (attributes.getFileuploadModel() != null) {
                        messageObj.setMessageType(attributes.getFileuploadModel().getMessageType());
                        messageObj.setUrl(attributes.getFileuploadModel().getMessageObject().optString(IMAGE_URL));
                        messageObj.setThumbnailUrl(attributes.getFileuploadModel().getMessageObject().optString(THUMBNAIL_URL));
                        messageObj.setDocumentType(attributes.getFileuploadModel().getDocumentType());
                        messageObj.setMuid(attributes.getFileuploadModel().getMuid());
                    }

                    arrayList.add(messageObj);
                    fuguCreateConversationParams.setInitialBotMessages(arrayList);
                } else {
                    for (Message msg : attributes.getmFuguGetMessageResponse().getData().getMessages()) {
                        if (msg.getMessageType() == 20) {
                            arrayList.add(attributes.getBotMessage());
                        } else if (msg.getMessageType() == 17) {
                            String botMessageMuid = attributes.getBotMessageMuid();
                            if (TextUtils.isEmpty(botMessageMuid))
                                botMessageMuid = UUID.randomUUID().toString() + "." + new Date().getTime();

                            Message message = attributes.getMessage();
                            BotMessage botMessage = new BotMessage();
                            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                                botMessage.setBotGroupId(HippoConfig.getInstance().getBotId());
                            } else {
                                botMessage.setBotGroupId(null);
                            }
                            botMessage.setContentValue(message.getContentValue());
                            try {
                                botMessage.getContentValue().get(0).setTextValue("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            botMessage.setValues(message.getValues());

                            botMessage.setDateTime(message.getDate());
                            botMessage.setFullName(userName);
                            botMessage.setIsTyping(0);
                            botMessage.setMessage(message.getMessage());
                            botMessage.setMessageStatus(MESSAGE_UNSENT);
                            botMessage.setMessageType(message.getOriginalMessageType());
                            botMessage.setMuid(botMessageMuid);
                            botMessage.setUserImage(attributes.getJsonObject().optString(USER_IMAGE));

                            botMessage.setUserId(userId);
                            botMessage.setUserType(ANDROID_USER);

                            botMessage.setUserId(message.getUserId());
                            botMessage.setUserType(message.getUserType());

                            botMessage.setIsFromBot(attributes.getJsonObject().optInt("is_from_bot"));
                            botMessage.setIsSkipButton(attributes.getJsonObject().optInt("is_skip_button"));
                            botMessage.setIsSkipEvent(attributes.getJsonObject().optInt("is_skip_event"));

                            if (botMessage.getIsSkipEvent() == 1) {
                                hasBotMessage = false;
                            }

                            arrayList.add(botMessage);
                        } else if (msg.getMessageType() == 16) {
                            // do nothing
                        } else if (msg.getMessageType() == 14) {
                            // do nothing
                        } else {
                            arrayList.add(msg);
                        }
                    }
                    fuguCreateConversationParams.setInitialBotMessages(arrayList);
                }
            }
        }


        if (insertBotId && !TextUtils.isEmpty(CommonData.getUpdatedDetails().getData().getCustomerConversationBotId())) {
            fuguCreateConversationParams.setInitiateBotGroupId(CommonData.getUpdatedDetails().getData().getCustomerConversationBotId());
        }

        if (!attributes.isPaymentBot() && CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping() == 1) {
            ArrayList<String> tags = new ArrayList<>();
            if (fuguCreateConversationParams != null && fuguCreateConversationParams.getTags() != null &&
                    fuguCreateConversationParams.getTags().size() > 0) {
                tags.addAll(fuguCreateConversationParams.getTags());
            }
            String appName = getApplicationName(context);
            tags.add(appName + " Android");
            fuguCreateConversationParams.setTags(tags);

            fuguCreateConversationParams.setMultiChannelLabelMapping(CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping());
        }

        if (!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
            fuguCreateConversationParams.setLang(HippoConfig.getInstance().getCurrentLanguage());


        HippoLog.e("CREATE", "" + new Gson().toJson(fuguCreateConversationParams));


        RestClient.getApiInterface().createConversation(fuguCreateConversationParams)
                .enqueue(new ResponseResolver<FuguCreateConversationResponse>(context, showLoader, true) {
                    @Override
                    public void success(FuguCreateConversationResponse fuguCreateConversationResponse) {

                        if (callback != null) {
                            callback.onSuccess(fuguCreateConversationResponse, messageType, attributes.getJsonObject(), attributes.getMessage(), hasBotMessage);
                        }
                    }

                    @Override
                    public void failure(APIError error) {

                        if (callback != null)
                            callback.onFailure(error);
                    }

                });

    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public interface Callback {
        void onSuccess(FuguCreateConversationResponse fuguCreateConversationResponse, int messageType, JSONObject data, Message message, boolean hasBotMessage);

        void onFailure(APIError error);
    }
}

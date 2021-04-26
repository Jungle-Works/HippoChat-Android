package com.hippo.support.Utils;

import android.text.TextUtils;

import com.hippo.HippoConfig;
import com.hippo.database.CommonData;
import com.hippo.model.CustomAttributes;
import com.hippo.support.model.HippoSendQueryParams;
import com.hippo.support.model.TagsModel;
import com.hippo.support.model.callbackModel.OpenChatParams;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 06/04/18.
 * @author gurmail
 */

public class CommonSupportParam {

    public CommonSupportParam() {

    }

    /**
     * Used to open chat screens
     * @param categoryName
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     * @return
     */
    public OpenChatParams getOpenChatParam(String categoryName, String transactionId, String userUniqueId,
                                           int supportId, ArrayList<String> pathList, String subHeader) {
        String channelName = getChannelName(pathList.get(pathList.size() - 1), userUniqueId, supportId, transactionId);
        String message = getMessage(userUniqueId, transactionId, categoryName, pathList.get(pathList.size() - 1),
                null, subHeader);

        if(!TextUtils.isEmpty(transactionId)) {
            transactionId = transactionId + "_" + supportId;
        } else {
            transactionId = userUniqueId + "_" + supportId;
        }

        String data[] = new String[1];
        data[0] = message;

        ArrayList<String> tagsList = new ArrayList<>();
        tagsList.add(categoryName);

        CustomAttributes attributes = getAttributes(pathList, userUniqueId, transactionId, null);

        return new OpenChatParams(transactionId, userUniqueId, channelName, tagsList, data, attributes);
    }



    /**
     * Use for post message through server api
     * @param categoryName
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     * @param textboxMsg
     * @return
     */
    public HippoSendQueryParams getSubmitQueryParams(String categoryName, String transactionId,
                                                             String userUniqueId, int supportId, ArrayList<String> pathList, String textboxMsg, String subHeader) {
        String channelName = getChannelName(pathList.get(pathList.size() - 1), userUniqueId, supportId, transactionId);
        String message = getMessage(userUniqueId, transactionId, categoryName, pathList.get(pathList.size() - 1),
                textboxMsg, subHeader);

        if(!TextUtils.isEmpty(transactionId)) {
            transactionId = transactionId + "_" + supportId;
        } else {
            transactionId = userUniqueId + "_" + supportId;
        }

        String msg[] = new String[1];
        msg[0] = message;

        ArrayList<TagsModel> tags = new ArrayList<>();
        tags.add(new TagsModel(categoryName));

        HippoSendQueryParams params = new HippoSendQueryParams(HippoConfig.getInstance().getAppKey(),
                -1l,
                transactionId,
                HippoConfig.getInstance().getUserData().getUserId(),
                channelName,
                tags,
                msg,
                HippoConfig.getInstance().getUserData().getEnUserId(), 1);

        CustomAttributes attributes = getAttributes(pathList, userUniqueId, transactionId, textboxMsg);
        params.setCustomAttributes(attributes);

        try {
            if(CommonData.getExtraTicketData() != null) {
                params.setObject(CommonData.getExtraTicketData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;

    }


    /**
     * Channel name of a particular ticket
     * @param pageTitle
     * @param userUniqueId
     * @param supportId
     * @param transactionId
     * @return
     */
    private String getChannelName(String pageTitle, String userUniqueId, int supportId, String transactionId) {
        String channelName = pageTitle + " #"+userUniqueId + "_" + supportId;
        if(!TextUtils.isEmpty(transactionId))
            channelName = pageTitle + " #"+transactionId;
        return channelName;
    }

    /**
     * Message to be send
     * @param userUniqueId
     * @param transactionId
     * @param categoryName
     * @param pageTitle
     * @param edittextBoxText
     * @param subHeader
     * @return
     */
    private String getMessage(String userUniqueId, String transactionId, String categoryName, String pageTitle,
                              String edittextBoxText, String subHeader) {
        String message = "";

        message = "[User ID: "+userUniqueId+"]";
        if(!TextUtils.isEmpty(transactionId)) {
            message = message + "\n[Transaction ID: " + transactionId+"]";
        }
        message = message + "\n[Request type: "+categoryName+"]";
        message = message + "\n[Request] ";
        if(!TextUtils.isEmpty(edittextBoxText))
            message = message + edittextBoxText;
        else if(!TextUtils.isEmpty(subHeader))
            message = message + pageTitle + "->" + subHeader;

        return message;
    }

    /**
     * Create Attributes for agent
     * @param pathList
     * @param userUniqueId
     * @param transactionId
     * @return
     */
    private CustomAttributes getAttributes(ArrayList<String> pathList, String userUniqueId
            , String transactionId, String query) {
        String attributesPath = "";
        for(String str : pathList) {
            if(TextUtils.isEmpty(attributesPath)) {
                attributesPath = attributesPath + str;
            } else {
                attributesPath = attributesPath + " -> " + str;
            }
        }

        CustomAttributes attributes = new CustomAttributes();
        attributes.setPath(attributesPath);
        attributes.setUserId(userUniqueId);
        if(!TextUtils.isEmpty(transactionId)) {
            attributes.setTransactionId(transactionId);
        }
        if(!TextUtils.isEmpty(query))
            attributes.setQuery(query);
        return attributes;
    }


}
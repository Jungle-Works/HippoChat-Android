package com.hippo.support.model.callbackModel;

import com.hippo.support.Utils.SupportKeys;
import com.hippo.support.model.Category;

import java.util.ArrayList;

/**
 * Created by gurmail on 05/04/18.
 */

public class SendQueryChat {

    private SupportKeys.SupportQueryType type;
    private int queryType;
    private Category category;
    private String transactionId;
    private String userUniqueId;
    private int supportId;
    private ArrayList<String> pathList;
    private String textboxMsg;
    private String categoryName;
    private String successMessage;

    //Add outside the constructor
    private String subHeader;

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }


    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserUniqueId() {
        return userUniqueId;
    }

    public void setUserUniqueId(String userUniqueId) {
        this.userUniqueId = userUniqueId;
    }

    public int getSupportId() {
        return supportId;
    }

    public void setSupportId(int supportId) {
        this.supportId = supportId;
    }

    public ArrayList<String> getPathList() {
        return pathList;
    }

    public void setPathList(ArrayList<String> pathList) {
        this.pathList = pathList;
    }

    public String getTextboxMsg() {
        return textboxMsg;
    }

    public void setTextboxMsg(String textboxMsg) {
        this.textboxMsg = textboxMsg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public SupportKeys.SupportQueryType getType() {
        return type;
    }

    public void setType(SupportKeys.SupportQueryType type) {
        this.type = type;
    }


    /**
     * For Sending Query
     * @param category
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     * @param textboxMsg
     */
    public SendQueryChat(SupportKeys.SupportQueryType type, Category category, String transactionId, String userUniqueId, int supportId,
                         ArrayList<String> pathList, String textboxMsg, String successMessage) {
        this.type = type;
        this.category = category;
        this.transactionId = transactionId;
        this.userUniqueId = userUniqueId;
        this.supportId = supportId;
        this.pathList = pathList;
        this.textboxMsg = textboxMsg;
        this.successMessage = successMessage;
    }


    /**
     * For Open chat support
     * @param categoryName
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     */
    public SendQueryChat(SupportKeys.SupportQueryType type, Category category, String transactionId, String userUniqueId,
                         int supportId, ArrayList<String> pathList) {
        this.type = type;
        this.category = category;
        this.transactionId = transactionId;
        this.userUniqueId = userUniqueId;
        this.supportId = supportId;
        this.pathList = pathList;
    }

}

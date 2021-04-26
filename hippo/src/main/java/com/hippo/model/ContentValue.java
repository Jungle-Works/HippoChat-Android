package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Content value.
 */
public class ContentValue {
    @SerializedName("button_id")
    @Expose
    private String buttonId;
    @SerializedName("button_type")
    @Expose
    private String buttonType;
    @SerializedName("button_title")
    @Expose
    private String buttonTitle;
    @SerializedName("payload")
    @Expose
    private String payload;
    @SerializedName("business_id")
    @Expose
    private int businessId;
    @SerializedName("bot_id")
    @Expose
    private String botId;
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("action_id")
    @Expose
    private String actionId;
    @SerializedName("params")
    @Expose
    private List<String> params = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("data_type")
    @Expose
    private ArrayList<String> data_type;
    @SerializedName("questions")
    @Expose
    private ArrayList<String> questions;

    // for user concent
    @SerializedName("btn_id")
    @Expose
    private String btnId;
    @SerializedName("btn_color")
    @Expose
    private String btnColor;
    @SerializedName("btn_title")
    @Expose
    private String btnTitle;
    @SerializedName("btn_selected_color")
    @Expose
    private String btnSelectedColor;

    @SerializedName("btn_title_color")
    @Expose
    private String btnTitleColor;
    @SerializedName("btn_title_selected_color")
    @Expose
    private String btnTitleSelectedColor;
    @SerializedName("button_action_type")
    @Expose
    private String buttonActionType;
    @SerializedName("button_action_json_1")
    @Expose
    private String botButtonType;
    @SerializedName("button_action_json")
    @Expose
    private ButtonActionJson buttonActionJson;
    @SerializedName("id")
    @Expose
    private String cardId;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("rating")
    @Expose
    private String ratingValue;
    @SerializedName("status")
    @Expose
    private Integer status;

    public String getBtnTitleColor() {
        return btnTitleColor;
    }

    public void setBtnTitleColor(String btnTitleColor) {
        this.btnTitleColor = btnTitleColor;
    }

    public String getBtnTitleSelectedColor() {
        return btnTitleSelectedColor;
    }

    public void setBtnTitleSelectedColor(String btnTitleSelectedColor) {
        this.btnTitleSelectedColor = btnTitleSelectedColor;
    }

    public String getBtnId() {
        return btnId;
    }

    public void setBtnId(String btnId) {
        this.btnId = btnId;
    }

    public String getBtnColor() {
        return btnColor;
    }

    public void setBtnColor(String btnColor) {
        this.btnColor = btnColor;
    }

    public String getBtnTitle() {
        return btnTitle;
    }

    public void setBtnTitle(String btnTitle) {
        this.btnTitle = btnTitle;
    }

    public String getBtnSelectedColor() {
        return btnSelectedColor;
    }

    public void setBtnSelectedColor(String btnSelectedColor) {
        this.btnSelectedColor = btnSelectedColor;
    }

    private String textValue;

    private String countryCode;

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<String> getData_type() {
        return data_type;
    }

    public void setData_type(ArrayList<String> data_type) {
        this.data_type = data_type;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public boolean isDeleted(boolean isDeleted) {
        return this.isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getButtonActionType() {
        return buttonActionType;
    }

    public void setButtonActionType(String buttonActionType) {
        this.buttonActionType = buttonActionType;
    }

    public String getBotButtonType() {
        return botButtonType;
    }

    public void setBotButtonType(String botButtonType) {
        this.botButtonType = botButtonType;
    }

    public ButtonActionJson getButtonActionJson() {
        return buttonActionJson;
    }

    public void setButtonActionJson(ButtonActionJson buttonActionJson) {
        this.buttonActionJson = buttonActionJson;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /*public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }*/

}

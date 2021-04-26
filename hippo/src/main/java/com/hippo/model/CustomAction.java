package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class CustomAction {

    @SerializedName("title")
    private String title;

    @SerializedName("title_description")
    private String titleDescription;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("description")
    private ArrayList<DescriptionObject> descriptionObjects;

    @SerializedName("action_buttons")
    private ArrayList<ActionButtonModel> actionButtons;


    // for new payment

    @SerializedName("items")
    @Expose
    private ArrayList<HippoPayment> hippoPayment;
    @SerializedName("selected_id")
    @Expose
    private String selectedId;
    @SerializedName("result_message")
    @Expose
    private String resultMessage;

    @SerializedName("min_selection")
    @Expose
    private Integer minSelection;
    @SerializedName("max_selection")
    @Expose
    private Integer maxSelection;
    @SerializedName("is_replied")
    @Expose
    private Integer isReplied;
    @SerializedName("multi_select_buttons")
    @Expose
    private ArrayList<MultiSelectButtons> multiSelectButtons;

    public Integer getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(Integer minSelection) {
        this.minSelection = minSelection;
    }

    public Integer getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(Integer maxSelection) {
        this.maxSelection = maxSelection;
    }

    public ArrayList<MultiSelectButtons> getMultiSelectButtons() {
        return multiSelectButtons;
    }

    public void setMultiSelectButtons(ArrayList<MultiSelectButtons> multiSelectButtons) {
        this.multiSelectButtons = multiSelectButtons;
    }

    public String getTitleDescription() {
        return titleDescription;
    }

    public void setTitleDescription(final String titleDescription) {
        this.titleDescription = titleDescription;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<DescriptionObject> getDescriptionObjects() {
        return descriptionObjects;
    }

    public ArrayList<ActionButtonModel> getActionButtons() {
        return actionButtons;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescriptionObjects(final ArrayList<DescriptionObject> descriptionObjects) {
        this.descriptionObjects = descriptionObjects;
    }

    public void setActionButtons(final ArrayList<ActionButtonModel> actionButtons) {
        this.actionButtons = actionButtons;
    }

    public ArrayList<HippoPayment> getHippoPayment() {
        return hippoPayment;
    }

    public void setHippoPayment(ArrayList<HippoPayment> hippoPayment) {
        this.hippoPayment = hippoPayment;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public boolean isReplied() {
        try {
            return isReplied == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void setIsReplied(Integer isReplied) {
        this.isReplied = isReplied;
    }
}

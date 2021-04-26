package com.hippo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2019-11-25.
 *
 * @author gurmail
 */
public class CustomField {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("field_name")
    @Expose
    private String fieldName;
    @SerializedName("field_type")
    @Expose
    private String fieldType;
    @SerializedName("is_required")
    @Expose
    private Boolean isRequired;
    @SerializedName("placeholder")
    @Expose
    private String placeholder;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("show_to_customer")
    @Expose
    private Boolean showToCustomer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getShowToCustomer() {
        return showToCustomer;
    }

    public void setShowToCustomer(Boolean showToCustomer) {
        this.showToCustomer = showToCustomer;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

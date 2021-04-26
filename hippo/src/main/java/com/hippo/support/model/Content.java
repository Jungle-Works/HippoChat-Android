package com.hippo.support.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public class Content {

    @SerializedName("sub_heading")
    @Expose
    private SubHeading subHeading;
    @SerializedName("description")
    @Expose
    private Description description;
    @SerializedName("call_button")
    @Expose
    private CallButton callButton;
    @SerializedName("chat_button")
    @Expose
    private ChatButton chatButton;
    @SerializedName("submit_button")
    @Expose
    private SubmitButton submitButton;
    @SerializedName("query_form")
    @Expose
    private QueryForm queryForm;

    public SubHeading getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(SubHeading subHeading) {
        this.subHeading = subHeading;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public CallButton getCallButton() {
        return callButton;
    }

    public void setCallButton(CallButton callButton) {
        this.callButton = callButton;
    }

    public ChatButton getChatButton() {
        return chatButton;
    }

    public void setChatButton(ChatButton chatButton) {
        this.chatButton = chatButton;
    }

    public SubmitButton getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(SubmitButton submitButton) {
        this.submitButton = submitButton;
    }

    public QueryForm getQueryForm() {
        return queryForm;
    }

    public void setQueryForm(QueryForm queryForm) {
        this.queryForm = queryForm;
    }


    public class CallButton {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("phone")
        @Expose
        private String phone;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

    }

    public class ChatButton {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }


    public class Description {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public class QueryForm {

        @SerializedName("text_view")
        @Expose
        private TextViewData textView;
        /*@SerializedName("submit_button")
        @Expose
        private SubmitButton submitButton;*/

        public TextViewData getTextView() {
            return textView;
        }

        public void setTextView(TextViewData textView) {
            this.textView = textView;
        }

        /*public SubmitButton getSubmitButton() {
            return submitButton;
        }

        public void setSubmitButton(SubmitButton submitButton) {
            this.submitButton = submitButton;
        }*/

    }

    public class SubHeading {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public class SubmitButton {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("response_text")
        @Expose
        private String sucessMessage;

        public String getSucessMessage() {
            return sucessMessage;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public class TextViewData {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

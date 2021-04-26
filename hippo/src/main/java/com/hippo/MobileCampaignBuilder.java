package com.hippo;

import android.app.Activity;

import java.io.Serializable;

/**
 * Created by gurmail on 2019-12-23.
 *
 * @author gurmail
 */
public class MobileCampaignBuilder {

    private String mTitle;
    private String parseFormat;
    private NotificationListener listener;
    private boolean hasCampaignPager;
    private boolean closeActivityOnClick;
    private boolean closeOnlyDeepLink;
    private String notificationTitle;
    private String emptyNotificationText;
    private String clearText;
    private String deleteMessage;
    private boolean hideDownloadBtn;
    private String clearBtn;

    public String getmTitle() {
        return mTitle;
    }

    public String getParseFormat() {
        return parseFormat;
    }

    public NotificationListener getListener() {
        return listener;
    }

    public boolean isCloseActivityOnClick() {
        return closeActivityOnClick;
    }

    public boolean isCloseOnlyDeepLink() {
        return closeOnlyDeepLink;
    }

    public boolean hasCampaignPager() {
        return hasCampaignPager;
    }

    public boolean isHideDownloadBtn() {
        return hideDownloadBtn;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getDeleteMessage() {
        return deleteMessage;
    }

    public String getEmptyNotificationText() {
        return emptyNotificationText;
    }

    public String getClearText() {
        return clearText;
    }

    public String getClearBtn() {
        return clearBtn;
    }

    public static class Builder {

        private String mTitle;
        private String parseFormat;
        private NotificationListener listener;
        private boolean closeActivityOnClick;
        private boolean closeOnlyDeepLink;
        private boolean hasCampaignPager;
        private String notificationTitle;
        private String emptyNotificationText;
        private String clearText;
        private String deleteMessage;
        private boolean hideDownloadBtn;
        private String clearBtn;

        public Builder setTitle(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder setClearText(String clearText) {
            this.clearText = clearText;
            return this;
        }

        public Builder setParseFormat(String parseFormat) {
            this.parseFormat = parseFormat;
            return this;
        }

        public Builder setListener(NotificationListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder isCloseActivityOnClick(boolean closeActivityOnClick) {
            this.closeActivityOnClick = closeActivityOnClick;
            return this;
        }

        public Builder isCloseOnlyDeepLink(boolean closeOnlyDeepLink) {
            this.closeOnlyDeepLink = closeOnlyDeepLink;
            return this;
        }

        public Builder hasCampaignPager(boolean hasCampaignPager) {
            this.hasCampaignPager = hasCampaignPager;
            return this;
        }

        public Builder setNotificationTitle(String notificationTitle) {
            this.notificationTitle = notificationTitle;
            return this;
        }

        public Builder setEmptyNotificationText(String emptyNotificationText) {
            this.emptyNotificationText = emptyNotificationText;
            return this;
        }

        public Builder setDeleteMessage(String deleteMessage) {
            this.deleteMessage = deleteMessage;
            return this;
        }

        public Builder hideDownloadBtn(boolean hideDownloadBtn) {
            this.hideDownloadBtn = hideDownloadBtn;
            return this;
        }

//        public Builder setClearBtnTxt(String clearBtn) {
//            this.clearBtn = clearBtn;
//            return this;
//        }

        public MobileCampaignBuilder build() {
            return new MobileCampaignBuilder(this);
        }
    }

    private MobileCampaignBuilder(Builder builder) {
        this.mTitle = builder.mTitle;
        this.parseFormat = builder.parseFormat;
        this.listener = builder.listener;
        this.closeActivityOnClick = builder.closeActivityOnClick;
        this.closeOnlyDeepLink = builder.closeOnlyDeepLink;
        this.hasCampaignPager = builder.hasCampaignPager;
        this.notificationTitle = builder.notificationTitle;
        this.emptyNotificationText = builder.emptyNotificationText;
        this.clearText = builder.clearText;
        this.deleteMessage = builder.deleteMessage;
        this.hideDownloadBtn = builder.hideDownloadBtn;
        this.clearBtn = builder.clearBtn;
    }
}

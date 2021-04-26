package com.hippo.model;

import java.io.Serializable;

/**
 * Created by gurmail on 2019-11-13.
 *
 * @author gurmail
 */
public class HippoUserProfileModel implements Serializable {

    private String imageUrl;
    private String enUserId;
    private long channelID;
    private String title;
    private String description;
    private String userId;
    private String rating;

    public HippoUserProfileModel(String imageUrl, String enUserId, long channelID, String title) {
        this.imageUrl = imageUrl;
        this.enUserId = enUserId;
        this.channelID = channelID;
        this.title = title;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public long getChannelID() {
        return channelID;
    }

    public void setChannelID(long channelID) {
        this.channelID = channelID;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}

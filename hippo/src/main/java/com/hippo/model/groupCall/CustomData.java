package com.hippo.model.groupCall;

/**
 * Created by gurmail on 2020-07-16.
 *
 * @author gurmail
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomData {

    @SerializedName("room_title")
    @Expose
    private String roomTitle;
    @SerializedName("room_unique_id")
    @Expose
    private String roomUniqueId;
    @SerializedName("session_end_time")
    @Expose
    private String sessionEndTime;
    @SerializedName("session_start_time")
    @Expose
    private String sessionStartTime;
    @SerializedName("chat_type")
    @Expose
    private String chatType;
    @SerializedName("is_audio_enabled")
    @Expose
    private int isAudioEnabled;
    @SerializedName("is_video_enabled")
    @Expose
    private int isVideoEnabled;

    public int getIsAudioEnabled() {
        return isAudioEnabled;
    }

    public void setIsAudioEnabled(int isAudioEnabled) {
        this.isAudioEnabled = isAudioEnabled;
    }

    public int getIsVideoEnabled() {
        return isVideoEnabled;
    }

    public void setIsVideoEnabled(int isVideoEnabled) {
        this.isVideoEnabled = isVideoEnabled;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomUniqueId() {
        return roomUniqueId;
    }

    public void setRoomUniqueId(String roomUniqueId) {
        this.roomUniqueId = roomUniqueId;
    }

    public String getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(String sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public String getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(String sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
}
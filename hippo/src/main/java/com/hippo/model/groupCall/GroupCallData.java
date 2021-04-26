package com.hippo.model.groupCall;

/**
 * Created by gurmail on 2020-07-18.
 *
 * @author gurmail
 */
public class GroupCallData {

    private String transactionId;
    private Long channelId;
    private String roomTitle;
    private String roomUniqueId;
    private int callType;
    private String muid;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }
}

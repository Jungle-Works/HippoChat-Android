package com.hippo;

import android.app.Activity;
import android.content.Context;
import org.json.JSONObject;

/**
 * Created by gurmail on 04/01/19.
 *
 * @author gurmail
 */
public interface CallData {
    void onNotificationReceived(Context context, JSONObject data);
    void onConfNotificationReceived(Context context, JSONObject data);
    void onGroupNotificationReceived(Context context, JSONObject data);
    void networkStatus(int status);
    void onCallClick(Context context, int callType, Long channelId, Long userId, boolean isAgentFlow,
                     boolean isAllowCall, String fullname, String image, String myImagePath);

    void onExternalClick(Context context, String callType, Long userid, String otherUserName,
                         String fuguPeerChatParams, String otherUserImagePath, String myImagePath);

    void openDirectLink(Context context, String roomId, String userName, String callType,
                        String imagePath, Long channelId, String transactionId,
                        int isAudioEnabled, int isVideoEnabled);

    void leaveGroupCall(Context context, String transactionId);

}

package com.hippo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.hippo.HippoConfig;
import com.hippo.activity.FuguChatActivity;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.eventbus.BusProvider;
import com.hippo.model.Message;
import com.hippo.utils.DateUtils;
import com.hippo.utils.HippoLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import faye.ConnectionManager;
import faye.FayeClient;
import faye.FayeClientListener;
import faye.MetaMessage;
import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 23/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguNetworkStateReceiver extends BroadcastReceiver implements FuguAppConstant {

    private static final String TAG = FuguNetworkStateReceiver.class.getSimpleName();
    @NonNull
    private HashMap<Long, LinkedHashMap<String, JSONObject>> unsentMessageMap = new HashMap<>();

    private Long channelId;
    private HashMap<Long, LinkedHashMap<String, JSONObject>> allUnsentMessageMap = new HashMap<>();
    private LinkedHashMap<String, JSONObject> sendingMessages = new LinkedHashMap<>();

    private String tempDate = "";
    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";

    public static HashMap<Long, LinkedHashMap<String, Message>> UNSENT_MESSAGES = new HashMap<>();
    private LinkedHashMap<String, Message> sendingMessagesList = new LinkedHashMap<>();
    private LinkedHashMap<String, Message> sentMessages = new LinkedHashMap<>();

    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        int status = NetworkUtil.getConnectivityStatusString(context);
        try {
            ConnectionManager.INSTANCE.changeStatus(status);
            //FuguChannelsActivity.changeStatus(status);
            //BusProvider.getInstance().post(new NetworkStatus(status));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (HippoConfig.getInstance().getCallData() != null) {
                HippoLog.i("TAG", "Sending network update = "+status);
                HippoConfig.getInstance().getCallData().networkStatus(status);
            }
        } catch (Exception e) {

        }

        try {
            HippoLog.d("app", "Network connectivity change");
            if (intent.getExtras() != null) {
                boolean isEnabled;
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    isEnabled = true;
                } else {
                    isEnabled = false;
                }

                //HippoCallConfig.getInstance().networkStatus(isEnabled);
                Intent mIntent = new Intent(NETWORK_STATE_INTENT);
                mIntent.putExtra("isConnected", isEnabled);
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

            }
        } catch (Exception e) {

        }
    }


    public void onConnectedServer() {
        try {
            setDateExpireDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onDisconnectedServer() {

    }

    public void onReceivedMessage(String msg, String channel) {
        onMessageReceived(msg, channel);

    }

    private void onMessageReceived(String msg, String channel) {
        Long channelid = null;
        channelid = Long.parseLong(channel.substring(1));
        try {
            final JSONObject messageJson = new JSONObject(msg);

            if ((messageJson.has("message") && !messageJson.getString("message").isEmpty()) ||
                    (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty())) {

                String muid = messageJson.getString("muid");

                if (FuguChatActivity.currentChannelId != channelid) {

                    if (channelid != channelId && messageJson.optInt("message_type") == 10) {
                        return;
                    }

                    Message listItem = sendingMessagesList.get(messageJson.getString("muid"));
                    listItem.setMessageStatus(MESSAGE_SENT);


                    String time = listItem.getSentAtUtc();
                    String localDate = DateUtils.getInstance().convertToLocal(time, inputFormat, outputFormat);
                    if (!tempDate.equalsIgnoreCase(localDate)) {
                        sentMessages.put(localDate, new Message(localDate, true));
                    }


                    sentMessages.put(messageJson.getString("muid"), listItem);

                    sendingMessages.remove(muid);
                    UNSENT_MESSAGES.remove(muid);
                    unsentMessageMap.remove(muid);
                    sendingMessages();
                } else {
                    if (sentMessages != null || sentMessages.size() > 0) {
                        CommonData.addExistingMessages(channelid, sentMessages);
                    }
                    allUnsentMessageMap.remove(channelid);
                    sendMessages();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPongReceived() {

    }

    public void onWebSocketError() {

    }

    public void onErrorReceived(String msg, String channel) {

    }

    public void onNotConnected() {

    }

    private void setDateExpireDate() throws Exception {
        Long currentChannelId = 0l;
        currentChannelId = FuguChatActivity.currentChannelId;

        for (Long channelId : unsentMessageMap.keySet()) {
            if (currentChannelId.compareTo(channelId) != 0) {
                LinkedHashMap<String, Message> unsentMessage = UNSENT_MESSAGES.get(channelId);
                LinkedHashMap<String, JSONObject> unsentMessageObj = unsentMessageMap.get(channelId);
                if (unsentMessageObj != null && unsentMessageObj.size() == 0) {
                    CommonData.removeUnsentMessageChannel(channelId);
                    CommonData.removeUnsentMessageMapChannel(channelId);
                    continue;
                }
                if(unsentMessage == null || unsentMessage.size() == 0)
                    return;
                for (String key : unsentMessage.keySet()) {
                    Message message = unsentMessage.get(key);
                    String time = message.getSentAtUtc();
                    int expireTimeCheck = message.getIsMessageExpired();

                    if (message.getMessageType() != IMAGE_MESSAGE && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                        message.setIsMessageExpired(1);
                        try {
                            JSONObject messageJson = unsentMessageObj.get(key);
                            if(messageJson != null) {
                                messageJson.put("is_message_expired", 1);
                                unsentMessageObj.put(key, messageJson);
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else if(message.getMessageType() == IMAGE_MESSAGE) {
                        JSONObject messageJson = unsentMessageObj.get(key);
                        if(messageJson == null) {
                            message.setMessageStatus(MESSAGE_IMAGE_RETRY);
                        }
                    }
                }

                allUnsentMessageMap.put(channelId, unsentMessageObj);

                if (FuguChatActivity.currentChannelId.compareTo(channelId) != 0) {
                    UNSENT_MESSAGES.put(channelId, unsentMessage);
                    unsentMessageMap.put(channelId, unsentMessageObj);
                    CommonData.setUnsentMessageByChannel(channelId, unsentMessage);
                    CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageObj);
                }
            }
        }
        sentMessages.clear();
        sendMessages();


    }

    private void sendMessages() throws Exception {
        if (allUnsentMessageMap != null && allUnsentMessageMap.size() > 0) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (final Long key : allUnsentMessageMap.keySet()) {
                        channelId = key;
                        if (FuguChatActivity.currentChannelId.compareTo(key) != 0) {
                            //publishUnsentMessages(key, allUnsentMessageMap.get(key));
                            channelId = key;
                            if (FuguChatActivity.currentChannelId.compareTo(channelId) != 0)
                                ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(key));

                            sendingMessages = allUnsentMessageMap.get(key);
                            sendingMessagesList = UNSENT_MESSAGES.get(key);

                            List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                            Collections.reverse(reverseOrderedKeys);
                            for (String key1 : reverseOrderedKeys) {
                                if (sentMessages.get(key1).isDateView()) {
                                    tempDate = key1;
                                    break;
                                }
                            }


                            try {
                                sendingMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            allUnsentMessageMap.remove(channelId);
                            try {
                                sendMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }, 1000);
        }
    }

    private void sendingAgentMessages() throws Exception {
        if (sendingMessages.size() == 0) {
            allUnsentMessageMap.remove(channelId);
            ConnectionManager.INSTANCE.unsubScribeChannel(String.valueOf(channelId));
        } else {
            for (String key : sendingMessages.keySet()) {
                JSONObject messageJson = sendingMessages.get(key);
                if (messageJson.has("local_url") && !TextUtils.isEmpty(messageJson.optString("local_url", ""))) {
                    //uploadFileServerCall(messageJson.optString("local_url", ""), "image/*", channelId, messageJson);
                    continue;
                } else {
                    HippoLog.e(TAG, "**************");
                    ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
                }
                break;
            }
        }

    }

    private void sendingMessages() throws Exception {
        if (sendingMessages.size() == 0) {
            allUnsentMessageMap.remove(channelId);
            ConnectionManager.INSTANCE.unsubScribeChannel(String.valueOf(channelId));
            CommonData.removeUnsentMessageMapChannel(channelId);
            CommonData.removeUnsentMessageChannel(channelId);
            if (sentMessages != null && sentMessages.size() > 0) {
                CommonData.addExistingMessages(channelId, sentMessages);
            }
            sendMessages();
        } else {
            for (String key : sendingMessages.keySet()) {
                JSONObject messageJson = sendingMessages.get(key);
                if (messageJson.has("local_url") && !TextUtils.isEmpty(messageJson.optString("local_url", ""))) {
                    //uploadFileServerCall(messageJson.optString("local_url", ""), "image/*", channelId, messageJson);
                    continue;
                } else if(messageJson.optInt("is_message_expired", 0) == 0) {
                    HippoLog.e(TAG, "**************");
                    ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
                }
                break;
            }
        }

    }
}

package faye;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.hippo.HippoConfig;
import com.hippo.constant.FuguAppConstant;
import com.hippo.model.FuguFileDetails;
import com.hippo.utils.DateUtils;
import com.hippo.utils.HippoLog;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Date;
import java.util.HashSet;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FayeClient extends BaseSocketClient implements FuguAppConstant {

    private static final String LOG_TAG = FayeClient.class.getSimpleName();
    private static final String FAYE_LOG = "FAYE_LOG ";

    private WebSocket mWebSocket = null;
    private FayeClientListener mConnectionListener = null;
    private FayeServiceListener serviceListener = null;
    private FayeAgentListener mAgentListener = null;
    private HashSet<String> mChannels;
    private String mServerUrl = "";
    private boolean mFayeConnected = false;
    private boolean mIsConnectedServer = false;
    private MetaMessage mMetaMessage;
    private Handler mMessageHandler;

    public FayeClient(String url, MetaMessage meta) {
        mServerUrl = url;
        HippoLog.e("TAG", "faye url = "+url);
        mMetaMessage = meta;
        mChannels = new HashSet<String>();

		HippoLog.w(FAYE_LOG+"initialize", "url="+url);
    }

    {
        HandlerThread thread = new HandlerThread("FayeHandler");
        thread.start();
        mMessageHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WebSocket.ON_OPEN:
                        HippoLog.i(LOG_TAG, "onOpen() executed");
						HippoLog.w(FAYE_LOG+"handleMessage", "on_open");
                        mIsConnectedServer = true;
                        handShake();
                        break;
                    case WebSocket.ON_CLOSE:
                        HippoLog.i(LOG_TAG, "onClosed() executed");
						HippoLog.w(FAYE_LOG+"handleMessage", "on_close");
                        mIsConnectedServer = false;
                        mFayeConnected = false;
                        if(mAgentListener != null) {
                            mAgentListener.onDisconnectedServer(FayeClient.this);
                        }
                        if(mConnectionListener != null)
                            mConnectionListener.onDisconnectedServer(FayeClient.this);
                        break;
                    case WebSocket.ON_MESSAGE:
                        try {
                            HippoLog.i(LOG_TAG, "onMessage executed");
							HippoLog.w(FAYE_LOG+"handleMessage", "on_message "+((String) msg.obj));
                            handleFayeMessage((String) msg.obj);
                        } catch (NotYetConnectedException e) {
                            // Do noting
                            e.printStackTrace();
                        }
                        break;

                    case WebSocket.ON_PONG:
						HippoLog.w(FAYE_LOG+"handleMessage", "on_pong");
                        if(mAgentListener != null)
                            mAgentListener.onPongReceived();
                        if(mConnectionListener != null)
                            mConnectionListener.onPongReceived();

                        break;

                    case WebSocket.ON_NOT_CONNECTED:
						HippoLog.w(FAYE_LOG+"handleMessage", "on_not_connected");
                        if(mConnectionListener != null)
                            mConnectionListener.onNotConnected();

                        break;

                }
            }
        };
    }

    private void sendLocalBroadcast(int status) {
        try {
            if(HippoConfig.getInstance().getContext() != null) {
                Intent mIntent = new Intent(FuguAppConstant.FUGU_LISTENER_NULL);
                mIntent.putExtra("status", status);
                LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
            }
        } catch (Exception e) {
            if(HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    /* Public Methods */
    public FayeClientListener getmConnectionListener() {
        return mConnectionListener;
    }

    public void setmConnectionListener(FayeClientListener mConnectionListener) {
        this.mConnectionListener = mConnectionListener;
    }

    public FayeServiceListener getServiceListener() {
        return serviceListener;
    }

    public void setServiceListener(FayeServiceListener serviceListener) {
        this.serviceListener = serviceListener;
    }

    public FayeAgentListener getAgentListener() {
        return mAgentListener;
    }

    public void setAgentListener(FayeAgentListener listener) {
        this.mAgentListener = listener;
    }

    public void addChannel(String channel) {
        mChannels.add(channel);
    }

    public HashSet<String> getmChannels() {
        return mChannels;
    }

    public boolean isConnectedServer() {
        return mIsConnectedServer;
    }

    public boolean isFayeConnected() {
        return mFayeConnected;
    }

    public void connectServer() {
        openWebSocketConnection();
    }

    public void disconnectServer() {
        for (String channel : mChannels) {
            unsubscribe(channel);
        }
        mChannels.clear();
        disconnect();
    }

    public void subscribeChannel(String channel) {
        mChannels.add(channel);
        subscribe(channel);
        HippoLog.v("channel------>>>>>>>>>>--------------", channel);
    }

    public void subscribeToChannels(String... channels) {
        for (String channel : channels) {
            mChannels.add(channel);
            subscribe(channel);
        }
    }

    public void unsubscribeChannel(String channel) {
        if (mChannels.contains(channel)) {
            unsubscribe(channel);
            mChannels.remove(channel);
        }
    }

    public void unsubscribeChannels(String... channels) {
        for (String channel : channels) {
            unsubscribe(channel);
        }
    }

    public void unsubscribeAll() {
        for (String channel : mChannels) {
            unsubscribe(channel);
        }
    }

    public void publish(String message, int messageType, String url, String thumbnailUrl,
                        FuguFileDetails fileDetails, int notificationType,
                        String uuid, int position, String channel, Long userId, String userName, Long channelId,
                        int isTyping) {
        JSONObject messageJson = new JSONObject();
        String localDate = DateUtils.getFormattedDate(new Date());
        try {
            if (notificationType == NOTIFICATION_READ_ALL) {
                messageJson.put(NOTIFICATION_TYPE, notificationType);
                messageJson.put(CHANNEL_ID, channelId);
            } else {
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE, message);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                if (position == 0) {
                    messageJson.put(MESSAGE_INDEX, position);
                } else {
                    messageJson.put(MESSAGE_INDEX, position);
                }
                messageJson.put("UUID", uuid);
                if (messageType == IMAGE_MESSAGE && !url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                }

                if (messageType == FILE_MESSAGE && !url.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                if (messageType == TEXT_MESSAGE) {
                    messageJson.put(IS_TYPING, isTyping);
                } else {
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                }

                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            }

            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publish(channel, messageJson, null, null);
    }

    public void publish(String channel, JSONObject data) {
        publish(channel, data, null, null);
    }

    public void publish(String channel, JSONObject data, String ext, String id) {
        try {
            HippoLog.e("@@@@@@@@", channel+"%%%%%%%%%%%%%%%%%%%%%%% "+data);
            String publish = mMetaMessage.publish(channel, data, ext, id);
            mWebSocket.send(publish);

        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Build publish message to JSON error" + e);
            connectServer();
        }
    }

    public void publish(String channel, JSONObject data, ConnectionError connectionError) {
        try {
            HippoLog.e("@@@@@@@@", "%%%%%%%%%%%%%%%%%%%%%%% "+data);
            String publish = mMetaMessage.publish(channel, data, null, null);
            //HippoLog.e("@@@@@@@@", "*********************** "+publish);
            mWebSocket.send(publish);

        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Build publish message to JSON error" + e);
            //connectServer();
            if(connectionError != null)
                connectionError.onError(data);
        }
    }

    /* Private Methods */
    private Socket getSSLWebSocket() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory.createSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openWebSocketConnection() {
        // Clean up any existing socket
        WebSocketImpl.DEBUG = false;
        if (mWebSocket != null) {
            mWebSocket.close();
        }
        try {
            URI uri = new URI(mServerUrl);
            mWebSocket = new WebSocket(uri, mMessageHandler);
            mWebSocket.setConnectionLostTimeout(60);
            HippoLog.e("uri.getScheme()", "==" + uri.getScheme());

            if (uri.getScheme().equals("https") || uri.getScheme().equals("wss")) {
                mWebSocket.setSocket(getSSLWebSocket());
            }
            mWebSocket.connect();
			HippoLog.w(FAYE_LOG+"openWebSocketConnection", "uri.getScheme()="+uri.getScheme());
        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Server URL error" + e);
        }
    }

    private void closeWebSocketConnection() {
        if (mWebSocket != null) {
            mWebSocket.close();
			HippoLog.w(FAYE_LOG+"closeWebSocketConnection", "on_close");
        }
    }

    private void handShake() {
        try {
            String handshake = mMetaMessage.handShake();
            mWebSocket.send(handshake);
			HippoLog.w(FAYE_LOG+"handShake", "json = "+handshake);
        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "HandShake message error" + e);
            if(mConnectionListener != null)
                mConnectionListener.onWebSocketError();
        }
    }

    private void subscribe(String channel) {
        try {
            String subscribe = mMetaMessage.subscribe(channel);
            mWebSocket.send(subscribe);
            System.out.println(subscribe);
			HippoLog.w(FAYE_LOG+"subscribe", "json = "+subscribe);
        }  catch(WebsocketNotConnectedException e) {
            if(mConnectionListener != null)
                mConnectionListener.onWebSocketError();

            if(mAgentListener != null)
                mAgentListener.onWebSocketError();

            mFayeConnected = false;
            e.printStackTrace();
            HippoLog.e(LOG_TAG, "Subscribe message error" + e);
        }catch (Exception e) {
            HippoLog.e(LOG_TAG, "Subscribe message error" + e);
        }
    }

    private void unsubscribe(String channel) {
        try {
            String unsubscribe = mMetaMessage.unsubscribe(channel);
            mWebSocket.send(unsubscribe);
            HippoLog.i(LOG_TAG, "UnSubscribe:" + channel);
			HippoLog.w(FAYE_LOG+"unsubscribe", "json = "+unsubscribe);
        } catch (Exception e) {
            e.printStackTrace();
            HippoLog.e(LOG_TAG, "Unsubscribe message error: " + e);
        }
    }

    private void connect() {
        try {
            String connect = mMetaMessage.connect();
            mWebSocket.send(connect);
            mWebSocket.setConnectionLostTimeout(10);
			HippoLog.w(FAYE_LOG+"connect", "json = "+connect);
        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Connect message error" + e);
        }
    }

    private void disconnect() {
        try {
            String disconnect = mMetaMessage.disconnect();
            mWebSocket.send(disconnect);
			HippoLog.w(FAYE_LOG+"disconnect", "json = "+disconnect);
        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Disconnect message error" + e);
        }
    }

    public boolean hasSubscribed(String s) {
        if(mChannels.contains(s))
            return false;
        return true;
    }

    private void handleFayeMessage(String message) {
        HippoLog.v("handleFayeMessage", "handleFayeMessage = "+message);
        JSONArray arr = null;
        try {
            arr = new JSONArray(message);
        } catch (Exception e) {
            HippoLog.e(LOG_TAG, "Unknown message type: " + message + e);
        }

        int length = arr.length();
        for (int i = 0; i < length; ++i) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) continue;

            String channel = obj.optString(MetaMessage.KEY_CHANNEL);
            boolean successful = obj.optBoolean("successful");
            if (channel.equals(MetaMessage.HANDSHAKE_CHANNEL)) {
                if (successful) {
                    mMetaMessage.setClient(obj.optString(MetaMessage.KEY_CLIENT_ID));
                    if(mConnectionListener != null)
                        mConnectionListener.onConnectedServer(this);
                    if(serviceListener != null) {
                        serviceListener.onConnectedServer(this);
                    }
                    if(mAgentListener != null) {
                        mAgentListener.onConnectedServer(this);
                    }
                    connect();
                } else {
                    HippoLog.e(LOG_TAG, "Handshake Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.CONNECT_CHANNEL)) {
                if (successful) {
                    mFayeConnected = true;
                    connect();
                } else {
                    String errorCode = obj.optString("error", "");
                    if(mConnectionListener != null && errorCode.contains("401:")) {
                        mConnectionListener.onSubscriptionError();
                        HippoLog.e(LOG_TAG, "Connecting Error with listener: " + obj.toString());
                    } else {
                        HippoLog.e(LOG_TAG, "Connecting Error: " + obj.toString());
                    }
                }
                return;
            }

            if (channel.equals(MetaMessage.DISCONNECT_CHANNEL)) {
                if (successful) {
                    if(mConnectionListener != null)
                        mConnectionListener.onDisconnectedServer(this);
                    if(mAgentListener != null) {
                        mAgentListener.onDisconnectedServer(this);
                    }
                    if(serviceListener != null) {
                        serviceListener.onDisconnectedServer(this);
                    }

                    mFayeConnected = false;
                    closeWebSocketConnection();
                } else {
                    HippoLog.e(LOG_TAG, "Disconnecting Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.SUBSCRIBE_CHANNEL)) {
                String subscription = obj.optString(MetaMessage.KEY_SUBSCRIPTION);
                if (successful) {
                    mFayeConnected = true;
                    HippoLog.i(LOG_TAG, "Subscribed channel " + subscription);
                } else {
                    HippoLog.e(LOG_TAG, "Subscribing channel " + subscription
                            + " Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.UNSUBSCRIBE_CHANNEL)) {
                String subscription = obj.optString(MetaMessage.KEY_SUBSCRIPTION);
                if (successful) {
                    HippoLog.i(LOG_TAG, "Unsubscribed channel " + subscription);
                } else {
                    HippoLog.e(LOG_TAG, "Unsubscribing channel " + subscription
                            + " Error: " + obj.toString());
                }
                return;
            }

            if (mChannels.contains(channel)) {
                String data = obj.optString(MetaMessage.KEY_DATA, null);
                HippoLog.e("data", "data in faye = "+data);
                if (data != null) {
                    if(mConnectionListener != null)
                        mConnectionListener.onReceivedMessage(this, data, channel);
                    if(mAgentListener != null) {
                        mAgentListener.onReceivedMessage(this, data, channel);
                    }
                    if(serviceListener != null)
                        serviceListener.onReceivedMessage(this, data, channel);

                } else {
                    try {
                        if (obj.has("error")) {
                            if(serviceListener != null)
                                serviceListener.onErrorReceived(this, obj.getString("error"), channel);
                            if(mConnectionListener != null)
                                mConnectionListener.onErrorReceived(this, obj.getString("error"), channel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                HippoLog.e(LOG_TAG, "Cannot handle this message: " + obj.toString());
                String data = obj.optString(MetaMessage.KEY_DATA, null);
                if (data != null) {
                    if(mConnectionListener != null)
                        mConnectionListener.onReceivedMessage(this, data, channel);

                    if(mAgentListener != null) {
                        mAgentListener.onReceivedMessage(this, data, channel);
                    }

                    if(serviceListener != null)
                        serviceListener.onReceivedMessage(this, data, channel);

                }
                try {
                    if (obj.has("error")) {
                        if(serviceListener != null)
                            serviceListener.onErrorReceived(this, obj.getString("error"), channel);
                        if(mConnectionListener != null)
                            mConnectionListener.onErrorReceived(this, obj.getString("error"), channel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
    }

    public boolean isOpened() {
        if(mWebSocket != null && mWebSocket.isOpen() && mWebSocket.getSocket().isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void updateLang(@NotNull String lang) {

    }
}

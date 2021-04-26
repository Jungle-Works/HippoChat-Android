package faye

import android.os.Handler
import org.json.JSONException
import org.json.JSONObject
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import android.view.WindowManager
import com.hippo.HippoConfig
import com.hippo.callback.OnCloseListener
import com.hippo.callback.OnInitializedListener
import com.hippo.constant.FuguAppConstant
import com.hippo.database.CommonData
import com.hippo.eventbus.BusProvider
import com.hippo.helper.BusEvents
import com.hippo.helper.FayeMessage
import com.hippo.helper.ParseMessage
import com.hippo.receiver.NetworkStatus
import com.hippo.utils.HippoLog
import java.util.*
import socket.io.SocketIOClient
import socket.io.SocketMessage


/**
 * Created by gurmail on 2020-04-18.
 * @author gurmail
 */
object ConnectionManager: OnCloseListener, OnInitializedListener {

    override fun onClose() {
        stopFayeClient()
        //closeConnection()
    }

    override fun onInitialized() {
        initFayeConnection()
    }

    private var fayeClient: BaseSocketClient? = null
    private val mChannels: HashSet<String> = HashSet()
    private val mPendingChannels: HashSet<String> = HashSet()
    private var connecting = false
    private var isDelaySubscribe = false

    //private var handler: Handler = Handler(Looper.getMainLooper())
    private val RECONNECTION_TIME = 5000
    private var retryCount = 0
    private var stopConnection = false

    private val NOT_CONNECTED = 0
    private val CONNECTED_TO_INTERNET = 1
    private val CONNECTED_TO_INTERNET_VIA_WIFI = 2
    private var NETWORK_STATUS = 1

    private var pongCount = 0
    private const val MAX_COUNT = 5
    private const val MAX_RETRY_ATTEMPTS = 50

    fun initFayeConnection() {
        fayeClient = getSocketIOClient()
        if(fayeClient != null && !isConnected() && !connecting) {
            fayeClient!!.connectServer()
            connecting = true
            println("########################## ConnectionManager initFayeConnection ##########################")
        }
        setListener()
    }

    fun forceInitFayeConnection() {
        fayeClient = getSocketIOClient()
        if(fayeClient != null && !isConnected()) {
            fayeClient!!.connectServer()
            connecting = true
            println("########################## ConnectionManager forceInitFayeConnection ##########################")
        }
        setListener()
    }

    private fun getSocketIOClient(): BaseSocketClient? {
        val socketMessage = SocketMessage()
        if(fayeClient == null) {
            if (CommonData.getUserDetails() != null && !TextUtils.isEmpty(CommonData.getUserDetails().data.en_user_id)) {
                val jObj = JSONObject()
                jObj.putOpt(SocketMessage.KEY_APP_SECRET_KEY, CommonData.getUserDetails().data.appSecretKey)
                jObj.putOpt(SocketMessage.KEY_EN_USER_ID, CommonData.getUserDetails().data.en_user_id)
                jObj.putOpt(SocketMessage.KEY_DEVICE_TYPE, 1)
                jObj.putOpt(SocketMessage.KEY_SOURCE, 1)
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("language = "+HippoConfig.getInstance().currentLanguage)
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                if(TextUtils.isEmpty(HippoConfig.getInstance().currentLanguage)) {
                    jObj.putOpt(SocketMessage.KEY_LANG, "en")
                } else {
                    jObj.putOpt(SocketMessage.KEY_LANG, HippoConfig.getInstance().currentLanguage)
                }

                socketMessage.setUserAuthObj(jObj)
            } else {
                return null
            }
            fayeClient = SocketIOClient(getSocketIOUrl(), socketMessage)
        }
        return fayeClient
    }

    private fun reconnectConnection() {
        initFayeConnection()
    }

    private fun forceReConnection() {
        forceInitFayeConnection();
    }

    private fun getSocketIOUrl(): String {
        if (CommonData.getServerUrl() == FuguAppConstant.BETA_LIVE_SERVER) {
            return "https://beta-live-api.fuguchat.com:3001"
        }
        return CommonData.getSocketServerUrl()
    }



    private fun setListener() {
        if(fayeClient != null) {
            fayeClient!!.setmConnectionListener(object: FayeClientListener {

                override fun onConnectedServer(fc: BaseSocketClient?) {
                    stopConnection = true
                    connecting = false
                    retryCount = 0
                    if(!isDelaySubscribe)
                        subscribePendingChannels()
                    else
                        subscribeDelayPendingChannels()
                    BusProvider.getInstance().post(FayeMessage(BusEvents.CONNECTED_SERVER.toString(), "", ""))
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onConnectedServer <<<<<<<<<<<<<<<<<<<<<<")
                    stopAutoAttemptConnection()
                }

                override fun onDisconnectedServer(fc: BaseSocketClient?) {
                    connecting = false
                    stopConnection = false
                    if(!forceStop) {
                        saveSubsribedChannels()
                        BusProvider.getInstance().post(FayeMessage(BusEvents.DISCONNECTED_SERVER.toString(), "", ""))
                        //println(">>>>>>>>>>>>>>>>> ConnectionManager onDisconnectedServer <<<<<<<<<<<<<<<<<<<<<<")
                        attemptAutoConnection()
                    }
                }

                override fun onReceivedMessage(fc: BaseSocketClient?, msg: String?, channel: String?) {
                    //connecting = true
                    println(msg)
                    stopConnection = true
                    retryCount = 0
                    ParseMessage.receivedMessage(msg, channel)
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onReceivedMessage <<<<<<<<<<<<<<<<<<<<<<")
                }

                override fun onPongReceived() {
                    //connecting = true
                    stopConnection = true
                    retryCount = 0
                    subscribePendingChannels()
                    BusProvider.getInstance().post(FayeMessage(BusEvents.PONG_RECEIVED.toString(), "", ""))
                    pongCount()
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onPongReceived <<<<<<<<<<<<<<<<<<<<<<")
                }

                override fun onWebSocketError() {
                    connecting = false
                    pongCount = 0
                    BusProvider.getInstance().post(FayeMessage(BusEvents.WEBSOCKET_ERROR.toString(), "", ""))
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onWebSocketError <<<<<<<<<<<<<<<<<<<<<<")
                }

                override fun onErrorReceived(fc: BaseSocketClient?, msg: String?, channel: String?) {
                    //connecting = true
                    BusProvider.getInstance().post(FayeMessage(BusEvents.ERROR_RECEIVED.toString(), channel, msg))
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onErrorReceived <<<<<<<<<<<<<<<<<<<<<<")
                }

                override fun onNotConnected() {
                    connecting = false
                    pongCount = 0
                    BusProvider.getInstance().post(FayeMessage(BusEvents.NOT_CONNECTED.toString(), "", ""))
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager onNotConnected <<<<<<<<<<<<<<<<<<<<<<")
                }

                override fun onSubscriptionError() {
                    //connecting = true
                    retryCount = 0
                    //println(">>>>>>>>>>>>>>>>> ConnectionManager subscriptionError <<<<<<<<<<<<<<<<<<<<<<")
                    reSubscribeChannels()
                }
            })
        }
    }

    public fun isConnected(): Boolean {
        return fayeClient != null && fayeClient!!.isConnectedServer()
    }

    public fun subScribeChannel(channel: String) {
        println(">>>>>>>>>>>>>>>>>>  $channel <<<<<<<<<<<<<<<<<")
        if(isConnected()) {
            if(!mChannels.contains(channel)) {
                mChannels.add(channel)
                fayeClient!!.subscribeChannel(channel)
            } else {
                // already subscribed channel
            }
        } else {
            // pending channels for subscriptions
            mPendingChannels.add(channel)
            reconnectConnection()
        }
    }

    public fun subscribeOnDelay(channel: String) {
        if(isConnected()) {
            if(!mChannels.contains(channel)) {
                mChannels.add(channel)
                fayeClient!!.subscribeChannel(channel)
            } else {
                mChannels.add(channel)
                fayeClient!!.subscribeChannel(channel)
                // already subscribed channel
            }
        } else {
            isDelaySubscribe = true
            mPendingChannels.add(channel)
            reconnectConnection()
        }
    }

    public fun unsubScribeChannel(channel: String) {
        mChannels.remove(channel)
        if(isConnected())
            fayeClient!!.unsubscribeChannel(channel)
    }

    public fun publish(channel: String, jsonObject: JSONObject) {
        if(jsonObject.has(FuguAppConstant.IS_TYPING)) {
            jsonObject.put("lang", HippoConfig.getInstance().currentLanguage)
        }
        publish(channel, jsonObject, false)
    }

    var count: Int = 0
    public fun publish(channel: String, jsonObject: JSONObject, canStoreLocally: Boolean) {
        if(isConnected()) {
            fayeClient!!.publish(channel, jsonObject)
        } else {
            mPendingChannels.add(channel)
            if(connecting) {
                count += 1;
            }

            if(count>2) {
                count = 0;
                forceReConnection()
            } else {
                //CoroutineScope
                reconnectConnection()
            }
        }
    }

    private fun pongCount() {
        pongCount += 1
        if(pongCount > MAX_COUNT) {
            if(HippoConfig.getInstance().context == null || !ConnectionUtils.isAppRunning(HippoConfig.getInstance().context)) {
                if (HippoConfig.getInstance().fayeCallDate == null || !HippoConfig.getInstance().fayeCallDate.isCallServiceRunning()) {
                    println("Closing connection")
                    stopFayeClient()
                } else {
                    pongCount = 0
                    println("Inside the running activities")
                }
            } else {
                pongCount = 0
                println("Outside the running activities")
            }
        }
    }

    var forceStop: Boolean = false
    private fun stopFayeClient() {
        if(fayeClient != null &&
            (HippoConfig.getInstance().fayeCallDate == null || !HippoConfig.getInstance().fayeCallDate.isCallServiceRunning())) {
            val thread = HandlerThread("TerminateThread")
            thread.start()
            Handler(thread.looper).post(Runnable {
                if(fayeClient != null) {
                    mPendingChannels.clear()
                    mChannels.clear()
                    if(isConnected()) {
                        forceStop = true
                        fayeClient!!.disconnectServer()
                    }
                    pongCount = 0
                    fayeClient = null
                    connecting = false
                }
            })
        }
    }

    private fun reSubscribeChannels() {
        synchronized(this) {
            val tempChannel = mChannels
            for(channel in tempChannel) {
                unsubScribeChannel(channel)
            }
            Handler().postDelayed({
                for(channel in tempChannel) {
                    subScribeChannel(channel)
                }
            }, 500)
        }
    }

    private fun subscribePendingChannels() {
        synchronized(this) {
            if(mPendingChannels.size>0) {
                val tempChannel = mPendingChannels
                for (channel in tempChannel) {
                    fayeClient!!.subscribeChannel(channel)
                    mChannels.add(channel)
                }
                mPendingChannels.removeAll(tempChannel)
            }
        }
    }

    private fun subscribeDelayPendingChannels() {
        Handler().postDelayed({
            isDelaySubscribe = false
            if(mPendingChannels.size>0) {
                val tempChannel = mPendingChannels
                for (channel in tempChannel) {
                    fayeClient!!.subscribeChannel(channel)
                }
                mPendingChannels.removeAll(tempChannel)
            }
        }, 2000)
    }

    public fun changeStatus(status: Int) {
        NETWORK_STATUS = status
        when (status) {
            NOT_CONNECTED -> {
                saveSubsribedChannels()
                stopAutoAttemptConnection()
                retryCount = 0
                BusProvider.getInstance().post(NetworkStatus(NOT_CONNECTED))
            }
            CONNECTED_TO_INTERNET, CONNECTED_TO_INTERNET_VIA_WIFI -> {
                initFayeConnection()
                BusProvider.getInstance().post(NetworkStatus(CONNECTED_TO_INTERNET))
            }
        }
    }

    @Synchronized
    private fun saveSubsribedChannels() {
        connecting = false
        if(mChannels.size>0) {
            mPendingChannels.addAll(mChannels)
            mChannels.clear()
        }
    }

    private fun attemptAutoConnection() {
        if(NETWORK_STATUS != NOT_CONNECTED) {
            if(retryCount < MAX_RETRY_ATTEMPTS && !stopConnection) {
                retryCount += 1
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            try {
                                if(!stopConnection)
                                    initFayeConnection()
                            } catch (e: Exception) {

                            }
                        }
                    }, RECONNECTION_TIME.toLong())
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun stopAutoAttemptConnection() {
        //handler.removeCallbacks(runnable)
    }

    internal var runnable: Runnable = Runnable {
        try {
            println("************************** RECONNECTING ***********************************")
            initFayeConnection()
        } catch (e: Exception) {

        }
    }

    fun updateLanguage(lang: String) {
        fayeClient?.updateLang(lang)
    }

}
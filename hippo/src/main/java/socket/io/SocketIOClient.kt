package socket.io

import android.util.Log
import com.hippo.database.CommonData
import faye.BaseSocketClient
import faye.FayeAgentListener
import faye.FayeClientListener
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class SocketIOClient : BaseSocketClient{

    private val TAG =  "SOCKET_IO "

    private val socket: Socket

    private val socketMessage:SocketMessage
    private val url:String

//    private var serverConnected:Boolean = false
//    private var socketConnected:Boolean = false

    private val channels:HashSet<String>

    private var mConnectionListener: FayeClientListener? = null
    private var mAgentListener: FayeAgentListener? = null

    private val onConnect: Emitter.Listener = Emitter.Listener { args ->
        /*if(!serverConnected) {
            serverConnected = true
            handshake()
            //Log.w(TAG + "onConnect", "args="+getArgsStr(args))
        }*/
        handshake()
        //Log.w(TAG + "onConnect", "args="+getArgsStr(args))
    }
    private val onDisconnect: Emitter.Listener = Emitter.Listener { args ->
        //serverConnected = false
        //Log.w(TAG + "onDisconnect", "args="+getArgsStr(args))
        GlobalScope.launch(Dispatchers.Main) {
            mConnectionListener?.onDisconnectedServer(this@SocketIOClient)
            mAgentListener?.onDisconnectedServer(this@SocketIOClient)
        }
    }
    private val onConnectError: Emitter.Listener = Emitter.Listener { args ->
        //serverConnected = false
        //Log.w(TAG + "onConnectError", "args="+getArgsStr(args))
        GlobalScope.launch(Dispatchers.Main) {
            mConnectionListener?.onNotConnected()
        }
    }
    private val onPong: Emitter.Listener = Emitter.Listener { args ->
        //Log.w(TAG + "onPong", "args="+getArgsStr(args))
        GlobalScope.launch(Dispatchers.Main) {
            mConnectionListener?.onPongReceived()
            mAgentListener?.onPongReceived()
        }
    }
    private val onError: Emitter.Listener = Emitter.Listener { args ->
        //Log.w(TAG + "onError", "args="+getArgsStr(args))
        GlobalScope.launch(Dispatchers.Main) {
            mConnectionListener?.onWebSocketError()
            mAgentListener?.onWebSocketError()
        }
    }


    private val handshakeListener: Emitter.Listener = Emitter.Listener { args ->
        //Log.w(TAG + "handshakeListener", "args="+getArgsStr(args))
        //socketConnected = true
    }

    private val subscribeListener: Emitter.Listener = Emitter.Listener { args ->
        //Log.w(TAG + "subscribeListener", "args="+getArgsStr(args))
    }

    private val unsubscribeListener: Emitter.Listener = Emitter.Listener { args ->
        //Log.w(TAG + "unsubscribeListener", "args="+getArgsStr(args))
    }

    private val messageListener: Emitter.Listener = Emitter.Listener { args ->
        args.forEach {
            if(it != null){
                if(it is JSONObject){
                    //Log.w(TAG + "messageListener", "json arg => "+(it as JSONObject).toString())
                    handleMessage(it)
                } else if(it is String){
                    //Log.w(TAG + "messageListener", "str arg => "+(it as String))
                    try{ handleMessage(JSONObject(it)) } catch(e:JSONException){ e.printStackTrace() }
                }
            }
        }
    }

    private val handshakeAck = object: Ack{
        override fun call(vararg args: Any?) {
            args.forEach {
                if(it != null){
                    if(it is JSONObject){
                        //Log.w(TAG + "handshake ack", "json arg = "+(it as JSONObject).toString())
                    } else if(it is String){
                        //Log.w(TAG + "handshake ack", "str arg = "+(it as String))
                    }
                    GlobalScope.launch(Dispatchers.Main) {
                        mConnectionListener?.onConnectedServer(this@SocketIOClient)
                        mAgentListener?.onConnectedServer(this@SocketIOClient)
                    }
                }
            }
        }
    }
    private val publishAck = object: Ack{
        override fun call(vararg args: Any?) {
            args.forEach {
                if(it != null){
                    if(it is JSONObject){
                        Log.w(TAG + "publish ack", "json arg =>> "+(it as JSONObject).toString())
                        handleMessage(it)
                    } else if(it is String){
                        Log.w(TAG + "publish ack", "str arg =>> "+(it as String))
                        try{ handleMessage(JSONObject(it)) } catch(e:JSONException){ e.printStackTrace() }
                    }
                }
            }
        }
    }


    constructor(url: String, socketMessage: SocketMessage){
        this.url = url
        this.socketMessage = socketMessage
        this.channels = HashSet()

        val opts = IO.Options()
        opts.transports = arrayOf(WebSocket.NAME)
        opts.reconnection = true

        socket = IO.socket(url, opts)
        println(TAG + " <-- initialize url --> $url")
        //Log.w(TAG + "initialize", "url=$url")
    }

    private fun initListeners(){
        socket.off(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_CONNECT, onConnect)

        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)

        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)

        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)

        socket.off(Socket.EVENT_RECONNECT, onConnect)
        socket.on(Socket.EVENT_RECONNECT, onConnect)

        socket.off(Socket.EVENT_RECONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_RECONNECT_ERROR, onConnectError)

        socket.off(Socket.EVENT_RECONNECT_FAILED, onConnectError)
        socket.on(Socket.EVENT_RECONNECT_FAILED, onConnectError)

        socket.off(Socket.EVENT_PONG, onPong)
        socket.on(Socket.EVENT_PONG, onPong)

        socket.off(Socket.EVENT_ERROR, onError)
        socket.on(Socket.EVENT_ERROR, onError)


        socket.off(SocketMessage.HANDSHAKE_CHANNEL, handshakeListener)
        socket.on(SocketMessage.HANDSHAKE_CHANNEL, handshakeListener)

        socket.off(SocketMessage.SUBSCRIBE_USER, subscribeListener)
        socket.on(SocketMessage.SUBSCRIBE_USER, subscribeListener)

        socket.off(SocketMessage.UNSUBSCRIBE_USER, unsubscribeListener)
        socket.on(SocketMessage.UNSUBSCRIBE_USER, unsubscribeListener)

        socket.off(SocketMessage.SUBSCRIBE_CHAT, subscribeListener)
        socket.on(SocketMessage.SUBSCRIBE_CHAT, subscribeListener)

        socket.off(SocketMessage.UNSUBSCRIBE_CHAT, unsubscribeListener)
        socket.on(SocketMessage.UNSUBSCRIBE_CHAT, unsubscribeListener)

        socket.off(SocketMessage.MESSAGE_CHANNEL, messageListener)
        socket.on(SocketMessage.MESSAGE_CHANNEL, messageListener)

        socket.off(SocketMessage.SERVER_PUSH, messageListener)
        socket.on(SocketMessage.SERVER_PUSH, messageListener)
    }

    private fun removeListeners() {
        socket.off(Socket.EVENT_CONNECT, onConnect)
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        socket.off(Socket.EVENT_RECONNECT, onConnect)
        socket.off(Socket.EVENT_RECONNECT_ERROR, onConnectError)
        socket.off(Socket.EVENT_RECONNECT_FAILED, onConnectError)
        socket.off(Socket.EVENT_PONG, onPong)
        socket.off(Socket.EVENT_ERROR, onError)

        socket.off(SocketMessage.HANDSHAKE_CHANNEL, handshakeListener)
        socket.off(SocketMessage.SUBSCRIBE_USER, subscribeListener)
        socket.off(SocketMessage.UNSUBSCRIBE_USER, unsubscribeListener)
        socket.off(SocketMessage.SUBSCRIBE_CHAT, subscribeListener)
        socket.off(SocketMessage.UNSUBSCRIBE_CHAT, unsubscribeListener)
        socket.off(SocketMessage.MESSAGE_CHANNEL, messageListener)
        socket.off(SocketMessage.SERVER_PUSH, messageListener)
    }

    override fun setmConnectionListener(mConnectionListener: FayeClientListener) {
        this.mConnectionListener = mConnectionListener
    }

    override fun setAgentListener(listener: FayeAgentListener) {
        this.mAgentListener = listener
    }

    override fun connectServer(){
        if(isConnected()) {
            //initListeners()
            return
            //disconnectServer()
        }

        initListeners()
        socket.connect()
        //Log.w(TAG + "connectServer", "socket=$socket")
    }

    override fun isConnectedServer(): Boolean {
        return socket.connected()
    }

    override fun isOpened(): Boolean {
        return socket.connected()
    }



    private fun handshake(){
        val json = socketMessage.handshakeJSON()
        socket.emit(SocketMessage.HANDSHAKE_CHANNEL, json, handshakeAck)
        Log.w(TAG + "handshake", SocketMessage.HANDSHAKE_CHANNEL+" json=$json")
    }

    override fun subscribeChannel(channel: String?){
        if(channel == null){
            return
        }
        val subChannel = if(CommonData.getUserDetails() != null && CommonData.getUserDetails().data != null
                && channel.equals("/"+CommonData.getUserDetails().data.userChannel, true)){
            SocketMessage.SUBSCRIBE_USER
        } else {
            SocketMessage.SUBSCRIBE_CHAT
        }
        channels.add(channel)
        val json = socketMessage.subscribeJSON(channel)
        socket.emit(subChannel, json)
        //Log.w("subscribeChannel", "$subChannel json=$json")
    }

    override fun unsubscribeChannel(channel: String?){
        if(channel == null){
            return
        }
        if(channels.contains(channel)) {
            unsubscribe(channel)
            channels.remove(channel)
        }
    }

    private fun unsubscribe(channel: String){
        val subChannel = if(CommonData.getUserDetails() != null && CommonData.getUserDetails().data != null
                && channel.equals("/"+CommonData.getUserDetails().data.userChannel, true)){
            SocketMessage.UNSUBSCRIBE_USER
        } else {
            SocketMessage.UNSUBSCRIBE_CHAT
        }

        val json = socketMessage.unsubscribeJSON(channel)
        socket.emit(subChannel, json)
        //Log.w(TAG + "unsubscribe", "$subChannel json=$json")
    }

    override fun unsubscribeAll() {
        for (channel in channels) {
            unsubscribe(channel)
        }
    }

    override fun publish(channel: String?, data: JSONObject?){
        if(channel == null || data == null){
            return
        }
        val json = socketMessage.publishJSON(channel, data)
        socket.emit(SocketMessage.MESSAGE_CHANNEL, json, publishAck)
        //Log.w(TAG + "publish", SocketMessage.MESSAGE_CHANNEL+" json=$json")
    }


    override fun disconnectServer(){
        //unsubscribe all channels
        for (channel in channels) {
            unsubscribe(channel)
        }
        channels.clear()

        socket.disconnect()
        removeListeners()
        //Log.w(TAG, "socket=$socket")
    }

    fun isConnected():Boolean{
        return socket.connected() // && serverConnected && socketConnected
    }

    private fun getArgsStr(vararg args: Any?):String{
        val sb = StringBuilder()
        args.forEach {
            if (it != null) {
                if (it is JSONObject) {
                    sb.append((it as JSONObject).toString()).append(',')
                } else if (it is String) {
                    sb.append(it).append(',')
                }
            }
        }
        return sb.toString()
    }


    private fun handleMessage(json: JSONObject) {
        try{
            val channel = json.optString(SocketMessage.KEY_CHANNEL)

            if(channels.contains(channel)){
                if (json.has(SocketMessage.KEY_ERROR)) {
                    val error = json.optString(SocketMessage.KEY_ERROR, null)
                    GlobalScope.launch(Dispatchers.Main) {
                        mConnectionListener?.onErrorReceived(this@SocketIOClient, error, channel)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        mConnectionListener?.onReceivedMessage(this@SocketIOClient, json.toString(), channel)
                        mAgentListener?.onReceivedMessage(this@SocketIOClient, json.toString(), channel)
                    }
                }
            } else {
                //Log.e(TAG + "handleMessage", "Not a valid channel $channel")
            }

        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun updateLang(lang: String) {
        if(socketMessage != null)
            socketMessage.updateLanguage(lang)
    }
}
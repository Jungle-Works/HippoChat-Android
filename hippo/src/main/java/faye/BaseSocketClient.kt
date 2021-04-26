package faye

import org.json.JSONObject

abstract class BaseSocketClient {

    abstract fun connectServer()

    abstract fun setmConnectionListener(mConnectionListener: FayeClientListener)

    abstract fun setAgentListener(listener: FayeAgentListener)

    abstract fun isConnectedServer(): Boolean

    abstract fun isOpened(): Boolean

    abstract fun subscribeChannel(channel: String?)

    abstract fun unsubscribeChannel(channel: String?)

    abstract fun publish(channel: String?, data: JSONObject?)

    abstract fun disconnectServer()

    abstract fun unsubscribeAll()

    abstract fun updateLang(lang: String)
}
package socket.io

import org.json.JSONObject

class SocketMessage {

    private var userAuthObj:JSONObject? = null

    fun setUserAuthObj(userAuthObj:JSONObject?){
        this.userAuthObj = userAuthObj
    }

    fun updateLanguage(lang: String) {
        if(userAuthObj != null) {
            userAuthObj!!.put(KEY_LANG, lang)
        }
    }

    private fun putUserKeys(json:JSONObject){
        userAuthObj?.run{
            for(key in keys()){
                json.putOpt(key, get(key))
            }
        }
    }

    fun handshakeJSON(): JSONObject{
        val json = JSONObject()
        putUserKeys(json)
        return json
    }

    fun subscribeJSON(channel:String): JSONObject{
        val json = JSONObject()
        json.putOpt(KEY_CHANNEL, channel)
        putUserKeys(json)
        return json
    }

    fun unsubscribeJSON(channel:String): JSONObject{
        val json = JSONObject()
        json.putOpt(KEY_CHANNEL, channel)
        putUserKeys(json)
        return json
    }

    fun publishJSON(channel: String, data: JSONObject): JSONObject{
        val json = JSONObject()
        json.putOpt(KEY_CHANNEL, channel)
        json.putOpt(KEY_DATA, data)
        putUserKeys(json)
        return json
    }

    companion object {
        const val HANDSHAKE_CHANNEL = "/socketio/handshake"
        const val SUBSCRIBE_USER = "/socketio/subscribe/user"
        const val UNSUBSCRIBE_USER = "/socketio/unsubscribe/user"
        const val SUBSCRIBE_CHAT = "/socketio/subscribe/chat"
        const val UNSUBSCRIBE_CHAT = "/socketio/unsubscribe/chat"
        const val MESSAGE_CHANNEL = "/socketio/message"
        const val SERVER_PUSH = "/socketio/server/push"

        const val KEY_CHANNEL = "channel"
        const val KEY_DATA = "data"
        const val KEY_ERROR = "error"

        const val KEY_DEVICE_TYPE = "device_type"
        const val KEY_SOURCE = "source"
        const val KEY_LANG = "lang"
        const val KEY_APP_SECRET_KEY = "app_secret_key"
        const val KEY_EN_USER_ID = "en_user_id"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_AGENT_TYPE = "agent_type"
    }

}
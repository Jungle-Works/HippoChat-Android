package com.hippo.helper

import android.text.TextUtils
import com.hippo.HippoConfig
import com.hippo.eventbus.BusProvider
import com.hippo.utils.HippoLog
import org.json.JSONObject

/**
 * Created by gurmail on 2020-04-19.
 * @author gurmail
 */
object ParseMessage {

    fun receivedMessage(msg: String?, channel: String?) {
        if(TextUtils.isEmpty(msg))
            return

        try {
            val data = JSONObject(msg)
            when(data.optInt("message_type")) {
                18 -> {
                    if (HippoConfig.getInstance().fayeCallDate != null) {
                        HippoConfig.getInstance().fayeCallDate.callingFlow(data, msg, channel)
                    }
                }
                27 -> {
                    if (HippoConfig.getInstance().fayeCallDate != null) {
                        HippoConfig.getInstance().fayeCallDate.startGroupCall(data, msg, channel)
                    }
                }
                else -> {
                    BusProvider.getInstance().post(FayeMessage(BusEvents.RECEIVED_MESSAGE.toString(), channel, msg))
                }
            }
        } catch (e: Exception) {
            BusProvider.getInstance().post(FayeMessage(BusEvents.RECEIVED_MESSAGE.toString(), channel, msg))
        }
    }

}
package com.hippo

import android.content.Context
import org.json.JSONObject

/**
 * Created by gurmail on 2020-04-28.
 * @author gurmail
 */
interface FayeCallDate {
    fun callingFlow(data: JSONObject, msg: String?, channel: String?)
    fun startGroupCall(data: JSONObject, msg: String?, channel: String?)
    fun isCallServiceRunning(): Boolean
}
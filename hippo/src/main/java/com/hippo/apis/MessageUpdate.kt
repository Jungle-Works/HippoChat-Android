package com.hippo.apis

import android.app.Activity
import android.text.TextUtils
import com.hippo.database.CommonData
import com.hippo.interfaces.OnMessageUpdate
import com.hippo.retrofit.*

/**
 * Created by gurmail on 14/10/20.
 * @author gurmail
 */
object MessageUpdate {

    fun updateMessage(activity: Activity, channelId: Long, messageMuid: String, taskStatus: Int, updatedMessage: String, listener: OnMessageUpdate) {
        val params: CommonParams.Builder = CommonParams.Builder()
        params.add("app_secret_key", CommonData.getUserDetails().data.appSecretKey)
        params.add("en_user_id", CommonData.getUserDetails().data.en_user_id)
        params.add("channel_id", channelId)
        params.add("message_muid", messageMuid)
        params.add("task_status", taskStatus)
        if(!TextUtils.isEmpty(updatedMessage)) {
            params.add("new_message", updatedMessage)
        }
        params.add("device_details", CommonData.deviceDetails(activity))
        val paramMap = params.build()

        RestClient.getApiInterface().deleteOrEditMessage(paramMap.map).enqueue(object: ResponseResolver<CommonResponse>(activity, true, true) {
            override fun success(t: CommonResponse?) {
                listener.onUpdateListener()
            }

            override fun failure(error: APIError?) {
                listener.onUpdatefailed()
            }
        })
    }

}
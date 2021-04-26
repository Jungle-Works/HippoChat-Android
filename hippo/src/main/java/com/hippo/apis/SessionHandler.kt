package com.hippo.apis

import com.hippo.callback.OnStartSessionListener
import com.hippo.database.CommonData
import com.hippo.model.groupCall.GroupCallResponse
import com.hippo.retrofit.*
import java.util.*

/**
 * Created by gurmail on 2020-07-13.
 * @author gurmail
 */
object SessionHandler {

    fun startSession(transactionId: String, listener: OnStartSessionListener) {
        val params = HashMap<String, Any>()
        params["app_secret_key"] = CommonData.getUserDetails().data.appSecretKey
        params["user_id"] = CommonData.getUserDetails().data.userId
        params["en_user_id"] = CommonData.getUserDetails().data.en_user_id
        params["transaction_id"] = transactionId


        val paramsObj = CommonParams.Builder()
                .addAll(params)
                .build()

        RestClient.getApiInterface().groupCallChannelDetails(paramsObj.map).enqueue(object: ResponseResolver<GroupCallResponse>() {
            override fun success(t: GroupCallResponse?) {
                listener?.onStartListener(t!!)
                //startGroupCall()
            }

            override fun failure(error: APIError?) {
                try {
                    listener?.onErrorListener(error?.message!!)
                } catch (e: Exception) {
                }
            }

        })
    }


}
package com.hippo.callback

import com.hippo.model.groupCall.GroupCallResponse

/**
 * Created by gurmail on 2020-07-16.
 * @author gurmail
 */
interface OnStartSessionListener {
    fun onStartListener(t: GroupCallResponse)
    fun onErrorListener(error: String)
}
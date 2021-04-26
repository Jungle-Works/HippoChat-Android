package com.hippo

/**
 * Created by gurmail on 2020-07-14.
 * @author gurmail
 */
interface OnGroupSessionListener {

    fun onStartSession(transactionId: String)
    fun onSessionEnded(transactionId: String)
    fun onErrorInSession(error: String)
    fun onJoiningSession(transactionId: String)
}
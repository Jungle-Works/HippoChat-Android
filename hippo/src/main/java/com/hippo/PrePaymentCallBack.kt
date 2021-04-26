package com.hippo

import com.hippo.model.payment.AddedPaymentGateway

/**
 * Created by gurmail on 2020-06-16.
 * @author gurmail
 */
interface PrePaymentCallBack {

    fun onMethodReceived(prepaymentList: List<AddedPaymentGateway>)
    fun onPaymentSuccess()
    fun onPaymentfailed()
}
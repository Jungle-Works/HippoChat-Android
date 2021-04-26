package com.hippo.callback

import com.hippo.model.payment.AddedPaymentGateway

/**
 * Created by gurmail on 2020-05-06.
 * @author gurmail
 */
interface OnPaymentItemClickListener {
    fun onItemClickListener(paymentGayeway: AddedPaymentGateway)
}
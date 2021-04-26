package com.hippo.adapter;

import com.hippo.model.HippoPayment;
import com.hippo.model.Message;

/**
 * Created by gurmail on 2019-11-05.
 *
 * @author gurmail
 */
public interface OnPaymentListener {
    void onPaymentViewClicked(Message message, HippoPayment payment, int position, String url, int messagePos);
}

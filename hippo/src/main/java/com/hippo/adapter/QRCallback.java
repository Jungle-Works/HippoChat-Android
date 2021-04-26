package com.hippo.adapter;

import com.hippo.model.Message;

/**
 * Created by gurmail on 26/04/18.
 *
 * @author gurmail
 */

public interface QRCallback {


    void onFormClickListener(int id, Message currentFormMsg);

    void onFormClickListenerTicket(int id, Message currentFormMsg, int position);

    void skipFormCallback(Message currentFormMsg);

    void onClickListener(Message message, int pos, FuguMessageAdapter.QuickReplyViewHolder viewHolder);
}

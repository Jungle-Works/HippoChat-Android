package com.hippo.adapter;

import com.hippo.model.Message;

/**
 * Created by amit on 08/05/18.
 */

public interface QuickReplyAdapaterActivityCallback {

    void QuickReplyListener(Message message, int pos);

    void sendActionId(Message event);

    void onCardClicked(Message message, String userId, int pos);

    void onProfileClicked(Message message, String userId, int pos);
}

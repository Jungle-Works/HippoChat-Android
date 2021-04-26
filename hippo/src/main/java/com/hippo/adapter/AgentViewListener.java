package com.hippo.adapter;

import com.hippo.model.Message;

/**
 * Created by gurmail on 2019-10-21.
 *
 * @author gurmail
 */
public interface AgentViewListener {

    void onCardClickListener(Message message, String id, int pos);

    void onShowProfile(Message message, String userId, int pos);
}

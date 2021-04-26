package com.hippo.adapter;

import com.hippo.model.Message;

/**
 * Created by gurmail on 11/02/19.
 *
 * @author gurmail
 */
public interface UserConcentListener {

    void onUserConcent(int position, String btnId, Message message, String actionId, String url);
}

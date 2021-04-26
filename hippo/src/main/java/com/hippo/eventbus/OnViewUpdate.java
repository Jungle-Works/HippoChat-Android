package com.hippo.eventbus;

import com.hippo.model.FuguConversation;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-01-26.
 *
 * @author gurmail
 */
public class OnViewUpdate {
    public int type;
    public ArrayList<FuguConversation> fuguConversationList;

    public OnViewUpdate(int type) {
        this.type = type;
    }

    public OnViewUpdate(int type, ArrayList<FuguConversation> fuguConversationList) {
        this.type = type;
        this.fuguConversationList = fuguConversationList;
    }

}

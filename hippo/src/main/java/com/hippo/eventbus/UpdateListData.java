package com.hippo.eventbus;

import com.hippo.model.FuguConversation;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-01-26.
 *
 * @author gurmail
 */
public class UpdateListData {

    public ArrayList<FuguConversation> fuguConversationList;

    public UpdateListData(ArrayList<FuguConversation> fuguConversationList) {
        this.fuguConversationList = fuguConversationList;
    }
}

package com.hippo.support.callback;

import com.hippo.support.model.callbackModel.OpenChatParams;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public interface SupportDetailView extends BaseInterface {

    void openChat(OpenChatParams chatParams);

    void sucessfull();

    void showError();

}

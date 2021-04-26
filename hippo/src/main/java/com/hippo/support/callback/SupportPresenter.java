package com.hippo.support.callback;

import com.hippo.support.model.callbackModel.SendQueryChat;

/**
 * Created by gurmail on 29/03/18.
 */

public interface SupportPresenter {

    void fetchData(String defaultFaqName, int serverDBVersion);

//    @Deprecated
//    void openChat(String categoryName, String transactionId, String userUniqueId, int supportId, ArrayList<String> pathList);

    void openChat(SendQueryChat queryChat);

    void onDestroy();

}

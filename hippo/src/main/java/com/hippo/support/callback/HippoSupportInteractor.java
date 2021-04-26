package com.hippo.support.callback;

import android.app.Activity;

import com.hippo.support.model.SupportDataList;

/**
 * Created by gurmail on 29/03/18.
 */

public interface HippoSupportInteractor {

    interface OnFinishedListener {

        void onSuccess(SupportDataList response);

        void onSuccess();

        void onFailure();

        //void openChat(String transactionId, String userUniqueKey, String  channelName, ArrayList<String> tagsList, String[] data);
    }

    void getSupportData(Activity activity, int serverDBVersion, String defaultCategory, OnFinishedListener onFinishedListener);

    //void openChat(String categoryName, String transactionId, String userUniqueId, int supportId, ArrayList<String> pathList, OnFinishedListener onFinishedListener);


}

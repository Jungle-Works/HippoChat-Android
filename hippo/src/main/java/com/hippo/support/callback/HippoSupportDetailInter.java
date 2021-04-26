package com.hippo.support.callback;

import android.app.Activity;

import com.hippo.support.model.callbackModel.SendQueryChat;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public interface HippoSupportDetailInter {

    interface OnFinishedListener {

        void onSuccess();

        void onFailure();
    }

    void getSupportData(Activity activity, SendQueryChat queryChat, OnFinishedListener onFinishedListener);

}

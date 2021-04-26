package com.hippo.support.logicImplView;

import android.app.Activity;

import com.hippo.support.Utils.CommonSupportParam;
import com.hippo.support.callback.HippoSupportInteractor;
import com.hippo.support.callback.HippoSupportView;
import com.hippo.support.callback.SupportPresenter;
import com.hippo.support.model.SupportDataList;
import com.hippo.support.model.callbackModel.OpenChatParams;
import com.hippo.support.model.callbackModel.SendQueryChat;

/**
 * Created by gurmail on 29/03/18.
 */

public class HippoSupportViewImpl implements SupportPresenter, HippoSupportInteractor.OnFinishedListener {
    private static final String TAG = HippoSupportViewImpl.class.getSimpleName();
    private HippoSupportView supportView;
    private Activity activity;
    private HippoSupportInteractor interactor;

    public HippoSupportViewImpl(Activity activity, HippoSupportView supportView, HippoSupportInteractor interactor) {
        this.activity = activity;
        this.supportView = supportView;
        this.interactor = interactor;
    }


    @Override
    public void onSuccess(SupportDataList supportResponse) {
        if(supportView != null)
            supportView.hideProgress();
        supportView.setData(supportResponse);
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure() {
        if (supportView != null) {
            supportView.hideProgress();
            supportView.showError();
        }
    }

    @Override
    public void fetchData(String defaultCategory, int serverDBVersion) {
        if(supportView != null)
            supportView.showProgress();
        interactor.getSupportData(activity, serverDBVersion, defaultCategory, this);
    }

    @Override
    public void openChat(SendQueryChat queryChat) {
        try {

            OpenChatParams chatParams = new CommonSupportParam().getOpenChatParam(
                    queryChat.getCategory().getCategoryName(),
                    queryChat.getTransactionId(), queryChat.getUserUniqueId(),
                    queryChat.getSupportId(), queryChat.getPathList(), queryChat.getSubHeader());

            if(supportView != null)
                supportView.openChatSupport(chatParams);

        } catch (Exception e) {
            e.printStackTrace();
            if(supportView != null)
                supportView.showError();
        }
    }

    @Override
    public void onDestroy() {
        supportView = null;
    }
}

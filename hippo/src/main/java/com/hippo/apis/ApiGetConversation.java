package com.hippo.apis;

import android.app.Activity;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.constant.FuguAppConstant;
import com.hippo.model.FuguGetConversationsResponse;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;

/**
 * Created by gurmail on 2019-12-09.
 *
 * @author gurmail
 */
public class ApiGetConversation implements FuguAppConstant {

    private Activity activity;
    private CallbackListener listener;
    private int defaultPageSize = 20;
    private int pageEnd = 0;

    public ApiGetConversation(Activity activity, CallbackListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void getConversation(String enUserId, int pageStart, boolean showLoader, boolean showError) {
        int pageEnd = pageStart + defaultPageSize - 1;
        CommonParams.Builder params = new CommonParams.Builder();
        params.add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
        params.add(EN_USER_ID, enUserId);
        params.add(APP_VERSION, HippoConfig.getInstance().getVersionCode());
        params.add(DEVICE_TYPE, 1);
        params.add("page_start", pageStart);
        params.add("page_end", pageEnd);

        CommonParams commonParams = params.build();

        RestClient.getApiInterface().getConversations(commonParams.getMap()).enqueue(new ResponseResolver<FuguGetConversationsResponse>(activity, showLoader, showError) {
            @Override
            public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                if(listener != null) {
                    listener.onSuccess(fuguGetConversationsResponse);
                }
            }

            @Override
            public void failure(APIError error) {
                if(listener != null)
                    listener.onFailure();
            }
        });


    }

    public interface CallbackListener {
        void onSuccess(FuguGetConversationsResponse fuguGetConversationsResponse);
        void onFailure();
    }
}

package com.hippo.apis;

import android.app.Activity;
import android.text.TextUtils;
import com.hippo.HippoConfig;
import com.hippo.NotificationListener;
import com.hippo.constant.FuguAppConstant;
import com.hippo.interfaces.OnClearNotificationListener;
import com.hippo.model.promotional.PromotionResponse;
import com.hippo.retrofit.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by gurmail on 2019-12-23.
 *
 * @author gurmail
 */
public class ApiGetMobileNotification {

    private Activity activity;
    private NotificationListener listener;
    private OnClearNotificationListener onClearNotificationListener;

    public ApiGetMobileNotification(Activity activity, NotificationListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public ApiGetMobileNotification(Activity activity, OnClearNotificationListener onClearNotificationListener) {
        this.activity = activity;
        this.onClearNotificationListener = onClearNotificationListener;
    }

    public void getNotificationData(int startOffset, int endOffset) {

        if(TextUtils.isEmpty(HippoConfig.getInstance().getAppKey()) ||
                TextUtils.isEmpty(HippoConfig.getInstance().getUserData().getEnUserId()))
            return;

        CommonParams params = new CommonParams.Builder()
                .add(FuguAppConstant.APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                .add("en_user_id", HippoConfig.getInstance().getUserData().getEnUserId())
                .add("start_offset", startOffset)
                .add("end_offset", endOffset)
                .build();
        RestClient.getApiInterface().fetchMobilePush(params.getMap()).enqueue(new ResponseResolver<PromotionResponse>() {
            @Override
            public void success(PromotionResponse promotionResponse) {
                if(listener != null)
                    listener.onSucessListener(promotionResponse);
            }

            @Override
            public void failure(APIError error) {
                if(listener != null)
                    listener.onFailureListener();
            }
        });
    }


    public void clearNotification(long channelId, final int position) {
        CommonParams.Builder builder = new CommonParams.Builder();
        builder.add(FuguAppConstant.APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
        builder.add("en_user_id", HippoConfig.getInstance().getUserData().getEnUserId());
        if(channelId > 0) {
            ArrayList<Long> ids = new ArrayList<>();
            ids.add(channelId);
            builder.add("channel_ids", ids);
            builder.add("delete_all_announcements", 0);
        } else {
            builder.add("delete_all_announcements", 1);
        }

        CommonParams params = builder.build();

        RestClient.getApiInterface().clearMobilePush(params.getMap()).enqueue(new ResponseResolver<CommonResponse>(activity, true, true) {
            @Override
            public void success(CommonResponse promotionResponse) {
                if(onClearNotificationListener != null)
                    onClearNotificationListener.onSucessListener(position);
            }

            @Override
            public void failure(APIError error) {
                if(onClearNotificationListener != null)
                    onClearNotificationListener.onFailure();
            }
        });
    }

}


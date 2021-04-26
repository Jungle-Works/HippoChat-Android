package com.hippo.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.UnreadCountFor;
import com.hippo.database.CommonData;
import com.hippo.helper.P2pUnreadCount;
import com.hippo.model.*;
import com.hippo.retrofit.*;

import java.util.ArrayList;

import static com.hippo.constant.FuguAppConstant.*;

/**
 * Created by gurmail on 06/06/18.
 *
 * @author gurmail
 */

public class UnreadCountApi {

    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();

    public UnreadCountApi() {

    }

    /**
     * Get conversations api hit
     */
    public void getConversations(Activity activity, String enUserId) {
        {
            try {
                if(CommonData.getHasUnreadCount()) {
                    int count = CommonData.getTotalUnreadCount();
                    System.out.println("from saved count = "+count);
                    HippoLog.e("count", "from saved count = "+count);
                    //countUnread.countValue(count);
                    if (HippoConfig.getInstance().getCallbackListener() != null) {
                        HippoConfig.getInstance().getCallbackListener().count(count);
                    }
                    return;
                }
            } catch (Exception e) {

            }
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, enUserId)
                    .add(APP_VERSION, HippoConfig.getInstance().getVersionCode())
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().getConversations(commonParams.getMap())
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(activity, false, false) {
                        @Override
                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                            try {
                                fuguConversationList.clear();
                                fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
                                new UnreadCount().execute(fuguConversationList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {

                        }
                    });
        }
    }

    public interface CountUnread {
        void countValue(int count);
    }

    //private ArrayList<UnreadCountModel> unreadCountModels = new ArrayList<>();

    /*private void updateCount(ArrayList<FuguConversation> fuguConversationList, CountUnread countUnread) {
        try {
            new UnreadCount(countUnread).execute(fuguConversationList);

//            int count = 0;
//            unreadCountModels.clear();
//            CommonData.setUnreadCount(unreadCountModels);
//            for(int i=0;i<fuguConversationList.size();i++) {
//                if(fuguConversationList.get(i).getUnreadCount()>0) {
//                    UnreadCountModel countModel = new UnreadCountModel(fuguConversationList.get(i).getChannelId(),
//                            fuguConversationList.get(i).getLabelId(), fuguConversationList.get(i).getUnreadCount());
//                    unreadCountModels.add(countModel);
//                    count = count + fuguConversationList.get(i).getUnreadCount();
//                }
//            }
//
//            CommonData.setUnreadCount(unreadCountModels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void getChannelUnreadCount(Activity activity, String enUserId, final String transactionId, String userUniqueKey,
                                      ArrayList<String> otherUserUniqueKeys, final UnreadCountFor countUnread) {

        CommonParams commonParams = new CommonParams.Builder()
                .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                .add(EN_USER_ID, enUserId)
                .add(USER_UNIQUE_KEY, userUniqueKey)
                .add(OTHER_USER_UNIQUE_KEY, otherUserUniqueKeys)
                .add("transaction_id", transactionId)
                .add(APP_VERSION,HippoConfig.getInstance().getVersionCode())
                .add(DEVICE_TYPE, 1)
        .build();

        Gson gson = new GsonBuilder().create();
        JsonArray otherUsersArray = null;

        if (otherUserUniqueKeys != null) {
            otherUsersArray = gson.toJsonTree(otherUserUniqueKeys).getAsJsonArray();
        }

        HippoUnreadCountParams params = new HippoUnreadCountParams(HippoConfig.getInstance().getAppKey(),
                transactionId, userUniqueKey, otherUsersArray, enUserId);

        if(!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
            params.setLang(HippoConfig.getInstance().getCurrentLanguage());

        try {
            P2pUnreadCount.INSTANCE.removeTransactionId(transactionId);
            P2pUnreadCount.INSTANCE.setLocalTransactionId(transactionId, otherUserUniqueKeys.get(0));
        } catch (Exception e) {

        }

        RestClient.getApiInterface().fetchUnreadCountFor(params)
           .enqueue(new ResponseResolver<UnreadCountResponse>(activity, false, false) {
           @Override
           public void success(UnreadCountResponse response) {
              try {
                  int count = response.getData().getUnreadCount();
                  Long channelId = response.getData().getChannelId();
                  if(countUnread != null) {
                      countUnread.unreadCountFor(transactionId, count);
                  }

                  P2pUnreadCount.INSTANCE.updateChannelId(transactionId, channelId, count, "");
              } catch (Exception e) {
                  e.printStackTrace();
                  P2pUnreadCount.INSTANCE.updateChannelId(transactionId, -2L, 0, "");
              }
           }
           @Override
           public void failure(APIError error) {
               P2pUnreadCount.INSTANCE.updateChannelId(transactionId, -2L, 0, "");
           }
        });

    }
}

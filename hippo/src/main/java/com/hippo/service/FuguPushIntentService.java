package com.hippo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.hippo.HippoConfig;
import com.hippo.HippoNotificationConfig;
import com.hippo.R;
import com.hippo.activity.CampaignActivity;
import com.hippo.activity.ChannelActivity;
import com.hippo.activity.FuguChatActivity;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.FuguConversation;
import com.hippo.utils.filepicker.ToastUtil;

import static com.hippo.constant.FuguAppConstant.NOTIFICATION_TAPPED;

/**
 * Created by Bhavya Rattan on 26/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPushIntentService extends IntentService {

    private static final String TAG = "FuguPushIntentService";

    public FuguPushIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent notificationIntent;
        boolean isPromotional = intent.getBooleanExtra("is_announcement_push", false);
        if (isPromotional) {
            if (HippoConfig.getInstance() != null && !HippoConfig.getInstance().isDataCleared()) {
                Intent broadcastIntent = new Intent(this, CampaignActivity.class);
                broadcastIntent.putExtra("is_promotional_push", true);
                broadcastIntent.putExtra("is_announcement_push", true);
                broadcastIntent.putExtra("dataMessage", (String) intent.getBundleExtra("data").get("message"));
                broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(broadcastIntent);
            } else {
                PackageManager pm = this.getPackageManager();
                notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());
                notificationIntent.putExtra("is_announcement_push", true);
                startActivity(notificationIntent);
            }
        } else {
            long channelId = intent.getLongExtra("channelId", -1l);
            long labelId = intent.getLongExtra("labelId", -1l);
            FuguConversation conversation = new FuguConversation();
            conversation.setChannelId(channelId);
            conversation.setEnUserId(intent.getStringExtra("en_user_id"));
            conversation.setUserId(intent.getLongExtra("userId", -1L));
            conversation.setLabel(intent.getStringExtra("label"));
            conversation.setLabelId(labelId);
            conversation.setDisableReply(intent.getIntExtra("disable_reply", 0));
            if (channelId < 0 && labelId > 0) {
                conversation.setOpenChat(true);
            }
            if (HippoConfig.getInstance() != null && !HippoConfig.getInstance().isDataCleared()) {
                notificationIntent = new Intent(this, FuguChatActivity.class);
                if (channelId < 0 && labelId < 0) {
                    notificationIntent = new Intent(this, ChannelActivity.class);
                    String title = CommonData.getChatTitleContext();
                    if (TextUtils.isEmpty(title))
                        title = Restring.getString(this, R.string.fugu_support);
                    notificationIntent.putExtra("title", title);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(notificationIntent);
                } else if (HippoNotificationConfig.pushChannelId.compareTo(-1l) == 0) {
                    notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(notificationIntent);
                } else {
                    Intent mIntent = new Intent(NOTIFICATION_TAPPED);
                    mIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
                }
            } else {
                PackageManager pm = this.getPackageManager();
                notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());
                conversation.setStartChannelsActivity(true);
                notificationIntent.putExtra("startChatActivity", true);
                notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                startActivity(notificationIntent);

            }
        }
    }
}

package com.hippo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.hippo.activity.CampaignActivity;
import com.hippo.activity.ChannelActivity;
import com.hippo.activity.CustomerInitalActivity;
import com.hippo.activity.FuguChatActivity;
import com.hippo.activity.PrePaymentActivity;
import com.hippo.apis.ApiPutUserDetails;
import com.hippo.apis.GetPaymentGateway;
import com.hippo.apis.SessionHandler;
import com.hippo.callback.OnPaymentListListener;
import com.hippo.callback.OnStartSessionListener;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.dialog.SingleBtnDialog;
import com.hippo.helper.P2pUnreadCount;
import com.hippo.langs.LanguageManager;
import com.hippo.langs.Restring;
import com.hippo.model.AppUpdateModel;
import com.hippo.model.BusinessLanguages;
import com.hippo.model.CustomAttributes;
import com.hippo.model.FuguConversation;
import com.hippo.model.FuguCreateConversationParams;
import com.hippo.model.FuguPutUserDetailsResponse;
import com.hippo.model.HippoPayment;
import com.hippo.model.UserInfoModel;
import com.hippo.model.groupCall.GroupCallResponse;
import com.hippo.model.payment.AddedPaymentGateway;
import com.hippo.model.payment.PrePaymentData;
import com.hippo.payment.RazorPayData;
import com.hippo.payment.RazorPayment;
import com.hippo.receiver.FuguNetworkStateReceiver;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.CommonResponse;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.service.HippoService;
import com.hippo.support.HippoSupportActivity;
import com.hippo.utils.AndroidLoggingHandler;
import com.hippo.utils.CustomAlertDialog;
import com.hippo.utils.HippoLog;
import com.hippo.utils.StringUtil;
import com.hippo.utils.UniqueIMEIID;
import com.hippo.utils.UnreadCountApi;
import com.hippo.utils.customROM.XiaomiUtilities;
import com.hippo.utils.fileUpload.Prefs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

import faye.ConnectionManager;
import io.paperdb.Paper;
import io.socket.client.Manager;
import io.socket.client.Socket;


/**
 * Created by gurmail on 27/12/18.
 *
 * @author gurmail
 */
public class HippoConfig implements FuguAppConstant {

    private static final String TAG = HippoConfig.class.getSimpleName();
    private static HippoConfig hippoConfig;
    public static volatile Handler applicationHandler;
    private CaptureUserData userData;
    private static HippoConfigAttributes configAttributes;

    private String serverUrl = "";

    public int getHomeUpIndicatorDrawableId() {
        return homeUpIndicatorDrawableId;
    }

    public void setHomeUpIndicatorDrawableId(int homeUpIndicatorDrawableId) {
        this.homeUpIndicatorDrawableId = homeUpIndicatorDrawableId;
    }

    public int getChatScreenBg() {
        return chatScreenBg;
    }

    /*
     * used to set bg for chat screen
     * */
    public void setChatScreenBg(int chatScreenBg) {
        this.chatScreenBg = chatScreenBg;
    }

    //Drawable
    private int homeUpIndicatorDrawableId = -1;//R.drawable.hippo_ic_arrow_back;
    private int videoCallNotificationDrawable = R.drawable.hippo_default_notif_icon;
    private int videoCallDrawableId = -1;//R.drawable.hippo_ic_info;
    private int audioCallDrawableId = -1;
    private int chatInfoDrawable = -1;
    private int homeIconDrawable = -1;
    private int broadcastDrawable = -1;
    private int icSend = -1;
    private int chatScreenBg = -1;

    public int getIcSend() {
        return icSend;
    }

    public void setIcSend(int icSend) {
        this.icSend = icSend;
    }

    public String appKey = "";
    private String appType = "1";
    private HippoConfigAttributes attributes;

    private static String mResellerToken;
    private static int mReferenceId = -1;

    protected Context context;
    private Activity activity;
    private long lastClickTime = 0;

    private boolean isDataCleared = true;
    public static boolean DEBUG = false;
    private boolean isChannelActivity;
    private boolean isAnnouncementActivity;
    private boolean isAnnouncement;
    private static boolean isUnreadRequired;

    public static boolean progressLoader = true;
    private boolean setSkipNumber;

    private HippoConfig() {

    }

    public static long getMaxSize() {
        try {
            Long maxFileSize = 26214400l;
            maxFileSize = CommonData.getUserDetails().getData().getMaxFileSize();
            return maxFileSize;
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
        return 26214400;
    }

    public Context getContext() {
        return context;
    }

    public static HippoConfig getInstance() {
        if (hippoConfig == null) {
            hippoConfig = new HippoConfig();
        }
        return hippoConfig;
    }

    private HippoConfigAttributes getAttributes() {
        if (attributes == null) {
            attributes = CommonData.getAttributes();
        }
        return attributes;
    }

    private HippoInitCallback initCallback;
    private HippoAdditionalListener hippoAdditionalListener;

    public HippoAdditionalListener getHippoAdditionalListener() {
        return hippoAdditionalListener;
    }

    public void setHippoAdditionalListener(HippoAdditionalListener hippoAdditionalListener) {
        this.hippoAdditionalListener = hippoAdditionalListener;
    }

    public HippoInitCallback getInitCallback() {
        return initCallback;
    }

    private void setInitCallback(HippoInitCallback initCallback) {
        this.initCallback = initCallback;
    }

    public boolean isSetSkipNumber() {
        return setSkipNumber;
    }

    public void setSetSkipNumber(boolean setSkipNumber) {
        this.setSkipNumber = setSkipNumber;
    }

    //set true if you want to share all types of files in chat


    public static HippoConfig initHippoConfig(Activity activity, HippoConfigAttributes attributes) {
        return initHippoConfig(activity, attributes, null);
    }

    public static HippoConfig initHippoConfig(Activity activity, HippoConfigAttributes attributes, HippoInitCallback callback) {
        return initHippoConfig(activity, attributes, callback, null);
    }

    public static HippoConfig initHippoConfig(Activity activity, HippoConfigAttributes attributes, HippoInitCallback callback, Bundle bundle) {
        count = 0;
        hippoConfig = getInstance();
        hippoConfig.setInitCallback(callback);
        Paper.init(activity);

        applicationHandler = new Handler(activity.getMainLooper());

        DEBUG = attributes.isShowLog();
        HippoConfig.getInstance().activity = activity;

        if (TextUtils.isEmpty(attributes.getProvider())) {
            new CustomAlertDialog.Builder(activity)
                    .setMessage("Provider cannot be null")
                    .setPositiveButton("Ok", new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
                        @Override
                        public void onClick() {
                            HippoConfig.getInstance().activity.finish();
                        }
                    })
                    .show();
        } else {
            CommonData.setProvider(attributes.getProvider());
        }
        if (attributes.getColorConfig() != null) {
            CommonData.setColorConfig(attributes.getColorConfig());
        }

        FuguPutUserDetailsResponse response = CommonData.getUpdatedDetails();
        boolean isHippoPush = false;
        try {
            if (bundle != null) {
                if (bundle.containsKey("is_announcement_push") && bundle.getBoolean("is_announcement_push")) {
                    isHippoPush = true;
                } else {
                    FuguConversation conversation = new Gson().fromJson(bundle.getString(FuguAppConstant.CONVERSATION), FuguConversation.class);
                    if (conversation != null) {
                        isHippoPush = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null && response.getData() != null) {
            ConnectionManager.INSTANCE.initFayeConnection();
            if (callback != null) {
                callback.hasData();
                progressLoader = false;
                boolean fetchPayment = false;
                if (attributes != null && attributes.getAdditionalInfo() != null && attributes.getAdditionalInfo().isFetchPaymentMethod()) {
                    fetchPayment = true;
                }
                if (isHippoPush && fetchPayment)
                    GetPaymentGateway.INSTANCE.getPaymentGatewaysList(activity, null);
            }
        }

        if (!TextUtils.isEmpty(attributes.getImagePath()))
            CommonData.setUserImagePath(attributes.getImagePath());

        try {
            CommonData.saveDeviceToken(attributes.getDeviceToken());
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
        CommonData.saveFuguConfigAttribute(attributes);
        HippoLog.v("inside initHippoConfig", "inside initHippoConfig");

        if (isHippoPush && response != null && response.getData() != null && !TextUtils.isEmpty(response.getData().getEn_user_id())) {
            if (callback != null)
                callback.onPutUserResponse();
            if (attributes.isUnreadCount())
                hippoConfig.getUnreadCount(HippoConfig.getInstance().activity, CommonData.getUserDetails().getData().getEn_user_id());

            ConnectionManager.INSTANCE.subScribeChannel("/" + response.getData().getUserChannel());
            hippoConfig.setAllreadyData(activity, attributes);

            try {
                if (response.getData().getAppSecretKey() != null &&
                        !TextUtils.isEmpty(response.getData().getAppSecretKey())) {
                    HippoConfig.getInstance().appKey = response.getData().getAppSecretKey();
                    CommonData.setAppSecretKey(response.getData().getAppSecretKey());
                }
            } catch (Exception e) {

            }

        } else {
            hippoConfig.setFuguConfig(activity, attributes);
        }

        try {
            AndroidLoggingHandler.reset(new AndroidLoggingHandler());
            java.util.logging.Logger.getLogger(Socket.class.getName()).setLevel(Level.FINE);
            java.util.logging.Logger.getLogger(io.socket.parser.IOParser.class.getName()).setLevel(Level.FINE);
            java.util.logging.Logger.getLogger(io.socket.engineio.client.Socket.class.getName()).setLevel(Level.FINE);
            java.util.logging.Logger.getLogger(Manager.class.getName()).setLevel(Level.FINE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return hippoConfig;
    }

    private void initDownloader() {
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(activity, config);
    }

    private void setAllreadyData(final Activity activity, HippoConfigAttributes attributes) {
        try {
            HippoConfig.getInstance().isDataCleared = false;
            CommonData.setIsDataCleared(false);
            HippoConfig.getInstance().activity = activity;
            HippoConfig.getInstance().context = activity;
            HippoConfig.getInstance().appType = attributes.getAppType();
            HippoConfig.getInstance().userData = attributes.getCaptureUserData() == null ? new CaptureUserData()
                    : attributes.getCaptureUserData();
            CommonData.saveUserData(HippoConfig.getInstance().userData);
            CommonData.saveFuguConfigAttribute(attributes);
            hippoConfig.attributes = attributes;
            HippoConfig.getInstance().serverUrl = LIVE_SERVER; // live server
            HippoConfig.getInstance().appKey = attributes.getAppKey();
            HippoConfig.getInstance().appType = attributes.getAppType();
            if (HippoConfig.getInstance().appKey != null)
                CommonData.setAppSecretKey(HippoConfig.getInstance().appKey);
            CommonData.setAppType(HippoConfig.getInstance().appType);
            CommonData.clearLeftTimeInSec();
            registerNetworkListener(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFuguConfig(final Activity activity, HippoConfigAttributes attributes) {
        String environment = TextUtils.isEmpty(attributes.getEnvironment()) ? "live" : attributes.getEnvironment();

        if (environment != null && environment.equalsIgnoreCase("live")) {
            HippoConfig.getInstance().serverUrl = LIVE_SERVER; // live server
            CommonData.setServerUrl(LIVE_SERVER);
            CommonData.setSocketServerUrl(LIVE_SOCKEY_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("test")) {
            HippoConfig.getInstance().serverUrl = TEST_SERVER;
            CommonData.setServerUrl(TEST_SERVER);
            CommonData.setSocketServerUrl(TEST_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("dev")) {
            HippoConfig.getInstance().serverUrl = DEV_SERVER;
            CommonData.setServerUrl(DEV_SERVER);
            CommonData.setSocketServerUrl(DEV_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("dev3004")) {
            HippoConfig.getInstance().serverUrl = DEV_SERVER_3004;
            CommonData.setServerUrl(DEV_SERVER_3004);
            CommonData.setSocketServerUrl(DEV_SERVER_3004);
        } else if (environment != null && environment.equalsIgnoreCase("dev3003")) {
            HippoConfig.getInstance().serverUrl = DEV_SERVER_3003;
            CommonData.setServerUrl(DEV_SERVER_3003);
            CommonData.setSocketServerUrl(DEV_SERVER_3003);
        } else if (environment != null && environment.equalsIgnoreCase("beta-live")) {
            HippoConfig.getInstance().serverUrl = BETA_LIVE_SERVER; //test server
            CommonData.setServerUrl(BETA_LIVE_SERVER);
            CommonData.setSocketServerUrl(BETA_LIVE_SOCKEY_SERVER);
        } else {
            HippoConfig.getInstance().serverUrl = LIVE_SERVER; // live server
            CommonData.setServerUrl(LIVE_SERVER);
            CommonData.setSocketServerUrl(LIVE_SOCKEY_SERVER);
        }
        this.attributes = attributes;
        registerNetworkListener(activity);

        if (!TextUtils.isEmpty(CommonData.getUserCountryCode())) {
            initHippoCustomer(activity, attributes);
        } else {
            new ApiPutUserDetails(activity, null).getUserContryInfo(attributes, new ApiPutUserDetails.UserCallback() {
                @Override
                public void onSuccess(UserInfoModel userInfoModel, HippoConfigAttributes attributes) {
                    initHippoCustomer(activity, attributes);
                }
            });
        }

    }

    private void initHippoCustomer(Activity activity, HippoConfigAttributes attributes) {
        HippoConfig.getInstance().appKey = attributes.getAppKey();
        HippoConfig.getInstance().appType = attributes.getAppType();
        if (HippoConfig.getInstance().appKey != null)
            CommonData.setAppSecretKey(HippoConfig.getInstance().appKey);
        CommonData.setAppType(HippoConfig.getInstance().appType);
        CommonData.clearLeftTimeInSec();
        updateUserDetails(activity, attributes);
    }


    private void updateUserDetails(Activity activity, final HippoConfigAttributes attributes) {
        HippoConfig.getInstance().isDataCleared = false;
        CommonData.setIsDataCleared(false);
        HippoConfig.getInstance().activity = activity;
        HippoConfig.getInstance().context = activity;
        HippoConfig.getInstance().appType = attributes.getAppType();
        HippoConfig.getInstance().userData = attributes.getCaptureUserData() == null ? new CaptureUserData()
                : attributes.getCaptureUserData();
        CommonData.saveUserData(HippoConfig.getInstance().userData);
        CommonData.saveFuguConfigAttribute(attributes);
        new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
            @Override
            public void onSuccess() {
                if (getInitCallback() != null) {
                    getInitCallback().onPutUserResponse();
                }
                if (attributes.isUnreadCount())
                    getUnreadCount(HippoConfig.getInstance().activity, CommonData.getUserDetails().getData().getEn_user_id());

                try {
                    if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                            HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                        XiaomiUtilities.checkForDevicePermission(HippoConfig.getInstance().activity);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure() {
                if (getInitCallback() != null) {
                    getInitCallback().onErrorResponse();
                }
                try {
                    if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                            HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                        XiaomiUtilities.checkForDevicePermission(HippoConfig.getInstance().activity);
                    }
                } catch (Exception e) {

                }
                //XiaomiUtilities.checkForDevicePermission(HippoConfig.getInstance().activity);
            }
        }).sendUserDetails(attributes.getResellerToken(), attributes.getReferenceId());
    }

    public static void clearHippoData(Activity activity) {
        try {
            logOutUser(activity);
        } catch (Exception e) {

        }
        try {
            hippoConfig.clearLocalData();
            Prefs.with(activity).removeAll();
        } catch (Exception e) {

        }
    }

    private void clearLocalData() {
        HippoConfig.getInstance().isDataCleared = true;
        CommonData.setIsDataCleared(true);
        try {
            CommonData.clearData();
            userData = null;
        } catch (Exception e) {

        }
    }

    public String getAppKey() {
        if (TextUtils.isEmpty(appKey))
            appKey = CommonData.getAppSecretKey();
        return appKey;
    }

    public String getVersionCode() {
//        try {
//            PackageManager manager = activity.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
//            return info.versionCode + "";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "1";
//
//        }

        return BuildConfig.VERSION_CODE+"";
    }

    public String getVersionName() {
        return BuildConfig.VERSION_NAME;

//        try {
//            PackageManager manager = activity.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
//            return info.versionName + "";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "1";
//        }
    }

    public int getCodeVersion() {
        return BuildConfig.VERSION_CODE;

//        try {
//            PackageManager manager = activity.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
//            return info.versionCode;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 1;
//        }

    }

    public static String getmResellerToken() {
        if (TextUtils.isEmpty(mResellerToken))
            mResellerToken = CommonData.getResellerToken();
        return mResellerToken;
    }

    public static int getmReferenceId() {
        if (mReferenceId == -1)
            mReferenceId = CommonData.getReferenceId();
        return mReferenceId;
    }

    public String getAppType() {
        return HippoConfig.getInstance().appType;
    }

    public boolean isDataCleared() {
        return isDataCleared;
    }

    public boolean isChannelActivity() {
        return isChannelActivity;
    }

    public void setChannelActivity(boolean channelActivity) {
        isChannelActivity = channelActivity;
    }

    public boolean isAnnouncements() {
        return isAnnouncement;
    }

    public void setAnnouncementActivity(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }

    public void setRideTime(long timeInSecs) {
        if (timeInSecs > 0) {
            CommonData.setLeftTimeInSec(timeInSecs);
        } else {
            CommonData.clearLeftTimeInSec();
        }
    }

    public void stopOnlineStatus(Activity activity) {
        CommonData.clearLeftTimeInSec();
        new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        }).stopRideStatus();
    }

    public void discartEstimatedTime() {
        CommonData.clearLeftTimeInSec();
    }

    public void setTitle(String title) {
        CommonData.setChatTitle(title);
    }

    public void showConversations(final Activity activity, final String title) {
        if (HippoConfig.getInstance().activity == null || HippoConfig.getInstance().context == null) {
            HippoConfig.getInstance().activity = activity;
            HippoConfig.getInstance().context = activity;
        }
        CommonData.setChatTitle(title);
        if (CommonData.isFirstTimeWithNotification() || (CommonData.getConversationList() != null
                && CommonData.getConversationList().size() <= 0)) {
            if (!TextUtils.isEmpty(HippoConfig.getInstance().getUserData().getEnUserId())) {
                caseElse(title);
            } else {
                new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                    @Override
                    public void onSuccess() {
                        CommonData.setNotificationFirstClick(false);
                        /*if (CommonData.getConversationList().size() == 0) {
                            caseOne(title);
                        } else {
                            caseElse(title);
                        }*/
                        caseElse(title);
                        try {
                            if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                    HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                                XiaomiUtilities.checkForDevicePermission(activity);
                            }
                        } catch (Exception e) {

                        }
                        //XiaomiUtilities.checkForDevicePermission(activity);
                    }

                    @Override
                    public void onFailure() {
                        CommonData.setNotificationFirstClick(false);
                        try {
                            if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                    HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                                XiaomiUtilities.checkForDevicePermission(activity);
                            }
                        } catch (Exception e) {

                        }
                        //XiaomiUtilities.checkForDevicePermission(activity);
                    }
                }).sendUserDetails(HippoConfig.getmResellerToken(), HippoConfig.getmReferenceId(), HippoConfig.progressLoader, true);
            }
        } else {
            caseElse(title);
        }
    }

    public void openChat(Activity activity, Long messageChannelId) {
        if (HippoConfig.getInstance().activity == null || HippoConfig.getInstance().context == null) {
            HippoConfig.getInstance().activity = activity;
            HippoConfig.getInstance().context = activity;
        }
        openChat(activity, messageChannelId, null);
    }

    public void openChat(final Activity activity, final Long messageChannelId, final String titleString) {
        openChat(activity, messageChannelId, titleString, false);
    }

    public void openChat(final Activity activity, final Long messageChannelId, final String titleString, final boolean finishActivity) {

        if (HippoConfig.getInstance().getUserData() == null || userData.getUserId().compareTo(-1l) == 0) {
            HippoLog.v("In openChat before FuguChatActivity", "userData null");
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabelId(messageChannelId);
                    conversation.setLabel(titleString);
                    conversation.setOpenChat(true);
                    conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
                    conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
                    conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    activity.startActivity(chatIntent);
                    if (finishActivity)
                        activity.finish();

                    //XiaomiUtilities.checkForDevicePermission(activity);
                    try {
                        if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                            XiaomiUtilities.checkForDevicePermission(activity);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure() {
                    //XiaomiUtilities.checkForDevicePermission(activity);
                    try {
                        if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                            XiaomiUtilities.checkForDevicePermission(activity);
                        }
                    } catch (Exception e) {

                    }
                }
            }).sendUserDetails(HippoConfig.getInstance().getmResellerToken(), HippoConfig.getInstance().getmReferenceId());
        } else {
            Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setLabelId(messageChannelId);
            conversation.setLabel(titleString);
            conversation.setOpenChat(true);
            conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
            conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
            conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            activity.startActivity(chatIntent);
            if (finishActivity)
                activity.finish();
        }


    }

    public void openChatByUniqueId(Activity context, ChatByUniqueIdAttributes attributes) {
        activity = context;
        openChatByUniqueId(attributes);
    }

    @Deprecated
    public void openChatByUniqueId(ChatByUniqueIdAttributes attributes) {
        String transactionId = attributes.getTransactionId();
        String userUniqueKey = attributes.getUserUniqueKey();
        ArrayList<String> otherUserUniqueKeys = attributes.getOtherUserUniqueKeys();
        String channelName = attributes.getChannelName();
        ArrayList<String> tags = attributes.getTags();
        int chatType = 0;//attributes.getChatType() == null ? 2 : attributes.getChatType().chatType;
        String[] message = attributes.getMessage();
        ArrayList<String> groupingTags = attributes.getGroupingTags();
        String isSupportTicket = attributes.isSupportTicket() ? "1" : "0";
        CustomAttributes customAttributes = attributes.getCustomAttributes();
        boolean isInsertBotId = attributes.isInsertBotId();
        boolean skipBot = attributes.isSkipBot();

        if (!TextUtils.isEmpty(attributes.getAgentEmail())) {
            setAgentEmail(attributes.getAgentEmail());
        }

        setSingleChannelTransactionId(attributes.isSingleChannelTransactionId());

        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message,
                groupingTags, isSupportTicket, customAttributes, isInsertBotId, skipBot);
    }

    private void showGroupChat(final String transactionId, final String userUniqueKey, final ArrayList<String> otherUserUniqueKeys,
                               final String channelName, final ArrayList<String> tags, final int chatType, final String[] message,
                               final ArrayList<String> groupingTags, final String isSupportTicket, final CustomAttributes customAttributes,
                               final boolean isInsertBotId, final boolean skipBot) {
        HippoLog.i("showGroupChat", "In ShowGroupChat");
        if (HippoConfig.getInstance().getUserData() == null || getUserData().getUserId().compareTo(-1l) == 0) {

            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message,
                            groupingTags, isSupportTicket, customAttributes, isInsertBotId, skipBot);

//                    XiaomiUtilities.checkForDevicePermission(activity);
                    try {
                        if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                            XiaomiUtilities.checkForDevicePermission(activity);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure() {
//                    XiaomiUtilities.checkForDevicePermission(activity);
                    try {
                        if (HippoConfig.getInstance().getAttributes().getAdditionalInfo() != null &&
                                HippoConfig.getInstance().getAttributes().getAdditionalInfo().needDeviceOptimization()) {
                            XiaomiUtilities.checkForDevicePermission(activity);
                        }
                    } catch (Exception e) {

                    }
                }
            }).sendUserDetails(HippoConfig.getmResellerToken(), HippoConfig.getmReferenceId(), true);
        } else {
            showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType,
                    message, groupingTags, isSupportTicket, customAttributes, isInsertBotId, skipBot);
        }

    }

    private void showGroupChats(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys,
                                String channelName, ArrayList<String> tags, int chatType, String[] message,
                                ArrayList<String> groupingTags, String isSupportTicket,
                                CustomAttributes customAttributes, boolean isInsertBotId, boolean skipBot) {
        HippoLog.i("showGroupChat", "userData not null");
        Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
        HippoLog.d("UserName", "showGroupChat" + HippoConfig.getInstance().getUserData().getUserId());
        FuguConversation conversation = new FuguConversation();
        conversation.setLabelId(-1l);
        conversation.setLabel(CommonData.getUserDetails().getData().getBusinessName());
        conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
        conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
        conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

        if (otherUserUniqueKeys != null && otherUserUniqueKeys.size() > 0 && !TextUtils.isEmpty(otherUserUniqueKeys.get(0))) {
            chatType = 2;
        } else {
            chatType = 3;
        }
        chatIntent.putExtra(CHAT_TYPE, chatType);
        chatIntent.putExtra("isInsertBotId", isInsertBotId);
        chatIntent.putExtra("single_chat_trans_id", getSingleChannelTransactionId());

        if (skipBot) {
            chatIntent.putExtra("is_skip_bot", 1);
            chatIntent.putExtra("skipCreateChannel", false);
        }

        Gson gson = new GsonBuilder().create();
        JsonArray otherUsersArray = null;
        JsonArray tagsArray = null;

        if (otherUserUniqueKeys != null) {
            otherUsersArray = gson.toJsonTree(otherUserUniqueKeys).getAsJsonArray();
        }

        if (tags != null) {
            tagsArray = gson.toJsonTree(tags).getAsJsonArray();
        }

        FuguCreateConversationParams fuguPeerChatParams = new FuguCreateConversationParams();
        fuguPeerChatParams.setAppSecretKey(HippoConfig.getInstance().getAppKey());
        fuguPeerChatParams.setChannelName(channelName);
        if (!TextUtils.isEmpty(transactionId))
            fuguPeerChatParams.setTransactionId(transactionId);
        fuguPeerChatParams.setLabelId(-1l);
        fuguPeerChatParams.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
        fuguPeerChatParams.setUserId(HippoConfig.getInstance().getUserData().getUserId());
        fuguPeerChatParams.setChatType(chatType);
        if (!TextUtils.isEmpty(userUniqueKey)) {
            fuguPeerChatParams.setUserUniqueKey(userUniqueKey);
        }
        chatType = 0;
        if (otherUserUniqueKeys != null && otherUserUniqueKeys.size() > 0 && !TextUtils.isEmpty(otherUserUniqueKeys.get(0))) {
            fuguPeerChatParams.setOtherUserUniqueKeys(otherUsersArray);
            if (otherUserUniqueKeys != null) {
                //chatType = 2;
                if (otherUserUniqueKeys.size() > 1) {
                    chatType = 1;
                } else {
                    chatType = 1;
                }
            } else {
                chatType = 0;
            }
        }

        fuguPeerChatParams.setChatType(chatType);
        if (message != null && message.length > 0 && !TextUtils.isEmpty(message[0])) {
            fuguPeerChatParams.setUser_first_messages(message);
        }

        if (tags != null && tags.size() > 0) {
            fuguPeerChatParams.setTags(tags);
        }

        try {
            if (customAttributes != null)
                fuguPeerChatParams.setCustomAttributes(customAttributes);

            if (!TextUtils.isEmpty(isSupportTicket) && Integer.parseInt(isSupportTicket) == 1)
                fuguPeerChatParams.setIsSupportTicket(Integer.parseInt(isSupportTicket));

            if (groupingTags != null && groupingTags.size() > 0) {
                fuguPeerChatParams.setGroupingTags(groupingTags);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(fuguPeerChatParams.getEnUserId()) && fuguPeerChatParams.getUserId() == -1) {
            Log.e(TAG, "UserID can't be -1");
            //HippoLog
            return;
        }

        chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS,
                new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));

        activity.startActivity(chatIntent);
        //activity.finish();
    }

    /**
     * Open Support menu
     *
     * @param HippoTicketAttributes
     */

    public void showFAQSupport(HippoTicketAttributes HippoTicketAttributes) {
        showFAQSupport(HippoTicketAttributes, null);
    }

    public void showFAQSupport(HippoTicketAttributes HippoTicketAttributes, Object object) {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        if (object != null) {
            CommonData.saveExtraData(object);
        } else {
            CommonData.clearExtraTicketData();
        }
        if (HippoTicketAttributes != null) {
            openSupportScreen(HippoTicketAttributes.getmFaqName(), HippoTicketAttributes.getmTransactionId());
        } else {
            openSupportScreen(null, null);
        }
        lastClickTime = SystemClock.elapsedRealtime();
    }

    private void openSupportScreen(final String categoryId, final String transactionId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openFuguSupportActivity(categoryId, transactionId);
            }
        }, 100);
    }


    private static void logOutUser(Activity activity) throws Exception {
        if (HippoConfig.getInstance().getUserData() != null && HippoConfig.getInstance().getUserData().getEnUserId() != null) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, HippoConfig.getInstance().getUserData().getEnUserId())
                    .add(APP_VERSION, HippoConfig.getInstance().getVersionName())
                    .add("device_id", UniqueIMEIID.getUniqueIMEIId(activity))
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().logOut(commonParams.getMap())
                    .enqueue(new ResponseResolver<CommonResponse>(activity, false, false) {
                        @Override
                        public void success(CommonResponse commonResponse) {

                        }

                        @Override
                        public void failure(APIError error) {

                        }
                    });
        }
    }

    private void openUserInitForm() {
        Intent intent = new Intent(activity.getApplicationContext(), CustomerInitalActivity.class);
        activity.startActivity(intent);
    }

    private void caseOne(String title) {
        HippoLog.e("Case 1", "case 1");
        if (CommonData.getUpdatedDetails().getData().getCustomerInitialFormInfo() != null) {
            openUserInitForm();
        } else {
            Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setBusinessName(title);
            conversation.setOpenChat(true);
            conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
            conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
            conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            activity.startActivity(chatIntent);
        }
    }

    private void caseElse(String title) {
        HippoLog.e("Case else", "case else");
        if (CommonData.getUpdatedDetails().getData().getCustomerInitialFormInfo() != null) {
            openUserInitForm();
        } else {
            try {
                Intent conversationsIntent = new Intent(activity.getApplicationContext(), ChannelActivity.class);
                conversationsIntent.putExtra("title", title);
                conversationsIntent.putExtra("hasPager", getAttributes().getAdditionalInfo().isHasChannelPager());
                conversationsIntent.putExtra("appVersion", getAppVersion());
                activity.startActivity(conversationsIntent);

                /*if(getAttributes().getConversationalData().isHasPager()) {
                    Intent conversationsIntent = new Intent(activity.getApplicationContext(), ChannelActivity.class);
                    conversationsIntent.putExtra("title", title);
                    conversationsIntent.putExtra("hasPager", getAttributes().getConversationalData().isHasPager());
                    conversationsIntent.putExtra("appVersion", getAppVersion());
                    activity.startActivity(conversationsIntent);
                } else{
                    Intent conversationsIntent = new Intent(activity.getApplicationContext(), FuguChannelsActivity.class);
                    conversationsIntent.putExtra("title", title);
                    conversationsIntent.putExtra("appVersion", getAppVersion());
                    activity.startActivity(conversationsIntent);
                }*/
            } catch (Exception e) {
                Intent conversationsIntent = new Intent(activity.getApplicationContext(), ChannelActivity.class);
                conversationsIntent.putExtra("title", title);
                conversationsIntent.putExtra("hasPager", "false");
                conversationsIntent.putExtra("appVersion", getAppVersion());
                activity.startActivity(conversationsIntent);
                /*Intent conversationsIntent = new Intent(activity.getApplicationContext(), FuguChannelsActivity.class);
                conversationsIntent.putExtra("title", title);
                conversationsIntent.putExtra("appVersion", getAppVersion());
                activity.startActivity(conversationsIntent);*/
            }
        }
    }

    private void openFuguSupportActivity(String faqName, String transactionId) {
        Intent intent = new Intent(activity.getApplicationContext(), HippoSupportActivity.class);
        intent.putExtra(FuguAppConstant.SUPPORT_ID, faqName);
        intent.putExtra(FuguAppConstant.SUPPORT_TRANSACTION_ID, transactionId);
        //intent.putExtra("userData", getUserData());
        activity.startActivity(intent);
    }


    private int getAppVersion() {
        try {
            if (activity != null) {
                return HippoConfig.getInstance().activity.getPackageManager().getPackageInfo(HippoConfig.getInstance()
                        .activity.getPackageName(), 0).versionCode;
            }
            return 205;
        } catch (Exception e) {
            e.printStackTrace();
            return 205;
        }
    }

    public CaptureUserData getUserData() {
        return getUserData(true);
    }

    public CaptureUserData getUserData(boolean fetchSavedData) {
        if (userData == null)
            userData = CommonData.getUserData();

        if (fetchSavedData) {
            try {
                if (TextUtils.isEmpty(userData.getEnUserId())) {
                    FuguPutUserDetailsResponse response = CommonData.getUpdatedDetails();
                    if (response != null && response.getData() != null && !TextUtils.isEmpty(response.getData().getEn_user_id())) {
                        userData.setEnUserId(response.getData().getEn_user_id());
                        userData.setUserId(response.getData().getUserId());
                        userData.setFullName(response.getData().getFullName());
                        userData.setEmail(response.getData().getEmail());
                    } else if (context != null) {
                        userData.setEnUserId(Prefs.with(context).getString("en_user_id", ""));
                        userData.setUserId(Prefs.with(context).getLong("user_id", -1l));
                        userData.setFullName(Prefs.with(context).getString("full_name", ""));
                        userData.setEmail(Prefs.with(context).getString("email", ""));
                    }
                }
            } catch (Exception e) {
                if (HippoConfig.DEBUG)
                    e.printStackTrace();
                try {
                    if (context != null) {
                        userData.setEnUserId(Prefs.with(context).getString("en_user_id", ""));
                        userData.setUserId(Prefs.with(context).getLong("user_id", -1l));
                        userData.setFullName(Prefs.with(context).getString("full_name", ""));
                        userData.setEmail(Prefs.with(context).getString("email", ""));
                    }
                } catch (Exception e1) {

                }
            }
        }

        return userData;
    }

    public void getUnreadCount() {
        getUnreadCount(activity, HippoConfig.getInstance().getUserData().getEnUserId());
    }

    private void getUnreadCount(Activity activity, String enUserId) {
        new UnreadCountApi().getConversations(activity, enUserId);
    }


    public void fetchUnreadCountForRequest(ChatByUniqueIdAttributes attributes, final UnreadCountFor countCallback) {
        String countTransactionId = attributes.getTransactionId();
        ArrayList<String> otherUserUniqueKeys = attributes.getOtherUserUniqueKeys();

        if (TextUtils.isEmpty(countTransactionId)) {
            Toast.makeText(activity, R.string.hippo_empty_transaction_id, Toast.LENGTH_SHORT).show();
            return;
        }

        if (otherUserUniqueKeys == null || otherUserUniqueKeys.size() == 0) {
            Toast.makeText(activity, R.string.hippo_empty_other_user_unique_keys, Toast.LENGTH_SHORT).show();
            return;
        }

        this.countCallback = countCallback;

        Long channelId = P2pUnreadCount.INSTANCE.getChannelId(countTransactionId, otherUserUniqueKeys.get(0));
        if (channelId != null) {
            if (channelId == -2) {
                Log.e("P2pUnreadCount", "P2pUnreadCount no channel found");
            } else {
                int count = P2pUnreadCount.INSTANCE.getChannelCount(channelId);
                if (count > 0)
                    countCallback.unreadCountFor(countTransactionId, count);
                else
                    countCallback.unreadCountFor(countTransactionId, 0);
            }
        } else {
            new UnreadCountApi().getChannelUnreadCount(activity, HippoConfig.getInstance().getUserData().getEnUserId(),
                    countTransactionId, HippoConfig.getInstance().getUserData().getUserUniqueKey(), otherUserUniqueKeys, countCallback);
        }

    }

    private HashMap<String, Long> channelIds = new HashMap<>();

    public void setChannelIds(String transactionId, Long channelId) {
        channelIds.put(transactionId, channelId);
    }

    public Long getChannelId(String transactionId) {
        return channelIds.get(transactionId);
    }

    // For permission

    public int getTargetSDKVersion() {
        return targetSDKVersion;
    }

    private int targetSDKVersion = 0;

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(Context activity, String permission) {

        PackageManager pm = activity.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(HippoConfig.getInstance().activity.getPackageName(), 0);
            if (applicationInfo != null) {
                targetSDKVersion = applicationInfo.targetSdkVersion;
            }
        } catch (Exception e) {

        }

        if (targetSDKVersion > 22) {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return PermissionChecker.checkSelfPermission(activity, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String permission, String explanation, int code) {
        HippoLog.e(TAG, "permissions" + permission);
        return askUserToGrantPermission(activity, new String[]{permission}, explanation, code);
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permissions
     * @param explanation
     * @param requestCode
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String[] permissions, String explanation, int requestCode) {
        String permissionRequired = null;

        for (String permission : permissions)
            if (!isPermissionGranted(activity, permission)) {
                permissionRequired = permission;
                break;
            }

        // Check if the Permission is ALREADY GRANTED
        if (permissionRequired == null) return true;

        // Check if there is a need to show the PERMISSION DIALOG
        boolean explanationRequired = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequired);

        // Convey the EXPLANATION if required
        if (explanationRequired) {

            if (explanation == null) explanation = "Please grant permission";
            Toast.makeText(activity, explanation, Toast.LENGTH_SHORT).show();
        } else {

            // We can request the permission, if no EXPLANATIONS required
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        return false;
    }


    //**************************** For Agent SDK *************************

    //Get unread count
    private UnreadCount callbackListener;

    private boolean openAgentScreen;
    private boolean apiInProgress;
    private String chatTitle = "Chat";
    private Queue<String> objectQueue = new LinkedList();

    private AgentUnreadCountListener countListener;

    public UnreadCount getCallbackListener() {
        return callbackListener;
    }

    public void setCallbackListener(UnreadCount callbackListener) {
        this.callbackListener = callbackListener;
    }

    public AgentUnreadCountListener getAgentCountListener() {
        return countListener;
    }

    public void setCountForCallbackListener(AgentUnreadCountListener countListener) {
        this.countListener = countListener;
    }

    private void registerNetworkListener(Context context) {
        try {
            context.registerReceiver(new FuguNetworkStateReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private CallData callData;
    private FayeCallDate fayeCallDate;
    private HippoLifeCycle lifeCycle;

    public FayeCallDate getFayeCallDate() {
        return fayeCallDate;
    }

    public void setFayeCallDate(FayeCallDate fayeCallDate) {
        this.fayeCallDate = fayeCallDate;
    }

    public CallData getCallData() {
        return callData;
    }

    public void setCallListener(CallData callData) {
        this.callData = callData;
    }

    public void setLifeCyclerListener(HippoLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public HippoLifeCycle getLifeCyclerListener() {
        return lifeCycle;
    }

    private Integer botId = null;

    public void setBotGroupId(Integer botId) {
        CommonData.setBotId(botId);
        this.botId = botId;
    }

    public Integer getBotId() {
        if (botId == null) {
            botId = CommonData.getBotId();
        }
        return botId;
    }

    private Boolean skipBot = null;
    private String skipBotReasion = "";
    private boolean showCreateBtn = false;

    public boolean isShowCreateBtn() {
        return showCreateBtn;
    }

    public void setShowCreateBtn(boolean showCreateBtn) {
        this.showCreateBtn = showCreateBtn;
    }

    public void setSkipBotReasion(String reason) {
        this.skipBotReasion = skipBotReasion;
    }

    public void setSkipBot(boolean skipBot) {
        CommonData.skipBot(skipBot);
    }

    public boolean getSkipBot() {
        skipBot = CommonData.getSkipBot();
        return skipBot;
    }

    public String getSkipBotReason() {
        return skipBotReasion;
    }


    /**
     * @param context             Activity context of the class
     * @param callType            1 for video call, 2 for audio call
     * @param transactionId       unique Id
     * @param otherUserName       End user name
     * @param userUniqueKey       User unique key
     * @param otherUserUniqueKeys Other user unique key
     */
    public void startCall(Context context, String callType, String transactionId, String userUniqueKey, String otherUserName,
                          ArrayList<String> otherUserUniqueKeys, String otheruserImageUrl, String myName, String myImagePath) {
        if (callType.equalsIgnoreCase("video") && !CommonData.getVideoCallStatus()) {
            Toast.makeText(context, "This feature not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        if (callType.equalsIgnoreCase("audio") && !CommonData.getAudioCallStatus()) {
            Toast.makeText(context, "This feature not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(transactionId)) {
            Toast.makeText(context, "TransactionId can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userUniqueKey)) {
            Toast.makeText(context, "User unique key can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(otherUserName)) {
            Toast.makeText(context, "other user name can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (otherUserUniqueKeys == null || otherUserUniqueKeys.size() == 0) {
            Toast.makeText(context, "Otheruser unique key can't be null", Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new GsonBuilder().create();
        JsonArray otherUsersArray = null;
        JsonArray tagsArray = null;


        if (otherUserUniqueKeys != null) {
            otherUsersArray = gson.toJsonTree(otherUserUniqueKeys).getAsJsonArray();
        }

        FuguCreateConversationParams fuguPeerChatParams = new FuguCreateConversationParams(HippoConfig.getInstance().getAppKey()
                , -1l, transactionId, userUniqueKey, otherUsersArray, transactionId, tagsArray,
                HippoConfig.getInstance().getUserData().getEnUserId());

        Long userId = CommonData.getUserDetails().getData().getUserId();
        String fullname = otherUserName;

        if (HippoConfig.getInstance().getCallData() != null) {
            HippoConfig.getInstance().getCallData().onExternalClick(context, callType, userId, fullname,
                    new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class), otheruserImageUrl, myImagePath);
        } else {
            //HippoCallConfig
            Log.e(TAG, "Please call setCallListener before this method");
        }

    }


    public OnApiCallback onApiCallback;

    public OnApiCallback getOnApiCallback() {
        return onApiCallback;
    }

    public void setApiListener(OnApiCallback onApiCallback) {
        this.onApiCallback = onApiCallback;
    }

    private MobileCampaignBuilder campaignBuilder;

    public MobileCampaignBuilder getMobileCampaignBuilder() {
        if (campaignBuilder == null) {
            campaignBuilder = CommonData.getCampaignBuilder();
        }
        return campaignBuilder;
    }

    public void setCampaignBuilder(MobileCampaignBuilder campaignBuilder) {
        this.campaignBuilder = campaignBuilder;
    }

    public void openMobileCampaigns(Activity activity, MobileCampaignBuilder campaignBuilder) {
        this.campaignBuilder = campaignBuilder;
//        if(campaignBuilder != null)
//            CommonData.setMobileCampaignBuilder(campaignBuilder);
        Intent intent = new Intent(activity, CampaignActivity.class);
        activity.startActivity(intent);
    }


    private HashMap<String, Integer> questions = new HashMap<>();
    private HashMap<Integer, String> suggestions = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> mapping = new HashMap<>();

    public HashMap<String, Integer> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<String, Integer> questions) {
        this.questions = questions;
    }

    public HashMap<Integer, String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(HashMap<Integer, String> suggestions) {
        this.suggestions = suggestions;
    }

    public HashMap<Integer, ArrayList<Integer>> getMapping() {
        return mapping;
    }

    public void setMapping(HashMap<Integer, ArrayList<Integer>> mapping) {
        this.mapping = mapping;
    }

    private String agentEmail;
    private boolean isSingleChannelTransactionId;

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public boolean getSingleChannelTransactionId() {
        return isSingleChannelTransactionId;
    }

    public void setSingleChannelTransactionId(boolean singleChannelTransactionId) {
        isSingleChannelTransactionId = singleChannelTransactionId;
    }


    public void isChatScreenBackBtnRequired(boolean flag) {
        CommonData.setNewBackBtn(flag);
    }

    static int count = 0;


    private int getVersion(Activity context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 102;
        }
    }

    private String getBundle(Activity context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.packageName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "com.buddy.customer";
        }
    }


    public void checkAutoUpdate(final Activity activity) {
        count = count + 1;
        if (count > 1)
            return;

        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.APP_SECRET_KEY, hippoConfig.getAppKey())
                .add("bundle_id", getBundle(activity))
                .add("app_type", 1)
                .add(FuguAppConstant.APP_VERSION, getVersion(activity))
                .add("device_id", UniqueIMEIID.getUniqueIMEIId(activity))
                .add(FuguAppConstant.DEVICE_TYPE, 1)
                .add(APP_SOURCE_TYPE, 7)
                .build();

        RestClient.getApiInterface().updateApp(commonParams.getMap()).enqueue(new ResponseResolver<AppUpdateModel>() {
            @Override
            public void success(AppUpdateModel updateModel) {
                CommonData.saveVersionInfo(updateModel);
                Prefs.with(activity).save("force_update_version", false);
                HippoLog.e("version", "version = " + getVersion(activity));

                if (updateModel.getData().getCriticalVersion() > getVersion(activity)) {
                    Prefs.with(activity).save("force_update_version", true);
                    SingleBtnUpdateWindow(activity, updateModel);
                } else if (isAvailable(activity, updateModel)) {
                    showUpdatePopup(activity);
                }
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    private void SingleBtnUpdateWindow(final Activity context, final AppUpdateModel updateModel) {
        try {

            SingleBtnDialog.with(context).setMessage(updateModel.getData().getMessaage())
                    .hideHeading().setCancelableOnTouchOutside(false).setCancelable(false).setCallback(new SingleBtnDialog.OnActionPerformed() {
                @Override
                public void positive() {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateModel.getData().getDownloadLink()));
                    context.startActivity(browserIntent);
                    context.finish();
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAvailable(Activity activity, AppUpdateModel updateModel) {
        int last_displayed_version = Prefs.with(activity).getInt("last_displayed_version", 0);
        long last_update_displayed = Prefs.with(activity).getLong("last_update_displayed", 0);
        boolean diff = System.currentTimeMillis() - last_update_displayed > updateModel.getData().getSoftUpdateRetry() * 60 * 1000;
        if (getVersion(activity) < updateModel.getData().getLatestVersion()) {
            if (last_displayed_version < updateModel.getData().getLatestVersion() || diff) {
                //true
                Prefs.with(activity).save("last_displayed_version", updateModel.getData().getLatestVersion());
                Prefs.with(activity).save("last_update_displayed", System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }


    public void showUpdatePopup(final Context context) {
        AppUpdateModel update = CommonData.getVersionInfo();
        if (update == null)
            return;

        String message = update.getData().getMessaage();
        final String link = update.getData().getDownloadLink();

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(Restring.getString(context, R.string.fugu_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        context.startActivity(browserIntent);
                    }
                })
                .show();

    }

    public void setAppName(String appName) {
        CommonData.setAppName(appName);
    }

    /**
     * Where data load was requested.
     */
    private boolean serviceStarted;
    private int WAIT_TIME = 2000;
    /**
     * Whether {@link #onServiceDestroy()} has been called.
     */
    private boolean closed;

    public void onCheckActity(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HippoLog.v(TAG, "onActivityCreated " + activity.getClass().getSimpleName());
                startHippoService(activity);
            }
        }, WAIT_TIME);
    }

    /**
     * Starts data loading in background if not started yet.
     */
    public void onServiceStarted() {
        if (serviceStarted) {
            return;
        }
        //serviceStarted = true;
        closed = false;
        Log.i(TAG, "onServiceStarted in HippoConfig");

        onInitialized();
    }

    /**
     * Service have been destroyed.
     */
    public void onServiceDestroy() {
        Log.i(TAG, "onServiceDestroy");

        if (closed) {
            Log.i(TAG, "onServiceDestroy closed");
            return;
        }
        onClose();
    }

    private void onInitialized() {
        /*for (OnInitializedListener listener : getManagers(OnInitializedListener.class)) {
            Log.i(TAG, "OnInitializedListener onInitialized " + listener);
            listener.onInitialized();
        }*/
    }

    private void onClose() {
        Log.i(TAG, "onClose1");
        /*for (Object manager : registeredManagers) {
            if (manager instanceof OnCloseListener) {
                ((OnCloseListener) manager).onClose();
            }
        }*/
        closed = true;
        serviceStarted = false;
        Log.i(TAG, "onClose2");
    }

    private synchronized void startHippoService(Activity activity) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                activity.startForegroundService(HippoService.createIntent(activity));
            } else {
                activity.startService(HippoService.createIntent(activity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param directOpen
     * @param labelId
     * @deprecated
     */
    public void setAdditionalInfo(boolean directOpen, Long labelId) {
        CommonData.directScreens(directOpen);
        CommonData.setConstantLabelId(labelId);
    }


    private UnreadCountFor countCallback;

    public void clearCount(Long channelId) {
        if (countCallback != null) {
            String transactionId = P2pUnreadCount.INSTANCE.getTransactionId(channelId);
            if (!TextUtils.isEmpty(transactionId))
                countCallback.unreadCountFor(transactionId, 0);
        }
        P2pUnreadCount.INSTANCE.clearCount(channelId);
    }

    public void updateCount(String transactionId, int countTotal) {
        if (countCallback != null)
            countCallback.unreadCountFor(transactionId, countTotal);
    }

    /**
     * Method used to update the unread count from push if channel not created
     *
     * @param transactionId
     * @param channelId
     */
    public void setUpdatedChannelForCount(JSONObject messageJson) {
        try {
            String transactionId = messageJson.optString("chat_transaction_id", "");
            String muid = messageJson.optString("muid", "");
            long channelId = messageJson.optLong("channel_id", -1);
            if (!TextUtils.isEmpty(transactionId)) {
                if (messageJson.has("user_unique_key")) {
                    JSONArray jsonarray = messageJson.getJSONArray("user_unique_key");
                    String myUniqueId = CommonData.getAttributes().getCaptureUserData().getUserUniqueKey();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        String jsonobject = jsonarray.getString(i);
                        if (!jsonobject.equalsIgnoreCase(myUniqueId) && P2pUnreadCount.INSTANCE.hasTransactionId(transactionId + "_" + jsonobject)) {
                            P2pUnreadCount.INSTANCE.updateChannelId(transactionId, channelId, muid);
                            break;
                        }
                    }
                } else {
                    P2pUnreadCount.INSTANCE.updateChannelId(transactionId, channelId, muid);
                }
            }
        } catch (Exception e) {

        }
    }

    private PrePaymentCallBack paymentCallBack;

    public PrePaymentCallBack getPrePaymentCallBack() {
        return paymentCallBack;
    }

    public void getPaymentMethods(Activity activity, PrePaymentCallBack paymentCallBack) {
        this.paymentCallBack = paymentCallBack;
        try {
            List<AddedPaymentGateway> paymentMethods = CommonData.getPaymentList();
            if (paymentMethods.size() == 0 && CommonData.getUserDetails().getData().isAskPaymentAllowed()) {
                getPaymentMethodsList(activity);
            } else {
                if (paymentCallBack != null)
                    paymentCallBack.onMethodReceived(paymentMethods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean perFetchPaymentPlan = false;

    public boolean isPaymentFeched() {
        return perFetchPaymentPlan;
    }

    public void getPaymentMethods(Activity activity, String appSecretKey, PrePaymentCallBack paymentCallBack) {
        this.paymentCallBack = paymentCallBack;
        try {
            getPaymentMethodsList(activity, appSecretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openPrePaymentView(Activity activity, HippoPrePaymentBuilder prePaymentBuilder) {
        PrePaymentData paymentData = new PrePaymentData();
        paymentData.setApp_secret_key(getAttributes().getAppKey());
        paymentData.setEn_user_id(CommonData.getUserDetails().getData().getEn_user_id());
        paymentData.setUser_id(CommonData.getUserDetails().getData().getUserId());
        paymentData.setOperation_type(1);
        paymentData.setFetch_payment_url(1);
        paymentData.setPayment_gateway_id(prePaymentBuilder.getPaymentGatewayId());
        if (prePaymentBuilder.getPaymentType() != null && prePaymentBuilder.getPaymentType() > 0)
            paymentData.setPaymentType(prePaymentBuilder.getPaymentType());

//        paymentData.setTransaction_id("123123123");

        ArrayList<HippoPayment> payment_items = new ArrayList<>();
        HippoPayment payment = new HippoPayment();
        payment.setId("1");
        payment.setAmount(prePaymentBuilder.getAmount());
        payment.setCurrency(prePaymentBuilder.getCurrency());
        payment.setCurrencySymbol(prePaymentBuilder.getCurrencySymbol());
        payment.setDescription(prePaymentBuilder.getDescription());
        payment.setTitle(prePaymentBuilder.getTitle());
        payment.setTransactionId(prePaymentBuilder.getTransactionId());

        payment_items.add(payment);
        paymentData.setPayment_items(payment_items);

        Intent intent = new Intent(activity, PrePaymentActivity.class);
        intent.putExtra("data", new Gson().toJson(paymentData));
        activity.startActivity(intent);
    }

    private void getPaymentMethodsList(Activity activity) {
        GetPaymentGateway.INSTANCE.getPaymentGatewaysList(activity, new OnPaymentListListener() {
            @Override
            public void onSuccessListener() {
                List<AddedPaymentGateway> paymentMethods = CommonData.getPaymentList();
                if (paymentCallBack != null)
                    paymentCallBack.onMethodReceived(paymentMethods);
            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    private void getPaymentMethodsList(Activity activity, String appSecretKey) {
        GetPaymentGateway.INSTANCE.getPaymentGatewaysList(activity, appSecretKey, new OnPaymentListListener() {
            @Override
            public void onSuccessListener() {
                List<AddedPaymentGateway> paymentMethods = CommonData.getPaymentList();
                if (paymentCallBack != null)
                    paymentCallBack.onMethodReceived(paymentMethods);

                perFetchPaymentPlan = true;
            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    public String getCurrencySymbol(String currency) {
        switch (currency) {
            case "USD":
                return "$";
            case "EUR":
                return "‎€";
            case "JPY":
                return "¥‎";
            case "GBP":
                return "£";
            case "AUD":
                return "$";
            case "CAD":
                return "C$";
            case "CHF":
                return "Fr.";
            case "CNY":
                return "¥";
            case "SEK":
                return "kr";
            case "MXN":
                return "Mex$";
            case "NZD":
                return "NZ$";
            case "SGD":
                return "S$";
            case "HKD":
                return "HK$";
            case "NOK":
                return "kr";
            case "KRW":
                return "₩";
            case "INR":
                return "₹";
            case "RUB":
                return "₽";
            case "ZAR":
                return "R";
            case "KES":
                return "KSh";
            case "ZMW":
                return "ZK";
            case "AED":
                return "AED";
            case "EGP":
                return "E£";
            case "PEN":
                return "S";
            case "UGX":
                return "UGX";
            case "IQD":
                return "ع.د";
            case "QAR":
                return "﷼";
            case "COP":
                return "$";
            case "NGN":
                return "₦";
            case "MYR":
                return "RM";
            case "RES":
                return "re";
            default:
                return "$";
        }
    }

    private ArrayList<String> tags;

    public void setTags(ArrayList<String> tags) {
        tags = new ArrayList<>();
        this.tags = tags;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    private String currentLanguage;

    public String getCurrentLanguage() {
        currentLanguage = CommonData.getCurrentLanguage();
        return currentLanguage;
    }

    public void updateLanguage() {
        updateLanguage(getCurrentLanguage());
    }

    public void updateLanguage(String languageCode) {
        if (CommonData.getUserDetails() == null
                || CommonData.getUserDetails().getData() == null
                || TextUtils.isEmpty(CommonData.getUserDetails().getData().getEn_user_id())) {
            return;
        }
        String lang = "";
        try {
            for (BusinessLanguages languages : CommonData.getUserDetails().getData().getBusinessLanguages()) {
                if (languages.getLangCode().equalsIgnoreCase(languageCode)) {
                    lang = languageCode;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "This language code not found");
            return;
        }
        if (TextUtils.isEmpty(lang)) {
            Log.e("TAG", "This language code not found");
            return;
        }

        String getCurrentLang = CommonData.getCurrentLanguage();
        HippoLog.w("Lang", getCurrentLang + " -> currentLang, Requerd Lang -> " + languageCode);
        if (!getCurrentLang.equalsIgnoreCase(languageCode)) {
            currentLanguage = languageCode;
            CommonData.saveCurrentLang(currentLanguage);
            setLanguage(currentLanguage);
            ConnectionManager.INSTANCE.updateLanguage(currentLanguage);
        }
    }

    public String getBusinessLanguage() {
        try {
            if (CommonData.getUserDetails().getData().getBusinessLanguages() != null)
                return new Gson().toJson(CommonData.getUserDetails().getData().getBusinessLanguages());
        } catch (Exception e) {

        }
        return "";
    }

    private void setLanguage(String language) {
        LanguageManager.INSTANCE.updateLanguage(language);
    }

    // for Group call

    /*public void demo(Context context) {
        //{"statusCode":200,"message":"Data Fetched Successfully","data":{"status":0,"channel_id":8371636,"room_title":"s7","channel_data":{"call_type":"VIDEO","room_title":"s7","room_unique_id":"oeTSFWJ4Ez","session_end_time":"2020-08-10T12:30:00.000Z","session_start_time":"2020-08-10T11:00:00.000Z"}}}
        HippoConfig.getInstance().getCallData().openDirectLink(context, "oeTSFWJ4Ez",
                "test", "VIDEO", "", 8371636l, "12345678");
    }*/

    public void joinGroupCall(final Context activity, final String transactionId, final String imagePath) {
        // TODO: 2020-07-20   show connecting loader
        SessionHandler.INSTANCE.startSession(transactionId, new OnStartSessionListener() {
            @Override
            public void onStartListener(@NotNull GroupCallResponse t) {
                if (HippoConfig.getInstance().getCallData() != null) {
                    String userName = "";
                    try {
                        userName = CommonData.getUserData().getFullName();
                    } catch (Exception e) {

                    }
                    HippoConfig.getInstance().getCallData().openDirectLink(activity, t.getData().getCustomData().getRoomUniqueId(),
                            userName, t.getData().getCustomData().getChatType(), imagePath, t.getData().getChannelId(), transactionId,
                            t.getData().getCustomData().getIsAudioEnabled(),
                            t.getData().getCustomData().getIsVideoEnabled());

                    if (HippoConfig.getInstance().getGroupSessionListener() != null)
                        HippoConfig.getInstance().getGroupSessionListener().onJoiningSession(transactionId);

                    /*try {
                        GroupCallData groupCallData = new GroupCallData();
                        groupCallData.setTransactionId(transactionId);
                        groupCallData.setChannelId(t.getData().getChannelId());
                        int callType = 1;
                        //t.getData().getCustomData().getChatType()
                        groupCallData.setCallType(callType);
                        groupCallData.setMuid("muid");
                        groupCallData.setRoomTitle(t.getData().getRoomTitle());
                        groupCallData.setRoomUniqueId(t.getData().getCustomData().getRoomUniqueId());
                        CommonData.saveGroupCall(transactionId, groupCallData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onErrorListener(String error) {
                // TODO: 2020-07-20 show error to customer
                if (HippoConfig.getInstance().getGroupSessionListener() != null)
                    HippoConfig.getInstance().getGroupSessionListener().onErrorInSession(error);
            }
        });
    }

    private void leaveSession(final Activity activity, final String transactionId) {
        if (HippoConfig.getInstance().getCallData() != null) {
            HippoConfig.getInstance().getCallData().leaveGroupCall(activity, transactionId);
        }
    }

    private OnGroupSessionListener sessionListener;

    public void updateGroupSession(OnGroupSessionListener sessionListener) {
        this.sessionListener = sessionListener;
    }

    public OnGroupSessionListener getGroupSessionListener() {
        return sessionListener;
    }

    public void openDevicePermission(Context context) {
        XiaomiUtilities.checkForDevicePermission(context);
        //XiaomiUtilities.checkForDevicePermission(context);

//        if(Build.VERSION.SDK_INT>=19 && XiaomiUtilities.isMIUI() &&
//                !XiaomiUtilities.isCustomPermissionGranted(context, XiaomiUtilities.OP_SHOW_WHEN_LOCKED)){
//
//        }
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
//        intent.putExtra("extra_pkgname", context.getPackageName());
//        context.startActivity(intent);


//        Intent rIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName() );
//        PendingIntent intent = PendingIntent.getActivity(context, 0, rIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.set(AlarmManager.RTC, System.currentTimeMillis(), intent);
//        System.exit(2);

        //PermissionHandler.INSTANCE.addAutoStartupswitch(context);
    }

    public int getUnreadAnnouncementsCount() {
        return CommonData.getAnnouncementCount().size();
    }

    public void openRazorpaySDK(Activity activity, RazorPayData razorpayObj) {
        if (razorpayObj == null) {
            return;
        }
        CommonData.saveData(FuguAppConstant.SP_RZP_ORDER_ID, razorpayObj.getOrderId());
        CommonData.saveData(FuguAppConstant.SP_RZP_AUTH_ORDER_ID, razorpayObj.getAuthOrderId());
        CommonData.saveData(FuguAppConstant.SP_RZP_NEGATIVE_BALANCE_SETTLE, 0);

        Intent intent = new Intent(activity, RazorPayment.class);
        intent.putExtra("razorpayObj", razorpayObj);
        activity.startActivity(intent);
//        RazorPayment.INSTANCE.setPurchaseSubscriptionResponse(razorpayObj.getReferenceId(), razorpayObj.getAuthOrderId(), 0);
//        RazorPayment.INSTANCE.startRazorPayPayment(activity, razorpayObj, false);
    }

    private boolean hideBackBtn = false;

    public boolean isHideBackBtn() {
        return hideBackBtn;
    }

    public void setHideBackBtn(boolean hideBackBtn) {
        this.hideBackBtn = hideBackBtn;
    }

}

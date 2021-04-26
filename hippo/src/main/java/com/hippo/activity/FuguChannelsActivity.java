//package com.hippo.activity;
//
//import android.annotation.SuppressLint;
//import android.app.NotificationManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import androidx.annotation.NonNull;
//import androidx.core.content.PermissionChecker;
//import androidx.appcompat.app.AlertDialog;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.Toolbar;
//import android.text.Html;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.style.ForegroundColorSpan;
//import android.text.style.RelativeSizeSpan;
//import android.text.style.StyleSpan;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.hippo.GroupingTag;
//import com.hippo.*;
//import com.hippo.adapter.HippoChannelsAdapter;
//import com.hippo.apis.ApiGetConversation;
//import com.hippo.constant.FuguAppConstant;
//import com.hippo.database.CommonData;
//import com.hippo.model.FuguConversation;
//import com.hippo.model.FuguDeviceDetails;
//import com.hippo.model.FuguGetConversationsResponse;
//import com.hippo.model.FuguPutUserDetailsResponse;
//import com.hippo.model.UnreadCountModel;
//import com.hippo.retrofit.APIError;
//import com.hippo.retrofit.CommonParams;
//import com.hippo.retrofit.ResponseResolver;
//import com.hippo.retrofit.RestClient;
//import com.hippo.utils.HippoLog;
//import com.hippo.utils.StringUtil;
//import com.hippo.utils.UniqueIMEIID;
//import com.hippo.utils.fileUpload.Prefs;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by rajatdhamija  14/12/17.
// */
//
//public class FuguChannelsActivity extends FuguBaseActivity implements SwipeRefreshLayout.OnRefreshListener, Animation.AnimationListener {
//
//    private final String TAG = FuguChannelsActivity.class.getSimpleName();
//    private static final int NOT_CONNECTED = 0;
//    private static final int CONNECTED_TO_INTERNET = 1;
//    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
//    private RelativeLayout rlRoot;
//    private SwipeRefreshLayout swipeRefresh;
//    private RecyclerView rvChannels;
//    private TextView tvNoInternet, tvNewConversation;
//    private final int READ_PHONE_PERMISSION = 101;
//
//    private String label = "";
//    private Long userId = -1L;
//    private String enUserId = "";
//    private String userName = "Anonymous";
//    private String businessName = "Anonymous";
//    private int appVersion = 0;
//
//    private final int IS_HIT_REQUIRED = 200;
//    public static boolean isRefresh = false;
//    public static Long readChannelId = -1L;
//    public static Long readLabelId = -1L;
//    private TextView tvPoweredBy;
//    private HippoColorConfig hippoColorConfig;
//    @SuppressLint("StaticFieldLeak")
//    private static LinearLayout llInternet;
//    @SuppressLint("StaticFieldLeak")
//    private static TextView tvStatus;
//    private boolean isScreenOpen = false;
//    private boolean isFirstTimeOpen = true;
//    private boolean isFromHistory = false;
//    private Long openedChannelId = -1L;
//
//    private ImageView llNoConversation;
//    private TextView noConversationTextView;
//    private Button createBtn;
//
//
//    private HippoChannelsAdapter hippoChannelsAdapter;
//    private LinearLayoutManager layoutManager;
//    private int pastVisiblesItems, visibleItemCount, totalItemCount;
//    private boolean isPagingApiInProgress;
//    private HippoChannelsAdapter.ProgressBarItem progressBarItem;
//    private ArrayList<Object> fuguConversationList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fugu_activity_channels);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NOTIFICATION_INTENT));
//        initViews();
//        decideAppFlow();
//        HippoConfig.getInstance().setChannelActivity(true);
//    }
//
//    /**
//     * Initialize Views
//     */
//    private void initViews() {
//        isFromHistory = getIntent().getBooleanExtra("from_history", false);
//        openedChannelId = getIntent().getLongExtra("channelId", -1);
//        hippoColorConfig = CommonData.getColorConfig();
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        String title = getIntent().getStringExtra("title");
//        if (TextUtils.isEmpty(title))
//            title = CommonData.getChatTitle(this);
//        setToolbar(myToolbar, title);
//
//        appVersion = getIntent().getIntExtra("appVersion", 0);
//        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(this);
//        rvChannels = (RecyclerView) findViewById(R.id.rvChannels);
//        createBtn = findViewById(R.id.createBtn);
//        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
//        tvNewConversation = (TextView) findViewById(R.id.tvNewConversation);
//        tvPoweredBy = (TextView) findViewById(R.id.tvPoweredBy);
//        tvStatus = (TextView) findViewById(R.id.tvStatus);
//        llInternet = (LinearLayout) findViewById(R.id.llInternet);
//        llNoConversation = findViewById(R.id.llNoConversation);
//        noConversationTextView = findViewById(R.id.noConversationTextView);
//        configColors();
//        if (!isNetworkAvailable()) {
//            llInternet.setVisibility(View.VISIBLE);
//            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
//            tvStatus.setText(R.string.fugu_not_connected_to_internet);
//        }
//
//        createBtn.setText(getString(R.string.talk_to));
//        //rvChannels.addOnScrollListener(new HidingScrollListene);
//        rvChannels.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if ((dy > 0 || dy < 0) && createBtn.getVisibility() == View.VISIBLE) {
//                    createBtn.setVisibility(View.GONE);
////                    viewAnimate(true);
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
////                    viewAnimate(false);
//                    createBtn.setVisibility(View.VISIBLE);
//                }
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
//
//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createConversation();
//            }
//        });
//    }
//
//    /**
//     * Decide app flow on basis of user data and permisions
//     */
//    private void decideAppFlow() {
//        if (CommonData.getUserDetails() != null && (CommonData.getConversationList().size() > 0) || isFromHistory
//            || CommonData.getUpdatedDetails().getData().isMultiChannelLabelMapping()) {
//            setUpUI();
//            getConversations(true);
//        } else {
//            sendUserDetails();
//        }
//    }
//
//    private void setApiHit() {
//        if (CommonData.getUserDetails() != null && (CommonData.getConversationList().size() > 0) || isFromHistory) {
//            //setUpUI();
//            getConversations();
//        } else {
//            sendUserDetails();
//        }
//    }
//
//    /**
//     * Set up application UI
//     */
//    private void setUpUI() {
//        tvNoInternet.setVisibility(View.GONE);
//        swipeRefresh.setVisibility(View.VISIBLE);
//        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
//        label = userData.getBusinessName();
//        businessName = userData.getBusinessName();
//        userId = userData.getUserId();
//        enUserId = userData.getEn_user_id();
//        if (!TextUtils.isEmpty(userData.getFullName()))
//            userName = userData.getFullName();
//        fuguConversationList.clear();
//
//        try {
//            if (CommonData.getConversationList().size() > 0) {
//            //if (userData.getFuguConversations().size() > 0) {
//                fuguConversationList.addAll(CommonData.getConversationList());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setRecyclerViewData();
//        setPoweredByText(userData);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onRefresh() {
//        getConversations(false);
//    }
//
//    @Override
//    public void onAnimationStart(Animation animation) {
//
//    }
//
//    @Override
//    public void onAnimationEnd(Animation animation) {
//
//    }
//
//    @Override
//    public void onAnimationRepeat(Animation animation) {
//
//    }
//
//    @Override
//    protected void onResume() {
//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (nm != null) {
//            nm.cancelAll();
//        }
//        super.onResume();
//        isScreenOpen = true;
//        if (isRefresh) {
//            isRefresh = false;
//            try {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    if (fuguConversationList.get(i) instanceof FuguConversation) {
//                        FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(i);
//                        if (readChannelId > -1 && currentConversation.getChannelId() > -1 && currentConversation.getChannelId().compareTo(readChannelId) == 0) {
//                            currentConversation.setUnreadCount(0);
//                            if (hippoChannelsAdapter != null)
//                                hippoChannelsAdapter.notifyDataSetChanged();
//                            break;
//                        } else if (readLabelId > -1 && currentConversation.getLabelId() > -1 && currentConversation.getLabelId().compareTo(readLabelId) == 0) {
//                            currentConversation.setUnreadCount(0);
//                            if (hippoChannelsAdapter != null)
//                                hippoChannelsAdapter.notifyDataSetChanged();
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            updateCount(fuguConversationList);
//        }
//        if (!isFirstTimeOpen) {
//            setApiHit();
//        }
//        isFirstTimeOpen = false;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        isScreenOpen = false;
//        isFirstTimeOpen = false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        HippoLog.d(TAG, "Permission callback called-------" + requestCode);
//        switch (requestCode) {
//            case READ_PHONE_PERMISSION: {
//                if (HippoConfig.getInstance().getTargetSDKVersion() > 22 &&
//                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    sendUserDetails();
//                } else if (HippoConfig.getInstance().getTargetSDKVersion() <= 22 &&
//                        grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
//                    sendUserDetails();
//                } else {
//                    //ActivityCompat.shouldShowRequestPermissionRationale(FuguFuguChannelsActivity.this, Manifest.permission.READ_PHONE_STATE);
//                    Toast.makeText(FuguChannelsActivity.this, "Go to Settings and grant permission to access phone state", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
//        }
//    }
//
//    /**
//     * Config Colors of App
//     */
//    private void configColors() {
//        rlRoot.setBackgroundColor(hippoColorConfig.getHippoChannelBg());
//        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            tvNewConversation.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
//        } else {
//            tvNewConversation.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
//        }
//        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
//        swipeRefresh.setColorSchemeColors(hippoColorConfig.getHippoThemeColorPrimary());
//        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorSecondary());
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        HippoConfig.getInstance().setChannelActivity(false);
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
//        readChannelId = null;
//        readLabelId = null;
//    }
//
//    /**
//     * onClick functionality
//     *
//     * @param v view clicked
//     */
//    public void onClick(View v) {
//
//        if (v.getId() == R.id.tvNoInternet) {
//            if (CommonData.getUserDetails() != null) {
//                getConversations();
//            } else {
//                sendUserDetails();
//            }
//        } else if (v.getId() == R.id.tvNewConversation) {
//            Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
//            FuguConversation conversation = new FuguConversation();
//            conversation.setUserId(userId);
//            conversation.setLabel(label);
//            conversation.setUserName(userName);
//            conversation.setStatus(STATUS_CHANNEL_OPEN);
//            conversation.setEnUserId(enUserId);
//            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//            startActivityForResult(chatIntent, IS_HIT_REQUIRED);
//        } else if (v.getId() == R.id.tvPoweredBy) {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(FUGU_WEBSITE_URL));
//            startActivity(i);
//        }
//    }
//
//    /**
//     * Broadcast receiver to handle push messages on channels screen
//     */
//    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            try {
//                if (!isScreenOpen)
//                    return;
//
//                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));
//
//                HippoLog.d("receiver", "Got message: " + messageJson.toString());
//
//                boolean hasChannelID = false;
//                boolean hasLabelID = false;
//
//                if (messageJson.has(NOTIFICATION_TYPE) && messageJson.getInt(NOTIFICATION_TYPE) == 5) {
//                    getConversations();
//                } else {
//                    if (messageJson.has(CHANNEL_ID) && messageJson.getLong(CHANNEL_ID) > 0) {
//                        int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
//                        if (index != -1)
//                            hasChannelID = true;
//                    }
//
//                    if (messageJson.has(LABEL_ID) && messageJson.getLong(LABEL_ID) > 0) {
//                        for (int i = 0; i < fuguConversationList.size(); i++) {
//                            if (fuguConversationList.get(i) instanceof FuguConversation) {
//                                FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(i);
//                                if (currentConversation.getLabelId() == messageJson.getLong(LABEL_ID)) {
//                                    hasLabelID = true;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    if ((!hasChannelID && !hasLabelID)) {
//                        getConversations();
//                    } else {
//                        if (messageJson.has(NEW_MESSAGE) && messageJson.has(CHANNEL_ID)) {
//                            int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
//                            if (index > -1) {
//                                if(fuguConversationList.get(index) instanceof FuguConversation) {
//                                    FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(index);
//                                    currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
//                                    if (messageJson.has(NEW_MESSAGE)) {
//                                        currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
//                                    }
//                                    if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
//                                        currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
//                                    } else {
//                                        currentConversation.setUnreadCount(0);
//                                    }
//                                    currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
//                                    currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
//                                    if (hippoChannelsAdapter != null)
//                                        hippoChannelsAdapter.notifyDataSetChanged();
//
//                                    updateCount(fuguConversationList);
//                                }
//                            } else {
//                                getConversations();
//                            }
//                        } else if (messageJson.has(NEW_MESSAGE) && messageJson.has(LABEL_ID)) {
//                            int index = -1;
//                            for (int i = 0; i < fuguConversationList.size(); i++) {
//                                if(fuguConversationList.get(i) instanceof FuguConversation) {
//                                    FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(i);
//                                    if (currentConversation.getLabelId().compareTo(messageJson.getLong(LABEL_ID)) == 0) {
//                                        index = i;
//                                        break;
//                                    }
//                                }
//                            }
//                            if (index > -1) {
//                                if(fuguConversationList.get(index) instanceof FuguConversation) {
//                                    FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(index);
//                                    currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
//                                    if (messageJson.has(NEW_MESSAGE)) {
//                                        currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
//                                    }
//                                    if (HippoNotificationConfig.pushLabelId.compareTo(messageJson.getLong(LABEL_ID)) != 0) {
//                                        currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
//                                    } else {
//                                        currentConversation.setUnreadCount(0);
//                                    }
//                                    currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
//                                    currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
//
//                                    if (hippoChannelsAdapter != null)
//                                        hippoChannelsAdapter.notifyDataSetChanged();
//
//
//                                    updateCount(fuguConversationList);
//                                }
//
//                            } else {
//                                getConversations();
//                            }
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    /**
//     * Send user details to server
//     */
//    private void sendUserDetails() {
//        if (isNetworkAvailable()) {
//            Gson gson = new GsonBuilder().create();
//            JsonObject deviceDetailsJson = null;
//            try {
//                deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(appVersion).getDeviceDetails()).getAsJsonObject();
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            CaptureUserData userData = null;//getIntent().getParcelableExtra("userData");
//
//            if (userData == null) {
//                userData = HippoConfig.getInstance().getUserData();
//            }
//
//            HashMap<String, Object> commonParams = new HashMap<>();
//            HippoConfig.getInstance();
//            if (HippoConfig.getmResellerToken() != null) {
//                commonParams.put(RESELLER_TOKEN, HippoConfig.getmResellerToken());
//                commonParams.put(REFERENCE_ID, String.valueOf(HippoConfig.getmReferenceId()));
//            } else {
//                commonParams.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
//            }
//            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(FuguChannelsActivity.this));
//            commonParams.put(APP_TYPE, HippoConfig.getInstance().getAppType());
//            commonParams.put(DEVICE_TYPE, ANDROID_USER);
//            commonParams.put(APP_VERSION, BuildConfig.VERSION_NAME);
//            commonParams.put(DEVICE_DETAILS, deviceDetailsJson);
//
//            if (userData != null) {
//                if (!TextUtils.isEmpty(userData.getUserUniqueKey()))
//                    commonParams.put(USER_UNIQUE_KEY, userData.getUserUniqueKey().trim());
//
//                if (!TextUtils.isEmpty(userData.getFullName()))
//                    commonParams.put(FULL_NAME, userData.getFullName().trim());
//
//                if (!TextUtils.isEmpty(userData.getEmail()))
//                    commonParams.put(EMAIL, userData.getEmail().trim());
//
//                if (!TextUtils.isEmpty(userData.getPhoneNumber())) {
//                    final String contact = (userData.getPhoneNumber()).replaceAll("[^\\d.]", "");
//                    /*if (!Utils.isValidPhoneNumber(contact)) {
//                        ToastUtil.getInstance(FuguChannelsActivity.this).showToast("Invalid phone number");
//                        return;
//                    }*/
//                    commonParams.put(PHONE_NUMBER, contact);
//                }
//
//                if (!TextUtils.isEmpty(CommonData.getImagePath()))
//                    commonParams.put(HIPPO_USER_IMAGE_PATH, CommonData.getImagePath());
//
//                if (!userData.getTags().isEmpty()) {
//                    ArrayList<GroupingTag> groupingTags = new ArrayList<>();
//                    for (GroupingTag tag : userData.getTags()) {
//                        GroupingTag groupingTag = new GroupingTag();
//                        if (!TextUtils.isEmpty(tag.getTagName()))
//                            groupingTag.setTagName(tag.getTagName());
//                        if (tag.getTeamId() != null)
//                            groupingTag.setTeamId(tag.getTeamId());
//
//                        if (!TextUtils.isEmpty(tag.getTagName()) || tag.getTeamId() != null) {
//                            groupingTags.add(groupingTag);
//                        }
//                    }
//                    commonParams.put(GROUPING_TAGS, new Gson().toJson(groupingTags));
//                } else {
//                    commonParams.put(GROUPING_TAGS, "[]");
//                }
//            }
//
//            String deviceToken = CommonData.getAttributes().getDeviceToken();
//            if (!TextUtils.isEmpty(deviceToken))
//                commonParams.put(DEVICE_TOKEN, deviceToken);
//            if (userData != null && !userData.getCustom_attributes().isEmpty()) {
//                commonParams.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
//            }
//
//            HippoLog.e(TAG + "sendUserDetails map", "==" + commonParams.toString());
//            if (!TextUtils.isEmpty(HippoConfig.getmResellerToken())) {
//                apiPutUserDetailReseller(commonParams);
//            } else {
//                apiPutUserDetail(commonParams);
//            }
//        } else {
//            tvNoInternet.setVisibility(View.VISIBLE);
//            swipeRefresh.setVisibility(View.GONE);
//            //tvNewConversation.setVisibility(View.GONE);
//            tvNewConversation.setVisibility(HippoConfig.getInstance().isShowCreateBtn() ? View.VISIBLE : View.GONE);
//        }
//    }
//
//    /**
//     * APi to send user details
//     *
//     * @param commonParams params to be sent
//     */
//    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
//        try {
//            if(HippoConfig.getInstance().getOnApiCallback() != null) {
//                HippoConfig.getInstance().getOnApiCallback().onProcessing();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        CommonParams params = new CommonParams.Builder()
//                .putMap(commonParams)
//                .build();
//        RestClient.getApiInterface().putUserDetails(params.getMap())
//                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivity.this, true, false) {
//                    @Override
//                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
//                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
//                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
//                        try {
//                            Prefs.with(FuguChannelsActivity.this).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
//                            Prefs.with(FuguChannelsActivity.this).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
//                            Prefs.with(FuguChannelsActivity.this).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
//                            Prefs.with(FuguChannelsActivity.this).save("email", fuguPutUserDetailsResponse.getData().getEmail());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            if (HippoConfig.getInstance().getOnApiCallback() != null) {
//                                HippoConfig.getInstance().getOnApiCallback().onSucess();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        setUpUI();
//
//                        try {
//                            if(fuguPutUserDetailsResponse.getData().getFuguConversations().size() == 0) {
//                                Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
//                                FuguConversation conversation = new FuguConversation();
//                                conversation.setBusinessName(CommonData.getChatTitle(FuguChannelsActivity.this));
//                                conversation.setOpenChat(true);
//                                conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
//                                conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
//                                conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
//                                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//                                startActivity(chatIntent);
//                                finish();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failure(APIError error) {
//                        try {
//
//                            /*try {
//                                if(BuildConfig.DEBUG) {
//                                    View view = getWindow().getDecorView().findViewById(android.R.id.content);
//                                    Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//
//                            }*/
//                            if(HippoConfig.getInstance().getOnApiCallback() != null) {
//                                HippoConfig.getInstance().getOnApiCallback().onFailure(error.getMessage());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
//                            Toast.makeText(FuguChannelsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            tvNoInternet.setVisibility(View.VISIBLE);
//                            swipeRefresh.setVisibility(View.GONE);
//                            tvNewConversation.setVisibility(View.GONE);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * APi to send user details for reseller
//     *
//     * @param commonParams params to be sent
//     */
//    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
//        try {
//            if(HippoConfig.getInstance().getOnApiCallback() != null) {
//                HippoConfig.getInstance().getOnApiCallback().onProcessing();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        CommonParams params = new CommonParams.Builder()
//                .putMap(commonParams)
//                .build();
//        RestClient.getApiInterface().putUserDetailsReseller(params.getMap())
//                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivity.this, true, false) {
//                    @Override
//                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
//                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
//                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
//                        try {
//                            Prefs.with(FuguChannelsActivity.this).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
//                            Prefs.with(FuguChannelsActivity.this).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
//                            Prefs.with(FuguChannelsActivity.this).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
//                            Prefs.with(FuguChannelsActivity.this).save("email", fuguPutUserDetailsResponse.getData().getEmail());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            if (HippoConfig.getInstance().getOnApiCallback() != null) {
//                                HippoConfig.getInstance().getOnApiCallback().onSucess();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        setUpUI();
//
//                        try {
//                            if(fuguPutUserDetailsResponse.getData().getFuguConversations().size() == 0) {
//                                Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
//                                FuguConversation conversation = new FuguConversation();
//                                conversation.setBusinessName(CommonData.getChatTitle(FuguChannelsActivity.this));
//                                conversation.setOpenChat(true);
//                                conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
//                                conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
//                                conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
//                                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//                                startActivity(chatIntent);
//                                finish();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failure(APIError error) {
//                        /*try {
//                            if(BuildConfig.DEBUG) {
//                                View view = getWindow().getDecorView().findViewById(android.R.id.content);
//                                Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }*/
//                        try {
//                            if (HippoConfig.getInstance().getOnApiCallback() != null) {
//                                HippoConfig.getInstance().getOnApiCallback().onFailure(error.getMessage());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
//                            Toast.makeText(FuguChannelsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            tvNoInternet.setVisibility(View.VISIBLE);
//                            swipeRefresh.setVisibility(View.GONE);
//                            tvNewConversation.setVisibility(View.GONE);
//
//                        }
//                    }
//                });
//    }
//
//    /**
//     * Get conversations api hit
//     */
//    private void getConversations() {
//        getConversations(isFromHistory);
////        if(isFromHistory) {
////            getHistory();
////        } else {
////            getConversations(false);
////        }
//
//    }
//
//    private void getConversations(boolean showLoader) {
//        if (isNetworkAvailable()) {
//
//            new ApiGetConversation(this, new ApiGetConversation.CallbackListener() {
//                @Override
//                public void onSuccess(FuguGetConversationsResponse fuguGetConversationsResponse) {
//                    try {
//
//                        for (int i = 0; i < fuguGetConversationsResponse.getData().getFuguConversationList().size(); i++) {
//                            String removeLt = fuguGetConversationsResponse.getData().getFuguConversationList().get(i).getMessage().replaceAll("<", "&lt;");
//                            String removeGt = removeLt.replaceAll(">", "&gt;");
//                            fuguGetConversationsResponse.getData().getFuguConversationList().get(i).setMessage(removeGt);
//                        }
//
//                        CommonData.setConversationList(fuguGetConversationsResponse.getData().getFuguConversationList());
//
//                        fuguConversationList.clear();
//                        fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
//                        updateCount(fuguConversationList);
//
//                        if (hippoChannelsAdapter != null)
//                            hippoChannelsAdapter.notifyDataSetChanged();
//                        swipeRefresh.setRefreshing(false);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure() {
//                    swipeRefresh.setRefreshing(false);
//                }
//            }).getConversation(enUserId, 1, showLoader, false);
//        } else {
//            swipeRefresh.setRefreshing(false);
//            // Toast.makeText(FuguFuguChannelsActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == IS_HIT_REQUIRED && resultCode == RESULT_OK) {
//            // Make sure the request was successful
//            //getConversations();
//
//            FuguConversation conversation =
//                    new Gson().fromJson(data.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);
//
//            if (conversation != null && conversation.getLabelId().compareTo(-1L) != 0) {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    if (fuguConversationList.get(i) instanceof FuguConversation) {
//                        FuguConversation fuguConversation = (FuguConversation) fuguConversationList.get(i);
//                        if(fuguConversation.getLabelId().compareTo(conversation.getLabelId()) == 0) {
//                            fuguConversation.setChannelId(conversation.getChannelId());
//                            fuguConversation.setMessage(conversation.getDefaultMessage());
//                            fuguConversation.setDateTime(conversation.getDateTime());
//                            fuguConversation.setChannelStatus(conversation.getChannelStatus());
//                            fuguConversation.setIsTimeSet(1);
//                            fuguConversation.setLast_sent_by_id(conversation.getLast_sent_by_id());
//                            fuguConversation.setUserId(conversation.getLast_sent_by_id());
//                            fuguConversation.setEnUserId(conversation.getEnUserId());
//                            fuguConversation.setLast_message_status(conversation.getLast_message_status());
//                            fuguConversation.setChatType(conversation.getChatType());
////                            if (fuguChannelsAdapter != null)
////                                fuguChannelsAdapter.updateList(fuguConversationList);
//                            if (hippoChannelsAdapter != null)
//                                hippoChannelsAdapter.updateList(fuguConversationList);
//
//                            updateCount(fuguConversationList);
//                            break;
//                        }
//                    }
//                }
//            } else if (conversation != null && conversation.getLabelId().compareTo(-1L) == 0) {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    if (fuguConversationList.get(i) instanceof FuguConversation) {
//                        FuguConversation fuguConversation = (FuguConversation) fuguConversationList.get(i);
//                        if (fuguConversation.getChannelId().compareTo(conversation.getChannelId()) == 0) {
//                            fuguConversation.setChannelId(conversation.getChannelId());
//                            fuguConversation.setMessage(conversation.getDefaultMessage());
//                            fuguConversation.setDateTime(conversation.getDateTime());
//                            fuguConversation.setChannelStatus(conversation.getChannelStatus());
//                            fuguConversation.setIsTimeSet(1);
//                            fuguConversation.setLast_sent_by_id(conversation.getLast_sent_by_id());
//                            fuguConversation.setLast_message_status(conversation.getLast_message_status());
//                            fuguConversation.setChatType(conversation.getChatType());
////                            if (fuguChannelsAdapter != null)
////                                fuguChannelsAdapter.updateList(fuguConversationList);
//                            if (hippoChannelsAdapter != null)
//                                hippoChannelsAdapter.updateList(fuguConversationList);
//                            updateCount(fuguConversationList);
//                            break;
//                        }
//                    }
//                }
//            }
//
//        }
//        try {
//            if (CommonData.getIsNewChat()) {
//                getConversations();
//                CommonData.setIsNewchat(false);
//            }
//        } catch (Exception e) {
//            //e.printStackTrace();
//        }
//    }
//
//    /**
//     * Set Recycler Data
//     */
//    private void setRecyclerViewData() {
//
////        fuguChannelsAdapter = new FuguChannelsAdapter(FuguChannelsActivity.this, fuguConversationList, userName, userId, businessName
////                , new FuguChannelsAdapter.Callback() {
////            @Override
////            public void onClick(FuguConversation conversation) {
////                Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
////                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
////                startActivityForResult(chatIntent, IS_HIT_REQUIRED);
////            }
////        }, enUserId);
//
//        hippoChannelsAdapter = new HippoChannelsAdapter(FuguChannelsActivity.this, fuguConversationList, userName, userId, businessName
//                , new HippoChannelsAdapter.Callback() {
//            @Override
//            public void onClick(FuguConversation conversation) {
//                Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
//                chatIntent.putExtra("is_from_history", isFromHistory);
//                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//                startActivityForResult(chatIntent, IS_HIT_REQUIRED);
//            }
//        }, enUserId);
//
//        updateCount(fuguConversationList);
//        layoutManager = new LinearLayoutManager(FuguChannelsActivity.this);
//        rvChannels.setLayoutManager(layoutManager);
//
//        if (hippoChannelsAdapter != null)
//            rvChannels.setAdapter(hippoChannelsAdapter);
//    }
//
//    /**
//     * Set powered by text
//     *
//     * @param userData user data
//     */
//    private void setPoweredByText(FuguPutUserDetailsResponse.Data userData) {
//        if (!userData.getWhiteLabel()) {
//            try {
//                poweredByView(getString(R.string.fugu_powered_by), getString(R.string.fugu_text), hippoColorConfig);
//            } catch (Exception e) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    String text = "<font color="
//                            + String.format("#%06X",
//                            (0xFFFFFF & hippoColorConfig.getHippoTextColorPrimary())) + ">"
//                            + getString(R.string.fugu_powered_by)
//                            + "<font color=" + String.format("#%06X",
//                            (0xFFFFFF & hippoColorConfig.getFuguRunsOnColor())) + "> "
//                            + getString(R.string.fugu_text) + "</font>";
//                    //noinspection deprecation
//                    tvPoweredBy.setText(Html.fromHtml(text));
//                } else {
//                    String text = "<font color="
//                            + String.format("#%06X",
//                            (0xFFFFFF & hippoColorConfig.getHippoTextColorPrimary())) + ">"
//                            + getString(R.string.fugu_powered_by)
//                            + "<font color=" + String.format("#%06X",
//                            (0xFFFFFF & hippoColorConfig.getFuguRunsOnColor())) + "> "
//                            + getString(R.string.fugu_text) + "</font>";
//                    tvPoweredBy.setText(Html.fromHtml(text));
//                }
//                tvPoweredBy.setBackgroundDrawable(HippoColorConfig.makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));
//            }
//        } else {
//            tvPoweredBy.setVisibility(View.GONE);
//        }
//    }
//
//    private void poweredByView(String firstString, String lastString, HippoColorConfig hippoColorConfig) throws Exception {
//        String changeString = (lastString != null ? lastString : "Hippo");
//        String totalString = firstString + " " + changeString;
//        Log.v(TAG, "totalString = " + totalString);
//        Spannable spanText = new SpannableString(totalString);
//        spanText.setSpan(new StyleSpan(Typeface.BOLD), String.valueOf(firstString).length(), totalString.length(), 0);
//        spanText.setSpan(new ForegroundColorSpan(hippoColorConfig.getFuguRunsOnColor()), String.valueOf(firstString).length(), totalString.length(), 0);
//        spanText.setSpan(new RelativeSizeSpan(0.8f), 0, String.valueOf(firstString).length(), 0);
//
//
//        tvPoweredBy.setText(spanText);
//        tvPoweredBy.setBackgroundDrawable(HippoColorConfig.makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));
//    }
//
//
//    ArrayList<UnreadCountModel> unreadCountModels = new ArrayList<>();
//
//    private void updateCount(ArrayList<Object> fuguConversationList) {
//        try {
//            int count = 0;
//            unreadCountModels.clear();
//            CommonData.setUnreadCount(unreadCountModels);
//            for (int i = 0; i < fuguConversationList.size(); i++) {
//                if (fuguConversationList.get(i) instanceof FuguConversation) {
//                    FuguConversation currentConversation = (FuguConversation) fuguConversationList.get(i);
//                    if (currentConversation.getUnreadCount() > 0) {
//                        UnreadCountModel countModel = new UnreadCountModel(currentConversation.getChannelId(), currentConversation.getLabelId(), currentConversation.getUnreadCount());
//                        unreadCountModels.add(countModel);
//                        count = count + currentConversation.getUnreadCount();
//                    }
//                }
//            }
//            CommonData.setUnreadCount(unreadCountModels);
//            HippoLog.e(TAG, "unreadCountModels: " + new Gson().toJson(unreadCountModels));
//            HippoLog.v(TAG, "unreadCountModels size = " + unreadCountModels.size());
//
//            if (HippoConfig.getInstance().getCallbackListener() != null) {
//                HippoConfig.getInstance().getCallbackListener().count(count);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void toggleProgressBarVisibility(boolean isVisible) {
//        if (isVisible) {
//            if (progressBarItem == null) {
//                progressBarItem = new HippoChannelsAdapter.ProgressBarItem();
//            }
//            if (!fuguConversationList.contains(progressBarItem)) {
//                fuguConversationList.add(progressBarItem);
//                rvChannels.post(new Runnable() {
//                    public void run() {
//                        hippoChannelsAdapter.notifyItemInserted(fuguConversationList.size() - 1);
//                    }
//                });
//            }
//        } else {
//            if (progressBarItem != null && fuguConversationList.contains(progressBarItem)) {
//                fuguConversationList.remove(progressBarItem);
//                hippoChannelsAdapter.notifyItemRemoved(fuguConversationList.size() - 1);
//            }
//        }
//    }
//
//
//    int pageStart = 1;
//    int pageEnd = 0;
//    int defaultPageSize = 20;
//
//    private void getHistory() {
//
//        pageStart = (pageStart > 0) ? pageStart : 1;
//        pageEnd = pageStart + defaultPageSize - 1;
//
//        HashMap<String, Object> params = new HashMap<>();
//        params.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
//        //params.put(USER_ID, userId);
//        params.put("channel_status", "[2]");
//        params.put(DEVICE_TYPE, ANDROID);
//        params.put(APP_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
//        params.put("page_offset", pageStart);
//        params.put("row_count", pageEnd);
//
//
//        CommonParams paramsObj = new CommonParams.Builder()
//                .addAll(params)
//                .build();
//
//        RestClient.getApiInterface().getConversation(paramsObj.getMap())
//                .enqueue(new ResponseResolver<FuguGetConversationsResponse>(this, false, false) {
//                    @Override
//                    public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
//                        try {
//                            for(int i=0;i<fuguGetConversationsResponse.getData().getFuguConversationList().size();i++) {
//                                String removeLt = fuguGetConversationsResponse.getData().getFuguConversationList().get(i).getMessage().replaceAll("<", "&lt;");
//                                String removeGt = removeLt.replaceAll(">", "&gt;");
//                                fuguGetConversationsResponse.getData().getFuguConversationList().get(i).setMessage(removeGt);
//                            }
//
//                            CommonData.setConversationList(fuguGetConversationsResponse.getData().getFuguConversationList());
//
//                            fuguConversationList.clear();
//                            fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
//                            updateCount(fuguConversationList);
//                            swipeRefresh.setRefreshing(false);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void failure(APIError error) {
//
//                    }
//                });
//    }
//
//    private void createConversation() {
//        ArrayList<String> tags = new ArrayList<>();
//        tags.add("New Conversation");
//
//        ChatByUniqueIdAttributes attributes = new ChatByUniqueIdAttributes.Builder()
//                .setTransactionId("")
//                .setUserUniqueKey(HippoConfig.getInstance().getUserData().getUserUniqueKey())
//                .setTags(tags)
//                .setInsertBotId(true)
//                .build();
//        HippoConfig.getInstance().openChatByUniqueId(attributes);
//    }
//
//    /*@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        int i = item.getItemId();
//        if (i == R.id.menuLogout) {
//            showDialog();
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.fugu_menu_logout, menu);
//        return true;
//    }*/
//
//    private void showDialog() {
//        new AlertDialog.Builder(FuguChannelsActivity.this)
//                .setMessage("Are you sure you want to logout?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, final int which) {
//                        Prefs.with(FuguChannelsActivity.this).remove("access_token");
//                        Prefs.with(FuguChannelsActivity.this).remove("user_unique_key");
//                        HippoConfig.clearHippoData(FuguChannelsActivity.this);
//                        //startActivity(new Intent(FuguChatActivity.this, MainA));
//                        finish();
//                        LibApp.getInstance().openMainScreen();
//                    }
//                })
//                .setNegativeButton("N0", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//    private void viewAnimate(boolean show) {
//        if(show) {
//            TranslateAnimation animate = new TranslateAnimation(0, 0,0, createBtn.getHeight()+100);
//            animate.setDuration(300);
//            animate.setFillAfter(true);
//            createBtn.startAnimation(animate);
//            createBtn.setVisibility(View.GONE);
//        } else {
//            TranslateAnimation animate = new TranslateAnimation(0, 0, createBtn.getHeight()+100, 0);
//            animate.setDuration(300);
//            animate.setFillAfter(true);
//            if(createBtn.getVisibility() == View.GONE) {
//                createBtn.startAnimation(animate);
//            }
//            createBtn.setVisibility(View.VISIBLE);
//        }
//    }
//
//}

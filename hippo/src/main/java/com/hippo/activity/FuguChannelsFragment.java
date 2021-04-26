//package com.hippo.activity;
//
//import android.annotation.SuppressLint;
//import android.app.NotificationManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.ContextMenu;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.hippo.BuildConfig;
//import com.hippo.CaptureUserData;
//import com.hippo.ChatByUniqueIdAttributes;
//import com.hippo.GroupingTag;
//import com.hippo.HippoColorConfig;
//import com.hippo.HippoConfig;
//import com.hippo.HippoNotificationConfig;
//import com.hippo.R;
//import com.hippo.adapter.FuguChannelsAdapter;
//import com.hippo.constant.FuguAppConstant;
//import com.hippo.database.CommonData;
//import com.hippo.datastructure.ChannelStatus;
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
//import com.hippo.utils.UniqueIMEIID;
//import com.hippo.utils.fileUpload.Prefs;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import static android.app.Activity.RESULT_OK;
//
///**
// * Created by gurmail on 2020-01-10.
// *
// * @author gurmail
// */
//public class FuguChannelsFragment extends BaseFragment implements FuguAppConstant, SwipeRefreshLayout.OnRefreshListener, Animation.AnimationListener {
//
//    private View rootView;
//    private ChannelActivity channelActivity;
//
//    private static final int NOT_CONNECTED = 0;
//    private static final int CONNECTED_TO_INTERNET = 1;
//    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
//    private RelativeLayout rlRoot;
//    private SwipeRefreshLayout swipeRefresh;
//    private RecyclerView rvChannels;
//    private TextView tvNoInternet, tvNewConversation;
//    private final int READ_PHONE_PERMISSION = 101;
//    private final String TAG = FuguChannelsActivity.class.getSimpleName();
//    private FuguChannelsAdapter fuguChannelsAdapter;
//    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();
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
//    private String title;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
//        if(getArguments() != null) {
//            title = getArguments().getString("title", "");
//            isFromHistory = getArguments().getBoolean("from_history", false);
//            openedChannelId = getArguments().getLong("channelId", -1);
//            appVersion = getArguments().getInt("appVersion", 0);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fugu_activity_channel, container, false);
//        return rootView;
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(NOTIFICATION_INTENT));
//        initViews(view);
//        decideAppFlow();
//        HippoConfig.getInstance().setChannelActivity(true);
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        channelActivity = (ChannelActivity) getActivity();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        channelActivity = null;
//    }
//
//    /**
//     * Initialize Views
//     */
//    private void initViews(View view) {
//
//        hippoColorConfig = CommonData.getColorConfig();
//
//        rlRoot = (RelativeLayout) view.findViewById(R.id.rlRoot);
//        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(this);
//        rvChannels = (RecyclerView) view.findViewById(R.id.rvChannels);
//        createBtn = view.findViewById(R.id.createBtn);
//        tvNoInternet = (TextView) view.findViewById(R.id.tvNoInternet);
//        tvNewConversation = (TextView) view.findViewById(R.id.tvNewConversation);
//        tvPoweredBy = (TextView) view.findViewById(R.id.tvPoweredBy);
//        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
//        llInternet = (LinearLayout) view.findViewById(R.id.llInternet);
//        llNoConversation = view.findViewById(R.id.llNoConversation);
//        noConversationTextView = view.findViewById(R.id.noConversationTextView);
//        configColors();
//        if (!isNetworkAvailable()) {
//            llInternet.setVisibility(View.VISIBLE);
//            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
//            tvStatus.setText(R.string.fugu_not_connected_to_internet);
//        }
//
//        createBtn.setText(getString(R.string.talk_to));
//        rvChannels.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if ((dy > 0 || dy < 0) && createBtn.getVisibility() == View.VISIBLE) {
//                    createBtn.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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
//    /**
//     * Decide app flow on basis of user data and permisions
//     */
//    private void decideAppFlow() {
//        if (CommonData.getUserDetails() != null && (CommonData.getConversationList().size() > 0) || isFromHistory
//                || CommonData.getUpdatedDetails().getData().isMultiChannelLabelMapping()) {
//            setUpUI();
//            getConversations();
//        } else {
//            sendUserDetails();
//        }
//    }
//
//    private void setApiHit() {
//        if (CommonData.getUserDetails() != null && (CommonData.getConversationList().size() > 0) || isFromHistory) {
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
//        if(!TextUtils.isEmpty(userData.getFullName()))
//            userName = userData.getFullName();
//        fuguConversationList.clear();
//        if (userData.getFuguConversations().size() > 0 && !isFromHistory) {
//            fuguConversationList.addAll(CommonData.getConversationList());
//        }
//        setRecyclerViewData();
//        setPoweredByText(tvPoweredBy);
//    }
//
//    /**
//     * Set Recycler Data
//     */
//    private void setRecyclerViewData() {
//
//        fuguChannelsAdapter = new FuguChannelsAdapter(getActivity(), fuguConversationList, userName, userId, businessName
//                , new FuguChannelsAdapter.Callback() {
//            @Override
//            public void onClick(FuguConversation conversation) {
//                Intent chatIntent = new Intent(getActivity(), FuguChatActivity.class);
//                chatIntent.putExtra("is_from_history", isFromHistory);
//                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//                startActivityForResult(chatIntent, IS_HIT_REQUIRED);
//            }
//        }, enUserId);
//        updateCount(fuguConversationList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        rvChannels.setLayoutManager(layoutManager);
//        rvChannels.setAdapter(fuguChannelsAdapter);
//    }
//
//    /**
//     * Get conversations api hit
//     */
//    private void getConversations() {
//        getConversations(isFromHistory);
//    }
//    private void getConversations(boolean showLoader) {
//        if (isNetworkAvailable()) {
//            CommonParams.Builder builder = new CommonParams.Builder();
//            builder.add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
//            builder.add(EN_USER_ID, enUserId);
//            builder.add(APP_VERSION, BuildConfig.VERSION_CODE);
//            if(isFromHistory) {
//                builder.add("status", "[2]");
//            }
//            builder.add(DEVICE_TYPE, 1);
//
//            CommonParams commonParams = builder.build();
//
//            RestClient.getApiInterface().getConversations(commonParams.getMap())
//                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(getActivity(), showLoader, false) {
//                        @Override
//                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
//                            try {
//
//                                List<FuguConversation> conversationList = new ArrayList<>();
//                                conversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
//                                if(isFromHistory && CommonData.getUpdatedDetails().getData().isMultiChannelLabelMapping()) {
//                                    for (int i = conversationList.size()-1; i >= 0; i--){
//                                        HippoLog.e("TAG", "status -> "+conversationList.get(i).getChannelStatus());
//                                        if (conversationList.get(i).getChannelStatus() == ChannelStatus.OPEN.getOrdinal() || openedChannelId.equals(conversationList.get(i).getChannelId())){
//                                            conversationList.remove(i);
//                                        } else {
//                                            String removeLt = conversationList.get(i).getMessage().replaceAll("<", "&lt;");
//                                            String removeGt = removeLt.replaceAll(">", "&gt;");
//                                            conversationList.get(i).setMessage(removeGt);
//                                        }
//                                    }
//                                } else {
//                                    for(int i=0;i<conversationList.size();i++) {
//                                        String removeLt = conversationList.get(i).getMessage().replaceAll("<", "&lt;");
//                                        String removeGt = removeLt.replaceAll(">", "&gt;");
//                                        conversationList.get(i).setMessage(removeGt);
//                                    }
//
//                                    CommonData.setConversationList(conversationList);
//                                }
//
//
//                                fuguConversationList.clear();
//                                fuguConversationList.addAll(conversationList);
//                                updateCount(fuguConversationList);
//                                fuguChannelsAdapter.notifyDataSetChanged();
//                                swipeRefresh.setRefreshing(false);
//
//                                if(fuguConversationList == null || fuguConversationList.size() == 0) {
//                                    llNoConversation.setVisibility(View.VISIBLE);
//                                    noConversationTextView.setVisibility(View.VISIBLE);
//                                } else {
//                                    llNoConversation.setVisibility(View.GONE);
//                                    noConversationTextView.setVisibility(View.GONE);
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void failure(APIError error) {
//                            swipeRefresh.setRefreshing(false);
//                        }
//                    });
//        } else {
//            swipeRefresh.setRefreshing(false);
//        }
//    }
//
//    ArrayList<UnreadCountModel> unreadCountModels = new ArrayList<>();
//
//    private void updateCount(ArrayList<FuguConversation> fuguConversationList) {
//        try {
//            int count = 0;
//            unreadCountModels.clear();
//            CommonData.setUnreadCount(unreadCountModels);
//            for(int i=0;i<fuguConversationList.size();i++) {
//                if(fuguConversationList.get(i).getUnreadCount()>0) {
//                    UnreadCountModel countModel = new UnreadCountModel(fuguConversationList.get(i).getChannelId(), fuguConversationList.get(i).getLabelId(), fuguConversationList.get(i).getUnreadCount());
//                    unreadCountModels.add(countModel);
//                    count = count + fuguConversationList.get(i).getUnreadCount();
//                }
//            }
//            CommonData.setUnreadCount(unreadCountModels);
//            HippoLog.e(TAG, "unreadCountModels: "+new Gson().toJson(unreadCountModels));
//            HippoLog.v(TAG, "unreadCountModels size = "+unreadCountModels.size());
//
//            if(HippoConfig.getInstance().getCallbackListener() != null) {
//                HippoConfig.getInstance().getCallbackListener().count(count);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        HippoConfig.getInstance().setChannelActivity(false);
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
//        readChannelId = null;
//        readLabelId = null;
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        if (nm != null) {
//            nm.cancelAll();
//        }
//        super.onResume();
//        isScreenOpen = true;
//        if (isRefresh) {
//            isRefresh = false;
//            try {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    FuguConversation currentConversation = fuguConversationList.get(i);
//                    if(readChannelId > -1 && currentConversation.getChannelId() > -1 && currentConversation.getChannelId().compareTo(readChannelId) == 0) {
//                        currentConversation.setUnreadCount(0);
//                        if (fuguChannelsAdapter != null)
//                            fuguChannelsAdapter.notifyDataSetChanged();
//                        break;
//                    } else if(readLabelId > -1 && currentConversation.getLabelId() > -1 && currentConversation.getLabelId().compareTo(readLabelId) == 0) {
//                        currentConversation.setUnreadCount(0);
//                        if (fuguChannelsAdapter != null)
//                            fuguChannelsAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            updateCount(fuguConversationList);
//        }
//        if(!isFirstTimeOpen) {
//            setApiHit();
//        }
//        isFirstTimeOpen = false;
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
//                if(!isScreenOpen)
//                    return;
//
//                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));
//
//                HippoLog.d("receiver", "Got message: " + messageJson.toString());
//
//                boolean hasChannelID = false;
//                boolean hasLabelID = false;
//
//                if(messageJson.has(NOTIFICATION_TYPE) && messageJson.getInt(NOTIFICATION_TYPE) == 5) {
//                    getConversations();
//                } else {
//                    if(messageJson.has(CHANNEL_ID) && messageJson.getLong(CHANNEL_ID) > 0) {
//                        int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
//                        if(index != -1)
//                            hasChannelID = true;
//                    }
//
//                    if(messageJson.has(LABEL_ID) && messageJson.getLong(LABEL_ID) > 0) {
//                        for (int i = 0; i < fuguConversationList.size(); i++) {
//                            FuguConversation currentConversation = fuguConversationList.get(i);
//                            if (currentConversation.getLabelId() == messageJson.getLong(LABEL_ID)) {
//                                hasLabelID = true;
//                                break;
//                            }
//                        }
//                    }
//
//                    if((!hasChannelID && !hasLabelID)) {
//                        getConversations();
//                    } else {
//                        if (messageJson.has(NEW_MESSAGE) && messageJson.has(CHANNEL_ID)) {
//                            int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
//                            if(index>-1) {
//                                FuguConversation currentConversation = fuguConversationList.get(index);
//                                currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
//                                if (messageJson.has(NEW_MESSAGE)) {
//                                    currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
//                                }
//                                if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
//                                    currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
//                                } else {
//                                    currentConversation.setUnreadCount(0);
//                                }
//                                currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
//                                currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
//                                if (fuguChannelsAdapter != null)
//                                    fuguChannelsAdapter.notifyDataSetChanged();
//
//                                updateCount(fuguConversationList);
//                            } else {
//                                getConversations();
//                            }
//                        } else if(messageJson.has(NEW_MESSAGE) && messageJson.has(LABEL_ID)) {
//                            int index = -1;
//                            for(int i=0;i<fuguConversationList.size();i++) {
//                                if(fuguConversationList.get(i).getLabelId().compareTo(messageJson.getLong(LABEL_ID)) == 0) {
//                                    index = i;
//                                    break;
//                                }
//                            }
//                            if(index>-1) {
//                                FuguConversation currentConversation = fuguConversationList.get(index);
//                                currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
//                                if (messageJson.has(NEW_MESSAGE)) {
//                                    currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
//                                }
//                                if (HippoNotificationConfig.pushLabelId.compareTo(messageJson.getLong(LABEL_ID)) != 0) {
//                                    currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
//                                } else {
//                                    currentConversation.setUnreadCount(0);
//                                }
//                                currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
//                                currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
//                                if (fuguChannelsAdapter != null)
//                                    fuguChannelsAdapter.notifyDataSetChanged();
//
//                                updateCount(fuguConversationList);
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
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == IS_HIT_REQUIRED && resultCode == RESULT_OK) {
//
//            FuguConversation conversation =
//                    new Gson().fromJson(data.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);
//
//            if (conversation != null && conversation.getLabelId().compareTo(-1L) != 0) {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    if (fuguConversationList.get(i).getLabelId().compareTo(conversation.getLabelId()) == 0) {
//                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
//                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
//                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
//                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
//                        fuguConversationList.get(i).setIsTimeSet(1);
//                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
//                        fuguConversationList.get(i).setUserId(conversation.getLast_sent_by_id());
//                        fuguConversationList.get(i).setEnUserId(conversation.getEnUserId());
//                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
//                        fuguConversationList.get(i).setChatType(conversation.getChatType());
//                        fuguChannelsAdapter.updateList(fuguConversationList);
//                        updateCount(fuguConversationList);
//                        break;
//                    }
//                }
//            } else if (conversation != null && conversation.getLabelId().compareTo(-1L) == 0) {
//                for (int i = 0; i < fuguConversationList.size(); i++) {
//                    if (fuguConversationList.get(i).getChannelId().compareTo(conversation.getChannelId()) == 0) {
//                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
//                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
//                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
//                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
//                        fuguConversationList.get(i).setIsTimeSet(1);
//                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
//                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
//                        fuguConversationList.get(i).setChatType(conversation.getChatType());
//                        fuguChannelsAdapter.updateList(fuguConversationList);
//                        updateCount(fuguConversationList);
//                        break;
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
//            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(getActivity()));
//            commonParams.put(APP_TYPE, HippoConfig.getInstance().getAppType());
//            commonParams.put(DEVICE_TYPE, ANDROID_USER);
//            commonParams.put(APP_VERSION, BuildConfig.VERSION_NAME);
//            commonParams.put(DEVICE_DETAILS, deviceDetailsJson);
//
//            if (userData != null) {
//                if (!TextUtils.isEmpty(userData.getUserUniqueKey()) && !userData.getUserUniqueKey().trim().isEmpty())
//                    commonParams.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());
//
//                if (!TextUtils.isEmpty(userData.getFullName()) && !userData.getFullName().trim().isEmpty())
//                    commonParams.put(FULL_NAME, userData.getFullName());
//
//                if (!TextUtils.isEmpty(userData.getEmail()) && !userData.getEmail().trim().isEmpty())
//                    commonParams.put(EMAIL, userData.getEmail());
//
//                if (!TextUtils.isEmpty(userData.getPhoneNumber()) && !userData.getPhoneNumber().trim().isEmpty())
//                    commonParams.put(PHONE_NUMBER, userData.getPhoneNumber());
//
//                if(!TextUtils.isEmpty(CommonData.getImagePath()) && !TextUtils.isEmpty(CommonData.getImagePath()))
//                    commonParams.put(HIPPO_USER_IMAGE_PATH, CommonData.getImagePath());
//
//                if(!userData.getTags().isEmpty()) {
//                    ArrayList<GroupingTag> groupingTags = new ArrayList<>();
//                    for(GroupingTag tag : userData.getTags()) {
//                        GroupingTag groupingTag = new GroupingTag();
//                        if(!TextUtils.isEmpty(tag.getTagName()))
//                            groupingTag.setTagName(tag.getTagName());
//                        if(tag.getTeamId() != null)
//                            groupingTag.setTeamId(tag.getTeamId());
//
//                        if(!TextUtils.isEmpty(tag.getTagName()) || tag.getTeamId() != null) {
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
//            if (HippoConfig.getmResellerToken() != null) {
//                apiPutUserDetailReseller(commonParams);
//            } else {
//                apiPutUserDetail(commonParams);
//            }
//        } else {
//            tvNoInternet.setVisibility(View.VISIBLE);
//            swipeRefresh.setVisibility(View.GONE);
//            tvNewConversation.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * APi to send user details
//     *
//     * @param commonParams params to be sent
//     */
//    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
//        CommonParams params = new CommonParams.Builder()
//                .putMap(commonParams)
//                .build();
//        RestClient.getApiInterface().putUserDetails(params.getMap())
//                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(getActivity(), true, false) {
//                    @Override
//                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
//                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
//                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
//                        try {
//                            Prefs.with(getActivity()).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
//                            Prefs.with(getActivity()).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
//                            Prefs.with(getActivity()).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
//                            Prefs.with(getActivity()).save("email", fuguPutUserDetailsResponse.getData().getEmail());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        setUpUI();
//                    }
//
//                    @Override
//                    public void failure(APIError error) {
//                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
//                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
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
//        CommonParams params = new CommonParams.Builder()
//                .putMap(commonParams)
//                .build();
//        RestClient.getApiInterface().putUserDetailsReseller(params.getMap())
//                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(getActivity(), true, false) {
//                    @Override
//                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
//                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
//                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
//                        try {
//                            Prefs.with(getActivity()).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
//                            Prefs.with(getActivity()).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
//                            Prefs.with(getActivity()).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
//                            Prefs.with(getActivity()).save("email", fuguPutUserDetailsResponse.getData().getEmail());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        setUpUI();
//                    }
//
//                    @Override
//                    public void failure(APIError error) {
//                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
//                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
//                        } else {
//                            tvNoInternet.setVisibility(View.VISIBLE);
//                            swipeRefresh.setVisibility(View.GONE);
//                            tvNewConversation.setVisibility(View.GONE);
//
//                        }
//                    }
//                });
//    }
//}

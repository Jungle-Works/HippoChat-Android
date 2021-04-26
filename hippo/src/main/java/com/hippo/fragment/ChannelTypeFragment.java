package com.hippo.fragment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hippo.*;
import com.hippo.activity.BaseFragment;
import com.hippo.adapter.PagerAdapter;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.datastructure.ChannelStatus;
import com.hippo.eventbus.BusProvider;
import com.hippo.eventbus.ConversationEvent;
import com.hippo.eventbus.OnViewUpdate;
import com.hippo.helper.BusEvents;
import com.hippo.helper.FayeMessage;
import com.hippo.helper.P2pUnreadCount;
import com.hippo.langs.Restring;
import com.hippo.model.FuguConversation;
import com.hippo.model.FuguDeviceDetails;
import com.hippo.model.FuguGetConversationsResponse;
import com.hippo.model.FuguPutUserDetailsResponse;
import com.hippo.model.UnreadCountModel;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.CustomViewPager;
import com.hippo.utils.HippoLog;
import com.hippo.utils.UniqueIMEIID;
import com.hippo.utils.UnreadCount;
import com.hippo.utils.fileUpload.Prefs;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import faye.ConnectionManager;

import static android.app.Activity.RESULT_OK;

/**
 * Created by gurmail on 2020-01-26.
 *
 * @author gurmail
 */
public class ChannelTypeFragment extends BaseFragment implements FuguAppConstant, ViewPager.OnPageChangeListener {

    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private View rootView;

    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();

    private String label = "";
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private int appVersion = 0;

    private final int IS_HIT_REQUIRED = 200;
    public static boolean isRefresh = false;
    public static Long readChannelId = -1L;
    public static Long readLabelId = -1L;
    private boolean isScreenOpen = false;
    private boolean isFirstTimeOpen = true;
    private boolean isFromHistory = false;
    private Long openedChannelId = -1L;

    private PagerAdapter pagerAdapter;
    private ArrayList<Fragment> pagerFragments = new ArrayList<>();
    String[] titles = new String[2];

    private String title;

    private TextView tvToolbarName;
    private RelativeLayout myToolbar;
    private ImageView logout, ivBackBtn;
    private LinearLayout llBtn;

    private boolean hasPager;
    private HippoColorConfig hippoColorConfig;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if(getArguments() != null) {
            title = getArguments().getString("title", "");
            isFromHistory = getArguments().getBoolean("from_history", false);
            openedChannelId = getArguments().getLong("channelId", -1);
            appVersion = getArguments().getInt("appVersion", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_channel_pager,  container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    private Typeface customBold, customNormal;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(NOTIFICATION_INTENT));
        HippoConfig.getInstance().setChannelActivity(true);
        title = CommonData.getChatTitle(getActivity());


        myToolbar = view.findViewById(R.id.my_toolbar);
        tvToolbarName = view.findViewById(R.id.tv_toolbar_name);
        logout = view.findViewById(R.id.logout);
        ivBackBtn = view.findViewById(R.id.ivBackBtn);
        llBtn = view.findViewById(R.id.llBtn);

        tvToolbarName.setText(title);
        myToolbar.setBackgroundColor(CommonData.getColorConfig().getHippoActionBarBg());
        tvToolbarName.setTextColor(CommonData.getColorConfig().getHippoActionBarText());
        if(HippoConfig.getInstance().isHideBackBtn()) {
            ivBackBtn.setVisibility(View.GONE);
        } else {
            ivBackBtn.getDrawable().setColorFilter(CommonData.getColorConfig().getHippoActionBarText(), PorterDuff.Mode.SRC_ATOP);
        }

        tvToolbarName.setTextSize(20);


        ConnectionManager.INSTANCE.initFayeConnection();



        viewPager = view.findViewById(R.id.pagerView);
        tabLayout = view.findViewById(R.id.tabs);

        tabLayout.setBackgroundColor(CommonData.getColorConfig().getHippoActionBarBg());

        viewPager.setPagingEnabled(true);
        viewPager.addOnPageChangeListener(this);

        customBold = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/ProximaNova-Sbold.ttf");
        customNormal = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/ProximaNova-Reg.ttf");

        pagerFragments = new ArrayList<>();
        OpenChannelFragment closeFragment = new OpenChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isClosedChannel", true);
        closeFragment.setArguments(bundle);

        HippoConfigAttributes attributes = CommonData.getAttributes();
        try {
            if(attributes != null && attributes.getAdditionalInfo() != null) {
                hasPager = attributes.getAdditionalInfo().isHasChannelPager();
                if(attributes.getAdditionalInfo().hasLogoutBtn()) {
                    llBtn.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {

        }

        pagerFragments.add(new OpenChannelFragment());
        if(hasPager) {
            pagerFragments.add(closeFragment);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }


        titles[0] = Restring.getString(getActivity(), R.string.hippo_current);
        titles[1] = Restring.getString(getActivity(), R.string.hippo_past);

        pagerAdapter = new PagerAdapter(getChildFragmentManager(), pagerFragments, titles);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        if(hasPager) {
            try {
                setTabColor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        decideAppFlow();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                try {
                    LibApp.getInstance().trackEvent("List Screen", "button clicked", "Logout");
                } catch (Exception e) {

                }
            }
        });

        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().onBackPressed();
                } catch (Exception e) {

                }
            }
        });
    }

    private void setTabColor() throws Exception {
        tabLayout.setSelectedTabIndicatorColor(CommonData.getColorConfig().getHippoSelectedTabIndicatorColor());
        tabLayout.setTabTextColors(CommonData.getColorConfig().getHippoTabTextColor(),
                CommonData.getColorConfig().getHippoTabSelectedTextColor());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
                    tabTextView.setTypeface(customBold);
                    tabTextView.setTextSize(16);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                try {
                    LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
                    tabTextView.setTypeface(customNormal);
                    tabTextView.setTextSize(16);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
        tabTextView.setTypeface(customNormal);
        tabTextView.setTextSize(16);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * Decide app flow on basis of user data and permisions
     */
    private void decideAppFlow() {
        if (CommonData.getUserDetails() != null) {
            setUpUI();
            getConversations();
        } else {
            sendUserDetails();
        }
    }

    private void setApiHit() {
        if (CommonData.getUserDetails() != null) {
            getConversations();
        } else {
            sendUserDetails();
        }
    }

    /**
     * Set up application UI
     */
    private void setUpUI() {
        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
        label = userData.getBusinessName();
        businessName = userData.getBusinessName();
        userId = userData.getUserId();
        enUserId = userData.getEn_user_id();
        if(!TextUtils.isEmpty(userData.getFullName()))
            userName = userData.getFullName();
        fuguConversationList.clear();
        if (CommonData.getConversationList().size() > 0 && !isFromHistory) {
            fuguConversationList.addAll(CommonData.getConversationList());
        } else if(CommonData.getConversationList().size() > 0) {
            fuguConversationList.addAll(CommonData.getConversationList());
        }

        BusProvider.getInstance().post(new OnViewUpdate(4));
    }

    /**
     * Get conversations api hit
     */
    private void getConversations() {
        getConversations(isFromHistory);
    }

    private void getConversations(boolean showLoader) {
        if (isNetworkAvailable()) {
            CommonParams.Builder builder = new CommonParams.Builder();
            builder.add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
            builder.add(EN_USER_ID, enUserId);
            builder.add(APP_VERSION, HippoConfig.getInstance().getVersionCode());
            if(isFromHistory) {
                builder.add("status", "[2]");
            }
            builder.add(DEVICE_TYPE, 1);

            CommonParams commonParams = builder.build();

            RestClient.getApiInterface().getConversations(commonParams.getMap())
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(getActivity(), showLoader, false) {
                        @Override
                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                            try {

                                List<FuguConversation> conversationList = new ArrayList<>();
                                conversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
                                if(isFromHistory && CommonData.getUpdatedDetails().getData().isMultiChannelLabelMapping()) {
                                    for (int i = conversationList.size()-1; i >= 0; i--){
                                        HippoLog.e("TAG", "status -> "+conversationList.get(i).getChannelStatus());
                                        if (conversationList.get(i).getChannelStatus() == ChannelStatus.OPEN.getOrdinal() || openedChannelId.equals(conversationList.get(i).getChannelId())){
                                            conversationList.remove(i);
                                        } else {
                                            try {
                                                String removeLt = conversationList.get(i).getMessage().replaceAll("<", "&lt;");
                                                String removeGt = removeLt.replaceAll(">", "&gt;");
                                                conversationList.get(i).setMessage(removeGt);
                                            } catch (Exception e) {
                                                conversationList.get(i).setMessage(conversationList.get(i).getMessage());
                                            }
                                        }
                                    }
                                } else {
                                    for(int i=0;i<conversationList.size();i++) {
                                        try {
                                            String removeLt = conversationList.get(i).getMessage().replaceAll("<", "&lt;");
                                            String removeGt = removeLt.replaceAll(">", "&gt;");
                                            conversationList.get(i).setMessage(removeGt);
                                        } catch (Exception e) {
                                            conversationList.get(i).setMessage(conversationList.get(i).getMessage());
                                        }
                                    }

                                    CommonData.setConversationList(conversationList);
                                }


                                fuguConversationList.clear();
                                fuguConversationList.addAll(conversationList);
                                updateCount(fuguConversationList);
                                BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            BusProvider.getInstance().post(new OnViewUpdate(3));
                        }
                    });
        } else {
            BusProvider.getInstance().post(new OnViewUpdate(3));
        }
    }

    ArrayList<UnreadCountModel> unreadCountModels = new ArrayList<>();

    private void updateCount(ArrayList<FuguConversation> fuguConversationList) {
        try {
            new UnreadCount().execute(fuguConversationList);
            /*int count = 0;
            unreadCountModels.clear();
            CommonData.setUnreadCount(unreadCountModels);
            for(int i=0;i<fuguConversationList.size();i++) {
                if(fuguConversationList.get(i).getUnreadCount()>0) {
                    UnreadCountModel countModel = new UnreadCountModel(fuguConversationList.get(i).getChannelId(), fuguConversationList.get(i).getLabelId(), fuguConversationList.get(i).getUnreadCount());
                    unreadCountModels.add(countModel);
                    count = count + fuguConversationList.get(i).getUnreadCount();
                }
            }
            CommonData.setUnreadCount(unreadCountModels);

            if(HippoConfig.getInstance().getCallbackListener() != null) {
                HippoConfig.getInstance().getCallbackListener().count(count);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createConversation() {
        ArrayList<String> tags = new ArrayList<>();
        if(HippoConfig.getInstance().getTags() !=null && HippoConfig.getInstance().getTags().size()>0) {
            tags.addAll(HippoConfig.getInstance().getTags());
        }

        ChatByUniqueIdAttributes attributes = new ChatByUniqueIdAttributes.Builder()
                .setTransactionId("")
                .setUserUniqueKey(HippoConfig.getInstance().getUserData().getUserUniqueKey())
                .setTags(tags)
                .setInsertBotId(true)
                .build();
        HippoConfig.getInstance().openChatByUniqueId(attributes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HippoConfig.getInstance().setChannelActivity(false);
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        readChannelId = null;
        readLabelId = null;

    }

    @Override
    public void onResume() {
        super.onResume();

        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        super.onResume();
        isScreenOpen = true;
        if (isRefresh) {
            isRefresh = false;
            try {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    FuguConversation currentConversation = fuguConversationList.get(i);
                    if(readChannelId > -1 && currentConversation.getChannelId() > -1 && currentConversation.getChannelId().compareTo(readChannelId) == 0) {
                        currentConversation.setUnreadCount(0);
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        break;
                    } else if(readLabelId > -1 && currentConversation.getLabelId() > -1 && currentConversation.getLabelId().compareTo(readLabelId) == 0) {
                        currentConversation.setUnreadCount(0);
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateCount(fuguConversationList);
        }
        if(!isFirstTimeOpen) {
            setApiHit();
        }
        isFirstTimeOpen = false;
    }

    /**
     * Broadcast receiver to handle push messages on channels screen
     */
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                if(!isScreenOpen)
                    return;

                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));

                HippoLog.d("receiver", "Got message: " + messageJson.toString());

                boolean hasChannelID = false;
                boolean hasLabelID = false;

                if(messageJson.has(NOTIFICATION_TYPE) && messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    getConversations();
                } else {
                    if(messageJson.has(CHANNEL_ID) && messageJson.getLong(CHANNEL_ID) > 0) {
                        int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
                        if(index != -1)
                            hasChannelID = true;
                    }

                    if(messageJson.has(LABEL_ID) && messageJson.getLong(LABEL_ID) > 0) {
                        for (int i = 0; i < fuguConversationList.size(); i++) {
                            FuguConversation currentConversation = fuguConversationList.get(i);
                            if (currentConversation.getLabelId() == messageJson.getLong(LABEL_ID)) {
                                hasLabelID = true;
                                break;
                            }
                        }
                    }

                    if((!hasChannelID && !hasLabelID)) {
                        getConversations();
                    } else {
                        if (messageJson.has(NEW_MESSAGE) && messageJson.has(CHANNEL_ID)) {
                            int index = fuguConversationList.indexOf(new FuguConversation(messageJson.getLong(CHANNEL_ID)));
                            if(index>-1) {
                                FuguConversation currentConversation = fuguConversationList.get(index);
                                currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
                                if (messageJson.has(NEW_MESSAGE)) {
                                    currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
                                }
                                if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.getLong("channel_id")) != 0) {
                                    currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
                                } else {
                                    currentConversation.setUnreadCount(0);
                                }
                                currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
                                currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
                                BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));

                                if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.optLong("channel_id")) != 0) {
                                    updateCount(fuguConversationList);
                                }
                            } else {
                                getConversations();
                            }
                        } else if(messageJson.has(NEW_MESSAGE) && messageJson.has(LABEL_ID)) {
                            int index = -1;
                            for(int i=0;i<fuguConversationList.size();i++) {
                                if(fuguConversationList.get(i).getLabelId().compareTo(messageJson.getLong(LABEL_ID)) == 0) {
                                    index = i;
                                    break;
                                }
                            }
                            if(index>-1) {
                                FuguConversation currentConversation = fuguConversationList.get(index);
                                currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));
                                if (messageJson.has(NEW_MESSAGE)) {
                                    currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
                                }
                                if (HippoNotificationConfig.pushLabelId.compareTo(messageJson.getLong(LABEL_ID)) != 0) {
                                    currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
                                } else {
                                    currentConversation.setUnreadCount(0);
                                }
                                currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
                                currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
                                BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));

                                if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.optLong("channel_id")) != 0) {
                                    updateCount(fuguConversationList);
                                }
                            } else {
                                getConversations();
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_HIT_REQUIRED && resultCode == RESULT_OK) {

            FuguConversation conversation =
                    new Gson().fromJson(data.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);

            if (conversation != null && conversation.getLabelId().compareTo(-1L) != 0) {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    if (fuguConversationList.get(i).getLabelId().compareTo(conversation.getLabelId()) == 0) {
                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
                        fuguConversationList.get(i).setIsTimeSet(1);
                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setUserId(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setEnUserId(conversation.getEnUserId());
                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
                        fuguConversationList.get(i).setChatType(conversation.getChatType());
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        updateCount(fuguConversationList);
                        break;
                    }
                }
            } else if (conversation != null && conversation.getLabelId().compareTo(-1L) == 0) {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    if (fuguConversationList.get(i).getChannelId().compareTo(conversation.getChannelId()) == 0) {
                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
                        fuguConversationList.get(i).setIsTimeSet(1);
                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
                        fuguConversationList.get(i).setChatType(conversation.getChatType());
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        updateCount(fuguConversationList);
                        break;
                    }
                }
            }

        }
        try {
            if (CommonData.getIsNewChat()) {
                getConversations();
                CommonData.setIsNewchat(false);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Subscribe
    public void onConversationEvent(ConversationEvent event) {
        if(event.type == 1) {
            onRefresh();
        } else if(event.type == 2) {
            createConversation();
        } else if(event.type == 3) {
            ivBackBtn.performClick();
        }
    }

    @Subscribe
    public void onFayeMessage(FayeMessage events) {
        if (events.type.equalsIgnoreCase(BusEvents.RECEIVED_MESSAGE.toString())) {
            //onReceivedMessage(events.message, events.channelId);
            try {
                JSONObject messageJson = new JSONObject(events.message);
                int notificationType = messageJson.optInt(NOTIFICATION_TYPE, 0);
                if(notificationType == 13) {
                    String channelImageUrl = messageJson.optString("channel_image_url");
                    String label = messageJson.optString("label");
                    //String dateTime = messageJson.optString("date_time");
                    Long channelId = messageJson.optLong("channel_id", -1);
                    if(channelId > 0) {
                        int index = fuguConversationList.indexOf(new FuguConversation(channelId));
                        if (index != -1) {
                            fuguConversationList.get(index).setLabel(label);
                            fuguConversationList.get(index).setChannelImage(channelImageUrl);

                            BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        }
                    }
                } else if(notificationType == 1) {
                    if (events.channelId.equalsIgnoreCase("/" + CommonData.getUserDetails().getData().getUserChannel())) {
                        Long channelId = messageJson.optLong("channel_id", -1);
                        if(channelId>0) {
                            if(messageJson.optInt("channel_status", 0) == ChannelStatus.CLOSED.getOrdinal()) {
                                int index = fuguConversationList.indexOf(new FuguConversation(channelId));
                                if (index != -1) {
                                    fuguConversationList.get(index).setChannelStatus(ChannelStatus.CLOSED.getOrdinal());
                                    BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                                }
                            } else {

                                updateChannelCount(messageJson);
//                                updateCount(channelId);
//                                P2pUnreadCount.INSTANCE.updateCount(channelId);
                            }
                        }
                    }

                } else if(notificationType == 24) {
                    if (events.channelId.equalsIgnoreCase("/" + CommonData.getUserDetails().getData().getUserChannel())) {
                        Long channelId = messageJson.optLong("channel_id", -1);
                        if(channelId>0 && messageJson.optInt("is_last_message") == 1) {
                            int index = fuguConversationList.indexOf(new FuguConversation(channelId));
                            if(messageJson.optInt("status") == 2) {
                                fuguConversationList.get(index).setMessage(messageJson.optString("edited_message"));
                            } else if(messageJson.optInt("status") == 1) {

                                String deleteStr = Restring.getString(getActivity(), R.string.hippo_message_deleted);
                                fuguConversationList.get(index).setMessage(deleteStr);
                            }
                            BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*private void updateCount(Long channelId) {
        if (channelId > 0 && HippoNotificationConfig.pushChannelId != null && HippoNotificationConfig.pushChannelId.compareTo(channelId) != 0) {
            HippoConfig.getInstance().increaseCount(channelId);
        }
    }*/

    private void updateChannelCount(JSONObject messageJson) {
        try {
            String transactionId = messageJson.optString("transaction_id", "");
            String muid = messageJson.optString("muid", "");
            long channelId = messageJson.optLong("channel_id", -1);
            if(!TextUtils.isEmpty(transactionId)) {
                P2pUnreadCount.INSTANCE.updateChannelId(transactionId, channelId, muid);
            }
        } catch (Exception e) {

        }
    }

    //@Override
    public void onRefresh() {
        getConversations(false);
    }

    /**
     * Send user details to server
     */
    private void sendUserDetails() {
        if (isNetworkAvailable()) {
            Gson gson = new GsonBuilder().create();
            JsonObject deviceDetailsJson = null;
            try {
                deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(appVersion).getDeviceDetails()).getAsJsonObject();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            CaptureUserData userData = null;//getIntent().getParcelableExtra("userData");

            if (userData == null) {
                userData = HippoConfig.getInstance().getUserData();
            }

            HashMap<String, Object> commonParams = new HashMap<>();
            HippoConfig.getInstance();
            if (HippoConfig.getmResellerToken() != null) {
                commonParams.put(RESELLER_TOKEN, HippoConfig.getmResellerToken());
                commonParams.put(REFERENCE_ID, String.valueOf(HippoConfig.getmReferenceId()));
            } else {
                commonParams.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
            }
            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(getActivity()));
            commonParams.put(APP_TYPE, HippoConfig.getInstance().getAppType());
            commonParams.put(DEVICE_TYPE, ANDROID_USER);
            commonParams.put(APP_VERSION, HippoConfig.getInstance().getVersionName());
            commonParams.put(DEVICE_DETAILS, deviceDetailsJson);

            if (userData != null) {
                if (!TextUtils.isEmpty(userData.getUserUniqueKey()) && !userData.getUserUniqueKey().trim().isEmpty())
                    commonParams.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());

                if (!TextUtils.isEmpty(userData.getFullName()) && !userData.getFullName().trim().isEmpty())
                    commonParams.put(FULL_NAME, userData.getFullName());

                if (!TextUtils.isEmpty(userData.getEmail()) && !userData.getEmail().trim().isEmpty())
                    commonParams.put(EMAIL, userData.getEmail());

                if (!TextUtils.isEmpty(userData.getPhoneNumber()) && !userData.getPhoneNumber().trim().isEmpty())
                    commonParams.put(PHONE_NUMBER, userData.getPhoneNumber());

                if(!TextUtils.isEmpty(CommonData.getImagePath()) && !TextUtils.isEmpty(CommonData.getImagePath()))
                    commonParams.put(HIPPO_USER_IMAGE_PATH, CommonData.getImagePath());

                if(!userData.getTags().isEmpty()) {
                    ArrayList<GroupingTag> groupingTags = new ArrayList<>();
                    for(GroupingTag tag : userData.getTags()) {
                        GroupingTag groupingTag = new GroupingTag();
                        if(!TextUtils.isEmpty(tag.getTagName()))
                            groupingTag.setTagName(tag.getTagName());
                        if(tag.getTeamId() != null)
                            groupingTag.setTeamId(tag.getTeamId());

                        if(!TextUtils.isEmpty(tag.getTagName()) || tag.getTeamId() != null) {
                            groupingTags.add(groupingTag);
                        }
                    }
                    commonParams.put(GROUPING_TAGS, new Gson().toJson(groupingTags));
                } else {
                    commonParams.put(GROUPING_TAGS, "[]");
                }
            }

            String deviceToken = CommonData.getAttributes().getDeviceToken();
            if (!TextUtils.isEmpty(deviceToken))
                commonParams.put(DEVICE_TOKEN, deviceToken);
            if (userData != null && !userData.getCustom_attributes().isEmpty()) {
                commonParams.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
            }

//            try {
//                if(attributes.getCustomAttributes() != null && attributes.getCustomAttributes().size() > 0)
//                    commonParamsMAp.put(CUSTOM_ATTRIBUTES, new JSONObject(attributes.getCustomAttributes()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (HippoConfig.getmResellerToken() != null) {
                apiPutUserDetailReseller(commonParams);
            } else {
                apiPutUserDetail(commonParams);
            }
        } else {
            BusProvider.getInstance().post(new OnViewUpdate(1));
        }
    }

    /**
     * APi to send user details
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build();
        RestClient.getApiInterface().putUserDetails(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(getActivity(), true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        try {
                            Prefs.with(getActivity()).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
                            Prefs.with(getActivity()).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
                            Prefs.with(getActivity()).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
                            Prefs.with(getActivity()).save("email", fuguPutUserDetailsResponse.getData().getEmail());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setUpUI();


                        fuguConversationList.clear();
                        fuguConversationList.addAll(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        updateCount(fuguConversationList);
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            BusProvider.getInstance().post(new OnViewUpdate(1));
                        }
                    }
                });
    }

    /**
     * APi to send user details for reseller
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build();
        RestClient.getApiInterface().putUserDetailsReseller(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(getActivity(), true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        try {
                            Prefs.with(getActivity()).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
                            Prefs.with(getActivity()).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
                            Prefs.with(getActivity()).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
                            Prefs.with(getActivity()).save("email", fuguPutUserDetailsResponse.getData().getEmail());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setUpUI();

                        fuguConversationList.clear();
                        fuguConversationList.addAll(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        updateCount(fuguConversationList);
                        BusProvider.getInstance().post(new OnViewUpdate(2, fuguConversationList));
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            BusProvider.getInstance().post(new OnViewUpdate(1));

                        }
                    }
                });
    }

    private void showDialog() {
        String msg = Restring.getString(getActivity(), R.string.hippo_logout_msg);
        String yes = Restring.getString(getActivity(), R.string.hippo_yes);
        String no = Restring.getString(getActivity(), R.string.hippo_no);
        new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Prefs.with(getActivity()).remove("access_token");
                        Prefs.with(getActivity()).remove("user_unique_key");
                        HippoConfig.clearHippoData(getActivity());
                        //startActivity(new Intent(FuguChatActivity.this, MainA));
                        getActivity().finish();
                        LibApp.getInstance().openMainScreen();
                        try {
                            LibApp.getInstance().trackEvent("List Screen", "Logout Button clicked", "Yes");
                        } catch (Exception e) {

                        }
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            LibApp.getInstance().trackEvent("List Screen", "Logout Button clicked", "No");
                        } catch (Exception e) {

                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

}

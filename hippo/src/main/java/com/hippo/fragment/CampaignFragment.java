package com.hippo.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.hippo.*;
import com.hippo.activity.BaseFragment;
import com.hippo.adapter.CampaignAdapter;
import com.hippo.apis.ApiGetMobileNotification;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.interfaces.OnClearNotificationListener;
import com.hippo.interfaces.OnItemClickListener;
import com.hippo.langs.Restring;
import com.hippo.model.FuguConversation;
import com.hippo.model.FuguPutUserDetailsResponse;
import com.hippo.model.promotional.CustomAttributes;
import com.hippo.model.promotional.Data;
import com.hippo.model.promotional.PromotionResponse;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.filepicker.ToastUtil;
import com.hippo.utils.swipetoshow.SwipeHolder;
import com.hippo.utils.swipetoshow.SwipeOnItemTouchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;

import static com.hippo.constant.FuguAppConstant.NOTIFICATION_INTENT;

/**
 * Created by gurmail on 2020-01-10.
 *
 * @author gurmail
 */
public class CampaignFragment extends BaseFragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = CampaignFragment.class.getSimpleName();
    private View rootView;
    private TextView tvToolbarName;
    private RelativeLayout myToolbar;
    private TextView delete;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CampaignAdapter campaignAdapter;
    private MobileCampaignBuilder campaignBuilder;
    private int startOffset = 0;
    private int offset = 20;
    //private int endOffset;
    private ArrayList<Data> arrayList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean hasMorePages;
    private boolean isPagingApiInProgress;
    private TextView titleError;
    private ImageView ivBackBtn;
    private LinearLayout llNoNotifications;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.hippo_campaigns_layout, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        HippoConfig.getInstance().setAnnouncementActivity(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HippoConfig.getInstance().setAnnouncementActivity(false);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiverChat);

    }

    @Override
    public void onResume() {
        super.onResume();
        clearNotificationBanners();

    }

    @Override
    public void onPause() {
        super.onPause();
        HippoConfig.getInstance().setAnnouncementActivity(false);

    }

    private BroadcastReceiver mMessageReceiverChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                JSONObject json = new JSONObject(intent.getStringExtra("message"));
                if (json.getInt("is_announcement_push") == 1) {
                    setDataFromPush(intent.getStringExtra("message"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiverChat, new IntentFilter(NOTIFICATION_INTENT));

        campaignBuilder = HippoConfig.getInstance().getMobileCampaignBuilder();

        refreshLayout = view.findViewById(R.id.swipe_refresh);
        titleError = view.findViewById(R.id.title_error);
        recyclerView = view.findViewById(R.id.recyclerView);
        //endOffset = offset;

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.hippo_white);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.fugu_theme_color_primary);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        llNoNotifications = view.findViewById(R.id.llNoNotifications);

        myToolbar = view.findViewById(R.id.my_toolbar);
        tvToolbarName = view.findViewById(R.id.tv_toolbar_name);

        myToolbar.setBackgroundColor(CommonData.getColorConfig().getHippoActionBarBg());
        tvToolbarName.setTextColor(CommonData.getColorConfig().getHippoActionBarText());
        delete = view.findViewById(R.id.deleteTxt);

        String title = Restring.getString(getActivity(), R.string.hippo_notifications_title);
        String errorTxt = Restring.getString(getActivity(), R.string.hippo_no_notifications);
        String clearText = Restring.getString(getActivity(), R.string.hippo_clear_all_notification);

        if (campaignBuilder != null && !TextUtils.isEmpty(campaignBuilder.getNotificationTitle())) {
            title = campaignBuilder.getNotificationTitle();
        }

        if (campaignBuilder != null && !TextUtils.isEmpty(campaignBuilder.getEmptyNotificationText())) {
            errorTxt = campaignBuilder.getEmptyNotificationText();
        }

        if (campaignBuilder != null && !TextUtils.isEmpty(campaignBuilder.getClearText())) {
            clearText = campaignBuilder.getClearText();
        }

        tvToolbarName.setText(title);
        titleError.setText(errorTxt);
        delete.setText(clearText);
        delete.setTextColor(CommonData.getColorConfig().getHippoActionBarText());

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        boolean hideImahe = false;
        if (campaignBuilder != null && campaignBuilder.isHideDownloadBtn())
            hideImahe = true;
        String clear = "";
        if (campaignBuilder != null && !TextUtils.isEmpty(campaignBuilder.getClearBtn()))
            clear = campaignBuilder.getClearBtn();
        campaignAdapter = new CampaignAdapter(getActivity(), this, null, recyclerView, this, hideImahe, clear);
        recyclerView.setAdapter(campaignAdapter);

        ivBackBtn = view.findViewById(R.id.ivBackBtn);
        ivBackBtn.getDrawable().setColorFilter(CommonData.getColorConfig().getHippoActionBarText(), PorterDuff.Mode.SRC_ATOP);

        setTextSize(tvToolbarName, 20);
        setTextSize(delete, 13.4f);
        setTextSize(titleError, 18);

        if (getActivity().getIntent().hasExtra("dataMessage")) {
            getSavedData(false);
            if (arrayList.size() > 0) {
                HashSet<String> unreadDate = CommonData.getAnnouncementCount();
                if (unreadDate.size() > 0) {
                    HashSet<String> list = new HashSet();
                    for (Data data : arrayList)
                        list.add(String.valueOf(data.getChannelId()));
                    if (list.size() > 0) {
                        unreadDate.removeAll(list);
                        if (HippoConfig.getInstance().getCallbackListener() != null) {
                            HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(unreadDate.size());
                        }
                        CommonData.setAnnouncementCount(unreadDate);
                    }
                }
                updatePushData();
//                setUI();
//                setDataFromPush(getActivity().getIntent().getStringExtra("dataMessage"));
            } else
                getSavedData(true);
        } else {
            getSavedData(true);
        }


        //CommonData.deleteAnnouncementCount();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isPagingApiInProgress && hasMorePages) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            startOffset = arrayList.size();
                            //endOffset = startOffset +offset;
                            fetchData();
                        }
                    }
                }
            }
        });

        recyclerView.addOnItemTouchListener(new SwipeOnItemTouchAdapter(getActivity(), recyclerView, layoutManager) {
            @Override
            public void onItemHiddenClick(SwipeHolder swipeHolder, int position) {
                clearData(arrayList.get(position).getChannelId(), position);
                //call reset to hide.
                swipeHolder.reset();
            }

            @Override
            public void onItemClick(int position) {

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData(-1, -1);
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

    private void setDataFromPush(String pushMessage) {
        try {
            JSONObject json = new JSONObject(pushMessage);
            if (json.getInt("is_announcement_push") == 1) {

                Data broadcast = new Data(Integer.parseInt(HippoConfig.getInstance().getUserData().getUserId().toString()));
//                Data broadcast = new Data(10);
                broadcast.setChannelId(json.getInt("channel_id"));
                broadcast.setCreatedAt(json.getString("date_time"));
                broadcast.setDisableReply(json.getInt("disable_reply"));
                broadcast.setDescription(json.getString("message"));
                CustomAttributes attr = new Gson().fromJson(json.getString("custom_attributes"), CustomAttributes.class);
                broadcast.setCustomAttributes(attr);
                broadcast.setTitle(json.getString("title"));
                broadcast.setAddedFromBroadcast(true);
                if (arrayList == null)
                    arrayList = new ArrayList<>();

//                if (!ifExsist(broadcast.getChannelId()))
                arrayList.add(0, broadcast);

                HashSet<String> unreadDate = CommonData.getAnnouncementCount();
                if (unreadDate.size() > 0) {
                    HashSet<String> list = new HashSet();
                    for (Data data : arrayList)
                        list.add(String.valueOf(data.getChannelId()));
                    if (list.size() > 0) {
                        unreadDate.removeAll(list);
                        if (HippoConfig.getInstance().getCallbackListener() != null) {
                            HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(unreadDate.size());
                        }
                        CommonData.setAnnouncementCount(unreadDate);
                    }
                }
                updatePushData();
//                setUI();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUI() {
        if (campaignAdapter != null)
            campaignAdapter.notifyDataSetChanged();

        if (arrayList.size() > 0) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }
        if (arrayList.size() == 0) {
            llNoNotifications.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        } else {
            llNoNotifications.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }
    }

    public void clearNotificationBanners() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            ids.add(arrayList.get(i).getChannelId());
        }
        if (ids.size() > 0)
            HippoNotificationConfig.clearNotifications(getContext(), ids);
    }

    HippoConfigAttributes attributes;

    private HippoConfigAttributes getAttributes() {
        if (attributes == null)
            attributes = CommonData.getAttributes();
        return attributes;
    }

    public void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
            startOffset = 0;
            //endOffset = startOffset + offset;
            fetchData();
        } else {
            refreshLayout.setRefreshing(false);
        }
    }

    public static JSONObject objectToJSONObject(Object object) {
        Object json = null;
        JSONObject jsonObject = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            jsonObject = (JSONObject) json;
        }
        return jsonObject;
    }

    @Override
    public void onClickListener(int position) {
        try {
            if (campaignBuilder != null && campaignBuilder.getListener() != null) {
                String data = new Gson().toJson(arrayList.get(position).getCustomAttributes());
                campaignBuilder.getListener().onItemClickListener(data);
            }
        } catch (Exception e) {

        }

        try {
            if (arrayList.get(position).getCustomAttributes() != null && !TextUtils.isEmpty(arrayList.get(position).getCustomAttributes().getDeeplink()) &&
                    arrayList.get(position).getCustomAttributes().getDeeplink().equals("3x67AU1") && CommonData.getAttributes().getDeepLinks() != null) {
                DeeplinKData deeplinKData = CommonData.getAttributes().getDeepLinks().get(arrayList.get(position).getCustomAttributes().getDeeplink());
                if (deeplinKData != null && !TextUtils.isEmpty(deeplinKData.getPakageName()) && !TextUtils.isEmpty(deeplinKData.getClassFullPath())) {

                    FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
                    //String label = userData.getBusinessName();
                    String businessName = userData.getBusinessName();
                    long userId = userData.getUserId();
                    String enUserId = userData.getEn_user_id();

                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabel(arrayList.get(position).getTitle());
                    conversation.setLabelId((long) arrayList.get(position).getChannelId());
                    conversation.setDefaultMessage("");
                    conversation.setBusinessName(businessName);
                    conversation.setUserId(userId);
                    conversation.setEnUserId(enUserId);
                    conversation.setOpenChat(true);
                    conversation.setUserName(userData.getFullName());
                    conversation.setIsTimeSet(1);
                    conversation.setChatType(0);
                    conversation.setStatus(1);

                    int skipBot = 0;
                    try {
                        JSONObject jsonObject = objectToJSONObject(arrayList.get(position).getCustomAttributes().getData());
                        if (jsonObject.optInt("skip_bot", 0) == 1) {
                            skipBot = 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Intent notificationIntent = new Intent();
                    if (skipBot == 1) {
                        notificationIntent.putExtra("is_skip_bot", 1);
                    }
                    notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    String packageName = deeplinKData.getPakageName();
                    String className = deeplinKData.getClassFullPath();
                    notificationIntent.setComponent(new ComponentName(packageName, className));
                    startActivity(notificationIntent);

                    if (campaignBuilder != null) {
                        if (campaignBuilder.isCloseActivityOnClick()) {
                            getActivity().finish();
                        } else if (campaignBuilder.isCloseOnlyDeepLink() && !TextUtils.isEmpty(arrayList.get(position).getCustomAttributes().getDeeplink())) {
                            getActivity().finish();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {

    }


    private void getSavedData(boolean hitFetchData) {
        PromotionResponse response = CommonData.getPromotionResponse();
        arrayList = new ArrayList<>();
        if (response != null && response.getData() != null && response.getData().size() > 0) {
            arrayList.addAll(response.getData());
            if (campaignAdapter != null) {
                campaignAdapter.setData(arrayList);
            }
        }
        if (arrayList.size() < 1) {
            refreshLayout.setRefreshing(true);
        }
        if (hitFetchData)
            fetchData();
    }

//    public void fetchDataforSeeMore(int channelId) {
//        startOffset = 0;
//        fetchData(channelId);
//    }

    private void fetchData() {
        fetchData(0);
    }

    private void fetchData(final int channelId) {
        if (isNetworkAvailable()) {
            if (startOffset != 0) {
                isPagingApiInProgress = true;
                campaignAdapter.showPaginationProgressBar(true, true);
            }
            new ApiGetMobileNotification(getActivity(), new NotificationListener() {
                @Override
                public void onSucessListener(PromotionResponse response) {
                    try {

                        if (arrayList == null)
                            arrayList = new ArrayList<>();

                        if (startOffset == 0) {
                            arrayList.clear();
                            CommonData.savePromotionResponse(response);
                        }

                        if (startOffset != 0) {
                            isPagingApiInProgress = false;
                            campaignAdapter.showPaginationProgressBar(false, true);
                        }

                        HashSet<String> unreadDate = CommonData.getAnnouncementCount();
                        if (unreadDate.size() > 0) {
                            HashSet<String> list = new HashSet();
                            for (Data data : response.getData())
                                list.add(String.valueOf(data.getChannelId()));
                            if (list.size() > 0) {
                                unreadDate.removeAll(list);
                                if (HippoConfig.getInstance().getCallbackListener() != null) {
                                    HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(unreadDate.size());
                                }
                                CommonData.setAnnouncementCount(unreadDate);
                            }
                        }

                        arrayList.addAll(response.getData());
//                        if (channelId > 0) {
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).getChannelId() == channelId) {
//                                    arrayList.get(i).setShowMore(2);
//                                    arrayList.get(i).setAddedFromBroadcast(false);
//                                    break;
//                                }
//                            }
//                        }

                        if (arrayList.size() == 0) {
                            llNoNotifications.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.GONE);
                        } else {
                            llNoNotifications.setVisibility(View.GONE);
                            delete.setVisibility(View.VISIBLE);
                        }

                        if (campaignAdapter != null) {
                            campaignAdapter.setData(arrayList);
                        }
                        refreshLayout.setRefreshing(false);
                        if (response.getData().size() == 0) {
                            hasMorePages = false;
                        } else {
                            hasMorePages = true;
                        }
                        //hasMorePages = response.getData().size() == offset;

                        Log.e("SIZE", "TOTAL SIZE = " + arrayList.size());
                        clearNotificationBanners();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureListener() {
                    refreshLayout.setRefreshing(false);
                    if (startOffset != 0) {
                        isPagingApiInProgress = false;
                        campaignAdapter.showPaginationProgressBar(false, true);
                    }
                    if (arrayList.size() == 0) {
                        llNoNotifications.setVisibility(View.VISIBLE);

                    } else {
                        llNoNotifications.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onItemClickListener(String url) {

                }
            }).getNotificationData(startOffset, offset);
        } else {
            String text = Restring.getString(getActivity(), R.string.fugu_not_connected_to_internet);
            ToastUtil.getInstance(getActivity()).showToast(text);
            refreshLayout.setRefreshing(false);
            if (startOffset != 0) {
                isPagingApiInProgress = false;
                campaignAdapter.showPaginationProgressBar(false, true);
            }
        }
    }

    private void deleteAll() {
        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to clear all notifications?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearData(-1, -1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    public void clearData(final long channelId, int position) {
        if (isNetworkAvailable()) {
            new ApiGetMobileNotification(getActivity(), new OnClearNotificationListener() {
                @Override
                public void onSucessListener(int position) {
                    if (position > -1) {
                        campaignAdapter.notifyItemRemoved(position);
                        arrayList.remove(position);
                        String text = Restring.getString(getActivity(), R.string.hippo_notifications_deleted);
                        if (campaignBuilder != null && !TextUtils.isEmpty(campaignBuilder.getDeleteMessage())) {
                            text = campaignBuilder.getDeleteMessage();
                        }
                        ToastUtil.getInstance(getActivity()).showToast(text);
                    } else {
                        arrayList.clear();
                        campaignAdapter.notifyDataSetChanged();
                    }
                    CommonData.savePromotionResponse(new PromotionResponse());

                    if (arrayList.size() == 0) {
                        delete.setVisibility(View.GONE);
                        llNoNotifications.setVisibility(View.VISIBLE);
                        CommonData.setAnnouncementCount(new HashSet<String>());

                    }

                }

                @Override
                public void onFailure() {

                }
            }).clearNotification(channelId, position);
        } else {
            String text = Restring.getString(getActivity(), R.string.fugu_not_connected_to_internet);
            ToastUtil.getInstance(getActivity()).showToast(text);
        }
    }

    private void updatePushData() {

        JSONArray channelIdArray = new JSONArray();

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isAddedFromBroadcast()) {
                channelIdArray.put(arrayList.get(i).getChannelId());
            }
        }

        if (channelIdArray.length() <= 0)
            return;

        CommonParams params = new CommonParams.Builder()
                .add(FuguAppConstant.APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                .add("user_id", HippoConfig.getInstance().getUserData().getUserId())
                .add("channel_ids", channelIdArray)
                .build();
        RestClient.getApiInterface().getAndUpdateAnnouncement(params.getMap()).enqueue(new ResponseResolver<PromotionResponse>() {
            @Override
            public void success(PromotionResponse promotionResponse) {

                Log.e("", ">>>>>>>>>>>>>>>>>>>>>>>>>>");

                for (int i = 0; i < promotionResponse.getData().size(); i++) {
                    for (int j = 0; j < arrayList.size(); j++) {
                        if (promotionResponse.getData().get(i).getChannelId() == arrayList.get(j).getChannelId()) {
                            arrayList.get(j).setDescription(promotionResponse.getData().get(i).getDescription());
                            arrayList.get(i).setShowMore(1);
                            arrayList.get(i).setAddedFromBroadcast(false);
                            break;
                        }
                    }
                }

                HashSet<String> unreadDate = CommonData.getAnnouncementCount();
                if (unreadDate.size() > 0) {
                    HashSet<String> list = new HashSet();
                    for (Data data : arrayList)
                        list.add(String.valueOf(data.getChannelId()));
                    if (list.size() > 0) {
                        unreadDate.removeAll(list);
                        if (HippoConfig.getInstance().getCallbackListener() != null) {
                            HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(unreadDate.size());
                        }
                        CommonData.setAnnouncementCount(unreadDate);
                    }
                }
                campaignAdapter.notifyDataSetChanged();
                clearNotificationBanners();
                setUI();
            }

            @Override
            public void failure(APIError error) {
                setUI();
            }
        });
    }

}

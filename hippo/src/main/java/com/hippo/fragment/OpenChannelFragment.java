package com.hippo.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.HippoConfigAttributes;
import com.hippo.LibApp;
import com.hippo.R;
import com.hippo.activity.BaseFragment;
import com.hippo.activity.FuguChatActivity;
import com.hippo.adapter.FuguChannelsAdapter;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.datastructure.ChannelStatus;
import com.hippo.eventbus.BusProvider;
import com.hippo.eventbus.ConversationEvent;
import com.hippo.eventbus.OnViewUpdate;
import com.hippo.helper.GeneralFunctions;
import com.hippo.langs.Restring;
import com.hippo.model.FuguConversation;
import com.hippo.model.FuguPutUserDetailsResponse;
import com.hippo.receiver.NetworkStatus;
import com.hippo.utils.FloatingActionButtonExpandable;
import com.hippo.utils.Utils;
import com.hippo.utils.filepicker.Util;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-01-26.
 *
 * @author gurmail
 */
public class OpenChannelFragment extends BaseFragment implements FuguAppConstant, SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private RelativeLayout rlRoot;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvChannels;
    private TextView tvNoInternet, tvNewConversation;
    private HippoColorConfig hippoColorConfig;
    private TextView tvPoweredBy;
    private LinearLayout retryLayout;
    private TextView btnRetry;
    private Button noChatBtn;
    private final int IS_HIT_REQUIRED = 200;
    private final String TAG = OpenChannelFragment.class.getSimpleName();
    private FuguChannelsAdapter fuguChannelsAdapter;

    private ArrayList<FuguConversation> openConversationList = new ArrayList<>();
    private ArrayList<FuguConversation> closeConversationList = new ArrayList<>();

    private LinearLayout llNoConversation;
    private TextView noConversationTextView;
    private FloatingActionButtonExpandable createBtn;

    private String label = "";
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private int appVersion = 0;

    private boolean isClosedChannel = false;
    private boolean hasCreateNowBtn = false;
    private boolean showEmptyChatBtn = false;

    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey("isClosedChannel"))
            isClosedChannel = getArguments().getBoolean("isClosedChannel", false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fugu_activity_channel, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
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

    /**
     * Initialize Views
     */
    private void initViews(View view) {

        hippoColorConfig = CommonData.getColorConfig();

        rlRoot = (RelativeLayout) view.findViewById(R.id.rlRoot);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        rvChannels = (RecyclerView) view.findViewById(R.id.rvChannels);
        createBtn = view.findViewById(R.id.createBtn);
        tvNoInternet = (TextView) view.findViewById(R.id.tvNoInternet);
        tvNewConversation = (TextView) view.findViewById(R.id.tvNewConversation);
        btnRetry = (TextView) view.findViewById(R.id.tvStatus);
        retryLayout = (LinearLayout) view.findViewById(R.id.llInternet);
        llNoConversation = view.findViewById(R.id.llNoConversation);
        noConversationTextView = view.findViewById(R.id.noConversationTextView);
        tvPoweredBy = (TextView) view.findViewById(R.id.tvPoweredBy);
        noChatBtn = view.findViewById(R.id.noChatBtn);
        tvPoweredBy.setVisibility(View.GONE);
        tvNoInternet.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);

        swipeRefresh.setColorSchemeResources(R.color.hippo_white);
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.fugu_theme_color_primary);
        swipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);

        createBtn.setContent(Restring.getString(getActivity(), R.string.fugu_create_conversation));
        tvNewConversation.setText(Restring.getString(getActivity(), R.string.fugu_new_conversation));
        noConversationTextView.setText(Restring.getString(getActivity(), R.string.hippo_you_have_no_chats));
        noChatBtn.setText(Restring.getString(getActivity(), R.string.hippo_find_an_expert));

        createBtn.setVisibility(View.GONE);

        if(getAttributes() != null && getAttributes().getAdditionalInfo() != null) {
            if(!TextUtils.isEmpty(getAttributes().getAdditionalInfo().getEmptyChannelList())) {
                noConversationTextView.setText(getAttributes().getAdditionalInfo().getEmptyChannelList());
            }

            if(getAttributes().getAdditionalInfo().isHasCreateNewChat()) {
                createBtn.setVisibility(View.VISIBLE);
                hasCreateNowBtn = true;
            }

            if(getAttributes().getAdditionalInfo().showEmptyChatBtn())
                showEmptyChatBtn = true;

            if(!TextUtils.isEmpty(getAttributes().getAdditionalInfo().getCreateChatBtnText()))
                createBtn.setContent(getAttributes().getAdditionalInfo().getCreateChatBtnText());

            createBtn.setTextColor(hippoColorConfig.getHippoFloatingBtnText());
            createBtn.setBackgroundButtonColor(hippoColorConfig.getHippoFloatingBtnBg());

            createBtn.expand(true);
        }

        configColors();
        if (!isNetworkAvailable()) {
            if(!isNetworkAvailable()) {
                setConnectionMessage(3);
            }
        }

        rvChannels.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(createBtn.getVisibility() == View.VISIBLE) {
                    if (dy > 0) {
                        createBtn.collapse(true);
                    } else {
                        createBtn.expand(true);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isClosedChannel && hasCreateNowBtn) {
                    createBtn.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new ConversationEvent(2));
                try {
                    LibApp.getInstance().trackEvent("List Screen", "Button clicked", "New chat");
                } catch (Exception e) {

                }
            }
        });

        noChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HippoConfig.getInstance().getHippoAdditionalListener() != null) {
                    HippoConfig.getInstance().getHippoAdditionalListener().onNoChatListener();
                }
                BusProvider.getInstance().post(new ConversationEvent(3));
            }
        });

        if(isClosedChannel) {
            createBtn.setVisibility(View.GONE);
        }


        setRecyclerViewData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
                try {
                    if (CommonData.getConversationList().size() > 0) {
                        notifyData((ArrayList<FuguConversation>) CommonData.getConversationList());
                    }
                } catch (Exception e) {
                    if(HippoConfig.DEBUG)
                        e.printStackTrace();
                }
            }
        }, 100);

    }

    /**
     * Config Colors of App
     */
    private void configColors() {
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvNewConversation.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        } else {
            tvNewConversation.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        }
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorSecondary());

    }

    /**
     * Set Recycler Data
     */
    private void setRecyclerViewData() {
        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
        label = userData.getBusinessName();
        businessName = userData.getBusinessName();
        userId = userData.getUserId();
        enUserId = userData.getEn_user_id();
        if(!TextUtils.isEmpty(userData.getFullName()))
            userName = userData.getFullName();

        if(isClosedChannel) {
            fuguChannelsAdapter = new FuguChannelsAdapter(getActivity(), closeConversationList, userName, userId, businessName
                    , new FuguChannelsAdapter.Callback() {
                @Override
                public void onClick(FuguConversation conversation) {
                    Intent chatIntent = new Intent(getActivity(), FuguChatActivity.class);
                    chatIntent.putExtra("is_from_history", false);
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    startActivityForResult(chatIntent, IS_HIT_REQUIRED);
                }
            }, enUserId, false);
        } else {
            fuguChannelsAdapter = new FuguChannelsAdapter(getActivity(), openConversationList, userName, userId, businessName
                    , new FuguChannelsAdapter.Callback() {
                @Override
                public void onClick(FuguConversation conversation) {
                    Intent chatIntent = new Intent(getActivity(), FuguChatActivity.class);
                    chatIntent.putExtra("is_from_history", false);
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    startActivityForResult(chatIntent, IS_HIT_REQUIRED);
                }
            }, enUserId, hasCreateNowBtn);
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChannels.setLayoutManager(layoutManager);
        rvChannels.setAdapter(fuguChannelsAdapter);


        if(hasCreateNowBtn) {
            rvChannels.setPadding(0,Util.dip2px(getContext(), 16),0, Util.dip2px(getContext(), 75));
        } else {
            rvChannels.setPadding(0,Util.dip2px(getContext(), 16),0, 0);
        }
    }


    @Override
    public void onRefresh() {
        BusProvider.getInstance().post(new ConversationEvent(1));
    }

    @Subscribe
    public void onViewUpdate(OnViewUpdate update) {
        switch (update.type) {
            case 1:
                tvNoInternet.setVisibility(View.VISIBLE);
                swipeRefresh.setVisibility(View.GONE);
                tvNewConversation.setVisibility(View.GONE);
                break;
            case 2:
                notifyData(update.fuguConversationList);
                swipeRefresh.setRefreshing(false);
                break;
            case 3:
                swipeRefresh.setRefreshing(false);
                break;
            case 4:
                setRecyclerViewData();
                break;
            default:

                break;
        }
    }

    HippoConfigAttributes attributes;

    private HippoConfigAttributes getAttributes() {
        if(attributes == null)
            attributes = CommonData.getAttributes();
        return attributes;
    }

    private void notifyData(ArrayList<FuguConversation> conversationList) {

        openConversationList.clear();
        closeConversationList.clear();
        if(getAttributes() != null && getAttributes().getAdditionalInfo() != null && getAttributes().getAdditionalInfo().isHasChannelPager()) {
            for(FuguConversation conversation : conversationList) {
                if(isClosedChannel && conversation.getChannelStatus() == ChannelStatus.CLOSED.getOrdinal()) {
                    closeConversationList.add(conversation);
                } else if(!isClosedChannel && conversation.getChannelStatus() != ChannelStatus.CLOSED.getOrdinal()) {
                    openConversationList.add(conversation);
                }
            }
        } else {
            openConversationList.addAll(conversationList);
        }


        fuguChannelsAdapter.notifyDataSetChanged();

        if(isClosedChannel) {
            if(closeConversationList == null || closeConversationList.size() == 0) {
                llNoConversation.setVisibility(View.VISIBLE);
                noConversationTextView.setVisibility(View.VISIBLE);
                noConversationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                noConversationTextView.setText(Restring.getString(getActivity(), R.string.hippo_you_have_no_chats));
            } else {
                llNoConversation.setVisibility(View.GONE);
                noConversationTextView.setVisibility(View.GONE);
            }
        } else {
            if(openConversationList == null || openConversationList.size() == 0) {
                llNoConversation.setVisibility(View.VISIBLE);
                noConversationTextView.setVisibility(View.VISIBLE);
                noConversationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                noConversationTextView.setText(Restring.getString(getActivity(), R.string.hippo_you_have_no_chats));
            } else {
                llNoConversation.setVisibility(View.GONE);
                noConversationTextView.setVisibility(View.GONE);
            }
        }

        if(showEmptyChatBtn && conversationList.size() == 0) {
            noChatBtn.setVisibility(View.VISIBLE);
            noChatBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            noConversationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            noConversationTextView.setText(Restring.getString(getActivity(), R.string.hippo_no_chat_init));
        } else {
            noChatBtn.setVisibility(View.GONE);
            noConversationTextView.setText(Restring.getString(getActivity(), R.string.hippo_you_have_no_chats));
        }
    }

    @Subscribe
    public void onNetworkStatus(NetworkStatus networkStatus) {
        switch (networkStatus.getStatus()) {
            case NOT_CONNECTED:
                setConnectionMessage(3);
                break;
            case CONNECTED_TO_INTERNET:
            case CONNECTED_TO_INTERNET_VIA_WIFI:
                setConnectionMessage(2);
                break;
            default:

                break;
        }
    }

    private void setConnectionMessage(int status) {
        if (isNetworkAvailable()) {
            switch (status) {
                case 0:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 2:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setBackgroundColor(Color.parseColor("#FFA500"));
                            btnRetry.setText(Restring.getString(getActivity(), R.string.fugu_connecting));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    retryLayout.setBackgroundColor(Color.parseColor("#00FF00"));
                                    btnRetry.setText(Restring.getString(getActivity(), R.string.fugu_connected));
                                    retryLayout.setVisibility(View.GONE);
                                }
                            }, 1500);
                        }
                    });
                    break;
                case 3:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            String noNet = Restring.getString(getActivity(), R.string.hippo_no_internet_connected);
                            btnRetry.setText(noNet);
                            retryLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                            //enableButtons();
                        }
                    });
                    break;
                case 6:
                    retryLayout.setVisibility(View.VISIBLE);
                    retryLayout.setBackgroundColor(Color.parseColor("#FBE799"));
                    new GeneralFunctions().spannableRetryText(btnRetry, Restring.getString(getActivity(), R.string.error_msg_yellow_bar), Restring.getString(getActivity(), R.string.hippo_tap_to_retry));
                    retryLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //api hit
                                }
                            }, 1000);
                        }
                    });
                    break;
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    retryLayout.setVisibility(View.VISIBLE);
                    String noNet = Restring.getString(getActivity(), R.string.hippo_no_internet_connected);
                    btnRetry.setText(noNet);
                    retryLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                    //enableButtons();
                }
            });
        }
    }
}

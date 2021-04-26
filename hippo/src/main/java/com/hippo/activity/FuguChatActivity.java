package com.hippo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.*;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.*;
import com.hippo.adapter.*;
import com.hippo.apis.ApiPutUserDetails;
import com.hippo.apis.MessageUpdate;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.datastructure.ChannelStatus;
import com.hippo.datastructure.ChatType;
import com.hippo.eventbus.BusProvider;
import com.hippo.fragment.AttachmentSheetFragment;
import com.hippo.fragment.BottomSheetPopup;
import com.hippo.helper.BusEvents;
import com.hippo.helper.FayeMessage;
import com.hippo.helper.GeneralFunctions;
import com.hippo.helper.P2pUnreadCount;
import com.hippo.tickets.AttachmentSelectedTicketListener;
import com.hippo.interfaces.OnMessageUpdate;
import com.hippo.langs.Restring;
import com.hippo.model.*;
import com.hippo.model.Message;
import com.hippo.model.labelResponse.GetLabelMessageResponse;
import com.hippo.model.labelResponse.LabelData;
import com.hippo.model.labelResponse.LabelMessage;
import com.hippo.model.payment.AddedPaymentGateway;
import com.hippo.payment.RazorPayData;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.CommonResponse;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.tickets.DataFormTicketAdapter;
import com.hippo.utils.*;
import com.hippo.utils.beatAnimation.AVLoadingIndicatorView;
import com.hippo.utils.easypermissions.AfterPermissionGranted;
import com.hippo.utils.easypermissions.AppSettingsDialog;
import com.hippo.utils.easypermissions.EasyPermissions;
import com.hippo.utils.fileUpload.FileManager;
import com.hippo.utils.fileUpload.FileuploadModel;
import com.hippo.utils.fileUpload.Prefs;
import com.hippo.utils.filepicker.*;
import com.hippo.utils.filepicker.activity.AudioPickActivity;
import com.hippo.utils.filepicker.activity.ImagePickActivity;
import com.hippo.utils.filepicker.activity.NormalFilePickActivity;
import com.hippo.utils.filepicker.activity.VideoPickActivity;
import com.hippo.utils.filepicker.filter.entity.AudioFile;
import com.hippo.utils.filepicker.filter.entity.ImageFile;
import com.hippo.utils.filepicker.filter.entity.NormalFile;
import com.hippo.utils.filepicker.filter.entity.VideoFile;
import com.hippo.utils.loadingBox.LoadingBox;
import com.hippo.utils.loadingBox.ProgressWheel;
import com.hippo.utils.zoomview.ZoomageView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultWithDataListener;
import com.squareup.otto.Subscribe;

import faye.ConnectionManager;
import io.paperdb.Paper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Build.VERSION.SDK_INT;
import static com.hippo.utils.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.hippo.utils.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;
import static com.hippo.utils.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;
import static com.hippo.utils.filepicker.activity.ImagePickActivity.IS_NEED_IMAGE_PAGER;

public class FuguChatActivity extends FuguBaseActivity implements Animation.AnimationListener,
        FuguMessageAdapter.OnRatingListener, QuickReplyAdapaterActivityCallback, FuguMessageAdapter.OnUserConcent,
        KeyboardUtil.SoftKeyboardToggleListener, EasyPermissions.PermissionCallbacks, ListItem,
        FuguMessageAdapter.AdapterCallback, PaymentDialogFragment.OnInputListener, PaymentResultWithDataListener {

    private String TAG = getClass().getSimpleName();
    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    // Initial FayeClient

    // Declaring Views
    private RelativeLayout rlRoot;
    private CustomLinear llRoot;

    //private LinearLayout cvTypeMessage;
    private RelativeLayout llMessageLayout;


    private TextView tvClosed;
    private TextView tvNoInternet;
    public EditText etMsg;
    private RecyclerView rvMessages;
    private FuguMessageAdapter fuguMessageAdapter;
    //    private Toolbar myToolbar;
    private AVLoadingIndicatorView aviTyping;
    private LinearLayout llTyping;
    private ProgressBar pbLoading;
    private FuguConversation conversation;

    private ProgressBar pbSendingImage;
    //private RelativeLayout ivSend;
    private ImageView ivSendBtn;
    public ImageView ivAttachment;
    private ImageView ivCancelEdit;

    private boolean isNetworkStateChanged = false;
    private boolean isFayeChannelActive = false;
    private boolean firstTime = true;
    private boolean isFirst = true;
    private Animation animSlideUp, animSlideDown;
    private ProgressWheel pbPeerChat;
    private FuguGetMessageResponse mFuguGetMessageResponse;
    private GetLabelMessageResponse labelMessageResponse;

    private String AgentName = "";
    private String sentAtUTC = "";
    private Long channelId = -1L;
    public static Long currentChannelId = -1L;
    private Long agentId = -1L;
    private Long userId = -1L;
    private String enUserId = "";
    private Long labelId = -1L;
    private String userName = "";
    private int isTyping = TYPING_SHOW_MESSAGE;
    private String label = "";
    private String defaultMessage = "";
    private String businessName = "";
    private int status;
    private boolean isConversationCreated;
    private FuguImageUtils fuguImageUtils;
    private int onSubscribe = CHANNEL_UNSUBSCRIBED;
    private boolean showLoading = true;
    private boolean allMessagesFetched = false;
    private DateUtils dateUtils;
    private int pageStart = 1, position;
    private int dateItemCount = 0;
    private HippoColorConfig hippoColorConfig;
    private CustomLinearLayoutManager layoutManager;

    private boolean isP2P = false;
    private FuguCreateConversationParams fuguCreateConversationParams;
    private int previousPos = 0;
    private boolean runAnim = true, runAnim2 = false;
    private Handler handler = null;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private TextView tvDateLabel;
    private HashMap<String, Long> transactionIdsMap;
    private String globalUuid;

    private boolean hasFormValue;

    @NonNull
    private ArrayList<Message> fuguMessageList = new ArrayList<>();
    private LinkedHashMap<String, Message> sentMessages = new LinkedHashMap<>();
    private LinkedHashMap<String, Message> unsentMessages = new LinkedHashMap<>();
    @NonNull
    private LinkedHashMap<String, JSONObject> unsentMessageMapNew = new LinkedHashMap<>();
    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";
    private LinearLayout retryLayout;
    private TextView btnRetry;
    private ProgressWheel progressWheel;
    private int messageIndex = -1;
    private ImageView ivVideoView, ivAudioView;
    private boolean keyboardVisibility;
    public String audioMuid = "";
    public int playingItem = -1;

    public int timeLeft = 0;
    private int messageChatType = 0;

    private RelativeLayout rootToolbar;
    private ImageView ivBackBtn;
    private View blankView;
    private TextView tvToolbarName, isTypingView;
    private ImageView userImageIcon;
    private ImageView ivHistoryView;

    private LinearLayout container;
    private boolean isFromHistory;

    boolean hideTopBar = false;
    private LinearLayout mainBg;
    private boolean createDefaultChat = false;
    private int skipBot = 0;
    private boolean skipCreateChannel = true;
    private boolean singleChatTransId;

    // for suggestions
    private RecyclerView rvSuggestions;
    private SuggestionAdapter suggestionAdapter;
    private boolean isSuggestionNeeded = false;
    private boolean attachemtFromFormTicketBot = false;
    private AttachmentSelectedTicketListener attachmentSelectedTicketListener;
    private int attachmentItemPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_chat);

        try {
            LibApp.getInstance().screenOpened("Chat Screen");
        } catch (Exception e) {
        }

        HippoLog.e("TAG", "chat screen opened");
        isFirstTimeOpened = true;
        isFromHistory = getIntent().getBooleanExtra("is_from_history", false);
        insertBotId = getIntent().getBooleanExtra("isInsertBotId", false);
        if (getIntent().hasExtra("is_skip_bot"))
            skipBot = getIntent().getIntExtra("is_skip_bot", 0);

        if (getIntent().hasExtra("skipCreateChannel"))
            skipCreateChannel = getIntent().getBooleanExtra("skipCreateChannel", true);

        if (getIntent().hasExtra("single_chat_trans_id"))
            singleChatTransId = getIntent().getBooleanExtra("single_chat_trans_id", false);

        container = findViewById(R.id.container);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverChat, new IntentFilter(NOTIFICATION_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(fileUploadReceiver, new IntentFilter(FuguAppConstant.HIPPO_FILE_UPLOAD));
        initViews();

        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    hideKeyboard(FuguChatActivity.this);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            handleOnCreate();
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            fetchIntentData();
            if (!getIntent().getBooleanExtra("is_from_history", false)) {
                getClient();
            } else {
                setUpUI();
                stateChangeListeners();
                LocalBroadcastManager.getInstance(FuguChatActivity.this).registerReceiver(mMessageReceiver, getIntentFilter());

            }
        }
        CommonData.clearPushChannel();
        try {
            remainingTime();
        } catch (Exception e) {

        }
        checkAutoUpload();

        if (!isNetworkAvailable()) {
            setConnectionMessage(3);
        }
    }

    @Subscribe
    public void onFayeMessage(FayeMessage events) {
        if (events.type.equalsIgnoreCase(BusEvents.CONNECTED_SERVER.toString())) {
            onConnectedServer();
        } else if (events.type.equalsIgnoreCase(BusEvents.RECEIVED_MESSAGE.toString())) {
            onReceivedMessage(events.message, events.channelId);
        } else if (events.type.equalsIgnoreCase(BusEvents.PONG_RECEIVED.toString())) {
            onPongReceived();
        } else if (events.type.equalsIgnoreCase(BusEvents.DISCONNECTED_SERVER.toString())) {

        } else if (events.type.equalsIgnoreCase(BusEvents.ERROR_RECEIVED.toString())) {
            onErrorReceived(events.message, events.channelId);
        } else if (events.type.equalsIgnoreCase(BusEvents.WEBSOCKET_ERROR.toString())) {
            onWebSocketError();
        } else if (events.type.equalsIgnoreCase(BusEvents.NOT_CONNECTED.toString())) {
            // TODO: 2020-04-27 show error in faye connection.
        }
    }

    private void handleOnCreate() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    fetchIntentData();
                    if (!getIntent().getBooleanExtra("is_from_history", false)) {
                        getClient();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpUI();
                                stateChangeListeners();
                                LocalBroadcastManager.getInstance(FuguChatActivity.this).registerReceiver(mMessageReceiver, getIntentFilter());
                            }
                        });
                    }
                } catch (Exception e) {

                }
            }
        });

        thread.start();
    }

    private void getClient() {
        ConnectionManager.INSTANCE.initFayeConnection();
        onConnectedServer();
        aftergettingClient();
    }

    private void aftergettingClient() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpFayeConnection();
                setUpUI();
                stateChangeListeners();
                LocalBroadcastManager.getInstance(FuguChatActivity.this).registerReceiver(mMessageReceiver, getIntentFilter());
            }
        });

    }

    private void updateUnreadCount(Long channelId, Long labelId) {
        if (HippoConfig.getInstance().getCallbackListener() == null) {
            return;
        }
        if (channelId < 1)
            new UnreadCountPush().execute(labelId, -1L);
        else
            new UnreadCountPush().execute(channelId, -1L);
        /*ArrayList<UnreadCountModel> unreadCountModels = CommonData.getUnreadCountModel();
        if (unreadCountModels.size() == 0)
            return;
        int index = -1;
        if (channelId > 0) {
            index = unreadCountModels.indexOf(new UnreadCountModel(channelId));
        } else if (labelId > 0) {
            for (int i = 0; i < unreadCountModels.size(); i++) {
                if (unreadCountModels.get(i).getLabelId().compareTo(labelId) == 0) {
                    index = i;
                    break;
                }
            }
        }
        HippoLog.v(TAG, "index = " + index);
        HippoLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
        if (index > -1)
            unreadCountModels.remove(index);

        HippoLog.v(TAG, "unreadCountModels = " + new Gson().toJson(unreadCountModels));
        HippoLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
        CommonData.setUnreadCount(unreadCountModels);

        int count = 0;
        for (int i = 0; i < unreadCountModels.size(); i++) {
            count = count + unreadCountModels.get(i).getCount();
            HippoLog.v(TAG, i + " count = " + unreadCountModels.get(i).getCount());
        }

        HippoLog.v(TAG, "count = " + count);

        if (HippoConfig.getInstance().getCallbackListener() != null) {
            HippoConfig.getInstance().getCallbackListener().count(count);
        }*/
    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        intent.addAction(NOTIFICATION_TAPPED);
        return intent;
    }

    public void applyThemeToDrawable(int color, int drawableId, ImageView imageView) {
        final Resources.Theme theme = getResources().newTheme();
        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, theme);
        if (drawable != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(porterDuffColorFilter);
        }
        imageView.setImageDrawable(drawable);
    }

    private void initViews() {
        transactionIdsMap = CommonData.getTransactionIdsMap();
        hippoColorConfig = CommonData.getColorConfig();
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        llRoot = (CustomLinear) findViewById(R.id.llRoot);
        tvDateLabel = (TextView) findViewById(R.id.tvDateLabel);
        tvClosed = (TextView) findViewById(R.id.tvClosed);
        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        tvClosed.setText(Restring.getString(FuguChatActivity.this, R.string.hippo_conversation_closed));
        tvNoInternet.setText(Restring.getString(FuguChatActivity.this, R.string.hippo_something_wrong));

        aviTyping = (AVLoadingIndicatorView) findViewById(R.id.aviTyping);
        llTyping = (LinearLayout) findViewById(R.id.llTyping);
        etMsg = (EditText) findViewById(R.id.etMsg);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbSendingImage = (ProgressBar) findViewById(R.id.pbSendingImage);
        ivSendBtn = findViewById(R.id.ivSendBtn);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        llMessageLayout = (RelativeLayout) findViewById(R.id.llMessageLayout);
        fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);
        dateUtils = DateUtils.getInstance();
        pbPeerChat = findViewById(R.id.pbPeerChat);
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        mainBg = findViewById(R.id.main_bg);

        rvSuggestions = (RecyclerView) findViewById(R.id.rvSuggestions);
        rvSuggestions.setVisibility(View.GONE);

        ivAttachment = findViewById(R.id.ivAttachment);
        ivCancelEdit = findViewById(R.id.iv_cancel_btn);
        etMsg.setHint(Restring.getString(FuguChatActivity.this, R.string.fugu_send_message));
        try {
            ivSendBtn.getBackground().setColorFilter(hippoColorConfig.getHippoSendBtnBg(), PorterDuff.Mode.SRC_OVER);
            applyThemeToDrawable(hippoColorConfig.getHippoSendBtnBg(), R.drawable.fugu_ic_add_attachment, ivAttachment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEditing();
            }
        });

//        suggestionAdapter = new SuggestionAdapter(new ArrayList(), new SuggestionAdapter.OnSuggestionClickListener() {
//            @Override
//            public void onClicked(String value) {
//                sendMessage(value, TEXT_MESSAGE, "", "", null, null, null);
//            }
//        });
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rvSuggestions.setLayoutManager(manager);
//        rvSuggestions.setAdapter(suggestionAdapter);


//        RecyclerView.ItemAnimator animator = rvMessages.getItemAnimator();
//        if (animator instanceof SimpleItemAnimator) {
//            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//        }

        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setText(Restring.getString(FuguChatActivity.this, R.string.hippo_tap_to_retry));
        retryLayout = findViewById(R.id.retry_layout);
        progressWheel = findViewById(R.id.retry_loader);

        rootToolbar = findViewById(R.id.my_toolbar);
        userImageIcon = findViewById(R.id.user_image);
        userImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
        tvToolbarName = findViewById(R.id.tv_toolbar_name);

        ivBackBtn = findViewById(R.id.ivBackBtn);
        ivVideoView = findViewById(R.id.ivVideoView);
        ivAudioView = findViewById(R.id.ivAudioView);

        tvToolbarName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });


        isTypingView = findViewById(R.id.is_typing);
        blankView = findViewById(R.id.blankView);
        ivHistoryView = findViewById(R.id.ivHistoryView);

        isTypingView.setText(Restring.getString(FuguChatActivity.this, R.string.hippo_is_typing));

        ivHistoryView.setVisibility(View.GONE);

        ivAudioView.setImageResource(R.drawable.hippo_ic_call_black);
        ivVideoView.setImageResource(R.drawable.hippo_ic_videocam);

        if (HippoConfig.getInstance().getChatScreenBg() != -1) {
            mainBg.setBackgroundResource(HippoConfig.getInstance().getChatScreenBg());
        }

        if (HippoConfig.getInstance().getHomeUpIndicatorDrawableId() != -1) {
            ivBackBtn.setImageResource(HippoConfig.getInstance().getHomeUpIndicatorDrawableId());
        } else {
            ivBackBtn.getDrawable().setColorFilter(CommonData.getColorConfig().getHippoActionBarText(), PorterDuff.Mode.SRC_ATOP);
        }

        if (!CommonData.isBackBtn()) {
            blankView.setVisibility(View.VISIBLE);
            ivBackBtn.setVisibility(View.GONE);
            ivHistoryView.setVisibility(View.VISIBLE);
        }

        setTextSize(tvToolbarName, 20);
        setTextSize(etMsg, 18);
        setTextSize(isTypingView, 12);

        isDisableReply = false;
        if (isFromHistory) {
            ivHistoryView.setVisibility(View.GONE);
            blankView.setVisibility(View.GONE);
            ivBackBtn.setVisibility(View.VISIBLE);
            llMessageLayout.setVisibility(View.GONE);
            isDisableReply = true;
        }

        /*ivAudioView.setImageResource(HippoConfig.getInstance().getAudioCallDrawableId() == -1
                ? R.drawable.hippo_ic_call_black : HippoConfig.getInstance().getAudioCallDrawableId());
        if(HippoConfig.getInstance().getAudioCallDrawableId() == -1) {
            ivAudioView.getDrawable().setColorFilter(hippoColorConfig.getHippoAudioCallBg(), PorterDuff.Mode.SRC_IN);
        }

        ivVideoView.setImageResource(HippoConfig.getInstance().getVideoCallDrawableId() == -1
                ? R.drawable.hippo_ic_videocam : HippoConfig.getInstance().getVideoCallDrawableId());
        if(HippoConfig.getInstance().getVideoCallDrawableId() == -1) {
            ivVideoView.getDrawable().setColorFilter(hippoColorConfig.getHippoVideoCallBg(), PorterDuff.Mode.SRC_IN);
        }*/

        ivAudioView.setVisibility(View.GONE);
        ivVideoView.setVisibility(View.GONE);
        configColors();
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_up_time);
        animSlideUp.setAnimationListener(this);

        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_down_time);
        animSlideDown.setAnimationListener(this);

    }

    public void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    private void openProfile() {
        try {
            boolean canShowImage = CommonData.getAttributes().getAdditionalInfo() == null
                    ? false : CommonData.getAttributes().getAdditionalInfo().canShowAgentImage();
            if (channelId > 0 && canShowImage) {
                FuguGetMessageResponse fuguGetMessageResponse = mFuguGetMessageResponse;
                if (fuguGetMessageResponse == null) {
                    fuguGetMessageResponse = CommonData.getSingleAgentData(channelId);
                }
                if (fuguGetMessageResponse.getData().getAgentId() != null
                        && fuguGetMessageResponse.getData().getAgentId().intValue() > 0) {
                    agentId = fuguGetMessageResponse.getData().getAgentId();
                }

                if (agentId > 0) {
                    HippoUserProfileModel profileModel = new HippoUserProfileModel(channelImageUrl, enUserId, channelId, tvToolbarName.getText().toString());
                    profileModel.setUserId(String.valueOf(agentId));
                    Intent intent = new Intent(FuguChatActivity.this, ProfileActivity.class);
                    intent.putExtra("profileModel", profileModel);
                    startActivityForResult(intent, Constant.REQUEST_CODE_IMAGE_VIEW);

                    try {
                        LibApp.getInstance().trackEvent("Chat Screen", "Image Opened", "");
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configColors() {
        //rlRoot.setBackgroundColor(hippoColorConfig.getHippoChatBg());
        GradientDrawable drawable = (GradientDrawable) llTyping.getBackground();
        drawable.setColor(hippoColorConfig.getHippoBgMessageFrom());
        drawable.setStroke((int) getResources().getDimension(R.dimen.fugu_border_width), hippoColorConfig.getHippoBorderColor()); // set stroke width and stroke color
        aviTyping.setIndicatorColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
        tvClosed.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());
        tvClosed.getBackground().setColorFilter(hippoColorConfig.getHippoChannelItemBg(), PorterDuff.Mode.SRC_ATOP);
        //cvTypeMessage.getBackground().setColorFilter(hippoColorConfig.getHippoTypeMessageBg(), PorterDuff.Mode.SRC_ATOP);
        etMsg.setHintTextColor(hippoColorConfig.getHippoTypeMessageHint());
        etMsg.setTextColor(hippoColorConfig.getHippoTypeMessageText());
        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());

        rootToolbar.setBackgroundColor(hippoColorConfig.getHippoActionBarBg());
        tvToolbarName.setTextColor(hippoColorConfig.getHippoActionBarText());
        isTypingView.setTextColor(hippoColorConfig.getHippoActionBarText());

//        GradientDrawable bgShape = (GradientDrawable) ivSendBtn.getBackground();
//        bgShape.setColor(hippoColorConfig.getHippoSendBtnBg());


        ivSendBtn.setAlpha(0.4f);


        /*try {
            if(HippoConfig.getInstance().getIcSend() != -1)
                ivSend.setImageResource(HippoConfig.getInstance().getIcSend());
//            int color = Color.parseColor("#000000");
//            ivSend.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private BroadcastReceiver mMessageReceiverChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));
                HippoLog.d("receiver", "Got message: " + messageJson.toString());
                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    CommonData.setIsNewchat(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver nullListenerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", 0);
            setListener(status);
        }
    };

    private void setListener(int status) {
        HippoLog.e(TAG, "Listener added");
        //mClient.setListener(this);
        switch (status) {
            case 1:
                onPongReceived();
                break;
            case 2:
                onWebSocketError();
                break;
            default:

                break;
        }

    }

    private void fetchIntentData() {

        conversation = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);
        if (conversation.getUnreadCount() > 0) {
            rvMessages.setAlpha(0);
        }
        if (conversation.getChatType() == ChatType.P2P.getOrdinal()) {
            isSuggestionNeeded = true;
        }
        getUnreadCount();
        HippoLog.d("userName in SDK", "FuguChatActivity onCreate " + conversation.getUserName());
        int chatType = getIntent().getIntExtra(CHAT_TYPE, ChatType.P2P.getOrdinal());
        if (!TextUtils.isEmpty(conversation.getLabel())) {
            label = conversation.getLabel();
        } else {
            label = conversation.getBusinessName();
        }
        if (conversation.getLabelId() != null) {
            labelId = conversation.getLabelId();
            /*try {
                if(channelId == -1) {
                    Long channelId = CommonData.getChannelId(labelId);
                    if(channelId != null && channelId > 0) {
                        conversation.setChannelId(channelId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }


        messageChatType = conversation.getChatType();
        channelId = conversation.getChannelId();
        updateUnreadCount(channelId, labelId);
        HippoNotificationConfig.pushLabelId = labelId;
        HippoNotificationConfig.pushChannelId = conversation.getChannelId();
        currentChannelId = conversation.getChannelId();
        HippoLog.e(TAG, "==" + HippoNotificationConfig.pushChannelId);

        userId = conversation.getUserId();
        enUserId = conversation.getEnUserId();
        userName = StringUtil.toCamelCase(conversation.getUserName());
        if (TextUtils.isEmpty(userName)) {
            userName = StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName());
        }
        try {
            if (userName.equalsIgnoreCase("Anonymous") && !TextUtils.isEmpty(HippoConfig.getInstance().getUserData().getFullName())) {
                userName = HippoConfig.getInstance().getUserData().getFullName();
            }
        } catch (Exception e) {
            userName = "";
        }

        status = conversation.getStatus();
        defaultMessage = conversation.getDefaultMessage();
        businessName = conversation.getBusinessName();


        HippoLog.v("is p2p chat", "---> " + isP2P);

        if (chatType == ChatType.GROUP_CHAT.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
        } else if (chatType == ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
            label = "";
        } else {
            fuguCreateConversationParams = new FuguCreateConversationParams(HippoConfig.getInstance().getAppKey(), labelId,
                    enUserId);
        }

        HippoLog.d(TAG, "fuguCreateConversationParams 1 = " + new Gson().toJson(fuguCreateConversationParams));

        mFuguGetMessageResponse = CommonData.getSingleAgentData(channelId);
        try {
            checkValidMsg = mFuguGetMessageResponse.getData().getRestrictPersonalInfo();
        } catch (Exception e) {

        }

        setToolbar();
    }

    private void setToolbar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFuguGetMessageResponse != null && channelId.intValue() > 0)
                    setToolbar(label);
                else
                    setToolbar(label, conversation.getChannelImage());

                isDisableReply = false;
                if (conversation.isDisableReply() || (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null
                        && mFuguGetMessageResponse.getData().isDisableReply())) {
                    llMessageLayout.setVisibility(View.GONE);
                    isDisableReply = true;
                }
            }
        });
    }

    private void setToolbar(String title) {
        String userImage = "";

        try {
            userImage = mFuguGetMessageResponse.getData().getChannelImageUrl();
        } catch (Exception e) {

        }
        HippoLog.e(TAG, "in setToolbar ~~~~~~~~~~~> " + true);
        setToolbar(title, userImage);
    }

    String channelImageUrl = "";

    @Override
    public boolean isFinishing() {
        return super.isFinishing();
    }

    private void setToolbar(String title, String userImage) {

        try {
            HippoLog.e(TAG, "userImage ~~~~~~~~~~~> " + userImage);
            if (!this.isFinishing()) {
                tvToolbarName.setText(title);
                channelImageUrl = userImage;
                if (messageChatType != 1) {

                    int color1 = hippoColorConfig.getHippoActionBarText();//generator.getColor(title.toUpperCase());
                    int textColor1 = hippoColorConfig.getHippoActionBarBg();
                    Resources r1 = getResources();
                    float px1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r1.getDisplayMetrics());
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, r1.getDisplayMetrics());

                    String name1 = title.trim();
                    char text1 = ' ';
                    if (!TextUtils.isEmpty(name1)) {
                        text1 = name1.charAt(0);
                    }

                    TextDrawable drawable1 = TextDrawable.builder()
                            .beginConfig().textColor(textColor1)
                            .fontSize(size)
                            .endConfig()
                            .buildRoundRect((text1 + "").toUpperCase(), color1, Math.round(px1));

                    int roundingRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.2f, r1.getDisplayMetrics());
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transform(new CenterCrop(), new RoundedCorners(roundingRadius))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(drawable1)
                            .error(drawable1);

                    if (!TextUtils.isEmpty(userImage)) {
                        Glide.with(this).asBitmap()
                                .apply(options)
                                .load(userImage)
                                .into(userImageIcon);
                    } else {
                        Resources r = getResources();
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

                        String name = title.trim();
                        name = name.toUpperCase();
                        char text = ' ';
                        if (!TextUtils.isEmpty(name)) {
                            text = name.charAt(0);
                        }

                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        int color = generator.getColor(text);
                        Log.w("color", text + " color = " + color);
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRoundRect((text + "").toUpperCase(), color, Math.round(px));

                        userImageIcon.setImageDrawable(drawable);
                    }
                } else {
                    userImageIcon.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    private boolean getView() {
        boolean hasReplyAll = false;
        try {
            hasReplyAll = CommonData.getAttributes().getAdditionalInfo().isReplyOnDiable();
        } catch (Exception e) {

        }
        return (llMessageLayout.getVisibility() == View.VISIBLE || hasReplyAll);
    }

    private void setRecyclerViewData1() {
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        boolean isSourceMessageEnabled = false;
        String botImage = "";

        try {
            if (CommonData.getUserDetails() != null && CommonData.getUserDetails().getData() != null) {
                botImage = CommonData.getUserDetails().getData().getBotImageUrl();
                if (CommonData.getUserDetails().getData().isMessageSourceEnabled())
                    isSourceMessageEnabled = true;
            }
        } catch (Exception e) {

        }

        HippoLog.e("TAG", "chatType >~~~~~~~~~~> " + conversation.getChatType());
        fuguMessageAdapter = new FuguMessageAdapter(FuguChatActivity.this, fuguMessageList, rvMessages,
                labelId, conversation, this, this,
                getSupportFragmentManager(), this, isSourceMessageEnabled, botImage, messageChatType);
        if (mFuguGetMessageResponse != null) {
            setAgentName();
        }

        fuguMessageAdapter.setHasStableIds(true);

        layoutManager = new CustomLinearLayoutManager(FuguChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setHasFixedSize(false);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(fuguMessageAdapter);
        rvMessages.setItemAnimator(null);

        RecyclerView.ItemAnimator animator = rvMessages.getItemAnimator();

        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        setRecyclerViewData();
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        runAnim2 = false;
                        if (handler == null) {
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!runAnim2) {
                                        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                                R.anim.fugu_slide_up_time);
//                                        tvDateLabel.startAnimation(animSlideUp);
//                                        tvDateLabel.setVisibility(View.INVISIBLE);
                                        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                runAnim = true;
                                                handler = null;
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    } else {
                                        handler = null;
                                    }
                                }
                            }, 1200);
                        }


                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        HippoLog.d(TAG, "scroll down triggered");
//                        tvDateLabel.clearAnimation();
                        runAnim2 = true;
                        if (runAnim) {
//                            tvDateLabel.setVisibility(View.VISIBLE);
//                            tvDateLabel.clearAnimation();
                            animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.fugu_slide_down_time);
//                            tvDateLabel.startAnimation(animSlideDown);
                            animSlideDown.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    runAnim = false;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            break;
                        }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = layoutManager.findFirstVisibleItemPosition();

                /*if (previousPos > position && fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ITEM_TYPE_SELF || fuguMessageList.get(position).getType() == ITEM_TYPE_OTHER) {
                        if (!TextUtils.isEmpty(fuguMessageList.get(position).getSentAtUtc())) {
                            tvDateLabel.setText(DateUtils.getDate(dateUtils.convertToLocal(fuguMessageList.get(position).getSentAtUtc())));
                        }
                    }
                } else if (fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == TYPE_HEADER) {
                        String date = DateUtils.getInstance().getDate(fuguMessageList.get(position).getDate());
                        tvDateLabel.setText(date);
                    }
                }*/
                if (position == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (isNetworkAvailable()) {
                        pbLoading.setVisibility(View.VISIBLE);
                        getMessages(null);
                    }
                }
                previousPos = position;
            }
        });
    }

    private void setUpUI() {
        allMessagesFetched = false;
        setRecyclerViewData1();
        if (channelId.compareTo(-1L) == 0 && labelId.compareTo(-1L) != 0 && !conversation.isOpenChat()) {
            globalUuid = UUID.randomUUID().toString();
            Message message = new Message(businessName,
                    -1L,
                    defaultMessage,
                    "",
                    false,
                    onSubscribe == CHANNEL_SUBSCRIBED ? MESSAGE_READ : MESSAGE_UNSENT,
                    0,
                    TEXT_MESSAGE, globalUuid);

            message.setUserType(1);
            message.setOriginalMessageType(TEXT_MESSAGE);
            message.setMessageType(ITEM_TYPE_OTHER);

            fuguMessageList.add(message);
        }

        if (isP2P) {
            HippoLog.v("call createConversation", "setUpUI");
            transactionIdsMap = new HashMap<>();
            if (CommonData.getTransactionIdsMap() != null) {
                transactionIdsMap = CommonData.getTransactionIdsMap();
            }
            String transactionId = fuguCreateConversationParams.getTransactionId();
            try {
                if (fuguCreateConversationParams != null
                        && fuguCreateConversationParams.getOtherUserUniqueKeys() != null
                        && fuguCreateConversationParams.getOtherUserUniqueKeys().size() > 0
                ) {
                    String uniqueId = fuguCreateConversationParams.getOtherUserUniqueKeys().get(0).getAsString();
                    if (!TextUtils.isEmpty(uniqueId))
                        transactionId = transactionId + uniqueId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            HippoLog.i("transactionId", "transactionId = " + transactionId);
            if (!singleChatTransId && !transactionIdsMap.isEmpty()
                    && !TextUtils.isEmpty(fuguCreateConversationParams.getTransactionId())
                    && transactionIdsMap.get(transactionId) != null) {
                channelId = transactionIdsMap.get(transactionId);
                pbLoading.setVisibility(View.GONE);
                pbPeerChat.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.VISIBLE);
                getSavedMessages(channelId);
                isDisableReply = false;
            } else {
                pbPeerChat.setVisibility(View.VISIBLE);
                pbPeerChat.spin();
                llMessageLayout.setVisibility(View.GONE);
                isDisableReply = true;
            }

            CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                    .setMessageType(TEXT_MESSAGE)
                    .setText(etMsg.getText().toString().trim())
                    .setIsP2P(isP2P)
                    .setFuguGetMessageResponse(mFuguGetMessageResponse)
                    .setGetLabelMessageResponse(labelMessageResponse)
                    .setConversationParams(fuguCreateConversationParams)
                    .build();

            if (skipCreateChannel) {
                createConversation(attribute);
            } else {
                if (!TextUtils.isEmpty(fuguCreateConversationParams.getChannelName()))
                    setToolbar(fuguCreateConversationParams.getChannelName());
                pbLoading.setVisibility(View.GONE);
                pbPeerChat.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.VISIBLE);
                isDisableReply = false;
            }
            pbSendingImage.setVisibility(View.GONE);
        } else if (conversation.getChannelId() != null && conversation.getChannelId() > 0) {
            getSavedMessages(conversation.getChannelId());
            llRoot.setVisibility(View.VISIBLE);
            try {
                if (CommonData.getMessageResponse(conversation.getChannelId()) != null) {
                    label = CommonData.getLabelIdResponse(conversation.getChannelId()).getData().getLabel();
                    setToolbar(label);
                }
            } catch (Exception e) {

            }
            isDisableReply = false;
            if (status == 0) {
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
                isDisableReply = true;
            } else {
                getMessages(null);
            }
        } else if (conversation.getLabelId() != null && conversation.getLabelId() > 0) {
            //channelId = CommonData.getChannelId(conversation.getLabelId());
            getSavedMessages(-1l);
            getByLabelId();
        } else {
            setRecyclerViewData();
            llRoot.setVisibility(View.VISIBLE);

            isDisableReply = false;
            if (status == 0) {
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
                isDisableReply = true;
            }
        }
    }

    private void getSavedMessages(Long channelId) {
        showLoading = false;
        //LoadingBox.showOn(this);
        sentMessages = new LinkedHashMap<>();
        unsentMessages = new LinkedHashMap<>();
        fuguMessageList.clear();
        dateItemCount = 0;
        sentAtUTC = "";

        if (channelId > 0 && CommonData.getSentMessageByChannel(channelId) != null) {
            sentMessages = CommonData.getSentMessageByChannel(channelId);
            fuguMessageList.addAll(sentMessages.values());
        }
        List<String> reverseOrderedKeys = new ArrayList<String>(sentMessages.keySet());
        Collections.reverse(reverseOrderedKeys);
        sentAtUTC = "";
        for (String key : reverseOrderedKeys) {
            if (sentMessages.get(key).isDateView()) {
                sentAtUTC = key;
                break;
            }
        }

        if (channelId > 0 && CommonData.getUnsentMessageMapByChannel(channelId) != null) {
            unsentMessageMapNew = CommonData.getUnsentMessageMapByChannel(channelId);
        }

        if (channelId > 0 && CommonData.getUnsentMessageByChannel(channelId) != null) {
            unsentMessages = CommonData.getUnsentMessageByChannel(channelId);
            if (unsentMessages == null)
                unsentMessages = new LinkedHashMap<>();

            for (String key : unsentMessages.keySet()) {
                Message message = unsentMessages.get(key);
                String time = message.getSentAtUtc();
                int expireTimeCheck = message.getIsMessageExpired();

                if (message.getOriginalMessageType() != IMAGE_MESSAGE && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                    message.setIsMessageExpired(1);
                    try {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        if (messageJson != null) {
                            messageJson.put("is_message_expired", 1);
                            unsentMessageMapNew.put(key, messageJson);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                } else if (message.getOriginalMessageType() == IMAGE_MESSAGE) {
                    JSONObject messageJson = unsentMessageMapNew.get(key);
                    if (messageJson == null) {
                        message.setMessageStatus(MESSAGE_IMAGE_RETRY);
                    }
                }
            }
            CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
            CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);

            for (String key : unsentMessages.keySet()) {
                String time = unsentMessages.get(key).getSentAtUtc();
                String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                    fuguMessageList.add(new Message(localDate, true));
                    sentAtUTC = localDate;
                    System.out.println("Date 1: " + localDate);
                }
                fuguMessageList.add(unsentMessages.get(key));
            }
        }
    }

    private Timer timer = new Timer();
    private final long DELAY = 3000; // milliseconds

    private void stateChangeListeners() {
        etMsg.requestFocus();
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (isNetworkAvailable()) {
                        ivSendBtn.setClickable(true);
                        ivSendBtn.setAlpha(1f);
                    }
                } else {
                    ivSendBtn.setClickable(false);
                    ivSendBtn.setAlpha(0.4f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isNetworkAvailable()) {
                    if (isTyping != TYPING_STARTED) {
                        HippoLog.d(TAG, isTyping + "started typing");
                        // publish start typing event
                        if (channelId > -1 && !etMsg.getText().toString().isEmpty()) {
                            isTyping = TYPING_STARTED;
                            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
                        }
                    }

                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            HippoLog.d(TAG, isTyping + "stopped typing");
                            stopTyping();
                        }
                    }, DELAY);
                }
            }
        });

        llRoot.setOnKeyBoardStateChanged(new CustomLinear.OnKeyboardOpened() {
            @Override
            public boolean onKeyBoardStateChanged(boolean isVisible) {
                if (etMsg.hasFocus() && isVisible && fuguMessageAdapter != null && fuguMessageAdapter.getItemCount() > 0) {
                    rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
    }

    private void stopTyping() {
        if (isTyping == TYPING_STARTED) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
        }
    }

    private void setUpFayeConnection() {
        // Set FayeClient listener
        /*if (mClient == null) {
            HippoConfig.getExistingClient(new fayeClient() {
                @Override
                public void Listener(FayeClient client) {
                    mClient = client;
                    afterSetUpFayeConnection();
                }
            });
        } else {
            afterSetUpFayeConnection();
        }*/

        afterSetUpFayeConnection();
    }

    private void afterSetUpFayeConnection() {
        /*mClient.setListener(this);
        if (!mClient.isConnectedServer() && isNetworkAvailable()) {
            mClient.connectServer();
        } else {
            if (!isNetworkAvailable()) {
                setConnectionMessage(1);
            }
        }*/
    }

    private void sendReadAcknowledgement() {
        if (channelId > -1) {
            publishOnFaye(getString(R.string.fugu_empty), 0,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_READ_ALL, null);
        }
    }

    private void updateFeedback(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fuguMessageAdapter.notifyItemChanged(position);
            }
        });
    }

    private void updateFeedback(final int position, final boolean scrollDown) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (scrollDown) {
                    fuguMessageAdapter.notifyDataSetChanged();
                    scrollListToBottom();
                } else {
                    fuguMessageAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    private void scrollListToBottom() {
        try {
            rvMessages.scrollToPosition(fuguMessageList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readFunctionality(JSONObject messageJson) {
        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(messageJson.optLong(USER_ID)) != 0) {
            for (int i = 0; i < fuguMessageList.size(); i++) {
                if (fuguMessageList.get(i).getType() == ITEM_TYPE_SELF) {
                    if (fuguMessageList.get(i).getMessageStatus() == MESSAGE_SENT) {
                        fuguMessageList.get(i).setMessageStatus(MESSAGE_READ);
                    }
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.getRecycledViewPool().clear();
                fuguMessageAdapter.notifyDataSetChanged();
            }
        });
    }

    void removeItemAndUpdateUI() {
        if (CommonData.getQuickReplyData() != null) {
            try {
                String defaultActionId;
                if (!TextUtils.isEmpty(CommonData.getQuickReplyData().getDefaultActionId())) {
                    defaultActionId = CommonData.getQuickReplyData().getDefaultActionId();
                } else {
                    defaultActionId = CommonData.getQuickReplyData().getContentValue().get(0).getActionId();
                }
                sendQuickReply(CommonData.getQuickReplyData(), 0, defaultActionId);

                fuguMessageList.remove(CommonData.getQuickReplyData());
                CommonData.clearQuickReplyData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fuguMessageAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject prepareMessageJson(int onSubscribe) {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(FULL_NAME, userName);
            messageJson.put(IS_TYPING, isTyping);
            messageJson.put(MESSAGE, "");
            messageJson.put(MESSAGE_TYPE, TEXT_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(ON_SUBSCRIBE, onSubscribe);
            messageJson.put(CHANNEL_ID, channelId);

            String localDate = DateUtils.getFormattedDate(new Date());
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageJson;
    }


    private void publishOnFaye(final String message, final int messageType, final String url, final String thumbnailUrl,
                               final FuguFileDetails fileDetails, final int notificationType, String uuid) {
        try {
            publishMessage(message, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String message, int messageType, String url, String thumbnailUrl, FuguFileDetails fileDetails,
                                int notificationType, String uuid, int position) throws JSONException {
        if (isNetworkAvailable()) {
            String localDate = DateUtils.getFormattedDate(new Date());
            HippoLog.d("userName in SDK", "publishOnFaye " + userName);
            //To be shifted
            JSONObject messageJson = new JSONObject();

            if (notificationType == NOTIFICATION_READ_ALL) {
                messageJson.put(NOTIFICATION_TYPE, notificationType);
                messageJson.put(CHANNEL_ID, channelId);
            } else {
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE, message);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                if (position == 0) {
                    messageJson.put(MESSAGE_INDEX, fuguMessageList.size() - 1);
                } else {
                    messageJson.put(MESSAGE_INDEX, position);
                }
                if (uuid != null) {
                    messageJson.put("UUID", uuid);
                }
                if (messageType == IMAGE_MESSAGE && !url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                }

                if (messageType == FILE_MESSAGE && !url.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                if (messageType == TEXT_MESSAGE) {
                    messageJson.put(IS_TYPING, isTyping);
                } else {
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                }

                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            }

            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);

            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }

            if (getView()) {
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            }

            /*if (mClient.isConnectedServer()) {
                if(getView()) {
                    mClient.publish("/" + String.valueOf(channelId), messageJson);
                }
            } else {
                setListener(0);
                mClient.connectServer();
            }*/
            //end to be shifted
        } else if (!message.isEmpty() && messageType == TEXT_MESSAGE) {

        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //noinspection ConstantConditions
            switch (intent.getAction()) {
                case NETWORK_STATE_INTENT:
                    HippoLog.d(TAG, "Network connectivity change " + intent.getBooleanExtra("isConnected", false));
                    if (intent.getBooleanExtra("isConnected", false)) {

                        // TODO: 2020-04-29 connecting and connected
                        if (unsentMessageMapNew.size() == 0) {
                            pageStart = 1;
                            if (isP2P)
                                getMessages(label);
                            else
                                getMessages(null);
                        } else {
                            isNetworkStateChanged = true;
                            btnRetry.setText("Connecting...");
                        }
                        enableButtons();
                        setConnectionMessage(2);
                    } else {
                        setConnectionMessage(3);
                    }
                    break;

                case NOTIFICATION_TAPPED:

                    conversation = new Gson().fromJson(intent.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);

                    channelId = conversation.getChannelId();
                    userId = conversation.getUserId();
                    labelId = conversation.getLabelId();
                    HippoNotificationConfig.pushChannelId = channelId;
                    HippoNotificationConfig.pushLabelId = labelId;
                    currentChannelId = channelId;

                    pageStart = 1;
                    setUpUI();
                    //ConnectionManager.INSTANCE.unsubScribeChannel();
                    ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(channelId));
                    /*if (mClient.isConnectedServer()) {
                        mClient.unsubscribeAll();
                        mClient.subscribeChannel("/" + String.valueOf(channelId));
                    } else {
                        mClient.connectServer();
                    }*/
                    break;
            }

        }
    };

    private void enableButtons() {
        try {
            if (etMsg.getText().toString().trim().length() > 0 && isNetworkAvailable()) {
                ivSendBtn.setClickable(true);
                ivSendBtn.setAlpha(1f);
            } else {
                ivSendBtn.setClickable(false);
                ivSendBtn.setAlpha(0.4f);
            }
            if (!isNetworkAvailable() && ConnectionManager.INSTANCE.isConnected()) {
                ivSendBtn.setAlpha(0.4f);
            }
        } catch (Exception e) {

        }
    }

    private void startAnim() {
//        aviTyping.show();
//        aviTyping.setVisibility(View.VISIBLE);
//        llTyping.setVisibility(View.VISIBLE);
        isTypingView.setVisibility(View.VISIBLE);

    }

    private void stopAnim() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isTypingView.setVisibility(View.GONE);
//                llTyping.setVisibility(View.GONE);
//                aviTyping.setVisibility(View.GONE);
//                aviTyping.hide();
            }
        });
    }

    private boolean isFirstTimeOpened = true;
    private boolean isFromFilePicker = false;
    private boolean isCallClicked = false;

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaymentOpen = false;
        isCallClicked = false;
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }

        if (channelId > 0) {
            ConnectionManager.INSTANCE.subScribeChannel("/" + channelId);
        }
        HippoNotificationConfig.pushChannelId = channelId;
        HippoNotificationConfig.pushLabelId = labelId;
        currentChannelId = channelId;
        KeyboardUtil.addKeyboardToggleListener(FuguChatActivity.this, this);

        if (isNetworkAvailable())
            setUpFayeConnection();

        if (isFromFilePicker) {
            isFromFilePicker = false;
        } else {
            if (CommonData.getPushBoolean() && CommonData.getPushChannel().compareTo(channelId) == 0) {
                allMessagesFetched = false;
                pageStart = 1;
                getMessages(null);
            } else if (!isFirstTimeOpened && isNetworkAvailable()) {
                allMessagesFetched = false;
                pageStart = 1;
                apiGetMessages(null, true);
            }
            isFirstTimeOpened = false;
            CommonData.setPushBoolean(false);
            CommonData.clearPushChannel();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (fuguMessageAdapter != null)
                    fuguMessageAdapter.attachObservers(true);
            }
        }).start();

        LocalBroadcastManager.getInstance(this).registerReceiver(nullListenerReceiver, new IntentFilter(FuguAppConstant.FUGU_LISTENER_NULL));
    }


    boolean isPaymentOpen = false;

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFromHistory && !isPaymentOpen) {
            HippoNotificationConfig.pushChannelId = -1L;
            HippoNotificationConfig.pushLabelId = -1L;
            CommonData.setPushChannel(channelId);

            LocalBroadcastManager.getInstance(this).unregisterReceiver(nullListenerReceiver);

            if (unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                CommonData.removeUnsentMessageMapChannel(channelId);
            } else {
                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
            }

            if (unsentMessages != null && unsentMessages.size() == 0) {
                CommonData.removeUnsentMessageChannel(channelId);
            } else {
                CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
            }

            if (channelId > 0) {
                if (!TextUtils.isEmpty(audioMuid)) {
                    sentMessages.get(audioMuid).setAudioPlaying(false);
                    CommonMediaPlayer.getInstance().stopMedia();
                }
                CommonData.setSentMessageByChannel(channelId, sentMessages);
            }

            // Fire stop typing event on Faye before close the chat
            stopTyping();
            stopAnim();
            KeyboardUtil.removeKeyboardToggleListener(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (fuguMessageAdapter != null)
                        fuguMessageAdapter.attachObservers(false);
                }
            }).start();
        }
    }


    @Override
    protected void onDestroy() {
        if (!isFromHistory) {
            CommonData.clearQuickReplyData();
            HippoLog.e(TAG, "onDestroy");
            try {
                if (unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                    CommonData.removeUnsentMessageMapChannel(channelId);
                } else {
                    CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
                }

                if (unsentMessages != null && unsentMessages.size() == 0) {
                    CommonData.removeUnsentMessageChannel(channelId);
                } else {
                    CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
                }
            } catch (Exception e) {

            }
            if (channelId > 0) {
                HippoLog.e(TAG, "count in onDestroy: " + channelId);
                CommonData.setSentMessageByChannel(channelId, sentMessages);
            }
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverChat);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(fileUploadReceiver);

                updateUnreadCount(channelId, labelId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            messagesApi.clear();

            try {
                // Fire stop typing event on Faye before closing the chat
                stopTyping();
                stopFayeClient();

                HippoNotificationConfig.pushChannelId = -1L;
                HippoNotificationConfig.pushLabelId = -1L;
                currentChannelId = -1L;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();

    }

    public void stopFayeClient() {
        // TODO: 2020-04-28 unsubscribe channel
        ConnectionManager.INSTANCE.unsubScribeChannel("/" + String.valueOf(channelId));
        /*try {
            HandlerThread thread = new HandlerThread("TerminateThread");
            thread.start();
            new Handler(thread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mClient.isConnectedServer()) {
                        mClient.disconnectServer();
                        mClient.setListener(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public boolean checkValidMsg(String msg) {
        if (Utils.isEmailValid(msg) || Utils.isValidNumber(msg)) {
            return true;
        }
        return false;
    }

    private boolean checkValidMsg = false;

    public void onClick(View v) {
        if (v.getId() == R.id.ivSendBtn) {
            if (checkValidMsg && !TextUtils.isEmpty(etMsg.getText().toString().trim())) {
                if (checkValidMsg(etMsg.getText().toString().trim())) {
                    ToastUtil.getInstance(FuguChatActivity.this).showToast(Restring.getString(FuguChatActivity.this, R.string.hippo_message_not_allow));
                    return;
                }
            }
            if (!etMsg.getText().toString().trim().isEmpty()) {
                if (channelId.compareTo(-1L) > 0) {
                    removeItemAndUpdateUI();
                    sendMessage(etMsg.getText().toString().trim(), TEXT_MESSAGE, "", "", null, null, null);

                } else {
                    if (!isConversationCreated) {
                        conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                        CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                                .setMessageType(TEXT_MESSAGE)
                                .setIsP2P(isP2P)
                                .setText(etMsg.getText().toString().trim())
                                .setFuguGetMessageResponse(mFuguGetMessageResponse)
                                .setGetLabelMessageResponse(labelMessageResponse)
                                .setConversationParams(fuguCreateConversationParams)
                                .build();
                        createConversation(attribute);
                    }
                }
            }
        } else if (v.getId() == R.id.ivBackBtn) {
            onBackPressed();
        } else if (v.getId() == R.id.ivVideoView) {
            if (CommonData.getVideoCallStatus())
                videoCallInit(VIDEO_CALL_VIEW);
            else {
                String text = Restring.getString(FuguChatActivity.this, R.string.hippo_feature_no_supported);
                Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.ivAudioView) {
            videoCallInit(AUDIO_CALL_VIEW);
        } else if (v.getId() == R.id.ivAttachment) {
            if (checkButtonClick()) {
                selectImage(v);
            }
        } else if (v.getId() == R.id.tvNoInternet) {
            if (isP2P) {
                pbPeerChat.setVisibility(View.VISIBLE);
                tvNoInternet.setVisibility(View.GONE);
                HippoLog.v("call createConversation", "onClick no internet");

                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setMessageType(TEXT_MESSAGE)
                        .setText(etMsg.getText().toString().trim())
                        .setIsP2P(isP2P)
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .build();
                createConversation(attribute);
            } else if (conversation.isOpenChat() && conversation.getLabelId().compareTo(-1L) != 0) {
                tvNoInternet.setVisibility(View.GONE);
                getByLabelId();
            } else {
                pageStart = 1;
                tvNoInternet.setVisibility(View.GONE);
                getMessages(null);
            }
        }
    }

    private boolean isChannelCreated = false;
    private boolean insertBotId = false;


    private void createConversation(final CreateChannelAttribute attribute) {
        createConversation(attribute, "");
    }

    private void createConversation(final CreateChannelAttribute attribute, final String actionId) {
        if (isChannelCreated && channelId.compareTo(-1l) > 0 && attribute.hasJson()) {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), attribute.getJsonObject());
                        /*if (mClient.isConnectedServer()) {
                            mClient.publish("/" + String.valueOf(channelId), attribute.getJsonObject());
                        }*/
                    }
                }, 500);
            } catch (Exception e) {

            }
            return;
        }

        if (isNetworkAvailable()) {
            pbSendingImage.setVisibility(View.VISIBLE);
            ivSendBtn.setVisibility(View.VISIBLE);

            new CreateConversation(this, userName, userId, !TextUtils.isEmpty(HippoConfig.getInstance().getAgentEmail()), insertBotId).createChannel(attribute, fuguMessageList, skipBot, new CreateConversation.Callback() {
                @Override
                public void onSuccess(FuguCreateConversationResponse fuguCreateConversationResponse,
                                      int messageType, final JSONObject jsonObject, Message message, final boolean hasBotMessage) {

                    try {
                        if (fuguMessageAdapter != null) {
                            if (fuguMessageAdapter.getCurrentChatType() != fuguCreateConversationResponse.getData().getChatType()) {
                                fuguMessageAdapter.updateChatType(fuguCreateConversationResponse.getData().getChatType());
                                messageChatType = fuguCreateConversationResponse.getData().getChatType();
                            }
                        }

                        globalUuid = UUID.randomUUID().toString();
                        isConversationCreated = true;
                        isFayeChannelActive = true;
                        channelId = fuguCreateConversationResponse.getData().getChannelId();
                        String transactionId = fuguCreateConversationParams.getTransactionId();
                        try {
                            if (fuguCreateConversationParams != null
                                    && fuguCreateConversationParams.getOtherUserUniqueKeys() != null
                                    && fuguCreateConversationParams.getOtherUserUniqueKeys().size() > 0
                            ) {
                                String uniqueId = fuguCreateConversationParams.getOtherUserUniqueKeys().get(0).getAsString();
                                if (!TextUtils.isEmpty(uniqueId))
                                    transactionId = transactionId + uniqueId;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            if (!TextUtils.isEmpty(transactionId)) {
                                transactionIdsMap.put(transactionId, fuguCreateConversationResponse.getData().getChannelId());
                                P2pUnreadCount.INSTANCE.updateChannelId(transactionId, fuguCreateConversationResponse.getData().getChannelId(), "");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transactionIdsMap != null)
                            CommonData.setTransactionIdsMap(transactionIdsMap);
                        HippoNotificationConfig.pushChannelId = channelId;
                        currentChannelId = channelId;

                        HippoLog.v("channelId in createConversation is", "--> " + fuguCreateConversationResponse.getData().getChannelId());
                        ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(channelId));
                        if (messageType != 22)
                            ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

                        isTyping = TYPING_SHOW_MESSAGE;

                        pbSendingImage.setVisibility(View.GONE);
                        ivSendBtn.setVisibility(View.VISIBLE);

                        boolean alreadyAssign = false;
                        try {
                            if (fuguCreateConversationResponse.getData().isAlreadyAssigned()) {
                                alreadyAssign = true;
                            }
                        } catch (Exception e) {

                        }


                        if (channelId > 0 && CommonData.getSentMessageByChannel(channelId) != null) {
                            getSavedMessages(channelId);
                        }

                        if (!TextUtils.isEmpty(HippoConfig.getInstance().getAgentEmail())) {
                            if (!alreadyAssign) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        assignChat(channelId);
                                    }
                                }, 600);
                            } else {
                                pbLoading.setVisibility(View.GONE);
                                pbPeerChat.setVisibility(View.GONE);
                                llMessageLayout.setVisibility(View.VISIBLE);
                                getSavedMessages(channelId);
                                try {
                                    if (CommonData.getMessageResponse(currentChannelId) != null) {
                                        label = CommonData.getLabelIdResponse(currentChannelId).getData().getLabel();
                                        setToolbar(label);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        } else {
                            FuguGetMessageResponse fuguGetMessageResponse = new FuguGetMessageResponse();

                            fuguGetMessageResponse.setStatusCode(fuguCreateConversationResponse.getStatusCode());
                            fuguGetMessageResponse.setMessage(fuguCreateConversationResponse.getMessage());

                            Data data = fuguGetMessageResponse.getData();


                            data.getMessages().add((new Message(businessName, -1L, defaultMessage, "", false,
                                    MESSAGE_SENT, 0, TEXT_MESSAGE, globalUuid)));

                            //if (isP2P)
                            //label = fuguCreateConversationResponse.getData().getlabel();
                            data.setLabel(label);
                            data.setFullName(userName);
                            data.setOnSubscribe(onSubscribe);
                            data.setPageSize(25);
                            data.setChannelId(channelId);
                            data.setStatus(STATUS_CHANNEL_OPEN);
                            data.setBusinessName(businessName);

                            data.setChatType(fuguCreateConversationResponse.getData().getChatType());
                            data.setChannelImageUrl(fuguCreateConversationResponse.getData().getChannelImageUrl());


                            fuguGetMessageResponse.setData(data);
                            HippoLog.v("set data is", "--> " + fuguGetMessageResponse.getData().getChannelId());
                            CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                            setToolbar(label, fuguCreateConversationResponse.getData().getChannelImageUrl());


                            if (messageType == TEXT_MESSAGE && !TextUtils.isEmpty(etMsg.getText().toString().trim())) {
                                sendMessage(etMsg.getText().toString().trim(), TEXT_MESSAGE, "", "", null, null, null);
                            } else if (messageType == ACTION_MESSAGE_NEW) {
                                sendPaymentRequest();
                            } else if (messageType == IMAGE_MESSAGE) {
                                if (jsonObject != null) {
                                    try {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), jsonObject);
//                                                if (mClient.isConnectedServer())
//                                                    mClient.publish("/" + String.valueOf(channelId), jsonObject);
                                            }
                                        }, 500);
                                    } catch (Exception e) {

                                    }
                                }
                            }


                            if (messageType == 22) {
                                pageStart = 1;
                                setRecyclerViewData();
                                llRoot.setVisibility(View.VISIBLE);
                                final String label = fuguCreateConversationResponse.getData().getlabel();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getMessages(label, true);
                                    }
                                }, 500);

                                return;
                            } else if (messageType == 17) {
                                hasFormValue = false;
                                JSONObject messageJson = Paper.book(HIPPO_PAPER_NAME).read("hippo_bot_message");
                                messageJson.put("message_id", fuguCreateConversationResponse.getData().getBotMessageId());
                                messageJson.put("id", fuguCreateConversationResponse.getData().getBotMessageId());
                                message.setMessageId(fuguCreateConversationResponse.getData().getBotMessageId());
                                message.setId(fuguCreateConversationResponse.getData().getBotMessageId());
                                message.setMuid(messageJson.optString("muid"));

                                for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                                    if (fuguMessageList.get(i).getType() == FUGU_FORUM_VIEW || fuguMessageList.get(i).getType() == FUGU_FORUM_TICKET_) {
                                        fuguMessageList.get(i).setMessageId(fuguCreateConversationResponse.getData().getBotMessageId());
                                        fuguMessageList.get(i).setId(fuguCreateConversationResponse.getData().getBotMessageId());
                                        fuguMessageList.get(i).setMuid(messageJson.optString("muid"));
                                        break;
                                    }
                                }

                                fuguGetMessageResponse.getData().getMessages().add(message);
                                CommonData.setMessageResponse(channelId, fuguGetMessageResponse);

                                messageJson.put(USER_IMAGE, getUserImage());
                                if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                                    messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
                                }
                                Paper.book(HIPPO_PAPER_NAME).write("hippo_bot_message", messageJson);

                                try {
                                    for (int i = 0; i < fuguMessageList.size(); i++) {
                                        if (fuguMessageList.get(i).getType() == FUGU_FORUM_VIEW || fuguMessageList.get(i).getType() == FUGU_FORUM_TICKET_) {
                                            fuguMessageList.get(i).setId(fuguCreateConversationResponse.getData().getBotMessageId());
                                            fuguMessageList.get(i).setMessageId(fuguCreateConversationResponse.getData().getBotMessageId());
                                            fuguMessageAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                } catch (Exception e) {

                                }


                            /*if (mClient.isConnectedServer()) {
                                mClient.publish("/" + String.valueOf(channelId), messageJson);
                            } else {
                                mClient.connectServer();
                            }*/

                                pageStart = 1;
                                setRecyclerViewData();
                                llRoot.setVisibility(View.VISIBLE);
                                final String label = fuguCreateConversationResponse.getData().getlabel();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getMessages(label, true);
                                    }
                                }, 500);

                                return;

                            } else {
                                CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                            }
                        }

                        etMsg.setText("");

                        if (isP2P || hasBotMessage) {
                            pageStart = 1;
                            setRecyclerViewData();
                            llRoot.setVisibility(View.VISIBLE);
                            final String label = fuguCreateConversationResponse.getData().getlabel();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (pbPeerChat.getVisibility() == View.VISIBLE) {
                                        showLoading = false;
                                        hideTopBar = true;
                                    }
                                    getMessages(label, hasBotMessage);
                                }
                            }, 500);


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showLoading = false;
                                    hideTopBar = true;
                                    getMessages(label, hasBotMessage);
                                }
                            }, 2500);
                        } else {
                            if (pbPeerChat.getVisibility() == View.VISIBLE) {
                                pbPeerChat.setVisibility(View.GONE);
                            }
                        }

                        if (!TextUtils.isEmpty(CommonData.getActionId())) {
                            takeMessageAction(CommonData.getActionId(), CommonData.getUrl());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(APIError error) {
                    if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                        Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        if (isP2P) {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            tvNoInternet.setText(error.getMessage());
                        } else {
                            String text = Restring.getString(FuguChatActivity.this, R.string.fugu_unable_to_connect_internet);
                            Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
                        }
                    }

                    pbSendingImage.setVisibility(View.GONE);
                    ivSendBtn.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private void updateRecycler() {
        if (fuguMessageAdapter != null) {
            fuguMessageAdapter.updateList(fuguMessageList);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fuguMessageAdapter != null) {
                        try {
                            if (fuguMessageList.size() > 2)
                                fuguMessageAdapter.notifyItemRangeChanged(fuguMessageList.size() - 2, fuguMessageList.size());
                            else
                                fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                            //fuguMessageAdapter.notifyDataSetChanged();

                            if (fuguMessageAdapter.getItemCount() > 0) {
                                rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMessages(String channelName) {
        if (!isCallClicked)
            getMessages(channelName, false);
    }

    private boolean fromCreateBotId = false;

    private void getMessages(final String channelName, final boolean hasBotMessage) {
        HippoLog.d("userName in SDK", "getMessages before call" + userName);
        fromCreateBotId = hasBotMessage;
        if (isNetworkAvailable()) {
            if ((HippoConfig.getInstance().getUserData() == null)
                    || (TextUtils.isEmpty(HippoConfig.getInstance().getAppKey()))) {
                new ApiPutUserDetails(FuguChatActivity.this, new ApiPutUserDetails.Callback() {
                    @Override
                    public void onSuccess() {
                        apiGetMessages(channelName);
                    }

                    @Override
                    public void onFailure() {

                    }
                }).sendUserDetails(HippoConfig.getmResellerToken(), HippoConfig.getmReferenceId());
            } else {
                apiGetMessages(channelName);
            }
        } else {
            if (pageStart == 1 &&
                    (CommonData.getMessageResponse(channelId) == null ||
                            CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            if (isP2P) {
                tvNoInternet.setVisibility(View.VISIBLE);
                pbPeerChat.setVisibility(View.GONE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    boolean isApiRunning;
    private HashSet<String> messagesApi = new HashSet<>();

    private void apiGetMessages(String channelName) {
        apiGetMessages(channelName, false);
    }

    private void apiGetMessages(final String channelName, final boolean isFromOnResume) {
        if (channelId < 0) {
            return;
        }
        if (isApiRunning) {
            messagesApi.clear();
            messagesApi.add("getMessages");
            return;
        }
        showLoading = false;
        if (!allMessagesFetched || isNetworkStateChanged) {
            isApiRunning = true;
            FuguGetMessageParams commonParams = new FuguGetMessageParams(HippoConfig.getInstance().getAppKey(),
                    channelId,
                    enUserId,
                    pageStart,
                    channelName);

            if (!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
                commonParams.setLang(HippoConfig.getInstance().getCurrentLanguage());

            if (isFromOnResume && fuguMessageList.size() > 100)
                commonParams.setPageEnd(fuguMessageList.size() - dateItemCount);

            if (CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping() == 1) {
                fuguCreateConversationParams.setMultiChannelLabelMapping(CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping());
            }

            if (!hideTopBar) {
                if (sentMessages == null || sentMessages.size() == 0) {
                    showLoading = true;
                    //setConnectionMessage(0);
                } else if (pageStart == 1) {
                    //setConnectionMessage(1);
                }
            }

            if (retryLayout.getVisibility() == View.VISIBLE) {
                setConnectionMessage(0);
            }


            final int localPageSize = pageStart;

            RestClient.getApiInterface().getMessages(commonParams).enqueue(new ResponseResolver<FuguGetMessageResponse>(FuguChatActivity.this, showLoading, false) {
                @Override
                public void success(FuguGetMessageResponse fuguGetMessageResponse) {
                    HippoLog.e("fuguGetMessageResponse", "fuguGetMessageResponse = " + new Gson().toJson(fuguGetMessageResponse));
                    setConnectionMessage(6);
                    isApiRunning = false;
                    //messagesApi.add("parsing");
                    if (messagesApi != null && messagesApi.size() > 0) {
                        messagesApi.remove("getMessages");
                        isNetworkStateChanged = true;
                        apiGetMessages(channelName, isFromOnResume);
                    }

                    try {
                        HippoConfig.getInstance().clearCount(fuguGetMessageResponse.getData().getChannelId());
                    } catch (Exception e) {

                    }

                    mFuguGetMessageResponse = fuguGetMessageResponse;

                    try {
                        checkValidMsg = mFuguGetMessageResponse.getData().getRestrictPersonalInfo();
                    } catch (Exception e) {

                    }

                    try {
                        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null) {
                            agentId = mFuguGetMessageResponse.getData().getAgentId();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null && mFuguGetMessageResponse.getData().getChannelId() != null)
                        CommonData.saveVideoCallAgent(mFuguGetMessageResponse.getData().getChannelId(), mFuguGetMessageResponse);

                    if (pbPeerChat.getVisibility() == View.VISIBLE)
                        pbPeerChat.setVisibility(View.GONE);

                    if (isP2P) {
                        pbPeerChat.setVisibility(View.GONE);
                        llMessageLayout.setVisibility(View.VISIBLE);
                    }
                    label = fuguGetMessageResponse.getData().getLabel();
                    setToolbar(label);
                    isDisableReply = false;
                    if (fuguGetMessageResponse.getData().isDisableReply()) {
                        llMessageLayout.setVisibility(View.GONE);
                        isDisableReply = true;
                    }
                    if (fuguGetMessageResponse.getData().getMessages() != null) {

                        LinkedHashMap<String, Message> tempMessages = new LinkedHashMap<>();
                        LinkedHashMap<String, Message> tempSentMessages = new LinkedHashMap<>();

                        String tempSentAtUtc = "";

                        if (localPageSize == 1) {
                            tempSentMessages.putAll(sentMessages);
                            fuguMessageList.clear();
                            sentMessages.clear();
                            dateItemCount = 0;

                            if (fuguMessageAdapter != null && fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null) {
                                if (fuguMessageAdapter.getCurrentChatType() != fuguGetMessageResponse.getData().getChatType()) {
                                    fuguMessageAdapter.updateChatType(fuguGetMessageResponse.getData().getChatType());
                                    messageChatType = fuguGetMessageResponse.getData().getChatType();
                                }
                            }

                            //HippoLog.e(TAG, "This is a first page");
                        } else {
                            //HippoLog.e(TAG, "No first page");
                        }

                        Data messageResponseData = fuguGetMessageResponse.getData();
                        if (messageResponseData.getMessages().size() < messageResponseData.getPageSize()) {
                            allMessagesFetched = true;
                        } else {
                            allMessagesFetched = false;
                        }
                        onSubscribe = messageResponseData.getOnSubscribe();
                        HippoLog.e("getMessages onSubscribe", "==" + onSubscribe);

                        int dateCount = 0;
                        /*try {
                            if(TextUtils.isEmpty(messageResponseData.getMessages().get(messageResponseData.getMessages().size()-1).getInputType())) {
                                updateKeyboard(fuguGetMessageResponse.getData().getInputType());
                            }
                        } catch (Exception e) {

                        }*/
                        for (int i = 0; i < messageResponseData.getMessages().size(); i++) {
                            Message messageObj = messageResponseData.getMessages().get(i);
                            if (messageObj.getMessageType() == PAYMENT_TYPE && messageObj.getCustomAction() == null)
                                break;
                            boolean isSelf = false;
                            if (messageObj.getUserId().compareTo(userId) == 0)
                                isSelf = true;

                            String localDate = dateUtils.convertToLocal(messageObj.getSentAtUtc(), inputFormat, outputFormat);

                            boolean hasDateView = false;
                            if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                tempMessages.put(localDate, new Message(localDate, true));
                                tempSentAtUtc = localDate;
                                dateItemCount = dateItemCount + 1;
                                dateCount = dateCount + 1;
                                hasDateView = true;
                            }

                            String muid = messageObj.getMuid();
                            if (TextUtils.isEmpty(muid))
                                muid = TextUtils.isEmpty(String.valueOf(messageObj.getId()))
                                        ? UUID.randomUUID().toString() : String.valueOf(messageObj.getId());


                            String msgTxt = messageObj.getMessage();
                            try {
                                if (!TextUtils.isEmpty(messageObj.getMultiLangMessage())) {
                                    Pattern pattern = Pattern.compile("\\{\\{\\{(.*?)\\}\\}\\}");
                                    Matcher matcher = pattern.matcher(messageObj.getMultiLangMessage());
                                    if (matcher.find()) {
                                        String key = matcher.group(1);
                                        String value = Restring.getString(key);
                                        if (!TextUtils.isEmpty(value)) {
                                            String oldStr = "{{{" + key + "}}}";
                                            msgTxt = messageObj.getMultiLangMessage().replace(oldStr, value);
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }

                            String removeLt = msgTxt.replaceAll("<", "&lt;");
                            String removeGt = removeLt.replaceAll(">", "&gt;");

//                            String removeGt = messageObj.getMessage();
//                            if(CommonData.isEncodeToHtml()) {
//                                String removeLt = messageObj.getMessage().replaceAll("<", "&lt;");
//                                removeGt = removeLt.replaceAll(">", "&gt;");
//                            }

                            Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                    messageObj.getUserId(),
                                    removeGt,
                                    messageObj.getSentAtUtc(),
                                    isSelf,
                                    messageObj.getMessageStatus(),
                                    i,
                                    messageObj.getUrl(),
                                    messageObj.getThumbnailUrl(),
                                    messageObj.getMessageType(),
                                    messageObj.getMuid());

                            message.setUserType(messageObj.getUserType());
                            message.setOriginalMessageType(messageObj.getMessageType());
                            message.setUserImage(messageObj.getUserImage());

                            if (messageObj.getCustomAction() != null) {
                                message.setCustomAction(messageObj.getCustomAction());
                            }

                            if (!TextUtils.isEmpty(messageObj.getCallType())) {
                                message.setCallType(messageObj.getCallType());
                            }

                            message.setIntegrationSource(messageObj.getIntegrationSource());

                            if (messageObj.getMessageType() == FILE_MESSAGE || messageObj.getMessageType() == IMAGE_MESSAGE) {
                                message.setFileExtension(messageObj.getFileExtension());
                                message.setFileName(messageObj.getFileName());
                                message.setFileSize(messageObj.getFileSize());
                                message.setFilePath(messageObj.getFilePath());
                                message.setFileUrl(messageObj.getFileUrl());
                                message.setDocumentType(messageObj.getDocumentType());
                            } else if (messageObj.getMessageType() == FEEDBACK_MESSAGE) {
                                message.setIsRatingGiven(messageObj.getIsRatingGiven());
                                message.setTotalRating(messageObj.getTotalRating());
                                message.setRatingGiven(messageObj.getRatingGiven());
                                message.setComment(messageObj.getComment());
                                message.setLineBeforeFeedback(messageObj.getLineBeforeFeedback());
                                message.setLineAfterFeedback_1(messageObj.getLineAfterFeedback_1());
                                message.setLineAfterFeedback_2(messageObj.getLineAfterFeedback_2());
                            } else if (messageObj.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                                message.setContentValue(messageObj.getContentValue());
                                message.setDefaultActionId(messageObj.getDefaultActionId());
                                message.setValues(messageObj.getValues());
                                if (messageObj.getValues() != null && messageObj.getValues().size() > 0)
                                    continue;
                            } else if (messageObj.getMessageType() == FUGU_FORUM_VIEW || messageObj.getMessageType() == FUGU_FORUM_TICKET_) {
                                message.setContentValue(messageObj.getContentValue());
                                if (messageObj.getMessageType() == FUGU_FORUM_TICKET_ &&
                                        messageObj.getValues() != null &&
                                        messageObj.getValues().size() > 0 &&
                                        !Utils.isEmailValid(messageObj.getValues().get(0))) {
                                    message.setValues(new ArrayList<String>());
                                } else
                                    message.setValues(messageObj.getValues());
                                message.setId(messageObj.getId());
                                message.setIsSkipEvent(messageObj.getIsSkipEvent());
                                message.setIsSkipButton(messageObj.getIsSkipButton());
                                message.setIsFromBot(messageObj.getIsFromBot());
                                message.setIsActive(messageObj.getIsActive());
                            } else if (messageObj.getMessageType() == HIPPO_USER_CONSENT) {
                                message.setContentValue(messageObj.getContentValue());
                                message.setId(messageObj.getId());
                                message.setIsActive(messageObj.getIsActive());
                                message.setSelectedBtnId(messageObj.getSelectedBtnId());
                            } else if (messageObj.getMessageType() == CARD_LIST) {
                                message.setContentValue(messageObj.getContentValue());
                                message.setSelectedAgentId(messageObj.getSelectedAgentId());
                                message.setFallbackText(messageObj.getFallbackText());

                                /*if(i !=0) {
                                    ArrayList<Message> items = new ArrayList<>();
                                    items.addAll(tempMessages.values());
                                    Message lastMessage = items.get(items.size()-1);
                                    lastMessage.setBelowUserId(-2L);
                                    tempMessages.put(lastMessage.getMuid(), lastMessage);
                                }*/
                                if (messageObj.getContentValue() != null && messageObj.getContentValue().size() > 0) {
                                    if (i != 0) {
                                        ArrayList<Message> items = new ArrayList<>();
                                        items.addAll(tempMessages.values());
                                        Message lastMessage = items.get(items.size() - 1);
                                        lastMessage.setBelowUserId(-2L);
                                        tempMessages.put(lastMessage.getMuid(), lastMessage);
                                    }
                                } else {
                                    message.setMessage(messageObj.getFallbackText());
                                    messageObj.setMessageType(FUGU_TEXT_VIEW);
                                }
                            }

                            int messageViewType = getType(messageObj.getMessageType(), isSelf, true, messageObj.getDocumentType());
                            message.setMessageType(messageViewType);
                            message.setVideoCallDuration(messageObj.getVideoCallDuration());
                            message.setMessageState(messageObj.getMessageState());

                            if (i != 0) {
                                message.setAboveMuid(messageResponseData.getMessages().get(i - 1).getMuid());
                                message.setAboveUserId(messageResponseData.getMessages().get(i - 1).getUserId());
                            }

                            if (i + 1 < messageResponseData.getMessages().size()) {
                                message.setBelowMuid(messageResponseData.getMessages().get(i + 1).getMuid());
                                message.setBelowUserId(messageResponseData.getMessages().get(i + 1).getUserId());
                            }

                            if (hasDateView && i != 0) {
                                ArrayList<Message> items = new ArrayList<>();
                                items.addAll(tempMessages.values());
                                Message lastMessage = items.get(items.size() - 2);
                                lastMessage.setBelowMuid(localDate);
                                lastMessage.setBelowUserId(-2L);
                                tempMessages.put(lastMessage.getMuid(), lastMessage);
                            }

                            tempMessages.put(muid, message);
                            tempSentMessages.remove(muid);

                            if (!TextUtils.isEmpty(messageObj.getMuid())) {
                                if (unsentMessageMapNew.size() > 0) {
                                    unsentMessageMapNew.remove(messageObj.getMuid());
                                }
                                if (unsentMessages.size() > 0) {
                                    unsentMessages.remove(messageObj.getMuid());
                                }
                            }
                        }

                        if (pageStart == 1) {
                            fuguMessageList.clear();
                        }

                        if (sentMessages.containsKey(tempSentAtUtc)) {
                            sentMessages.remove(tempSentAtUtc);
                            dateItemCount = dateItemCount - 1;
                            dateCount = dateCount - 1;
                        }

                        tempMessages.putAll(sentMessages);
                        sentMessages.clear();
                        sentMessages.putAll(tempMessages);

                        /*messagesApi.remove("parsing");
                        if(tempParseMessages.size()>0) {
                            for(Message msg : tempParseMessages) {
                                if(!sentMessages.containsKey(msg.getMuid())) {
                                    sentMessages.put(msg.getMuid(), msg);
                                }
                            }
                        }*/

                        if (fromCreateBotId) {
                            //fromCreateBotId = false;
                            tempSentMessages.clear();
                        }

                        // put local sent messages into updated sent list
                        try {
                            if (tempSentMessages.values().size() > 0) {
                                long lastMessageTime = dateUtils.getTimeInLong(messageResponseData.getMessages()
                                        .get(messageResponseData.getMessages().size() - 1).getSentAtUtc());
                                if (lastMessageTime > 0) {
                                    for (String key : tempSentMessages.keySet()) {
                                        try {
                                            if (!tempSentMessages.get(key).isDateView()) {
                                                Message message = tempSentMessages.get(key);
                                                long localMessageTime = dateUtils.getTimeInLong(message.getSentAtUtc());
                                                //HippoLog.i(TAG, "localMessageTime: " + localMessageTime + " > " + lastMessageTime);
                                                if (localMessageTime > lastMessageTime) {
                                                    //HippoLog.e(TAG, "localMessageTime: " + localMessageTime + " > " + lastMessageTime);

                                                    ArrayList<Message> items = new ArrayList<>();
                                                    items.addAll(sentMessages.values());
                                                    Message lastMessage = items.get(items.size() - 1);
                                                    lastMessage.setBelowMuid(message.getMuid());
                                                    lastMessage.setBelowUserId(message.getUserId());
                                                    sentMessages.put(lastMessage.getMuid(), lastMessage);

                                                    message.setAboveUserId(lastMessage.getUserId());
                                                    message.setAboveMuid(lastMessage.getMuid());
                                                    sentMessages.put(message.getMuid(), message);
                                                }
                                            }
                                        } catch (Exception e) {
                                            if (HippoConfig.DEBUG)
                                                e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }

                        tempSentMessages.clear();

                        fuguMessageList = new ArrayList<>();
                        fuguMessageList.addAll(sentMessages.values());

                        try {
                            for (String key : unsentMessages.keySet()) {
                                try {
                                    if (!unsentMessageMapNew.containsKey(key)) {
                                        unsentMessages.remove(key);
                                        continue;
                                    }
                                } catch (Exception e) {

                                }
                                Message listItem = unsentMessages.get(key);
                                String time = listItem.getSentAtUtc();
                                String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                                boolean hasDate = false;
                                if (!sentMessages.containsKey(localDate)) {
                                    if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                        int index = fuguMessageList.size() - 1;

                                        fuguMessageList.get(index).setBelowMuid(localDate);
                                        fuguMessageList.get(index).setBelowUserId(-2L);

                                        fuguMessageList.add(new Message(localDate, true));
                                        tempSentAtUtc = localDate;
                                        hasDate = true;
                                        //System.out.println("Date 2: " + localDate);
                                    }
                                }
                                if (unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                                    CommonData.removeUnsentMessageMapChannel(channelId);
                                } else {
                                    CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
                                }


                                if (unsentMessages != null && unsentMessages.size() == 0) {
                                    CommonData.removeUnsentMessageChannel(channelId);
                                } else {
                                    CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
                                }

                                // update
                                try {
                                    JSONObject messageJson = unsentMessageMapNew.get(key);
                                    if (messageJson != null) {
                                        messageJson.put("message_index", fuguMessageList.size());
                                        unsentMessageMapNew.put(key, messageJson);
                                    }
                                    Message message = unsentMessages.get(key);
                                    if (hasDate) {
                                        message.setAboveUserId(-2L);
                                        message.setAboveMuid(tempSentAtUtc);
                                    } else {
                                        message.setAboveUserId(fuguMessageList.get(fuguMessageList.size() - 1).getUserId());
                                        message.setAboveMuid(fuguMessageList.get(fuguMessageList.size() - 1).getMuid());
                                    }
                                    fuguMessageList.get(fuguMessageList.size() - 1).setBelowUserId(message.getUserId());
                                    fuguMessageList.get(fuguMessageList.size() - 1).setBelowMuid(message.getMuid());

                                    fuguMessageList.add(message);
                                } catch (Exception e) {

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tvNoInternet.setVisibility(View.GONE);
                        llRoot.setVisibility(View.VISIBLE);
                        if (conversation.getUnreadCount() > 0) {
                            rvMessages.setAlpha(0);
                        }

                        if (CommonData.getDirectCallBtnDisabled()) {
                            ivVideoView.setVisibility(View.GONE);
                            ivAudioView.setVisibility(View.GONE);
                        } else {
                            if (CommonData.getVideoCallStatus() && fuguGetMessageResponse.getData() != null) {
                                if ((fuguGetMessageResponse.getData().isAllowVideoCall() && fuguGetMessageResponse.getData().getAgentId() != null
                                        && fuguGetMessageResponse.getData().getAgentId().intValue() > 0)
                                        && (fuguGetMessageResponse.getData().isAllowVideoCall()))
                                    ivVideoView.setVisibility(View.VISIBLE);
                            } else {
                                ivVideoView.setVisibility(View.GONE);
                            }

                            if (CommonData.getAudioCallStatus() && fuguGetMessageResponse.getData() != null) {
                                if ((fuguGetMessageResponse.getData().isAllowAudioCall() && fuguGetMessageResponse.getData().getAgentId() != null
                                        && fuguGetMessageResponse.getData().getAgentId().intValue() > 0)
                                        && (fuguGetMessageResponse.getData().isAllowAudioCall()))
                                    ivAudioView.setVisibility(View.VISIBLE);
                            } else {
                                ivAudioView.setVisibility(View.GONE);
                            }
                        }

                        setAgentName();
                        if (pageStart == 1) {
                            showLoading = false;
                            sentAtUTC = tempSentAtUtc;
                            CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                            if (fromCreateBotId) {
                                if (fuguMessageAdapter != null) {
                                    fuguMessageAdapter.updateList(fuguMessageList);
                                    fuguMessageAdapter.notifyDataSetChanged();
                                }
                            } else {
                                updateRecycler();
                            }
                            scrollListToBottom();

                            try {
                                updateKeyboard(messageResponseData.getMessages().get(messageResponseData.getMessages().size() - 1).getInputType());
                            } catch (Exception e) {

                            }

                        } else {
                            pbLoading.setVisibility(View.GONE);
                            fuguMessageAdapter.updateList(fuguMessageList, false);
                            fuguMessageAdapter.notifyItemRangeInserted(0, messageResponseData.getMessages().size() + dateCount);
                        }
                        fromCreateBotId = false;
                        pageStart = sentMessages.values().size() + 1 - dateItemCount;


                    } else {
                        allMessagesFetched = true;
                    }
                    //checkAutoSuggestions();
                    pbLoading.setVisibility(View.GONE);
                    isP2P = false;
                    setConnectionMessage(0);
                    getUnreadCount();
                    if (hasFormValue) {
                        sendMessage(etMsg.getText().toString().trim(), TEXT_MESSAGE, "", "", null, null, null);
                        hasFormValue = false;
                    }
                }

                @Override
                public void failure(APIError error) {
                    isApiRunning = false;
                    if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                        Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        pbLoading.setVisibility(View.GONE);
                        if (isP2P) {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            pbPeerChat.setVisibility(View.GONE);
                        } else if (pageStart == 1 &&
                                (CommonData.getMessageResponse(channelId) == null ||
                                        CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                            llRoot.setVisibility(View.GONE);
                            tvNoInternet.setVisibility(View.VISIBLE);
                        } else {
                            setConnectionMessage(6);
                        }

                        /*if (messagesApi != null && messagesApi.size() > 0) {
                            messagesApi.remove("getMessages");
                            isNetworkStateChanged = true;
                            apiGetMessages(channelName, isFromOnResume);
                        } else if (isP2P) {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            pbPeerChat.setVisibility(View.GONE);
                        } else if (pageStart == 1 &&
                                (CommonData.getMessageResponse(channelId) == null ||
                                        CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                            llRoot.setVisibility(View.GONE);
                            tvNoInternet.setVisibility(View.VISIBLE);
                        }*/
                    }
                }
            });
        }
    }

    // for self view
 /*   private boolean getViewType(int messageType, boolean isSelf, boolean quickValue) {
        switch (messageType) {
            case TEXT_MESSAGE:
            case IMAGE_MESSAGE:
            case ACTION_MESSAGE:
            case ACTION_MESSAGE_NEW:
            case FUGU_TEXT_VIEW:
                if(isSelf)
                    return true;
                return false;
            case HIPPO_FILE_VIEW:
                if(isSelf){
                    return true;
                }
            case FEEDBACK_MESSAGE:
                return true;
            case FUGU_QUICK_REPLY_VIEW:
                if(quickValue)
                    return true;
                return false;
            case FUGU_FORUM_VIEW:
                return false;
            case FUGU_SELF_VIDEO_VIEW:
                if(isSelf)
                    return true;
                return false;
            case HIPPO_USER_CONSENT:
                return false;
            default:
                if(isSelf)
                    return true;
                return false;
        }

        //return false;
    }
*/
//    private boolean hasUserLastMessage(int position, Message message, int messageViewType, Message nextMessage,
//                                       boolean hasDateView, boolean isSelf, boolean quickValue) {
//        boolean hasLastMessage = false;
////        if(hasDateView)
////            return true;
//        int type = messageViewType;
//        if(position == 0) {
//            if(nextMessage == null) {
//                hasLastMessage = true;
//            } else {
//                boolean isNextSelf = false;
//                if (nextMessage.getUserId().compareTo(message.getUserId()) != 0)
//                    hasLastMessage = true;
//                else
//                    hasLastMessage = false;
//            }
//        } else if(nextMessage == null) {
//            hasLastMessage = true;
//        } else {
//            if (nextMessage.getUserId().compareTo(message.getUserId()) != 0)
//                hasLastMessage = true;
//            else
//                hasLastMessage = false;
//        }
//        return hasLastMessage;
//    }

    private void setAgentName() {
        String agentName = "";

        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null &&
                !TextUtils.isEmpty(mFuguGetMessageResponse.getData().getAgentName())) {
            agentName = mFuguGetMessageResponse.getData().getAgentName();
        }
        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null && mFuguGetMessageResponse.getData().getOtherUsers() != null
                && mFuguGetMessageResponse.getData().getOtherUsers().size() > 0) {
            agentName = mFuguGetMessageResponse.getData().getOtherUsers().get(0).getFullName();
        }

        fullname = agentName;
        if (fuguMessageAdapter != null) {
            fuguMessageAdapter.setAgentName(agentName);
            if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null &&
                    mFuguGetMessageResponse.getData().getAgentId() != null && mFuguGetMessageResponse.getData().getAgentId().intValue() > 0) {
                fuguMessageAdapter.isAudioCallEnabled(mFuguGetMessageResponse.getData().isAllowAudioCall());
                fuguMessageAdapter.isVideoCallEnabled(mFuguGetMessageResponse.getData().isAllowVideoCall());
            }
        }
    }

    private void getByLabelId() {
        if (isNetworkAvailable()) {
            CommonData.clearQuickReplyData();
            if (!allMessagesFetched) {
                FuguGetByLabelIdParams commonParams = new FuguGetByLabelIdParams(HippoConfig.getInstance().getAppKey(),
                        labelId,
                        enUserId,
                        pageStart);

                if (CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping() == 1) {
                    commonParams.setMultiChannelLabelMapping(CommonData.getUpdatedDetails().getData().getMultiChannelLabelMapping());
                }

                if (!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
                    commonParams.setLang(HippoConfig.getInstance().getCurrentLanguage());

                if (sentMessages == null || sentMessages.size() == 0) {
                    showLoading = true;
                    setConnectionMessage(0);
                } else if (pageStart == 1) {
                    setConnectionMessage(1);
                }

                RestClient.getApiInterface().getByLabelId(commonParams)
                        .enqueue(new ResponseResolver<GetLabelMessageResponse>(FuguChatActivity.this, showLoading, false) {
                            @Override
                            public void success(GetLabelMessageResponse fuguGetMessageResponse) {
                                if (pageStart == 1) {
                                    fuguMessageList.clear();
                                    dateItemCount = 0;
                                }

                                mFuguGetMessageResponse = new FuguGetMessageResponse();
                                Data dataa = new Data();
                                dataa.setAgentId(fuguGetMessageResponse.getData().getAgentId());
                                dataa.setAgentImage(fuguGetMessageResponse.getData().getAgentImage());
                                dataa.setAgentName(fuguGetMessageResponse.getData().getAgentName());
                                if (!TextUtils.isEmpty(fuguGetMessageResponse.getData().getBotGroupId())) {
                                    dataa.setBotGroupId(Integer.parseInt(fuguGetMessageResponse.getData().getBotGroupId()));
                                }
                                dataa.setBusinessName(fuguGetMessageResponse.getData().getBusinessName());
                                dataa.setChannelImageUrl(fuguGetMessageResponse.getData().getChannelImageUrl());
                                dataa.setChatType(fuguGetMessageResponse.getData().getChatType());
                                dataa.setStatus(fuguGetMessageResponse.getData().getStatus());
                                dataa.setDisableReply(fuguGetMessageResponse.getData().getDisableReply());
                                mFuguGetMessageResponse.setData(dataa);

                                labelMessageResponse = fuguGetMessageResponse;
                                if (!TextUtils.isEmpty(fuguGetMessageResponse.getData().getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                } else if (!TextUtils.isEmpty(conversation.getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                }

                                isDisableReply = false;
                                if (fuguGetMessageResponse.getData().isDisableReply()) {
                                    llMessageLayout.setVisibility(View.GONE);
                                    isDisableReply = true;
                                }

                                try {
                                    if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null) {
                                        agentId = mFuguGetMessageResponse.getData().getAgentId();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (fuguGetMessageResponse.getData() != null && fuguGetMessageResponse.getData().getMessages() != null) {
                                    if (fuguMessageAdapter != null) {
                                        if (fuguMessageAdapter.getCurrentChatType() != fuguGetMessageResponse.getData().getChatType()) {
                                            fuguMessageAdapter.updateChatType(fuguGetMessageResponse.getData().getChatType());
                                            messageChatType = fuguGetMessageResponse.getData().getChatType();
                                        }
                                    }
                                    showLoading = false;

                                    LinkedHashMap<String, Message> tempMessages = new LinkedHashMap<>();
                                    LinkedHashMap<String, Message> tempSentMessages = new LinkedHashMap<>();

                                    String tempSentAtUtc = sentAtUTC;

                                    LabelData data = fuguGetMessageResponse.getData();
                                    if (data.getMessages().size() < data.getPageSize()) {
                                        allMessagesFetched = true;
                                    } else {
                                        allMessagesFetched = false;
                                    }


                                    HippoLog.d("userName in SDK", "getByLabelId " + userName);
                                    onSubscribe = data.getOnSubscribe();
                                    channelId = data.getChannelId();
                                    CommonData.setChannelId(labelId, channelId);
                                    HippoNotificationConfig.pushChannelId = data.getChannelId();
                                    conversation.setChannelId(data.getChannelId());
                                    currentChannelId = data.getChannelId();
                                    status = data.getStatus();
                                    businessName = data.getBusinessName();

                                    if (currentChannelId < 1) {
                                        CommonData.clearAllUnsentMessages();
                                        unsentMessages = new LinkedHashMap<>();
                                        unsentMessageMapNew = new LinkedHashMap<>();
                                    }

                                    HippoLog.e("getByLabelId onSubscribe", "==" + onSubscribe);

                                    isDisableReply = false;
                                    if (status == STATUS_CHANNEL_CLOSED) {
                                        isDisableReply = true;
                                        llMessageLayout.setVisibility(View.GONE);
                                        tvClosed.setVisibility(View.VISIBLE);
                                    } else {
                                        try {
                                            if (TextUtils.isEmpty(data.getInputType())) {
                                                updateKeyboard(data.getInputType());
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                    for (int i = 0; i < data.getMessages().size(); i++) {
                                        LabelMessage messageObj = data.getMessages().get(i);
                                        /*if((data.getBotGroupId() == null || data.getBotGroupId().intValue() < 1)
                                                && messageObj.getOtherLangData() != null) {
                                            label = messageObj.getOtherLangData().getChannelName();
                                        }*/
                                        if (messageObj.getOtherLangData() != null) {
                                            label = messageObj.getOtherLangData().getChannelName();
                                        }

                                        boolean isSelf = false;
                                        if (messageObj.getUserId().compareTo(userId) == 0)
                                            isSelf = true;

                                        String localDate = dateUtils.convertToLocal(messageObj.getSentAtUtc(), inputFormat, outputFormat);

                                        boolean hasDateView = false;
                                        if (!tempSentAtUtc.equalsIgnoreCase(localDate) && channelId > -1) {
                                            tempMessages.put(localDate, new Message(localDate, true));
                                            tempSentAtUtc = localDate;
                                            dateItemCount = dateItemCount + 1;
                                            hasDateView = true;
                                        }

                                        String muid = messageObj.getMuid();
                                        if (TextUtils.isEmpty(muid)) {
                                            if (messageObj.getId() > 0) {
                                                muid = TextUtils.isEmpty(String.valueOf(messageObj.getId()))
                                                        ? UUID.randomUUID().toString() : String.valueOf(messageObj.getId());
                                            } else {
                                                muid = UUID.randomUUID().toString();
                                            }
                                        }

                                        String messageStr = messageObj.getMessage();
                                        try {
                                            if (!TextUtils.isEmpty(messageObj.getMultiLangMessage())) {
                                                Pattern pattern = Pattern.compile("\\{\\{\\{(.*?)\\}\\}\\}");
                                                Matcher matcher = pattern.matcher(messageObj.getMultiLangMessage());
                                                if (matcher.find()) {
                                                    String key = matcher.group(1);
                                                    String value = Restring.getString(key);
                                                    if (!TextUtils.isEmpty(value)) {
                                                        String oldStr = "{{{" + key + "}}}";
                                                        messageStr = messageObj.getMultiLangMessage().replace(oldStr, value);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
                                        try {
                                            if ((TextUtils.isEmpty(data.getBotGroupId()) || Integer.parseInt(data.getBotGroupId()) < 1)
                                                    && messageObj.getOtherLangData() != null) {
                                                messageStr = messageObj.getOtherLangData().getChannelMessage();
                                            }
                                        } catch (Exception e) {

                                        }

                                        String removeLt = messageStr.replaceAll("<", "&lt;");
                                        String removeGt = removeLt.replaceAll(">", "&gt;");

                                        Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                                messageObj.getUserId(),
                                                removeGt,
                                                messageObj.getSentAtUtc(),
                                                isSelf,
                                                messageObj.getMessageStatus(),
                                                i,
                                                messageObj.getUrl(),
                                                messageObj.getThumbnailUrl(),
                                                messageObj.getMessageType(),
                                                muid);

                                        message.setUserType(messageObj.getUserType());
                                        message.setOriginalMessageType(messageObj.getMessageType());
                                        message.setUserImage(messageObj.getUserImage());

                                        if (messageObj.getCustomAction() != null) {
                                            message.setCustomAction(messageObj.getCustomAction());
                                        }

                                        if (!TextUtils.isEmpty(messageObj.getCallType())) {
                                            message.setCallType(messageObj.getCallType());
                                        }

                                        message.setIntegrationSource(messageObj.getIntegrationSource());

                                        if (messageObj.getMessageType() == FILE_MESSAGE) {
                                            message.setFileExtension(messageObj.getFileExtension());
                                            message.setFileName(messageObj.getFileName());
                                            message.setFileSize(messageObj.getFileSize());
                                            message.setFilePath(messageObj.getFilePath());
                                            message.setFileUrl(messageObj.getFileUrl());
                                            message.setDocumentType(messageObj.getDocumentType());
                                        } else if (messageObj.getMessageType() == FEEDBACK_MESSAGE) {
                                            message.setIsRatingGiven(messageObj.getIsRatingGiven());
                                            message.setTotalRating(messageObj.getTotalRating());
                                            message.setRatingGiven(messageObj.getRatingGiven());
                                            message.setComment(messageObj.getComment());
                                            message.setLineBeforeFeedback(messageObj.getLineBeforeFeedback());
                                            message.setLineAfterFeedback_1(messageObj.getLineAfterFeedback_1());
                                            message.setLineAfterFeedback_2(messageObj.getLineAfterFeedback_2());
                                        } else if (messageObj.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                                            message.setContentValue(messageObj.getContentValue());
                                            message.setDefaultActionId(messageObj.getDefaultActionId());
                                            message.setValues(messageObj.getValues());
                                            if (messageObj.getValues() != null && messageObj.getValues().size() > 0)
                                                continue;
                                        } else if (messageObj.getMessageType() == FUGU_FORUM_VIEW || messageObj.getMessageType() == FUGU_FORUM_TICKET_) {
                                            message.setContentValue(messageObj.getContentValue());
                                            if (messageObj.getMessageType() == FUGU_FORUM_TICKET_ &&
                                                    messageObj.getValues() != null &&
                                                    messageObj.getValues().size() > 0 &&
                                                    !Utils.isEmailValid(messageObj.getValues().get(0))) {
                                                message.setValues(new ArrayList<String>());
                                            } else
                                                message.setValues(messageObj.getValues());
                                            message.setId(messageObj.getId());
                                            message.setIsSkipEvent(messageObj.getIsSkipEvent());
                                            message.setIsSkipButton(messageObj.getIsSkipButton());
                                            message.setIsFromBot(messageObj.getIsFromBot());
                                            message.setIsActive(messageObj.getIsActive());
                                        } else if (messageObj.getMessageType() == HIPPO_USER_CONSENT) {
                                            message.setContentValue(messageObj.getContentValue());
                                            message.setId(messageObj.getId());
                                            message.setIsActive(messageObj.getIsActive());
                                            message.setSelectedBtnId(messageObj.getSelectedBtnId());
                                        } else if (messageObj.getMessageType() == CARD_LIST) {
                                            message.setContentValue(messageObj.getContentValue());
                                            message.setSelectedAgentId(messageObj.getSelectedAgentId());
                                            message.setFallbackText(messageObj.getFallbackText());

                                            if (messageObj.getContentValue() != null && messageObj.getContentValue().size() > 0) {
                                                if (i != 0) {
                                                    ArrayList<Message> items = new ArrayList<>();
                                                    items.addAll(tempMessages.values());
                                                    Message lastMessage = items.get(items.size() - 1);
                                                    lastMessage.setBelowUserId(-2L);
                                                    tempMessages.put(lastMessage.getMuid(), lastMessage);
                                                }
                                            } else {
                                                message.setMessage(messageObj.getFallbackText());
                                                messageObj.setMessageType(FUGU_TEXT_VIEW);
                                            }
                                        }

                                        int messageViewType = getType(messageObj.getMessageType(), isSelf, true, messageObj.getDocumentType());
                                        message.setMessageType(messageViewType);
                                        message.setVideoCallDuration(messageObj.getVideoCallDuration());
                                        message.setMessageState(messageObj.getMessageState());

                                        if (i != 0) {
                                            message.setAboveMuid(data.getMessages().get(i - 1).getMuid());
                                            message.setAboveUserId(data.getMessages().get(i - 1).getUserId());
                                        } else {
                                            message.setAboveMuid("");
                                            message.setAboveUserId(-1L);
                                        }

                                        if (i + 1 < data.getMessages().size()) {
                                            message.setBelowMuid(data.getMessages().get(i + 1).getMuid());
                                            message.setBelowUserId(data.getMessages().get(i + 1).getUserId());
                                        }

                                        if (hasDateView && i != 0) {
                                            ArrayList<Message> items = new ArrayList<>();
                                            items.addAll(tempMessages.values());
                                            Message lastMessage = items.get(items.size() - 2);
                                            lastMessage.setBelowMuid(localDate);
                                            lastMessage.setBelowUserId(-2L);
                                            tempMessages.put(lastMessage.getMuid(), lastMessage);
                                        }


                                        tempMessages.put(muid, message);
                                        tempSentMessages.remove(muid);

                                        if (!TextUtils.isEmpty(messageObj.getMuid())) {
                                            if (unsentMessageMapNew.size() > 0) {
                                                unsentMessageMapNew.remove(messageObj.getMuid());
                                            }
                                            if (unsentMessages.size() > 0) {
                                                unsentMessages.remove(messageObj.getMuid());
                                            }
                                        }
                                    }

                                    if (sentMessages.containsKey(tempSentAtUtc)) {
                                        sentMessages.remove(tempSentAtUtc);
                                        dateItemCount = dateItemCount - 1;
                                    }

                                    tempMessages.putAll(sentMessages);
                                    sentMessages.clear();
                                    sentMessages.putAll(tempMessages);

                                    // put local sent messages into updated sent list
                                    if (tempSentMessages.values().size() > 0) {
                                        long lastMessageTime = dateUtils.getTimeInLong(data.getMessages().get(data.getMessages().size() - 1).getSentAtUtc());
                                        if (lastMessageTime > 0) {
                                            for (String key : tempSentMessages.keySet()) {
                                                try {
                                                    if (!tempSentMessages.get(key).isDateView()) {
                                                        Message listItem = tempSentMessages.get(key);
                                                        long localMessageTime = dateUtils.getTimeInLong(listItem.getSentAtUtc());
                                                        Log.i(TAG, "localMessageTime: " + localMessageTime);
                                                        if (localMessageTime > lastMessageTime) {
                                                            sentMessages.put(listItem.getMuid(), listItem);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    tempSentMessages.clear();

                                    fuguMessageList = new ArrayList<>();
                                    fuguMessageList.addAll(sentMessages.values());

                                    for (String key : unsentMessages.keySet()) {
                                        Message listItem = unsentMessages.get(key);
                                        String time = listItem.getSentAtUtc();
                                        String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                                        if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                            fuguMessageList.add(new Message(localDate, true));
                                            tempSentAtUtc = localDate;
                                        }

                                        // update
                                        try {
                                            JSONObject messageJson = unsentMessageMapNew.get(key);
                                            if (messageJson != null) {
                                                messageJson.put("message_index", fuguMessageList.size());
                                                unsentMessageMapNew.put(key, messageJson);
                                                fuguMessageList.add(unsentMessages.get(key));
                                            } else {
                                                fuguMessageList.remove(key);
                                            }
                                        } catch (JSONException e) {

                                        }
                                    }


                                    tvNoInternet.setVisibility(View.GONE);
                                    llRoot.setVisibility(View.VISIBLE);
                                    if (conversation.getUnreadCount() > 0) {
                                        rvMessages.setAlpha(0);
                                    }
                                    if (pageStart == 1) {
                                        showLoading = false;
                                        sentAtUTC = tempSentAtUtc;
                                        //CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                                        fuguMessageAdapter.updateList(fuguMessageList);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fuguMessageAdapter.notifyDataSetChanged();
                                                rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                            }
                                        });

                                        try {
                                            updateKeyboard(data.getMessages().get(data.getMessages().size() - 1).getInputType());
                                        } catch (Exception e) {

                                        }

                                    } else {
                                        pbLoading.setVisibility(View.GONE);
                                        fuguMessageAdapter.notifyItemRangeInserted(0, data.getMessages().size());
                                    }
                                    pageStart = fuguMessageList.size() + 1 - dateItemCount;


                                    if (CommonData.getDirectCallBtnDisabled()) {
                                        ivVideoView.setVisibility(View.GONE);
                                        ivAudioView.setVisibility(View.GONE);
                                    } else {
                                        if (CommonData.getVideoCallStatus() && fuguGetMessageResponse.getData() != null) {
                                            if ((fuguGetMessageResponse.getData().isAllowVideoCall() && fuguGetMessageResponse.getData().getAgentId() != null
                                                    && fuguGetMessageResponse.getData().getAgentId().intValue() > 0)
                                                    && (fuguGetMessageResponse.getData().isAllowVideoCall()))
                                                ivVideoView.setVisibility(View.VISIBLE);
                                        } else {
                                            ivVideoView.setVisibility(View.GONE);
                                        }

                                        if (CommonData.getAudioCallStatus() && fuguGetMessageResponse.getData() != null) {
                                            if ((fuguGetMessageResponse.getData().isAllowAudioCall() && fuguGetMessageResponse.getData().getAgentId() != null
                                                    && fuguGetMessageResponse.getData().getAgentId().intValue() > 0)
                                                    && (fuguGetMessageResponse.getData().isAllowAudioCall()))
                                                ivAudioView.setVisibility(View.VISIBLE);
                                        } else {
                                            ivAudioView.setVisibility(View.GONE);
                                        }
                                    }


                                } else {
                                    allMessagesFetched = true;
                                    fuguMessageList.clear();
                                }

                                setToolbar(label);

                                pbLoading.setVisibility(View.GONE);
                                getUnreadCount();

                                if (channelId > -1) {
                                    ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(channelId));
                                    //ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

                                    pageStart = 1;
                                    isApiRunning = false;
                                    allMessagesFetched = false;
                                }

                                int botId = 0;
                                if (fuguGetMessageResponse.getData() != null && fuguGetMessageResponse.getData().getCreateNewChannel() == 1) {
                                    if (!TextUtils.isEmpty(fuguGetMessageResponse.getData().getBotGroupId()) && Integer.parseInt(fuguGetMessageResponse.getData().getBotGroupId()) > 0)
                                        botId = Integer.parseInt(fuguGetMessageResponse.getData().getBotGroupId());
                                    CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                                            .setMessageType(22)
                                            .setText(etMsg.getText().toString().trim())
                                            .setIsP2P(isP2P)
                                            .setFuguGetMessageResponse(mFuguGetMessageResponse)
                                            .setGetLabelMessageResponse(labelMessageResponse)
                                            .setIsPaymentBot(true)
                                            .setBotId(botId)
                                            .setConversationParams(fuguCreateConversationParams)
                                            .build();

                                    createConversation(attribute);
                                }

                                //HippoConfig.getInstance().checkAutoUpdate(FuguChatActivity.this);
                            }

                            @Override
                            public void failure(APIError error) {
                                HippoLog.e("error type", error.getType() + "");
                                if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                    Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if ((error.getStatusCode() == DATA_UNAVAILABLE && error.getType() == 1)) {
                                    ChatByUniqueIdAttributes attributes = new ChatByUniqueIdAttributes.Builder()
                                            .setTransactionId("7865")
                                            .setUserUniqueKey(HippoConfig.getInstance().getUserData().getUserUniqueKey())
                                            .setChannelName("Fugu Default")
                                            .setTags(null)
                                            .build();
                                    HippoConfig.getInstance().openChatByUniqueId(attributes);
                                } else {
                                    if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                                            || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                                        llRoot.setVisibility(View.GONE);
                                        tvNoInternet.setVisibility(View.VISIBLE);
                                    }

                                    pbLoading.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else {
            if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                    || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void setRecyclerViewData() {
        fuguMessageAdapter.notifyDataSetChanged();
        fuguMessageAdapter.setOnRetryListener(new FuguMessageAdapter.OnRetryListener() {
            @Override
            public void onRetry(String file, int messageIndex, int messageType, FuguFileDetails fileDetails, String muid) {
                //uploadFileServerCall(file, "image/*", messageIndex, muid);
            }

            @Override
            public void onMessageRetry(String muid, int position) {
                try {
                    JSONObject jsonObject = unsentMessageMapNew.get(muid);

                    if (jsonObject == null) {
                        return;
                    }
                    Message listItem = unsentMessages.get(muid);

                    jsonObject.put("is_message_expired", 0);
                    jsonObject.put("message_index", position);
                    String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                    jsonObject.put("date_time", DateUtils.getInstance().convertToUTC(localDate));

                    listItem.setIsMessageExpired(0);
                    listItem.setMessageIndex(position);
                    listItem.setSentAtUtc(DateUtils.getInstance().convertToUTC(localDate));

                    unsentMessageMapNew.put(muid, jsonObject);
                    unsentMessages.put(muid, listItem);
                    fuguMessageList.remove(position);
                    fuguMessageList.add(position, listItem);

                    updateRecycler();
                    if (fuguMessageAdapter != null) {
                        fuguMessageAdapter.updateList(fuguMessageList);
                    }
                    fuguMessageAdapter.notifyItemRangeChanged(position, fuguMessageList.size());

                    sendMessages();
                } catch (Exception e) {

                }
            }

            @Override
            public void onMessageCancel(String muid, int position) {
                fuguMessageList.remove(position);
                fuguMessageAdapter.notifyItemRemoved(position);
                boolean isItemFound = false;


                for (String key : unsentMessageMapNew.keySet()) {
                    if (key.equalsIgnoreCase(muid)) {
                        isItemFound = true;
                        continue;
                    }
                    if (isItemFound) {
                        try {
                            Message listItem = unsentMessages.get(key);
                            int index = listItem.getMessageIndex();
                            listItem.setMessageIndex(index - 1);
                            JSONObject jsonObject = unsentMessageMapNew.get(key);
                            jsonObject.put("message_index", index);
                            unsentMessageMapNew.put(key, jsonObject);
                            unsentMessages.put(key, listItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                unsentMessageMapNew.remove(muid);
                unsentMessages.remove(muid);

                try {
                    String data = Prefs.with(FuguChatActivity.this).getString(KEY, "");
                    ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(data, fileuploadType);
                    for (int i = 0; i < fileuploadModels.size(); i++) {
                        FileuploadModel fileuploadModel = fileuploadModels.get(i);
                        if (fileuploadModel.getMuid().equalsIgnoreCase(muid)) {
                            fileuploadModels.remove(i);
                            String dataNew = new Gson().toJson(fileuploadModels, fileuploadType);
                            Prefs.with(FuguChatActivity.this).save(KEY, dataNew);
                            return;
                        }
                    }
                } catch (Exception e) {
                    if (HippoConfig.DEBUG)
                        e.printStackTrace();
                }

            }

            @Override
            public void onFileMessageRetry(final String muid, final int position) {
                try {
                    ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(Prefs.with(FuguChatActivity.this).getString(KEY, ""), fileuploadType);
                    if (fileuploadModels == null)
                        fileuploadModels = new ArrayList<>();

                    JSONObject jsonObject = unsentMessageMapNew.get(muid);

                    if (jsonObject == null) {
                        String text = Restring.getString(FuguChatActivity.this, R.string.hippo_something_went_wrong);
                        String ok = Restring.getString(FuguChatActivity.this, R.string.fugu_ok);
                        String cancel = Restring.getString(FuguChatActivity.this, R.string.fugu_cancel);
                        new AlertDialog.Builder(FuguChatActivity.this)
                                .setMessage(text)
                                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        onMessageCancel(muid, position);
                                    }
                                })
                                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setCancelable(false)
                                .show();
                        return;
                    }
                    Message listItem = unsentMessages.get(muid);

                    jsonObject.put("is_message_expired", 0);
                    jsonObject.put("message_index", position);
                    String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                    jsonObject.put("date_time", DateUtils.getInstance().convertToUTC(localDate));

                    listItem.setIsMessageExpired(0);
                    listItem.setMessageIndex(position);
                    listItem.setSentAtUtc(DateUtils.getInstance().convertToUTC(localDate));
                    listItem.setMessageStatus(MESSAGE_UNSENT);
                    listItem.setUploadStatus(FuguAppConstant.UPLOAD_IN_PROGRESS);

                    unsentMessageMapNew.put(muid, jsonObject);
                    unsentMessages.put(muid, listItem);
                    fuguMessageList.remove(position);
                    fuguMessageList.add(position, listItem);

                    updateRecycler();
                    if (fuguMessageAdapter != null) {
                        fuguMessageAdapter.updateList(fuguMessageList);
                    }
                    fuguMessageAdapter.notifyItemRangeChanged(position, fuguMessageList.size());

                    if (!TextUtils.isEmpty(jsonObject.optString("local_url"))) {
                        String fileName = jsonObject.optString("file_name", "");
                        String fileSize = jsonObject.optString("file_size");
                        String filePath = jsonObject.optString("local_url");

                        FileuploadModel fileuploadModel = new FileuploadModel(fileName, fileSize, filePath, muid);

                        fileuploadModel.setChannelId(channelId);
                        fileuploadModel.setFileUploaded(false);
                        fileuploadModel.setDocumentType(jsonObject.optString("document_type"));
                        fileuploadModel.setMessageIndex(jsonObject.optInt("message_index"));
                        fileuploadModel.setMessageType(jsonObject.optInt("message_type"));
                        fileuploadModel.setMessageObject(jsonObject);

                        if (jsonObject.optInt("message_type") == 10) {
                            ArrayList<Integer> integers = new ArrayList<>();
                            integers.add(jsonObject.optInt("image_height"));
                            integers.add(jsonObject.optInt("image_width"));
                            fileuploadModel.setDimns(integers);
                        }

                        uploadFile(fileuploadModel);
                    } else {
                        if (isNetworkAvailable()) {
                            sendMessages();
                        }
                    }

                    //sendMessages();
                } catch (JSONException e) {

                }
            }
        });

        // Add the scroll listener
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (ConnectionManager.INSTANCE.isConnected()) {
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                    if (unsentMessages.size() == 0) {
                        getMessages(null);
                    }
                }
            }
        });

        fuguMessageAdapter.setOnVideoCallListener(new FuguMessageAdapter.onVideoCall() {
            @Override
            public void onVideoCallClicked(int callType) {
                videoCallInit(callType);
            }
        });


        String agentName = "";
        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null &&
                !TextUtils.isEmpty(mFuguGetMessageResponse.getData().getAgentName())) {
            agentName = mFuguGetMessageResponse.getData().getAgentName();
        }
        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null && mFuguGetMessageResponse.getData().getOtherUsers() != null
                && mFuguGetMessageResponse.getData().getOtherUsers().size() > 0) {
            agentName = mFuguGetMessageResponse.getData().getOtherUsers().get(0).getFullName();
        }

        if (fuguMessageAdapter != null)
            fuguMessageAdapter.setAgentName(agentName);
    }

    private boolean isAllowVideoCall() {
        try {
            if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData().isAllowVideoCall())
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        if (container.getVisibility() == View.VISIBLE) {
            getSupportFragmentManager().beginTransaction().remove(LoadingFragment.getInstance());
            container.setVisibility(View.GONE);
            return;
        }
        CommonData.clearQuickReplyData();
        if (ConnectionManager.INSTANCE.isConnected()) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
            ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), prepareMessageJson(CHANNEL_UNSUBSCRIBED));
        }

        boolean hasPager = false;
        try {
            hasPager = CommonData.getAttributes().getAdditionalInfo().isHasChannelPager();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (Prefs.with(FuguChatActivity.this).getBoolean("direct_screen", false)) {
            Prefs.with(FuguChatActivity.this).save("direct_screen", false);
            startActivity(new Intent(FuguChatActivity.this, ChannelActivity.class));
            /*if(hasPager) {
                startActivity(new Intent(FuguChatActivity.this, ChannelActivity.class));
            } else {
                startActivity(new Intent(FuguChatActivity.this, FuguChannelsActivity.class));
            }*/
        } else {
            Intent intent = new Intent();
            if (fuguMessageList.size() > 0) {
                conversation.setChannelId(channelId);
                conversation.setLabelId(labelId);
                //conversation.setChannelImage(fuguMessageList.get(fuguMessageList.size() - 1).get);
                conversation.setDefaultMessage(fuguMessageList.get(fuguMessageList.size() - 1).getMessage());
                conversation.setDateTime(fuguMessageList.get(fuguMessageList.size() - 1).getSentAtUtc());
                conversation.setLast_sent_by_id(fuguMessageList.get(fuguMessageList.size() - 1).getUserId());
                conversation.setLast_message_status(fuguMessageList.get(fuguMessageList.size() - 1).getMessageStatus());
                conversation.setMessage_type(fuguMessageList.get(fuguMessageList.size() - 1).getOriginalMessageType());
                intent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onAnimationStart(Animation animation) {
//        if (!(tvDateLabel.getVisibility() == View.VISIBLE)) {
//            tvDateLabel.clearAnimation();
//        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * Called when a custom action button is clicked
     *
     * @param buttonAction the action button object associated with this button
     */
    public void onCustomActionClicked(final Object buttonAction) {
        //send a broadcast to listening parent app
        String payload = new Gson().toJson(buttonAction);
        Intent intent = new Intent();
        intent.putExtra(FUGU_CUSTOM_ACTION_PAYLOAD, payload);
        intent.setAction(FUGU_CUSTOM_ACTION_SELECTED);
        sendBroadcast(intent);

    }

    private void getUnreadCount() {
    }

    @Override
    public void onSubmitRating(String text, Message currentOrderItem, int position) {
        try {
            sendFeedbackData(currentOrderItem, position);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        String error = Restring.getString(FuguChatActivity.this, R.string.hippo_error_msg_sending);
        String ok = Restring.getString(FuguChatActivity.this, R.string.fugu_ok);

        new CustomAlertDialog.Builder(FuguChatActivity.this)
                .setMessage(error)
                .setPositiveButton(ok, null)
                .show();
    }

    @Override
    public void onRatingSelected(int rating, Message currentOrderItem) {
        currentOrderItem.setRatingGiven(rating);
    }

    @Override
    public void onFormDataCallback(Message currentOrderItem) {
        try {
            sendFormData(currentOrderItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTicketdataCallback(Message currentOrderItem, int position) {
        try {
            sendFormData(currentOrderItem, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSkipForm(Message message) {
        try {
            skipBotFormData(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFeedbackData(Message currentOrderItem, int position) throws Exception {
        if (isNetworkAvailable()) {
            //currentOrderItem.setMessageType(currentOrderItem.getOriginalMessageType());
            currentOrderItem.setIsRatingGiven(1);
            currentOrderItem.setTotalRating(5);
            //currentOrderItem.setUserId(CommonData.getUserDetails().getData().getUserId());
            JSONObject messageJson = new JSONObject(new Gson().toJson(currentOrderItem));
            messageJson.put(MESSAGE_TYPE, currentOrderItem.getOriginalMessageType());
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }
            HippoLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            if (channelId < 1) {
                String botMessageMuid = UUID.randomUUID().toString();
                messageJson.put(MESSAGE_UNIQUE_ID, botMessageMuid);
                Paper.book(HIPPO_PAPER_NAME).write("hippo_bot_message", messageJson);
                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setMessageType(14)
                        .setBotMessageMuid(botMessageMuid)
                        .setMessage(currentOrderItem)
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .setJsonObject(messageJson)
                        .setIsP2P(isP2P)
                        .build();
                createConversation(attribute);
                return;
            }
            boolean canReply = false;
            try {
                canReply = CommonData.getAttributes().getAdditionalInfo().istReplyOnFeedback();
            } catch (Exception e) {

            }
            if (getView() || canReply) {
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            }
        }
    }

    private void skipBotFormData(Message message) throws Exception {
        if (isNetworkAvailable()) {

            List<String> arrayList = new ArrayList<>();
            if (message.getValues() != null)
                arrayList.addAll(message.getValues());

//            if(!TextUtils.isEmpty(message.getComment()))
//                arrayList.add(message.getComment());

            //message.setMessageType(message.getOriginalMessageType());
            message.setValues((ArrayList<String>) arrayList);
            message.setComment("");

            message.setUserId(CommonData.getUserDetails().getData().getUserId());
            message.setMessageId(message.getId());

            message.setIsSkipEvent(1);
            message.setIsSkipButton(0);

            JSONObject messageJson = new JSONObject(new Gson().toJson(message));
            messageJson.put(MESSAGE_TYPE, message.getOriginalMessageType());
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }

            HippoLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            if (channelId < 1) {
                String botMessageMuid = UUID.randomUUID().toString();
                messageJson.put(MESSAGE_UNIQUE_ID, botMessageMuid);
                Paper.book(HIPPO_PAPER_NAME).write("hippo_bot_message", messageJson);
                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setMessageType(17)
                        .setBotMessageMuid(botMessageMuid)
                        .setMessage(message)
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .setJsonObject(messageJson)
                        .setIsP2P(isP2P)
                        .build();
                createConversation(attribute);
//                createConversation(17, "", "", null, isP2P, null, botMessageMuid, message);
                return;
            }
            if (getView()) {
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            }
            /*mClient.setListener(this);
            if (mClient.isConnectedServer()) {
            } else {
                mClient.connectServer();
            }*/
        }
    }

    private void sendFormData(Message message) throws Exception {
        sendFormData(message, -1);
    }

    private void sendFormData(Message message, int pos) throws Exception {
        if (isNetworkAvailable()) {

            List<String> arrayList = new ArrayList<>();
            if (message.getValues() != null)
                arrayList.addAll(message.getValues());

            if (pos == -1)
                arrayList.add(message.getComment());
            else {
                if (pos < message.getValues().size())
                    arrayList.set(pos, message.getComment());
                else
                    arrayList.add(message.getComment());

                Log.e("pos>>>>>>", pos + "");
            }

            message.setValues((ArrayList<String>) arrayList);
            message.setComment("");


            message.setUserId(CommonData.getUserDetails().getData().getUserId());
            message.setMessageId(message.getId());

            JSONObject messageJson = new JSONObject(new Gson().toJson(message));
            messageJson.put(MESSAGE_TYPE, message.getOriginalMessageType());
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }
            if (messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_TICKET_) {
                int is_ticket_creation = 0;
                if (message.getContentValue().get(0).getQuestions().size() == message.getValues().size())
                    is_ticket_creation = 1;
                messageJson.put("is_ticket_creation", is_ticket_creation);
            }


            HippoLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            if (channelId < 1) {
                String botMessageMuid = UUID.randomUUID().toString();
                messageJson.put(MESSAGE_UNIQUE_ID, botMessageMuid);
                message.setMuid(botMessageMuid);

                Paper.book(HIPPO_PAPER_NAME).write("hippo_bot_message", messageJson);

                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setMessageType(17)
                        .setBotMessageMuid(botMessageMuid)
                        .setMessage(message)
                        .setJsonObject(messageJson)
                        .setIsP2P(isP2P)
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .build();

                createConversation(attribute);
//                createConversation(17, "", "", null, isP2P, null, botMessageMuid, message);
                return;
            }
            if (getView()) {
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            }
            /*mClient.setListener(this);
            if (mClient.isConnectedServer()) {
            } else {
                mClient.connectServer();
            }*/
        }
    }

    private void sendQuickReply(Message message, int position, String defaultActionId) throws Exception {

        if (isNetworkAvailable() && channelId.intValue() > -1) {
            List<String> arrayList = new ArrayList<>();
            arrayList.add(defaultActionId);
            message.setValues((ArrayList<String>) arrayList);

            message.setUserId(CommonData.getUserDetails().getData().getUserId());
            if (message.getId() > 0) {
                message.setMessageId(message.getId());
            }

            JSONObject messageJson = new JSONObject(new Gson().toJson(message));
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(MESSAGE_TYPE, FUGU_QUICK_REPLY_VIEW);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }
            messageJson.put("bot_button_reply", 1);
            HippoLog.d("userName in SDK", "currentOrderItem for bot " + new Gson().toJson(messageJson));
            if (getView()) {
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            }
            /*mClient.setListener(this);
            if (mClient.isConnectedServer()) {
            } else {
                mClient.connectServer();
            }*/
        }
    }


    @Override
    public void QuickReplyListener(Message message, int pos) {
        try {
            if (channelId.compareTo(-1L) > 0) {
                fuguMessageList.remove(message);
                conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                isTyping = TYPING_SHOW_MESSAGE;
                globalUuid = UUID.randomUUID().toString();
                publishOnFaye(message.getContentValue().get(pos).getButtonTitle(), TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty),
                        null, NOTIFICATION_DEFAULT, globalUuid);
            } else {
                //if (mClient.isConnectedServer()) {
                if (!isConversationCreated) {
                    conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                    HippoLog.v("call createConversation", "onClick");
                    CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                            .setMessageType(TEXT_MESSAGE)
                            .setText(message.getContentValue().get(pos).getButtonTitle())
                            .setIsP2P(isP2P)
                            .setFuguGetMessageResponse(mFuguGetMessageResponse)
                            .setGetLabelMessageResponse(labelMessageResponse)
                            .setConversationParams(fuguCreateConversationParams)
                            .build();

                    createConversation(attribute);
//                        createConversation(TEXT_MESSAGE, "", "", null, isP2P);
                }
                /*} else {
                    mClient.setListener(this);
                    mClient.connectServer();
                    Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                }*/
            }

            CommonData.clearQuickReplyData();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            sendQuickReply(message, pos, message.getContentValue().get(pos).getActionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendActionId(Message event) {
        try {
            sendQuickReply(event, position, event.getDefaultActionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProfileClicked(Message message, String userId, int pos) {
        //HippoLog.v(TAG, "message = " + new Gson().toJson(message));
        /*try {
            try {
                if(channelId>0) {
                    ContentValue value = message.getContentValue().get(pos);
                    HippoUserProfileModel profileModel = new HippoUserProfileModel(value.getImageUrl(), enUserId, channelId,
                            value.getTitle());

                    profileModel.setDescription(value.getDescription());
                    profileModel.setUserId(userId);

                    Intent intent = new Intent(FuguChatActivity.this, ProfileActivity.class);
                    intent.putExtra("profileModel", profileModel);
                    startActivityForResult(intent, Constant.REQUEST_CODE_IMAGE_VIEW);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }*/
    }

    @Override
    public void onCardClicked(Message message, String userId, int pos) {
        HippoLog.v(TAG, "message = " + new Gson().toJson(message));
        try {
            sendSelectedCard(position, message, userId);
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    private void updateUI(final JSONObject messageJson) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            if (messageJson.has("restrict_personal_info_sharing")) {
                                checkValidMsg = messageJson.optInt("restrict_personal_info_sharing") == 1;
                            }
                        } catch (Exception e) {

                        }

                        if (channelId.compareTo(messageJson.optLong("channel_id")) == 0 &&
                                messageJson.optInt("close_the_chat") == 1) {
                            onBackPressed();
                            //finish();
                        } else {
                            if (channelId.compareTo(messageJson.optLong("channel_id")) == 0) {
                                ivVideoView.setVisibility(View.GONE);
                                ivAudioView.setVisibility(View.GONE);
                                if (isUserExists(messageJson) && messageJson.optInt("is_customer_allowed_to_initiate_video_call") == 1) {
                                    if (CommonData.getDirectCallBtnDisabled()) {
                                        ivVideoView.setVisibility(View.GONE);
                                        ivAudioView.setVisibility(View.GONE);
                                    } else {
                                        if (CommonData.getVideoCallStatus() && messageJson.optInt("allow_video_call", 0) == 1) {
                                            if (!CommonData.getDirectCallBtnDisabled()) {
                                                ivVideoView.setVisibility(View.VISIBLE);
                                            }

                                            if (fuguMessageAdapter != null)
                                                fuguMessageAdapter.isVideoCallEnabled(true);
                                        }
                                        if (CommonData.getAudioCallStatus() && messageJson.optInt("allow_audio_call", 0) == 1) {
                                            if (!CommonData.getDirectCallBtnDisabled()) {
                                                ivAudioView.setVisibility(View.VISIBLE);
                                            }
                                            if (fuguMessageAdapter != null)
                                                fuguMessageAdapter.isAudioCallEnabled(true);
                                        }
                                    }

                                    if (fuguMessageAdapter != null) {
                                        String agentName = mFuguGetMessageResponse.getData().getOtherUsers().get(0).getFullName();
                                        fuguMessageAdapter.setAgentName(agentName);
                                    }
                                } else {
                                    if (messageJson.optInt("allow_video_call", 0) == 1) {
                                        ivVideoView.setVisibility(View.VISIBLE);
                                    }
                                    if (messageJson.optInt("allow_audio_call", 0) == 1) {
                                        ivAudioView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            if (channelId.compareTo(messageJson.optLong("channel_id")) == 0 &&
                                    messageJson.optInt("disable_reply", 0) == 1) {
                                conversation.setDisableReply(messageJson.optInt("disable_reply", 0));
                                if (conversation.isDisableReply()) {
                                    llMessageLayout.setVisibility(View.GONE);
                                } else {
                                    llMessageLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            String channelImageUrl = messageJson.optString("channel_image_url");
                            String label = messageJson.optString("label", "");

                            mFuguGetMessageResponse.getData().setChannelImageUrl(channelImageUrl);
                            CommonData.saveVideoCallAgent(channelId, mFuguGetMessageResponse);
                            if (!TextUtils.isEmpty(label)) {
                                setToolbar(label);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUserExists(JSONObject messageJson) throws Exception {
        List<OtherUser> otherUsers = new ArrayList<>();
        try {
            JSONArray array = messageJson.optJSONArray("all_users");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (userId.compareTo(object.optLong("user_id")) != 0) {
                    OtherUser user = new OtherUser();
                    user.setFullName(object.optString("full_name"));
                    user.setUserId(object.optInt("user_id"));
                    user.setUserImage(object.optString("user_image", ""));
                    otherUsers.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFuguGetMessageResponse = CommonData.getSingleAgentData(channelId);
        try {
            JSONArray array = messageJson.optJSONArray("all_users");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (CommonData.getUserDetails().getData().getUserId().compareTo(object.optLong("user_id")) != 0) {
                    agentId = object.optLong("user_id");
                    try {
                        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null) {
                            mFuguGetMessageResponse.getData().setAgentId(agentId);
                            CommonData.saveVideoCallAgent(mFuguGetMessageResponse.getData().getChannelId(), mFuguGetMessageResponse);
                        } else {
                            mFuguGetMessageResponse = new FuguGetMessageResponse();
                            Data data = new Data();
                            data.setChannelId(channelId);
                            data.setAgentId(agentId);
                            mFuguGetMessageResponse.setData(data);
                            CommonData.saveVideoCallAgent(channelId, mFuguGetMessageResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (otherUsers.size() > 0) {
            //mFuguGetMessageResponse = CommonData.getSingleAgentData(channelId);
            if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null) {
                mFuguGetMessageResponse.getData().setOtherUsers(new ArrayList());
                mFuguGetMessageResponse.getData().setOtherUsers(otherUsers);

                if (messageJson.optInt("allow_video_call", 0) == 1) {
                    mFuguGetMessageResponse.getData().setAllowVideoCall(1);
                }
                if (messageJson.optInt("allow_audio_call", 0) == 1) {
                    mFuguGetMessageResponse.getData().setAllowAudioCall(1);
                }
                CommonData.saveVideoCallAgent(mFuguGetMessageResponse.getData().getChannelId(), mFuguGetMessageResponse);
            } else {
                mFuguGetMessageResponse = new FuguGetMessageResponse();
                Data data = new Data();
                data.setChannelId(channelId);
                data.setOtherUsers(otherUsers);

                if (messageJson.optInt("allow_video_call", 0) == 1) {
                    data.setAllowVideoCall(1);
                }
                if (messageJson.optInt("allow_audio_call", 0) == 1) {
                    data.setAllowAudioCall(1);
                }
                mFuguGetMessageResponse.setData(data);
                CommonData.saveVideoCallAgent(channelId, mFuguGetMessageResponse);
            }
            return true;
        }
        return false;
    }

    boolean isContentValueNull = false;
    String cardMuid = "";

    public void onReceivedMessage(String msg, String channel) {
        try {
            if (!channel.equalsIgnoreCase("/" + String.valueOf(channelId))) {
                HippoLog.e(TAG, "Other channel id message received");
                return;
            }
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            if (!taskList.get(0).topActivity.getClassName().equals("com.hippo.activity.FuguChatActivity")) {
                HippoLog.e(TAG, "FuguChatActivity false");
                return;
            }
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }

        Log.e(TAG, "FuguMessage >>: " + msg);
        HippoLog.e(TAG, "channel" + channel);

        boolean isSelf = false;
        try {
            final JSONObject messageJson = new JSONObject(msg);
            int notificationType = messageJson.optInt(NOTIFICATION_TYPE, 0);
            try {
                if (notificationType != 24 && messageJson.optInt(MESSAGE_TYPE, 0) == 1 && TextUtils.isEmpty(messageJson.optString("message"))
                        && !messageJson.has("is_typing")) {
                    return;
                }
                if (retryLayout.getVisibility() == View.VISIBLE) {
                    setConnectionMessage(0);
                }
            } catch (Exception e) {

            }


            if (notificationType == 3 || notificationType == 4) {
                return;
            } else if (notificationType == 1) {
                updateKeyboard(messageJson.optString("input_type", "DEFAULT"));
            } else if (notificationType == 24) {
                updateEdittedMessage(messageJson);
                return;
            }

            if ((messageJson.optInt(IS_TYPING) == TYPING_STOPPED) && !messageJson.optString(USER_ID).equals(String.valueOf(userId))) {
                HippoLog.v("onReceivedMessage", "in elseIf stopAnim");
                stopAnim();
            }
            if (!String.valueOf(messageJson.optString("user_id")).equals(String.valueOf(userId)) && messageJson.has("on_subscribe")) {
                onSubscribe = messageJson.getInt("on_subscribe");
                HippoLog.e("onReceivedMessage onSubscribe", "==" + onSubscribe);
            }
            try {
                if (messageJson.optInt(MESSAGE_TYPE, 0) == 14) {
                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                        if (fuguMessageList.get(i).getType() == ITEM_TYPE_RATING) {
                            Message currentMessage = fuguMessageList.get(i);
                            if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                messageSending = false;
                                currentMessage.setRatingGiven(messageJson.getInt("rating_given"));
                                currentMessage.setComment(messageJson.getString("comment"));
                                currentMessage.setIsRatingGiven(messageJson.getInt("is_rating_given"));
                                currentMessage.setTotalRating(messageJson.getInt("total_rating"));

                                currentMessage.setLineBeforeFeedback(messageJson.getString("line_before_feedback"));
                                currentMessage.setLineAfterFeedback_1(messageJson.getString("line_after_feedback_1"));
                                currentMessage.setLineAfterFeedback_2(messageJson.getString("line_after_feedback_2"));

                                updateFeedback(i);
                                removeItemAndUpdateUI();
                                return;
                            }
                        }
                    }
                } else if ((messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_VIEW) && !messageJson.has("id")) {
                    return;
                } else if ((messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_VIEW || messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_TICKET_)) {
                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                        //for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == FUGU_FORUM_VIEW || fuguMessageList.get(i).getType() == FUGU_FORUM_TICKET_) {
                            Message currentMessage = fuguMessageList.get(i);
                            if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                messageSending = false;
                                ArrayList<String> values = new ArrayList<>();
                                JSONArray valuesArray = messageJson.getJSONArray("values");

                                if (valuesArray != null) {
                                    for (int b = 0; b < valuesArray.length(); b++) {
                                        values.add(valuesArray.getString(b));
                                    }
                                }
                                currentMessage.setValues(values);

                                if (currentMessage.getMessageType() == FUGU_FORUM_TICKET_ &&
                                        currentMessage.getValues() != null &&
                                        currentMessage.getValues().size() > 0 &&
                                        !Utils.isEmailValid(currentMessage.getValues().get(0))) {
                                    currentMessage.setValues(new ArrayList<String>());
                                } else
                                    currentMessage.setValues(values);


                                if (messageJson.has("is_skip_button"))
                                    currentMessage.setIsSkipButton(messageJson.optInt("is_skip_button"));

                                if (messageJson.has("is_skip_event"))
                                    currentMessage.setIsSkipEvent(messageJson.optInt("is_skip_event"));

                                if (messageJson.has("is_active"))
                                    currentMessage.setIsActive(messageJson.optInt("is_active"));

                                if (messageJson.has("is_from_bot"))
                                    currentMessage.setIsFromBot(messageJson.optInt("is_from_bot"));

                                try {
                                    if (values != null && values.size() == currentMessage.getContentValue().get(0).getQuestions().size()) {
                                        currentMessage.setIsSkipButton(0);
                                        currentMessage.setIsSkipEvent(1);
                                        //currentMessage.setIsActive(0);
                                    }
                                } catch (Exception e) {

                                }

                                if (messageJson.has("full_name") && !TextUtils.isEmpty(messageJson.optString("full_name")))
                                    currentMessage.setFromName(messageJson.optString("full_name"));

                                if (messageJson.has("user_id"))
                                    currentMessage.setUserId(messageJson.optLong("user_id"));

                                if (messageJson.has("user_image"))
                                    currentMessage.setUserImage(messageJson.optString("user_image"));

                                if (values != null) {
                                    updateFeedback(i, false);
                                    return;
                                }
                                removeItemAndUpdateUI();
                                return;
                            }
                            /*if (currentMessage.getId() == messageJson.getLong("id")) {

                                ArrayList<String> values = new ArrayList<>();
                                JSONArray valuesArray = messageJson.getJSONArray("values");

                                if (valuesArray != null) {
                                    for (int b = 0; b < valuesArray.length(); b++) {
                                        values.add(valuesArray.getString(b));
                                    }
                                }
                                currentMessage.setValues(values);
                                if (values != null) {
                                    updateFeedback(i, false);
                                    return;
                                }
                                removeItemAndUpdateUI();
                                return;
                            }*/
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_QUICK_REPLY_VIEW) {
                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                        if (!fuguMessageList.get(i).isDateView()) {
                            Message currentMessage = fuguMessageList.get(i);
                            if (currentMessage.getId() == messageJson.getLong("id")) {
                                ArrayList<String> values = new ArrayList<>();
                                JSONArray valuesArray = messageJson.getJSONArray("values");

                                if (valuesArray != null) {
                                    for (int b = 0; b < valuesArray.length(); b++) {
                                        values.add(valuesArray.getString(b));
                                    }
                                }
                                currentMessage.setValues(values);
                            }
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == HIPPO_USER_CONSENT) {
                    if (!TextUtils.isEmpty(CommonData.getActionId())) {
                        takeMessageAction(CommonData.getActionId(), CommonData.getUrl());
                    }
                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                        if (fuguMessageList.get(i).getType() == HIPPO_USER_CONSENT) {
                            Message currentMessage = fuguMessageList.get(i);
                            if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                messageSending = false;
                                currentMessage.setIsActive(messageJson.optInt("is_active"));
                                currentMessage.setSelectedBtnId(messageJson.optString("selected_btn_id"));

                                currentMessage.setFromName(messageJson.optString("full_name"));
                                currentMessage.setUserImage(messageJson.optString("user_image"));
                                currentMessage.setUserId(messageJson.optLong("user_id"));

                                unsentMessageMapNew.remove(messageJson.getString("muid"));
                                unsentMessages.remove(messageJson.getString("muid"));

                                sentMessages.put(messageJson.optString("muid", ""), currentMessage);

                                updateFeedback(i);
                                removeItemAndUpdateUI();
                                return;
                            }
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == 19) {
                    messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
                } else if (messageJson.optInt(MESSAGE_TYPE) == CARD_LIST) {
                    if (!TextUtils.isEmpty(messageJson.optString("selected_agent_id", ""))) {
                        for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                            if (fuguMessageList.get(i).getOriginalMessageType() == CARD_LIST) {
                                Message currentMessage = fuguMessageList.get(i);
                                if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.optString("muid"))) {
                                    messageSending = false;
                                    unsentMessageMapNew.remove(messageJson.getString("muid"));
                                    unsentMessages.remove(messageJson.getString("muid"));
                                    currentMessage.setSelectedAgentId(messageJson.optString("selected_agent_id"));
                                    sentMessages.put(messageJson.optString("muid", ""), currentMessage);
                                    fuguMessageList.get(i).setSelectedAgentId(messageJson.optString("selected_agent_id"));
                                    updateFeedback(i);
                                    removeItemAndUpdateUI();
                                    return;
                                }
                            }
                        }
                    }
                }
                /*else if(messageJson.optInt(MESSAGE_TYPE) == PAYMENT_TYPE) {
                    if(messageJson.has("custom_action") && !TextUtils.isEmpty(messageJson.optJSONObject("custom_action").optString("selected_id", ""))) {
                        for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                            if (fuguMessageList.get(i).getOriginalMessageType() == PAYMENT_TYPE) {
                                Message currentMessage = fuguMessageList.get(i);
                                if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.optString("muid"))) {
                                    String selectedId = messageJson.optJSONObject("custom_action").optString("selected_id", "");
                                    currentMessage.getCustomAction().setSelectedId(selectedId);
                                    sentMessages.put(messageJson.optString("muid", ""), currentMessage);
                                    fuguMessageList.get(i).getCustomAction().setSelectedId(selectedId);
                                    updateFeedback(i);
                                    removeItemAndUpdateUI();
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                } */
                else if (messageJson.optInt(MESSAGE_TYPE) == MULTI_SELECTION) {
                    if (messageJson.has("custom_action") && messageJson.optJSONObject("custom_action").optInt("is_replied", 0) == 1) {
                        for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                            if (fuguMessageList.get(i).getOriginalMessageType() == MULTI_SELECTION) {
                                Message currentMessage = fuguMessageList.get(i);
                                if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.optString("muid"))) {
                                    int isReplyed = messageJson.optJSONObject("custom_action").optInt("is_replied", 0);
                                    currentMessage.getCustomAction().setIsReplied(isReplyed);
                                    sentMessages.put(messageJson.optString("muid", ""), currentMessage);
                                    fuguMessageList.get(i).getCustomAction().setIsReplied(isReplyed);
                                    messageSending = false;
                                    unsentMessageMapNew.remove(messageJson.getString("muid"));
                                    unsentMessages.remove(messageJson.getString("muid"));

                                    updateFeedback(i);
                                    removeItemAndUpdateUI();
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE) == PAYMENT_TYPE) {
                    if (messageJson.has("custom_action") && messageJson.optJSONObject("custom_action").optInt("selected_id", 0) == 1) {
                        for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                            if (fuguMessageList.get(i).getOriginalMessageType() == PAYMENT_TYPE) {
                                Message currentMessage = fuguMessageList.get(i);
                                if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.optString("muid"))) {
                                    int isSelected = messageJson.optJSONObject("custom_action").optInt("selected_id", 0);
                                    currentMessage.getCustomAction().setSelectedId("" + isSelected);
                                    sentMessages.put(messageJson.optString("muid", ""), currentMessage);
                                    fuguMessageList.get(i).getCustomAction().setSelectedId("" + isSelected);
                                    messageSending = false;
                                    unsentMessageMapNew.remove(messageJson.getString("muid"));
                                    unsentMessages.remove(messageJson.getString("muid"));

                                    updateFeedback(i);
                                    removeItemAndUpdateUI();
                                    return;
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (String.valueOf(messageJson.optString("user_id")).equals(String.valueOf(userId))) {
                isSelf = true;
            }
            if (messageJson.optInt(NOTIFICATION_TYPE, 0) == READ_MESSAGE) {
                readFunctionality(messageJson);
            } else if (messageJson.optInt(NOTIFICATION_TYPE, 0) == 13) {
                updateUI(messageJson);
                return;
            } else {
                if (messageJson.optInt(IS_TYPING, TYPING_STOPPED) == TYPING_STARTED) {
                    isFayeChannelActive = true;
                    readFunctionality(messageJson);
                }
                if (messageJson.optInt(IS_TYPING, 0) == TYPING_SHOW_MESSAGE &&
                        messageJson.getInt(MESSAGE_TYPE) == VIDEO_CALL && messageJson.has("muid")) {

                    //12345
                } else if (messageJson.optInt(IS_TYPING, 0) == TYPING_SHOW_MESSAGE &&
                        (messageJson.has("message") && !messageJson.getString(MESSAGE).isEmpty() ||
                                (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                (messageJson.has("url") && !messageJson.getString("url").isEmpty()) || messageJson.has(CUSTOM_ACTION))
                        && (messageJson.getInt(MESSAGE_TYPE) == TEXT_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == ACTION_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == ACTION_MESSAGE_NEW)
                        || messageJson.getInt(MESSAGE_TYPE) == FEEDBACK_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_QUICK_REPLY_VIEW
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_TEXT_VIEW
                        || messageJson.getInt(MESSAGE_TYPE) == HIPPO_USER_CONSENT
                        || messageJson.optInt(MESSAGE_TYPE) == CARD_LIST
                        || messageJson.optInt(MESSAGE_TYPE) == PAYMENT_TYPE
                        || messageJson.optInt(MESSAGE_TYPE) == MULTI_SELECTION
                        || messageJson.optInt(MESSAGE_TYPE) == FUGU_FORUM_TICKET_
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_FORUM_VIEW) {
                    HippoLog.v("onReceivedMessage", "in if 1");
                    if (isSelf && messageJson.has(MESSAGE_STATUS) && messageJson.has("muid")
                            && messageJson.getInt(MESSAGE_STATUS) == MESSAGE_UNSENT) {
                        try {
                            HippoLog.v("onReceivedMessage", "in if 2");

                            messageIndex = messageJson.getInt("message_index");
                            try {
                                if (fuguMessageList.get(messageJson.getInt("message_index")).getType() == TYPE_HEADER
                                        && (messageJson.getInt(MESSAGE_INDEX) + 1 < fuguMessageList.size())) {
                                    HippoLog.v("onReceivedMessage", "in if 3");
                                    messageIndex = messageIndex + 1;
                                    fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX) + 1).setMessageStatus(MESSAGE_SENT);
                                } else if (messageJson.getInt(MESSAGE_INDEX) < fuguMessageList.size()) {
                                    HippoLog.v("onReceivedMessage", "in elseIf 1");
                                    fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)).setMessageStatus(MESSAGE_SENT);
                                }
                            } catch (Exception e) {
                                try {
                                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                                        if (!fuguMessageList.get(i).isDateView()) {
                                            Message currentMessage = fuguMessageList.get(i);
                                            if (currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                                messageIndex = i;
                                                try {
                                                    if (fuguMessageList.get(messageJson.getInt("message_index")).getType() == TYPE_HEADER
                                                            && (messageJson.getInt(MESSAGE_INDEX) + 1 < fuguMessageList.size())) {
                                                        HippoLog.v("onReceivedMessage", "in if 3");
                                                        messageIndex = messageIndex + 1;
                                                        fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX) + 1).setMessageStatus(MESSAGE_SENT);
                                                    } else if (messageJson.getInt(MESSAGE_INDEX) < fuguMessageList.size()) {
                                                        HippoLog.v("onReceivedMessage", "in elseIf 1");
                                                        fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)).setMessageStatus(MESSAGE_SENT);
                                                    }
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }


                            Message listItem = unsentMessages.get(messageJson.getString("muid"));
                            if (listItem == null)
                                return;
                            listItem.setMessageStatus(MESSAGE_SENT);
                            //123

                            if (messageJson.has("thumbnail_url")) {
                                listItem.setThumbnailUrl(messageJson.optString("thumbnail_url"));
                                listItem.setUrl(messageJson.optString("image_url"));
                                listItem.setFileUrl(messageJson.optString("image_url"));
                                if (messageJson.has("url"))
                                    listItem.setFileUrl(messageJson.optString("url"));
                            }

                            List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                            Collections.reverse(reverseOrderedKeys);
                            String tempSentAtUTC = "";
                            for (String key : reverseOrderedKeys) {
                                if (sentMessages.get(key).isDateView()) {
                                    tempSentAtUTC = key;
                                    break;
                                }
                            }
                            String time = listItem.getSentAtUtc();
                            String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                            if (!tempSentAtUTC.equalsIgnoreCase(localDate)) {
                                sentMessages.put(localDate, new Message(localDate, true));
                            }

                            sentMessages.put(messageJson.getString("muid"), listItem);
                            unsentMessageMapNew.remove(messageJson.getString("muid"));
                            unsentMessages.remove(messageJson.getString("muid"));
                            pageStart = pageStart + 1;
                            if (unsentMessageMapNew.size() == 0 && isNetworkStateChanged) {
                                pageStart = 1;
                                isNetworkStateChanged = false;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HippoLog.e(TAG, "notifyItemChanged at: " + messageIndex);
                                        fuguMessageAdapter.updateList(fuguMessageList, false);
                                        fuguMessageAdapter.notifyItemChanged(messageIndex);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                            messageSending = false;
                            sendMessages();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        HippoLog.v("onReceivedMessage", "in else 1");
                        String localDate = dateUtils.convertToLocal(messageJson.getString("date_time"), inputFormat, outputFormat);
                        if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                            sentMessages.put(localDate, new Message(localDate, true));
                            fuguMessageList.add(new Message(localDate, true));
                            sentAtUTC = localDate;
                            dateItemCount = dateItemCount + 1;
                        }

                        String url = "";
                        if (messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE) {
                            url = messageJson.optString("image_url", "");
                        } else if (messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE) {
                            url = messageJson.optString("url", "");
                        }
                        String sentUuid;
                        try {
                            sentUuid = messageJson.getString("UUID");
                        } catch (Exception e) {
                            sentUuid = UUID.randomUUID().toString();
                        }

                        String msgTxt = messageJson.optString(MESSAGE, "");
                        try {
                            if (!TextUtils.isEmpty(messageJson.optString("multi_lang_message", ""))) {
                                Pattern pattern = Pattern.compile("\\{\\{\\{(.*?)\\}\\}\\}");
                                Matcher matcher = pattern.matcher(messageJson.optString("multi_lang_message", ""));
                                if (matcher.find()) {
                                    String key = matcher.group(1);
                                    String value = Restring.getString(key);
                                    if (!TextUtils.isEmpty(value)) {
                                        String oldStr = "{{{" + key + "}}}";
                                        msgTxt = messageJson.optString("multi_lang_message", "").replace(oldStr, value);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                        String removeLt = msgTxt.replaceAll("<", "&lt;");
                        String removeGt = removeLt.replaceAll(">", "&gt;");

//                        String removeGt = messageJson.optString(MESSAGE, "");
//                        if(CommonData.isEncodeToHtml()) {
//                            String removeLt = messageJson.optString(MESSAGE, "").replaceAll("<", "&lt;");
//                            removeGt = removeLt.replaceAll(">", "&gt;");
//                        }

                        if (messageJson.getInt(MESSAGE_TYPE) == HIPPO_USER_CONSENT && !messageJson.has(FULL_NAME))
                            return;

                        Message message = new Message(0, messageJson.getString(FULL_NAME),
                                Long.parseLong(messageJson.getString(USER_ID)),
                                removeGt,
                                messageJson.getString(DATE_TIME),
                                isSelf,
                                onSubscribe == 1 ? MESSAGE_READ : MESSAGE_SENT,
                                fuguMessageList.size(),
                                url,
                                messageJson.has("thumbnail_url") ? messageJson.getString("thumbnail_url") : "",
                                messageJson.getInt(MESSAGE_TYPE),
                                sentUuid);
                        if (messageJson.has(CUSTOM_ACTION)) {
                            message.setCustomAction(new Gson().fromJson(messageJson.getJSONObject(CUSTOM_ACTION).toString(), CustomAction.class));
                        }

                        message.setUserType(messageJson.optInt("user_type", 1));
                        message.setOriginalMessageType(messageJson.getInt(MESSAGE_TYPE));
                        message.setUserImage(messageJson.optString("user_image"));
                        message.setMuid(messageJson.optString("muid", ""));
                        message.setIntegrationSource(messageJson.optInt("integration_source", 0));

                        isContentValueNull = false;
                        if (message.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                            JSONArray valuesArray = messageJson.getJSONArray("values");
                            ArrayList<String> values = new ArrayList<>();
                            if (valuesArray != null) {
                                for (int b = 0; b < valuesArray.length(); b++) {
                                    values.add(valuesArray.getString(b));
                                }
                            }
                            if (values.size() > 0)
                                return;
                            if (messageJson.has("default_action_id")) {
                                message.setDefaultActionId(messageJson.getString("default_action_id"));
                            }

                            if (messageJson.has("id")) {
                                message.setId(messageJson.optLong("id"));
                                List<ContentValue> contentValue = new ArrayList<>();

                                JSONArray contentvaluesArray = messageJson.getJSONArray("content_value");
                                if (contentvaluesArray != null) {

                                    for (int a = 0; a < contentvaluesArray.length(); a++) {
                                        JSONObject object = contentvaluesArray.getJSONObject(a);
                                        ContentValue contentValue1 = new ContentValue();
                                        contentValue1.setBotId(object.getString("bot_id"));
                                        contentValue1.setButtonId(object.getString("button_id"));
                                        contentValue1.setButtonType(object.getString("button_type"));
                                        contentValue1.setButtonTitle(object.getString("button_title"));
                                        contentValue1.setActionId(object.getString("action_id"));

                                        contentValue.add(contentValue1);
                                    }
                                }
                                message.setContentValue(contentValue);
                                CommonData.setQuickReplyData(message);
                            } else {
                                return;
                            }
                        } else if ((message.getMessageType() == FUGU_FORUM_VIEW || message.getMessageType() == FUGU_FORUM_TICKET_) && message.getId() != null) {
                            message.setMuid(messageJson.optString("muid", ""));
                            message.setId(messageJson.optLong("id"));
                            message.setIsSkipEvent(messageJson.optInt("is_skip_event", 0));
                            message.setIsSkipButton(messageJson.optInt("is_skip_button", 0));
                            message.setIsFromBot(messageJson.optInt("is_from_bot"));
                            message.setIsActive(messageJson.optInt("is_active"));
                            List<ContentValue> contentValue = new ArrayList<>();
                            ArrayList<String> values = new ArrayList<>();


                            JSONArray contentvaluesArray = messageJson.getJSONArray("content_value");
                            JSONArray valuesArray = messageJson.getJSONArray("values");

                            if (contentvaluesArray != null) {

                                for (int a = 0; a < contentvaluesArray.length(); a++) {
                                    JSONObject object = contentvaluesArray.getJSONObject(a);
                                    ContentValue contentValue1 = new ContentValue();
                                    contentValue1.setBotId(object.getString("bot_id"));
                                    //contentValue1.setId(object.getString("_id"));
                                    JSONArray array = object.getJSONArray("questions");
                                    JSONArray dataTypeArray = object.getJSONArray("data_type");
                                    JSONArray paramArray = object.getJSONArray("params");
                                    ArrayList<String> questions = new ArrayList<>();
                                    ArrayList<String> dataType = new ArrayList<>();
                                    ArrayList<String> params = new ArrayList<>();

                                    for (int x = 0; x < array.length(); x++) {
                                        questions.add(array.getString(x));
                                    }

                                    for (int y = 0; y < dataTypeArray.length(); y++) {
                                        dataType.add(dataTypeArray.getString(y));
                                    }
                                    for (int z = 0; z < paramArray.length(); z++) {
                                        params.add(paramArray.getString(z));
                                    }
                                    contentValue1.setQuestions(questions);
                                    contentValue1.setData_type(dataType);
                                    contentValue1.setParams(params);

                                    contentValue.add(contentValue1);
                                }
                            }

                            if (valuesArray != null) {
                                for (int b = 0; b < valuesArray.length(); b++) {
                                    values.add(valuesArray.getString(b));
                                }
                            }

                            message.setValues(values);

                            if (message.getMessageType() == FUGU_FORUM_TICKET_ &&
                                    message.getValues() != null &&
                                    message.getValues().size() > 0 &&
                                    !Utils.isEmailValid(message.getValues().get(0))) {
                                message.setValues(new ArrayList<String>());
                            } else
                                message.setValues(values);
                            message.setContentValue(contentValue);
                            removeItemAndUpdateUI();
                        } else if (message.getMessageType() == FEEDBACK_MESSAGE) {
                            message.setRatingGiven(messageJson.optInt("rating_given"));
                            message.setComment(messageJson.optString("comment"));
                            message.setIsRatingGiven(messageJson.optInt("is_rating_given"));
                            message.setTotalRating(messageJson.optInt("total_rating"));
                            message.setLineBeforeFeedback(messageJson.optString("line_before_feedback"));
                            message.setLineAfterFeedback_1(messageJson.optString("line_after_feedback_1"));
                            message.setLineAfterFeedback_2(messageJson.optString("line_after_feedback_2"));
                        } else if (message.getMessageType() == FILE_MESSAGE) {
                            message.setFileName(messageJson.optString("file_name"));
                            message.setFileSize(messageJson.optString("file_size"));
                            message.setUrl(messageJson.optString("image_url"));
                            message.setFileUrl(messageJson.optString("url"));
                            message.setThumbnailUrl(messageJson.optString("thumbnail_url"));
                            message.setDocumentType(messageJson.optString("document_type"));
                            String fileExt = Util.getExtension(messageJson.optString("url"));
                            if (!TextUtils.isEmpty(fileExt))
                                message.setFileExtension(fileExt);
                        } else if (message.getMessageType() == HIPPO_USER_CONSENT) {
                            List<ContentValue> contentValue = new ArrayList<>();
                            JSONArray contentvaluesArray = messageJson.getJSONArray("content_value");
                            if (contentvaluesArray != null) {
                                for (int a = 0; a < contentvaluesArray.length(); a++) {
                                    JSONObject object = contentvaluesArray.getJSONObject(a);
                                    ContentValue contentValue1 = new ContentValue();
                                    contentValue1.setBtnId(object.optString("btn_id"));
                                    contentValue1.setBtnColor(object.optString("btn_color"));
                                    contentValue1.setBtnTitle(object.optString("btn_title"));
                                    contentValue1.setBtnSelectedColor(object.optString("btn_selected_color"));
                                    contentValue1.setBtnTitleColor(object.optString("btn_title_color"));
                                    contentValue1.setBtnTitleSelectedColor(object.optString("btn_title_selected_color"));
                                    contentValue1.setButtonType(object.optString("button_type"));
                                    contentValue1.setButtonActionType(object.optString("button_action_type"));
                                    ButtonActionJson actionJson = new ButtonActionJson();
                                    if (object.has("button_action_json")) {
                                        JSONObject obj = object.getJSONObject("button_action_json");
                                        actionJson.setUrl(obj.optString("link_url"));
                                    }
                                    contentValue1.setButtonActionJson(actionJson);
                                    contentValue.add(contentValue1);
                                }
                            }

                            //message.setUserId(0L);
                            message.setContentValue(contentValue);
                            message.setIsActive(messageJson.optInt("is_active"));
                            message.setSelectedBtnId(messageJson.optString("selected_btn_id"));
                            if (messageJson.has("id"))
                                message.setId(messageJson.optLong("id"));
                            else if (messageJson.has("message_id"))
                                message.setId(messageJson.optLong("message_id"));
                        } else if (message.getMessageType() == CARD_LIST) {
                            HippoLog.e("TAG", "in card view");
                            List<ContentValue> contentValue = new ArrayList<>();
                            JSONArray contentvaluesArray = messageJson.optJSONArray("content_value");//getJSONArray
                            if (contentvaluesArray != null) {
                                HippoLog.e("TAG", "in card view if");
                                for (int a = 0; a < contentvaluesArray.length(); a++) {

                                    HippoLog.e("TAG", "in card view if " + a);
                                    JSONObject object = contentvaluesArray.getJSONObject(a);
                                    ContentValue contentValue1 = new ContentValue();
                                    contentValue1.setCardId(object.optString("id"));
                                    contentValue1.setImageUrl(object.optString("image_url"));
                                    contentValue1.setTitle(object.optString("title"));
                                    contentValue1.setDescription(object.optString("description"));
                                    contentValue1.setRatingValue(object.optString("rating"));
                                    contentValue.add(contentValue1);
                                }
                            }
                            message.setContentValue(contentValue);
                            message.setSelectedAgentId(messageJson.optString("selected_agent_id"));
                            message.setMessage(messageJson.optString("fallback_text"));
                            if (messageJson.has("id"))
                                message.setId(messageJson.optLong("id"));
                            else if (messageJson.has("message_id"))
                                message.setId(messageJson.optLong("message_id"));

                            if (contentValue.size() == 0) {
                                isContentValueNull = true;
                            }

                            HippoLog.e("TAG", "in card view added");
                        } else if (message.getMessageType() == PAYMENT_TYPE) {

                            if (!messageJson.has(CUSTOM_ACTION) || cardMuid.equalsIgnoreCase(messageJson.optString("muid", ""))) {
                                cardMuid = "";
                                return;
                            }
                            cardMuid = message.getMuid();
                            //message.setCustomAction(new Gson().fromJson(messageJson.getJSONObject(CUSTOM_ACTION).toString(), PaymentData.class));
                        }

                        if (isContentValueNull) {
                            isContentValueNull = false;
                            message.setMessageType(ITEM_TYPE_OTHER);
                        } else {
                            int messageViewType = getType(message.getMessageType(), isSelf, true, messageJson.optString("document_type"));
                            message.setMessageType(messageViewType);
                        }

                        try {
                            message.setMessageState(messageJson.optInt("message_state"));
                            message.setVideoCallDuration(messageJson.optInt("video_call_duration"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /*if(messagesApi.contains("parsing")) {
                            tempParseMessages.add(message);
                        } else*/
                        if (!sentMessages.containsValue(messageJson.optString("muid", ""))) {
                            ArrayList<Message> items = new ArrayList<>();
                            items.addAll(sentMessages.values());
                            try {
                                if (items.get(items.size() - 1).isDateView()) {
                                    message.setAboveMuid("123456");
                                    message.setAboveUserId(-2L);
                                } else {
                                    Message lastMessage = items.get(items.size() - 1);
                                    lastMessage.setBelowMuid(message.getMuid());
                                    lastMessage.setBelowUserId(message.getUserId());

                                    fuguMessageList.get(fuguMessageList.size() - 1).setBelowMuid(message.getMuid());
                                    if (message.getOriginalMessageType() == CARD_LIST
                                            && (message.getContentValue() != null && message.getContentValue().size() > 0)) {
                                        fuguMessageList.get(fuguMessageList.size() - 1).setBelowUserId(-2l);
                                    } else {
                                        fuguMessageList.get(fuguMessageList.size() - 1).setBelowUserId(message.getUserId());
                                    }
                                    sentMessages.put(lastMessage.getMuid(), lastMessage);

                                    message.setAboveMuid(lastMessage.getMuid());
                                    message.setAboveUserId(lastMessage.getUserId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            fuguMessageList.add(message);
                            sentMessages.put(messageJson.optString("muid", ""), message);
                        }
                        //checkAutoSuggestions();
                        /*else if(message.getOriginalMessageType() == HIPPO_USER_CONSENT && message.getUserId().intValue() == 0) {
                            int index = fuguMessageList.indexOf(message);
                            if(index != -1) {
                                fuguMessageList.get(index).setUserId(message.getUserId());
                                fuguMessageList.get(index).setUserImage(message.getUserImage());
                                fuguMessageList.get(index).setFromName(message.getfromName());
                            }
//                            for(int i = fuguMessageList.size()-1;i==0;i--) {
//                                if(message.getMuid().equalsIgnoreCase(fuguMessageList.get(i).getMuid())) {
//                                    fuguMessageList.get(i).setUserId(message.getUserId());
//                                    fuguMessageList.get(i).setUserImage(message.getUserImage());
//                                    break;
//                                }
//                            }
                            sentMessages.put(messageJson.optString("muid", ""), message);
                        }*/
                        pageStart = pageStart + 1;
                        stopAnim();

                        if (!isSelf) {
                            sendReadAcknowledgement();
                        }
                    }
                }
            }

            if (!messageJson.getString(USER_ID).equals(String.valueOf(userId)) &&
                    onSubscribe == 1 && messageJson.has("on_subscribe")) {
                HippoLog.v("onReceivedMessage", "in If 4");
                for (int i = 0; i < fuguMessageList.size(); i++) {
                    if (fuguMessageList.get(i).getType() == ITEM_TYPE_SELF &&
                            fuguMessageList.get(i).getMessageStatus() == MESSAGE_SENT) {
                        fuguMessageList.get(i).setMessageStatus(MESSAGE_READ);
                    }
                }
            }

            if (messageJson.optInt(MESSAGE_TYPE) == FUGU_QUICK_REPLY_VIEW || messageJson.optInt(MESSAGE_TYPE) == FUGU_FORUM_VIEW || messageJson.optInt(MESSAGE_TYPE) == FUGU_FORUM_TICKET_
                    || messageJson.optInt(MESSAGE_TYPE) == FUGU_TEXT_VIEW || messageJson.optInt(MESSAGE_TYPE) == HIPPO_USER_CONSENT) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecycler();

                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (messageJson.optInt(MESSAGE_TYPE) == CARD_LIST ||
                                messageJson.optInt(MESSAGE_TYPE) == PAYMENT_TYPE ||
                                messageJson.optInt(MESSAGE_TYPE) == MULTI_SELECTION ||
                                messageJson.optInt(MESSAGE_TYPE) == HIPPO_USER_CONSENT) {
                            HippoLog.v("onReceivedMessage", "in If 4.1");
                            updateRecycler();
                        } else if (messageJson.has(MESSAGE_TYPE) && (messageJson.optInt(MESSAGE_TYPE) == TEXT_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == IMAGE_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == FILE_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == ACTION_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == HIPPO_USER_CONSENT ||
                                messageJson.optInt(MESSAGE_TYPE) == FEEDBACK_MESSAGE ||
                                messageJson.getInt(MESSAGE_TYPE) == ACTION_MESSAGE_NEW)) {
                            HippoLog.v("onReceivedMessage", "in If 5");
                            if ((messageJson.has(IS_TYPING) && messageJson.getInt(IS_TYPING) == 0) &&
                                    (!messageJson.getString(MESSAGE).isEmpty() ||
                                            (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                            (messageJson.has("url") && !messageJson.getString("url").isEmpty()) ||
                                            messageJson.has(CUSTOM_ACTION)) &&
                                    !String.valueOf(messageJson.get(USER_ID)).equals(String.valueOf(userId))) {
                                HippoLog.v("onReceivedMessage", "in If 6");
                                updateRecycler();
                                if (CommonData.getQuickReplyData() != null
                                        && CommonData.getQuickReplyData().getDefaultActionId() != null
                                        && !CommonData.getQuickReplyData().getDefaultActionId().isEmpty()) {
                                    try {
                                        //qwe
                                        sendQuickReply(CommonData.getQuickReplyData(), 0,
                                                CommonData.getQuickReplyData().getDefaultActionId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    fuguMessageList.remove(CommonData.getQuickReplyData());
                                    CommonData.clearQuickReplyData();

                                    fuguMessageAdapter.notifyDataSetChanged();


                                } else {
                                    fuguMessageList.remove(CommonData.getQuickReplyData());
                                }


                            } else if ((messageJson.has(IS_TYPING) && messageJson.getInt(IS_TYPING) == TYPING_STARTED) &&
                                    !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                HippoLog.v("onReceivedMessage", "in elseIf startAnim");
                                startAnim();
                            } else if ((messageJson.has(IS_TYPING) && messageJson.getInt(IS_TYPING) == TYPING_STOPPED) &&
                                    !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                HippoLog.v("onReceivedMessage", "in elseIf stopAnim");
                                stopAnim();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Message> tempParseMessages = new ArrayList<>();

    boolean messageSending = false;
    boolean messageSendingRecursion = false;
    int sendingTry = 0;

    private void sendMessage(String message, int messageType, String imageUrl, String thumbnailUrl,
                             String localPath, FuguFileDetails fileDetails, FileuploadModel fileuploadModel) {
        if (isMessageInEditMode) {
            updateMessage(message);
        } else {
            sendMessage(message, messageType, imageUrl, thumbnailUrl, localPath, fileDetails, null, -1, fileuploadModel);
        }
    }

    private void sendMessage(String message, int messageType, String url, String thumbnailUrl,
                             String localPath, FuguFileDetails fileDetails, String localMuid,
                             int localIndex, FileuploadModel fileuploadModel) {
        String initialMessage = message.trim();
        String removsinglequote = initialMessage;
        if (CommonData.isEncodeToHtml()) {
            String removeLt = initialMessage.replaceAll("<", "&lt;");
            String removeGt = removeLt.replaceAll(">", "&gt;");
            String removeQuotes = removeGt.replaceAll("\"", "&quot;");
            removsinglequote = removeQuotes.replaceAll("'", "&#39;");
        }

        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        int index = fuguMessageList.size();
        if (localIndex > 0)
            index = localIndex;
        String muid;
        if (TextUtils.isEmpty(localMuid)) {
            muid = UUID.randomUUID().toString() + "." + new Date().getTime();
            if (fileuploadModel != null && !TextUtils.isEmpty(fileuploadModel.getMuid())) {
                muid = fileuploadModel.getMuid();
            }
            addMessageToList(removsinglequote, messageType, url, thumbnailUrl, localPath, fileuploadModel, muid, index);
            if (fuguMessageList.size() > 0)
                index = fuguMessageList.size() - 1;
        } else {
            muid = localMuid;
        }
        try {
            JSONObject messageJson = new JSONObject();
            messageJson.put("muid", muid);
            messageJson.put("is_message_expired", 0);
            messageJson.put(MESSAGE, removsinglequote);
            messageJson.put(MESSAGE_TYPE, messageType);
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
            messageJson.put(MESSAGE_INDEX, index);

            if (messageType == IMAGE_MESSAGE) {
                if (!url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                } else if (!TextUtils.isEmpty(localPath)) {
                    messageJson.put("local_url", localPath);
                }
                if (fileuploadModel != null && fileuploadModel.getDimns() != null && fileuploadModel.getDimns().size() > 0) {
                    messageJson.put("image_height", fileuploadModel.getDimns().get(0));
                    messageJson.put("image_width", fileuploadModel.getDimns().get(1));
                } else {
                    messageJson.put("image_height", 700);
                    messageJson.put("image_width", 700);
                }
                messageJson.put("file_name", fileuploadModel.getFileName());
                messageJson.put("document_type", fileuploadModel.getDocumentType());
            } else if (messageType == FILE_MESSAGE) {
                if (!url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                } else if (!TextUtils.isEmpty(localPath)) {
                    messageJson.put("local_url", localPath);
                }
                messageJson.put("file_name", fileuploadModel.getFileName());
                messageJson.put("file_size", fileuploadModel.getFileSizeReadable());
                messageJson.put("document_type", fileuploadModel.getDocumentType());
            }

            if (isRemaingAvailable) {
                int timeLeft = getRemainingTime();
                if (timeLeft > 0)
                    messageJson.put("estimated_inride_secs", timeLeft);
            }

            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put("message_status", MESSAGE_UNSENT);
            messageJson.put(FULL_NAME, userName);
            messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }
            unsentMessageMapNew.put(muid, messageJson);
            if (conversation != null && conversation.getChannelId() != null)
                CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

            if (messageType == IMAGE_MESSAGE || messageType == FILE_MESSAGE) {
                if (!TextUtils.isEmpty(localPath)) {
                    fileuploadModel.setMuid(muid);
                    fileuploadModel.setMessageIndex(index);
                    fileuploadModel.setChannelId(channelId);
                    if (channelId == -1) {
                        fileuploadModel.setLabelId(labelId);
                        CommonData.setFirstTimeCreated(true);
                    }
                    HippoLog.e(TAG, "messageJson = " + new Gson().toJson(messageJson));
                    fileuploadModel.setMessageObject(messageJson);
                    HippoLog.d(TAG, "fileuploadModel = " + new Gson().toJson(fileuploadModel));
                    uploadFile(fileuploadModel);
                } else {
                    if (isNetworkAvailable()) {
                        sendMessages();
                    }
                }
            } else {
                if (!messageSendingRecursion && isNetworkAvailable()) {
                    sendMessages();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isRemaingAvailable = false;
    long estimatedTime = -1;

    private void remainingTime() throws Exception {
        estimatedTime = CommonData.getLeftTime();
        if (estimatedTime > 1)
            isRemaingAvailable = true;
        else {
            CommonData.clearLeftTimeInSec();
            return;
        }
    }

    private int getRemainingTime() {
        try {
            if (estimatedTime == -1)
                estimatedTime = CommonData.getLeftTime();

            if (estimatedTime < 1) {
                isRemaingAvailable = false;
                return 0;
            }

            long timeDiff = System.currentTimeMillis() - CommonData.getAddedLeftTime();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff);
            long remainingSec = estimatedTime - seconds;

            if (remainingSec > 0)
                return (int) remainingSec;
        } catch (Exception e) {

        }
        isRemaingAvailable = false;
        CommonData.clearLeftTimeInSec();
        return 0;
    }

    private void sendPaymentRequest() {
        PaymentData paymentData = CommonData.getPaymentData();
        sendPaymentRequest(paymentData);
        CommonData.clearPaymentData();
    }

    private void sendPaymentRequest(PaymentData paymentData) {
        if (channelId.compareTo(-1L) == 0) {
            //if (mClient.isConnectedServer()) {
            if (!isConversationCreated) {
                conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .setMessageType(ACTION_MESSAGE_NEW)
                        .build();

                createConversation(attribute);
                //createConversation();
                CommonData.setPaymentData(paymentData);
                return;
            }
            /*} else {
                mClient.connectServer();
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                return;
            }*/
        }
        String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
        ArrayList<DescriptionObject> descriptionObjects = new ArrayList<>();

        try {

            float totalAmount = 0;
            String localDate = DateUtils.getInstance().getFormattedDate(new Date());
            JSONObject messageJson = new JSONObject();
            messageJson.put("muid", muid);
            messageJson.put("is_message_expired", 0);
            messageJson.put(MESSAGE, "");
            messageJson.put(MESSAGE_TYPE, 19);
            messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
            messageJson.put(MESSAGE_INDEX, fuguMessageList.size() - 1);
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(FULL_NAME, userName);
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));

            JSONObject customAction = new JSONObject();
            customAction.put("title", paymentData.getTitle());

            customAction.put("currency_symbol", paymentData.getCurrencySymbol());


            JSONArray jsonArray = new JSONArray();
            try {
                for (int a = 0; a < paymentData.getPaymentModelData().size(); a++) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("header", paymentData.getPaymentModelData().get(a).getItemDescription());
                    jsonObj.put("content", paymentData.getCurrencySymbol() + " " + paymentData.getPaymentModelData().get(a).getPrice());

                    jsonArray.put(jsonObj);
                    DescriptionObject descriptionObject = new DescriptionObject();
                    descriptionObject.setHeader(paymentData.getPaymentModelData().get(a).getItemDescription());
                    descriptionObject.setContent(paymentData.getCurrencySymbol() + " " + paymentData.getPaymentModelData().get(a).getPrice());
                    descriptionObjects.add(descriptionObject);

                    totalAmount = totalAmount + Float.parseFloat(paymentData.getPaymentModelData().get(a).getPrice());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            customAction.put("amount", totalAmount);

            JSONArray actionButtons = new JSONArray();
            JSONObject actionButtonObj = new JSONObject();

            JSONObject buttonAction = new JSONObject();
            buttonAction.put("title", paymentData.getTitle());
            buttonAction.put("amount", totalAmount);
            buttonAction.put("currency_symbol", paymentData.getCurrencySymbol());
            buttonAction.put("description", jsonArray);
            buttonAction.put("reference", "");
            buttonAction.put("action_type", "NATIVE_ACTIVITY");
            buttonAction.put("transaction_id", "");
            String payBtnText = Restring.getString(FuguChatActivity.this, R.string.hippo_pay_btnText);
            actionButtonObj.put("button_text", payBtnText);
            actionButtonObj.put("button_action", buttonAction);

            actionButtons.put(actionButtonObj);
            customAction.put("action_buttons", actionButtons);
            customAction.put("description", jsonArray);
            messageJson.put("custom_action", customAction);
            messageJson.put(USER_IMAGE, getUserImage());
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
            }
            HippoLog.v("custom_action", "custom_action = " + new Gson().toJson(messageJson));
            unsentMessageMapNew.put(muid, messageJson);
            if (conversation != null && conversation.getChannelId() != null)
                CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

            if (!messageSendingRecursion && isNetworkAvailable()) {
                sendMessages();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String localDate = DateUtils.getInstance().getFormattedDate(new Date());
            String localDate1 = DateUtils.getInstance().getFormattedDate(new Date(), outputFormat);

            if (!sentAtUTC.equalsIgnoreCase(localDate1)) {
                fuguMessageList.add(new Message(localDate1, true));
                sentAtUTC = localDate1;
                dateItemCount = dateItemCount + 1;
            }

            HippoLog.d("userName in SDK", "addMessageToList " + userName);


            Message messageObj = new Message(0,
                    userName,
                    userId,
                    "",
                    DateUtils.getInstance().convertToUTC(localDate),
                    true,
                    MESSAGE_UNSENT,
                    index,
                    "",
                    "",
                    19,
                    muid);

            messageObj.setUserType(0);
            messageObj.setOriginalMessageType(19);
            int localMessageType = getType(19, true, false, "");
            messageObj.setMessageType(localMessageType);

            CustomAction customAction = new CustomAction();
            customAction.setTitle(paymentData.getTitle());
            customAction.setDescriptionObjects(descriptionObjects);

            ArrayList<ActionButtonModel> actionButtonObject = new ArrayList<>();
            ActionButtonModel buttonModel = new ActionButtonModel();
            String payBtnText = Restring.getString(FuguChatActivity.this, R.string.hippo_pay_btnText);
            buttonModel.setButtonText(payBtnText);
            actionButtonObject.add(buttonModel);

            customAction.setActionButtons(actionButtonObject);
            messageObj.setCustomAction(customAction);

            messageObj.setMuid(muid);
            messageObj.setIntegrationSource(0);
            messageObj.setIsMessageExpired(0);
            messageObj.setLocalImagePath(null);
            //messageObj.setMessageState(MESSAGE_UNSENT);

            fuguMessageList.add(messageObj);
            unsentMessages.put(muid, messageObj);
            etMsg.setText("");
            updateRecycler();

            scrollListToBottom();

            HippoLog.d("messageObj", "messageObj = " + new Gson().toJson(messageObj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSelectedCard(int position, Message message, String selectedId) throws Exception {
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        JSONObject messageJson = new JSONObject();
        messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
        messageJson.put(USER_TYPE, ANDROID_USER);
        messageJson.put(FULL_NAME, userName);
        messageJson.put(USER_ID, String.valueOf(userId));
        messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
        messageJson.put("message_id", message.getId());
        messageJson.put("message_type", 21);
        messageJson.put("muid", message.getMuid());
        messageJson.put("is_active", 0);
        messageJson.put("message", message.getMessage());
        messageJson.put("selected_agent_id", String.valueOf(selectedId));
        try {
            if (!TextUtils.isEmpty(message.getFallbackText()))
                messageJson.put("fallback_text", message.getFallbackText());
            else
                messageJson.put("fallback_text", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        messageJson.put(USER_IMAGE, getUserImage());
        if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
            messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
        }
        JSONArray jsonArray = new JSONArray();
        try {
            for (int a = 0; a < message.getContentValue().size(); a++) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", message.getContentValue().get(a).getCardId());
                jsonObj.put("image_url", message.getContentValue().get(a).getImageUrl());
                jsonObj.put("title", message.getContentValue().get(a).getTitle());
                jsonArray.put(jsonObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageJson.put("content_value", jsonArray);

        unsentMessageMapNew.put(message.getMuid(), messageJson);
        if (conversation != null && conversation.getChannelId() != null)
            CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

        if (!messageSendingRecursion && isNetworkAvailable()) {
            sendMessages();
        }

    }

    private void sendMultiSelection(int position, Message message) throws Exception {
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        JSONObject messageJson = new JSONObject();
        messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
        messageJson.put(USER_TYPE, ANDROID_USER);
        messageJson.put(FULL_NAME, message.getfromName());
        messageJson.put(USER_ID, String.valueOf(userId));
        messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
        messageJson.put("message_id", message.getId());
        messageJson.put("message_type", 23);
        messageJson.put("muid", message.getMuid());
        messageJson.put("is_active", 0);
        messageJson.put("message", message.getMessage());

        messageJson.put("replied_by", userName);
        messageJson.put("replied_by_id", String.valueOf(userId));
        messageJson.put(USER_IMAGE, getUserImage());
        if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
            messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
        }

        //message.setCustomAction(new Gson().fromJson(messageJson.getJSONObject(CUSTOM_ACTION).toString(), CustomAction.class));
        messageJson.put("custom_action", new Gson().toJson(message.getCustomAction()));

        message.getCustomAction().setIsReplied(1);
        JSONObject jsonObj = new JSONObject(new Gson().toJson(message.getCustomAction()));
        messageJson.put("custom_action", jsonObj);

        HippoLog.e("messageJson", "messageJson = " + messageJson);

        if (channelId < 1) {

            return;
        }
        unsentMessageMapNew.put(message.getMuid(), messageJson);
        if (conversation != null && conversation.getChannelId() != null)
            CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

        if (!messageSendingRecursion && isNetworkAvailable()) {
            sendMessages();
        }
    }


    private void sendUserConsent(int position, Message message, String actionId, String url) throws Exception {
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        JSONObject messageJson = new JSONObject();
        messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
        messageJson.put(USER_TYPE, ANDROID_USER);
        messageJson.put(FULL_NAME, message.getfromName());
        messageJson.put(USER_ID, String.valueOf(userId));
        messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
        messageJson.put("message_id", message.getId());
        messageJson.put("message_type", 20);
        messageJson.put("muid", message.getMuid());
        messageJson.put("selected_btn_id", message.getSelectedBtnId());
        messageJson.put("is_active", 0);
        messageJson.put("message", message.getMessage());

        messageJson.put("replied_by", userName);
        messageJson.put("replied_by_id", String.valueOf(userId));
        messageJson.put(USER_IMAGE, getUserImage());
        if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
            messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
        }


        JSONArray jsonArray = new JSONArray();
        try {
            for (int a = 0; a < message.getContentValue().size(); a++) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("btn_color", message.getContentValue().get(a).getBtnColor());
                jsonObj.put("btn_id", message.getContentValue().get(a).getBtnId());
                jsonObj.put("btn_selected_color", message.getContentValue().get(a).getBtnSelectedColor());
                jsonObj.put("btn_title", message.getContentValue().get(a).getBtnTitle());
                jsonObj.put("btn_title_color", message.getContentValue().get(a).getBtnTitleColor());
                jsonObj.put("btn_title_selected_color", message.getContentValue().get(a).getBtnTitleSelectedColor());

                try {
                    if (!TextUtils.isEmpty(message.getContentValue().get(a).getButtonActionType())) {
                        jsonObj.put("button_action_type", message.getContentValue().get(a).getButtonActionType());
                    }
                    if (message.getContentValue().get(a).getButtonActionJson() != null) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("link_url", message.getContentValue().get(a).getButtonActionJson().getUrl());
                        jsonObj.put("button_action_json", jObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageJson.put("content_value", jsonArray);
        CommonData.setActionId("");
        CommonData.setUrl("");

        if (!TextUtils.isEmpty(actionId))
            CommonData.setActionId(actionId);
        if (!TextUtils.isEmpty(url))
            CommonData.setUrl(url);

        if (channelId < 1) {

            String botMessageMuid = message.getMuid();//UUID.randomUUID().toString() + "." + new Date().getTime();

            BotMessage botMessage = new BotMessage();
            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                botMessage.setBotGroupId(HippoConfig.getInstance().getBotId());
            } else {
                botMessage.setBotGroupId(null);
            }
            botMessage.setContentValue(message.getContentValue());
            botMessage.setDateTime(DateUtils.getInstance().convertToUTC(localDate));
            botMessage.setFullName(userName);
            botMessage.setIsActive(0);
            botMessage.setIsTyping(0);
            botMessage.setMessage(message.getMessage());
            botMessage.setMessageStatus(MESSAGE_UNSENT);
            botMessage.setMessageType(HIPPO_USER_CONSENT);
            botMessage.setMuid(botMessageMuid);
            botMessage.setSelectedBtnId(message.getSelectedBtnId());
            botMessage.setUserImage(getUserImage());

            botMessage.setRepliedBy(userName);
            botMessage.setRepliedById(userId);
            botMessage.setUserId(userId);
            botMessage.setUserType(ANDROID_USER);

            botMessage.setUserId(message.getUserId());
            botMessage.setUserType(SYSTEM_USER);

            messageJson.put(MESSAGE_UNIQUE_ID, botMessageMuid);
//            for(Message msg: fuguMessageList) {
//                if(msg.getMessageType() == 20) {
//                    msg.setMuid(botMessageMuid);
//                }
//            }
//            message.setMuid(botMessageMuid);

            HippoLog.i("fuguMessageList", "fuguMessageList = " + new Gson().toJson(fuguMessageList));
            messageJson.put("muid", botMessageMuid);
            messageJson.put("bot_group_id", mFuguGetMessageResponse.getData().getBotGroupId());

            CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                    .setMessageType(20)
                    .setIsP2P(isP2P)
                    .setJsonObject(messageJson)
                    .setMessage(message)
                    .setBotMessage(botMessage)
                    .setFuguGetMessageResponse(mFuguGetMessageResponse)
                    .setGetLabelMessageResponse(labelMessageResponse)
                    .setConversationParams(fuguCreateConversationParams)
                    .build();

            createConversation(attribute, actionId);
            return;
        }

        unsentMessageMapNew.put(message.getMuid(), messageJson);
        if (conversation != null && conversation.getChannelId() != null)
            CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

        if (!messageSendingRecursion && isNetworkAvailable()) {
            sendMessages();
        }

    }


    private LinkedHashMap<String, JSONObject> unsentMessageMap = new LinkedHashMap<>();

    private void removeFirstItem() {
        if (unsentMessageMapNew != null && unsentMessageMapNew.size() > 0) {
            if (unsentMessageMapNew.keySet().iterator().hasNext()) {
                String key = unsentMessageMapNew.keySet().iterator().next();
                unsentMessageMapNew.remove(key);
                unsentMessageMap.remove(key);

                try {
                    for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                        if (!fuguMessageList.get(i).isDateView()) {
                            Message currentMessage = fuguMessageList.get(i);
                            if (currentMessage.getMuid().equals(key)) {
                                messageIndex = i;
                                try {
                                    fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_SENT);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


                Message listItem = unsentMessages.get(key);
                if (listItem == null)
                    return;
                listItem.setMessageStatus(MESSAGE_SENT);

                List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                Collections.reverse(reverseOrderedKeys);
                String tempSentAtUTC = "";
                for (String key1 : reverseOrderedKeys) {
                    if (sentMessages.get(key1).isDateView()) {
                        tempSentAtUTC = key1;
                        break;
                    }
                }
                String time = listItem.getSentAtUtc();
                String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                if (!tempSentAtUTC.equalsIgnoreCase(localDate)) {
                    sentMessages.put(localDate, new Message(localDate, true));
                }

                sentMessages.put(key, listItem);
//                unsentMessageMapNew.remove(key);
//                unsentMessages.remove(key);
                pageStart = pageStart + 1;
                if (unsentMessageMapNew.size() == 0 && isNetworkStateChanged) {
                    pageStart = 1;
                    isNetworkStateChanged = false;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HippoLog.e(TAG, "notifyItemChanged at: " + messageIndex);
                            fuguMessageAdapter.updateList(fuguMessageList, false);
                            fuguMessageAdapter.notifyItemChanged(messageIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                messageSending = false;
                sendMessages();


            }
        }
    }

    private synchronized void sendMessages() {
        if (ConnectionManager.INSTANCE.isConnected()) {
            if (unsentMessageMapNew == null || unsentMessageMapNew.size() == 0)
                messageSendingRecursion = false;

            sendingTry = sendingTry + 1;
            if (sendingTry == 3) {
                messageSendingRecursion = false;
                messageSending = false;
            }

            try {
                if (unsentMessageMapNew.keySet().iterator().hasNext()) {
                    String key = unsentMessageMapNew.keySet().iterator().next();
                    Log.e(TAG, "key: " + key + " messageSending status = " + messageSending);
                    JSONObject messageJson = unsentMessageMapNew.get(key);
                    if (messageJson.optInt("is_message_expired", 0) == 1) {
                        unsentMessageMap.put(key, messageJson);
                        unsentMessageMapNew.remove(key);
                        sendMessages();
                    } else {
                        int messageType = messageJson.optInt(MESSAGE_TYPE);
                        if (!messageSending && messageJson.optInt("is_message_expired", 0) == 0 && (messageType != IMAGE_MESSAGE && messageType != FILE_MESSAGE)) {
                            Log.e(TAG, "Sending: " + new Gson().toJson(messageJson));
                            sendingTry = 0;
                            messageSending = true;
                            if (getView()) {

                                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
                            }
                        }
                        if (!messageSending && messageJson.optInt("is_message_expired", 0) == 0 && (messageType == IMAGE_MESSAGE || messageType == FILE_MESSAGE)) {
                            String localPath = messageJson.optString("local_url", "");
                            String url = messageJson.optString("url");
                            String muid = messageJson.optString("muid");
                            int index = messageJson.optInt(MESSAGE_INDEX);
                            if (!TextUtils.isEmpty(url)) {
                                localPath = null;
                                messageJson.remove("local_url");
                            }
                            if (!TextUtils.isEmpty(localPath)) {
                                for (String muidKey : unsentMessageMapNew.keySet()) {
                                    JSONObject newMessageJson = unsentMessageMapNew.get(muidKey);
                                    if (newMessageJson.optInt("is_message_expired", 0) == 1) {
                                        unsentMessageMap.put(muidKey, newMessageJson);
                                        unsentMessageMapNew.remove(muidKey);
                                        sendMessages();
                                        return;
                                    } else {
                                        int newMessageType = newMessageJson.optInt(MESSAGE_TYPE);
                                        if (!messageSending && newMessageJson.optInt("is_message_expired", 0) == 0
                                                && ((!TextUtils.isEmpty(newMessageJson.optString("url", "")))
                                                || (newMessageType != IMAGE_MESSAGE && newMessageType != FILE_MESSAGE))) {
                                            messageJson.put(USER_IMAGE, getUserImage());
                                            if (HippoConfig.getInstance().getBotId() != null && HippoConfig.getInstance().getBotId() > 0) {
                                                messageJson.put(BOT_GROUP_ID, HippoConfig.getInstance().getBotId());
                                            }
                                            sendingTry = 0;
                                            messageSending = true;
                                            if (getView()) {
                                                ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), newMessageJson);
                                            }
                                            return;
                                        } else if (!messageSending && newMessageJson.optInt("is_message_expired", 0) == 0
                                                && (newMessageType == IMAGE_MESSAGE || newMessageType == FILE_MESSAGE)) {
                                            continue;
                                        }
                                    }
                                }
                            } else {
                                sendingTry = 0;
                                messageSending = true;
                                if (getView()) {
                                    ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
                                }
                            }
                        }
                    }
                } else if (unsentMessageMap.size() > 0) {
                    unsentMessageMapNew.putAll(unsentMessageMap);
                    unsentMessageMap.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                connectAgainToServer();
            } catch (Exception e) {

            }
        }
    }

    /**
     * @param message
     * @param messageType
     * @param imageUrl
     * @param thumbnailUrl
     * @param localPath
     * @param fileDetails
     * @param muid
     */
    private void addMessageToList(String message, int messageType, String imageUrl, String thumbnailUrl,
                                  String localPath, FileuploadModel fileDetails, String muid, int index) {
        try {
            String localDate = DateUtils.getInstance().getFormattedDate(new Date());
            String localDate1 = DateUtils.getInstance().getFormattedDate(new Date(), outputFormat);

            if (!sentAtUTC.equalsIgnoreCase(localDate1)) {
                fuguMessageList.add(new Message(localDate1, true));
                sentAtUTC = localDate1;
                dateItemCount = dateItemCount + 1;
            }

            String removeLt = message.replaceAll("<", "&lt;");
            String removeGt = removeLt.replaceAll(">", "&gt;");

//            String removeGt = message;
//            if(CommonData.isEncodeToHtml()) {
//                String removeLt = message.replaceAll("<", "&lt;");
//                removeGt = removeLt.replaceAll(">", "&gt;");
//            }


            Message messageObj = new Message(0,
                    userName,
                    userId,
                    removeGt,
                    DateUtils.getInstance().convertToUTC(localDate),
                    true,
                    MESSAGE_UNSENT,
                    index,
                    imageUrl.isEmpty() ? localPath : imageUrl,
                    thumbnailUrl.isEmpty() ? localPath : thumbnailUrl,
                    messageType,
                    muid);

            messageObj.setUserType(1);
            messageObj.setOriginalMessageType(messageType);
            String docType = "";
            if (fileDetails != null)
                docType = fileDetails.getDocumentType();

            int listMessageType = getType(messageType, true, false, docType);
            messageObj.setMessageType(listMessageType);

            messageObj.setMuid(muid);
            messageObj.setIntegrationSource(0);
            messageObj.setIsMessageExpired(0);
            messageObj.setLocalImagePath(localPath);
            //messageObj.setMessageState(MESSAGE_UNSENT);

            if (fileDetails != null) {
                messageObj.setFileName(fileDetails.getFileName());
                messageObj.setFileSize(fileDetails.getFileSizeReadable());
                messageObj.setFileExtension(fileDetails.getFileMime());
                messageObj.setFilePath(fileDetails.getFilePath());
                messageObj.setFileUrl(fileDetails.getFilePath());
                messageObj.setDocumentType(fileDetails.getDocumentType());
                messageObj.setUploadStatus(UPLOAD_IN_PROGRESS);
                if (fileDetails.getDimns() != null && fileDetails.getDimns().size() > 1) {
                    messageObj.setImageHeight(fileDetails.getDimns().get(0));
                    messageObj.setImageWidth(fileDetails.getDimns().get(1));
                }
            }

            if (fuguMessageList.size() > 0) {
                String preMuid = fuguMessageList.get(fuguMessageList.size() - 1).getMuid();
                messageObj.setAboveUserId(fuguMessageList.get(fuguMessageList.size() - 1).getUserId());
                messageObj.setAboveMuid(preMuid);

                int preIndex = fuguMessageList.size() - 1;
                fuguMessageList.get(preIndex).setBelowMuid(muid);
                fuguMessageList.get(preIndex).setBelowUserId(userId);

                if (sentMessages.containsKey(preMuid)) {
                    sentMessages.put(preMuid, fuguMessageList.get(preIndex));
                } else if (unsentMessages.containsKey(preMuid)) {
                    unsentMessages.put(preMuid, fuguMessageList.get(preIndex));
                }

            }


            fuguMessageList.add(messageObj);
            HippoLog.e("messageObj", "messageObj = " + new Gson().toJson(messageObj));
            unsentMessages.put(muid, messageObj);
            etMsg.setText("");

            /*fuguMessageAdapter.updateMessageList(fuguMessageList);
            rvMessages.post(new Runnable() {
                    @Override
                    public void run() {
                        //scrollListToBottom();
                        rvMessages.smoothScrollToPosition(fuguMessageList.size());
                    }
                });*/

            if (fuguMessageAdapter != null) {
                fuguMessageAdapter.updateList(fuguMessageList);
                fuguMessageAdapter.notifyDataSetChanged();
            }


            //updateRecycler();

            scrollListToBottom();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnectedServer() {
        if (channelId > -1) {
            ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(channelId));
            ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

//            pageStart = 1;
//            isApiRunning = false;
//            allMessagesFetched = false;
//            if (isP2P && !TextUtils.isEmpty(label))
//                getMessages(label);
//            else
//                getMessages(null);
        }
    }

    //private boolean isFirstTimeDisconnected = true;
    /*Handler handlerDisable = new Handler();
    Runnable runnableDisable = new Runnable() {
        @Override
        public void run() {
            setConnectionMessage(2);
        }
    };*/


    private Handler newhandler = new Handler();
    private final static Integer RECONNECTION_TIME = 2000;
    private int index = -1;


    public void onDisconnectedServer() {
        messageSending = false;
        messageSendingRecursion = false;
        enableButtons();
        try {
            if (isNetworkAvailable())
                newhandler.postDelayed(runnable, RECONNECTION_TIME);


            //handlerDisable.postDelayed(runnableDisable, 2000);
            //isFirstTimeDisconnected = false;

        } catch (Exception e) {

        }
    }

    public void onPongReceived() {
        enableButtons();
        checkUnsentMessageStatus(new RefreshDone() {
            @Override
            public void onRefreshComplete() {
                sendMessages();
            }
        });

        if (retryLayout.getVisibility() == View.VISIBLE) {
            setConnectionMessage(0);
        }
    }

    public void onWebSocketError() {
        messageSending = false;
        messageSendingRecursion = false;
        enableButtons();
        try {
            if (isNetworkAvailable())
                newhandler.postDelayed(runnable, RECONNECTION_TIME);
        } catch (Exception e) {

        }
    }

    public void onErrorReceived(String msg, String channel) {
        try {
            if (channel.equalsIgnoreCase("/" + String.valueOf(channelId))) {
                HippoLog.e(TAG, "Other channel id message received");
                JSONObject messageJson1 = new JSONObject(msg);
                //JSONObject errorObj = messageJson1.optJSONObject("error");
                if (messageJson1.optInt("statusCode") == 420) {
                    String muid = messageJson1.optString("muid", "");
                    if (!TextUtils.isEmpty(muid)) {
                        unsentMessageMapNew.remove(muid);
                        Message message = unsentMessages.get(muid);

                        int messageIndex = message.getMessageIndex();
                        try {
                            if (fuguMessageList.get(messageIndex).getType() == TYPE_HEADER
                                    && (messageIndex + 1 < fuguMessageList.size())) {
                                messageIndex = messageIndex + 1;
                                fuguMessageList.get(messageIndex + 1).setMessageStatus(MESSAGE_SENT);
                            } else if (messageIndex < fuguMessageList.size()) {
                                fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_SENT);
                            }
                        } catch (Exception e) {
                            try {
                                for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                                    if (!fuguMessageList.get(i).isDateView()) {
                                        Message currentMessage = fuguMessageList.get(i);
                                        if (currentMessage.getMuid().equals(muid)) {
                                            messageIndex = i;
                                            try {
                                                if (fuguMessageList.get(messageIndex).getType() == TYPE_HEADER
                                                        && (messageIndex + 1 < fuguMessageList.size())) {
                                                    HippoLog.v("onReceivedMessage", "in if 3");
                                                    messageIndex = messageIndex + 1;
                                                    fuguMessageList.get(messageIndex + 1).setMessageStatus(MESSAGE_SENT);
                                                } else if (messageIndex < fuguMessageList.size()) {
                                                    HippoLog.v("onReceivedMessage", "in elseIf 1");
                                                    fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_SENT);
                                                }
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        Message listItem = unsentMessages.get(muid);
                        if (listItem == null)
                            return;
                        listItem.setMessageStatus(MESSAGE_SENT);

                        List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                        Collections.reverse(reverseOrderedKeys);
                        String tempSentAtUTC = "";
                        for (String key : reverseOrderedKeys) {
                            if (sentMessages.get(key).isDateView()) {
                                tempSentAtUTC = key;
                                break;
                            }
                        }
                        String time = listItem.getSentAtUtc();
                        String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                        if (!tempSentAtUTC.equalsIgnoreCase(localDate)) {
                            sentMessages.put(localDate, new Message(localDate, true));
                        }

                        sentMessages.put(muid, listItem);
                        unsentMessageMapNew.remove(muid);
                        unsentMessages.remove(muid);
                        pageStart = pageStart + 1;
                        if (unsentMessageMapNew.size() == 0 && isNetworkStateChanged) {
                            pageStart = 1;
                            isNetworkStateChanged = false;
                        }

                        final int finalMessageIndex = messageIndex;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HippoLog.e(TAG, "notifyItemChanged at: " + finalMessageIndex);
                                    fuguMessageAdapter.updateList(fuguMessageList, false);
                                    fuguMessageAdapter.notifyItemChanged(finalMessageIndex);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        messageSending = false;
                    }
                    if (messageJson1.optInt("statusCode") == 417) {
                        String message = messageJson1.optString("message", "");
                        Toast.makeText(FuguChatActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    } else {
                        resubscribeChannel();
                    }
                } else if (messageJson1.optInt("statusCode") == 421) {
                    String message = messageJson1.optString("message", "");
                    if (!TextUtils.isEmpty(message))
                        ToastUtil.getInstance(FuguChatActivity.this).showToast(message);

                } else if (messageJson1.optInt("statusCode") == 417) {

                    String message = messageJson1.optString("message", "");
                    String muid = messageJson1.optString("muid", "");
                    if (!unsentMessages.containsKey(muid)) {
                        return;
                    }

                    if (lastMuid.equals(muid))
                        return;
                    if (!TextUtils.isEmpty(message)) {
                        ToastUtil.getInstance(FuguChatActivity.this).showToast(message);
                    }

                    messageSendingRecursion = false;
                    messageSending = false;

                    unsentMessageMapNew.remove(muid);
                    unsentMessageMap.remove(muid);
                    unsentMessages.remove(muid);
                    sentMessages.remove(muid);


                    int index = fuguMessageList.indexOf(new Message(muid, 1));
                    String msgTxt = fuguMessageList.get(index).getMessage();
                    etMsg.setText("");
                    etMsg.setText("" + msgTxt);
                    fuguMessageList.remove(index);
                    if (fuguMessageAdapter != null) {
                        fuguMessageAdapter.updateList(fuguMessageList);
                        fuguMessageAdapter.notifyItemRemoved(index);
                    }
                    lastMuid = muid;
                }

                //return;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String lastMuid = "";

    private void resubscribeChannel() {
        if (channelId > -1) {
            // TODO: 2020-04-28 check why this
            //mClient.unsubscribeChannel("/" + String.valueOf(channelId));
            ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(channelId));
        }
    }


    // TODO: 2020-04-28 show faye server not connected.
    public void onNotConnected() {
        messageSending = false;
        messageSendingRecursion = false;
        /*try {
            handlerDisable.postDelayed(runnableDisable, 1000);
        } catch (Exception e) {

        }*/
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (HippoNotificationConfig.pushLabelId > 0 && ConnectionManager.INSTANCE.isConnected()) {
                    connectAgainToServer();
                    //setConnectionMessage(3);
                }
                /*try {
                    handlerDisable.removeCallbacks(runnableDisable);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            } catch (Exception e) {

            }
        }
    };


    private void connectAgainToServer() throws Exception {
        if (!isNetworkAvailable()) {
            //setConnectionMessage(3);
            return;
        }
        //setConnectionMessage(4);
        if (!ConnectionManager.INSTANCE.isConnected()) {
            newhandler.removeCallbacks(runnable);
            ConnectionManager.INSTANCE.initFayeConnection();
            onConnectedServer();
        }

        HandlerThread thread = new HandlerThread("FayeReconnect");
        thread.start();
    }


    private void checkUnsentMessageStatus(RefreshDone done) {
        try {
            index = -1;
            if (unsentMessages == null)
                unsentMessages = new LinkedHashMap<>();
            for (String key : unsentMessages.keySet()) {
                Message listItem = unsentMessages.get(key);
                String time = listItem.getSentAtUtc();
                int expireTimeCheck = listItem.getIsMessageExpired();
                if (expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                    listItem.setIsMessageExpired(1);
                    if (listItem.getOriginalMessageType() == 10) {
                        listItem.setMessageStatus(MESSAGE_FILE_RETRY);
                    }
                    try {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        messageJson.put("is_message_expired", 1);
                        unsentMessageMapNew.put(key, messageJson);
                        if (index == -1)
                            index = messageJson.optInt("message_index", -1);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            CommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
            CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

            if (index > -1 && fuguMessageAdapter != null) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fuguMessageAdapter.notifyItemRangeChanged(index, fuguMessageList.size());
                        }
                    });
                } catch (Exception e) {

                }
            }

            if (done != null) {
                done.onRefreshComplete();
            }
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        keyboardVisibility = isVisible;
    }

    @Override
    public void onConcentClicked(int position, Message message, String actionId, String url) {
        HippoLog.v(TAG, "message = " + new Gson().toJson(message));
        try {
            sendUserConsent(position, message, actionId, url);
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    @Override
    public void onPaymentLink(int position, Message message, HippoPayment payment, String url) {
        if (getView()) {
            selectPaymentMethod(url, payment);
            //openPaymentDialog(url, payment);
        }
    }

    @Override
    public void onMultiSelectionClicked(int position, Message message) {
        //HippoLog.e("TAG", "message = "+new Gson().toJson(message));
        try {
            sendMultiSelection(position, message);
        } catch (Exception e) {
            if (HippoConfig.DEBUG)
                e.printStackTrace();
        }
    }

    private void closeFragmentNow() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("fragment_dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    @Override
    public void closeFragment() {
        onResume();
    }


    public interface RefreshDone {
        void onRefreshComplete();
    }

    private void setConnectionMessage(int status) {
        if (isNetworkAvailable()) {
            switch (status) {
                case 0:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressWheel.setVisibility(View.GONE);
                            retryLayout.setBackgroundColor(Color.parseColor("#FFA500"));
                            btnRetry.setText(Restring.getString(FuguChatActivity.this, R.string.fugu_connecting));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    retryLayout.setBackgroundColor(Color.parseColor("#00FF00"));
                                    btnRetry.setText(Restring.getString(FuguChatActivity.this, R.string.fugu_connected));
                                    retryLayout.setVisibility(View.GONE);
                                }
                            }, 1500);
                        }
                    });
                    break;
                case 3:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            String text = Restring.getString(FuguChatActivity.this, R.string.hippo_no_internet_connected);
                            btnRetry.setText(text);
                            retryLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                            //enableButtons();
                        }
                    });
                    break;
                case 6:
                    retryLayout.setVisibility(View.VISIBLE);
                    retryLayout.setBackgroundColor(Color.parseColor("#FBE799"));
                    new GeneralFunctions().spannableRetryText(btnRetry, Restring.getString(FuguChatActivity.this, R.string.error_msg_yellow_bar), Restring.getString(FuguChatActivity.this, R.string.hippo_tap_to_retry));
                    retryLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressWheel.setVisibility(View.VISIBLE);
                            getMessages(null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressWheel.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);
                        }
                    });
                    break;
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    retryLayout.setVisibility(View.VISIBLE);
                    progressWheel.setVisibility(View.GONE);
                    String text = Restring.getString(FuguChatActivity.this, R.string.hippo_no_internet_connected);
                    btnRetry.setText(text);
                    retryLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            });
        }
    }

    String fullname = "";
    String image = "";

    private void videoCallInit(int callType) {
        boolean isAgentFlow = false;
        boolean isAllowCall = false;
        isCallClicked = true;
        FuguGetMessageResponse mFuguGetMessageResponse = CommonData.getSingleAgentData(channelId);
        if (callType == AUDIO_CALL_VIEW && (!mFuguGetMessageResponse.getData().isAllowAudioCall() || !CommonData.getAudioCallStatus())) {
            HippoLog.e(TAG, "Audio call disabled");
            return;
        }

        if (callType == VIDEO_CALL_VIEW && (!mFuguGetMessageResponse.getData().isAllowVideoCall() || !CommonData.getVideoCallStatus())) {
            HippoLog.e(TAG, "Video call disabled");
            return;
        }


        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null &&
                !TextUtils.isEmpty(mFuguGetMessageResponse.getData().getAgentName())) {
            fullname = mFuguGetMessageResponse.getData().getAgentName();
        }
        if (mFuguGetMessageResponse != null && mFuguGetMessageResponse.getData() != null && mFuguGetMessageResponse.getData().getOtherUsers() != null
                && mFuguGetMessageResponse.getData().getOtherUsers().size() > 0) {
            fullname = mFuguGetMessageResponse.getData().getOtherUsers().get(0).getFullName();
            image = mFuguGetMessageResponse.getData().getOtherUsers().get(0).getUserImage();
        }

        if (TextUtils.isEmpty(fullname))
            fullname = "User";

//        LibApp.getInstance().onCallBtnClick(this, callType, channelId, userId, isAgentFlow, isAllowCall,
//                fullname, image);

//        String userImage = mFuguGetMessageResponse.getData().getAgentImage();
        String userImage = CommonData.getImagePath();

        if (HippoConfig.getInstance().getCallData() != null) {
            HippoConfig.getInstance().getCallData().onCallClick(this, callType, channelId, userId, isAgentFlow, isAllowCall,
                    fullname, image, userImage);
        } else {
            LibApp.getInstance().onCallBtnClick(this, callType, channelId, userId, isAgentFlow, isAllowCall,
                    fullname, image, userImage);
        }

        try {
            LibApp.getInstance().trackEvent("Chat Screen", "Call button click", "" + callType);
        } catch (Exception e) {

        }
    }


    /**
     * Method to select an image for Position in
     * the List of AddImages
     */
    public void selectImage(View view, boolean isFromChatFormAttachment, AttachmentSelectedTicketListener listner, int position) {
        attachmentItemPosition = position;
        attachmentSelectedTicketListener = listner;
        attachemtFromFormTicketBot = isFromChatFormAttachment;
        selectImage(view);
    }

    public void selectImage(View view) {
        if (fuguImageUtils == null)
            fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);

        boolean isPayment = (channelId.compareTo(-1L) > 0 && CommonData.getAttributes().isPaymentEnabled());

        AttachmentSheetFragment bottomSheetFragment = AttachmentSheetFragment.newInstance();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    isFromFilePicker = true;
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    StringBuilder builder = new StringBuilder();
                    String path = null;

                    ImageFile actualfile = list.get(0);
                    String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
                    String fileExt = Util.getExtension(actualfile.getPath());

                    String fileName = muid;
                    if (!TextUtils.isEmpty(fileExt)) {
                        actualfile.setName(fileName + "." + fileExt);
                    }
                    actualfile.setMuid(muid);
                    String localPath = Util.setImageFullPath(fileName + "." + fileExt);
                    actualfile.setDestinationPath(localPath);

                    if (actualfile != null)
                        compressImage(actualfile);

                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    isFromFilePicker = true;
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    VideoFile file = list.get(0);

                    String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
                    String fileExt = Util.getExtension(file.getPath());
                    String fileName = muid;//file.getName();
                    if (!TextUtils.isEmpty(fileExt)) {
                        fileName = fileName + "." + fileExt;
                        //fileName = file.getName() + "." + fileExt;
                    }
                    FileuploadModel fileuploadModel = new FileuploadModel(fileName, String.valueOf(file.getSize()), file.getPath(), muid);
                    fileuploadModel.setFileSizeReadable(Util.humanReadableByteCount(file.getSize(), true));
                    fileuploadModel.setDocumentType(DocumentType.VIDEO.toString());
                    fileuploadModel.setMessageType(FILE_MESSAGE);

                    copingFileToLocal(fileuploadModel, VIDEO_FOLDER);

                    HippoLog.v(TAG, "File List: " + new Gson().toJson(list));
                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    isFromFilePicker = true;
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                    AudioFile file = list.get(0);
                    String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
                    String fileExt = Util.getExtension(file.getPath());
//                    String fileName = file.getName();
//                    if (!TextUtils.isEmpty(fileExt)) {
////                        fileName = file.getName() + "_" + muid + "." + fileExt;
//                        fileName = file.getName() + "." + fileExt;
//                    }

                    String fileName = muid;
                    if (!TextUtils.isEmpty(fileExt)) {
                        fileName = fileName + "." + fileExt;
                    }

                    FileuploadModel fileuploadModel = new FileuploadModel(fileName, String.valueOf(file.getSize()), file.getPath(), muid);
                    fileuploadModel.setFileSizeReadable(Util.humanReadableByteCount(file.getSize(), true));
                    fileuploadModel.setDocumentType(DocumentType.AUDIO.toString());
                    fileuploadModel.setMessageType(FILE_MESSAGE);

                    copingFileToLocal(fileuploadModel, AUDIO_FOLDER);

                    HippoLog.v(TAG, "File List: " + new Gson().toJson(list));
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    isFromFilePicker = true;
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    StringBuilder builder = new StringBuilder();
                    NormalFile file = list.get(0);
                    /*for (NormalFile file : list) {
                        String path = file.getPath();
                        builder.append(path + "\n");
                        builder.append(file.getMimeType() +"\n");
                        builder.append(file.getSize() +"\n");
                    }*/

                    String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
                    String fileExt = Util.getExtension(file.getPath());
//                    String fileName = file.getName();
//                    if (!TextUtils.isEmpty(fileExt)) {
//                        fileName = file.getName() + "." + fileExt;
////                        fileName = file.getName() + "_" + muid + "." + fileExt;
//                    }

                    String fileName = muid;
                    if (!TextUtils.isEmpty(fileExt)) {
                        fileName = fileName + "." + fileExt;
                    }

                    String filePath = "";

                    FileuploadModel fileuploadModel = new FileuploadModel(fileName, String.valueOf(file.getSize()), file.getPath(), muid);
                    fileuploadModel.setFileSizeReadable(Util.humanReadableByteCount(file.getSize(), true));
                    fileuploadModel.setDocumentType(DocumentType.FILE.toString());
                    fileuploadModel.setMessageType(FILE_MESSAGE);

                    copingFileToLocal(fileuploadModel, DOC_FOLDER);
                    //sendMessage(getString(R.string.fugu_empty), FILE_MESSAGE, "", "", file.getPath(), null, fileuploadModel);


                    HippoLog.v(TAG, "File: " + builder.toString());
                    HippoLog.v(TAG, "File List: " + new Gson().toJson(list));
                }
                break;
            case Constant.REQUEST_CODE_TAKE_IMAGE:
                if (resultCode == RESULT_OK) {
                    isFromFilePicker = true;
                    String path = CommonData.getTime();
                    ImageFile imageFile = new ImageFile();
                    imageFile.setPath(path);
                    imageFile.setDestinationPath(path);
                    imageFile.setMuid(CommonData.getImageMuid());
                    imageFile.setSize(new File(path).length());
                    imageFile.setName(Util.extractFileNameWithSuffix(path));
                    HippoLog.d(TAG, "camera imageFile: " + new Gson().toJson(imageFile));
                    compressImage(imageFile);

                }
                break;
            case Constant.REQUEST_CODE_PICK_PAYMENT:
                if (resultCode == RESULT_OK) {
                    PaymentData paymentData = (PaymentData) data.getSerializableExtra("data");
                    //Log.v(TAG, "Data = "+new Gson().toJson(paymentData));
                    sendPaymentRequest(paymentData);
                }
                break;
            case Constant.REQUEST_CODE_IMAGE_VIEW:
                isFromFilePicker = true;
                break;
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE")) {
                        openFileIntent();
                    }
                } else {
                    if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
                        openFileIntent();
                    }
                }
                break;
        }
    }


    public void openScreenFromSheet(int id) {
        switch (id) {
            case 1:
                openCamera();
                break;
            case 2:
                Intent intent1 = new Intent(FuguChatActivity.this, ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, false);
                intent1.putExtra(IS_NEED_IMAGE_PAGER, true);
                intent1.putExtra(Constant.MAX_NUMBER, 1);
                intent1.putExtra(IS_NEED_FOLDER_LIST, false);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case 3:
                Intent intent3 = new Intent(FuguChatActivity.this, AudioPickActivity.class);
                intent3.putExtra(IS_NEED_RECORDER, false);
                intent3.putExtra(Constant.MAX_NUMBER, 1);
                intent3.putExtra(IS_NEED_FOLDER_LIST, true);
                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
                break;
            case 4:
                Intent intent2 = new Intent(FuguChatActivity.this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, false);
                intent2.putExtra(Constant.MAX_NUMBER, 1);
                intent2.putExtra(IS_NEED_FOLDER_LIST, true);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
                break;
            case 5:
                Intent intent4 = new Intent(FuguChatActivity.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);
                intent4.putExtra(IS_NEED_FOLDER_LIST, true);
                intent4.putExtra(NormalFilePickActivity.SUFFIX,
                        new String[]{"txt", "xlsx", "xls", "doc", "docX", "ppt", ".pptx", "pdf",
                                "ODT", "apk", "zip", "CSV", "SQL", "PSD"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            default:

                break;
        }
    }


    private void copingFileToLocal(FileuploadModel fileuploadModel, String folderType) {
        LoadingBox.showOn(FuguChatActivity.this);
        try {
            FileManager.getInstance().copyFile(fileuploadModel.getFilePath(), FOLDER_TYPE.get(folderType), fileuploadModel, new FileManager.FileCopyListener() {
                @Override
                public void onCopingFile(boolean flag, FileuploadModel fileuploadModel) {
                    if (attachemtFromFormTicketBot) {
                        sendAttachmentInBotTicket(fileuploadModel);
                    } else
                        sendMessage(getString(R.string.fugu_empty), FILE_MESSAGE, "", "", fileuploadModel.getFilePath(), null, fileuploadModel);
                    LoadingBox.hide();
                }

                @Override
                public void largeFileSize() {
                    String text = Restring.getString(FuguChatActivity.this, R.string.hippo_large_file);
                    String ok = Restring.getString(FuguChatActivity.this, R.string.fugu_ok);
                    showErrorMessage(text + " " + Util.humanReadableSize(HippoConfig.getMaxSize(), true), ok);
                    LoadingBox.hide();
                }

                @Override
                public void onError() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    boolean isCameraPermission;

    private void openCamera() {
        if (fuguImageUtils == null)
            fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);
        boolean isGranted = EasyPermissions.hasPermissions(this, "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        if (SDK_INT >= Build.VERSION_CODES.R)
            isGranted = EasyPermissions.hasPermissions(this, "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");


        isCameraPermission = true;
        if (isGranted) {
            fuguImageUtils.startCamera();
        } else {
            String text = Restring.getString(FuguChatActivity.this, R.string.vw_rationale_storage);
            EasyPermissions.requestPermissions(this, text,
                    RC_OPEN_CAMERA, "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        HippoLog.e(TAG, "onRequestPermissionsResult" + requestCode);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (isCameraPermission) {
            if (fuguImageUtils == null)
                fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);
            fuguImageUtils.startCamera();
            isCameraPermission = false;
        } else {

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // If Permission permanently denied, ask user again

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains("android.permission.MANAGE_EXTERNAL_STORAGE")) {
                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(1).build().show();

            } else
                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(0).build().show();
        } else {
            String text = Restring.getString(FuguChatActivity.this, R.string.hippo_grant_permission);
            Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
        }


//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        } else {
//            String text = Restring.getString(FuguChatActivity.this, R.string.hippo_grant_permission);
//            Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
//            //finish();
//        }
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    public void readExternalStorage() {
        boolean isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");

        if (SDK_INT >= Build.VERSION_CODES.R)
            isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");

        if (isGranted) {
            openFileIntent();
        } else {
            String text = Restring.getString(FuguChatActivity.this, R.string.vw_rationale_storage);
            if (SDK_INT >= Build.VERSION_CODES.R)
                EasyPermissions.requestPermissions(this, text,
                        RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");
            else
                EasyPermissions.requestPermissions(this, text,
                        RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    public boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R)
            return EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");
        else
            return EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    private void openFileIntent() {

    }

    public void compressImage(final ImageFile actualFile) {
        try {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            if (screenWidth > 1200)
                screenWidth = 1200;

            new Compressor()
                    .setMaxWidth(screenWidth)
                    .setMaxHeight(screenWidth)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setListener(new CompressorListener() {
                        @Override
                        public void onImageCompressed(File file, String path, ImageFile imageFile, ArrayList<Integer> integers) {
                            showImageDialog(FuguChatActivity.this, file, path, imageFile, integers);
                        }
                    })
                    .compressToFile(new File(actualFile.getPath()), actualFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showImageDialog(Context activity, File imgUrl, final String image, final ImageFile imageFile, final ArrayList<Integer> integers) {

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.dimAmount = 1.0f;

            dialogWindow.getAttributes().windowAnimations = R.style.CustomDialog;

            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            ZoomageView ivImage = dialog.findViewById(R.id.ivImage);
            RequestOptions myOptions = RequestOptions
                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.hippo_placeholder)
                    .error(R.drawable.hippo_placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(new CenterCrop(), new RoundedCorners(3));

            Glide.with(activity).asBitmap()
                    .apply(options)
                    .load(imgUrl)
                    //.transition(withCrossFade())
                    .into(ivImage);
            TextView tvCross = dialog.findViewById(R.id.tvCross);
            tvCross.setText(Restring.getString(activity, R.string.fugu_cancel));
//            LinearLayout linearLayout = dialog.findViewById(R.id.llMessageLayout);
//            linearLayout.setVisibility(View.VISIBLE);
//

            RelativeLayout ivSend = dialog.findViewById(R.id.ivSend);

            try {
                GradientDrawable bgShape = (GradientDrawable) ivSend.getBackground();
                bgShape.setColor(hippoColorConfig.getHippoSendBtnBg());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ivSend.setVisibility(View.VISIBLE);
            ivSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String muid = Util.getMuid(imageFile.getName());
                    if (!TextUtils.isEmpty(imageFile.getMuid())) {
                        muid = imageFile.getMuid();
                    }
                    FileuploadModel fileuploadModel = new FileuploadModel(imageFile.getName(), String.valueOf(imageFile.getSize()), imageFile.getPath(), muid);
                    fileuploadModel.setFileSizeReadable(Util.humanReadableByteCount(imageFile.getSize(), true));
                    fileuploadModel.setDocumentType(DocumentType.IMAGE.toString());
                    fileuploadModel.setMessageType(IMAGE_MESSAGE);
                    fileuploadModel.setDimns(integers);
                    fileuploadModel.setMuid(imageFile.getMuid());
                    if (attachemtFromFormTicketBot) {
                        sendAttachmentInBotTicket(fileuploadModel);
                    } else {
                        sendMessage(getString(R.string.fugu_empty), IMAGE_MESSAGE, "", "", imageFile.getPath(), null, fileuploadModel);
                    }
                    dialog.dismiss();
                    //sendMessage(getString(R.string.fugu_empty), IMAGE_MESSAGE, "", "", image, null);
                }
            });
            tvCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAttachmentInBotTicket(FileuploadModel fileuploadModel) {
        Log.e("attachment", "attachment in boat ticket");
        fileuploadModel.setTicketAttachmentFile(true);
        if (attachmentSelectedTicketListener != null)
            attachmentSelectedTicketListener.onAttachmentSelected(attachmentItemPosition);
        uploadFile(fileuploadModel);
    }

    private ArrayList<Integer> getIMGSize(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        ArrayList<Integer> dimns = new ArrayList<>();
        dimns.add(imageHeight);
        dimns.add(imageWidth);
        return dimns;
    }

    private Type fileuploadType = new TypeToken<List<FileuploadModel>>() {
    }.getType();

    private void uploadFile(FileuploadModel fileuploadModel) {

        ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(Prefs.with(this).getString(KEY, ""), fileuploadType);
        if (fileuploadModels == null)
            fileuploadModels = new ArrayList<>();
//        if (fileuploadModels.size() > 4) {
//            Toast.makeText(FuguChatActivity.this, "Already files in queue. Please wait", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(!fileuploadModels.contains(fileuploadModel)) {
//            fileuploadModels.add(fileuploadModel);
//            String data = new Gson().toJson(fileuploadModels, fileuploadType);
//            HippoLog.e(TAG, "data = " + data);
//            Prefs.with(this).save(KEY, data);
//        }

        boolean hasItem = false;
        for (FileuploadModel model : fileuploadModels) {
            if (model.getMuid().equals(fileuploadModel.getMuid())) {
                hasItem = true;
                break;
            }
        }

        if (!hasItem) {
            fileuploadModels.add(fileuploadModel);
            String data = new Gson().toJson(fileuploadModels, fileuploadType);
            HippoLog.e(TAG, "data = " + data);
            Prefs.with(this).save(KEY, data);
        }


        if (!isMyServiceRunning(MyForeGroundService.class)) {
            Intent intent = new Intent(getBaseContext(), MyForeGroundService.class);
            intent.setAction("start");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                //lower then Oreo, just start the service.
                startService(intent);
            }
        } else {
            //ToastUtil.getInstance(this).showToast("Already running");
        }

        if (channelId.compareTo(-1l) == 0) {
            if (mFuguGetMessageResponse.getData().getBotGroupId() != null
                    && mFuguGetMessageResponse.getData().getBotGroupId().intValue() > -1) {
                // do nothing
            } else {
                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                        .setMessageType(10)
                        .setText(etMsg.getText().toString().trim())
                        .setIsP2P(isP2P)
                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                        .setGetLabelMessageResponse(labelMessageResponse)
                        .setConversationParams(fuguCreateConversationParams)
                        .build();

                createConversation(attribute);
            }
        }
    }

    private BroadcastReceiver fileUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BROADCAST_STATUS, 0);
            switch (status) {
                case BroadCastStatus.UPLOADED_SUCESSFULLY:
                    if (attachmentSelectedTicketListener != null) {
                        String muid = intent.getStringExtra("muid");
                        int messageIndex = intent.getIntExtra("messageIndex", 0);
                        String imageUrl = intent.getStringExtra("image_url");
                        String thumbnailUrl = intent.getStringExtra("thumbnail_url");
                        FileuploadModel fileuploadModel = new Gson().fromJson(intent.getStringExtra("fileuploadModel"), FileuploadModel.class);


                        fileuploadModel.setUploadedUrl(imageUrl);
                        attachmentSelectedTicketListener.onAttachmentListener(fileuploadModel, attachmentItemPosition);
                        return;
                    }
                    if (channelId != null && channelId.equals(intent.getLongExtra("channelId", -2))) {
                        String muid = intent.getStringExtra("muid");
                        int messageIndex = intent.getIntExtra("messageIndex", 0);
                        String imageUrl = intent.getStringExtra("image_url");
                        String thumbnailUrl = intent.getStringExtra("thumbnail_url");
                        FileuploadModel fileuploadModel = new Gson().fromJson(intent.getStringExtra("fileuploadModel"), FileuploadModel.class);


                        if (attachmentSelectedTicketListener != null) {
                            fileuploadModel.setUploadedUrl(imageUrl);
                            attachmentSelectedTicketListener.onAttachmentListener(fileuploadModel, attachmentItemPosition);
                            return;
                        }
                        unsentMessageMapNew.put(muid, fileuploadModel.getMessageObject());

                        Message listItem = unsentMessages.get(fileuploadModel.getMuid());
                        if (listItem == null)
                            return;
                        Message message = listItem;
                        message.setMessageStatus(MESSAGE_FILE_UPLOADED);
                        message.setFileUrl(imageUrl);
                        message.setUrl(imageUrl);
                        message.setLocalImagePath("");
                        message.setThumbnailUrl(thumbnailUrl);
                        message.setUploadStatus(UPLOAD_COMPLETED);

                        unsentMessages.put(muid, message);

                        try {
                            fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_FILE_UPLOADED);
                            fuguMessageAdapter.notifyItemChanged(messageIndex);
                            if (!messageSendingRecursion && isNetworkAvailable()) {
                                sendMessages();
                            }
                        } catch (Exception e) {
                            try {
                                for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                                    if (!fuguMessageList.get(i).isDateView()) {
                                        Message currentMessage = fuguMessageList.get(i);
                                        if (currentMessage.getMuid().equals(muid)) {
                                            messageIndex = i;
                                            fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_FILE_UPLOADED);
                                            fuguMessageAdapter.notifyItemChanged(messageIndex);
                                            if (!messageSendingRecursion && isNetworkAvailable()) {
                                                sendMessages();
                                            }
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    break;
                case BroadCastStatus.UPLOADING_FAILED:
                    if (channelId != null && channelId.equals(intent.getLongExtra("channelId", -2))) {
                        String muid = intent.getStringExtra("muid");
                        int messageIndex = intent.getIntExtra("messageIndex", 0);
                        String imageUrl = intent.getStringExtra("image_url");
                        String thumbnailUrl = intent.getStringExtra("thumbnail_url");
                        FileuploadModel fileuploadModel = new Gson().fromJson(intent.getStringExtra("fileuploadModel"), FileuploadModel.class);

                        if (attachmentSelectedTicketListener != null)
                            return;

                        unsentMessageMapNew.put(muid, fileuploadModel.getMessageObject());

                        Message listItem = unsentMessages.get(fileuploadModel.getMuid());
                        if (listItem == null)
                            return;
                        Message message = listItem;
                        message.setMessageStatus(MESSAGE_FILE_RETRY);
                        message.setFileUrl(imageUrl);
                        message.setUrl(imageUrl);
                        message.setLocalImagePath(fileuploadModel.getFilePath());
                        message.setThumbnailUrl(thumbnailUrl);
                        message.setUploadStatus(UPLOAD_FAILED);

                        unsentMessages.put(muid, message);

                        try {
                            fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_FILE_RETRY);
                            fuguMessageAdapter.notifyItemChanged(messageIndex);
                        } catch (Exception e) {
                            try {
                                for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                                    if (!fuguMessageList.get(i).isDateView()) {
                                        Message currentMessage = fuguMessageList.get(i);
                                        if (currentMessage.getMuid().equals(muid)) {
                                            messageIndex = i;
                                            fuguMessageList.get(messageIndex).setMessageStatus(MESSAGE_FILE_RETRY);
                                            fuguMessageAdapter.notifyItemChanged(messageIndex);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    break;
                case BroadCastStatus.PUBLISHED:

                    break;
                case BroadCastStatus.CREATE_CHANNEL:
                    if (CommonData.isFirstTimeCreated()) {
                        CommonData.setFirstTimeCreated(false);
                        final FileuploadModel fileuploadModel = new Gson().fromJson(intent.getStringExtra("fileuploadModel"), FileuploadModel.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CreateChannelAttribute attribute = new CreateChannelAttribute.Builder()
                                        .setMessageType(10)
                                        .setText("")
                                        .setLabelId(fileuploadModel.getLabelId())
                                        .setJsonObject(fileuploadModel.getMessageObject())
                                        .setFuguGetMessageResponse(mFuguGetMessageResponse)
                                        .setGetLabelMessageResponse(labelMessageResponse)
                                        .setConversationParams(fuguCreateConversationParams)
                                        .setFileuploadModel(fileuploadModel)
                                        .build();

                                createConversation(attribute);
                                //createConversation(fileuploadModel.getMessageObject(), fileuploadModel.getLabelId());
                            }
                        });

                    }
                    break;
                case BroadCastStatus.MESSAGE_EXPIRED:
                    String muid = intent.getStringExtra("muid");
                    int messageIndex = intent.getIntExtra("messageIndex", 0);

                    if (unsentMessages == null)
                        unsentMessages = new LinkedHashMap<>();

                    Message listItem = unsentMessages.get(muid);
                    if (listItem != null) {
                        listItem.setIsMessageExpired(1);
                        listItem.setUploadStatus(UPLOAD_FAILED);
                    }

                    try {
                        JSONObject messageJson = unsentMessageMapNew.get(muid);
                        messageJson.put("is_message_expired", 1);
                        unsentMessageMapNew.put(muid, messageJson);
                        index = messageJson.optInt("message_index", -1);
                        fuguMessageAdapter.notifyItemChanged(index);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

                    CommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
                    CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

                    break;
                case BroadCastStatus.FILE_TYPE_NOT_ALLOWED:
                    String muidd = intent.getStringExtra("muid");
                    String errorMessage = intent.getStringExtra("errorMessage");
                    long channelId = intent.getLongExtra("channelId", -2);
                    int messageIndexx = intent.getIntExtra("messageIndex", 0);

                    if (conversation.getChannelId() == intent.getLongExtra("channelId", -2L) ||
                            conversation.getChannelId() == -1L) {

                        if (unsentMessages.get(muidd) != null)
                            unsentMessages.remove(muidd);
                        if (unsentMessageMapNew.get(muidd) != null)
                            unsentMessageMapNew.remove(muidd);
                        fuguMessageList.remove(messageIndexx);
                        if (fuguMessageList.size() > 0) {
                            if (fuguMessageList.get(fuguMessageList.size() - 1).getMessageType() == 0) {
                                fuguMessageList.remove(fuguMessageList.size() - 1);
                            }
                        }
                        fuguMessageAdapter.notifyDataSetChanged();
                    } else {
                        LinkedHashMap<String, JSONObject> unsent = CommonData.getUnsentMessageMapByChannel(channelId);
                        LinkedHashMap<String, Message> unsentmsg = CommonData.getUnsentMessageByChannel(channelId);

                        unsent.remove(muidd);
                        unsentmsg.remove(unsentmsg);
                        CommonData.setUnsentMessageMapByChannel(channelId, unsent);
                        CommonData.setUnsentMessageByChannel(channelId, unsentmsg);
                    }
                    Toast.makeText(FuguChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    break;
                default:

                    break;
            }
        }
    };

    private boolean checkButtonClick() {
        boolean click = false;
        if (isNetworkAvailable()) {
            click = true;
            ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(Prefs.with(this).getString(KEY, ""), fileuploadType);
            try {
                if (fileuploadModels != null && fileuploadModels.size() > 5) {
                    click = false;
                    String text = Restring.getString(FuguChatActivity.this, R.string.hippo_file_already_in_queue);
                    Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                click = true;
            }
        } else {
            String text = Restring.getString(FuguChatActivity.this, R.string.fugu_unable_to_connect_internet);
            Toast.makeText(FuguChatActivity.this, text, Toast.LENGTH_SHORT).show();
        }
        return click;
    }

    private void checkFileExpireyData() {
        checkUnsentMessageStatus(new RefreshDone() {
            @Override
            public void onRefreshComplete() {
                sendMessages();
            }
        });
    }

    private String userImage = CommonData.getImagePath();

    private String getUserImage() {
        if (TextUtils.isEmpty(userImage))
            userImage = "";//HippoConfig.getInstance().getUserData().getUserImgage();
        return userImage;
    }

    private void checkCallButtons() {

    }

    @Override
    public void showImageDialog(Context activity, String imgUrl, ImageView imageView, Message message) {
        try {
            if (!Utils.preventMultipleClicks()) {
                return;
            }

            Intent imageIntent = new Intent(activity, ImageDisplayActivity.class);
            Image image = new Image(message.getUrl(), message.getThumbnailUrl(), message.getMuid(), message.getSentAtUtc(), "");
            imageIntent.putExtra("image", image);
            startActivityForResult(imageIntent, Constant.REQUEST_CODE_IMAGE_VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getType(int messageType, boolean isSelf, boolean quickValues, String docType) {
        switch (messageType) {
            case IMAGE_MESSAGE:
                if (isSelf)
                    return HIPPO_IMAGE_MESSGAE_SELF;
                return HIPPO_IMAGE_MESSGAE_OTHER;
            case TEXT_MESSAGE:
            case ACTION_MESSAGE:
            case ACTION_MESSAGE_NEW:
            case FUGU_TEXT_VIEW:
                if (isSelf)
                    return ITEM_TYPE_SELF;
                return ITEM_TYPE_OTHER;
            case HIPPO_FILE_VIEW:
                if (isSelf) {
                    if (docType.equalsIgnoreCase(FuguAppConstant.DocumentType.VIDEO.toString())) {
                        return HIPPO_VIDEO_MESSGAE_SELF;
                    } else {
                        return HIPPO_SELF_FILE_VIEW;
                    }
                } else {
                    if (docType.equalsIgnoreCase(FuguAppConstant.DocumentType.VIDEO.toString())) {
                        return HIPPO_VIDEO_MESSGAE_OTHER;
                    } else {
                        return HIPPO_FILE_VIEW;
                    }
                }
            case FEEDBACK_MESSAGE:
                return ITEM_TYPE_RATING;
            case FUGU_QUICK_REPLY_VIEW:
                //if(event.getValues() == null || event.getValues().size() == 0)
                if (quickValues)
                    return FUGU_QUICK_REPLY_VIEW;
                return ITEM_TYPE_OTHER;
            case FUGU_FORUM_VIEW:
                return FUGU_FORUM_VIEW;
            case FUGU_SELF_VIDEO_VIEW:
                if (isSelf)
                    return FUGU_SELF_VIDEO_VIEW;
                return FUGU_OTHER_VIDEO_VIEW;
            case FUGU_GALLERY_VIEW:
                return FUGU_GALLERY_VIEW;
            case HIPPO_USER_CONSENT:
                return HIPPO_USER_CONSENT;
            case CARD_LIST:
                return AGENT_LIST_VIEW;
            case PAYMENT_TYPE:
                return AGENT_PAYMENT_VIEW;
            case MULTI_SELECTION:
                return HIPPO_MULTI_SELECTION;
            case HIPPO_NEW_LEAD_FORM_TICKET:
                return HIPPO_NEW_LEAD_FORM_TICKET;

            default:
                if (isSelf)
                    return HIPPO_UNKNOWN_MESSAGE_SELF;
                return HIPPO_UNKNOWN_MESSAGE_OTHER;
        }
    }


    private void checkAutoSuggestions() {
        if (HippoConfig.getInstance().getQuestions() == null || HippoConfig.getInstance().getQuestions().size() == 0)
            return;
        if (fuguMessageList == null || fuguMessageList.size() == 0) {
            updateData(-1);
            return;
        } else if (fuguMessageList.get(fuguMessageList.size() - 1).getMessageType() == ITEM_TYPE_OTHER) {
            try {
                int id = HippoConfig.getInstance().getQuestions().get(fuguMessageList.get(fuguMessageList.size() - 1).getMessage());
                if (id > -1) {
                    updateData(id);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isSuggestionNeeded) {
            isSuggestionNeeded = false;
            updateData(0);
        }
    }

    private void updateData(int id) {
        if (suggestionAdapter != null) {
            final ArrayList<String> data = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();
            ids.addAll(HippoConfig.getInstance().getMapping().get(id));
            if (ids.size() > 0) {
                for (int i = 0; i < ids.size(); i++) {
                    data.add(HippoConfig.getInstance().getSuggestions().get(ids.get(i)));
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    suggestionAdapter.updateList(data);

                }
            });

        }
    }


    private void takeMessageAction(String actionId, String url) {
        if (TextUtils.isEmpty(actionId))
            return;
        if (actionId.equalsIgnoreCase(ACTION.ASSIGNMENT)) {

        } else if (actionId.equalsIgnoreCase(ACTION.AUDIO_CALL)) {
            videoCallInit(AUDIO_CALL_VIEW);
        } else if (actionId.equalsIgnoreCase(ACTION.VIDEO_CALL)) {
            videoCallInit(VIDEO_CALL_VIEW);
        } else if (actionId.equalsIgnoreCase(ACTION.CONTINUE_CHAT)) {
            //ToastUtil.getInstance(this).showToast("4 "+actionId);

        } else if (actionId.equalsIgnoreCase(ACTION.OPEN_URL)) {
            if (!TextUtils.isEmpty(url)) {
                Intent intent = new Intent(FuguChatActivity.this, WebviewActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", tvToolbarName.getText().toString());
                startActivity(intent);
            }
        } else if (actionId.equalsIgnoreCase(ACTION.DEFAULT)) {

        }
    }

    private BottomSheetPopup bottomSheetPopup;

    private void selectPaymentMethod(String url, HippoPayment payment) {
        try {
            ArrayList<AddedPaymentGateway> arrayList = new ArrayList<>();
            for (AddedPaymentGateway gateway : CommonData.getPaymentList()) {
                if (gateway.getCurrencyallowed().contains(payment.getCurrency().toUpperCase()))
                    arrayList.add(gateway);
            }

            if (arrayList.size() == 1) {
                openPaymentDialog("", payment, arrayList.get(0));
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("currency", payment.getCurrency());
                bundle.putString("payment", new Gson().toJson(payment));
                BottomSheetPopup bottomSheetPopup = BottomSheetPopup.newInstance(bundle);
                bottomSheetPopup.show(getSupportFragmentManager(), bottomSheetPopup.getTag());
            }
        } catch (Exception e) {

        }
    }

    public void openPaymentDialog(String url, HippoPayment payment, AddedPaymentGateway paymentGateway) {
        isPaymentOpen = true;
        MakePayment makePayment = new MakePayment();
        makePayment.setChannel_id(channelId.intValue());
        makePayment.setAppSecretKey(HippoConfig.getInstance().getAppKey());
        makePayment.setEn_user_id(HippoConfig.getInstance().getUserData().getEnUserId());
        ArrayList<HippoPayment> arrayList = new ArrayList();
        arrayList.add(payment);
        makePayment.setItems(arrayList);
        String paymentData = new Gson().toJson(makePayment);
        if (paymentGateway.getGatewayId() == 1 && !TextUtils.isEmpty(paymentGateway.getKeyId())) {
            makePayment.setPayment_gateway_id(1);
            createPaymentLink(makePayment, paymentData, paymentGateway);
        } else {
            PaymentDialogFragment newFragment = PaymentDialogFragment.newInstance(url, paymentData, paymentGateway);
            newFragment.show(getSupportFragmentManager().beginTransaction(), "fragment_dialog");
        }
    }


    boolean isDisableReply = false;

    private synchronized void updateKeyboard(final String type) {
        if (etMsg == null || isDisableReply) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type.toUpperCase()) {
                    case INPUT_TYPE.NONE:
                        //llMessageLayout.setVisibility(View.GONE);
                        etMsg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        break;
                    case INPUT_TYPE.NUMBER:
                        //llMessageLayout.setVisibility(View.VISIBLE);
                        etMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    default:
                        //llMessageLayout.setVisibility(View.VISIBLE);
                        etMsg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        break;
                }
            }
        });
    }

    private void assignChat(Long channelID) {
        if (!TextUtils.isEmpty(HippoConfig.getInstance().getAgentEmail())) {
            CommonParams params = new CommonParams.Builder()
                    .add("agent_email", HippoConfig.getInstance().getAgentEmail())
                    .add("app_secret_key", HippoConfig.getInstance().getAppKey())
                    .add("en_user_id", enUserId)
                    .add("channel_id", channelID)
                    .build();

            RestClient.getApiInterface().assignAgent(params.getMap()).enqueue(new ResponseResolver<CommonResponse>() {
                @Override
                public void success(CommonResponse commonResponse) {
                    HippoConfig.getInstance().setAgentEmail("");
                }

                @Override
                public void failure(APIError error) {

                }
            });

        }
    }


    boolean isMessageInEditMode = false;
    private String firstEditMuid = "";
    int editPosition = -1;

//    public void cancelEditing() {
//        cancelEditing("");
//    }

    public void cancelEditing() {
        int position = editPosition;
        if (position == -1) {
            for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                Message msg = fuguMessageList.get(i);
                if (msg.getMuid().equalsIgnoreCase(firstEditMuid)) {
                    position = i;
                    break;
                }
            }
        }
        if (position == -1) {
            return;
        }
        fuguMessageList.get(position).setEditMode(false);
        fuguMessageAdapter.notifyItemChanged(position);
        etMsg.setText("");
        firstEditMuid = "";
        editPosition = -1;
        isMessageInEditMode = false;
        ivCancelEdit.setVisibility(View.GONE);
        ivAttachment.setVisibility(View.VISIBLE);
        ivSendBtn.setImageResource(R.drawable.ic_new_send);
    }

    public void editText(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int pos = position;
                if (pos > fuguMessageList.size() - 1) {
                    pos = fuguMessageList.size() - 1;
                }
                fuguMessageAdapter.setOnLongClickValue(false);
                if (isMessageInEditMode && editPosition > -1) {
                    fuguMessageList.get(editPosition).setEditMode(false);
                    fuguMessageAdapter.notifyItemChanged(editPosition);
                    firstEditMuid = "";
                    etMsg.setText("");
                }

                editPosition = pos;
                isMessageInEditMode = true;

                final Message message = fuguMessageList.get(pos);
                message.setEditMode(true);
                fuguMessageAdapter.notifyItemChanged(pos);
                ivAttachment.setVisibility(View.GONE);
                ivCancelEdit.setVisibility(View.VISIBLE);
                ivSendBtn.setImageResource(R.drawable.success);

                etMsg.setText(message.getMessage());
                etMsg.requestFocus();
                firstEditMuid = message.getMuid();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        etMsg.setSelection(message.getMessage().length());
                        KeyboardUtil.toggleKeyboardVisibility(FuguChatActivity.this);
                    }
                });

            }
        });
    }

    public void copyText(int position, boolean isReplied) {
        try {
            Message messageObj = fuguMessageList.get(position);
            int selectedPosition = 0;
            // TODO: 13/10/20 handle for consent message type
            /*if (messageObj.getMessageType() == USER_CONCENT_MESSAGE_LOCAL && isReplied) {
                String selectedBtnId = "";
                if (!TextUtils.isEmpty(messageObj.getSelectedBtnId())) {
                    selectedBtnId = messageObj.getSelectedBtnId();
                }

                for (int i = 0; i < messageObj.getContentValue().size(); i++) {
                    if (selectedBtnId.equalsIgnoreCase(messageObj.getContentValue().get(i).getBtnId())) {
                        selectedPosition = i;
                        break;
                    }
                }
            }*/

            fuguMessageAdapter.setOnLongClickValue(false);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String message = messageObj.getMessage().replace("<br/>", "hippoLineBreak").replace("<br>", "hippoLineBreak").replace("\n", "hippoLineBreak");
            if (isReplied) {
                message = messageObj.getContentValue().get(selectedPosition).getBtnTitle();
            }
            ClipData clip = ClipData.newPlainText("", Html.fromHtml(message).toString().replace("hippoLineBreak", "\n"));
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            fuguMessageAdapter.setOnLongClickValue(false);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            Message messageObj = fuguMessageList.get(fuguMessageList.size() - 1);
            String message = messageObj.getMessage().replace("<br/>", "hippoLineBreak").replace("<br>", "hippoLineBreak").replace("\n", "hippoLineBreak");
            ClipData clip = ClipData.newPlainText("", Html.fromHtml(message).toString().replace("hippoLineBreak", "\n"));
            clipboard.setPrimaryClip(clip);

            e.printStackTrace();
        }
    }

    public void setOnLongClickValue() {
        fuguMessageAdapter.setOnLongClickValue(false);
    }

    private void updateMessage(final String message) {
        MessageUpdate.INSTANCE.updateMessage(FuguChatActivity.this, channelId,
                firstEditMuid, 2, message, new OnMessageUpdate() {

                    @Override
                    public void onUpdatefailed() {
                        cancelEditing();
                        //sendAndupdateMessage(message);
                    }

                    @Override
                    public void onUpdateListener() {
                        cancelEditing();
                    }
                });
    }

    public void deleteMessage(int position, String muid, int messageStatus) {
        firstEditMuid = muid;
        editPosition = position;

        MessageUpdate.INSTANCE.updateMessage(FuguChatActivity.this, channelId,
                muid, 1, "", new OnMessageUpdate() {

                    @Override
                    public void onUpdatefailed() {
                        //sendAndupdateMessage("");

                    }

                    @Override
                    public void onUpdateListener() {

                    }
                });
    }

    private void updateEdittedMessage(JSONObject messageJson) {
        String muid = messageJson.optString("muid");
        Message msg = sentMessages.get(muid);
        if (editPosition == -1) {
            if (muid.equalsIgnoreCase(fuguMessageList.get(msg.getMessageIndex()).getMuid()))
                editPosition = msg.getMessageIndex();
            else {
                for (int i = fuguMessageList.size() - 1; i >= 0; i--) {
                    Message currentMessage = fuguMessageList.get(i);
                    if (currentMessage.getMuid().equals(muid)) {
                        editPosition = i;
                        break;
                    }
                }
            }
        }
        Message message = fuguMessageList.get(editPosition);
        if (messageJson.optInt("status") == 2) {
            fuguMessageList.get(editPosition).setMessage(messageJson.optString("edited_message"));
            fuguMessageList.get(editPosition).setMessageState(5);
            sentMessages.get(muid).setMessage(messageJson.optString("edited_message"));
            sentMessages.get(muid).setMessageState(5);
        } else if (messageJson.optInt("status") == 1) {
            String deleteStr = Restring.getString(FuguChatActivity.this, R.string.hippo_message_deleted);
            fuguMessageList.get(editPosition).setMessage(deleteStr);
            fuguMessageList.get(editPosition).setMessageState(4);
            sentMessages.get(muid).setMessage("<i>" + deleteStr + "</i>");
            sentMessages.get(muid).setMessageState(4);
        }
        fuguMessageAdapter.notifyItemChanged(editPosition);
        editPosition = -1;
    }

    private void createPaymentLink(MakePayment makePayment, final String paymentData, final AddedPaymentGateway paymentGateway) {
        if (isNetworkAvailable()) {
            makePayment.setIsSdkFlow(1);
            makePayment.setIs_multi_gateway_flow(1);
            makePayment.setDevice_details(CommonData.deviceDetailString(this));
            makePayment.setApp_version(HippoConfig.getInstance().getCodeVersion());
            makePayment.setDevice_id(UniqueIMEIID.getUniqueIMEIId(this));
            makePayment.setSource_type(1);
            makePayment.setDevice_type(ANDROID_USER);

            if (!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
                makePayment.setLang(HippoConfig.getInstance().getCurrentLanguage());

            RestClient.getApiInterface().createPaymentLink(makePayment)
                    .enqueue(new ResponseResolver<PaymentResponse>(this, true, true) {
                        @Override
                        public void success(PaymentResponse response) {
                            if (!TextUtils.isEmpty(response.getData().getOrderId())) {
                                RazorPayData options = new RazorPayData();
                                //options.setAuthOrderId(Integer.parseInt()response.getData().getAuth_order_id());
                                options.setOrderId(response.getData().getOrderId());
                                options.setCurrency(response.getData().getCurrency());
                                options.setDescription(response.getData().getDescription());
                                options.setPhoneNo(response.getData().getPhoneNumber());
                                options.setAmount(response.getData().getAmount());
                                options.setUserEmail(response.getData().getUserEmail());
                                options.setName(response.getData().getName());
                                options.setAuthOrderId(response.getData().getAuth_order_id());
                                options.setReferenceId(response.getData().getReference_id());
                                startRazorPayPayment(options, response.getData().getApiKey());
                            } else {
                                PaymentDialogFragment newFragment = PaymentDialogFragment.newInstance(response.getData().getPaymentUrl(), paymentData, paymentGateway);
                                newFragment.show(getSupportFragmentManager().beginTransaction(), "fragment_dialog");
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            //handleLayout(2);
                        }
                    });
        } else {
            String text = Restring.getString(this, R.string.fugu_unable_to_connect_internet);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    private void startRazorPayPayment(RazorPayData options, String apiKey) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(apiKey);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(FuguAppConstant.KEY_ORDER_ID, options.getOrderId());
            jsonObject.put(FuguAppConstant.KEY_PHONE_NO, options.getPhoneNo());
            jsonObject.put(FuguAppConstant.KEY_USER_EMAIL, options.getUserEmail());
            jsonObject.put(FuguAppConstant.KEY_DESCRIPTION, options.getDescription());
            //jsonObject.put(FuguAppConstant.KEY_AUTH_ORDER_ID, options.getAuthOrderId());
            jsonObject.put(FuguAppConstant.KEY_AMOUNT, options.getAmount());
            jsonObject.put(FuguAppConstant.KEY_CURRENCY, options.getCurrency());
            jsonObject.put(FuguAppConstant.KEY_NAME, options.getName());
            if (!TextUtils.isEmpty(options.getUserEmail()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.getUserEmail());
            if (!TextUtils.isEmpty(options.getPhoneNo()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.getPhoneNo());
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi");
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");

            startRazorPayPayment(this, jsonObject, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            JSONObject jObj = new JSONObject();
            try {
                jObj = new JSONObject(gson.toJson(options, RazorPayData.class));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            startRazorPayPayment(this, jObj, apiKey);
        }
    }

    private void startRazorPayPayment(Activity activity, JSONObject options, String apiKeys) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(apiKeys);
        try {
            options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID);
            if (options.has(FuguAppConstant.KEY_USER_EMAIL))
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(FuguAppConstant.KEY_USER_EMAIL).toString());
            if (options.has(FuguAppConstant.KEY_PHONE_NO))
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(FuguAppConstant.KEY_PHONE_NO).toString());
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "");
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");
            Log.i("RazorpayBaseActivity", "startRazorPayPayment options= " + options);
            checkout.setFullScreenDisable(true);
            checkout.open(activity, options);
            //loadingLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }

    @Override
    public void onPaymentSuccess(String s, com.razorpay.PaymentData paymentData) {
        Log.e("Data", "onPaymentSuccess = " + s);
        Log.e("Data", "PaymentData = " + new Gson().toJson(paymentData));

    }

    @Override
    public void onPaymentError(int i, String s, com.razorpay.PaymentData paymentData) {
        Log.e("Data", "onPaymentError = " + s);
        Log.e("Data", "onPaymentError PaymentData = " + new Gson().toJson(paymentData));
    }
}
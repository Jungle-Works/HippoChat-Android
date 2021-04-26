package com.hippo.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.*;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.appcompat.widget.*;

import android.os.Handler;
import android.text.*;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.*;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.*;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.activity.ImageDisplayActivityNew;
import com.hippo.activity.VideoPlayerActivity;
import com.hippo.constant.FuguAppConstant;
import com.hippo.customLayout.StartSnapHelper;
import com.hippo.database.CommonData;
import com.hippo.fragment.BottomSheetMsgFragment;
import com.hippo.interfaces.CustomerInitalListener;
import com.hippo.interfaces.OnMultiSelectionListener;
import com.hippo.langs.Restring;
import com.hippo.model.*;
import com.hippo.tickets.DataFormTicketAdapter;
import com.hippo.utils.*;
import com.hippo.utils.RatingBar;
import com.hippo.utils.fileUpload.FileManager;
import com.hippo.utils.fileUpload.FileuploadModel;
import com.hippo.utils.filepicker.ToastUtil;
import com.hippo.utils.filepicker.Util;
import com.hippo.utils.loadingBox.ProgressWheel;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Bhavya Rattan on 02/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant,
        QRCallback, OnRecyclerListener, UserConcentListener, CustomerInitalListener, AgentViewListener, OnPaymentListener {

    private static final String TAG = FuguMessageAdapter.class.getSimpleName();
    // in center view
    private final int FUGU_TYPE_HEADER = 0;

    // self side view
    private final int FUGU_ITEM_TYPE_SELF = 2;
    private final int FUGU_VIDEO_CALL_VIEW = 18;
    private final int HIPPO_FILE_SENT_VIEW = 10;
    private final int HIPPO_VIDEO_MESSGAE_SELF = 12;
    private final int HIPPO_UNKNOWN_MESSAGE_SELF = -2;
    private final int FUGU_QUICK_REPLY_VIEW = 16;
    private final int Hippo_IMAGE_MESSAGE_SELF = 22;
    private final int HIPPO_NEW_LEAD_FORM = 23;
    private final int HIPPO_NEW_LEAD_FORM_TICKET = 29;

    // other side view
    private final int FUGU_ITEM_TYPE_OTHER = 1;
    private final int FUGU_RATING_VIEW = 3;
    private final int FUGU_FORUM_VIEW = 17;
    private final int FUGU_OTHER_VIDEO_CALL_VIEW = 19;
    private final int HIPPO_FILE_RECEIVED_VIEW = 11;
    private final int HIPPO_VIDEO_MESSGAE_OTHER = 13;
    private final int HIPPO_UNKNOWN_MESSAGE_OTHER = -1;
    private final int HIPPO_IMAGE_MESSGAE_OTHER = 21;

    // Both side view
    private final int FUGU_TEXT_VIEW = 15;
    private final int HIPPO_USER_CONCENT_VIEW = 20;
    private final int HIPPO_AGENT_LIST_VIEW = 24;
    private final int HIPPO_AGENT_PAYMENT_VIEW = 25;
    private final int HIPPO_MULTISELECTION_VIEW = 26;

    //view not used yet
    private final int FUGU_GALLERY_VIEW = -990;


    private DateUtils fuguDateUtil = DateUtils.getInstance();
    private Long fuguLabelId;
    private OnRetryListener mOnRetry;
    private onVideoCall onVideoCall;
    private HippoColorConfig hippoColorConfig;
    private Activity activity;
    private FuguConversation fuguConversation;
    private FuguChatActivity fuguChatActivity;
    private Configuration config;
    //for bot
    private QuickReplyAdapaterActivityCallback qrCallback;
    private OnRatingListener onRatingListener;
    private OnUserConcent onUserConcent;
    private FragmentManager fragmentManager;

    private String agentName = "";
    private boolean isVideoCallEnabled = false;
    private boolean isAudioCallEnabled = false;
    String callType = "video";
    boolean isSourceMessageEnabled = false;
    private static final int p2pChatType = 1;
    int chatType = 0;

    @NonNull
    private List<Message> fuguItems = Collections.emptyList();
    private RecyclerView recyclerView;
    private Typeface customBold, customNormal;
    //private RequestOptions options;

    public FuguMessageAdapter(Activity activity, @NonNull List<Message> fuguItems, RecyclerView recyclerView, Long fuguLabelId,
                              FuguConversation fuguConversation, OnRatingListener onRatingListener,
                              QuickReplyAdapaterActivityCallback callback, FragmentManager fragmentManager, OnUserConcent onUserConcent,
                              boolean isSourceMessageEnabled, String botImage, int chatType) {
        this.fuguItems = fuguItems;
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.fuguLabelId = fuguLabelId;
        this.fuguConversation = fuguConversation;
        removeDefaultMsgTime();
        hippoColorConfig = CommonData.getColorConfig();
        config = activity.getResources().getConfiguration();
        this.onRatingListener = onRatingListener;
        this.onUserConcent = onUserConcent;
        this.qrCallback = callback;
        this.fragmentManager = fragmentManager;
        this.isSourceMessageEnabled = isSourceMessageEnabled;
        this.botImage = botImage;
        this.chatType = chatType;
        amount = "";
        HippoLog.e("TAG", "chatType >~~~~~~~~~~> " + chatType);

        customBold = Typeface.createFromAsset(activity.getAssets(), "fonts/ProximaNova-Sbold.ttf");
        customNormal = Typeface.createFromAsset(activity.getAssets(), "fonts/ProximaNova-Reg.ttf");
    }

    public int getCurrentChatType() {
        return chatType;
    }

    public void updateChatType(int chatType) {
        HippoLog.e("TAG", "updated chatType >~~~~~~~~~~> " + chatType);
        this.chatType = chatType;
        notifyDataSetChanged();
    }

    int dp1 = 0;
    int dp2 = 0;
    int dp4 = 0;
    int dp8 = 0;
    int dp15 = 0;
    int dp20 = 0;
    int dp30 = 0;
    int dp40 = 0;
    String botImage = "";

    private String getBotImage() {
        if (TextUtils.isEmpty(botImage)) {
            botImage = CommonData.getUserDetails().getData().getBotImageUrl();
        }
        if (TextUtils.isEmpty(botImage))
            botImage = "http://";
        return botImage;
    }

    private int dp1() {
        if (dp1 == 0) {
            dp1 = pxToDp(1);
        }
        return dp1;
    }

    private int dp2() {
        if (dp2 == 0) {
            dp2 = pxToDp(2);
        }
        return dp2;
    }

    private int dp4() {
        if (dp4 == 0) {
            dp4 = pxToDp(4);
        }
        return dp4;
    }

    private int dp8() {
        if (dp8 == 0) {
            dp8 = pxToDp(8);
        }
        return dp8;
    }

    private int dp15() {
        if (dp15 == 0) {
            dp15 = pxToDp(15);
        }
        return dp15;
    }

    private int dp20() {
        if (dp20 == 0) {
            dp20 = pxToDp(20);
        }
        return dp20;
    }

    private int dp30() {
        if (dp30 == 0) {
            dp30 = pxToDp(35);
        }
        return dp30;
    }

    private int dp40() {
        if (dp40 == 0) {
            dp40 = pxToDp(40);
        }
        return dp40;
    }


    public void setOnRetryListener(OnRetryListener OnRetryListener) {
        mOnRetry = OnRetryListener;
    }

    public void setOnVideoCallListener(onVideoCall onVideoCall) {
        this.onVideoCall = onVideoCall;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void isVideoCallEnabled(boolean isVideoCallEnabled) {
        this.isVideoCallEnabled = isVideoCallEnabled;
    }

    public void isAudioCallEnabled(boolean isAudioCallEnabled) {
        this.isAudioCallEnabled = isAudioCallEnabled;
    }

    @Override
    public void onFormClickListener(int id, Message currentFormMsg) {
        onRatingListener.onFormDataCallback(currentFormMsg);
    }
 @Override
    public void onFormClickListenerTicket(int id, Message currentFormMsg,int position) {
        onRatingListener.onTicketdataCallback(currentFormMsg,position);
    }

    @Override
    public void skipFormCallback(Message currentFormMsg) {
        onRatingListener.onSkipForm(currentFormMsg);
    }

    @Override
    public void onClickListener(Message message, int pos, QuickReplyViewHolder viewHolder) {
        viewHolder.list_qr.setVisibility(View.GONE);
        qrCallback.QuickReplyListener(message, pos);
    }

    @Override
    public void onItemClick(View viewClicked, View parentView, int position) {
        int positionInList = recyclerView.getChildLayoutPosition(parentView);
        if (positionInList != RecyclerView.NO_POSITION) {
            if (!Utils.preventMultipleClicks()) {
                return;
            }
            Message otherVideoMessage = fuguItems.get(positionInList);
            String fname = Util.getFileName(otherVideoMessage.getFileName(), otherVideoMessage.getMuid());
            String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(otherVideoMessage.getDocumentType()));
            Intent intent = new Intent(activity, VideoPlayerActivity.class);
            intent.putExtra("url", localPath);
            intent.putExtra("title", otherVideoMessage.getFileName());
            activity.startActivity(intent);
        }
    }

    public void onItemClick(View parentView, int position) {
        int positionInList = recyclerView.getChildLayoutPosition(parentView);
        if (positionInList != RecyclerView.NO_POSITION) {
            if (!Utils.preventMultipleClicks()) {
                return;
            }
            Message message = fuguItems.get(positionInList);
            if (message.getCustomAction() != null && message.getCustomAction().getHippoPayment() != null
                    && message.getCustomAction().getHippoPayment().size() == 1) {
                if (onUserConcent != null)
                    onUserConcent.onPaymentLink(position, message, message.getCustomAction().getHippoPayment().get(0),
                            message.getCustomAction().getHippoPayment().get(0).getPaymentUrl());
            } else {
                if (payment == null || paymentMessage == null) {
                    String text = Restring.getString(activity, R.string.hippo_minimum_Multiselection);
                    ToastUtil.getInstance(activity).showToast(text);
                    return;
                }
                if (onUserConcent != null) {
                    onUserConcent.onPaymentLink(position, paymentMessage, payment, url);
                }
            }
        }
    }

    private Message paymentMessage;
    private HippoPayment payment;
    private int paymentPos;
    private String url;
    String amount = "";

    @Override
    public void onPaymentViewClicked(Message message, HippoPayment payment, int position, String url, int messagePos) {
        this.payment = payment;
        this.paymentMessage = message;
        this.url = url;
    }

    @Override
    public void onUserConcent(int position, String btnId, Message message, String actionId, String url) {
        if (onUserConcent != null)
            onUserConcent.onConcentClicked(position, message, actionId, url);
    }

    @Override
    public void onButtonClicked(ArrayList<Object> objects) {

    }

    @Override
    public void onNotifyAdapter(ArrayList<Object> objects) {

    }

    @Override
    public void onShowProfile(Message message, String userId, int pos) {
        if (qrCallback != null)
            qrCallback.onProfileClicked(message, userId, pos);
    }

    @Override
    public void onCardClickListener(Message message, String userId, int pos) {
        if (qrCallback != null)
            qrCallback.onCardClicked(message, userId, pos);
    }

    public void updateMessageList(List<Message> fuguMessageList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(this.fuguItems, fuguMessageList));
        fuguItems.clear();
        this.fuguItems.addAll(fuguMessageList);
        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnRetryListener {
        void onRetry(String file, final int messageIndex, int messageType, FuguFileDetails fileDetails, String uuid);

        void onMessageRetry(String muid, int position);

        void onMessageCancel(String muid, int position);

        void onFileMessageRetry(String muid, int position);
    }

    public void updateList(@NonNull List<Message> items) {
        updateList(items, true);
    }

    public void updateList(@NonNull List<Message> items, boolean flag) {
        this.fuguItems = items;
        //updateMessageList(items);
        if (flag)
            removeDefaultMsgTime();
    }

    private void removeDefaultMsgTime() {
        if (fuguItems.size() > 0) {
            for (int i = 0; i < 2; i++) {
                if (i >= fuguItems.size()) {
                    break;
                }

                if (fuguItems.get(i).getMessageType() == FUGU_ITEM_TYPE_OTHER && fuguLabelId.compareTo(-1L) != 0) {
                    fuguItems.get(i).setSentAtUtc("");
                    break;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normalView;
        switch (viewType) {
            case FUGU_ITEM_TYPE_SELF:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_right, parent, false);
                return new SelfMessageViewHolder(normalView, this);
            case FUGU_ITEM_TYPE_OTHER:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_left, parent, false);
                return new OtherMessageViewHolder(normalView);
            case FUGU_TYPE_HEADER:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_date, parent, false);
                return new DateViewHolder(normalView);
            case FUGU_FORUM_VIEW:

                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_data_fourm, parent, false);
                return new ForumViewHolder(normalView);
            case HIPPO_NEW_LEAD_FORM_TICKET:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_data_fourm, parent, false);
                return new ForumTicketViewHolder(normalView);
            case FUGU_TEXT_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_text_item, parent, false);
                return new SimpleTextView(normalView);
            case HIPPO_FILE_SENT_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_file_sent, parent, false);
                return new SentFileViewHolder(normalView);
            case HIPPO_FILE_RECEIVED_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_file_received, parent, false);
                return new ReceivedFileViewHolder(normalView);
            case HIPPO_VIDEO_MESSGAE_SELF:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_video_self, parent, false);
                return new SelfVideoMessageViewHolder(normalView);
            case HIPPO_VIDEO_MESSGAE_OTHER:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_video_other, parent, false);
                return new OtherVideoMessageViewHolder(normalView, this);
            case FUGU_QUICK_REPLY_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_quick_replay, parent, false);
                return new QuickReplyViewHolder(normalView);
            case FUGU_GALLERY_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_gallery, parent, false);
                return new GalleryViewHolder(normalView);
            case FUGU_RATING_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_feedback_dialog, parent, false);
                return new RatingViewHolder(normalView, new MyCustomEditTextListener());
            case FUGU_VIDEO_CALL_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_video_self_side, parent, false);
                return new SelfVideoViewHolder(normalView);
            case FUGU_OTHER_VIDEO_CALL_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_video_other_side, parent, false);
                return new VideoViewHolder(normalView);
            case HIPPO_USER_CONCENT_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_user_concent, parent, false);
                return new UserConcentViewHolder(normalView);
            case HIPPO_UNKNOWN_MESSAGE_SELF:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_right, parent, false);
                return new SelfMessageViewHolder(normalView, this);
            case HIPPO_UNKNOWN_MESSAGE_OTHER:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_left, parent, false);
                return new OtherMessageViewHolder(normalView);
            case HIPPO_IMAGE_MESSGAE_OTHER:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_video_other, parent, false);
                return new OtherVideoMessageViewHolder(normalView, this);
            case Hippo_IMAGE_MESSAGE_SELF:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_video_self, parent, false);
                return new SelfVideoMessageViewHolder(normalView);
            case HIPPO_NEW_LEAD_FORM:
                normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_data_fourm, parent, false);
                return new ForumViewHolder(normalView);
            case HIPPO_AGENT_LIST_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_recyclerview, parent, false);
                return new RecyclerViewHolder(normalView);
            case HIPPO_AGENT_PAYMENT_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_payment_view, parent, false);
                return new PaymentView(normalView, this);
            case HIPPO_MULTISELECTION_VIEW:
                normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_multi_selection_view, parent, false);
                return new MultiSelectionView(normalView);
            default:
                return null;
        }

    }

    private int pxToDp(int dpParam) {
        float d = activity.getResources().getDisplayMetrics().density;
        return (int) (dpParam * d); // margin in pixels
    }

    private Long downloadFile(String url, String fileName, String ext) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ext);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            return manager.enqueue(request);
        } else
            return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final int itemType = getItemViewType(position);
        fuguChatActivity = (FuguChatActivity) activity;

        boolean isRightToLeft = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isRightToLeft = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
            } else {
                isRightToLeft = TextUtilsCompat.getLayoutDirectionFromLocale(Locale
                        .getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
            }
        } catch (Exception e) {

        }

        switch (itemType) {
            case HIPPO_NEW_LEAD_FORM:
                final NewLeadViewHolder leadViewHolder = (NewLeadViewHolder) holder;
                final Message leadMessage = fuguItems.get(position);
                ArrayList<Object> objects = new ArrayList<>();
                objects.addAll(CommonData.getUserDetails().getData().getCustomerInitialFormInfo().getFields());
                objects.add(CommonData.getUserDetails().getData().getCustomerInitialFormInfo().getButton());
                CustomerInitalAdapter initalAdapter = new CustomerInitalAdapter(objects, fragmentManager, this);

                leadViewHolder.recyclerView.setAdapter(initalAdapter);

                break;
            case FUGU_VIDEO_CALL_VIEW:
                final SelfVideoViewHolder videoViewHolder = (SelfVideoViewHolder) holder;
                final Message videoMessage = fuguItems.get(position);

                String messageSelf = "";
                if (videoMessage.getMessageState() != null && videoMessage.getMessageState().intValue() == 2) {
                    String call_back = Restring.getString(activity, R.string.hippo_call_back);
                    videoViewHolder.callAgain.setText(call_back);
                    if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                        messageSelf = Restring.getString(activity, R.string.hippo_missed_call);
                        videoViewHolder.tvDuration.setText(Restring.getString(activity, R.string.hippo_the_voice_call));
                    } else {
                        messageSelf = Restring.getString(activity, R.string.hippo_missed_call);
                        videoViewHolder.tvDuration.setText(Restring.getString(activity, R.string.hippo_the_video_call));
                    }
                } else {
                    String call_back = Restring.getString(activity, R.string.hippo_call_back);
                    String call_again = Restring.getString(activity, R.string.call_again);
                    videoViewHolder.callAgain.setText(call_again);
                    if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                        messageSelf = Restring.getString(activity, R.string.hippo_the_voice_call_ended);
                    else
                        messageSelf = Restring.getString(activity, R.string.hippo_the_video_call_ended);
                }

                videoViewHolder.tvMsg.setText(messageSelf);


                if (videoMessage.getSentAtUtc().isEmpty()) {
                    videoViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    videoViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(videoMessage.getSentAtUtc())));
                    videoViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
                if (videoMessage.getVideoCallDuration() > 0) {
                    videoViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                    videoViewHolder.tvDuration.setVisibility(View.VISIBLE);
                    videoViewHolder.tvDuration.setText(convertSeconds(videoMessage.getVideoCallDuration()) + "");

                }

                boolean buttonFlagSelf = false;
                if (CommonData.getDirectCallBtnDisabled()) {
                    buttonFlagSelf = true;
                } else {
                    if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                        if (!CommonData.getAudioCallStatus() || !isAudioCallEnabled) {
                            buttonFlagSelf = true;
                        }
                    } else {
                        if (!CommonData.getVideoCallStatus() || !isVideoCallEnabled) {
                            buttonFlagSelf = true;
                        }
                    }
                }

                if (buttonFlagSelf) {
                    videoViewHolder.callAgain.setVisibility(View.GONE);
                    videoViewHolder.dividerView.setVisibility(View.GONE);
                }

                videoViewHolder.callAgain.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());

                videoViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onVideoCall != null) {
                            int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                            if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                                callType = FuguAppConstant.AUDIO_CALL_VIEW;
                            }
                            onVideoCall.onVideoCallClicked(callType);
                        }
                    }
                });
                break;
            case FUGU_OTHER_VIDEO_CALL_VIEW:
                final VideoViewHolder videoOtherViewHolder = (VideoViewHolder) holder;
                final Message videoOtherMessage = fuguItems.get(position);

                if (videoOtherMessage.getSentAtUtc().isEmpty()) {
                    videoOtherViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    videoOtherViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(videoOtherMessage.getSentAtUtc())));
                    //videoOtherViewHolder.tvTime.setText(DateUtils.getTime(dateUtil.convertToLocal(videoOtherMessage.getSentAtUtc())));
                    videoOtherViewHolder.tvTime.setVisibility(View.VISIBLE);
                }

                String message = "";
                if (videoOtherMessage.getMessageState() != null && videoOtherMessage.getMessageState().intValue() == 2) {
                    String call_back = Restring.getString(activity, R.string.hippo_call_back);
                    videoOtherViewHolder.callAgain.setText(call_back);
                    if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                        message = Restring.getString(activity, R.string.hippo_missed_call);
                        videoOtherViewHolder.tvDuration.setText(Restring.getString(activity, R.string.hippo_the_voice_call));
                    } else {
                        message = Restring.getString(activity, R.string.hippo_missed_call);
                        videoOtherViewHolder.tvDuration.setText(Restring.getString(activity, R.string.hippo_the_video_call));
                    }
                } else {
                    videoOtherViewHolder.callAgain.setText(Restring.getString(activity, R.string.call_again));
                    if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                        message = Restring.getString(activity, R.string.hippo_the_voice_call_ended);
                    else
                        message = Restring.getString(activity, R.string.hippo_the_video_call_ended);
                }

                videoOtherViewHolder.tvMsg.setText(message);


                if (videoOtherMessage.getVideoCallDuration() > 0) {
                    videoOtherViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                    videoOtherViewHolder.tvDuration.setVisibility(View.VISIBLE);
                    videoOtherViewHolder.tvDuration.setText(convertSeconds(videoOtherMessage.getVideoCallDuration()) + "");
                }

                boolean buttonFlag = false;
                if (CommonData.getDirectCallBtnDisabled()) {
                    buttonFlag = true;
                } else {
                    if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                        if (!CommonData.getAudioCallStatus() || !isAudioCallEnabled) {
                            buttonFlag = true;
                        }
                    } else {
                        if (!CommonData.getVideoCallStatus() || !isVideoCallEnabled) {
                            buttonFlag = true;
                        }
                    }
                }

                videoOtherViewHolder.callAgain.setVisibility(View.VISIBLE);
                videoOtherViewHolder.callDivider.setVisibility(View.VISIBLE);
                if (buttonFlag) {
                    videoOtherViewHolder.callAgain.setVisibility(View.GONE);
                    videoOtherViewHolder.callDivider.setVisibility(View.GONE);
                }

                String userNameText7 = "";

                userNameText7 = videoOtherMessage.getfromName();

                videoOtherViewHolder.tvUserName.setVisibility(View.VISIBLE);
                videoOtherViewHolder.userImage.setVisibility(View.VISIBLE);
                videoOtherViewHolder.tvUserName.setText(userNameText7 + "");

                loadCallUserImage(position, videoOtherMessage, videoOtherViewHolder.tvUserName, videoOtherViewHolder.otherView,
                        videoOtherViewHolder.userImage);


//                loadUserImage(videoOtherViewHolder.userImage, userNameText7, videoOtherMessage.getUserImage());
//                setUserName(videoOtherViewHolder.tvUserName);
                videoOtherViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onVideoCall != null) {
                            int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                            if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                                callType = FuguAppConstant.AUDIO_CALL_VIEW;
                            }
                            onVideoCall.onVideoCallClicked(callType);
                        }
                    }
                });

                videoOtherViewHolder.callAgain.setTextColor(hippoColorConfig.getHippoActionBarText());

                if (videoOtherMessage.getMessageState() != null && videoOtherMessage.getMessageState().intValue() == 2) {
                    videoOtherViewHolder.llChat.setBackgroundResource(R.drawable.hippo_missed_call_other);
                    videoOtherViewHolder.ivCallIcon.setImageResource(R.drawable.hippo_ic_missed_call);
                } else {
                    videoOtherViewHolder.llChat.setBackgroundResource(R.drawable.hippo_call_other);
                    videoOtherViewHolder.ivCallIcon.setImageResource(R.drawable.hippo_ic_call_received_white);
                }

                break;
            case HIPPO_FILE_SENT_VIEW:
                final SentFileViewHolder sentFileViewHolder = (SentFileViewHolder) holder;
                final Message fileSentMessage = fuguItems.get(position);
                setSelfMessageBackground(fileSentMessage, sentFileViewHolder.llRoot, sentFileViewHolder.llMessage, position);
                sentFileViewHolder.circleProgress.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());

                String fileExt = Util.getExtension(fileSentMessage.getFileName());
                if (TextUtils.isEmpty(fileExt))
                    fileExt = Util.getExtension(fileSentMessage.getFileUrl());
                Integer dimage = IMAGE_MAP.get(fileExt.toLowerCase());
                if (dimage != null) {
                    sentFileViewHolder.ivFileImage.setImageResource(dimage);
                } else {
                    sentFileViewHolder.ivFileImage.setImageResource(R.drawable.hippo_attachment);
                }
                sentFileViewHolder.tvFileName.setText(fileSentMessage.getFileName());
                if (fileExt.length() > 4) {
                    fileExt = fileExt.substring(0, 4) + "..";
                }
                sentFileViewHolder.tvFileExtension.setText(fileExt);
                if (!TextUtils.isEmpty(fileSentMessage.getFileSize())) {
                    try {
                        sentFileViewHolder.tvFileSize.setText(fileSentMessage.getFileSize());
                    } catch (Exception e) {
                        sentFileViewHolder.tvFileSize.setText(fileSentMessage.getFileSize());
                    }
                }

                sentFileViewHolder.tvFileName.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());
                sentFileViewHolder.tvFileExtension.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());
                sentFileViewHolder.tvFileSize.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());
                sentFileViewHolder.tvFileTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());


                setIntegrationSource(sentFileViewHolder.messageSourceType, sentFileViewHolder.messageSourceType1, fileSentMessage.getIntegrationSource());

                messageStatusTick(sentFileViewHolder.ivMessageState, fileSentMessage);
                setFileUploadStatus(sentFileViewHolder.ivFileImage, sentFileViewHolder.ivFilePlay, sentFileViewHolder.circleProgress, sentFileViewHolder.ivFileDownload, sentFileViewHolder.ivFileUpload, fileSentMessage, position);
                setDownloadClick(sentFileViewHolder.ivFileDownload, sentFileViewHolder.circleProgress, fileSentMessage, position);
                setUploadClick(sentFileViewHolder.ivFileUpload, sentFileViewHolder.circleProgress, fileSentMessage);
                sentFileViewHolder.ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                if (!fileSentMessage.isAudioPlaying()) {
                    sentFileViewHolder.ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                } else {
                    sentFileViewHolder.ivFilePlay.setImageResource(R.drawable.hippo_song_pause);
                }
                setFileCLickListener(sentFileViewHolder.llMessage, fileSentMessage, sentFileViewHolder.ivFilePlay, position, sentFileViewHolder.ivFileDownload);
                sentFileViewHolder.ivFileUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = sentFileViewHolder.getAdapterPosition();
                        if (mOnRetry != null)
                            mOnRetry.onFileMessageRetry(fileSentMessage.getMuid(), pos);
                    }
                });
                break;
            case HIPPO_FILE_RECEIVED_VIEW:
                final ReceivedFileViewHolder fHolder = (ReceivedFileViewHolder) holder;
                final Message fileReceivedMessage = fuguItems.get(position);

                fHolder.progressBar.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());

                setIntegrationSource(fHolder.messageSourceType, fHolder.messageSourceType1, fileReceivedMessage.getIntegrationSource());

                setOtherSideView(position, fileReceivedMessage, fHolder.tvUserName,
                        fHolder.llRoot, fHolder.userImage);

                fHolder.tvFileName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());
                fHolder.tvFileExtension.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());
                fHolder.tvFileSize.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());
                fHolder.tvFileTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());
                setTime(fHolder.tvFileTime, fileReceivedMessage.getSentAtUtc());

                String receivedFileExt = Util.getExtension(fileReceivedMessage.getFileUrl());
                Integer dimage1 = IMAGE_MAP.get(receivedFileExt.toLowerCase());
                if (dimage1 != null) {
                    fHolder.ivFileImage.setImageResource(dimage1);
                } else {
                    fHolder.ivFileImage.setImageResource(R.drawable.hippo_attachment);
                }
                fHolder.tvFileName.setText(fileReceivedMessage.getFileName());
                String fileExt1 = TextUtils.isEmpty(fileReceivedMessage.getFileExtension()) ? receivedFileExt : fileReceivedMessage.getFileExtension();
                if (fileExt1.length() > 4) {
                    fileExt1 = fileExt1.substring(0, 4) + "..";
                }
                fHolder.tvFileExtension.setText(fileExt1);
                fHolder.tvFileSize.setText(fileReceivedMessage.getFileSize());
                setFileDownLoadStatus(fHolder.ivFileImage, fHolder.ivFilePlay, fHolder.progressBar, fHolder.ivFileDownload, fHolder.ivFileUpload, fileReceivedMessage, position);
                setDownloadClick(fHolder.ivFileDownload, fHolder.progressBar, fileReceivedMessage, position);
                if (!fileReceivedMessage.isAudioPlaying()) {
                    fHolder.ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                } else {
                    fHolder.ivFilePlay.setImageResource(R.drawable.hippo_song_pause);
                }
                setFileCLickListener(fHolder.llMessage, fileReceivedMessage, fHolder.ivFilePlay, position, fHolder.ivFileDownload);

                break;
            case HIPPO_VIDEO_MESSGAE_SELF:
                final SelfVideoMessageViewHolder selfVideoMessageViewHolder = (SelfVideoMessageViewHolder) holder;
                final Message selfVideoMessage = fuguItems.get(position);
                setSelfMessageBackground(selfVideoMessage, selfVideoMessageViewHolder.llRoot, selfVideoMessageViewHolder.llImageMessage, position);

                selfVideoMessageViewHolder.circle_progress.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());

                setTime(selfVideoMessageViewHolder.tvImageTime, selfVideoMessage.getSentAtUtc());
                setMessageStatus(selfVideoMessageViewHolder.ivMessageState, selfVideoMessage.getMessageStatus(), true);
                if (!TextUtils.isEmpty(selfVideoMessage.getThumbnailUrl())) {
                    setImage(activity, selfVideoMessageViewHolder.ivImageMsg, selfVideoMessage.getThumbnailUrl());
                } else {
                    selfVideoMessageViewHolder.ivImageMsg.setVisibility(View.GONE);
                }
                //setImageHeightAndWidth(selfVideoMessageViewHolder.ivImageMsg, selfVideoMessageViewHolder.rlImageMessage, selfVideoMessageViewHolder.llImageMessage, selfVideoMessage, true);
                selfVideoMessageViewHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Utils.preventMultipleClicks()) {
                            return;
                        }

                        String fname = Util.getFileName(selfVideoMessage.getFileName(), selfVideoMessage.getMuid());
                        String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(selfVideoMessage.getDocumentType()));
                        if (TextUtils.isEmpty(localPath))
                            localPath = selfVideoMessage.getFileUrl();
                        Intent intent = new Intent(activity, VideoPlayerActivity.class);
                        intent.putExtra("url", localPath);
                        intent.putExtra("title", selfVideoMessage.getFileName());
                        activity.startActivity(intent);
                    }
                });

                setIntegrationSource(selfVideoMessageViewHolder.messageSourceType, selfVideoMessageViewHolder.messageSourceType1, selfVideoMessage.getIntegrationSource());

                setVideoUiStatus(true, selfVideoMessage, selfVideoMessageViewHolder.llDownload, selfVideoMessageViewHolder.ivPlay,
                        selfVideoMessageViewHolder.btnRetry, selfVideoMessageViewHolder.btnCancel, selfVideoMessageViewHolder.circle_progress, false);

                selfVideoMessageViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = selfVideoMessageViewHolder.getAdapterPosition();
                        if (mOnRetry != null)
                            mOnRetry.onMessageCancel(selfVideoMessage.getMuid(), pos);
                    }
                });

                selfVideoMessageViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = selfVideoMessageViewHolder.getAdapterPosition();
                        if (mOnRetry != null)
                            mOnRetry.onFileMessageRetry(selfVideoMessage.getMuid(), pos);
                    }
                });
                selfVideoMessageViewHolder.llDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //setDownloadClick(selfVideoMessageViewHolder.llDownload, selfVideoMessageViewHolder.circle_progress, selfVideoMessage, position);
                        if (!fuguChatActivity.checkPermission()) {
                            fuguChatActivity.readExternalStorage();
                            return;
                        }
                        selfVideoMessageViewHolder.circle_progress.setVisibility(View.VISIBLE);
                        selfVideoMessageViewHolder.llDownload.setVisibility(View.GONE);

                        String docType = "video";
                        if (selfVideoMessage.getOriginalMessageType() == FILE_MESSAGE) {
                            docType = selfVideoMessage.getDocumentType();
                        }

                        String fileName = selfVideoMessage.getFileName();


                        if (TextUtils.isEmpty(fileName)) {
                            String timeStamp = new SimpleDateFormat("ddMMyyyy_hhmmss", Locale.ENGLISH).format(new Date());
                            fileName = "Hippochat_" + timeStamp + ".jpg";
                        }

                        String fullPath = Util.getDirectoryPath(FOLDER_TYPE.get(docType));
                        File file = new File(fullPath);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        int downloadId = downloadFileFromUrl(fullPath, fileName, selfVideoMessage, position);

                        selfVideoMessage.setDownloadId(downloadId);
                    }
                });
                break;
            case HIPPO_VIDEO_MESSGAE_OTHER:
                final OtherVideoMessageViewHolder otherVideoMessageViewHolder = (OtherVideoMessageViewHolder) holder;
                final Message otherVideoMessage = fuguItems.get(position);

                otherVideoMessageViewHolder.progressBar.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());
                setTime(otherVideoMessageViewHolder.tvImageTime, otherVideoMessage.getSentAtUtc());
                if (!TextUtils.isEmpty(otherVideoMessage.getThumbnailUrl())) {
                    setImage(activity, otherVideoMessageViewHolder.ivImageMsg, otherVideoMessage.getThumbnailUrl());
                } else {
                    otherVideoMessageViewHolder.ivImageMsg.setVisibility(View.GONE);
                }
                setVideoDownloadStatus(otherVideoMessageViewHolder.llDownload, otherVideoMessageViewHolder.ivPlay, otherVideoMessageViewHolder.tvFileSize, otherVideoMessageViewHolder.progressBar, otherVideoMessage, position);
                setOtherSideView(position, otherVideoMessage, otherVideoMessageViewHolder.tvUserName,
                        otherVideoMessageViewHolder.llRoot, otherVideoMessageViewHolder.userImage);

                setIntegrationSource(otherVideoMessageViewHolder.messageSourceType, otherVideoMessageViewHolder.messageSourceType1, otherVideoMessage.getIntegrationSource());

                otherVideoMessageViewHolder.llDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fuguChatActivity.isNetworkAvailable()) {
                            if (!fuguChatActivity.checkPermission()) {
                                fuguChatActivity.readExternalStorage();
                                return;
                            }
                            otherVideoMessageViewHolder.progressBar.setVisibility(View.VISIBLE);
                            otherVideoMessageViewHolder.llDownload.setVisibility(View.GONE);

                            String docType = "video";
                            if (otherVideoMessage.getOriginalMessageType() == FILE_MESSAGE) {
                                docType = otherVideoMessage.getDocumentType();
                            }

                            String fileName = otherVideoMessage.getFileName();

                            if (TextUtils.isEmpty(fileName)) {
                                String timeStamp = new SimpleDateFormat("ddMMyyyy_hhmmss", Locale.ENGLISH).format(new Date());
                                fileName = "Hippochat_" + timeStamp + ".jpg";
                            }

                            String fullPath = Util.getDirectoryPath(FOLDER_TYPE.get(docType));
                            File file = new File(fullPath);
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            int downloadId = downloadFileFromUrl(fullPath, fileName, otherVideoMessage, position);

                            otherVideoMessage.setDownloadId(downloadId);
                        } else {
                            Toast.makeText(activity, Restring.getString(activity, R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            case HIPPO_IMAGE_MESSGAE_OTHER:
                final OtherVideoMessageViewHolder otherImageMessageViewHolder = (OtherVideoMessageViewHolder) holder;
                final Message otherImageMessage = fuguItems.get(position);

                otherImageMessageViewHolder.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());

                otherImageMessageViewHolder.progressBar.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());
                setTime(otherImageMessageViewHolder.tvImageTime, otherImageMessage.getSentAtUtc());
                if (!TextUtils.isEmpty(otherImageMessage.getThumbnailUrl())) {
                    showImageView(otherImageMessageViewHolder.ivImageMsg, otherImageMessage, false);
                    setImage(activity, otherImageMessageViewHolder.ivImageMsg, otherImageMessage.getThumbnailUrl());
                } else {
                    otherImageMessageViewHolder.ivImageMsg.setVisibility(View.GONE);
                }
                //setImageHeightAndWidth(otherImageMessageViewHolder.ivImageMsg, otherImageMessageViewHolder.rlImageMessage, otherImageMessageViewHolder.llImageMessage, otherImageMessage, true);

                otherImageMessageViewHolder.llDownload.setVisibility(View.GONE);
                otherImageMessageViewHolder.progressBar.setVisibility(View.GONE);
                otherImageMessageViewHolder.ivPlay.setVisibility(View.GONE);
                //setVideoDownloadStatus(otherImageMessageViewHolder.llDownload, otherImageMessageViewHolder.ivPlay, otherImageMessageViewHolder.tvFileSize, otherImageMessageViewHolder.progressBar, otherImageMessage, position);

                setOtherSideView(position, otherImageMessage, otherImageMessageViewHolder.tvUserName,
                        otherImageMessageViewHolder.llRoot, otherImageMessageViewHolder.userImage);

                otherImageMessageViewHolder.ivImageMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageDialog(activity, otherImageMessage.getUrl(), otherImageMessageViewHolder.ivImageMsg, otherImageMessage);
                    }
                });

                setIntegrationSource(otherImageMessageViewHolder.messageSourceType, otherImageMessageViewHolder.messageSourceType1, otherImageMessage.getIntegrationSource());

                break;
            case Hippo_IMAGE_MESSAGE_SELF:
                final SelfVideoMessageViewHolder selfImageMessageViewHolder = (SelfVideoMessageViewHolder) holder;
                final Message selfImageMessage = fuguItems.get(position);
                setSelfMessageBackground(selfImageMessage, selfImageMessageViewHolder.llRoot, selfImageMessageViewHolder.llImageMessage, position);

                selfImageMessageViewHolder.circle_progress.setBarColor(hippoColorConfig.getHippoThemeColorPrimary());

                setTime(selfImageMessageViewHolder.tvImageTime, selfImageMessage.getSentAtUtc());
                setMessageStatus(selfImageMessageViewHolder.ivMessageState, selfImageMessage.getMessageStatus(), true);
                if (!TextUtils.isEmpty(selfImageMessage.getThumbnailUrl())) {
                    showImageView(selfImageMessageViewHolder.ivImageMsg, selfImageMessage);
                } else {
                    selfImageMessageViewHolder.ivImageMsg.setVisibility(View.GONE);
                }
                //setImageHeightAndWidth(selfImageMessageViewHolder.ivImageMsg, selfImageMessageViewHolder.rlImageMessage, selfImageMessageViewHolder.llImageMessage, selfImageMessage, true);

                setIntegrationSource(selfImageMessageViewHolder.messageSourceType, selfImageMessageViewHolder.messageSourceType1, selfImageMessage.getIntegrationSource());

                final boolean isClickable = setVideoUiStatus(true, selfImageMessage, selfImageMessageViewHolder.llDownload, selfImageMessageViewHolder.ivPlay,
                        selfImageMessageViewHolder.btnRetry, selfImageMessageViewHolder.btnCancel, selfImageMessageViewHolder.circle_progress, true);

                selfImageMessageViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = selfImageMessageViewHolder.getAdapterPosition();
                        if (mOnRetry != null)
                            mOnRetry.onMessageCancel(selfImageMessage.getMuid(), pos);
                    }
                });

                selfImageMessageViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = selfImageMessageViewHolder.getAdapterPosition();
                        if (mOnRetry != null)
                            mOnRetry.onFileMessageRetry(selfImageMessage.getMuid(), pos);
                    }
                });

                selfImageMessageViewHolder.ivImageMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isClickable)
                            showImageDialog(activity, selfImageMessage.getUrl(), selfImageMessageViewHolder.ivImageMsg, selfImageMessage);
                    }
                });

                break;
            case FUGU_GALLERY_VIEW:
                int count = 4;
                final GalleryViewHolder viewHolder1 = (GalleryViewHolder) holder;
                setView(viewHolder1.llGalleryButtonLayout, count);
                break;
            case FUGU_TEXT_VIEW:
                final SimpleTextView textView = (SimpleTextView) holder;
                Message msg = fuguItems.get(position);
                textView.tvText.setText(msg.getMessage());
                textView.userImage.setVisibility(View.INVISIBLE);
                /*boolean hasTextUserImage = msg.isHasImageView();//isUserImageView(position, msg.getUserId());
                if (hasTextUserImage) {
                    textView.userImage.setVisibility(View.VISIBLE);

                    String imageUrl = msg.getUserImage();
                    if(TextUtils.isEmpty(imageUrl) || msg.getUserId().intValue() == 0) {
                        imageUrl = getBotImage();
                    }
                    Glide.with(activity).asBitmap()
                            .apply(getRequestOptions(msg.getfromName()))
                            .load(imageUrl)
                            .into(textView.userImage);
                }*/
                break;
            case FUGU_RATING_VIEW:
                final RatingViewHolder viewHolder = (RatingViewHolder) holder;
                final Message currentMessage = fuguItems.get(position);

                if (currentMessage.isRatingGiven()) {
                    viewHolder.ratingView1.setVisibility(View.GONE);
                    viewHolder.ratingView2.setVisibility(View.VISIBLE);

                    int totalStar = currentMessage.getTotalRating();
                    int givenStar = currentMessage.getRatingGiven();
                    viewHolder.selectedStar.setText(givenStar + "/" + totalStar);
                    if (!TextUtils.isEmpty(currentMessage.getComment())) {
                        viewHolder.comment.setVisibility(View.VISIBLE);
                        viewHolder.comment.setText(currentMessage.getComment());
                    } else {
                        viewHolder.comment.setVisibility(View.GONE);
                    }
                    viewHolder.thanks.setText(Restring.getString(activity, R.string.hippo_thanks_feedback));
                } else {
                    viewHolder.myCustomEditTextListener.updatePosition(currentMessage);
                    viewHolder.editText.setText(currentMessage.getComment());
                    viewHolder.ratingView1.setVisibility(View.VISIBLE);
                    viewHolder.ratingView2.setVisibility(View.GONE);
                    viewHolder.ratingView.setRating(5);
                    currentMessage.setRatingGiven(5);
                    viewHolder.sendBtn.setText(Restring.getString(activity, R.string.hippo_submit));
                    viewHolder.editText.setHint(Restring.getString(activity, R.string.hippo_writereview));
                }

                viewHolder.title.setText(Restring.getString(activity, R.string.hippo_rating_review));
                setRatingView(position, currentMessage, viewHolder.userName, viewHolder.rootView, viewHolder.userImage);
//                viewHolder.userImage.setVisibility(View.VISIBLE);
//                viewHolder.userName.setVisibility(View.VISIBLE);
//                viewHolder.userName.setText(currentMessage.getfromName());
//                loadUserImage(viewHolder.userImage, currentMessage.getfromName(), currentMessage.getUserImage());

                viewHolder.ratingView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        int rate = (int) rating;
                        currentMessage.setRatingGiven(rate);
                        //onRatingListener.onRatingSelected(rate, currentMessage);
                    }
                });

                viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRatingListener.onSubmitRating(viewHolder.editText.getText().toString(), currentMessage, position);
                        viewHolder.editText.setText("");
                    }
                });
                break;

            case FUGU_QUICK_REPLY_VIEW:
                final QuickReplyViewHolder qrViewHolder = (QuickReplyViewHolder) holder;
                Message currentFormMsg = fuguItems.get(position);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                qrViewHolder.list_qr.setLayoutManager(layoutManager);
                HippoQuickReplayAdapter replayAdapter = new HippoQuickReplayAdapter(currentFormMsg, this, qrViewHolder);
                qrViewHolder.list_qr.setAdapter(replayAdapter);
                break;
            case HIPPO_AGENT_LIST_VIEW:
                final RecyclerViewHolder agentViewHolder = (RecyclerViewHolder) holder;
                final Message agentMsg = fuguItems.get(position);
                agentViewHolder.listView.setVisibility(View.GONE);
                agentViewHolder.cardLayout.setVisibility(View.GONE);
                agentViewHolder.fallbackLayout.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(agentMsg.getFallbackText()))
                    agentViewHolder.tvAssignment.setText(agentMsg.getFallbackText());
                else
                    agentViewHolder.tvAssignment.setText(Restring.getString(activity, R.string.hippo_all_agents_busy));


                if (!TextUtils.isEmpty(agentMsg.getSelectedAgentId())) {
                    agentViewHolder.listView.setVisibility(View.GONE);
                    agentViewHolder.fallbackLayout.setVisibility(View.GONE);
                    agentViewHolder.cardLayout.setVisibility(View.VISIBLE);

                    int posi = 0;
                    for (int a = 0; a < agentMsg.getContentValue().size(); a++) {
                        if (agentMsg.getSelectedAgentId().equalsIgnoreCase(agentMsg.getContentValue().get(a).getCardId())) {
                            posi = a;
                            break;
                        }
                    }

                    final int selectedPos = posi;

                    agentViewHolder.agentName.setText(agentMsg.getContentValue().get(posi).getTitle());
                    agentViewHolder.userSubCategory.setText(agentMsg.getContentValue().get(posi).getDescription());
                    //agentViewHolder.userSubCategory.setShowingLine(2);
                    RequestOptions myOptions = RequestOptions
                            .bitmapTransform(new RoundedCornersTransformation(activity, 2, 1))
                            .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                            .fitCenter()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                    Glide.with(activity).load(agentMsg.getContentValue().get(posi).getImageUrl())
                            .apply(myOptions)
                            .into(agentViewHolder.userImageView);

                    try {
                        if (!TextUtils.isEmpty(agentMsg.getContentValue().get(posi).getRatingValue())) {
                            agentViewHolder.starLayout.setVisibility(View.VISIBLE);
                            agentViewHolder.starText.setText("" + agentMsg.getContentValue().get(posi).getRatingValue());
                        } else {
                            agentViewHolder.starLayout.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        agentViewHolder.starLayout.setVisibility(View.GONE);
                    }

                    agentViewHolder.userSubCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (qrCallback != null)
                                qrCallback.onProfileClicked(agentMsg, agentMsg.getSelectedAgentId(), selectedPos);
                        }
                    });

                    agentViewHolder.infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (qrCallback != null)
                                qrCallback.onProfileClicked(agentMsg, agentMsg.getSelectedAgentId(), selectedPos);
                        }
                    });

                } else if (agentMsg.getContentValue() == null || agentMsg.getContentValue().size() == 0) {
                    agentViewHolder.listView.setVisibility(View.GONE);
                    agentViewHolder.cardLayout.setVisibility(View.GONE);
                    agentViewHolder.fallbackLayout.setVisibility(View.VISIBLE);

                    agentViewHolder.tvAssignment.setText(agentMsg.getFallbackText());

                } else {
                    agentViewHolder.listView.setVisibility(View.VISIBLE);
                    agentViewHolder.cardLayout.setVisibility(View.GONE);
                    agentViewHolder.fallbackLayout.setVisibility(View.GONE);
                    LinearLayoutManager agentLayoutManager = new LinearLayoutManager(activity);
                    agentLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    agentViewHolder.listView.setLayoutManager(agentLayoutManager);
                    AgentSnapAdapter agentAdapter = new AgentSnapAdapter(activity, agentMsg, this);
                    agentViewHolder.listView.setAdapter(agentAdapter);
                    ((SimpleItemAnimator) agentViewHolder.listView.getItemAnimator()).setSupportsChangeAnimations(false);
                    SnapHelper startSnapHelper = new StartSnapHelper();
                    agentViewHolder.listView.setOnFlingListener(null);
                    startSnapHelper.attachToRecyclerView(agentViewHolder.listView);
                    agentViewHolder.listView.setHorizontalScrollBarEnabled(false);
                }
                break;
            case FUGU_FORUM_VIEW:

                Message currentFormDataMsg = fuguItems.get(position);
                final ForumViewHolder forumViewHolder = (ForumViewHolder) holder;
                LinearLayoutManager mlayoutManager = new LinearLayoutManager(activity);
                forumViewHolder.rvDataForm.setLayoutManager(mlayoutManager);
                DataFormAdapter dataFormAdapter = new DataFormAdapter(currentFormDataMsg, this, fragmentManager);
                forumViewHolder.rvDataForm.setNestedScrollingEnabled(true);
                forumViewHolder.rvDataForm.setAdapter(dataFormAdapter);

                setOtherSideView(position, currentFormDataMsg, forumViewHolder.tvUserName, forumViewHolder.llRoot, forumViewHolder.userImage);

                break;
            case HIPPO_NEW_LEAD_FORM_TICKET:


                Message currentFormTicketDataMsg = fuguItems.get(position);
                final ForumTicketViewHolder forumTicketViewHolder = (ForumTicketViewHolder) holder;
                LinearLayoutManager mlayoutManagerTicket = new LinearLayoutManager(activity);
                forumTicketViewHolder.rvDataForm.setLayoutManager(mlayoutManagerTicket);
                DataFormTicketAdapter dataFormAdapterTicket = new DataFormTicketAdapter(currentFormTicketDataMsg, this, fragmentManager, activity);
                forumTicketViewHolder.rvDataForm.setNestedScrollingEnabled(true);
                forumTicketViewHolder.rvDataForm.setAdapter(dataFormAdapterTicket);

                setOtherSideView(position, currentFormTicketDataMsg, forumTicketViewHolder.tvUserName, forumTicketViewHolder.llRoot, forumTicketViewHolder.userImage);

                break;
            case FUGU_TYPE_HEADER:
                final DateViewHolder dateViewHolder = (DateViewHolder) holder;
                Message headerItem = fuguItems.get(position);
                if (TextUtils.isEmpty(headerItem.getDate())) {
                    dateViewHolder.tvDate.setVisibility(View.GONE);
                } else {
                    String date = DateUtils.getInstance().getDate(headerItem.getDate());
                    if (date.equalsIgnoreCase("Today")) {
                        date = Restring.getString(activity, R.string.hippo_today);
                    } else if (date.equalsIgnoreCase("Yesterday")) {
                        date = Restring.getString(activity, R.string.hippo_yesterday);
                    }
                    dateViewHolder.tvDate.setText(date);
                    dateViewHolder.tvDate.setVisibility(View.VISIBLE);
                }

                GradientDrawable drawable = (GradientDrawable) dateViewHolder.tvDate.getBackground();
                drawable.setStroke((int) activity.getResources().getDimension(R.dimen.fugu_border_width), hippoColorConfig.getHippoBorderColor()); // set stroke width and stroke color

                dateViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChatDateText());
                break;
            case FUGU_ITEM_TYPE_OTHER:
                final OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
                final Message currentOrderItem = fuguItems.get(position);
                otherMessageViewHolder.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());
                otherMessageViewHolder.tvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
                otherMessageViewHolder.tvMsg.setLinkTextColor(hippoColorConfig.getHippoUrlLinkText());
                otherMessageViewHolder.tvMsg.setAutoLinkMask(Linkify.ALL);
                otherMessageViewHolder.tvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());

                setIntegrationSource(otherMessageViewHolder.messageSourceType, otherMessageViewHolder.messageSourceType1, currentOrderItem.getIntegrationSource());

                if (TextUtils.isEmpty(currentOrderItem.getMessage())) {
                    otherMessageViewHolder.tvMsg.setVisibility(View.INVISIBLE);
                } else {
                    String messageStr = currentOrderItem.getMessage();//.replace(" ", "&nbsp;");
                    if (currentOrderItem.getMessageState() == 5) {
                        String editStr = Restring.getString(activity, R.string.hippo_edited);
                        messageStr = messageStr + " <font  color='grey'><small> (" + editStr + ")</small></font>";
                    } else if (currentOrderItem.getMessageState() == 4) {
                        String name = currentOrderItem.getfromName();
                        String deletedStr = Restring.getString(activity, R.string.hippo_message_deleted);
                        messageStr = "<font  color='grey'><i>" + name + " " + deletedStr + " </i></font>";
                    }

//                    otherMessageViewHolder.tvMsg.setText(Html.fromHtml(messageStr.replace("\n", "<br /> ")));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        otherMessageViewHolder.tvMsg.setText(Html.fromHtml(Html.fromHtml(messageStr.replace("\n", "<br /> "), Html.FROM_HTML_MODE_LEGACY).toString()));
                    } else
                        otherMessageViewHolder.tvMsg.setText(Html.fromHtml(Html.fromHtml(messageStr.replace("\n", "<br /> ")).toString()));

                    otherMessageViewHolder.tvMsg.setVisibility(View.VISIBLE);
                }

                otherMessageViewHolder.tvMsg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(otherMessageViewHolder.tvMsg.getText());
                        String text = Restring.getString(activity, R.string.hippo_copy_to_clipboard);
                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });


                String userNameText = "";
                if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                    userNameText = currentOrderItem.getfromName();
                } else {
                    userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : Restring.getString(activity, R.string.fugu_support);
                }

                setTextMessageOther(position, currentOrderItem, otherMessageViewHolder.llMessageBg, otherMessageViewHolder.tvUserName,
                        otherMessageViewHolder.llRoot, null, otherMessageViewHolder.ivMsgImage,
                        userNameText, otherMessageViewHolder.userImage, chatType);

                if (TextUtils.isEmpty(currentOrderItem.getSentAtUtc())) {
                    otherMessageViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    otherMessageViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem.getSentAtUtc())));
                    otherMessageViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
                if (otherMessageViewHolder.tvUserName.length() > (otherMessageViewHolder.tvMsg.length() + otherMessageViewHolder.tvTime.length())
                        && otherMessageViewHolder.tvUserName.getVisibility() == View.VISIBLE) {
                    int length;
                    switch (otherMessageViewHolder.tvMsg.length()) {
                        case 1:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() + 2;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        case 2:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length();
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        case 3:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        default:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                    }

                }
//                NinePatchDrawable drawable2 = (NinePatchDrawable) otherMessageViewHolder.llMessageBg.getBackground();
//                drawable2.setColorFilter(hippoColorConfig.getHippoBgMessageFrom(), PorterDuff.Mode.MULTIPLY);
                if (!TextUtils.isEmpty(currentOrderItem.getThumbnailUrl())) {
                    new RequestOptions();
                    RequestOptions myOptions = RequestOptions
                            .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                            .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                    Glide.with(activity).load(currentOrderItem.getThumbnailUrl())
                            .apply(myOptions)
                            .into(otherMessageViewHolder.ivMsgImage);
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.GONE);
                }

                otherMessageViewHolder.rlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImageDialog(activity, currentOrderItem.getUrl(), otherMessageViewHolder.ivMsgImage, currentOrderItem);
                    }
                });

                if (currentOrderItem.getOriginalMessageType() == FILE_MESSAGE) {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileName.setText(currentOrderItem.getFileName());

                    otherMessageViewHolder.ivDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.GONE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.VISIBLE);
                        }
                    });

                    otherMessageViewHolder.rlStopDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.GONE);
                            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                            if (manager != null) {
                                manager.remove(currentOrderItem.getDownloadId());
                            }

                        }
                    });

                    otherMessageViewHolder.llFileDetails.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileSize.setText(currentOrderItem.getFileSize());
                    otherMessageViewHolder.tvExtension.setText(currentOrderItem.getFileExtension());
                } else {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.GONE);
                }


                if (currentOrderItem.getOriginalMessageType() == ACTION_MESSAGE || currentOrderItem.getOriginalMessageType() == ACTION_MESSAGE_NEW) {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) otherMessageViewHolder
                            .rlCustomAction.getLayoutParams();

                    // increase left margin if background is chat_bg_left
                    if (otherMessageViewHolder.llMessageBg.getBackground().getConstantState() == ContextCompat.getDrawable(activity, R.drawable.hippo_chat_bg_left).getConstantState()) {
                        layoutParams.setMargins(pxToDp(13), pxToDp(10), pxToDp(10), pxToDp(10));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            layoutParams.setMarginStart(pxToDp(13));
                            layoutParams.setMarginEnd(pxToDp(10));
                        }
                    } else {
                        layoutParams.setMargins(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));

                    }

                    CustomAction customAction = currentOrderItem.getCustomAction();
                    if (customAction != null) {
                        // title
                        if (customAction.getTitle() != null && !TextUtils.isEmpty(customAction.getTitle())) {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitle.setText(customAction.getTitle());
                        } else {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.GONE);
                        }

                        // title description
                        if (customAction.getTitleDescription() != null && !TextUtils.isEmpty(customAction.getTitleDescription())) {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitleDescription.setText(customAction.getTitleDescription());
                        } else {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.GONE);
                        }

                        // image
                        if (customAction.getImageUrl() != null && !TextUtils.isEmpty(customAction.getImageUrl())) {
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_bottom);
                            otherMessageViewHolder.ivActionImage.setVisibility(View.VISIBLE);
                            RequestOptions myOptions = RequestOptions
                                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                            Glide.with(activity).load(customAction.getImageUrl())
                                    .apply(myOptions)
                                    .into(otherMessageViewHolder.ivActionImage);
                        } else {
                            otherMessageViewHolder.ivActionImage.setVisibility(View.GONE);
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_all_sides);
                        }
                        // description
                        if (customAction.getDescriptionObjects() != null && customAction.getDescriptionObjects().size() != 0) {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionDescription.setLayoutManager(new LinearLayoutManager(activity));
                            otherMessageViewHolder.rvActionDescription.setNestedScrollingEnabled(false);
                            otherMessageViewHolder.rvActionDescription.setAdapter(new CustomActionDescriptionAdapter(activity,
                                    customAction.getDescriptionObjects()));
                        } else {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.GONE);
                        }
                        // buttons
                        if (customAction.getActionButtons() != null && customAction.getActionButtons().size() != 0) {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setNestedScrollingEnabled(false);

                            // set span size of grid
                            int span = 2;
                            int size = customAction.getActionButtons().size();
                            if (size == 1) {
                                span = 1;
                            } else if (size % 3 == 0) {
                                span = 3;
                            } else {
                                span = 2;
                            }

                            otherMessageViewHolder.rvActionButtons.setLayoutManager(new GridLayoutManager(activity, span));
                            otherMessageViewHolder.rvActionButtons.addItemDecoration(new GridDividerItemDecoration(activity));

                            boolean disAbleClick = false;
                            if (currentOrderItem.getUserId().compareTo(CommonData.getUpdatedDetails().getData().getUserId()) == 0) {
                                disAbleClick = true;
                            }

                            otherMessageViewHolder.rvActionButtons.setAdapter(new CustomActionButtonsAdapter(activity,
                                    customAction.getActionButtons(), disAbleClick));
                        } else {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.GONE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.GONE);
                        }
                    }
                } else {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.GONE);
                }


                break;
            case FUGU_ITEM_TYPE_SELF:
                final SelfMessageViewHolder selfMessageViewHolder = (SelfMessageViewHolder) holder;
                final Message currentOrderItem2 = fuguItems.get(position);

                setTextMessageSelf(isRightToLeft, position, selfMessageViewHolder.fuguLlRoot, selfMessageViewHolder.fuguRlImageMessage,
                        selfMessageViewHolder.fuguRlMessages, selfMessageViewHolder.FuguLlMessageBg, selfMessageViewHolder.fuguTvMsg,
                        selfMessageViewHolder.fuguTvTime, currentOrderItem2);

                setIntegrationSource(selfMessageViewHolder.messageSourceType, selfMessageViewHolder.messageSourceType1, currentOrderItem2.getIntegrationSource());

                /*if (TextUtils.isEmpty(currentOrderItem2.getMessage())) {
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params2);
                    //selfMessageViewHolder.fuguTvMsg.setTextSize(pxToDp(3));
                } else {
                    String messageStr = currentOrderItem2.getMessage();//.replaceAll(" ", "&nbsp;");
                    setTextMessage(selfMessageViewHolder.fuguTvMsg, messageStr);
                    //selfMessageViewHolder.fuguTvMsg.setText(Html.fromHtml(currentOrderItem2.getMessage().replace("\n", "<br /> ")));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params);
                    //selfMessageViewHolder.fuguTvMsg.setTextSize(17);
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.VISIBLE);
                }*/

                String messageString = currentOrderItem2.getMessage();//.replaceAll(" ", "&nbsp;");
                if (currentOrderItem2.getMessageState() == 5) {
                    String editStr = Restring.getString(activity, R.string.hippo_edited);
                    messageString = messageString + " <font  color='grey'><small> (" + editStr + ")</small></font>";
                } else if (currentOrderItem2.getMessageState() == 4) {
                    String you = Restring.getString(activity, R.string.hippo_you);
                    String deletedStr = Restring.getString(activity, R.string.hippo_message_deleted);
                    messageString = "<font  color='grey'><i>" + you + " " + deletedStr + " </i></font>";
                }
                //selfMessageViewHolder.fuguTvMsg.setText(messageString);
                setTextMessage(selfMessageViewHolder.fuguTvMsg, messageString);

//                selfMessageViewHolder.fuguTvMsg.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//                        cm.setText(selfMessageViewHolder.fuguTvMsg.getText());
//                        String text = Restring.getString(activity, R.string.hippo_copy_to_clipboard);
//                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });

                if (TextUtils.isEmpty(currentOrderItem2.getSentAtUtc())) {
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguTvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem2.getSentAtUtc())));
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(currentOrderItem2.getThumbnailUrl()) || !TextUtils.isEmpty(currentOrderItem2.getLocalImagePath())) {
                    showImageView(selfMessageViewHolder.fuguIvMsgImage, currentOrderItem2);

                    if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT
                            || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY
                            || currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY
                                || currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                            selfMessageViewHolder.btnRetry.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.btnCancel.setVisibility(View.VISIBLE);
                        } else {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.btnRetry.setVisibility(View.GONE);
                            selfMessageViewHolder.btnCancel.setVisibility(View.GONE);
                        }
                    } else {
                        selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                        selfMessageViewHolder.btnRetry.setVisibility(View.GONE);
                        selfMessageViewHolder.btnCancel.setVisibility(View.GONE);
                    }
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.GONE);
                }
                selfMessageViewHolder.fuguRlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT
                                || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY
                                || currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                            return;
                        }
                        showImageDialog(activity, currentOrderItem2.getUrl(), selfMessageViewHolder.fuguIvMsgImage, currentOrderItem2);
                    }
                });

                if (currentOrderItem2.getIsMessageExpired() == 1 && currentOrderItem2.getOriginalMessageType() != 10) {
                    selfMessageViewHolder.llRetry.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.tvTryAgain.setTag(position);
                    selfMessageViewHolder.tvCancel.setTag(position);
                    selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                    selfMessageViewHolder.tvTryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mOnRetry != null) {
                                String muid = currentOrderItem2.getMuid();
                                int pos = selfMessageViewHolder.getAdapterPosition();
                                if (TextUtils.isEmpty(currentOrderItem2.getThumbnailUrl())) {
                                    mOnRetry.onMessageRetry(muid, pos);
                                } else {
                                    mOnRetry.onFileMessageRetry(muid, pos);
                                }

                            }
                        }
                    });

                    selfMessageViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //int pos = (int) view.getTag();
                            int pos = selfMessageViewHolder.getAdapterPosition();
                            if (mOnRetry != null) {
                                String muid = currentOrderItem2.getMuid();
                                mOnRetry.onMessageCancel(muid, pos);
                            }
                        }
                    });
                } else {
                    selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                }

                switch (currentOrderItem2.getMessageStatus()) {
                    case MESSAGE_UNSENT:
                    case MESSAGE_IMAGE_RETRY:
                    case MESSAGE_FILE_RETRY:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);

                        break;
                    case MESSAGE_READ:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoMessageRead(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_SENT:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_DELIVERED:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        if (Build.VERSION.SDK_INT >= 21) {
                            selfMessageViewHolder.fuguIvMessageState.getDrawable().setTint(ContextCompat.getColor(activity, R.color.fugu_drawable_color));
                        }
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                    default:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                }

                selfMessageViewHolder.fuguBtnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnRetry != null) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                            mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                    IMAGE_MESSAGE, null, currentOrderItem2.getMuid());
                        }
                    }
                });

                selfMessageViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnRetry != null) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.btnRetry.setVisibility(View.GONE);
                            String muid = currentOrderItem2.getMuid();
                            int pos = selfMessageViewHolder.getAdapterPosition();
                            mOnRetry.onFileMessageRetry(muid, pos);
                        }
                    }
                });

                selfMessageViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnRetry != null) {
                            String muid = currentOrderItem2.getMuid();
                            int pos = selfMessageViewHolder.getAdapterPosition();
                            mOnRetry.onMessageCancel(muid, pos);
                        }
                    }
                });

                if (currentOrderItem2.getOriginalMessageType() == FILE_MESSAGE) {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileName.setText(currentOrderItem2.getFileName());

                    selfMessageViewHolder.fuguIvUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mOnRetry != null) {

                                FuguFileDetails fileDetails = new FuguFileDetails();
                                fileDetails.setFilePath(currentOrderItem2.getFilePath());
                                fileDetails.setFileExtension(currentOrderItem2.getFileExtension());
                                fileDetails.setFileSize(currentOrderItem2.getFileSize());
                                fileDetails.setFileName(currentOrderItem2.getFileName());

                                selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                                selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                                mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                        FILE_MESSAGE, fileDetails, currentOrderItem2.getMuid());
                            }
                        }
                    });

                    selfMessageViewHolder.fuguLlFileDetails.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileSize.setText(currentOrderItem2.getFileSize());
                    selfMessageViewHolder.fuguTvExtension.setText(currentOrderItem2.getFileExtension());
                } else {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.GONE);
                }

                if (currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.VISIBLE);
                } else if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                }

                selfMessageViewHolder.fuguLlFileRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Check and ask for Permissions
                        String text = Restring.getString(activity, R.string.hippo_storage_permission);
                        if (!HippoConfig.getInstance().askUserToGrantPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, text,
                                PERMISSION_READ_FILE)) return;

//                        FuguLog.e("adapter file path", currentOrderItem2.getFilePath());

                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(currentOrderItem2.getFilePath());
                            photoPickerIntent.setData(Uri.fromFile(file));
                            activity.startActivity(photoPickerIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity.getApplicationContext(),
                                    Restring.getString(activity, R.string.fugu_file_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (currentOrderItem2.getOriginalMessageType() == ACTION_MESSAGE || currentOrderItem2.getOriginalMessageType() == ACTION_MESSAGE_NEW) {
                    selfMessageViewHolder.rlCustomAction.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selfMessageViewHolder
                            .rlCustomAction.getLayoutParams();

                    // increase left margin if background is chat_bg_left
                    if (selfMessageViewHolder.FuguLlMessageBg.getBackground().getConstantState() == ContextCompat.getDrawable(activity, R.drawable.hippo_chat_bg_left).getConstantState()) {
                        layoutParams.setMargins(pxToDp(13), pxToDp(10), pxToDp(10), pxToDp(10));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            layoutParams.setMarginStart(pxToDp(13));
                            layoutParams.setMarginEnd(pxToDp(10));
                        }
                    } else {
                        layoutParams.setMargins(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));

                    }

                    CustomAction customAction = currentOrderItem2.getCustomAction();
                    if (customAction != null) {
                        // title
                        if (customAction.getTitle() != null && !TextUtils.isEmpty(customAction.getTitle())) {
                            selfMessageViewHolder.tvActionTitle.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.tvActionTitle.setText(customAction.getTitle());
                        } else {
                            selfMessageViewHolder.tvActionTitle.setVisibility(View.GONE);
                        }

                        // title description
                        if (customAction.getTitleDescription() != null && !TextUtils.isEmpty(customAction.getTitleDescription())) {
                            selfMessageViewHolder.tvActionTitleDescription.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.tvActionTitleDescription.setText(customAction.getTitleDescription());
                        } else {
                            selfMessageViewHolder.tvActionTitleDescription.setVisibility(View.GONE);
                        }

                        // image
                        if (customAction.getImageUrl() != null && !TextUtils.isEmpty(customAction.getImageUrl())) {
                            selfMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_bottom);
                            selfMessageViewHolder.ivActionImage.setVisibility(View.VISIBLE);
                            RequestOptions myOptions = RequestOptions
                                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                            Glide.with(activity).load(customAction.getImageUrl())
                                    .apply(myOptions)
                                    .into(selfMessageViewHolder.ivActionImage);
                        } else {
                            selfMessageViewHolder.ivActionImage.setVisibility(View.GONE);
                            selfMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_all_sides);
                        }
                        // description
                        if (customAction.getDescriptionObjects() != null && customAction.getDescriptionObjects().size() != 0) {
                            selfMessageViewHolder.rvActionDescription.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.rvActionDescription.setLayoutManager(new LinearLayoutManager(activity));
                            selfMessageViewHolder.rvActionDescription.setNestedScrollingEnabled(false);
                            selfMessageViewHolder.rvActionDescription.setAdapter(new CustomActionDescriptionAdapter(activity,
                                    customAction.getDescriptionObjects()));
                        } else {
                            selfMessageViewHolder.rvActionDescription.setVisibility(View.GONE);
                        }
                        // buttons
                        if (customAction.getActionButtons() != null && customAction.getActionButtons().size() != 0) {
                            selfMessageViewHolder.vwActionButtonDivider.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.rvActionButtons.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.rvActionButtons.setNestedScrollingEnabled(false);

                            // set span size of grid
                            int span = 2;
                            int size = customAction.getActionButtons().size();
                            if (size == 1) {
                                span = 1;
                            } else if (size % 3 == 0) {
                                span = 3;
                            } else {
                                span = 2;
                            }

                            boolean disAbleClick = false;
                            if (currentOrderItem2.getUserId().compareTo(CommonData.getUpdatedDetails().getData().getUserId()) == 0) {
                                disAbleClick = true;
                            }

                            selfMessageViewHolder.rvActionButtons.setLayoutManager(new GridLayoutManager(activity, span));
                            selfMessageViewHolder.rvActionButtons.addItemDecoration(new GridDividerItemDecoration(activity));
                            selfMessageViewHolder.rvActionButtons.setAdapter(new CustomActionButtonsAdapter(activity,
                                    customAction.getActionButtons(), true));
                        } else {
                            selfMessageViewHolder.vwActionButtonDivider.setVisibility(View.GONE);
                            selfMessageViewHolder.rvActionButtons.setVisibility(View.GONE);
                        }
                    }
                } else {
                    selfMessageViewHolder.rlCustomAction.setVisibility(View.GONE);
                }
                break;
            case HIPPO_UNKNOWN_MESSAGE_OTHER:
                final OtherMessageViewHolder otherUnknownView = (OtherMessageViewHolder) holder;
                final Message unknownItemOther = fuguItems.get(position);
                otherUnknownView.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());
                otherUnknownView.tvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
                otherUnknownView.tvMsg.setLinkTextColor(hippoColorConfig.getHippoUrlLinkText());
                otherUnknownView.tvMsg.setAutoLinkMask(Linkify.ALL);
                otherUnknownView.tvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());

                otherUnknownView.tvMsg.setText(CommonData.getUserDetails().getData().getUnsupportedMessage());
                //otherUnknownView.tvMsg.setTextSize(17);
                otherUnknownView.tvMsg.setVisibility(View.VISIBLE);

                String userNameText1 = "";
                if (!TextUtils.isEmpty(unknownItemOther.getfromName())) {
                    userNameText1 = unknownItemOther.getfromName();
                } else {
                    userNameText1 = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : Restring.getString(activity, R.string.fugu_support);

                }

                setTextMessageOther(position, unknownItemOther, otherUnknownView.llMessageBg, otherUnknownView.tvUserName,
                        otherUnknownView.llRoot, otherUnknownView.tvMsg, otherUnknownView.ivMsgImage,
                        userNameText1, otherUnknownView.userImage, chatType);


                break;
            case HIPPO_UNKNOWN_MESSAGE_SELF:
                final SelfMessageViewHolder selfUnknownView = (SelfMessageViewHolder) holder;
                final Message unknownItemSelf = fuguItems.get(position);

                setTextMessageSelf(isRightToLeft, position, selfUnknownView.fuguLlRoot, selfUnknownView.fuguRlImageMessage,
                        selfUnknownView.fuguRlMessages, selfUnknownView.FuguLlMessageBg, selfUnknownView.fuguTvMsg,
                        selfUnknownView.fuguTvTime, unknownItemSelf);

                selfUnknownView.fuguTvMsg.setText(CommonData.getUserDetails().getData().getUnsupportedMessage());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                selfUnknownView.fuguRlMessages.setLayoutParams(params);
                //selfUnknownView.fuguTvMsg.setTextSize(17);
                selfUnknownView.fuguTvMsg.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(unknownItemSelf.getSentAtUtc())) {
                    selfUnknownView.fuguTvTime.setVisibility(View.GONE);
                } else {
                    selfUnknownView.fuguTvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(unknownItemSelf.getSentAtUtc())));
                    selfUnknownView.fuguTvTime.setVisibility(View.VISIBLE);
                }

                break;
            case HIPPO_USER_CONCENT_VIEW:
                final UserConcentViewHolder concentViewHolder = (UserConcentViewHolder) holder;
                final Message userConcentMsg = fuguItems.get(position);
                concentViewHolder.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());


                String userNameText2 = "";
                if (!TextUtils.isEmpty(userConcentMsg.getfromName())) {
                    userNameText2 = userConcentMsg.getfromName();
                } else {
                    userNameText2 = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : Restring.getString(activity, R.string.fugu_support);
                }

                setTextMessageOtherBot(position, userConcentMsg, concentViewHolder.llMessageBgLeft, concentViewHolder.tvUserName,
                        concentViewHolder.root_right, concentViewHolder.tvMsg, userNameText2, concentViewHolder.userImage, chatType);
                String messageStr = userConcentMsg.getMessage();//.replace(" ", "&nbsp;");
                concentViewHolder.tvMsg.setText(Html.fromHtml(messageStr.replace("\n", "<br /> ")));
                concentViewHolder.tvUserName.setText(userNameText2);
                if (TextUtils.isEmpty(userConcentMsg.getSentAtUtc())) {
                    concentViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    concentViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(userConcentMsg.getSentAtUtc())));
                    concentViewHolder.tvTime.setVisibility(View.VISIBLE);
                }

                concentViewHolder.tvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
                concentViewHolder.tvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());

                String selectedBtnId = "";
                if (!TextUtils.isEmpty(userConcentMsg.getSelectedBtnId()))
                    selectedBtnId = userConcentMsg.getSelectedBtnId();


                if (TextUtils.isEmpty(selectedBtnId)) {
                    concentViewHolder.recyclerView.setVisibility(View.VISIBLE);
                    concentViewHolder.tvTagTime.setVisibility(View.VISIBLE);
                    concentViewHolder.llRoot.setVisibility(View.GONE);

                    if (TextUtils.isEmpty(userConcentMsg.getSentAtUtc())) {
                        concentViewHolder.tvTagTime.setVisibility(View.GONE);
                    } else {
                        concentViewHolder.tvTagTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(userConcentMsg.getSentAtUtc())));
                        concentViewHolder.tvTagTime.setVisibility(View.VISIBLE);
                    }

                    TagsAdapter tagsAdapter = new TagsAdapter((ArrayList<ContentValue>) userConcentMsg.getContentValue(),
                            hippoColorConfig, new TagsAdapter.OnTagClicked() {
                        @Override
                        public void onClick(int position, ContentValue contentValue) {
                            String btnId = contentValue.getBtnId();
                            String actionId = contentValue.getButtonActionType();
                            userConcentMsg.setSelectedBtnId(btnId);
                            HippoLog.e(TAG, "userConcentMsg = " + new Gson().toJson(userConcentMsg));
                            if (!TextUtils.isEmpty(userConcentMsg.getContentValue().get(position).getButtonType())
                                    && Integer.parseInt(userConcentMsg.getContentValue().get(position).getButtonType()) == 2) {
                                String url = "";
                                if (actionId.equalsIgnoreCase(ACTION.OPEN_URL)) {
                                    try {
                                        Object obj = userConcentMsg.getContentValue().get(position).getButtonActionJson();
                                        ButtonActionJson actionJson = (ButtonActionJson) obj;
                                        url = actionJson.getUrl();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                onUserConcent(position, btnId, userConcentMsg, actionId, url);
                            } else {
                                onUserConcent(position, btnId, userConcentMsg, "", "");
                            }
                        }
                    });
                    FlexboxLayoutManager flayoutManager = new FlexboxLayoutManager(activity);
                    flayoutManager.setFlexDirection(FlexDirection.ROW);
                    flayoutManager.setJustifyContent(JustifyContent.FLEX_END);
                    concentViewHolder.recyclerView.setLayoutManager(flayoutManager);
                    concentViewHolder.recyclerView.setAdapter(tagsAdapter);

                } else {
                    concentViewHolder.recyclerView.setVisibility(View.GONE);
                    concentViewHolder.tvTagTime.setVisibility(View.GONE);
                    concentViewHolder.llRoot.setVisibility(View.VISIBLE);

                    concentViewHolder.tvMsgLeft.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());
                    concentViewHolder.tvTimeLeft.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());

                    int selectedPosition = 0;
                    for (int i = 0; i < userConcentMsg.getContentValue().size(); i++) {
                        if (selectedBtnId.equalsIgnoreCase(userConcentMsg.getContentValue().get(i).getBtnId())) {
                            selectedPosition = i;
                            break;
                        }
                    }

                    concentViewHolder.tvMsgLeft.setText(userConcentMsg.getContentValue().get(selectedPosition).getBtnTitle());
                    if (TextUtils.isEmpty(userConcentMsg.getSentAtUtc())) {
                        concentViewHolder.tvTimeLeft.setVisibility(View.GONE);
                    } else {
                        concentViewHolder.tvTimeLeft.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(userConcentMsg.getSentAtUtc())));
                        concentViewHolder.tvTimeLeft.setVisibility(View.VISIBLE);
                    }
                    concentViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                    concentViewHolder.ivMessageState.getDrawable().setColorFilter(hippoColorConfig.getHippoMessageRead(), PorterDuff.Mode.SRC_ATOP);

                }

                break;

            case HIPPO_AGENT_PAYMENT_VIEW:
                final PaymentView paymentView = (PaymentView) holder;
                Message paymentMsg = fuguItems.get(position);
                LinearLayoutManager paymentLayoutManager = new LinearLayoutManager(activity);

                paymentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                paymentView.recyclerView.setLayoutManager(paymentLayoutManager);

                PaymentAdapter paymentAdapter = null;

                if (paymentMsg.getCustomAction() != null) {
                    if (!TextUtils.isEmpty(paymentMsg.getCustomAction().getSelectedId())) {
                        paymentView.payBtn.setVisibility(View.GONE);
                        paymentView.secureLayout.setVisibility(View.GONE);
                        paymentView.selectPlan.setVisibility(View.GONE);
                        ArrayList<HippoPayment> payments = new ArrayList<>();
                        for (int i = 0; i < paymentMsg.getCustomAction().getHippoPayment().size(); i++) {
                            if (paymentMsg.getCustomAction().getHippoPayment().get(i).getId().equalsIgnoreCase(paymentMsg.getCustomAction().getSelectedId())) {
                                payments.add(paymentMsg.getCustomAction().getHippoPayment().get(i));
                            }
                        }
                        paymentAdapter = new PaymentAdapter(paymentMsg, payments,
                                this, hippoColorConfig, paymentMsg.getCustomAction().getSelectedId(), position);
                    } else {
                        paymentView.payBtn.setVisibility(View.VISIBLE);
                        paymentView.secureLayout.setVisibility(View.VISIBLE);
                        paymentView.selectPlan.setVisibility(View.VISIBLE);
                        paymentAdapter = new PaymentAdapter(paymentMsg, paymentMsg.getCustomAction().getHippoPayment(),
                                this, hippoColorConfig, paymentMsg.getCustomAction().getSelectedId(), position);
                        String text = Restring.getString(activity, R.string.hippo_proceed_to_pay);
                        paymentView.payBtn.setText(text);

                        paymentView.icon.setImageResource(R.drawable.hippo_ic_secure_payment);
                        paymentView.icon.setColorFilter(new PorterDuffColorFilter(hippoColorConfig.getHippoPaymentTitle(),
                                PorterDuff.Mode.SRC_IN));

                        paymentView.title1.setTextColor(hippoColorConfig.getHippoPaymentTitle());
                        paymentView.title2.setTextColor(hippoColorConfig.getHippoPaymentTitle());
                    }
                }
                paymentView.recyclerView.setAdapter(paymentAdapter);
                break;

            case HIPPO_MULTISELECTION_VIEW:
                final MultiSelectionView selectionView = (MultiSelectionView) holder;
                Message selectionMsg = fuguItems.get(position);

                LinearLayoutManager selectionLayoutManager = new LinearLayoutManager(activity);
                selectionLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                selectionView.recyclerView.setLayoutManager(selectionLayoutManager);

                if (selectionMsg.getCustomAction() != null && selectionMsg.getCustomAction().getMultiSelectButtons() != null
                        && selectionMsg.getCustomAction().getMultiSelectButtons().size() > 0) {
                    selectionView.recyclerView.setVisibility(View.VISIBLE);

                    boolean isMultiSelected = true;
                    int max = -1;
                    try {
                        max = selectionMsg.getCustomAction().getMaxSelection();
                        if (selectionMsg.getCustomAction().getMinSelection() < 1)
                            max = -1;
                        if (selectionMsg.getCustomAction().getMinSelection() == 1 && selectionMsg.getCustomAction().getMaxSelection() == 1) {
                            isMultiSelected = false;
                        }
                    } catch (Exception e) {

                    }

                    MultiSelectionAdapter adapter = new MultiSelectionAdapter(selectionMsg.getCustomAction().getMultiSelectButtons(),
                            isMultiSelected, max, selectionMsg.getCustomAction().isReplied(), hippoColorConfig, new OnMultiSelectionListener() {
                        @Override
                        public void onItemClicked(ArrayList<MultiSelectButtons> selectButtons) {
                            boolean isDisabled = true;
                            for (MultiSelectButtons buttons : selectButtons) {
                                if (buttons.getStatus() == 1) {
                                    selectionView.selectBtn.setEnabled(true);
                                    isDisabled = false;
                                    break;
                                }
                            }
                            if (isDisabled)
                                selectionView.selectBtn.setEnabled(false);
                        }
                    });
                    selectionView.selectBtn.setEnabled(false);
                    selectionView.recyclerView.setAdapter(adapter);

                    if (selectionMsg.getCustomAction().isReplied()) {
                        selectionView.selectBtn.setVisibility(View.GONE);
                    } else {
                        selectionView.selectBtn.setVisibility(View.VISIBLE);
                        selectionView.selectBtn.setText(Restring.getString(activity, R.string.hippo_submit));
                    }
                } else {
                    selectionView.recyclerView.setVisibility(View.GONE);
                    selectionView.selectBtn.setVisibility(View.GONE);
                }


                selectionView.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());

                String userNameText23 = "";
                if (!TextUtils.isEmpty(selectionMsg.getfromName())) {
                    userNameText23 = selectionMsg.getfromName();
                } else {
                    userNameText23 = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : Restring.getString(activity, R.string.fugu_support);
                }

                setTextMessageOtherBot(position, selectionMsg, selectionView.llMessageBgLeft, selectionView.tvUserName,
                        selectionView.rootRight, selectionView.tvMsg, userNameText23, selectionView.userImage, chatType);

                String messageStr23 = selectionMsg.getMessage();//.replace(" ", "&nbsp;");
                selectionView.tvMsg.setText(Html.fromHtml(messageStr23.replace("\n", "<br /> ")));
                selectionView.tvUserName.setText(userNameText23);
                if (TextUtils.isEmpty(selectionMsg.getSentAtUtc())) {
                    selectionView.tvTime.setVisibility(View.GONE);
                } else {
                    selectionView.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(selectionMsg.getSentAtUtc())));
                    selectionView.tvTime.setVisibility(View.VISIBLE);
                }


                break;
        }
    }


    private void setTextMessageOtherBot(int position, Message currentOrderItem, LinearLayout llMessageBg, TextView tvUserName,
                                        RelativeLayout llRoot, TextView tvMsg, String userNameText, ImageView userImage, int chatType) {

        tvUserName.setVisibility(View.VISIBLE);
        tvUserName.setText(userNameText);

        int right = 0;
        int left = dp40();
        int bottom = dp2();
        int top = dp2();
        int typePre = 0;
        if (position > 0)
            typePre = getItemViewType(position - 1);

        boolean hasUserImage = currentOrderItem.isHasImageView() || typePre == HIPPO_USER_CONCENT_VIEW;
        if (hasUserImage && chatType != p2pChatType) {
            userImage.setVisibility(View.VISIBLE);
            String imageUrl = currentOrderItem.getUserImage();
            if (TextUtils.isEmpty(imageUrl) || currentOrderItem.getUserId().intValue() == 0) {
                imageUrl = getBotImage();
            }

            Glide.with(activity).asBitmap()
                    .apply(getRequestOptions(userNameText))
                    .load(imageUrl)
                    .into(userImage);
            left = 0;
            top = dp15();
        } else if (chatType == p2pChatType) {
            left = dp8();
            userImage.setVisibility(View.GONE);
            tvUserName.setVisibility(View.GONE);
        } else {
            tvUserName.setVisibility(View.GONE);
            userImage.setVisibility(View.GONE);
        }
        llRoot.setPadding(left, top, right, bottom);
    }

    private void setRatingView(int position, Message currentOrderItem, TextView tvUserName,
                               LinearLayout llRoot, ImageView userImage) {

        tvUserName.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);

        int typePre = 0;
        if (position > 0)
            typePre = getItemViewType(position - 1);

        boolean hasUserImage = currentOrderItem.isHasImageView() || typePre == HIPPO_USER_CONCENT_VIEW;
        int bottom = dp2();
        int top = dp2();
        int left = dp40();
        if (hasUserImage) {

            String userNameText = "";
            String[] userNameSplitArray;
            int userNameStringCount = 1;
            if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                userNameText = currentOrderItem.getfromName();
            } else {
                userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                        : Restring.getString(activity, R.string.fugu_support);
            }

            if (!TextUtils.isEmpty(userNameText)) {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(userNameText);
            }

            String imageUrl = currentOrderItem.getUserImage();
            if (currentOrderItem.getUserType() == 0 || (currentOrderItem.getOriginalMessageType() == BOT_TEXT_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 16 ||
                    currentOrderItem.getOriginalMessageType() == BOT_FORM_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 20)) {
                imageUrl = getBotImage();
            }

            if (chatType != p2pChatType) {
                userImage.setVisibility(View.VISIBLE);
                left = 0;
                loadUserImage(userImage, userNameText, imageUrl);
            } else {
                userImage.setVisibility(View.GONE);
            }
            top = dp15();
        } else if (chatType == p2pChatType) {
            left = dp8();
            userImage.setVisibility(View.GONE);
        }

        HippoLog.e("Hippo", "Hippo bottom = " + bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            llRoot.setPaddingRelative(left, top, 0, bottom);
        } else {
            llRoot.setPadding(left, top, 0, bottom);
        }
    }

    private void setOtherSideView(int position, Message currentOrderItem, TextView tvUserName,
                                  LinearLayout llRoot, ImageView userImage) {
        tvUserName.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);

        int typePre = 0;
        if (position > 0)
            typePre = getItemViewType(position - 1);

        boolean hasUserImage = currentOrderItem.isHasImageView() || typePre == HIPPO_USER_CONCENT_VIEW;
        int bottom = dp2();
        int top = dp2();
        int left = dp40();
        if (chatType == p2pChatType) {
            left = dp8();
            userImage.setVisibility(View.GONE);
        } else if (hasUserImage) {

            String userNameText = "";
            String[] userNameSplitArray;
            int userNameStringCount = 1;
            if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                userNameText = currentOrderItem.getfromName();
            } else {
                userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                        : Restring.getString(activity, R.string.fugu_support);
            }

            if (!TextUtils.isEmpty(userNameText)) {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(userNameText);
            }

            String imageUrl = currentOrderItem.getUserImage();
            if (currentOrderItem.getUserType() == 0 || (currentOrderItem.getOriginalMessageType() == BOT_TEXT_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 16 ||
                    currentOrderItem.getOriginalMessageType() == BOT_FORM_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 20)) {
                imageUrl = getBotImage();
            }

            if (chatType != p2pChatType) {
                userImage.setVisibility(View.VISIBLE);
                left = 0;
                loadUserImage(userImage, userNameText, imageUrl);
            } else {
                userImage.setVisibility(View.GONE);
            }
            top = dp15();
        }

        HippoLog.e("Hippo", "Hippo bottom = " + bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            llRoot.setPaddingRelative(left, top, 0, bottom);
        } else {
            llRoot.setPadding(left, top, 0, bottom);
        }
    }

    private void setTextMessageOther(int position, Message currentOrderItem, LinearLayout llMessageBg, TextView tvUserName,
                                     LinearLayout llRoot, TextView tvMsg, ImageView ivMsgImage, String userNameText,
                                     ImageView userImage, int chatType) {


        tvUserName.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);

        int typePre = 0;
        if (position > 0)
            typePre = getItemViewType(position - 1);

        boolean hasUserImage = currentOrderItem.isHasImageView() || typePre == HIPPO_USER_CONCENT_VIEW;
        int bottom = dp2();
        int top = dp2();
        int left = dp40();
        if (hasUserImage) {

            if (!TextUtils.isEmpty(userNameText)) {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(userNameText);
            }

            String imageUrl = currentOrderItem.getUserImage();
            if (currentOrderItem.getUserType() == 0 || (currentOrderItem.getOriginalMessageType() == BOT_TEXT_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 16 ||
                    currentOrderItem.getOriginalMessageType() == BOT_FORM_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 20)) {
                imageUrl = getBotImage();
            }

            if (chatType != p2pChatType) {
                userImage.setVisibility(View.VISIBLE);
                left = 0;
                loadUserImage(userImage, userNameText, imageUrl);
            } else {
                left = dp8();
                userImage.setVisibility(View.GONE);
            }
            top = dp15();
        } else if (chatType == p2pChatType) {
            left = dp8();
            userImage.setVisibility(View.GONE);
        }

        HippoLog.e("Hippo", "Hippo bottom = " + bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            llRoot.setPaddingRelative(left, top, 0, bottom);
        } else {
            llRoot.setPadding(left, top, 0, bottom);
        }

        //llMessageBg.setBackgroundColor(hippoColorConfig.getHippoActionBarBg());
        //llMessageBg.setBackgroundColor(hippoColorConfig.getHippoBgMessageFrom());
    }


    private void setTextMessageSelf(boolean isRightToLeft, int position, LinearLayout fuguLlRoot, RelativeLayout fuguRlImageMessage,
                                    RelativeLayout fuguRlMessages, LinearLayout FuguLlMessageBg, TextView fuguTvMsg,
                                    TextView fuguTvTime, Message currentOrderItem) {

        int bottom = dp2();
        int top = dp2();

        boolean hasTextUserImage = currentOrderItem.isHasImageView();
        if (hasTextUserImage) {
            //HippoLog.e("TAG", "hasTextUserImage = "+hasTextUserImage);
            top = dp20();
            if ((position - 1 >= 0) && (getItemViewType(position - 1) == HIPPO_USER_CONCENT_VIEW))
                top = dp2();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fuguLlRoot.setPaddingRelative(0, top, 0, bottom);
        } else {
            fuguLlRoot.setPadding(0, top, 0, bottom);
        }

        fuguTvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());
        fuguTvMsg.setLinkTextColor(hippoColorConfig.getHippoUrlLinkText());
        fuguTvMsg.setAutoLinkMask(Linkify.ALL);
        fuguTvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());

    }


    private void setView(LinearLayout llGalleryButtonLayout, int count) {
        llGalleryButtonLayout.removeAllViews();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Amit");
        strings.add("Gurmail");
        strings.add("Ankush");
        strings.add("Vishal");
        strings.add("Gagan");
        for (int i = 0; i < count; i++) {
            final int pos = i;
            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.hippo_layout_gallery_button, null);
            TextView textView = view.findViewById(R.id.tvButton);
            textView.setText(strings.get(i));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "Button" + String.valueOf(pos) + "clicked", Toast.LENGTH_SHORT).show();
                }
            });
            llGalleryButtonLayout.addView(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return fuguItems.get(position).getMessageType();
    }


    @Override
    public long getItemId(int position) {
        try {
            return fuguItems.get(position).getMessageIndex();
        } catch (Exception e) {
            return position;
        }
    }

    /*@Override
    public long getItemId(int position) {
        return fuguItems.get(position).getId().intValue();
    }*/

    private float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public int getItemCount() {
        return fuguItems.size();
    }

    private void setTime(AppCompatTextView tvImageTime, String sentAtUtc) {
        tvImageTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(sentAtUtc)));
    }

    private void setTime(TextView tvImageTime, String sentAtUtc) {
        tvImageTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(sentAtUtc)));
    }

    private void setIntegrationSource(ImageView source, ImageView messenger, int type) {
        source.setVisibility(View.GONE);
        messenger.setVisibility(View.GONE);
        if (!isSourceMessageEnabled)
            return;

        int color = hippoColorConfig.getHippoSourceType();
        switch (type) {
            case 5:
                source.setImageResource(R.drawable.hippo_ic_email);
                source.setVisibility(View.VISIBLE);
                source.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                break;
            case 6:
                messenger.setVisibility(View.VISIBLE);
                break;
            case 7:
                source.setImageResource(R.drawable.hippo_ic_sms);
                source.setVisibility(View.VISIBLE);
                source.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                break;
            default:
                source.setVisibility(View.GONE);
                messenger.setVisibility(View.GONE);
                break;
        }
    }

    private void showImageView(ImageView imageView, Message message) {
        showImageView(imageView, message, true);
    }

    private void showImageView(ImageView imageView, Message message, boolean hasLocalImage) {
        String fname = "";
        String localPath = "";
        if (hasLocalImage) {
            fname = Util.getFileName(message.getFileName(), message.getMuid());
            localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(DocumentType.IMAGE.toString()));
            HippoLog.e("localPath", "localPath = " + localPath);
        }


//        RequestOptions myOptions = new RequestOptions()
//                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.hippo_placeholder)
//                .error(R.drawable.hippo_placeholder);

        RequestOptions myOptions = RequestOptions
                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));


//        Glide.with(activity).load(TextUtils.isEmpty(localPath) ? message.getThumbnailUrl() : new File(localPath))
//                .apply(myOptions)
//                .into(imageView);

        Glide.with(activity)
                .asBitmap()
                .load(TextUtils.isEmpty(localPath) ? message.getThumbnailUrl() : new File(localPath))
                .error(Glide.with(activity).asBitmap().load(message.getThumbnailUrl()))
                .apply(myOptions)
                .into(imageView);
    }

    private void setMessageStatus(AppCompatImageView ivMessageState, int messageStatus, Boolean isImage) {
        if (isImage) {
            switch (messageStatus) {
                case MESSAGE_SENT:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single_white));
                    break;
                case MESSAGE_UNSENT:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting_white));
                    break;
                case MESSAGE_DELIVERED:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                    break;
                case MESSAGE_READ:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                    break;
                case MESSAGE_FILE_RETRY:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting_white));
                    break;
                case MESSAGE_IMAGE_RETRY:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting_white));
                    break;
                default:

                    break;
            }
        } else {
            switch (messageStatus) {
                case MESSAGE_SENT:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                    break;
                case MESSAGE_UNSENT:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                    break;
                case MESSAGE_DELIVERED:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                    break;
                case MESSAGE_READ:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                    break;
                case MESSAGE_FILE_RETRY:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                    break;
                case MESSAGE_IMAGE_RETRY:
                    ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                    break;

                default:

                    break;
            }
        }
    }

    private void setImageHeightAndWidth(ImageView imageView, RelativeLayout rlImageMessage, LinearLayoutCompat llImageMessage, Message message, Boolean isSpiked) {
        int imageHeight = message.getImageHeight();
        int imageWidth = message.getImageWidth();
        if (imageHeight != 0 && message.getImageWidth() != 0) {
            float ratio = (float) (imageHeight / imageWidth);
            if (ratio < 1) {
                rlImageMessage.getLayoutParams().height = (int) (pxToDp(FuguAppConstant.MAX_HEIGHT) * ratio);
                rlImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH);
                if (isSpiked) {
                    llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER);
                } else {
                    llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER_SPIKED);
                }
            } else {
                rlImageMessage.getLayoutParams().height = pxToDp(FuguAppConstant.MAX_HEIGHT);
                rlImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH);
                if (isSpiked) {
                    llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER);
                } else {
                    llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER_SPIKED);
                }
            }
        } else {
            rlImageMessage.getLayoutParams().height = pxToDp(FuguAppConstant.MAX_HEIGHT);
            rlImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH);
            if (isSpiked) {
                llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER);
            } else {
                llImageMessage.getLayoutParams().width = pxToDp(FuguAppConstant.MAX_WIDTH_OUTER_SPIKED);
            }
        }
    }

    private void setVideoDownloadStatus(final LinearLayout videoDownloadStatus, ImageView playBtn, TextView fileSize, final ProgressWheel progressBar, final Message message, final int position) {
        String fname = Util.getFileName(message.getFileName(), message.getMuid());
        String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(message.getDocumentType()));
        videoDownloadStatus.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        playBtn.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(localPath) &&
                (message.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                        || message.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                        || message.getMessageStatus() == FuguAppConstant.MESSAGE_SENT)) {
            playBtn.setVisibility(View.VISIBLE);
        } else {
            switch (message.getDownloadStatus()) {
                case 1:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    playBtn.setVisibility(View.VISIBLE);
                    break;
                default:
                    videoDownloadStatus.setVisibility(View.VISIBLE);
                    fileSize.setText(message.getFileSize());
                    break;
            }
        }
    }

    private boolean setVideoUiStatus(boolean isSelf, Message message, LinearLayout download, ImageView playBtn,
                                     AppCompatButton btnRetry, AppCompatButton btnCancel, ProgressWheel progress, boolean isImageView) {

        if (isSelf) {

            download.setVisibility(View.GONE);
            playBtn.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);

            if (message.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                    || message.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                    || message.getMessageStatus() == FuguAppConstant.MESSAGE_SENT) {
                //sent
                if (!isImageView) {
                    String fname = Util.getFileName(message.getFileName(), message.getMuid());
                    final String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(message.getDocumentType()));
                    if (TextUtils.isEmpty(localPath)) {
                        // show download
                        download.setVisibility(View.VISIBLE);
                    } else {
                        //show play button
                        playBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    return true;
                }
            } else {
                //unsent
                if (message.getUploadStatus() == FuguAppConstant.UPLOAD_IN_PROGRESS) {
                    // show loader
                    progress.setVisibility(View.VISIBLE);

                    //message.setUploadStatus(FuguAppConstant.UPLOAD_FAILED);

                } else if (message.getIsMessageExpired() == 1) {
                    //retry
                    btnRetry.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            }
        } else {

        }
        return false;
    }

    private void setVideoUploadStatus(AppCompatTextView tvImageTime, ProgressWheel circleProgress, AppCompatButton fuguBtnRetry,
                                      ImageView ivPlay, Message currentOrderItem2, ImageView ivDownload) {
        switch (currentOrderItem2.getUploadStatus()) {
            case FuguAppConstant.UPLOAD_FAILED:
                if (currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_SENT) {
                    circleProgress.setVisibility(View.GONE);
                    fuguBtnRetry.setVisibility(View.GONE);
                    ivPlay.setVisibility(View.VISIBLE);

                } else {
                    fuguBtnRetry.setVisibility(View.VISIBLE);
                    circleProgress.setVisibility(View.GONE);
                    ivPlay.setVisibility(View.GONE);
                }
                break;
            case FuguAppConstant.UPLOAD_IN_PROGRESS:
                circleProgress.setVisibility(View.VISIBLE);
                fuguBtnRetry.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                break;
            case FuguAppConstant.UPLOAD_COMPLETED:
                circleProgress.setVisibility(View.GONE);
                fuguBtnRetry.setVisibility(View.GONE);

                if (currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_SENT) {
                    ivPlay.setVisibility(View.VISIBLE);
                } else {
                    ivPlay.setVisibility(View.GONE);
                }
                break;
            default:
                tvImageTime.append("10");
                circleProgress.setVisibility(View.GONE);
                fuguBtnRetry.setVisibility(View.GONE);

                if (currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                        || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_SENT) {
                    ivPlay.setVisibility(View.VISIBLE);
                } else {
                    ivPlay.setVisibility(View.GONE);
                }
                break;
        }
        if (currentOrderItem2.getIsMessageExpired() == 1) {
            fuguBtnRetry.setVisibility(View.VISIBLE);
        } else if (currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_READ
                || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_DELIVERED
                || currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_SENT) {
            circleProgress.setVisibility(View.GONE);
            fuguBtnRetry.setVisibility(View.GONE);
            ivPlay.setVisibility(View.VISIBLE);
            String fname = Util.getFileName(currentOrderItem2.getFileName(), currentOrderItem2.getMuid());
            String localPath = FileManager.getInstance().getLocalPath(fname,
                    FOLDER_TYPE.get(DocumentType.VIDEO.toString()));
            if (TextUtils.isEmpty(localPath)) {
                ivDownload.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.GONE);
            }
        }
    }

    private void setFileCLickListener(LinearLayoutCompat llFile, final Message message, final AppCompatImageView ivFilePlay,
                                      final int position, final AppCompatImageView ivFileDownload) {
        ivFilePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaySound(message, ivFilePlay, position);
            }
        });


        llFile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    if (message.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString())) {
                        return true;
                    }
                    String fname = Util.getFileName(message.getFileName(), message.getMuid());

                } catch (Exception e) {

                }
                new AlertDialog.Builder(activity)
                        .setMessage("Download this file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String docType = "image";
                                if (message.getOriginalMessageType() == FILE_MESSAGE) {
                                    docType = message.getDocumentType();
                                }

                                String fullPath = Util.getOrCreateDirectoryPath(FOLDER_TYPE.get(docType));

                                String fileName = message.getFileName();

                                String fName = Util.extractFileNameWithoutSuffix(message.getFileName());
                                String ext = Util.getExtension(fName);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getFileUrl()));
                                request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
                                request.setTitle(fName);
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                //request.setDestinationInExternalPublicDir(fullPath, fileName);
                                request.setDestinationInExternalFilesDir(HippoConfig.getInstance().getContext(), Environment.DIRECTORY_DOWNLOADS, fileName);

                                DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

                                if (manager != null) {
                                    manager.enqueue(request);
                                }


                            }
                        }).show();
                return true;
            }
        });

        llFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (message.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString())) {

                        return;
                    }
                    String fname = Util.getFileName(message.getFileName(), message.getMuid());
                    final String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(message.getDocumentType()));
                    if (!TextUtils.isEmpty(localPath)) {
                        FileManager.getInstance().openFileInDevice(activity, localPath, new FileManager.FileCopyListener() {
                            @Override
                            public void onCopingFile(boolean flag, FileuploadModel fileuploadModel) {

                            }

                            @Override
                            public void largeFileSize() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(activity, Restring.getString(activity, R.string.no_handler), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        if (!fuguChatActivity.checkPermission()) {
                            fuguChatActivity.readExternalStorage();
                            return;
                        }

                        Uri path = FileManager.getInstance().getPublicFilePath(HippoConfig.getInstance().getContext(), message.getFileName());
                        if (path != null) {
                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
                            String mimeType = myMime.getMimeTypeFromExtension(Util.getExtension(path.getPath()));
                            if (TextUtils.isEmpty(mimeType))
                                mimeType = URLConnection.guessContentTypeFromName(path.getPath());
                            Uri uri = path;//FileProvider.getUriForFile(activity, CommonData.getProvider(), new File(path.getPath()));
                            newIntent.setDataAndType(uri, mimeType);
                            newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {
                                activity.startActivity(newIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (ivFileDownload.getVisibility() == View.VISIBLE)
                                return;
                            if (!fuguChatActivity.isNetworkAvailable()) {
                                return;
                            }
                            new AlertDialog.Builder(activity)
                                    .setMessage("Download this file?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String docType = "image";
                                            if (message.getOriginalMessageType() == FILE_MESSAGE) {
                                                docType = message.getDocumentType();
                                            }

                                            String fullPath = Util.getOrCreateDirectoryPath(FOLDER_TYPE.get(docType));

                                            String fileName = message.getFileName();

                                            String fName = Util.extractFileNameWithoutSuffix(message.getFileName());
                                            String ext = Util.getExtension(fName);
                                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getFileUrl()));
                                            request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
                                            request.setTitle(fName);
                                            request.allowScanningByMediaScanner();
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                            //request.setDestinationInExternalPublicDir(fullPath, fileName);
                                            request.setDestinationInExternalFilesDir(HippoConfig.getInstance().getContext(), Environment.DIRECTORY_DOWNLOADS, fileName);

                                            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

                                            if (manager != null) {
                                                manager.enqueue(request);
                                            }


                                        }
                                    }).show();
                        }
                    }
                } catch (Exception e) {
                    if (HippoConfig.DEBUG)
                        e.printStackTrace();
                }
            }
        });
    }

    private void setFileDownLoadStatus(ImageView fileView, final AppCompatImageView ivFilePlay, ProgressWheel circleProgress, AppCompatImageView ivFileDownload,
                                       AppCompatImageView ivFileUpload, final Message currentOrderItem2, final int position) {
        ivFileDownload.setVisibility(View.GONE);
        circleProgress.setVisibility(View.GONE);
        ivFileUpload.setVisibility(View.GONE);
        ivFilePlay.setVisibility(View.GONE);

        String fname = Util.getFileName(currentOrderItem2.getFileName(), currentOrderItem2.getMuid());
        String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(currentOrderItem2.getDocumentType()));
        HippoLog.v("localPath", "localPath ********* = " + localPath);
        if (!TextUtils.isEmpty(localPath)) {
            if (currentOrderItem2.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString()))
                ivFilePlay.setVisibility(View.VISIBLE);
        } else {
            switch (currentOrderItem2.getDownloadStatus()) {
                case 1:
                    circleProgress.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    if (currentOrderItem2.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString()))
                        ivFilePlay.setVisibility(View.VISIBLE);
                    break;
                default:
                    ivFileDownload.setVisibility(View.VISIBLE);
                    break;
            }
        }

        ivFilePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaySound(currentOrderItem2, ivFilePlay, position);
            }
        });
    }

    private void setFileUploadStatus(ImageView fileView, final AppCompatImageView ivFilePlay, ProgressWheel circleProgress, AppCompatImageView ivFileDownload,
                                     AppCompatImageView ivFileUpload, final Message currentOrderItem2, final int position) {

        ivFileDownload.setVisibility(View.GONE);
        circleProgress.setVisibility(View.GONE);
        ivFileUpload.setVisibility(View.GONE);
        ivFilePlay.setVisibility(View.GONE);
        if (currentOrderItem2.getIsMessageExpired() == 1) {
            ivFileUpload.setVisibility(View.VISIBLE);
        } else {
            if (currentOrderItem2.getMessageStatus() == FuguAppConstant.MESSAGE_UNSENT) {
                switch (currentOrderItem2.getUploadStatus()) {
                    case FuguAppConstant.UPLOAD_FAILED:
                        ivFileUpload.setVisibility(View.VISIBLE);
                        break;
                    case FuguAppConstant.UPLOAD_IN_PROGRESS:
                        circleProgress.setVisibility(View.VISIBLE);
                        break;
                    case FuguAppConstant.UPLOAD_COMPLETED:
                        if (currentOrderItem2.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString()))
                            ivFilePlay.setVisibility(View.VISIBLE);
                        break;
                    default:

                        break;
                }
            } else {
                String fname = Util.getFileName(currentOrderItem2.getFileName(), currentOrderItem2.getMuid());
                String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(currentOrderItem2.getDocumentType()));
                HippoLog.v("localPath", "localPath ********* = " + currentOrderItem2.getFileName());
                if (!TextUtils.isEmpty(localPath)) {
                    if (currentOrderItem2.getDocumentType().equalsIgnoreCase(DocumentType.AUDIO.toString()))
                        ivFilePlay.setVisibility(View.VISIBLE);
                } else {
                    ivFileDownload.setVisibility(View.VISIBLE);
                }
            }
        }

        ivFilePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaySound(currentOrderItem2, ivFilePlay, position);
            }
        });
    }

    private void onPlaySound(final Message currentOrderItem2, final AppCompatImageView ivFilePlay, final int position) {
        if (!TextUtils.isEmpty(fuguChatActivity.audioMuid)) {
            if (fuguChatActivity.audioMuid.equalsIgnoreCase(currentOrderItem2.getMuid())) {
                fuguChatActivity.audioMuid = "";
                currentOrderItem2.setAudioPlaying(false);
                ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                CommonMediaPlayer.getInstance().stopMedia();
                notifyItemChanged(position);
                return;
            } else {
                fuguItems.get(fuguChatActivity.playingItem).setAudioPlaying(false);
                notifyItemChanged(fuguChatActivity.playingItem);
            }
        }

        String fname = Util.getFileName(currentOrderItem2.getFileName(), currentOrderItem2.getMuid());
        String localPath = FileManager.getInstance().getLocalPath(fname, FOLDER_TYPE.get(currentOrderItem2.getDocumentType()));
        CommonMediaPlayer.getInstance().playMediaPlayer(activity, localPath, new CommonMediaPlayer.MediaPlayerStatus() {
            @Override
            public void onPlaying() {
                currentOrderItem2.setAudioPlaying(true);
                fuguChatActivity.audioMuid = currentOrderItem2.getMuid();
                fuguChatActivity.playingItem = position;
                ivFilePlay.setImageResource(R.drawable.hippo_song_pause);
                currentOrderItem2.setAudioPlaying(true);
                notifyItemChanged(position);
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                fuguChatActivity.audioMuid = "";
                ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                currentOrderItem2.setAudioPlaying(false);
                CommonMediaPlayer.getInstance().stopMedia();
                notifyItemChanged(position);
            }

            @Override
            public void onError(MediaPlayer mp, int what, int extra) {
                fuguChatActivity.audioMuid = "";
                ivFilePlay.setImageResource(R.drawable.hippo_music_player);
                currentOrderItem2.setAudioPlaying(false);
                CommonMediaPlayer.getInstance().stopMedia();
                notifyItemChanged(position);
            }
        });
    }


    private void setDownloadClick(final AppCompatImageView ivFileDownload, final ProgressWheel circleProgress, final Message message, final int position) {
        if (!fuguChatActivity.isNetworkAvailable()) {

            return;
        }
        ivFileDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!fuguChatActivity.checkPermission()) {
                    fuguChatActivity.readExternalStorage();
                    return;
                }
                circleProgress.setVisibility(View.VISIBLE);

                ivFileDownload.setVisibility(View.GONE);

                String docType = "image";
                if (message.getOriginalMessageType() == FILE_MESSAGE) {
                    docType = message.getDocumentType();
                }

                String fileName = message.getFileName();

                String fullPath = Util.getOrCreateDirectoryPath(FOLDER_TYPE.get(docType));
                int downloadId = downloadFileFromUrl(fullPath, fileName, message, position);

                message.setDownloadId(downloadId);
            }
        });
    }

    private void setDownloadLongClick(final LinearLayoutCompat ivFileDownload, final ProgressWheel circleProgress, final Message message, final int position) {
        ivFileDownload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });

    }


    int currentProgress = -1;

    private int downloadFileFromUrl(String dirPath, String fileName, final Message message, final int position) {
        if (!fileName.contains(message.getMuid())) {
            String muid = message.getMuid();
            String ext = Util.getExtension(fileName);
            String name = Util.extractFileNameWithoutSuffix(fileName);
            fileName = name + "_" + muid + "." + ext;
        }
        currentProgress = -1;
//        if(PRDownloader.getStatus(message.getDownloadId()).equals(Status.RUNNING) || PRDownloader.getStatus(message.getDownloadId()).equals(Status.QUEUED)
//                || PRDownloader.getStatus(message.getDownloadId()).equals(Status.UNKNOWN)) {
//            return 0;
//        }
        return PRDownloader.download(message.getFileUrl(), dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        HippoLog.v(TAG, "OnStartOrResumeListener");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        message.setDownloadStatus(DownloadStatus.DOWNLOAD_PAUSED.downloadStatus);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int cProgress = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        if (currentProgress < cProgress) {
                            currentProgress++;
                        }

                        message.setCurrentprogress(cProgress);
                        message.setDownloadStatus(DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        message.setDownloadStatus(DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus);
                        Intent mIntent = getIntentExtraData(position, 100, message.getMuid(), FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus);
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(mIntent);
                    }

                    @Override
                    public void onError(Error error) {
                        message.setDownloadStatus(DownloadStatus.DOWNLOAD_FAILED.downloadStatus);
                        Intent mIntent = getIntentExtraData(position, 0, message.getMuid(), FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus);
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(mIntent);
                    }
                });

    }

    private Intent getIntentExtraData(int position, int progress, String muid, int status) {
        Intent mIntent = new Intent(HIPPO_PROGRESS_INTENT);
        mIntent.putExtra(HIPPO_POSITION, position);
        mIntent.putExtra(HIPPO_PROGRESS, progress);
        mIntent.putExtra(MESSAGE_UNIQUE_ID, muid);
        mIntent.putExtra(HIPPO_STATUS_UPLOAD, status);
        return mIntent;
    }

    private void messageStatusTick(ImageView fuguIvMessageState, Message currentOrderItem2) {
        switch (currentOrderItem2.getMessageStatus()) {
            case MESSAGE_UNSENT:
            case MESSAGE_IMAGE_RETRY:
            case MESSAGE_FILE_RETRY:
                fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                fuguIvMessageState.setVisibility(View.VISIBLE);
                fuguIvMessageState.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                break;
            case MESSAGE_READ:
                fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                fuguIvMessageState.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoMessageRead(), PorterDuff.Mode.SRC_ATOP);
                fuguIvMessageState.setVisibility(View.VISIBLE);
                break;
            case MESSAGE_SENT:
                fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                fuguIvMessageState.setVisibility(View.VISIBLE);
                fuguIvMessageState.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                break;
            case MESSAGE_DELIVERED:
                fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                if (Build.VERSION.SDK_INT >= 21) {
                    fuguIvMessageState.getDrawable().setTint(ContextCompat.getColor(activity, R.color.fugu_drawable_color));
                }
                fuguIvMessageState.setVisibility(View.VISIBLE);
                fuguIvMessageState.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                break;
            default:
                fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                fuguIvMessageState.setVisibility(View.VISIBLE);
                fuguIvMessageState.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);

                break;
        }
    }

    private void setUploadClick(AppCompatImageView ivFileUpload, ProgressWheel circleProgress, Message message) {
        ivFileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((FuguChatActivity) activity).isNetworkAvailable()) {
                    if (mOnRetry != null) {

                    }
                }
            }
        });
    }

    private boolean setOtherMessageBackground(View viewMessage, View viewBg, int position, TextView viewUsername) {
        int oneDp = pxToDp(1);
        int sixDp = pxToDp(6);
        int sevenDp = pxToDp(7);
        int eight = pxToDp(8);

        if (fuguItems.size() == 1) {
            viewBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);
            viewMessage.setPadding(eight, sevenDp, 0, oneDp);
            viewBg.setPadding(pxToDp(13), sixDp, sixDp, sixDp);
            return true;
        } else {
            if (position == 0) {
                viewMessage.setPadding(eight, sevenDp, 0, oneDp);
                viewBg.setPadding(pxToDp(13), sixDp, sixDp, sixDp);
                if (viewUsername != null) {
                    viewUsername.setPadding(eight, 0, 0, 0);
                }
                viewBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);

                return true;
            } else if (getItemViewType(position - 1) == FUGU_ITEM_TYPE_OTHER
                    || getItemViewType(position - 1) == FUGU_OTHER_VIDEO_CALL_VIEW
                    || getItemViewType(position - 1) == HIPPO_VIDEO_MESSGAE_OTHER) {
                viewBg.setBackgroundResource(R.drawable.hippo_chat_bg_right_normal);
                if (viewUsername != null) {
                    viewUsername.setVisibility(View.GONE);
                }
                viewMessage.setPadding(pxToDp(15), oneDp, 0, oneDp);
                viewBg.setPadding(sixDp, sixDp, sixDp, sixDp);
                return false;
            } else {
                viewBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);
                viewMessage.setPadding(eight, sevenDp, 0, oneDp);
                viewBg.setPadding(pxToDp(13), sixDp, sixDp, sixDp);
                return true;
            }
        }
    }

    private void setSelfMessageBackground(Message fileSentMessage, View viewMessage, View viewBg, int position) {

        int bottom = dp2();
        int top = dp2();

        boolean hasTextUserImage = fileSentMessage.isHasImageView();
        if (hasTextUserImage) {
            top = dp20();
            if ((position - 1 >= 0) && (getItemViewType(position - 1) == HIPPO_USER_CONCENT_VIEW))
                top = dp2();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            viewMessage.setPaddingRelative(0, top, 0, bottom);
        } else {
            viewMessage.setPadding(0, top, 0, bottom);
        }
    }

    private void setFileMessageOther(View viewMessage, View viewBg, int position, TextView userName, String name,
                                     ImageView userImage, Message otherVideoMessage, int chatType) {
        userName.setVisibility(View.GONE);
        userImage.setVisibility(View.INVISIBLE);

        boolean hasUserImage = otherVideoMessage.isHasImageView();//isUserImageView(position, otherVideoMessage.getUserId());
        int bottom = dp1();
        int right = 0;
        int left = dp4();
        if (hasUserImage) {
            if (p2pChatType == chatType) {
                right = dp30();
                left = dp8();
                userImage.setVisibility(View.GONE);
            } else {
                userImage.setVisibility(View.VISIBLE);

                String imageUrl = otherVideoMessage.getUserImage();
                if (TextUtils.isEmpty(imageUrl) || otherVideoMessage.getUserId().intValue() == 0) {
                    imageUrl = getBotImage();
                }

                Glide.with(activity).asBitmap()
                        .apply(getRequestOptions(name))
                        .load(imageUrl)
                        .into(userImage);
            }

            bottom = dp8();
        } else if (p2pChatType == chatType) {
            right = dp30();
            left = dp8();
            userImage.setVisibility(View.GONE);
        }

        userName.setText(name);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            viewMessage.setPaddingRelative(left, dp2(), right, bottom);
        } else {
            viewMessage.setPadding(left, dp2(), right, bottom);
        }*/


    }

    private void setImage(Activity activity, AppCompatImageView ivImageMessage, String thumbnailUrl) {
        try {
            HippoLog.e(TAG, "thumbnailUrl = " + thumbnailUrl);

            RequestOptions myOptions = RequestOptions
                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));


            String fileExt = Util.getExtension(thumbnailUrl);
            if (!TextUtils.isEmpty(fileExt) && fileExt.equalsIgnoreCase("gif")) {
                Glide
                        .with(activity)
                        .asGif()
                        .load(thumbnailUrl)
                        .error(R.drawable.hippo_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(ivImageMessage);
            } else {
                Glide.with(activity)
                        .asBitmap()
                        .load(thumbnailUrl)
                        .error(Glide.with(activity).asBitmap().load(thumbnailUrl))
                        .apply(myOptions)
                        .into(ivImageMessage);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class UserConcentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        //        private TagContainerLayout tagLayout;
        private RecyclerView recyclerView;
        private TextView tvTagTime;

        private TextView tvUserName;
        private TextView tvTime;
        private LinearLayout llTime;
        private RelativeLayout rlMessagesLeft, rlMessagesRight, root_right;
        private LinearLayout llMessageBgLeft;

        // left layout
        private LinearLayout llRoot, llMessageBgRight;
        private LinearLayout llTimeLeft;
        private TextView tvName;
        private TextView tvMsgLeft;
        private TextView tvTimeLeft;
        private ImageView ivMessageState, userImage;


        public UserConcentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
//            tagLayout = itemView.findViewById(R.id.tagLayout);
            tvTagTime = itemView.findViewById(R.id.tvTagTime);
            userImage = itemView.findViewById(R.id.user_image);

            llMessageBgLeft = itemView.findViewById(R.id.llMessageBgLeft);
            root_right = itemView.findViewById(R.id.root_right);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            llTime = itemView.findViewById(R.id.llTime);
            tvTime = itemView.findViewById(R.id.tvTime);

            llRoot = itemView.findViewById(R.id.llRoot);
            llMessageBgRight = itemView.findViewById(R.id.llMessageBgRight);
            llTimeLeft = itemView.findViewById(R.id.llTimeLeft);

            tvName = itemView.findViewById(R.id.tvName);
            tvMsgLeft = itemView.findViewById(R.id.tvMsgLeft);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);

            rlMessagesLeft = itemView.findViewById(R.id.rlMessagesLeft);
            rlMessagesRight = itemView.findViewById(R.id.rlMessagesRight);

            recyclerView = itemView.findViewById(R.id.recyclerView);

            setTextSize(tvMsg, 16);
            setTextSize(tvMsgLeft, 16);
            setTextSize(tvTime, 11);
            setTextSize(tvTimeLeft, 11);
            setTextSize(tvUserName, 16);
            setTextSize(tvName, 16);


        }
    }

    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private AppCompatTextView tvMsg;
        private TextView tvTime;
        private RelativeLayout rlImageMessage;
        private ImageView ivMsgImage;
        private LinearLayout llMessageBg;
        private LinearLayout llRoot;
        private LinearLayout llFileRoot;
        private TextView tvFileName;
        private ImageView ivDownload, userImage;
        private RelativeLayout rlStopDownload, rlMessages;
        private LinearLayout llFileDetails;
        private TextView tvFileSize;
        private TextView tvExtension;
        // custom action components
        private RelativeLayout rlCustomAction;
        private ImageView ivActionImage;
        private TextView tvActionTitle;
        private TextView tvActionTitleDescription;
        private RecyclerView rvActionDescription;
        private RecyclerView rvActionButtons;
        private View vwActionButtonDivider;
        private LinearLayout llTextualContent;
        private ImageView messageSourceType, messageSourceType1;

        OtherMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llMessageBg = itemView.findViewById(R.id.llMessageBg);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            userImage = itemView.findViewById(R.id.user_image);
            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            ivMsgImage = itemView.findViewById(R.id.ivMsgImage);
            llFileRoot = itemView.findViewById(R.id.llFileRoot);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            rlStopDownload = itemView.findViewById(R.id.rlStopDownload);
            llFileDetails = itemView.findViewById(R.id.llFileDetails);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvExtension = itemView.findViewById(R.id.tvExtension);
            rlMessages = itemView.findViewById(R.id.rlMessages);
            rlCustomAction = itemView.findViewById(R.id.layoutCustomAction);
            tvActionTitle = itemView.findViewById(R.id.tvActionTitle);
            rvActionDescription = itemView.findViewById(R.id.rvActionDescription);
            rvActionButtons = itemView.findViewById(R.id.rvActionButtons);
            ivActionImage = itemView.findViewById(R.id.ivActionImage);
            vwActionButtonDivider = itemView.findViewById(R.id.vwActionButtonDivider);
            tvActionTitleDescription = itemView.findViewById(R.id.tvActionDescription);
            llTextualContent = itemView.findViewById(R.id.llTextualContent);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);

            setTextSize(tvMsg, 16);
            setTextSize(tvTime, 11);
            setTextSize(tvUserName, 16);
        }
    }

    class SelfMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView fuguTvMsg, fuguTvTime;
        private RelativeLayout fuguRlImageMessage;
        private ImageView fuguIvMessageState, fuguIvMsgImage;
        private ProgressBar fuguPbLoading;
        private Button fuguBtnRetry;
        private Button btnRetry, btnCancel;
        private LinearLayout FuguLlMessageBg;
        private LinearLayout fuguLlRoot;
        private TextView tvTryAgain;
        private TextView tvCancel;

        private LinearLayout fuguLlFileRoot;
        private TextView fuguTvFileName;
        private ImageView fuguIvUpload;
        private RelativeLayout fuguRlStopUpload, fuguRlMessages;
        private LinearLayout fuguLlFileDetails;
        private TextView fuguTvFileSize;
        private TextView fuguTvExtension;
        private LinearLayout llRetry;

        private RelativeLayout rlCustomAction;
        private ImageView ivActionImage;
        private TextView tvActionTitle;
        private TextView tvActionTitleDescription;
        private RecyclerView rvActionDescription;
        private RecyclerView rvActionButtons;
        private View vwActionButtonDivider;
        private LinearLayout llTextualContent;
        private ImageView messageSourceType, messageSourceType1;

        SelfMessageViewHolder(final View itemView, final OnRecyclerListener itemClickListener) {
            super(itemView);
            fuguLlRoot = itemView.findViewById(R.id.llRoot);
            FuguLlMessageBg = itemView.findViewById(R.id.llMessageBg);
            fuguTvMsg = itemView.findViewById(R.id.tvMsg);
            fuguTvTime = itemView.findViewById(R.id.tvTime);
            fuguRlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            fuguRlMessages = itemView.findViewById(R.id.rlMessages);
            fuguIvMessageState = itemView.findViewById(R.id.ivMessageState);
            fuguIvMsgImage = itemView.findViewById(R.id.ivMsgImage);
            fuguPbLoading = itemView.findViewById(R.id.pbLoading);
            fuguBtnRetry = itemView.findViewById(R.id.btnRetry);
            tvTryAgain = itemView.findViewById(R.id.tvTryAgain);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            llRetry = itemView.findViewById(R.id.llRetry);

            btnRetry = itemView.findViewById(R.id.btnRetry);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            fuguLlFileRoot = itemView.findViewById(R.id.llFileRoot);
            fuguTvFileName = itemView.findViewById(R.id.tvFileName);
            fuguIvUpload = itemView.findViewById(R.id.ivUpload);
            fuguRlStopUpload = itemView.findViewById(R.id.rlStopUpload);
            fuguLlFileDetails = itemView.findViewById(R.id.llFileDetails);
            fuguTvFileSize = itemView.findViewById(R.id.tvFileSize);
            fuguTvExtension = itemView.findViewById(R.id.tvExtension);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);

            rlCustomAction = itemView.findViewById(R.id.layoutCustomAction);
            tvActionTitle = itemView.findViewById(R.id.tvActionTitle);
            rvActionDescription = itemView.findViewById(R.id.rvActionDescription);
            rvActionButtons = itemView.findViewById(R.id.rvActionButtons);
            ivActionImage = itemView.findViewById(R.id.ivActionImage);
            vwActionButtonDivider = itemView.findViewById(R.id.vwActionButtonDivider);
            tvActionTitleDescription = itemView.findViewById(R.id.tvActionDescription);
            llTextualContent = itemView.findViewById(R.id.llTextualContent);

            setTextSize(fuguTvMsg, 16);
            setTextSize(fuguTvTime, 11);

            setTextSize(btnRetry, 12);
            setTextSize(btnCancel, 12);
//
//            fuguTvMsg.setTypeface(customNormal);
//            fuguTvTime.setTypeface(customNormal);
//            btnRetry.setTypeface(customNormal);
//            btnCancel.setTypeface(customNormal);

            fuguTvMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onItemLongClick(fuguLlRoot, itemView, getAdapterPosition(), false);
                    return true;
                }
            });

            fuguLlRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onItemLongClick(fuguLlRoot, itemView, getAdapterPosition(), false);
                    return true;
                }
            });

        }
    }

    class ForumViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot;
        private RecyclerView rvDataForm;
        private ImageView userImage;
        private RelativeLayout llMessageBg;
        private LinearLayout lllist;
        private RelativeLayout skip_layout;
        private Button btnSkip;
        private TextView tvUserName;

        ForumViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            rvDataForm = itemView.findViewById(R.id.rvDataForm);
            userImage = itemView.findViewById(R.id.user_image);
            llMessageBg = itemView.findViewById(R.id.llMessageBg);
            lllist = itemView.findViewById(R.id.lllist);
            skip_layout = itemView.findViewById(R.id.skip_layout);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            tvUserName = itemView.findViewById(R.id.tvUserName);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRatingListener != null)
                        onRatingListener.onSkipForm(fuguItems.get(getAdapterPosition()));
                }
            });
        }
    }

    class ForumTicketViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot;
        private RecyclerView rvDataForm;
        private ImageView userImage;
        private RelativeLayout llMessageBg;
        private LinearLayout lllist;
        private RelativeLayout skip_layout;
        private Button btnSkip;
        private TextView tvUserName;

        ForumTicketViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            rvDataForm = itemView.findViewById(R.id.rvDataForm);
            userImage = itemView.findViewById(R.id.user_image);
            llMessageBg = itemView.findViewById(R.id.llMessageBg);
            lllist = itemView.findViewById(R.id.lllist);
            skip_layout = itemView.findViewById(R.id.skip_layout);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            tvUserName = itemView.findViewById(R.id.tvUserName);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRatingListener != null)
                        onRatingListener.onSkipForm(fuguItems.get(getAdapterPosition()));
                }
            });
        }
    }

    class SimpleTextView extends RecyclerView.ViewHolder {

        private TextView tvText;
        private ImageView userImage;

        public SimpleTextView(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvMsg);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView listView;
        private CardView cardview;
        private AppCompatImageView userImageView;
        private AppCompatTextView agentName;
        private TextView userSubCategory;
        private RelativeLayout cardLayout, fallbackLayout;
        private TextView tvAssignment;
        private RelativeLayout starLayout;
        private LinearLayout infoLayout;
        private TextView starText;
        private ImageView info;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            listView = itemView.findViewById(R.id.recyclerView);

            cardview = itemView.findViewById(R.id.cardView);
            userImageView = itemView.findViewById(R.id.userImage);
            agentName = itemView.findViewById(R.id.agentName);
            userSubCategory = itemView.findViewById(R.id.agentDesc);
            userSubCategory.setVisibility(View.VISIBLE);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            fallbackLayout = itemView.findViewById(R.id.fallbackLayout);
            tvAssignment = itemView.findViewById(R.id.tvAssignment);
            starLayout = itemView.findViewById(R.id.starLayout);
            starText = itemView.findViewById(R.id.starText);
            info = itemView.findViewById(R.id.info);
            infoLayout = itemView.findViewById(R.id.info_layout);
        }
    }

    class PaymentView extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private Button payBtn;
        private RelativeLayout selectPlan;
        private RelativeLayout secureLayout;
        private ImageView icon;
        private TextView title1, title2, tvDate;

        public PaymentView(@NonNull final View itemView, final OnRecyclerListener itemClickListener) {
            super(itemView);
            secureLayout = itemView.findViewById(R.id.secureLayout);
            recyclerView = itemView.findViewById(R.id.tagView);
            payBtn = itemView.findViewById(R.id.payBtn);
            selectPlan = itemView.findViewById(R.id.select_plan);
            icon = itemView.findViewById(R.id.icon);
            title1 = itemView.findViewById(R.id.title1);
            title2 = itemView.findViewById(R.id.title2);
            tvDate = itemView.findViewById(R.id.tvDate);

            tvDate.setText(Restring.getString(activity, R.string.hippo_select_a_plan));
            title1.setText(Restring.getString(activity, R.string.hippo_secure_payment));
            String titleTxt = activity.getString(R.string.app_name) + " " + Restring.getString(activity, R.string.app_guarantee);
            title2.setText(titleTxt);

            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int itemCount = mes;
                    itemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    class MultiSelectionView extends RecyclerView.ViewHolder {
        private LinearLayout llMessageBgLeft;
        private RelativeLayout rootRight;
        private LinearLayout llTime;
        private TextView tvUserName;
        private TextView tvMsg;
        private TextView tvTime;
        private ImageView userImage;

        private RecyclerView recyclerView;
        private Button selectBtn;

        public MultiSelectionView(@NonNull View itemView) {
            super(itemView);

            rootRight = itemView.findViewById(R.id.root_right);
            llMessageBgLeft = itemView.findViewById(R.id.llMessageBgLeft);
            llTime = itemView.findViewById(R.id.llTime);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);

            userImage = itemView.findViewById(R.id.user_image);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            selectBtn = itemView.findViewById(R.id.select_Btn);

            selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedCount = 0;
                    ArrayList<MultiSelectButtons> buttons = fuguItems.get(getAdapterPosition()).getCustomAction().getMultiSelectButtons();
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).getStatus() == 1) {
                            selectedCount += 1;
                        }
                    }
                    if (selectedCount == 0) {
                        String text = Restring.getString(activity, R.string.hippo_minimum_Multiselection);
                        ToastUtil.getInstance(activity).showToast(text);
                        return;
                    }
                    if (onUserConcent != null) {
                        onUserConcent.onMultiSelectionClicked(getAdapterPosition(), fuguItems.get(getAdapterPosition()));
                    }
                    //HippoLog.e("selected btn", "selected data = "+new Gson().toJson(fuguItems.get(getAdapterPosition())));
                }
            });

        }
    }

    class QuickReplyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView list_qr;
        private TextView title_view_text;
        private ImageView userImage;

        QuickReplyViewHolder(View itemView) {
            super(itemView);
            list_qr = itemView.findViewById(R.id.list_qr);
            title_view_text = itemView.findViewById(R.id.title_view_text);
            userImage = itemView.findViewById(R.id.user_image);
        }

    }

    class SelfVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        private LinearLayout llChat;
        private View dividerView;

        public SelfVideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            llChat = itemView.findViewById(R.id.llMessage);
            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        private LinearLayout llChat;
        private LinearLayoutCompat otherView;
        private ImageView userImage;
        private TextView tvUserName;
        private View callDivider;

        public VideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            llChat = itemView.findViewById(R.id.llMessage);
            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
            userImage = itemView.findViewById(R.id.user_image);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            callDivider = itemView.findViewById(R.id.call_divider);
            otherView = itemView.findViewById(R.id.other_view);
        }
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {

        public MyCustomEditTextListener myCustomEditTextListener;
        private LinearLayout rootView, ratingView1, ratingView2;
        private TextView title, userName, selectedStar, comment, thanks;
        private ImageView userImage;
        private EditText editText;
        private Button sendBtn;
        private RatingBar ratingView;


        public RatingViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            rootView = itemView.findViewById(R.id.llRoot);
            ratingView1 = itemView.findViewById(R.id.ratingView1);
            ratingView2 = itemView.findViewById(R.id.ratingView2);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.tvUserName);
            title = itemView.findViewById(R.id.title);
            editText = itemView.findViewById(R.id.ed_rating_txt);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);
            ratingView = itemView.findViewById(R.id.ratingView);
            sendBtn = itemView.findViewById(R.id.sendBtn);
            selectedStar = itemView.findViewById(R.id.selectedStar);
            comment = itemView.findViewById(R.id.comment);
            thanks = itemView.findViewById(R.id.thanks_text);
            sendBtn.setTypeface(customBold);
            title.setTypeface(customBold);
        }
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llGalleryButtonLayout;
        private TextView tvButton;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            llGalleryButtonLayout = itemView.findViewById(R.id.llGalleryButtonLayout);

        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;

        DateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

    }

    class ReceivedFileViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llRoot;
        private LinearLayoutCompat llMessage, llFile, llImages;
        private TextView tvUserName, tvFileName, tvFileSize, tvFileExtension, tvFileTime;
        private ImageView ivFileImage;
        private AppCompatImageView ivFileDownload, ivFilePlay, ivFileUpload;
        private ProgressWheel progressBar;
        private ImageView messageSourceType, messageSourceType1, userImage;


        public ReceivedFileViewHolder(@NonNull View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llMessage = itemView.findViewById(R.id.llMessage);
            llFile = itemView.findViewById(R.id.llFile);
            llImages = itemView.findViewById(R.id.llImages);

            userImage = itemView.findViewById(R.id.user_image);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvFileExtension = itemView.findViewById(R.id.tvFileExtension);
            tvFileTime = itemView.findViewById(R.id.tvFileTime);

            ivFileImage = itemView.findViewById(R.id.ivFileImage);
            ivFilePlay = itemView.findViewById(R.id.ivFilePlay);
            ivFileDownload = itemView.findViewById(R.id.ivFileDownload);
            ivFileUpload = itemView.findViewById(R.id.ivFileUpload);

            progressBar = itemView.findViewById(R.id.circle_progress);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);
        }
    }

    class SentFileViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat llRoot, llMessage, llImages;
        private TextView tvFileName, tvFileSize, tvFileExtension, tvFileTime;
        private AppCompatImageView ivFilePlay, ivFileDownload, ivFileUpload;
        private ImageView ivFileImage, ivMessageState;
        private ProgressWheel circleProgress;
        private ImageView messageSourceType, messageSourceType1;

        public SentFileViewHolder(@NonNull View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llMessage = itemView.findViewById(R.id.llMessage);
            llImages = itemView.findViewById(R.id.llImages);

            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvFileExtension = itemView.findViewById(R.id.tvFileExtension);
            tvFileTime = itemView.findViewById(R.id.tvFileTime);

            ivFileImage = itemView.findViewById(R.id.ivFileImage);
            ivFilePlay = itemView.findViewById(R.id.ivFilePlay);
            ivFileDownload = itemView.findViewById(R.id.ivFileDownload);
            ivFileUpload = itemView.findViewById(R.id.ivFileUpload);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);

            circleProgress = itemView.findViewById(R.id.circle_progress);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);
        }
    }

    class SelfVideoMessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llDownload;
        private LinearLayoutCompat llRoot, llImageMessage;
        private RelativeLayout rlImageMessage;
        private AppCompatImageView ivImageMsg, ivMessageState;
        private ImageView ivPlay, ivDownload;
        private AppCompatTextView tvImageTime;
        private TextView tvFileSize, tvImgWithText;
        private AppCompatButton btnRetry, btnCancel;
        private ProgressWheel circle_progress;
        private ImageView messageSourceType, messageSourceType1;

        public SelfVideoMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llImageMessage = itemView.findViewById(R.id.llImageMessage);
            llDownload = itemView.findViewById(R.id.llDownload);

            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            ivImageMsg = itemView.findViewById(R.id.ivImageMsg);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);

            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvImageTime = itemView.findViewById(R.id.tvImageTime);
            tvImgWithText = itemView.findViewById(R.id.tvImgWithText);
            circle_progress = itemView.findViewById(R.id.circle_progress);
            btnRetry = itemView.findViewById(R.id.btnRetry);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);

        }
    }

    class OtherVideoMessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llRoot;
        private LinearLayoutCompat llImageMessage;
        private LinearLayout llDownload;
        private TextView tvUserName, tvFileSize;
        private RelativeLayout rlImageMessage;
        private AppCompatImageView ivImageMsg;
        private AppCompatTextView tvImageTime, tvImgWithText;
        private ImageView ivPlay, ivDownload;
        private ProgressWheel progressBar;
        private ImageView messageSourceType, messageSourceType1, userImage;

        public OtherVideoMessageViewHolder(final View itemView, final OnRecyclerListener itemClickListener) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llImageMessage = itemView.findViewById(R.id.llImageMessage);
            llDownload = itemView.findViewById(R.id.llDownload);

            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            userImage = itemView.findViewById(R.id.user_image);

            ivImageMsg = itemView.findViewById(R.id.ivImageMsg);
            tvImageTime = itemView.findViewById(R.id.tvImageTime);
            tvImgWithText = itemView.findViewById(R.id.tvImgWithText);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivDownload = itemView.findViewById(R.id.ivDownload);

            progressBar = itemView.findViewById(R.id.circle_progress);
            messageSourceType = itemView.findViewById(R.id.message_source_type);
            messageSourceType1 = itemView.findViewById(R.id.message_source_type1);

            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(llRoot, itemView, getAdapterPosition());
                }
            });
        }
    }

    class NewLeadViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerView;

        public NewLeadViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//            recyclerView.setLayoutManager(layoutManager);
        }
    }


    private void showImageDialog(Activity activity, String imgUrl, ImageView imageView, Message message) {
        try {
            if (!Utils.preventMultipleClicks()) {
                return;
            }

            Intent profileImageIntent = new Intent(activity, ImageDisplayActivityNew.class);
            Image profileImage = new Image(message.getUrl(), message.getThumbnailUrl(), "imageOne", "", "");
            profileImageIntent.putExtra("image", profileImage);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    imageView, "imageOne");
            activity.startActivity(profileImageIntent, options.toBundle());


//            Intent imageIntent = new Intent(activity, ImageDisplayActivity.class);
//            Image image = new Image(message.getUrl(), message.getThumbnailUrl(), message.getMuid(), message.getSentAtUtc(), "");
//            imageIntent.putExtra("image", image);
//            activity.startActivity(imageIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface OnRatingListener {
        void onSubmitRating(String text, Message currentOrderItem, int position);

        void onRatingSelected(int rating, Message currentOrderItem);

        void onFormDataCallback(Message currentOrderItem);

        void onSkipForm(Message message);

        void onTicketdataCallback(Message currentOrderItem, int position);
    }

    private AdapterCallback adapterCallBack;

    public void setAdapterCallBack(AdapterCallback adapterCallBack) {
        this.adapterCallBack = adapterCallBack;
    }

    public interface AdapterCallback {
        public void showImageDialog(Context activity, String imgUrl, ImageView imageView, Message message);
    }

    public interface onVideoCall {
        void onVideoCallClicked(int callType);
    }

    public interface OnUserConcent {
        void onConcentClicked(int position, Message message, String actionId, String url);

        void onPaymentLink(int position, Message message, HippoPayment payment, String url);

        void onMultiSelectionClicked(int position, Message message);
    }

    private String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

    /**
     * Attach observers(broadcast) for progress updates
     *
     * @param attach boolean to set if observer has to be attacked or detached
     */
    public void attachObservers(Boolean attach) {
        if (attach) {
            attachObserver();
        } else {
            detachObserver();
        }
    }

    boolean receiverRegistered;
    private BroadcastReceiver mProgressReceiver = null;

    /**
     * Detach observer (unregister broadcast)
     */
    private void detachObserver() {
        receiverRegistered = false;
        try {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(mProgressReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Attach observer (register broadcast)
     */
    private void attachObserver() {
        if (mProgressReceiver == null) {
            if (receiverRegistered)
                return;
            initializeReciever();
        }
        LocalBroadcastManager.getInstance(activity).registerReceiver(mProgressReceiver,
                new IntentFilter(HIPPO_PROGRESS_INTENT));
        receiverRegistered = true;
    }

    /**
     * Initialize reciever to recieve broadcasts to update progress bar
     */
    private void initializeReciever() {
        mProgressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int position = intent.getIntExtra(FuguAppConstant.HIPPO_POSITION, 0);
                            String muid = intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID);
                            Message message = fuguItems.get(position);
                            if (message.getMuid().equals(muid)) {
                                checkHolderAndUpdateProgress(message, position);
                            } else {
                                for (int i = fuguItems.size() - 1; i > 0; i--) {
                                    message = fuguItems.get(position);
                                    if (message.getMuid().equals(muid)) {
                                        position = i;
                                        checkHolderAndUpdateProgress(message, position);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //check holder type and update progress accordingly
                    private void checkHolderAndUpdateProgress(Message currentOrderItem, int position) {
                        //CommonData.updateDownloadMap(intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID), position);

                        currentOrderItem.setCurrentprogress(intent.getIntExtra(FuguAppConstant.HIPPO_PROGRESS, 0));
                        currentOrderItem.setUploadStatus(intent.getIntExtra(FuguAppConstant.HIPPO_STATUS_UPLOAD, 1));


                        if (recyclerView.findViewHolderForAdapterPosition(position) instanceof ReceivedFileViewHolder) {
                            setOtherHolderFileProgress(currentOrderItem, position);
                        } else if (recyclerView.findViewHolderForAdapterPosition(position) instanceof SentFileViewHolder) {
                            setSelfHolderFileProgress(currentOrderItem, position);
                        } else if (recyclerView.findViewHolderForAdapterPosition(position) instanceof SelfVideoMessageViewHolder) {
                            setSelfHolderVideoProgress(currentOrderItem, position);
                        } else if (recyclerView.findViewHolderForAdapterPosition(position) instanceof OtherVideoMessageViewHolder) {
                            setOtherHolderVideoProgress(currentOrderItem, position);
                        }
                    }

                    private void setOtherHolderImageProgress(Message currentOrderItem, int position) {
//                        OtherImageMessageViewHolder otherImageMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as OtherImageMessageViewHolder
//                        otherImageMessageViewHolder.circleProgress.setVisibility(View.VISIBLE)
//                        otherImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        otherImageMessageViewHolder.circleProgress.setTextSize(0f)
//                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
//                        notifyItemChanged(position)
                    }

                    private void setSelfHolderImageProgress(Message currentOrderItem, int position) {
//                        val selfImageMessageViewHolder: SelfImageMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as SelfImageMessageViewHolder
//                        currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
//                        selfImageMessageViewHolder.circleProgress.setVisibility(View.VISIBLE)
//                        selfImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        selfImageMessageViewHolder.circleProgress.setTextSize(0f)
//                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
//                        notifyItemChanged(position)
                    }

                    private void setOtherHolderVideoProgress(Message currentOrderItem, int position) {
                        OtherVideoMessageViewHolder otherImageMessageViewHolder = (OtherVideoMessageViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        currentOrderItem.setCurrentprogress(intent.getIntExtra(FuguAppConstant.HIPPO_PROGRESS, 0));
//                        otherImageMessageViewHolder.progress.setVisibility(View.VISIBLE)
                        otherImageMessageViewHolder.llDownload.setVisibility(View.GONE);
//                        otherImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        otherImageMessageViewHolder.circleProgress.setTextSize(0f)
                        currentOrderItem.setDownloadStatus(intent.getIntExtra(FuguAppConstant.HIPPO_STATUS_UPLOAD, 1));
                        notifyItemChanged(position);
                    }

                    private void setSelfHolderVideoProgress(Message currentOrderItem, int position) {
                        SelfVideoMessageViewHolder selfImageMessageViewHolder = (SelfVideoMessageViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        currentOrderItem.setCurrentprogress(intent.getIntExtra(FuguAppConstant.HIPPO_PROGRESS, 0));
                        selfImageMessageViewHolder.circle_progress.setVisibility(View.VISIBLE);
//                        selfImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        selfImageMessageViewHolder.circleProgress.setTextSize(0f)
                        currentOrderItem.setDownloadStatus(intent.getIntExtra(FuguAppConstant.HIPPO_STATUS_UPLOAD, 1));
                        notifyItemChanged(position);
                    }

                    private void setOtherHolderFileProgress(Message currentOrderItem, int position) {
                        ReceivedFileViewHolder otherFileMessageViewHolder = (ReceivedFileViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        currentOrderItem.setCurrentprogress(intent.getIntExtra(FuguAppConstant.HIPPO_PROGRESS, 0));
                        otherFileMessageViewHolder.progressBar.setVisibility(View.VISIBLE);
//                        otherFileMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        otherFileMessageViewHolder.circleProgress.setTextSize(0f)
                        otherFileMessageViewHolder.ivFilePlay.setVisibility(View.GONE);
                        notifyItemChanged(position);
                    }

                    private void setSelfHolderFileProgress(Message currentOrderItem, int position) {
                        SentFileViewHolder selfFileMessageViewHolder = (SentFileViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        currentOrderItem.setCurrentprogress(intent.getIntExtra(FuguAppConstant.HIPPO_PROGRESS, 0));
                        selfFileMessageViewHolder.circleProgress.setVisibility(View.VISIBLE);
                        selfFileMessageViewHolder.ivFilePlay.setVisibility(View.GONE);
//                        selfFileMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        selfFileMessageViewHolder.circleProgress.setTextSize(0f)
                        currentOrderItem.setDownloadStatus(intent.getIntExtra(FuguAppConstant.HIPPO_STATUS_UPLOAD, 1));
                        notifyItemChanged(position);
                    }
                });
            }
        };

    }

    private boolean onLongClick = false;

    public void setOnLongClickValue(Boolean onLongClick) {
        this.onLongClick = onLongClick;
    }

    private void setTextMessage(TextView tvMessage, String message) {
        /*String[] textArray = message.toString().split(" ");
        String text = "";
        for (String msg : textArray) {
            if (msg.toLowerCase().contains("http") || msg.toLowerCase().contains("www")) {
                text = text + " <a href=\"" + msg + "\">" + msg + "</a> ";
            } else {
                text = text + " " + msg;
            }
        }*/
        String text = message;
        text = text.replace("Http", "http");
        text = text.replace("Https", "https");
        text = text.replace("WWW", "www");

        text = text.trim().replace("\n", "<br>");
        Spanned var14;
        Spannable var15;
        if (Build.VERSION.SDK_INT >= 24) {
            var14 = Html.fromHtml(text.toString(), 256);
            if (var14 == null) {
                tvMessage.setText(message);
                return;
                //throw new RuntimeException(("null cannot be cast to non-null type android.text.Spannable");
            }

            var15 = (Spannable) var14;
        } else {
            var14 = Html.fromHtml(text.toString());
            if (var14 == null) {
                tvMessage.setText(message);
                return;//throw new TypeCastException("null cannot be cast to non-null type android.text.Spannable");
            }

            var15 = (Spannable) var14;
        }

        Spannable s = var15;
        URLSpan[] var9 = s.getSpans(0, s.length(), URLSpan.class);
        int var10 = var9.length;

        //Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }

        tvMessage.setLinkTextColor(hippoColorConfig.getHippoUrlLinkText());
        tvMessage.setText((CharSequence) s);
        tvMessage.setMovementMethod(BetterLinkMovementMethod.getInstance());
        SpannableString ss1 = new SpannableString((CharSequence) s);
        ss1.setSpan(new RelativeSizeSpan(1.0F), 0, s.length(), 0);
        tvMessage.setText((CharSequence) ss1);
        BetterLinkMovementMethod.linkifyHtmlNone(tvMessage).setOnLinkClickListener(this.urlClickListener);
        //BetterLinkMovementMethod.linkifyHtmlNone(tvMessage).setOnLinkLongClickListener(this.urlClickListenerLong).setOnLinkClickListener(this.urlClickListener);

    }

    boolean onLinkLongClick = false;
    private BetterLinkMovementMethod.OnLinkLongClickListener urlClickListenerLong = new BetterLinkMovementMethod.OnLinkLongClickListener() {
        @Override
        public boolean onLongClick(TextView textView, String url) {
            /*if (url.toLowerCase().contains("http") || url.toLowerCase().contains("www")) {
                onLinkLongClick = true;
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", url);
                clipboard.setPrimaryClip(clip);
                String text = Restring.getString(activity, R.string.hippo_copy_to_clipboard);
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }*/
            return true;
        }
    };
    private BetterLinkMovementMethod.OnLinkClickListener urlClickListener = new BetterLinkMovementMethod.OnLinkClickListener() {
        @Override
        public boolean onClick(TextView textView, String url) {
            if (!onLongClick) {
                if (url.toLowerCase().contains("http") || url.toLowerCase().contains("www")) {
                    String clickableLink = url;
                    clickableLink = clickableLink.replace("<b>", "");
                    clickableLink = clickableLink.replace("<i>", "");
                    clickableLink = clickableLink.replace("</i>", "");
                    clickableLink = clickableLink.replace("</b>", "");
                    clickableLink = clickableLink.replace("</br>", "");
                    clickableLink = clickableLink.replace("<br>", "");
                    try {

                        if (clickableLink.startsWith("www")) {
                            clickableLink = "http://" + clickableLink;
                        }

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(clickableLink));
                        activity.startActivity(i);
                    } catch (Exception e) {

                    }
                }
            }
            return true;
        }
    };

    private RequestOptions options;

    private RequestOptions getRequestOptions(String name) {
        if (options == null) {
            options = new RequestOptions()
                    .centerCrop()
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transform(new CenterCrop(), new RoundedCorners(10))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
        }

        TextDrawable drawable = null;
        try {
            Resources r = activity.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

            name = name.trim();
            char text = ' ';

            text = name.charAt(0);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(text);

            drawable = TextDrawable.builder()
                    .buildRoundRect((text + "").toUpperCase(), color, Math.round(px));

            options.placeholder(drawable);
            options.error(drawable);
        } catch (Exception e) {
            options.placeholder(R.drawable.hippo_placeholder);
            options.error(R.drawable.hippo_placeholder);
        }


        options.placeholder(drawable);
        options.error(drawable);
        return options;
    }

    private void setUserName(TextView userName) {
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL_NAME;
        int color = colorGenerator.getColor(userName.getText().toString().trim());
        userName.setTextColor(color);
    }

    private void loadUserImage(ImageView imageView, String name, String url) {
        Glide.with(activity).asBitmap()
                .apply(getRequestOptions(name))
                .load(url)
                .into(imageView);
    }

    private void loadCallUserImage(int position, Message currentOrderItem, TextView tvUserName,
                                   LinearLayoutCompat llRoot, ImageView userImage) {
        tvUserName.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);

        int typePre = 0;
        if (position > 0)
            typePre = getItemViewType(position - 1);

        boolean hasUserImage = currentOrderItem.isHasImageView() || typePre == HIPPO_USER_CONCENT_VIEW;
        int bottom = dp2();
        int top = dp2();
        int left = dp40();
        if (hasUserImage) {

            String userNameText = "";
            if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                userNameText = currentOrderItem.getfromName();
            } else {
                userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                        : Restring.getString(activity, R.string.fugu_support);
            }

            if (!TextUtils.isEmpty(userNameText)) {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(userNameText);
            }

            String imageUrl = currentOrderItem.getUserImage();
            if (currentOrderItem.getUserType() == 0 || (currentOrderItem.getOriginalMessageType() == BOT_TEXT_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 16 ||
                    currentOrderItem.getOriginalMessageType() == BOT_FORM_MESSAGE ||
                    currentOrderItem.getOriginalMessageType() == 20)) {
                imageUrl = getBotImage();
            }

            if (chatType != p2pChatType) {
                userImage.setVisibility(View.VISIBLE);
                left = 0;
                loadUserImage(userImage, userNameText, imageUrl);
            } else {
                left = dp8();
                userImage.setVisibility(View.GONE);
            }
            top = dp15();
        } else if (chatType == p2pChatType) {
            left = dp8();
            userImage.setVisibility(View.GONE);
        }

        HippoLog.e("Hippo", "Hippo bottom = " + bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            llRoot.setPaddingRelative(left, top, 0, bottom);
        } else {
            llRoot.setPadding(left, top, 0, bottom);
        }
    }

    public void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void onItemLongClick(View viewClicked, final View parentView, int position, boolean isRight) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!onLinkLongClick) {
                    int positionInList = recyclerView.getChildLayoutPosition(parentView);
                    if (positionInList != RecyclerView.NO_POSITION) {
                        if (!Utils.preventMultipleClicks()) {
                            return;
                        }
                        FuguChatActivity chatActivity = (FuguChatActivity) activity;
                        chatActivity.hideKeyboard(chatActivity);
                        Message message = fuguItems.get(positionInList);
                        if (message.getMessageState() == 4) {
                            return;
                        }
                        BottomSheetMsgFragment sheetMsgFragment = BottomSheetMsgFragment.newInstance(positionInList, message, message.getUserId().compareTo(HippoConfig.getInstance().getUserData().getUserId()) == 0,
                                false, message.getMessageState() == 4);
                        sheetMsgFragment.show(chatActivity.getSupportFragmentManager(), BottomSheetMsgFragment.class.getSimpleName());
                    }
                } else {
                    onLinkLongClick = false;
                }
            }
        }, 100);

    }

}
//package com.hippo.adapter;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.res.Resources;
//import android.graphics.PorterDuff;
//import android.graphics.Typeface;
//import android.graphics.drawable.GradientDrawable;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.RequestOptions;
//import com.hippo.HippoColorConfig;
//import com.hippo.HippoConfig;
//import com.hippo.R;
//import com.hippo.constant.FuguAppConstant;
//import com.hippo.database.CommonData;
//import com.hippo.datastructure.ChannelStatus;
//import com.hippo.langs.Restring;
//import com.hippo.model.FuguConversation;
//import com.hippo.utils.ColorGenerator;
//import com.hippo.utils.DateUtils;
//import com.hippo.utils.HippoLog;
//import com.hippo.utils.TextDrawable;
//
//import java.util.ArrayList;
//
//import static com.hippo.constant.FuguAppConstant.*;
//import static com.hippo.constant.FuguAppConstant.MESSAGE_UNSENT;
//
///**
// * Created by gurmail on 2019-12-09.
// *
// * @author gurmail
// * @deprecated
// */
//public class HippoChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private LayoutInflater inflater;
//    private ArrayList<Object> fuguConversationList = new ArrayList<>();
//    private Activity activity;
//    private String userName;
//    private String businessName;
//    private Long userId = -1L;
//    private String enUserId = "";
//    private HippoColorConfig hippoColorConfig;
//    private Callback callback;
//    private RequestOptions options;
//
//    private static final int TYPE_ITEM = 1;
//    public static final int ITEM_PROGRESS_BAR = 3;
//
//    //FuguConversation
//
//    public HippoChannelsAdapter(Activity activity, ArrayList<Object> fuguConversationList, String userName, Long userId, String businessName, Callback callback, String enUserId) {
//        inflater = LayoutInflater.from(activity.getApplicationContext());
//
//        hippoColorConfig = CommonData.getColorConfig();
//        this.fuguConversationList = fuguConversationList;
//        this.activity = activity;
//        this.userName = userName;
//        this.businessName = businessName;
//        this.userId = userId;
//        this.callback = callback;
//        this.enUserId = enUserId;
//    }
//
//    private RequestOptions getRequestOptions(String name) {
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//        int color = generator.getColor(name.trim());
//        Resources r = activity.getResources();
//        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//        char text = name.trim().charAt(0);
//
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRoundRect((text + "").toUpperCase(), color, Math.round(px));
//        if (options == null) {
//            options = new RequestOptions()
//                    .circleCrop()
//                    .dontAnimate()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL);
//        }
//
//
//        options.placeholder(drawable);
//        options.error(drawable);
//        return options;
//    }
//
//    public void updateList(ArrayList<Object> fuguConversationList) {
//        this.fuguConversationList = fuguConversationList;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == ITEM_PROGRESS_BAR) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hippo_custom_loading_list_item, parent, false);
//            return new ProgressBarViewHolder(v);
//        } else {
//            View view = inflater.inflate(R.layout.fugu_item_channels, parent, false);
//            return new ChannelViewHolder(view);
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        if (holder instanceof ChannelViewHolder) {
//            try {
//                final ChannelViewHolder channelViewHolder = (ChannelViewHolder) holder;
//                final FuguConversation currentChannelItem = (FuguConversation) fuguConversationList.get(position);
//
//                channelViewHolder.tvChannelName.setText(currentChannelItem.getLabel());
//                channelViewHolder.tvChannelName.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
//                channelViewHolder.tvMessage.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
//                if (currentChannelItem.getMessage_type() == VIDEO_CALL) {
//                    channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                    channelViewHolder.tvMessage.setText(Html.fromHtml(getMessageData(currentChannelItem)));
//                } else if (TextUtils.isEmpty(currentChannelItem.getMessage())) {
//                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
//                        {
//                            if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
//                                if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
//                                    channelViewHolder.tvMessage.setText("You: " + activity.getString(R.string.fugu_attachment));
//                                } else if (currentChannelItem.getMessage_type() == FILE_MESSAGE) {
//                                    channelViewHolder.tvMessage.setText("You: " + activity.getString(R.string.hippo_attachment_file));
//                                } else {
//                                    channelViewHolder.tvMessage.setText("You sent a message");
//                                }
//                                channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
//                                if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
//                                } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
//                                } else {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
//                                }
//                            } else {
//                                if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
//                                    channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + activity.getString(R.string.fugu_attachment));
//                                } else if (currentChannelItem.getMessage_type() == FILE_MESSAGE) {
//                                    channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + activity.getString(R.string.hippo_attachment_file));
//                                } else {
//                                    channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + " " + "sent a message");
//                                }
//                                channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                } else {
//                    if (currentChannelItem.getMessage().contains("\n")) {
//                        channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getMessage().replaceAll("\n", " ")));
//                        if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
//                            HippoLog.e("error", HippoConfig.getInstance().getUserData().getUserId() + "");
//                            HippoLog.e("error", currentChannelItem.getUserId() + "");
//                            if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml("You: " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
//                                if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
//                                } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
//                                } else {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
//                                }
//                            } else {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                            }
//                        } else {
//                            if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml("You: " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
//                                if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
//                                } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
//                                } else {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
//                                }
//                            } else {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                            }
//                        }
//                    } else {
//                        if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
//                            HippoLog.e("error", HippoConfig.getInstance().getUserData().getUserId() + "");
//                            HippoLog.e("error", currentChannelItem.getUserId() + "");
//                            if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml("You: " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
//                                if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
//                                } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
//                                } else {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
//                                }
//                            } else {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                            }
//                        } else {
//                            if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml("You: " + currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
//                                if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
//                                } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
//                                } else {
//                                    channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
//                                }
//                            } else {
//                                channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getMessage()));
//                                channelViewHolder.ivMessageState.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                }
////            }
//
//                if (currentChannelItem.getUnreadCount() > 0) {
//                    channelViewHolder.tvChannelName.setTypeface(Typeface.DEFAULT_BOLD);
//                    channelViewHolder.tvMessage.setTypeface(Typeface.DEFAULT_BOLD);
//                    channelViewHolder.circularTvMessageCount.setVisibility(View.VISIBLE);
//                    channelViewHolder.circularTvMessageCount.setText(String.valueOf(currentChannelItem.getUnreadCount()));
//
//                    channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
//
//                } else {
//                    channelViewHolder.tvChannelName.setTypeface(Typeface.DEFAULT);
//                    channelViewHolder.tvMessage.setTypeface(Typeface.DEFAULT);
//                    channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
//                    channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChannelDateText());
//                }
//
//                if (currentChannelItem.getChannelImage() == null || currentChannelItem.getChannelImage().trim().isEmpty()) {
//                    channelViewHolder.ivChannelIcon.setVisibility(View.GONE);
//
//                    channelViewHolder.tvChannelIcon.setText(currentChannelItem.getLabel().trim().substring(0, 1).toUpperCase());
//                    channelViewHolder.tvChannelIcon.setVisibility(View.VISIBLE);
//
//                    Glide.with(activity).clear(channelViewHolder.ivChannelIcon);
//
//                    channelViewHolder.ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.hippo_ring_grey));
//
//                    channelViewHolder.ivChannelIcon.getDrawable()
//                            .setColorFilter(hippoColorConfig.getHippoChannelDateText(), PorterDuff.Mode.SRC_ATOP);
//                    channelViewHolder.tvChannelIcon.setTextColor(hippoColorConfig.getHippoChannelItemBg());
//
//                    GradientDrawable tvBackground = (GradientDrawable) channelViewHolder.tvChannelIcon.getBackground();
//                    tvBackground.setColor(hippoColorConfig.getHippoChannelDateText());
//
//                } else {
//
//                    String name = currentChannelItem.getLabel().trim().substring(0, 1).toUpperCase();
//                    Glide.with(activity).asBitmap()
//                            .apply(getRequestOptions(name))
//                            .load(currentChannelItem.getChannelImage())
//                            .into(channelViewHolder.ivChannelIcon);
//
//                    channelViewHolder.ivChannelIcon.setVisibility(View.VISIBLE);
//                    channelViewHolder.tvChannelIcon.setVisibility(View.GONE);
//                }
//
//                //if (currentChannelItem.getChannelId().compareTo(-1L) == 0) {
//                try {
//                    if (currentChannelItem.getChannelId() == null || currentChannelItem.getChannelId().intValue() < 0) {
//                        channelViewHolder.tvDate.setVisibility(View.GONE);
//                    } else {
//                        channelViewHolder.tvDate.setText(DateUtils.getRelativeDate(DateUtils.getInstance().convertToLocal(currentChannelItem.getDateTime()), true));
//                        channelViewHolder.tvDate.setVisibility(View.VISIBLE);
//                    }
//                } catch (Exception e) {
//                    channelViewHolder.tvDate.setVisibility(View.GONE);
//                }
//
//                if (currentChannelItem.getChannelStatus() == ChannelStatus.CLOSED.getOrdinal()
//                        && currentChannelItem.getLabelId() < 0) {
//                    channelViewHolder.vClosed.setVisibility(View.VISIBLE);
//                } else {
//                    channelViewHolder.vClosed.setVisibility(View.GONE);
//                }
//
//                channelViewHolder.rlRoot.setBackgroundDrawable(HippoColorConfig
//                        .makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));
//
//                channelViewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
//                        currentChannelItem.setUnreadCount(0);
//
//                        FuguConversation conversation = new FuguConversation();
//                        conversation.setLabel(currentChannelItem.getLabel());
//                        conversation.setChannelId(currentChannelItem.getChannelId());
//                        conversation.setLabelId(currentChannelItem.getLabelId());
//                        conversation.setDefaultMessage(currentChannelItem.getMessage());
//                        conversation.setChannelStatus(currentChannelItem.getChannelStatus());
//                        conversation.setChannelImage(currentChannelItem.getChannelImage());
//                        conversation.setBusinessName(businessName);
//                        conversation.setUserId(userId);
//                        conversation.setEnUserId(enUserId);
//                        conversation.setOpenChat(true);
//                        conversation.setUserName(userName);
//                        conversation.setIsTimeSet(1);
//                        conversation.setChatType(currentChannelItem.getChatType());
//                        conversation.setStatus(currentChannelItem.getStatus());
//                        conversation.setLast_sent_by_id(currentChannelItem.getLast_sent_by_id());
//                        callback.onClick(conversation);
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            ProgressBarViewHolder progressBarViewHolder = (ProgressBarViewHolder) holder;
//            progressBarViewHolder.textView.setText(Restring.getString(activity, R.string.fugu_loading));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (fuguConversationList == null || fuguConversationList.size() == 0) {
//            return 0;
//        } else {
//            return fuguConversationList.size();
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (fuguConversationList.get(position) instanceof HippoChannelsAdapter.ProgressBarItem) {
//            return ITEM_PROGRESS_BAR;
//        } else {
//            return TYPE_ITEM;
//        }
//    }
//
//    class ChannelViewHolder extends RecyclerView.ViewHolder {
//        private RelativeLayout rlRoot;
//        private TextView tvChannelName, tvMessage, tvDate, tvChannelIcon;
//        private ImageView ivChannelIcon, ivMessageState;
//        private TextView circularTvMessageCount;
//        private View vClosed;
//
//        ChannelViewHolder(View itemView) {
//            super(itemView);
//            rlRoot = itemView.findViewById(R.id.rlRoot);
//            tvChannelName = itemView.findViewById(R.id.tvChannelName);
//            tvMessage = itemView.findViewById(R.id.tvMessage);
//            tvDate = itemView.findViewById(R.id.tvDate);
//            ivChannelIcon = itemView.findViewById(R.id.ivChannelIcon);
//            ivMessageState = itemView.findViewById(R.id.ivMessageState);
//            circularTvMessageCount = itemView.findViewById(R.id.circularTvMessageCount);
//            vClosed = itemView.findViewById(R.id.vClosed);
//        }
//    }
//
//    public interface Callback {
//        void onClick(FuguConversation conversation);
//    }
//
//    /*private String getMessageData(FuguConversation currentChannelItem) {
//        String message = "The video call ended";
//        String callType = "video";
//        if(!TextUtils.isEmpty(currentChannelItem.getCallType()) && currentChannelItem.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
//            callType = "voice";
//        }
//        if(currentChannelItem.getMessageState() != null && currentChannelItem.getMessageState().intValue() == 2) {
//            if (currentChannelItem.getLast_sent_by_id().equals(HippoConfig.getInstance().getUserData().getUserId())) {
//                message = "Customer missed a " + callType + " call with you";
//            } else {
//                message = "You missed a " + callType + " call with "+currentChannelItem.getLast_sent_by_full_name();
//            }
//        } else {
//            message = "The " + callType + " call ended";
//        }
//
//        return message;
//    }*/
//
//    private String getMessageData(FuguConversation currentChannelItem) {
//        String message = "The video call ended";
//        String video = Restring.getString(activity, R.string.fugu_video);
//        String callType = video;
//        if(!TextUtils.isEmpty(currentChannelItem.getCallType()) && currentChannelItem.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
//            String voice = Restring.getString(activity, R.string.fugu_voice);
//            callType = voice;
//        }
//        if(currentChannelItem.getMessageState() != null && currentChannelItem.getMessageState().intValue() == 2) {
//            if (currentChannelItem.getLast_sent_by_id().equals(HippoConfig.getInstance().getUserData().getUserId())) {
//                String customer_missed_call = Restring.getString(activity, R.string.hippo_customer_missed_a);
//                String call_with_you = Restring.getString(activity, R.string.hippo_call_with_you);
//                message = customer_missed_call+" " + callType + " "+call_with_you;
//            } else {
//                String you_missed_a = Restring.getString(activity, R.string.hippo_you_missed_a);
//                String call_with = Restring.getString(activity, R.string.hippo_call_with);
//                message = you_missed_a+" " + callType + " "+call_with+" "+currentChannelItem.getLast_sent_by_full_name();
//            }
//        } else {
//            String the = Restring.getString(activity, R.string.hippo_the);
//            String call_ended = Restring.getString(activity, R.string.hippo_call_ended);
//            message = the+" " + callType + " "+call_ended;
//        }
//        return message;
//    }
//
//    public static class ProgressBarItem {
//
//    }
//
//    private class ProgressBarViewHolder extends RecyclerView.ViewHolder {
//        private TextView textView;
//        public ProgressBarViewHolder(View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.tv_loading_text);
//        }
//    }
//}
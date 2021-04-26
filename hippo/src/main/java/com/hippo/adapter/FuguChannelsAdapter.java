package com.hippo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.datastructure.ChannelStatus;
import com.hippo.langs.Restring;
import com.hippo.model.FuguConversation;
import com.hippo.utils.*;

import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hippo.constant.FuguAppConstant.*;

/**
 * Created by Bhavya Rattan on 08/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();
    private Activity activity;
    private String userName;
    private String businessName;
    private Long userId = -1L;
    private String enUserId = "";
    private HippoColorConfig hippoColorConfig;
    private Callback callback;
    private RequestOptions options;
    private Typeface customBold, customNormal;

    public FuguChannelsAdapter(Activity activity, ArrayList<FuguConversation> fuguConversationList,
                               String userName, Long userId, String businessName, Callback callback, String enUserId, boolean hasButton) {
        inflater = LayoutInflater.from(activity.getApplicationContext());

        hippoColorConfig = CommonData.getColorConfig();
        this.fuguConversationList = fuguConversationList;
        this.activity = activity;
        this.userName = userName;
        this.businessName = businessName;
        this.userId = userId;
        this.callback = callback;
        this.enUserId = enUserId;
        customBold = Typeface.createFromAsset(activity.getAssets(),  "fonts/ProximaNova-Sbold.ttf");
        customNormal = Typeface.createFromAsset(activity.getAssets(),  "fonts/ProximaNova-Reg.ttf");
    }

    private RequestOptions getRequestOptions(String name) {
        Resources r = activity.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, r.getDisplayMetrics());

        name = name.trim();
        char text = ' ';

        if(TextUtils.isEmpty(name)) {
            String bname = CommonData.getUserDetails().getData().getBusinessName();
            text = bname.trim().charAt(0);
        } else {
            text = name.trim().charAt(0);
        }

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(text);
        Log.w("color", text+" color = "+color);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(size) // size of text in pixels
                .endConfig()
                .buildRoundRect((text + "").toUpperCase(), color, Math.round(px));
        if (options == null) {
            options = new RequestOptions()
                    .centerCrop()
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transform(new CenterCrop(), new RoundedCorners(10))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
        }

        options.placeholder(drawable);
        options.error(drawable);
        return options;
    }

    public void updateList(ArrayList<FuguConversation> fuguConversationList) {
        this.fuguConversationList = fuguConversationList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_channels, parent, false);
        return new ChannelViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final ChannelViewHolder channelViewHolder = (ChannelViewHolder) holder;
            final FuguConversation currentChannelItem = fuguConversationList.get(position);

            channelViewHolder.tvChannelName.setTextColor(hippoColorConfig.getHippoListTextColorPrimary());
            channelViewHolder.tvMessage.setTextColor(hippoColorConfig.getHippoListTextColorSecondary());

            String message = currentChannelItem.getMessage();
            String label = currentChannelItem.getLabel();

            try {
                if(!TextUtils.isEmpty(currentChannelItem.getMultiLangMessage())) {
                    Pattern pattern = Pattern.compile("\\{\\{\\{(.*?)\\}\\}\\}");
                    Matcher matcher = pattern.matcher(currentChannelItem.getMultiLangMessage());
                    if (matcher.find()) {
                        String key = matcher.group(1);
                        String value = Restring.getString(key);
                        if(!TextUtils.isEmpty(value)) {
                            String oldStr = "{{{"+ key +"}}}";
                            message = currentChannelItem.getMultiLangMessage().replace(oldStr, value);
                        }
                    }
                }
            } catch (Exception e) {

            }

            if(currentChannelItem.getChannelType() == 2 && currentChannelItem.getChannelId() < 0 && currentChannelItem.getOtherLangData() != null
            && currentChannelItem.getOtherLangData().getLangCode().equalsIgnoreCase(HippoConfig.getInstance().getCurrentLanguage())) {
                message = currentChannelItem.getOtherLangData().getChannelMessage();
                label = currentChannelItem.getOtherLangData().getChannelName();
            }

            channelViewHolder.tvChannelName.setText(label);
            if(currentChannelItem.getMessageState() != null && currentChannelItem.getMessageState() == 4) {
                String you = Restring.getString(activity, R.string.hippo_you);
                String messageTxt = Restring.getString(activity, R.string.hippo_message_deleted);
                if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) != 0) {
                    you = currentChannelItem.getLast_sent_by_full_name().trim();
                }
                //String messageStr = "<font  color='grey'><i>"+you+": "+messageTxt+" </i></font>";
                channelViewHolder.tvMessage.setText(you+": " + messageTxt);
            } else if(currentChannelItem.getMessage_type() == VIDEO_CALL) {
                channelViewHolder.tvMessage.setText(Html.fromHtml(getMessageData(currentChannelItem)));
            } else if (TextUtils.isEmpty(currentChannelItem.getMessage())) {
                if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                    {
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            String you = Restring.getString(activity, R.string.hippo_you);
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                String attached = Restring.getString(activity, R.string.fugu_attachment);
                                channelViewHolder.tvMessage.setText(you+": " + attached);
                            } else if(currentChannelItem.getMessage_type() == FILE_MESSAGE) {
                                String attached = Restring.getString(activity, R.string.hippo_attachment_file);
                                channelViewHolder.tvMessage.setText(you+": " + attached);
                            } else {
                                String sentMsg = Restring.getString(activity, R.string.hippo_sent_a_msg);
                                channelViewHolder.tvMessage.setText(you+" "+sentMsg);
                            }
                        } else {
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                String attached = Restring.getString(activity, R.string.fugu_attachment);
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + attached);
                            } else if(currentChannelItem.getMessage_type() == FILE_MESSAGE) {
                                String attached = Restring.getString(activity, R.string.hippo_attachment_file);
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + attached);
                            } else {
                                String sentMsg = Restring.getString(activity, R.string.hippo_sent_a_msg);
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + " " + sentMsg);
                            }
                        }
                    }
                }
            } else {
                String you = Restring.getString(activity, R.string.hippo_you);
                if (message.contains("\n")) {
                    channelViewHolder.tvMessage.setText(Html.fromHtml(message.replaceAll("\n", " ")));
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(you+": " + message));
                        } else {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + message));
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(you+": " + message));
                        } else {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(message));
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(you+": " + message));
                        } else {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + message));
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(you+": " + message));
                        } else {
                            channelViewHolder.tvMessage.setText(Html.fromHtml(message));
                        }
                    }
                }
            }


            try {
                channelViewHolder.tvChannelName.setTypeface(customBold);
                channelViewHolder.tvDate.setTypeface(customNormal);
                channelViewHolder.tvMessage.setTypeface(customNormal);
                channelViewHolder.circularTvMessageCount.setTypeface(customNormal);
            } catch (Exception e) {
                e.printStackTrace();
            }

            channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChannelDateText());

            if (currentChannelItem.getUnreadCount() > 0) {
                channelViewHolder.circularTvMessageCount.setVisibility(View.VISIBLE);
                channelViewHolder.circularTvMessageCount.setText(String.valueOf(currentChannelItem.getUnreadCount()));
            } else {
                channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                channelViewHolder.tvMessage.setTextColor(hippoColorConfig.getHippoChannelReadMessage());
                channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChannelReadTime());
                channelViewHolder.tvMessage.setTypeface(customBold);
            }

            String name = label.trim().substring(0, 1).toUpperCase();
            Glide.with(activity).asBitmap()
                    .apply(getRequestOptions(name))
                    .load(currentChannelItem.getChannelImage())
                    .into(channelViewHolder.ivChannelIcon);

            channelViewHolder.ivChannelIcon.setVisibility(View.VISIBLE);

            try {
                if (currentChannelItem.getChannelId() == null || currentChannelItem.getChannelId().intValue() < 0) {
                    channelViewHolder.tvDate.setVisibility(View.GONE);
                } else {
                    channelViewHolder.tvDate.setText(DateUtils.getRelativeDate(DateUtils.getInstance().convertToLocal(currentChannelItem.getDateTime()), true));
                    channelViewHolder.tvDate.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                channelViewHolder.tvDate.setVisibility(View.GONE);
            }

            channelViewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                    currentChannelItem.setUnreadCount(0);

                    String message = currentChannelItem.getMessage();
                    String label = currentChannelItem.getLabel();
                    if(currentChannelItem.getChannelType() == 2 && currentChannelItem.getChannelId() < 0 && currentChannelItem.getOtherLangData() != null
                            && currentChannelItem.getOtherLangData().getLangCode().equalsIgnoreCase(HippoConfig.getInstance().getCurrentLanguage())) {
                        message = currentChannelItem.getOtherLangData().getChannelMessage();
                        label = currentChannelItem.getOtherLangData().getChannelName();
                    }

                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabel(label);
                    conversation.setChannelId(currentChannelItem.getChannelId());
                    conversation.setLabelId(currentChannelItem.getLabelId());
                    conversation.setDefaultMessage(message);
                    conversation.setChannelStatus(currentChannelItem.getChannelStatus());
                    conversation.setChannelImage(currentChannelItem.getChannelImage());
                    conversation.setBusinessName(businessName);
                    conversation.setUserId(userId);
                    conversation.setEnUserId(enUserId);
                    conversation.setOpenChat(true);
                    conversation.setUserName(userName);
                    conversation.setIsTimeSet(1);
                    conversation.setChatType(currentChannelItem.getChatType());
                    conversation.setStatus(currentChannelItem.getStatus());
                    conversation.setLast_sent_by_id(currentChannelItem.getLast_sent_by_id());
                    callback.onClick(conversation);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return fuguConversationList.size();
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlRoot, mainLayout;
        private TextView tvChannelName, tvMessage, tvDate;
        private ImageView ivChannelIcon;
        private TextView circularTvMessageCount;
        private TextView closed;


        ChannelViewHolder(View itemView) {
            super(itemView);
            rlRoot = itemView.findViewById(R.id.rlRoot);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivChannelIcon = itemView.findViewById(R.id.ivChannelIcon);
            circularTvMessageCount = itemView.findViewById(R.id.circularTvMessageCount);
            closed = itemView.findViewById(R.id.closed);
            mainLayout = itemView.findViewById(R.id.main_layout);

            setTextSize(tvChannelName, 18);
            setTextSize(tvMessage, 15);
            setTextSize(tvDate, 12);
            setTextSize(circularTvMessageCount, 12);

        }
    }

    public void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    class footerView  extends RecyclerView.ViewHolder {
        public footerView(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface Callback {
        void onClick(FuguConversation conversation);
    }

    private String getMessageData(FuguConversation currentChannelItem) {
        String message = "The video call ended";
        String video = Restring.getString(activity, R.string.fugu_video);
        String callType = video;
        if(!TextUtils.isEmpty(currentChannelItem.getCallType()) && currentChannelItem.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
            String voice = Restring.getString(activity, R.string.fugu_voice);
            callType = voice;
        }
        if(currentChannelItem.getMessageState() != null && currentChannelItem.getMessageState().intValue() == 2) {
            if (currentChannelItem.getLast_sent_by_id().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                String customer_missed_call = Restring.getString(activity, R.string.hippo_customer_missed_a);
                String call_with_you = Restring.getString(activity, R.string.hippo_call_with_you);
                message = customer_missed_call+" " + callType + " "+call_with_you;
            } else {
                String you_missed_a = Restring.getString(activity, R.string.hippo_you_missed_a);
                String call_with = Restring.getString(activity, R.string.hippo_call_with);
                message = you_missed_a+" " + callType + " "+call_with+" "+currentChannelItem.getLast_sent_by_full_name();
            }
        } else {
            String the = Restring.getString(activity, R.string.hippo_the);
            String call_ended = Restring.getString(activity, R.string.hippo_call_ended);
            message = the+" " + callType + " "+call_ended;
        }
        return message;
    }


}


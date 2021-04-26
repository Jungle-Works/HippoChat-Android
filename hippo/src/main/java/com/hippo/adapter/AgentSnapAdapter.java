package com.hippo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hippo.R;
import com.hippo.model.Message;
import com.hippo.utils.RoundedCornersTransformation;
import com.hippo.utils.showmoretextview.ShowMoreTextView;

/**
 * Created by gurmail on 2019-10-21.
 *
 * @author gurmail
 */
public class AgentSnapAdapter extends RecyclerView.Adapter<AgentSnapAdapter.ViewHolder> implements OnRecyclerListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private AgentViewListener viewListener;
    private OnRecyclerListener itemClickListener;
    private Message message;


    public AgentSnapAdapter(Context mContext, Message message, AgentViewListener viewListener) {
        this.mContext = mContext;
        this.viewListener = viewListener;
        this.message = message;
        this.itemClickListener = itemClickListener;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.hippo_agent_snap_itam, viewGroup, false), this);
    }

    @Override
    public long getItemId(int position) {
        try {
            return Integer.parseInt(message.getContentValue().get(position).getCardId());
        } catch (NumberFormatException e) {
            return position;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.agentName.setText(message.getContentValue().get(pos).getTitle());
        String desc = message.getContentValue().get(pos).getDescription();
        if(pos == 0) {
            viewHolder.fakeView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.fakeView.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(desc)) {
            viewHolder.userSubCategory.setVisibility(View.GONE);
        } else {
            viewHolder.userSubCategory.setVisibility(View.VISIBLE);
            viewHolder.userSubCategory.setText(desc);
        }

        try {
            if(!TextUtils.isEmpty(message.getContentValue().get(pos).getRatingValue())) {
                viewHolder.starLayout.setVisibility(View.VISIBLE);
                viewHolder.starText.setText(""+message.getContentValue().get(pos).getRatingValue());
            } else {
                viewHolder.starLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.starLayout.setVisibility(View.GONE);
        }

        RequestOptions myOptions = RequestOptions
                .bitmapTransform(new RoundedCornersTransformation(mContext, 4, 1))
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.hippo_placeholder))
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ContextCompat.getDrawable(mContext, R.drawable.hippo_placeholder));
        Glide.with(mContext).load(message.getContentValue().get(pos).getImageUrl())
                .apply(myOptions)
                .into(viewHolder.userImageView);


    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(message != null && message.getContentValue() != null)
            size = message.getContentValue().size();
        return size;
        //return 10;
    }

    @Override
    public void onItemClick(View viewClicked, View parentView, int position) {

    }

    @Override
    public void onItemClick(View parentView, int position) {

    }

    @Override
    public void onItemLongClick(View viewClicked, View parentView, int position, boolean isRightConcent) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardview;
        private AppCompatImageView userImageView;
        private AppCompatTextView agentName;
        private ShowMoreTextView userSubCategory;
        private View fakeView;
        private RatingBar ratingBar;
        private RelativeLayout starLayout;
        private TextView starText;
        private ImageView info;
        private LinearLayout info_layout;


        public ViewHolder(@NonNull final View itemView, final OnRecyclerListener itemClickListener) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardView);
            userImageView = itemView.findViewById(R.id.userImage);
            agentName = itemView.findViewById(R.id.agentName);
            ratingBar = itemView.findViewById(R.id.ratingBar2);
            starLayout = itemView.findViewById(R.id.starLayout);
            starText = itemView.findViewById(R.id.starText);
            userSubCategory = itemView.findViewById(R.id.agentDesc);
            userSubCategory.setShowingLine(2);
            info = itemView.findViewById(R.id.info);
            info_layout = itemView.findViewById(R.id.info_layout);

            /*userSubCategory.setTextListener(new ShowMoreTextView.OnClickListener() {
                @Override
                public void onShowMoreClicked() {
                    if(viewListener != null) {
                        String userId = message.getContentValue().get(getAdapterPosition()).getCardId();
                        viewListener.onShowProfile(message, userId, getAdapterPosition());
                    }
                    //showDialog(message.getContentValue().get(getAdapterPosition()).getDescription());
                }

                @Override
                public void onLessClicked() {

                }
            });*/
            fakeView = itemView.findViewById(R.id.fakeView);
            userSubCategory.setVisibility(View.GONE);

            cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewListener != null) {
                        String userId = message.getContentValue().get(getAdapterPosition()).getCardId();
                        viewListener.onCardClickListener(message, userId, getAdapterPosition());
                    }
                }
            });

            info_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewListener != null) {
                        String userId = message.getContentValue().get(getAdapterPosition()).getCardId();
                        viewListener.onShowProfile(message, userId, getAdapterPosition());
                    }
                }
            });

        }
    }
/*
    private void showDialog(String text) {
        new AlertDialog.Builder(mContext)
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }*/
}

package com.hippo.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.database.CommonData;
import com.hippo.model.ActionButtonModel;
import com.hippo.support.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class CustomActionButtonsAdapter extends RecyclerView.Adapter<CustomActionButtonsAdapter.ActionButtonViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ActionButtonModel> mActionButtons;
    private boolean disAbleClick;
    private HippoColorConfig hippoColorConfig;

    /**
     * Constructor
     *
     * @param context       calling context
     * @param actionButtons the action buttons
     */
    public CustomActionButtonsAdapter(Context context, ArrayList<ActionButtonModel> actionButtons) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mActionButtons = actionButtons;
    }

    /**
     * Constructor
     *
     * @param context       calling context
     * @param actionButtons the action buttons
     * @param disAbleClick  for self sent view
     */
    public CustomActionButtonsAdapter(Context context, ArrayList<ActionButtonModel> actionButtons, boolean disAbleClick) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mActionButtons = actionButtons;
        this.disAbleClick = disAbleClick;
        hippoColorConfig = CommonData.getColorConfig();
    }

    @Override
    public ActionButtonViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View main = mLayoutInflater.inflate(R.layout.hippo_list_item_action_button, parent, false);
        return new ActionButtonViewHolder(main);
    }

    @Override
    public void onBindViewHolder(final ActionButtonViewHolder holder, final int position) {
        int pos = holder.getAdapterPosition();
        ActionButtonModel actionButton = mActionButtons.get(pos);
        holder.btnAction.setText(actionButton.getButtonText());
    }

    @Override
    public int getItemCount() {
        return mActionButtons.size();
    }

    /**
     * Action Button ViewHolder
     */
    class ActionButtonViewHolder extends RecyclerView.ViewHolder {

        AppCompatButton btnAction;
        RelativeLayout rlButton;

        ActionButtonViewHolder(final View itemView) {
            super(itemView);
            btnAction = itemView.findViewById(R.id.btnAction);
            rlButton = itemView.findViewById(R.id.rl_button);

            btnAction.setTextColor(hippoColorConfig.getHippoActionBarText());
            int radius = (int) Constants.convertDpToPixel(1);
            GradientDrawable drawable = (GradientDrawable) rlButton.getBackground();
            drawable.setStroke(radius, hippoColorConfig.getHippoActionBarText());
            drawable.setColor(hippoColorConfig.getHippoActionBarBg());

            btnAction.setEnabled(!disAbleClick);
            btnAction.setClickable(!disAbleClick);

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(!disAbleClick) {
                        if(mContext instanceof FuguChatActivity) {
                            ((FuguChatActivity) mContext).onCustomActionClicked(mActionButtons.get(getAdapterPosition())
                                    .getButtonAction());
                        }
                    }
                }
            });
        }
    }
}

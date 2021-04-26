package com.hippo.support.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.support.Utils.SupportKeys;
import com.hippo.support.callback.OnItemListener;
import com.hippo.support.model.Item;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 29/03/18.
 * @author gurmail
 */

public class HippoSupportAdapter extends RecyclerView.Adapter<HippoSupportAdapter.ViewHolder> {

    private static final String TAG = HippoSupportAdapter.class.getSimpleName();
    private Context context;
    private OnItemListener onItemListener;
    private HippoColorConfig hippoColorConfig;

    private ArrayList<Item> supportResponses;

    public HippoSupportAdapter(OnItemListener onItemListener) {
        hippoColorConfig = CommonData.getColorConfig();
        this.onItemListener = onItemListener;
    }

    public void setAdapterData(ArrayList<Item> supportResponses) {
        this.supportResponses = supportResponses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fugu_layout_support,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(supportResponses.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return supportResponses == null ? 0 : supportResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private TextView textView;
        private View viewDivider;
        public ViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_layout);
            textView = itemView.findViewById(R.id.support_text_view);
            textView.setTextColor(hippoColorConfig.getHippoTextColorPrimary());

            /*for (Drawable drawable : textView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(),
                            hippoColorConfig.getHippoTextColorPrimary()), PorterDuff.Mode.SRC_IN));
                }
            }*/


            viewDivider = itemView.findViewById(R.id.viewDivider);
            viewDivider.setBackgroundColor(hippoColorConfig.getHippoBorderColor());

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int actionType = supportResponses.get(getAdapterPosition()).getActionType();
                    switch (SupportKeys.SupportActionType.get(actionType)) {
                        case LIST:
                            onItemListener.onClick(supportResponses.get(getAdapterPosition()).getActionType(),
                                    supportResponses.get(getAdapterPosition()).getItems(), supportResponses.get(getAdapterPosition()).getTitle());
                            break;
                        case DESCRIPTION:
                        case CHAT_SUPPORT:
                        case SHOW_CONVERSATION:
                            onItemListener.onOtherTypeClick(actionType, supportResponses.get(getAdapterPosition()));
                            break;
                        default:

                            break;
                    }
                }
            });
        }
    }
}

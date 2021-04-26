package com.hippo.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.interfaces.OnMultiSelectionListener;
import com.hippo.model.MultiSelectButtons;
import java.util.ArrayList;

/**
 * Created by gurmail on 2019-12-17.
 *
 * @author gurmail
 */
public class MultiSelectionAdapter extends RecyclerView.Adapter<MultiSelectionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MultiSelectButtons> selectButtons;
    private boolean isMultiSelected;
    private int maxSelection = 0;
    private int selectedCount = 0;
    private boolean isSelectable = true;
    private HippoColorConfig colorConfig;
    private OnMultiSelectionListener listener;

    public MultiSelectionAdapter(ArrayList<MultiSelectButtons> selectButtons,
                                 boolean isMultiSelected, int maxSelection, boolean isSelectable, HippoColorConfig colorConfig,
                                 OnMultiSelectionListener listener) {
        this.selectButtons = selectButtons;
        this.isMultiSelected = isMultiSelected;
        this.maxSelection = maxSelection;
        this.isSelectable = isSelectable;
        this.colorConfig = colorConfig;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.hippo_item_multi_selection, viewGroup, false), isMultiSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MultiSelectButtons buttons = selectButtons.get(i);
        viewHolder.textView.setText(buttons.getTitle());
        if(colorConfig != null)
            viewHolder.textView.setTextColor(colorConfig.getHippoPrimaryTextMsgYou());

        if(isMultiSelected) {
            viewHolder.radioButton.setVisibility(View.GONE);
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.radioButton.setVisibility(View.VISIBLE);
        }

        if(buttons.getStatus() == 1) {
            viewHolder.checkBox.setChecked(true);
            viewHolder.radioButton.setChecked(true);
            viewHolder.mainLayout.setBackground(context.getDrawable(R.drawable.hippo_mullti_selector_bg));
        } else {
            viewHolder.mainLayout.setBackground(context.getDrawable(R.drawable.hippo_multi_normal_bg));
            viewHolder.checkBox.setChecked(false);
            viewHolder.radioButton.setChecked(false);
        }
        if(isSelectable) {
            viewHolder.checkBox.setEnabled(!isSelectable);
            viewHolder.radioButton.setEnabled(!isSelectable);
        }
    }

    @Override
    public int getItemCount() {
        return selectButtons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CheckBox checkBox;
        private RadioButton radioButton;
        private LinearLayout mainLayout;

        public ViewHolder(@NonNull View itemView, final boolean isMultiSelect) {
            super(itemView);

            textView = itemView.findViewById(R.id.item_textview);
            checkBox = itemView.findViewById(R.id.cb_item_view);
            radioButton = itemView.findViewById(R.id.rb_item_view);
            mainLayout = itemView.findViewById(R.id.main_layout);

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isPressed()) {
                        mainLayout.performClick();
                    }
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isPressed()) {
                        if(maxSelection > 0 && selectedCount == maxSelection && isChecked) {
                            checkBox.setChecked(!isChecked);
                            return;
                        }
                        mainLayout.performClick();
                    }
                }
            });

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelectable)
                        return;
                    if(isMultiSelect) {
                        if(selectButtons.get(getAdapterPosition()).getStatus() == 0) {
                            if(maxSelection > 0 && selectedCount == maxSelection) {
                                notifyItemChanged(getAdapterPosition());
                                return;
                            }
                            selectButtons.get(getAdapterPosition()).setStatus(1);
                            selectedCount += 1;
                        } else {
                            selectButtons.get(getAdapterPosition()).setStatus(0);
                            selectedCount -= 1;
                        }
                        notifyItemChanged(getAdapterPosition());
                    } else {
                        for(int i=0;i<selectButtons.size();i++) {
                            selectButtons.get(i).setStatus(0);
                        }
                        selectButtons.get(getAdapterPosition()).setStatus(1);
                        notifyDataSetChanged();
                    }
                    if(listener != null)
                        listener.onItemClicked(selectButtons);
                }
            });
        }
    }
}

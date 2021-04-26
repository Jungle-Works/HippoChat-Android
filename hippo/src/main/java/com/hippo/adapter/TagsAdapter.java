package com.hippo.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.model.ContentValue;

import java.util.ArrayList;

/**
 * Created by gurmail on 2019-12-05.
 *
 * @author gurmail
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    public ArrayList<ContentValue> arrayList = new ArrayList<>();
    private HippoColorConfig hippoColorConfig;
    private Context mContext;
    private OnTagClicked onTagClicked;

    public TagsAdapter(ArrayList<ContentValue> arrayList, HippoColorConfig hippoColorConfig, OnTagClicked onTagClicked) {
        this.arrayList = arrayList;
        this.hippoColorConfig = hippoColorConfig;
        this.onTagClicked = onTagClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tag_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tagView.setText(arrayList.get(i).getBtnTitle());
        viewHolder.tagView.setTextColor(hippoColorConfig.getHippoBotConcentText());
        try {
            Drawable background = viewHolder.tagView.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(hippoColorConfig.getHippoBotConcentBtnBg());
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(hippoColorConfig.getHippoBotConcentBtnBg());
                ((GradientDrawable)background).setStroke(2, hippoColorConfig.getHippoBotConcentText());
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(hippoColorConfig.getHippoBotConcentBtnBg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tagView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tagView);
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onTagClicked != null) {
                        onTagClicked.onClick(getAdapterPosition(), arrayList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnTagClicked {
        void onClick(int position, ContentValue contentValue);
    }
}

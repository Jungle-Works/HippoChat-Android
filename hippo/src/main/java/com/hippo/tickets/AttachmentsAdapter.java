package com.hippo.tickets;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hippo.R;
import com.hippo.utils.fileUpload.FileuploadModel;

import java.util.ArrayList;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {

    ArrayList<FileuploadModel> attachments = new ArrayList<>();
    private LayoutInflater mInflater;

    // data is passed into the constructor
    AttachmentsAdapter(Context context, ArrayList<FileuploadModel> attachments) {
        this.mInflater = LayoutInflater.from(context);
        this.attachments = attachments;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.attachments_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(attachments.get(position).getFileName());

        holder.crossIV.setTag(position);
        holder.crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                attachments.remove(pos);
                notifyDataSetChanged();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return attachments.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageView crossIV;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvOptionName);
            crossIV = itemView.findViewById(R.id.crossIV);
        }

    }


}
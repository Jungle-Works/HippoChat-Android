package com.hippo.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hippo.R;
import com.hippo.model.ContentValue;
import com.hippo.model.Message;

import java.util.ArrayList;

/**
 * Created by gurmail on 26/04/18.
 *
 * @author gurmail
 */

public class HippoQuickReplayAdapter extends RecyclerView.Adapter<HippoQuickReplayAdapter.QRViewHolder> {

    private static final String TAG = HippoQuickReplayAdapter.class.getSimpleName();
    private Context context;
    private QRCallback qrCallback;
    private Message mMessage;
    private ArrayList<ContentValue> arrayList = new ArrayList<>();
    private FuguMessageAdapter.QuickReplyViewHolder viewHolder;

    public HippoQuickReplayAdapter(Message message, QRCallback qrCallback, FuguMessageAdapter.QuickReplyViewHolder qrViewHolder) {

        this.viewHolder = qrViewHolder;
        this.qrCallback = qrCallback;
        this.mMessage = message;
        this.arrayList.addAll(message.getContentValue());
    }

    @Override
    public QRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.hippo_item_rounded_button, parent, false);
        return new QRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QRViewHolder holder, final int position) {

        holder.button.setText(arrayList.get(position).getButtonTitle());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCallback.onClickListener(mMessage, position, viewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class QRViewHolder extends RecyclerView.ViewHolder {
        private Button button;

        public QRViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.action_button);

        }
    }
}

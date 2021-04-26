package com.hippo.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.langs.Restring;
import com.hippo.model.HippoPayment;
import com.hippo.model.Message;
import com.hippo.utils.HippoLog;

import java.util.ArrayList;

/**
 * Created by gurmail on 2019-11-04.
 *
 * @author gurmail
 */
public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private OnPaymentListener listener;
    private ArrayList<HippoPayment> hippoPayments;
    private Message message;
    private HippoColorConfig hippoColorConfig;
    private String selectedId;
    private int position;
    private Context context;

    public PaymentAdapter(Message message, ArrayList<HippoPayment> hippoPayments,
                          OnPaymentListener listener, HippoColorConfig hippoColorConfig, String selectedId, int position) {
        this.listener = listener;
        this.message = message;
        this.hippoPayments = hippoPayments;
        this.hippoColorConfig = hippoColorConfig;
        this.selectedId = selectedId;
        this.position = position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hippo_customer_payment_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.line.setVisibility(View.VISIBLE);
        if(hippoPayments.size() == 1) {
            viewHolder.radioBtn.setVisibility(View.GONE);
        } else {
            viewHolder.radioBtn.setVisibility(View.VISIBLE);
        }

        viewHolder.title.setText(hippoPayments.get(i).getTitle());
        if(!TextUtils.isEmpty(hippoPayments.get(i).getDescription())) {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(hippoPayments.get(i).getDescription());
        } else {
            viewHolder.description.setVisibility(View.GONE);
        }

        String symbols = hippoPayments.get(i).getCurrencySymbol();
        viewHolder.amount.setText(symbols +""+hippoPayments.get(i).getAmount());

        viewHolder.radioBtn.setChecked(hippoPayments.get(i).isSelected());

        viewHolder.title.setTextColor(hippoColorConfig.getHippoPaymentTitle());
        viewHolder.description.setTextColor(hippoColorConfig.getHippoPaymentDescription());
        viewHolder.amount.setTextColor(hippoColorConfig.getHippoPaymentAmount());
        viewHolder.paymentLayout.setBackgroundColor(hippoColorConfig.getHippoPaymentBg());

        if(!TextUtils.isEmpty(selectedId) && hippoPayments.get(i).getId().equalsIgnoreCase(selectedId)) {
            viewHolder.paid.setVisibility(View.VISIBLE);
            viewHolder.paid.setTextColor(hippoColorConfig.getHippoPaymentTitle());
        }

    }

    @Override
    public int getItemCount() {
//        if(!TextUtils.isEmpty(selectedId))
//            return 1;
        return hippoPayments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView amount;
        private View line;
        private RelativeLayout paymentLayout;
        private RadioButton radioBtn;
        private TextView paid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paymentLayout = itemView.findViewById(R.id.paymentLayout);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            amount = itemView.findViewById(R.id.amount);
            line = itemView.findViewById(R.id.line);
            radioBtn = itemView.findViewById(R.id.radioBtn);
            paid = itemView.findViewById(R.id.paid);
            String paidTxt = Restring.getString(context, R.string.hippo_paid);
            paid.setText(paidTxt);

            radioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isPressed()) {
                        paymentLayout.performClick();
                    }
                }
            });

            paymentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(!hippoPayments.get(pos).isSelected()) {
                        for (int i = 0; i < hippoPayments.size(); i++) {
                            if(i==pos) {
                                hippoPayments.get(i).setSelected(true);
                                try {
                                    if(listener != null) {
                                        listener.onPaymentViewClicked(message, hippoPayments.get(i), i, hippoPayments.get(i).getPaymentUrl(), position);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                hippoPayments.get(i).setSelected(false);
                            }
                        }
                        HippoLog.v("demo", "data = "+new Gson().toJson(hippoPayments));
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}

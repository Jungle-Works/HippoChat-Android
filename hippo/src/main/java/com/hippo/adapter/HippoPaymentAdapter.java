package com.hippo.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.interfaces.onItemOpertionListener;
import com.hippo.langs.Restring;
import com.hippo.model.PaymentModelData;

import java.util.ArrayList;

/**
 * Created by gurmail on 22/02/19.
 *
 * @author gurmail
 */
public class HippoPaymentAdapter extends RecyclerView.Adapter<HippoPaymentAdapter.ViewHolder> {

    private static final String TAG = HippoPaymentAdapter.class.getSimpleName();
    private ArrayList<PaymentModelData> arrayList = new ArrayList<>();
    private Context context;
    private onItemOpertionListener onItemOpertionListener;
    private HippoColorConfig hippoColorConfig;

    public HippoPaymentAdapter(ArrayList<PaymentModelData> arrayList, onItemOpertionListener onItemOpertionListener) {
        this.arrayList = arrayList;
        this.onItemOpertionListener = onItemOpertionListener;
        hippoColorConfig = CommonData.getColorConfig();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.hippo_item_payment, parent, false)
                , new MyFormEditTextListener(), new PriceEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PaymentModelData paymentModelData = arrayList.get(position);
        viewHolder.itemDescription.setText(paymentModelData.getItemDescription());
        viewHolder.itemPrice.setText(paymentModelData.getPrice());

        if(!TextUtils.isEmpty(paymentModelData.getErrorDesc()) && TextUtils.isEmpty(paymentModelData.getItemDescription()))
            viewHolder.itemDescription.setError(paymentModelData.getErrorDesc());
        else
            viewHolder.itemDescription.setError(null);

        if(TextUtils.isEmpty(paymentModelData.getPrice()))
            viewHolder.itemPrice.setError(paymentModelData.getErrorPrice());
        else
            viewHolder.itemPrice.setError(null);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MyFormEditTextListener myCustomEditTextListener;
        private PriceEditTextListener priceEditTextListener;
        private EditText itemDescription, itemPrice;
        private RelativeLayout crossButton;
        private ImageView crossView;
        private TextView desc_title, priceText;

        public ViewHolder(@NonNull View itemView, MyFormEditTextListener myCustomEditTextListener,
                          PriceEditTextListener priceEditTextListener) {
            super(itemView);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemPrice = itemView.findViewById(R.id.item_price);
            desc_title = itemView.findViewById(R.id.item_price);
            priceText = itemView.findViewById(R.id.priceText);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.priceEditTextListener = priceEditTextListener;

            String descTitle = Restring.getString(context, R.string.hippo_title_item_description);
            String priceTxt = Restring.getString(context, R.string.hippo_title_item_price);
            String descHint = Restring.getString(context, R.string.hippo_item_description);
            String itemPriceHint = Restring.getString(context, R.string.hippo_item_price);

            itemDescription.setHint(descHint);
            itemPrice.setHint(itemPriceHint);
            desc_title.setText(descTitle);
            priceText.setText(priceTxt);

            crossButton = itemView.findViewById(R.id.cross_button);
            crossButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemOpertionListener.onItemRemoved(getAdapterPosition());
                }
            });

            crossView = itemView.findViewById(R.id.image_cross);

            itemDescription.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
            itemPrice.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
            crossView.setColorFilter(new PorterDuffColorFilter(hippoColorConfig.getHippoSourceType(), PorterDuff.Mode.SRC_IN));

            itemDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayList.get(getAdapterPosition()).setItemDescription(itemDescription.getText().toString().trim());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            itemPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayList.get(getAdapterPosition()).setPrice(itemPrice.getText().toString().trim());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(onItemOpertionListener != null)
                        onItemOpertionListener.onItemAdded();
                }
            });


            itemDescription.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if (itemDescription.hasFocus()) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK){
                            case MotionEvent.ACTION_SCROLL:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public class MyFormEditTextListener implements TextWatcher {
        private int position;
        private boolean isEdittable;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            if(!TextUtils.isEmpty(editable.toString()) && isEdittable)
                arrayList.get(position).setItemDescription(editable.toString());
            isEdittable = true;
        }
    }

    public class PriceEditTextListener implements TextWatcher {
        private int position;
        private boolean isEdittable;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            if(!TextUtils.isEmpty(editable.toString()) && isEdittable)
                arrayList.get(position).setPrice(editable.toString());
            isEdittable = true;
        }
    }

}

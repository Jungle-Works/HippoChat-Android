package com.hippo.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.adapter.HippoPaymentAdapter;
import com.hippo.database.CommonData;
import com.hippo.interfaces.onItemOpertionListener;
import com.hippo.langs.Restring;
import com.hippo.model.PaymentData;
import com.hippo.model.PaymentModelData;
import com.hippo.support.Utils.Constants;
import com.hippo.utils.countrypicker.Country;
import com.hippo.utils.countrypicker.CurrencyPicker;
import com.hippo.utils.countrypicker.OnCountryPickerListener;
import com.hippo.utils.filepicker.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.hippo.utils.countrypicker.CurrencyPicker.SORT_BY_NAME;

/**
 * Created by gurmail on 22/02/19.
 *
 * @author gurmail
 */
public class HippoPaymentActivity extends FuguBaseActivity implements onItemOpertionListener {

    private static final String TAG = HippoPaymentActivity.class.getSimpleName();
    private HippoPaymentAdapter paymentAdapter;
    private ArrayList<PaymentModelData> paymentModelData = new ArrayList<>();
    private Country countryCurrency;

    private EditText titleView;
    private TextView currencyView;
    private TextView addOptionView;
    private TextView totalCount;
    private EditText itemDescription;
    private EditText itemPrice;
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;
    private Toolbar toolbar;
    private AppCompatButton buttonSubmit;
    private HippoColorConfig hippoColorConfig;
    private TextView titleTxt, currencyTxt, descriptionTxt, priceTxt;
    private ImageView crossView;

    private void setColorConfig() {
        hippoColorConfig = CommonData.getColorConfig();
        titleView.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
        currencyView.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
        itemDescription.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
        itemPrice.setTextColor(hippoColorConfig.getHippoTextColorPrimary());

        addOptionView.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());
        buttonSubmit.setTextColor(hippoColorConfig.getHippoActionBarText());
        int radius = (int) Constants.convertDpToPixel(1);
        GradientDrawable drawable = (GradientDrawable) buttonSubmit.getBackground();
        drawable.setStroke(radius, hippoColorConfig.getHippoActionBarText());
        drawable.setColor(hippoColorConfig.getHippoActionBarBg());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_payment);


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        String title = Restring.getString(HippoPaymentActivity.this, R.string.hippo_payment_request);
        setToolbar(toolbar, title);

        titleView = findViewById(R.id.titie_view);
        currencyView = findViewById(R.id.currency_type_view);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        recyclerView = findViewById(R.id.recycler_view);
        addOptionView = findViewById(R.id.add_option_view);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        titleTxt = findViewById(R.id.title_txt);
        currencyTxt = findViewById(R.id.currency_txt);
        descriptionTxt = findViewById(R.id.description_txt);
        priceTxt = findViewById(R.id.price_txt);
        totalCount = findViewById(R.id.total_count);
        totalCount.setVisibility(View.GONE);

        titleTxt.setText(Restring.getString(HippoPaymentActivity.this, R.string.fugu_title));
        titleView.setHint(Restring.getString(HippoPaymentActivity.this, R.string.hippo_enter_title));

        currencyTxt.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_currency));
        //currencyView.setHint(Restring.getString(HippoPaymentActivity.this, R.string.));

        descriptionTxt.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_title_item_description));
        itemDescription.setHint(Restring.getString(HippoPaymentActivity.this, R.string.hippo_item_description));

        priceTxt.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_title_item_price));
        itemPrice.setHint(Restring.getString(HippoPaymentActivity.this, R.string.hippo_item_price));

        totalCount.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_total_count));
        addOptionView.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_add_an_option));
        buttonSubmit.setText(Restring.getString(HippoPaymentActivity.this, R.string.hippo_request_payment));

        setColorConfig();

        paymentAdapter = new HippoPaymentAdapter(paymentModelData, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(paymentAdapter);

        currencyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker(currencyView);
            }
        });

        itemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onItemAdded();
            }
        });

        addOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_fill_pre_field);
                if(paymentModelData.size() > 0) {
                    if(TextUtils.isEmpty(paymentModelData.get(paymentModelData.size()-1).getItemDescription().trim()) ||
                            TextUtils.isEmpty(paymentModelData.get(paymentModelData.size()-1).getPrice().trim())) {
                        ToastUtil.getInstance(HippoPaymentActivity.this).showToast(text);
                        return;
                    }
                } else {
                    if(TextUtils.isEmpty(itemDescription.getText().toString().trim()) ||
                            TextUtils.isEmpty(itemPrice.getText().toString().trim())) {
                        ToastUtil.getInstance(HippoPaymentActivity.this).showToast(text);
                        return;
                    }
                }
                PaymentModelData modelData = new PaymentModelData();
                modelData.setItemDescription("");
                modelData.setPrice("");

                paymentModelData.add(modelData);
                paymentAdapter.notifyItemInserted(paymentModelData.size() - 1);

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(HippoPaymentActivity.this);
                PaymentData paymentData = new PaymentData();
                if(TextUtils.isEmpty(titleView.getText().toString().trim())) {
                    titleView.setError(Restring.getString(HippoPaymentActivity.this, R.string.hippo_field_cant_empty));
                    return;
                } else if(TextUtils.isEmpty(itemDescription.getText().toString().trim())) {
                    itemDescription.setError(Restring.getString(HippoPaymentActivity.this, R.string.hippo_field_cant_empty));
                    return;
                } else if(TextUtils.isEmpty(itemPrice.getText().toString().trim())) {
                    itemPrice.setError(Restring.getString(HippoPaymentActivity.this, R.string.hippo_field_cant_empty));
                    return;
                }

                paymentData.setTitle(titleView.getText().toString());
                if(countryCurrency != null) {
                    paymentData.setCurrency(countryCurrency.getCurrency());
                    paymentData.setCurrencySymbol(countryCurrency.getSymbol());
                } else {
                    paymentData.setCurrency("USD");
                    paymentData.setCurrencySymbol("$");
                }

                ArrayList<PaymentModelData> arrayList = new ArrayList<>();
                if(!TextUtils.isEmpty(itemDescription.getText().toString().trim()) &&
                        !TextUtils.isEmpty(itemPrice.getText().toString().trim())) {

                    try {
                        Double aDouble = Double.parseDouble(itemPrice.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_enter_valid_price);
                        ToastUtil.getInstance(HippoPaymentActivity.this).showToast(text);
                        return;
                    }

                    PaymentModelData modelData = new PaymentModelData();
                    modelData.setItemDescription(itemDescription.getText().toString().trim());
                    modelData.setPrice(itemPrice.getText().toString().trim());
                    arrayList.add(modelData);
                }
                for(int i = 0;i<paymentModelData.size();i++) {
                    PaymentModelData modelData = paymentModelData.get(i);
                    if(TextUtils.isEmpty(modelData.getItemDescription()) && TextUtils.isEmpty(modelData.getPrice())) {
                        continue;
                    } else {
                        paymentModelData.get(i).setErrorDesc(null);
                        paymentModelData.get(i).setErrorPrice(null);

                        if(TextUtils.isEmpty(modelData.getItemDescription().trim())) {
                            String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_field_cant_empty);
                            paymentModelData.get(i).setErrorDesc(text);
                            paymentAdapter.notifyItemChanged(i);
                            return;
                        } else if(TextUtils.isEmpty(modelData.getPrice().trim())) {
                            String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_field_cant_empty);
                            paymentModelData.get(i).setErrorPrice(text);
                            paymentAdapter.notifyItemChanged(i);
                            return;
                        } else {
                            try {
                                Double aDouble = Double.parseDouble(modelData.getPrice().trim());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_invalid_price);
                                paymentModelData.get(i).setErrorPrice(text);
                                paymentAdapter.notifyItemChanged(i);
                                return;
                            }
                            arrayList.add(modelData);
                        }
                    }
                }

                paymentData.setPaymentModelData(arrayList);

                Intent intent = new Intent();
                intent.putExtra("data", paymentData);
                setResult(RESULT_OK, intent);
                finish();
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



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //hideKeyboard(VideoPlayerActivity.this);
            onBackPressed(); // close this context and return to preview context (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onItemAdded() {
        Double totalPrice = 0.0;
        if(!TextUtils.isEmpty(itemPrice.getText().toString().trim())) {
            try {
                totalPrice = Double.parseDouble(itemPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
        }
        if(paymentModelData != null && paymentModelData.size()>0) {
            for(PaymentModelData modelData : paymentModelData) {
                if(modelData != null && !TextUtils.isEmpty(modelData.getPrice())) {
                    try {
                        totalPrice = totalPrice + Double.parseDouble(modelData.getPrice().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
        if(totalPrice>0) {
            totalCount.setVisibility(View.VISIBLE);
            String text = Restring.getString(HippoPaymentActivity.this, R.string.hippo_total_count);
            totalCount.setText(text+" "+getDecimalFormat().format(totalPrice));
        } else {
            totalCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemRemoved(int position) {
        paymentModelData.remove(position);
        paymentAdapter.notifyItemRemoved(position);
    }

    private void openPicker(final TextView textView) {
        CurrencyPicker currencyPicker = new CurrencyPicker.Builder().with(this)
                .sortBy(SORT_BY_NAME)
                .listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        countryCurrency = country;
                        textView.setText(country.getCode()+"("+country.getSymbol()+")");
                    }
                }).build();

        currencyPicker.showDialog(getSupportFragmentManager());
    }

    private DecimalFormat decimalFormatMoney;
    public DecimalFormat getDecimalFormat(){
        if(decimalFormatMoney == null){
            decimalFormatMoney = new DecimalFormat("#.##");
        }
        return decimalFormatMoney;
    }

}

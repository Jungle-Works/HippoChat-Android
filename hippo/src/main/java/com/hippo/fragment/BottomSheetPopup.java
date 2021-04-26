package com.hippo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.adapter.PaymentGatewayAdapter;
import com.hippo.apis.GetPaymentGateway;
import com.hippo.callback.OnPaymentItemClickListener;
import com.hippo.callback.OnPaymentListListener;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.HippoPayment;
import com.hippo.model.PaymentData;
import com.hippo.model.payment.AddedPaymentGateway;
import com.hippo.utils.filepicker.ToastUtil;
import com.hippo.utils.loadingBox.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-05-06.
 *
 * @author gurmail
 */
public class BottomSheetPopup extends BottomSheetDialogFragment implements OnPaymentItemClickListener {

    private RecyclerView recyclerView;
    private PaymentGatewayAdapter paymentAdapter;
    private String url;
    private String payment;
    private String currency;

    private LinearLayout paymentLayout;
    private TextView paymentMethod;
    private ProgressWheel progress;

    public static BottomSheetPopup newInstance(Bundle args) {
        BottomSheetPopup frag = new BottomSheetPopup();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            url = getArguments().getString("url");
            payment = getArguments().getString("payment");
            currency = getArguments().getString("currency");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new BottomSheetDialog(requireContext(), getTheme());
        return inflater.inflate(R.layout.hippo_payment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        paymentLayout = view.findViewById(R.id.paymentLayout);
        paymentMethod = view.findViewById(R.id.payment_method);
        progress = view.findViewById(R.id.progress);
        showPayemtMethods(true);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onItemClickListener(AddedPaymentGateway paymentGateway) {
        try {
            FuguChatActivity activity = (FuguChatActivity) getActivity();
            activity.openPaymentDialog(url, new Gson().fromJson(payment, HippoPayment.class), paymentGateway);

        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
    }

    private void showPayemtMethods(boolean hasRetry) {
        ArrayList<AddedPaymentGateway> arrayList = new ArrayList<>();
        for(AddedPaymentGateway gateway : CommonData.getPaymentList()) {
            if(gateway.getCurrencyallowed().contains(currency.toUpperCase()))
                arrayList.add(gateway);
        }
        if(arrayList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            paymentLayout.setVisibility(View.VISIBLE);
            if(hasRetry) {
                progress.setVisibility(View.VISIBLE);
                paymentMethod.setText(Restring.getString(getActivity(), R.string.hippo_fetching_payment_methods));
                fetchUpdateGateways();
            } else {
                progress.setVisibility(View.GONE);
                paymentMethod.setText(Restring.getString(getActivity(), R.string.hippo_no_payment_methods));
            }
        } else {
            paymentAdapter = new PaymentGatewayAdapter(arrayList, this);
            recyclerView.setAdapter(paymentAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.GONE);
        }
    }

    private void fetchUpdateGateways() {
        GetPaymentGateway.INSTANCE.getPaymentGatewaysList(getActivity(),new OnPaymentListListener() {
            @Override
            public void onSuccessListener() {
                showPayemtMethods(false);
            }

            @Override
            public void onErrorListener() {
                showPayemtMethods(false);
            }
        });
    }
}

package com.hippo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hippo.R;
import com.hippo.activity.PrePaymentActivity;
import com.hippo.langs.Restring;

/**
 * Created by gurmail on 2020-06-16.
 *
 * @author gurmail
 */
public class PaymentCancelDialog extends BottomSheetDialogFragment {

    private Button no;
    private Button yes;
    private TextView title, description;

    public static PaymentCancelDialog newInstance() {
        PaymentCancelDialog frag = new PaymentCancelDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new BottomSheetDialog(requireContext(), getTheme());
        return inflater.inflate(R.layout.cancel_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        no = view.findViewById(R.id.hippo_no);
        yes = view.findViewById(R.id.hippo_yes);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);

        final PrePaymentActivity activity = (PrePaymentActivity) getActivity();

        title.setText(Restring.getString(activity, R.string.hippo_cancel_payment));
        description.setText(Restring.getString(activity, R.string.cancel_payment_text));
        yes.setText(Restring.getString(activity, R.string.hippo_yes_cancel));
        no.setText(Restring.getString(activity, R.string.hippo_no));


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //PrePaymentActivity activity = (PrePaymentActivity) getActivity();
                    activity.cancelPayment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}

package com.hippo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hippo.R;
import com.hippo.langs.Restring;

/**
 * Created by gurmail on 2019-11-05.
 *
 * @author gurmail
 */
public class SingleBtnDialog {

    private Dialog dialog;
    private Context context;

    private SingleBtnDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();
        }
        // custom dialog
        dialog = new Dialog(context);
        this.context = context;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_single_btn_layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setText(Restring.getString(context, R.string.fugu_ok));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public static SingleBtnDialog with(Context context) {
        return new SingleBtnDialog(context);
    }

    public SingleBtnDialog setMessage(String msg) {
        if (dialog != null) {
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
            tvMessage.setText(msg);
        }
        return this;
    }

    public SingleBtnDialog setCancelable(boolean bool) {
        if (dialog != null) {
            dialog.setCancelable(false);
        }
        return this;
    }

    public SingleBtnDialog setCancelableOnTouchOutside(boolean bool) {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
        }
        return this;
    }

    public SingleBtnDialog setHeading(String heading) {
        if (dialog != null) {
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_heading);
            tvMessage.setText(heading);
            tvMessage.setVisibility(View.VISIBLE);
        }
        return this;
    }
    public SingleBtnDialog hideHeading() {
        if (dialog != null) {
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_heading);
            tvMessage.setVisibility(View.GONE);
        }
        return this;
    }
    public SingleBtnDialog setOptionPositive(String optionPositive) {
        if (dialog != null) {
            Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
            btnOk.setText(optionPositive);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return this;
    }

    public SingleBtnDialog setCallback(final OnActionPerformed onActionPerformed) {
        if (dialog != null && onActionPerformed != null) {
            Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onActionPerformed.positive();
                }
            });
        }
        return this;
    }

    public Dialog show() {
        if (dialog != null&& !((AppCompatActivity)context).isFinishing()) {
            dialog.show();
            return dialog;
        }
        return null;
    }

    public interface OnActionPerformed {
        void positive();


    }

}

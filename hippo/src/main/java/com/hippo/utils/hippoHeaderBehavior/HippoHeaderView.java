package com.hippo.utils.hippoHeaderBehavior;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippo.R;

/**
 * Created by gurmail on 2019-11-13.
 *
 * @author gurmail
 */
public class HippoHeaderView extends LinearLayout {

    TextView name;
    TextView rating;

    public HippoHeaderView(Context context) {
        super(context);
    }

    public HippoHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HippoHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HippoHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.name);
        rating = findViewById(R.id.rating);
    }

    public void bindTo(String name, String ratingTxt) {
        this.name.setText(name);
        if(!TextUtils.isEmpty(ratingTxt)) {
            this.rating.setVisibility(VISIBLE);
            this.rating.setText(ratingTxt);
        } else {
            this.rating.setVisibility(GONE);
        }
    }

    public void setTextSize(float size) {
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
}
package com.hippo.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AdapterView;

public class InstantAutoComplete extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private boolean enoughToFilter = true;

    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return enoughToFilter;
    }

    public void setEnoughFilter(boolean enoughFilter) {
        enoughToFilter = enoughFilter;
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return super.getOnItemClickListener();
    }

    @Override
    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return super.getOnItemSelectedListener();

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }

}
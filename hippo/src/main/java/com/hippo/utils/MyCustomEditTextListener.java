package com.hippo.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.hippo.model.Message;

/**
 * Created by gurmail on 08/05/18.
 *
 * @author gurmail
 */ // we make TextWatcher to be aware of the position it currently works with
// this way, once a new item is attached in onBindViewHolder, it will
// update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
public class MyCustomEditTextListener implements TextWatcher {
    private Message currentOrderItem;

    public void updatePosition(Message currentOrderItem) {
        this.currentOrderItem = currentOrderItem;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        // no op
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if(currentOrderItem != null)
            currentOrderItem.setComment(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // no op
    }
}

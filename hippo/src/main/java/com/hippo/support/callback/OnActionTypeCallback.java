package com.hippo.support.callback;

import com.hippo.support.model.Item;

import java.util.ArrayList;

/**
 * Created by gurmail on 30/03/18.
 */

public interface OnActionTypeCallback {

    void onActionType(ArrayList<Item> items, String path, String title, String transactionId, String categoryDate);

    void openDetailPage(Item items, String path, String transactionId, String categoryDate);

    void removeFragment();
}

package com.hippo.support.callback;

import com.hippo.support.model.Item;

import java.util.List;

/**
 * Created by gurmail on 29/03/18.
 */

public interface OnItemListener {

    void onClick(int actionType, List<Item> items, String title);

    void onOtherTypeClick(int actionType, Item item);

    /**
     * @deprecated
     */
    void onDescription(Item item);

    /**
     * @deprecated
     */
    void chatSupport(Item item);

    /**
     * @deprecated
     */
    void showConversaton(Item item);
}

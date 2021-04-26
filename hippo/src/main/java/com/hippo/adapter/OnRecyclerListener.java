package com.hippo.adapter;

import android.view.View;

/**
 * Created by gurmail on 21/01/19.
 *
 * @author gurmail
 */
public interface OnRecyclerListener {
    void onItemClick(View viewClicked, View parentView, int position);
    void onItemClick(View parentView, int position);
    void onItemLongClick(View viewClicked, View parentView, int position, boolean isRightConcent);
    //void onItemLongClick(View viewClicked, View parentView, int position);
}

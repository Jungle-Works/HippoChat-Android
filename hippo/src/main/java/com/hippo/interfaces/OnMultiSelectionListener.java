package com.hippo.interfaces;

import com.hippo.model.MultiSelectButtons;

import java.util.ArrayList;

/**
 * Created by gurmail on 2019-12-26.
 *
 * @author gurmail
 */
public interface OnMultiSelectionListener {
    void onItemClicked(ArrayList<MultiSelectButtons> selectButtons);
}

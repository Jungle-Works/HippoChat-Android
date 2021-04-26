package com.hippo;

import com.hippo.model.promotional.PromotionResponse;

/**
 * Created by gurmail on 2020-01-10.
 *
 * @author gurmail
 */
public interface NotificationListener {

    void onSucessListener(PromotionResponse response);
    void onFailureListener();
    void onItemClickListener(String url);
}

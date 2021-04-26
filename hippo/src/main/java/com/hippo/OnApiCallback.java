package com.hippo;

/**
 * Created by gurmail on 2019-12-09.
 *
 * @author gurmail
 */
public interface OnApiCallback {

    public void onSucess();

    public void onFailure(String errorMessage);

    public void onProcessing();
}

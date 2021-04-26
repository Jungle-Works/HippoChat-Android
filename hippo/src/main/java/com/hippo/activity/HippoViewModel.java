package com.hippo.activity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.hippo.utils.HippoLog;

/**
 * Created by gurmail on 2020-04-22.
 *
 * @author gurmail
 */
public class HippoViewModel extends AndroidViewModel {
    public HippoViewModel(@NonNull Application application) {
        super(application);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        HippoLog.e("HippoActivityLifecycleCallback", "OnCleared mainViewModel");

    }
}

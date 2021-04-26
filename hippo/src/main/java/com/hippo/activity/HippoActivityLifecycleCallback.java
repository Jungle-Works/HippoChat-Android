package com.hippo.activity;

/**
 * Class for handling activity lifecycle events
 * Created by gurmail on 2020-04-22.
 *
 * @author gurmail
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.hippo.HippoConfig;
import com.hippo.utils.HippoLog;
import java.util.HashSet;
import java.util.List;

import faye.ConnectionUtils;

import static android.content.Context.ACTIVITY_SERVICE;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class HippoActivityLifecycleCallback {
    static boolean registered = false;
    private static final String TAG = HippoActivityLifecycleCallback.class.getSimpleName();
    public static HashSet<String> hippoClasses;

    /**
     * Enables lifecycle callbacks for Android devices
     * @param application App's Application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static synchronized void registerApplication(android.app.Application application) {
        if (application == null) {
            HippoLog.i("register", "Application instance is null/system API is too old");
            return;
        }

        if (registered) {
            HippoLog.v("register", "Lifecycle callbacks have already been registered");
            return;
        }
        hippoClasses = new HashSet<>();
        registered = true;
        application.registerActivityLifecycleCallbacks(
                new android.app.Application.ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {
                        hippoClasses.add(activity.getClass().getName());
                        HippoLog.v(TAG, "onActivityCreated "+activity.getClass().getName());
                        //HippoConfig.getInstance().onCheckActity(activity);
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        HippoLog.v(TAG, "onActivityStarted "+activity.getClass().getSimpleName());
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        HippoLog.v(TAG, "onActivityResumed "+activity.getClass().getSimpleName());
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        HippoLog.v(TAG, "onActivityPaused "+activity.getClass().getSimpleName());
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        HippoLog.v(TAG, "onActivityStopped "+activity.getClass().getSimpleName());
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                        HippoLog.v(TAG, "onActivitySaveInstanceState "+activity.getClass().getSimpleName());
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        HippoLog.v(TAG, "onActivityDestroyed "+activity.getClass().getSimpleName());
                        hippoClasses.remove(activity.getClass().getSimpleName());
                        if(hippoClasses.size() == 0) {
                            HippoLog.w(TAG, "SDK's All classes Destroyed");
                        }
                        //isAppRunning(activity);
                    }
                }

        );
        HippoLog.i("In callback", "Activity Lifecycle Callback successfully registered");
    }

    /*private static void isAppRunning(Context context) {
        ActivityManager mngr = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if(taskList != null && taskList.size()>0) {
            for(ActivityManager.RunningTaskInfo task : taskList) {
                HippoLog.w("Name", "name = " + task.topActivity.getClassName());
            }
        }
    }

    private void handleBack() {

    }*/

    /**
     * Enables lifecycle callbacks for Android devices
     * @param application App's Application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static synchronized void register(android.app.Application application) {
        registerApplication(application);
    }
}
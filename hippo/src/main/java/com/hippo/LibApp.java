package com.hippo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * Created by gurmail on 2019-10-18.
 *
 * @author gurmail
 */
abstract public class LibApp extends Application {
    /** Instance of the current application. */
    private static LibApp instance;

    /**
     * Constructor.
     */
    public LibApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        //HippoActivityLifecycleCallback.register(this);
        super.onCreate();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
//                                .setDefaultFontPath("fonts/TitilliumWeb-Regular.ttf")
                                .setDefaultFontPath("fonts/ProximaNova-Reg.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

    public static synchronized LibApp getInstance() {
        return instance;
    }

    abstract public void openMainScreen();

    abstract public void onCallBtnClick(Context context, int callType, Long channelId, Long userId, boolean isAgentFlow,
                                     boolean isAllowCall, String fullname, String image, String myImagePath);

    abstract public void screenOpened(String screenName);

    abstract public void trackEvent(String category, String action, String label);
}

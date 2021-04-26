package com.hippo.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.CommonResponse;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.HippoLog;

import com.hippo.utils.fileUpload.FileuploadModel;
import com.hippo.utils.fileUpload.Prefs;
import com.hippo.utils.filepicker.MyForeGroundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by rajatdhamija  14/12/17.
 */

public class FuguBaseActivity extends AppCompatActivity implements FuguAppConstant {
    private static final String TAG = FuguBaseActivity.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor();
        }
        uncaughtExceptionError();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                registerReceiver(new FuguNetworkStateReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                HippoLog.e(TAG, "Error in broadcasting");
            }
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();


        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // finally change the color
        window.setStatusBarColor(CommonData.getColorConfig().getHippoStatusBar());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CommonData.getColorConfig().getHippoStatusBar());
        }*/
    }

    public Type fileuploadType = new TypeToken<List<FileuploadModel>>() {
    }.getType();

    protected void checkAutoUpload() {
        if (!isMyServiceRunning(MyForeGroundService.class)) {
            ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(Prefs.with(this).getString(FuguAppConstant.KEY, ""), fileuploadType);
            if (fileuploadModels == null)
                return;
            HippoLog.e("TAG", "fileuploadModels data = " + new Gson().toJson(fileuploadModels));
            if (fileuploadModels.size() > 0) {
                Intent intent = new Intent(getBaseContext(), MyForeGroundService.class);
                intent.setAction("start");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    //lower then Oreo, just start the service.
                    startService(intent);
                }
            }
        }
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Uncaught Exception encountered
     */
    private void uncaughtExceptionError() {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        //Do your own error handling here
                        HippoLog.e("unCaughtException paramThread", "---> " + paramThread.toString());
                        HippoLog.e("unCaughtException paramThrowable", "---> " + paramThrowable.toString());
                        StringWriter stackTrace = new StringWriter();
                        paramThrowable.printStackTrace(new PrintWriter(stackTrace));
                        HippoLog.e("unCaughtException stackTrace", "---> " + stackTrace);
                        System.err.println(stackTrace);
                        if (!HippoConfig.DEBUG)
                            apiSendError(stackTrace.toString());
                    }
                });
    }

    /**
     * APi to send error messages to server
     *
     * @param logs log to be sent
     */
    public void apiSendError(String logs) {
        if (isNetworkAvailable()) {
            PackageInfo pInfo = null;
            try {
                pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            JSONObject error = new JSONObject();
            try {
                error.put("log", logs);
                if (pInfo != null) {
                    error.put("version", pInfo.versionCode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put(FuguAppConstant.APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
            params.put(FuguAppConstant.DEVICE_TYPE, ANDROID_USER);
            params.put(APP_VERSION, getVersionCode());
            params.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(FuguBaseActivity.this));
            params.put(FuguAppConstant.ERROR, error.toString());

            CommonParams commonParams = new CommonParams.Builder()
                    .putMap(params)
                    .build();

            RestClient.getApiInterface().sendError(commonParams.getMap())
                    .enqueue(new ResponseResolver<CommonResponse>(FuguBaseActivity.this, false, true) {
                        @Override
                        public void success(CommonResponse commonResponse) {
                            HippoLog.v("success", commonResponse.toString());
                        }

                        @Override
                        public void failure(APIError error) {
                            HippoLog.v("failure", error.toString());
                        }
                    });
        }
    }

    public String getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            return info.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * Check Network Connection
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set toolbar data
     *
     * @param toolbar  toolbar instance
     * @param title    title to be displayed
     * @param subTitle subtitle to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(Toolbar toolbar, String title, String subTitle) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(CommonData.getColorConfig().getHippoActionBarBg()));
            toolbar.setTitleTextColor(CommonData.getColorConfig().getHippoActionBarText());
            toolbar.setSubtitleTextColor(CommonData.getColorConfig().getHippoActionBarText());
            ab.setHomeAsUpIndicator(R.drawable.hippo_ic_arrow_back);
            if (HippoConfig.getInstance().getHomeUpIndicatorDrawableId() != -1)
                ab.setHomeAsUpIndicator(HippoConfig.getInstance().getHomeUpIndicatorDrawableId());

            ab.setTitle(title);
            ab.setSubtitle(subTitle);

        }
        return getSupportActionBar();
    }

    /**
     * Hide softkeyboard of opened
     *
     * @param activity
     */
    public void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set toolbar data
     *
     * @param toolbar toolbar instance
     * @param title   title to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(Toolbar toolbar, String title) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(CommonData.getColorConfig().getHippoActionBarBg()));
            ab.setHomeAsUpIndicator(R.drawable.hippo_ic_arrow_back);

            if (HippoConfig.getInstance().getHomeUpIndicatorDrawableId() != -1)
                ab.setHomeAsUpIndicator(HippoConfig.getInstance().getHomeUpIndicatorDrawableId());

            ab.setTitle("");

            toolbar.setTitleTextColor(CommonData.getColorConfig().getHippoActionBarText());

            ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setText(title);
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setTextColor(CommonData.getColorConfig().getHippoActionBarText());
        }
        return getSupportActionBar();
    }

    public ActionBar setToolbar(Toolbar toolbar, String title, boolean hideback) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(CommonData.getColorConfig().getHippoActionBarBg()));
            ab.setHomeAsUpIndicator(R.drawable.hippo_ic_arrow_back);

            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(false);

            if (HippoConfig.getInstance().getHomeUpIndicatorDrawableId() != -1)
                ab.setHomeAsUpIndicator(HippoConfig.getInstance().getHomeUpIndicatorDrawableId());

            ab.setTitle("");

            toolbar.setTitleTextColor(CommonData.getColorConfig().getHippoActionBarText());

            titleView = (TextView) toolbar.findViewById(R.id.tv_toolbar_name);
            titleView.setText(title);
            titleView.setTextColor(CommonData.getColorConfig().getHippoActionBarText());
        }
        return getSupportActionBar();
    }

    public ActionBar setToolbarNew(Toolbar toolbar, String title) {
        ActionBar actionBar = setToolbarNew(toolbar, title, true);
        return actionBar;
    }

    public ActionBar setToolbarNew(Toolbar toolbar, String title, boolean flag) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(flag);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setText(title);

        return getSupportActionBar();
    }

    private TextView titleView;

    public void updateTitle(Toolbar toolbar, String title) {
        if (titleView == null)
            titleView = (TextView) toolbar.findViewById(R.id.tv_toolbar_name);

        titleView.setText(title);
    }

    public void showErrorMessage(final String errorMessage, final String positiveButtonText) {
        showErrorMessage(errorMessage, positiveButtonText, false);
    }

    public void showErrorMessage(final String errorMessage, final String positiveButtonText, final boolean isFinish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(FuguBaseActivity.this)
                        .setMessage(errorMessage)
                        .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                if (isFinish)
                                    finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (HippoConfig.getInstance().getLifeCyclerListener() != null) {
            HippoConfig.getInstance().getLifeCyclerListener().onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HippoConfig.getInstance().getLifeCyclerListener() != null) {
            HippoConfig.getInstance().getLifeCyclerListener().onResume();
        }
    }

}

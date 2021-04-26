package com.hippo.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.support.Utils.Constants;
import com.hippo.support.callback.OnActionTypeCallback;
import com.hippo.support.fragment.HippoSupportDetailFragment;
import com.hippo.support.fragment.HippoSupportFragment;
import com.hippo.support.model.Item;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.hippo.support.Utils.SupportKeys.SupportKey.DEFAULT_SUPPORT;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_DATA;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_ID;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_DB_VERSION;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_PATH;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_POWERED_VIA;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_TITLE;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_TRANSACTION_ID;

/**
 * Created by Gurmail S. Kang on 29/03/18.
 * @author gurmail
 */

public class HippoSupportActivity extends AppCompatActivity implements OnActionTypeCallback {

    private static final String TAG = HippoSupportActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_support_activity);

        String faqName = getIntent().getStringExtra(FuguAppConstant.SUPPORT_ID);
        String transactionId = getIntent().getStringExtra(FuguAppConstant.SUPPORT_TRANSACTION_ID);


        initView(faqName, transactionId);
    }

    private void initView(String faqName, String transactionId) {
        if(TextUtils.isEmpty(faqName))
            faqName = CommonData.getDefaultCategory();

        CommonData.clearPathList();
        loadFragment(null, null, null, 0, faqName,
                transactionId, null, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hippo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard(HippoSupportActivity.this);
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Open list fragment
     * @param items ArrayList of data for showing into recyclerview
     * @param path Contails User path history till current page
     * @param title Toolbar title text
     * @param support_db_version Currently we're not using this key for compaire local and server db version
     * @param faqName Default support category for first time to show
     * @param transactionId Any particular transaction id if provided by developer
     * @param categoryData Selected {@link com.hippo.support.model.Category}  category object
     */
    private void loadFragment(ArrayList<Item> items, String path, String title, int support_db_version,
                              String faqName, String transactionId, String categoryData, boolean powerFlag) {
        HippoSupportFragment fragment = new HippoSupportFragment();

        Bundle bundle = new Bundle();
        if(items != null) {
            try {
                bundle.putString(DEFAULT_SUPPORT, new Gson().toJson(items, Constants.listType));
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString(DEFAULT_SUPPORT, new Gson().toJson(items));
            }

        }
        if(!TextUtils.isEmpty(path))
            bundle.putString(SUPPORT_PATH, path);
        if(!TextUtils.isEmpty(title))
            bundle.putString(SUPPORT_TITLE, title);
        if(support_db_version>0)
            bundle.putInt(SUPPORT_DB_VERSION, support_db_version);
        if(!TextUtils.isEmpty(faqName))
            bundle.putString(SUPPORT_CATEGORY_ID, faqName);
        if(!TextUtils.isEmpty(transactionId))
            bundle.putString(SUPPORT_TRANSACTION_ID, transactionId);

        bundle.putString(SUPPORT_CATEGORY_DATA, categoryData);
        bundle.putBoolean(SUPPORT_POWERED_VIA, powerFlag);

        fragment.setArguments(bundle);
        openFragment(fragment);
    }

    private void openFragment(Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.container, fragment, fragment.getClass().getName());
        transaction.addToBackStack(fragment.getClass().getName());
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onActionType(ArrayList<Item> items, String path, String title, String transactionId, String categoryData) {
        loadFragment(items, path, title, -1,"", transactionId, categoryData, false);
    }

    @Override
    public void openDetailPage(Item items, String path, String transactionId, String categoryData) {
        HippoSupportDetailFragment detailFragment = new HippoSupportDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DEFAULT_SUPPORT, new Gson().toJson(items));
        if(!TextUtils.isEmpty(path))
            bundle.putString(SUPPORT_PATH, path);
        if(!TextUtils.isEmpty(transactionId))
            bundle.putString(SUPPORT_TRANSACTION_ID, transactionId);

        bundle.putString(SUPPORT_CATEGORY_DATA, categoryData);

        detailFragment.setArguments(bundle);

        openFragment(detailFragment);
    }

    @Override
    public void removeFragment() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount()-1; ++i) {
            fm.popBackStack();
        }
    }

    /**
     * Hide soft keyboard of opened
     * @param activity
     */
    private void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if(view == null)
                return;
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

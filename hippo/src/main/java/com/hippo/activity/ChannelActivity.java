package com.hippo.activity;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import android.view.View;
import com.hippo.HippoConfig;
import com.hippo.LibApp;
import com.hippo.R;
import com.hippo.adapter.TabAdapter;
import com.hippo.fragment.CampaignFragment;
import com.hippo.fragment.ChannelTypeFragment;
import com.hippo.langs.Restring;
import com.hippo.utils.CustomViewPager;

import faye.ConnectionManager;

/**
 * Created by gurmail on 2020-01-10.
 *
 * @author gurmail
 */
public class ChannelActivity extends FuguBaseActivity implements ViewPager.OnPageChangeListener {

    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private boolean isPromotionalPush;
    private boolean hasPager;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channle_pager);

        try {
            LibApp.getInstance().screenOpened("List Screen");
        } catch (Exception e) {

        }

        try {
            if(HippoConfig.getInstance().getMobileCampaignBuilder() != null) {
                hasPager = HippoConfig.getInstance().getMobileCampaignBuilder().hasCampaignPager();
            }
        } catch (Exception e) {
            if(HippoConfig.DEBUG)
                e.printStackTrace();
        }

        isPromotionalPush = getIntent().getBooleanExtra("is_promotional_push", false);
        viewPager = findViewById(R.id.pagerView);
        tabLayout = findViewById(R.id.tabs);
        if(!hasPager) {
            tabLayout.setVisibility(View.GONE);
        }

        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(this);

        loadPage();

        ConnectionManager.INSTANCE.initFayeConnection();
    }


    private TabAdapter pagerAdapter;
    //ArrayList<Fragment> pagerFragments = new ArrayList<>();
    String[] titles = new String[2];
    private int[] tabIcons = {
            R.drawable.ic_chat_tab,
            R.drawable.ic_btn_notification
    };

    private void loadPage() {
        titles[0] = Restring.getString(ChannelActivity.this, R.string.hippo_chats);
        titles[1] = Restring.getString(ChannelActivity.this, R.string.hippo_notifications);


        pagerAdapter = new TabAdapter(getSupportFragmentManager(), this);
        pagerAdapter.addFragment(new ChannelTypeFragment(), titles[0], tabIcons[0]);
        if(hasPager) {
            pagerAdapter.addFragment(new CampaignFragment(), titles[1], tabIcons[1]);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        viewPager.setAdapter(pagerAdapter);
        // Give the TabLayout the ViewPager

        if(hasPager) {
            tabLayout.setupWithViewPager(viewPager);
            highLightCurrentTab(0);
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    super.onTabReselected(tab);
                }

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    super.onTabUnselected(tab);
                }
            });
        }



        if(isPromotionalPush && hasPager) {
            isPromotionalPush = false;
            viewPager.setCurrentItem(1);
        }
    }

    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(pagerAdapter.getSelectedTabView(position));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.removeOnPageChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.position = position;
    }

    @Override
    public void onPageSelected(int position) {
        highLightCurrentTab(position);
        try {
            LibApp.getInstance().trackEvent("List Screen", "Tab button clicked", ""+position);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        intent.addAction(NOTIFICATION_TAPPED);
        return intent;
    }
}

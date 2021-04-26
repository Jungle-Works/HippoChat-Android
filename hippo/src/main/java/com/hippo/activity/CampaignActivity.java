package com.hippo.activity;

import android.content.Intent;
import android.os.Bundle;

import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.fragment.CampaignFragment;

/**
 * Created by gurmail on 2020-02-10.
 *
 * @author gurmail
 */
public class CampaignActivity extends FuguBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_campaign);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new CampaignFragment(), CampaignFragment.class.getName())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HippoConfig.getInstance().setAnnouncementActivity(true);

    }
}
package com.hippo.activity;

import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.model.HippoUserProfileModel;
import com.hippo.utils.hippoHeaderBehavior.HippoHeaderView;

/**
 * Created by gurmail on 2019-11-26.
 *
 * @author gurmail
 */
public class ProfileActivityNew extends FuguBaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private CollapsingToolbarLayout collapsingToolbar;
    protected HippoHeaderView toolbarHeaderView;
    protected HippoHeaderView floatHeaderView;
    protected AppBarLayout appBarLayout;
    protected Toolbar toolbar;
    private ImageView image;
    private LinearLayout contentLayout, descFields;
    private View view;
    private HippoColorConfig hippoColorConfig;

    private HippoUserProfileModel profileModel;
    private boolean isHideToolbarView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_profile_new);

//        toolbarHeaderView = findViewById(R.id.toolbar_header_view);
//        floatHeaderView = findViewById(R.id.float_header_view);
//        appBarLayout = findViewById(R.id.appbar);
//        toolbar = findViewById(R.id.toolbar);
//
//        toolbarHeaderView.bindTo("qwertyui", "fghjk");
//        toolbarHeaderView.bindTo("qwertyui", "fghjk");

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
    int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

//        if (percentage == 1f && isHideToolbarView) {
//            toolbarHeaderView.setVisibility(View.VISIBLE);
//            isHideToolbarView = !isHideToolbarView;
//
//        } else if (percentage < 1f && !isHideToolbarView) {
//            toolbarHeaderView.setVisibility(View.GONE);
//            isHideToolbarView = !isHideToolbarView;
//        }
    }
}

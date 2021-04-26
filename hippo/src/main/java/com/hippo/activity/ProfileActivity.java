package com.hippo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.AgentInfoResponse;
import com.hippo.model.CustomField;
import com.hippo.model.HippoUserProfileModel;
import com.hippo.model.Image;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.RoundedCornersTransformation;
import com.hippo.utils.hippoHeaderBehavior.HippoHeaderView;
import com.hippo.utils.loadingBox.ProgressWheel;

/**
 * Created by gurmail on 2019-11-25.
 *
 * @author gurmail
 */
public class ProfileActivity extends FuguBaseActivity implements AppBarLayout.OnOffsetChangedListener {

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

    private TextView description, descTitle, title_name;
    private ProgressWheel progressWheel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_profile);

        profileModel = (HippoUserProfileModel) getIntent().getSerializableExtra("profileModel");
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbarHeaderView = findViewById(R.id.toolbar_header_view);
        floatHeaderView = findViewById(R.id.float_header_view);
        appBarLayout = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        contentLayout = findViewById(R.id.contentLayout);
        descFields = findViewById(R.id.descFields);
        descFields.setVisibility(View.GONE);
        image = findViewById(R.id.image);

        progressWheel = findViewById(R.id.progress_wheel);

        hippoColorConfig = CommonData.getColorConfig();

        descTitle = findViewById(R.id.desc_title);
        description = findViewById(R.id.description);

        descTitle.setTextColor(hippoColorConfig.getHippoProfileTitle());
        description.setTextColor(hippoColorConfig.getHippoProfileValue());

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarHeaderView.setVisibility(View.GONE);

        initUi();

        getAgentInfo();

    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!TextUtils.isEmpty(profileModel.getImageUrl())) {
                        Intent profileImageIntent = new Intent(ProfileActivity.this, ImageDisplayActivityNew.class);
                        Image profileImage = new Image(profileModel.getImageUrl(), profileModel.getImageUrl(), "imageOne", "", "");
                        profileImageIntent.putExtra("image", profileImage);
                        profileImageIntent.putExtra("hide_download", true);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this,
                                image, "imageOne");
                        startActivity(profileImageIntent, options.toBundle());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loadImage();
        toolbarHeaderView.bindTo(profileModel.getTitle(), "");
        floatHeaderView.bindTo(profileModel.getTitle(), "");
    }

    private void loadImage() {
        RequestOptions myOptions = RequestOptions
                .bitmapTransform(new RoundedCornersTransformation(ProfileActivity.this, 4, 1))
                .placeholder(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.hippo_placeholder))
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.hippo_placeholder));
        Glide.with(ProfileActivity.this).load(profileModel.getImageUrl())
                .apply(myOptions)
                .into(image);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    private void getAgentInfo() {

        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();

        CommonParams.Builder params = new CommonParams.Builder();
        params.add("app_secret_key", HippoConfig.getInstance().getAppKey());
        params.add("en_user_id", profileModel.getEnUserId());
        if(TextUtils.isEmpty(profileModel.getUserId())) {
            params.add("channel_id", profileModel.getChannelID());
        } else {
            getSavedData(profileModel.getUserId());
            params.add("agent_id", profileModel.getUserId());
        }

        CommonParams commonParams = params.build();


        RestClient.getApiInterface().getAgentInfo(commonParams.getMap()).enqueue(new ResponseResolver<AgentInfoResponse>(this, false, false) {
            @Override
            public void success(AgentInfoResponse commonResponse) {

                try {
                    showData(commonResponse);
                    if(!TextUtils.isEmpty(commonResponse.getData().getUserId()))
                        CommonData.saveAgentData(commonResponse.getData().getUserId(), commonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressWheel.setVisibility(View.GONE);
                    String alert = Restring.getString(ProfileActivity.this, R.string.hippo_alert);
                    String text = Restring.getString(ProfileActivity.this, R.string.hippo_something_wentwrong);
                    new AlertDialog.Builder(ProfileActivity.this).setTitle(alert)
                            .setMessage(text).create().show();
                }
            }

            @Override
            public void failure(APIError error) {
                progressWheel.setVisibility(View.GONE);
                String alert = Restring.getString(ProfileActivity.this, R.string.hippo_alert);
                String text = Restring.getString(ProfileActivity.this, R.string.hippo_something_wentwrong);
                new AlertDialog.Builder(ProfileActivity.this).setTitle(alert)
                        .setMessage(text).create().show();
            }
        });
    }

    private void showData(AgentInfoResponse commonResponse) {
        try {
            progressWheel.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(commonResponse.getData().getDescription())) {
                descFields.setVisibility(View.VISIBLE);
                description.setText(commonResponse.getData().getDescription());
            } else {
                descFields.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(commonResponse.getData().getUserImage())) {
                profileModel.setImageUrl(commonResponse.getData().getUserImage());
                loadImage();
            }

            if(!TextUtils.isEmpty(commonResponse.getData().getRating())) {
                toolbarHeaderView.bindTo(profileModel.getTitle(), commonResponse.getData().getRating());
                floatHeaderView.bindTo(profileModel.getTitle(), commonResponse.getData().getRating());
            }

            if(commonResponse.getData().getCustomFields() != null) {
                contentLayout.removeAllViews();
                for (CustomField field : commonResponse.getData().getCustomFields()) {
                    if (field.getShowToCustomer() && !TextUtils.isEmpty(field.getValue())) {
                        contentLayout.addView(getViews(field.getDisplayName(), field.getValue()));
                    }
                }
            } else {
                contentLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSavedData(String agentId) {
        AgentInfoResponse infoResponse = CommonData.getSavedAgentData(agentId);
        if(infoResponse != null && !TextUtils.isEmpty(infoResponse.getData().getFullName())) {
            showData(infoResponse);
        }
    }

    private View getViews(String titleStr, String detailStr) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.hippo_content_main, null, false);
        TextView title = view.findViewById(R.id.title);
        TextView detail = view.findViewById(R.id.detail);

        title.setTextColor(hippoColorConfig.getHippoProfileTitle());
        detail.setTextColor(hippoColorConfig.getHippoProfileValue());

        title.setText(titleStr);
        detail.setText(detailStr);

        return view;
    }
}

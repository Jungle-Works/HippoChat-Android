package com.hippo.activity;


import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.model.Image;
import com.hippo.utils.DateUtils;
import com.hippo.utils.filepicker.Util;
import com.hippo.utils.loadingBox.ProgressWheel;
import com.hippo.utils.zoomview.ZoomageView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by gurmail on 21/01/19.
 *
 * @author gurmail
 */
public class ImageDisplayActivity extends FuguBaseActivity {

    private static final String TAG = ImageDisplayActivity.class.getSimpleName();
    private String imageUrl = "";

    ImageView ivImage, ivDownload;
    LinearLayout llTopBar;
    TextView tvDateTime;
    ZoomageView ivOriginalImage;
    ProgressWheel progressWheel;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_image_display);

        progressWheel = findViewById(R.id.progress);
        progressWheel.spin();

        mainLayout = findViewById(R.id.main_layout);
        ivImage = findViewById(R.id.ivMsgImage);
        ivDownload = findViewById(R.id.ivDownload);
        ivOriginalImage = findViewById(R.id.ivOriginalImage);
        llTopBar = findViewById(R.id.llTopBar);
        TextView tvChannelName  = findViewById(R.id.tvChannelName);
        tvDateTime = findViewById(R.id.tvDateTime);
        ImageView ivBack = findViewById(R.id.ivBack);

        final Image image = (Image) getIntent().getSerializableExtra("image");

        if (!TextUtils.isEmpty(image.getChannelName())) {
            tvChannelName.setText(image.getChannelName());
        } else {
            tvChannelName.setText("Message");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, hh:mm a");
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        try {
            String date = formatter.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.getDateTime())));
            if (android.text.format.DateUtils.isToday(sdf.parse(DateUtils.getInstance().convertToLocal(image.getDateTime())).getTime())) {
                String time = formatter2.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.getDateTime())));
                tvDateTime.setText("Today, "+time);
            } else {
                tvDateTime.setText(formatter.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.getDateTime()))));
            }
        } catch (Exception e) {
            tvDateTime.setVisibility(View.GONE);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(image.getImageUrl());
            }
        });

        supportPostponeEnterTransition();

        RequestOptions requestOptions = new RequestOptions().placeholderOf(R.drawable.hippo_placeholder)
                .dontAnimate()
                .onlyRetrieveFromCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.hippo_call_placeholder)
                .dontTransform();

        final RequestOptions requestOptions2 = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.hippo_call_placeholder)
                .dontTransform();

        final String url = image.getImageUrl();


        Glide.with(ImageDisplayActivity.this).load(image.getThumbnailUrl())
                .apply(requestOptions)
                .into(new CustomViewTarget<RelativeLayout, Drawable>(mainLayout) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        ivImage.setVisibility(View.GONE);
                        progressWheel.setVisibility(View.GONE);
                        Glide.with(ImageDisplayActivity.this).asBitmap()
                                .apply(requestOptions2)
                                .load(url)
                                .into(ivOriginalImage);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        supportStartPostponedEnterTransition();
                        ivImage.setVisibility(View.VISIBLE);
                        ivImage.setImageDrawable(resource);
                        progressWheel.setVisibility(View.GONE);
                        ObjectAnimator alphaAnimation = new ObjectAnimator().ofFloat(llTopBar, View.ALPHA, 0f, 1f);// .ofFloat(llTopBar, View.ALPHA, 0f, 1f)
                        alphaAnimation.setDuration(500);
                        alphaAnimation.setStartDelay(200);
                        alphaAnimation.start();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(ImageDisplayActivity.this).load(image.getImageUrl())
                                        .apply(requestOptions2)
                                        .into(new CustomViewTarget<RelativeLayout, Drawable>(mainLayout) {
                                            @Override
                                            protected void onResourceCleared(@Nullable Drawable placeholder) {

                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                ivImage.setVisibility(View.GONE);
                                                progressWheel.setVisibility(View.GONE);
                                                Glide.with(ImageDisplayActivity.this).asBitmap()
                                                        .apply(requestOptions2)
                                                        .load(url)
                                                        .into(ivOriginalImage);
                                            }

                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                supportStartPostponedEnterTransition();
                                                ivImage.setVisibility(View.GONE);
                                                ivOriginalImage.setImageDrawable(resource);
                                            }
                                        });
                            }
                        }, 500);
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                    }
                });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //supportFinishAfterTransition();
    }

    private Long downloadFile(String url) {
        String fileName = Util.extractFileNameWithoutSuffix(url);
        String ext = Util.getExtension(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String directory = Util.getOrCreateDirectoryPath(DocumentType.IMAGE.toString());
        request.setDestinationInExternalPublicDir(directory, fileName + ext);
        request.setMimeType("image/jpeg");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);



        if (manager != null) {
            return manager.enqueue(request);
        } else
            return null;
    }
}

package com.hippo.activity;

import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.Image;
import com.hippo.utils.HippoLog;
import com.hippo.utils.easypermissions.EasyPermissions;
import com.hippo.utils.filepicker.ToastUtil;
import com.hippo.utils.filepicker.Util;
import com.hippo.utils.photoview.OnDoubleTap;
import com.hippo.utils.photoview.PhotoView;
import com.hippo.utils.swipeLayout.frame.SwipeableLayout;
import com.hippo.utils.swipeLayout.listener.LayoutShiftListener;
import com.hippo.utils.swipeLayout.listener.OnLayoutPercentageChangeListener;
import com.hippo.utils.swipeLayout.listener.OnLayoutSwipedListener;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by gurmail on 2019-11-25.
 *
 * @author gurmail
 */
public class ImageDisplayActivityNew extends FuguBaseActivity implements EasyPermissions.PermissionCallbacks {

    private boolean hideDownloadBtn = false;
    private SwipeableLayout swipeableLayout;
    private PhotoView ivImageBig;
    private ImageView ivImageGif;
    private Toolbar toolbar;
    private ImageView ivBack;
    private FrameLayout colorContainer;
    private ImageView ivDownload;

    private Image imageUrl = null;
    private int swipedFromDefault = OnLayoutSwipedListener.SWIPE;
    private float lastPerc = 0f;
    private boolean isTopBarVisivle = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_new_image);

        imageUrl = (Image) getIntent().getSerializableExtra("image");
        hideDownloadBtn = getIntent().getBooleanExtra("hide_download", false);

        swipeableLayout = findViewById(R.id.swipeableLayout);
        ivImageBig = findViewById(R.id.ivImageBigNew);
        ivImageGif = findViewById(R.id.ivImageGif);
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        ivDownload = findViewById(R.id.ivDownload);
        colorContainer = findViewById(R.id.colorContainer);

        if (hideDownloadBtn) {
            ivDownload.setVisibility(View.GONE);
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
                if (checkPermission()) {
                    downloadFile(imageUrl.getImageUrl());
                } else {
                    requestPermission();
                }
            }
        });

        swipeableLayout.setOnLayoutPercentageChangeListener(new OnLayoutPercentageChangeListener() {
            @Override
            public void percentageX(float percentage) {
                super.percentageX(percentage);
            }

            @Override
            public void percentageY(float percentage) {
                super.percentageY(percentage);

                HippoLog.e("percent change", "percentage = " + percentage);
                if (lastPerc != 1.0f) {
                    colorContainer.setAlpha(1 - percentage);
                    toolbar.setAlpha(1 - percentage);
                }
                lastPerc = percentage;
            }
        });

        swipeableLayout.setLayoutShiftListener(new LayoutShiftListener() {
            @Override
            public void onLayoutShifted(float positionX, float positionY, boolean isTouched) {
                HippoLog.e("position Y", "positionY" + positionY);
                if (!isTouched && lastPerc != 1f && swipedFromDefault == OnLayoutSwipedListener.SWIPE) {
                    colorContainer.setAlpha(1f);
                    toolbar.setAlpha(1f);
                }
            }
        });

        swipeableLayout.setOnSwipedListener(new OnLayoutSwipedListener() {
            @Override
            public void onLayoutSwiped(int swipedFrom) {
                swipedFromDefault = swipedFrom;
                onBackPressed();
            }
        });

        ivImageBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTopBarVisivle) {
                    toolbar.animate().translationY(-150f).setDuration(100);
                } else {
                    toolbar.animate().translationY(0f).setDuration(100);
                }
                isTopBarVisivle = !isTopBarVisivle;
            }
        });

        RequestOptions requestOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform();

        supportPostponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivImageBig.setTransitionName(imageUrl.getTransitionName());
        }

        String fileExt = Util.getExtension(imageUrl.getImageUrl());
        if (!TextUtils.isEmpty(fileExt) && fileExt.equalsIgnoreCase("gif")) {
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 0f, 1f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setStartDelay(200);
            alphaAnimation.start();
            supportStartPostponedEnterTransition();

            ivImageGif.setVisibility(View.VISIBLE);
            ivImageBig.setVisibility(View.GONE);
            Glide
                    .with(ImageDisplayActivityNew.this)
                    .asGif()
                    .load(imageUrl.getImageUrl())
                    .error(R.drawable.hippo_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(ivImageGif);
        } else {
            Glide.with(ImageDisplayActivityNew.this).load(imageUrl.getImageUrl())
                    .apply(requestOptions)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 0f, 1f);
                            alphaAnimation.setDuration(500);
                            alphaAnimation.setStartDelay(200);
                            alphaAnimation.start();
                            supportStartPostponedEnterTransition();
                            ivImageBig.setImageDrawable(resource);
                        }
                    });
        }


        ivImageBig.setOnDoubleTap(new OnDoubleTap() {
            @Override
            public void onDoubleTap(float zoom) {
                if (zoom <= 1.0f) {
                    swipeableLayout.isSwipeable(true);
                } else {
                    swipeableLayout.isSwipeable(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (!TextUtils.isEmpty(imageUrl.getImageUrl())) {
//            Intent intent = new Intent();
//            intent.putExtra("imageUrl", imageUrl);
//            setResult(Activity.RESULT_OK, intent);
//        }

        supportFinishAfterTransition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private void downloadFile(String url) {
        try {
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

            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


            if (manager != null) {
                manager.enqueue(request);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void downloadcompleted() {
        try {
            ToastUtil.getInstance(ImageDisplayActivityNew.this).showToast(Restring.getString(ImageDisplayActivityNew.this, R.string.download_complete));
            if (onComplete != null) {
                unregisterReceiver(onComplete);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            if (intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                downloadcompleted();
            }
        }
    };

    private void requestPermission() {
        String text = Restring.getString(ImageDisplayActivityNew.this, R.string.vw_rationale_storage);
        if (SDK_INT >= Build.VERSION_CODES.R)
            EasyPermissions.requestPermissions(this, text, RC_READ_EXTERNAL_STORAGE,
                    "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");
        else
            EasyPermissions.requestPermissions(this, text, RC_READ_EXTERNAL_STORAGE,
                    "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R)
            return EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");
        else
            return EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (imageUrl != null && !TextUtils.isEmpty(imageUrl.getImageUrl()))
            downloadFile(imageUrl.getImageUrl());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        String text = Restring.getString(ImageDisplayActivityNew.this, R.string.hippo_storage_permission);
        ToastUtil.getInstance(this).showToast(text);
    }
}

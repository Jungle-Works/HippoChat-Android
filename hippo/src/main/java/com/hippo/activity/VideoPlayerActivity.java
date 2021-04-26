package com.hippo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;

import com.hippo.R;
import com.hippo.langs.Restring;
import com.hippo.utils.easypermissions.AfterPermissionGranted;
import com.hippo.utils.easypermissions.AppSettingsDialog;
import com.hippo.utils.easypermissions.EasyPermissions;
import com.hippo.utils.fileUpload.FileManager;
import com.hippo.utils.fileUpload.FileuploadModel;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by gurmail on 17/01/19.
 *
 * @author gurmail
 */
public class VideoPlayerActivity extends FuguBaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = VideoPlayerActivity.class.getSimpleName();
    private VideoView videoView;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        videoView = findViewById(R.id.videoView);
        readExternalStorage();
        String text = Restring.getString(VideoPlayerActivity.this, R.string.fugu_video);
        String name = TextUtils.isEmpty(getIntent().getStringExtra("title")) ? text : getIntent().getStringExtra("title");
        setToolbar(myToolbar, name);

    }

    private void permissionGranted() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        mediaController.setEnabled(true);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("url")));
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });


        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String text = Restring.getString(VideoPlayerActivity.this, R.string.hippo_file_not_supported);
                String ok = Restring.getString(VideoPlayerActivity.this, R.string.fugu_ok);
                showErrorMessage(text, ok, getIntent().getStringExtra("url"));
                return true;
            }
        });
    }

    public void showErrorMessage(final String errorMessage, final String positiveButtonText, final String localPath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cancel = Restring.getString(VideoPlayerActivity.this, R.string.fugu_cancel);
                new AlertDialog.Builder(VideoPlayerActivity.this)
                        .setMessage(errorMessage)
                        .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                openFile(localPath);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void openFile(String localPath) {
        if (!TextUtils.isEmpty(localPath)) {
            FileManager.getInstance().openFileInDevice(VideoPlayerActivity.this, localPath, new FileManager.FileCopyListener() {
                @Override
                public void onCopingFile(boolean flag, FileuploadModel fileuploadModel) {

                }

                @Override
                public void largeFileSize() {

                }

                @Override
                public void onError() {
                    String text = Restring.getString(VideoPlayerActivity.this, R.string.no_handler);
                    Toast.makeText(VideoPlayerActivity.this, text, Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //hideKeyboard(VideoPlayerActivity.this);
            onBackPressed(); // close this context and return to preview context (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void readExternalStorage() {
        boolean isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        if (SDK_INT >= Build.VERSION_CODES.R)
            isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");

        if (isGranted) {
            permissionGranted();
        } else {
            String text = Restring.getString(VideoPlayerActivity.this, R.string.vw_rationale_storage);
            if (SDK_INT >= Build.VERSION_CODES.R)
                EasyPermissions.requestPermissions(this, text,
                        RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE");
            else
                EasyPermissions.requestPermissions(this, text,
                        RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        permissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // If Permission permanently denied, ask user again


        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains("android.permission.MANAGE_EXTERNAL_STORAGE")) {
                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(1).build().show();

            } else
                new AppSettingsDialog.Builder(this).setIsManageStoragePermission(0).build().show();
        } else {
            finish();
        }


//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        } else {
//            finish();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.MANAGE_EXTERNAL_STORAGE")) {
                    permissionGranted();
                } else {
                    finish();
                }
            } else {
                if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    permissionGranted();
                } else {
                    finish();
                }
            }
        }
    }
}

package com.hippo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.utils.filepicker.Constant;
import com.hippo.utils.filepicker.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class FuguImageUtils implements FuguAppConstant {

    private static final String TAG = FuguImageUtils.class.getSimpleName();
    private Activity activity;

    public FuguImageUtils(Activity activity) {
        this.activity = activity;
    }

    /**
     * Method to start the Camera
     */
    public void startCamera() {
        HippoLog.e(TAG, "startCamera");

        /*  Check whether the Camera feature is available or not    */
        if (!isCameraAvailable()) {
            Toast.makeText(activity, "Camera feature unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }

        /*  Check for the SD CARD or External Storage   */
        if (!isExternalStorageAvailable()) {
            Toast.makeText(activity, "External storage unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            String timeStamp = new SimpleDateFormat("ddMMyyyy_hhmmss", Locale.ENGLISH).format(new Date());
            String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
            String fileName = "Hippochat_" + muid + ".jpg";
            File fileToBeWritten = new File(Util.getDirectoryPath(FOLDER_TYPE.get(IMAGE_FOLDER)), fileName);
            HippoLog.d(TAG, "Path: "+fileToBeWritten.getPath());
            HippoLog.d(TAG, "AbsolutePath: "+fileToBeWritten.getAbsolutePath());
            CommonData.setTime(fileToBeWritten.getAbsolutePath());
            CommonData.setImageMuid(muid);
            try {
                File file = fileToBeWritten.getParentFile();
                if (!file.exists()) {
                    file.mkdirs();
                }
//                File fileToBeWritten = new File(Util.getDirectoryPath(FOLDER_TYPE.get(IMAGE_FOLDER)), fileName);
                fileToBeWritten.createNewFile();
                //File image = File.createTempFile(fileName, ".jpg", new File(Util.getDirectoryPath(FOLDER_TYPE.get(IMAGE_FOLDER))));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (!fileToBeWritten.exists()) {
//                try {
//                    fileToBeWritten.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, CommonData.getProvider(), fileToBeWritten));
            activity.startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_TAKE_IMAGE);
        }
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isCameraAvailable() {

        HippoLog.e(TAG, "isCameraAvailable");

        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isExternalStorageAvailable() {

        HippoLog.e(TAG, "isExternalStorageAvailable");

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}

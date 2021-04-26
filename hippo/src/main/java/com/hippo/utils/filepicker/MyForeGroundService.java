package com.hippo.utils.filepicker;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.BuildConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.aws.AwsData;
import com.hippo.aws.ImageUploadInterface;
import com.hippo.aws.UploadTask;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.interfaces.fayeClient;
import com.hippo.langs.Restring;
import com.hippo.model.FuguUploadImageResponse;
import com.hippo.model.Message;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.MultipartParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.DateUtils;
import com.hippo.utils.HippoLog;
import com.hippo.utils.fileUpload.FileuploadModel;
import com.hippo.utils.fileUpload.Prefs;
import com.hippo.utils.fileUpload.ProgressRequestBody;

import faye.ConnectionManager;
import faye.FayeClient;
import faye.FayeServiceListener;
import okhttp3.MultipartBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by gurmail on 16/01/19.
 *
 * @author gurmail
 */
public class MyForeGroundService extends Service implements FuguAppConstant, ProgressRequestBody.UploadCallbacks {

    private static final String TAG = MyForeGroundService.class.getSimpleName();

    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";

    private Handler h;
    private Runnable r;
    //FayeClient mClient = null;

    int counter = 0;
    private Type fileuploadType = new TypeToken<List<FileuploadModel>>() {
    }.getType();
    NotificationManager manager;
    NotificationCompat.Builder builder;

    FileuploadModel fileuploadModel;
    ArrayList<FileuploadModel> fileuploadModels = new ArrayList<>();
    boolean apiInProgress;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification() {
        counter++;
        Context context = getApplicationContext();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "hippo_file_uploading";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("File uploading");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setProgress(100, counter, false);

        return builder.setContentTitle(Restring.getString(this, R.string.uploading))
                .setTicker("")
                .setOnlyAlertOnce(true)
                .setContentText(Restring.getString(this, R.string.uploading_in_progress))
                .setSmallIcon(R.drawable.hippo_default_notif_icon)
                .setOngoing(true).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null && intent.getAction().contains("start")) {
            startForeground(101, updateNotification());
            uploadFileServerCall();
        } else {
            stopFayeClient();
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    // TODO: 2020-04-28 check this
    public void stopFayeClient() {
        /*if(mClient == null)
            return;
        try {
            HandlerThread thread = new HandlerThread("TerminateThread");
            thread.start();
            new Handler(thread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mClient.setServiceListener(null);
//                    if (mClient.isConnectedServer()) {
//                        mClient.disconnectServer();
//                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void updateProgress(int uploaded, int total) {
        if (builder != null && manager != null) {
            builder.setProgress(total, uploaded, false);
            // Issues the notification
            manager.notify(101, builder.build());
        } else {
            startForeground(101, updateNotification());
        }
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


    private void uploadFileServerCall() {
        try {
            if (apiInProgress) {
                HippoLog.e(TAG, "***************ApiInProgress*************");
                return;
            }
            if (isNetworkAvailable()) {
                String data = Prefs.with(this).getString(KEY, "");
                HippoLog.v(TAG, "uploadFileServerCall 1: " + data);
                fileuploadModels = new Gson().fromJson(data, fileuploadType);

                if (fileuploadModels == null || fileuploadModels.size() == 0) {
                    Prefs.with(this).remove(KEY);
                    stopFayeClient();
                    stopForeground(true);
                    stopSelf();
                    return;
                }

                fileuploadModel = fileuploadModels.get(0);
                isFirstTime = true;
                ProgressRequestBody fileBody = new ProgressRequestBody(new File(fileuploadModel.getFilePath()), this,
                        getMimeType(fileuploadModel.getFilePath()), fileuploadModel.getFilePath(), fileuploadModel.getMessageIndex(),
                        fileuploadModel.getMuid());
                MultipartBody.Part filePart =
                        MultipartBody.Part.createFormData("file", fileuploadModel.getFileName(), fileBody);

//                MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
//                MultipartParams multipartParams = multipartBuilder
//                        .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
//                        .add(APP_VERSION, BuildConfig.VERSION_NAME)
//                        .add(DEVICE_TYPE, 1)
//                        .add("file_name", fileuploadModel.getFileName())
//                        .build();

                apiInProgress = true;


                HashMap<String, Object> map = new HashMap<>();

                map.put("app_secret_key", HippoConfig.getInstance().getAppKey());
                map.put("file_name", fileuploadModel.getFileName());
                map.put("file_type", getMimeType(fileuploadModel.getFilePath()));
                map.put("source", "3");

//                if (HippoConfig.getInstance().isShareAllFileTypes())
                map.put("allow_all_mime_type", true);
                map.put("restrict_mime_type", !CommonData.getAttributes().shareAllFileTypes());

                RestClient.getApiInterface().getUploadedFile(map).enqueue(new ResponseResolver<AwsData>() {
                    @Override
                    public void success(final AwsData awsData) {

                        if (getMimeType(fileuploadModel.getFilePath()).contains("video")) {
                            try {
                                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(fileuploadModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                                File f = new File(getApplication().getCacheDir(), awsData.getData().getThumbnail_file_name());

                                f.createNewFile();


//Convert bitmap to byte array
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bMap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
                                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                                FileOutputStream fos = new FileOutputStream(f);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                                Log.e("upload thumb", "thumb");

                                new UploadTask(awsData.getData().getThumbnail_url(), null, f, new ImageUploadInterface() {
                                    @Override
                                    public void sucessfullupload() {
                                        uploadTask(awsData);
                                    }

                                    @Override
                                    public void failureUpload(Throwable throwable) {
                                        handleException();
                                    }
                                }).execute();
                            } catch (Exception e) {
                                uploadTask(awsData);
                                e.printStackTrace();
                            }

                        } else {
                            uploadTask(awsData);
                        }


                    }

                    @Override
                    public void failure(APIError error) {
                        Log.e(TAG, "In uploading failure");
                        if (error.getStatusCode() == 400)
                            handleException(true, error.getMessage());
                        else
                            handleException();
                    }
                });

            } else {
                HippoLog.e(TAG, "In uploading else");
                handleException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            HippoLog.e(TAG, "In uploading else 2");
            handleException();
        }
    }

    private void uploadTask(final AwsData awsData) {
        new UploadTask(awsData.getData().getUrl(), fileuploadModel.getFilePath(), null, new ImageUploadInterface() {
            @Override
            public void sucessfullupload() {
                String image_url = awsData.getData().getSource_url();
                String thumbnail_url = awsData.getData().getSource_url();
                if (getMimeType(fileuploadModel.getFilePath()).contains("video")) {
                    thumbnail_url = awsData.getData().getThumbnail_source_url();
                }
                String data = Prefs.with(MyForeGroundService.this).getString(KEY, "");
                HippoLog.v(TAG, "In response: " + data);
                fileuploadModels = new Gson().fromJson(data, fileuploadType);


                JSONObject jsonObject = fileuploadModels.get(0).getMessageObject();

                if (fileuploadModel.isTicketAttachmentFile())
                    jsonObject = new JSONObject();
                try {
                    jsonObject.put(IMAGE_URL, image_url);
                    jsonObject.put(THUMBNAIL_URL, thumbnail_url);
                    jsonObject.put("url", image_url);
                    jsonObject.remove("local_url");
                } catch (JSONException e) {
                    if (HippoConfig.DEBUG)
                        e.printStackTrace();
                }
                fileuploadModels.get(0).setFileUploaded(true);
                fileuploadModels.get(0).setMessageObject(jsonObject);

                if (fileuploadModel.isTicketAttachmentFile()) {
                    data = new Gson().toJson(fileuploadModels, fileuploadType);
                    HippoLog.e(TAG, "In response else case " + data);
                    Prefs.with(MyForeGroundService.this).save(KEY, data);

                    fileuploadModel = fileuploadModels.get(0);

                    Intent mIntent = new Intent(FuguAppConstant.HIPPO_FILE_UPLOAD);
                    mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.UPLOADED_SUCESSFULLY);
                    mIntent.putExtra("muid", fileuploadModel.getMuid());
                    mIntent.putExtra("messageIndex", fileuploadModel.getMessageIndex());
                    mIntent.putExtra("image_url", image_url);
                    mIntent.putExtra("thumbnail_url", thumbnail_url);
                    mIntent.putExtra("fileuploadModel", new Gson().toJson(fileuploadModel));
                    LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
                    updatePref();

                } else if (fileuploadModels.get(0).getChannelId().intValue() < 1) {

                    Intent mIntent = new Intent(FuguAppConstant.HIPPO_FILE_UPLOAD);
                    mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.CREATE_CHANNEL);
                    mIntent.putExtra("fileuploadModel", new Gson().toJson(fileuploadModels.get(0)));
                    LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);

                    updatePref();

                } else {
                    data = new Gson().toJson(fileuploadModels, fileuploadType);
                    HippoLog.e(TAG, "In response else case " + data);
                    Prefs.with(MyForeGroundService.this).save(KEY, data);

                    fileuploadModel = fileuploadModels.get(0);

                    if (isOpenedChatActivityMsg(fileuploadModel.getChannelId())) {
                        Intent mIntent = new Intent(FuguAppConstant.HIPPO_FILE_UPLOAD);
                        mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.UPLOADED_SUCESSFULLY);
                        mIntent.putExtra("channelId", fileuploadModel.getChannelId());
                        mIntent.putExtra("muid", fileuploadModel.getMuid());
                        mIntent.putExtra("messageIndex", fileuploadModel.getMessageIndex());
                        mIntent.putExtra("image_url", image_url);
                        mIntent.putExtra("thumbnail_url", thumbnail_url);
                        mIntent.putExtra("fileuploadModel", new Gson().toJson(fileuploadModel));
                        LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
                        //getClient(fileuploadModel.getChannelId(), jsonObject);
                        updatePref();

                    } else {
                        updateLocalMessageObj(fileuploadModel.getChannelId(), false, image_url, thumbnail_url, jsonObject, new UpdateLocalMsgListener() {
                            @Override
                            public void onUpdation(JSONObject jsonObject) {
                                apiInProgress = false;
                                getClient(fileuploadModel.getChannelId(), jsonObject);
                            }
                        });
                    }
                }

            }

            @Override
            public void failureUpload(Throwable throwable) {

            }
        }).execute();
    }

    private void handleException() {
        handleException(false, "");
    }

    private void handleException(boolean isFileNotsupported, String errorMessage) {
        Log.e(TAG, "In handleException failure");
        try {
            String data = Prefs.with(this).getString(KEY, "");
            Log.e(TAG, "In response else case " + data);
            fileuploadModels = new Gson().fromJson(data, fileuploadType);
            if (fileuploadModels == null || fileuploadModels.size() == 0) {
                Prefs.with(this).remove(KEY);
                stopFayeClient();
                stopForeground(true);
                stopSelf();
                return;
            }
            fileuploadModel = fileuploadModels.get(0);
            apiInProgress = false;
            if (isFileNotsupported) {
                Intent mIntent = new Intent(HIPPO_FILE_UPLOAD);
                mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.FILE_TYPE_NOT_ALLOWED);
                mIntent.putExtra("channelId", fileuploadModel.getChannelId());
                mIntent.putExtra("muid", fileuploadModel.getMuid());
                mIntent.putExtra("messageIndex", fileuploadModel.getMessageIndex());
                mIntent.putExtra("fileuploadModel", new Gson().toJson(fileuploadModel));
                mIntent.putExtra("errorMessage", errorMessage);
                LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
                Prefs.with(this).removeAll();
                stopFayeClient();
                stopForeground(true);
                stopSelf();
            } else if (isOpenedChatActivityMsg(fileuploadModel.getChannelId())) {
                setMessageExpired();
                Intent mIntent = new Intent(HIPPO_FILE_UPLOAD);
                mIntent.putExtra("channelId", fileuploadModel.getChannelId());
                mIntent.putExtra("muid", fileuploadModel.getMuid());
                mIntent.putExtra("messageIndex", fileuploadModel.getMessageIndex());
                mIntent.putExtra("fileuploadModel", new Gson().toJson(fileuploadModel));
                mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.UPLOADING_FAILED);
                LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
            } else {
                uploadingFailed(fileuploadModel.getChannelId());
                setMessageExpired();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Prefs.with(this).remove(KEY);
            stopFayeClient();
            stopForeground(true);
            stopSelf();
        }
    }


    /**
     * get mime type of selected file/image on basis of extension
     */
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type != null)
            return type;
        else {
            if (extension.contains("html"))
                return "text/html";
            else if (extension.contains("jsp"))
                return "application/json";
            else if (extension.contains("aspx"))
                return "application/xml";
            else if (extension.contains("php"))
                return "text/php";
            else
                return extension;

        }
    }


    private void updatePref() {
        HippoLog.e(TAG, "updatePref: " + Prefs.with(this).getString(KEY, ""));
        fileuploadModels = new Gson().fromJson(Prefs.with(this).getString(KEY, ""), fileuploadType);
        if (fileuploadModels == null || fileuploadModels.size() == 0) {
            Prefs.with(this).remove(KEY);
            stopFayeClient();
            stopForeground(true);
            stopSelf();
        } else {
            fileuploadModels.remove(0);
            String data = new Gson().toJson(fileuploadModels, fileuploadType);
            Prefs.with(this).save(KEY, data);

            HippoLog.e(TAG, "int updatePref set data : " + data);
            if (fileuploadModels.size() == 0) {
                Prefs.with(this).remove(KEY);
                stopFayeClient();
                stopForeground(true);
                stopSelf();
            } else {
                apiInProgress = false;
                uploadFileServerCall();
            }
        }
    }

    boolean isFirstTime;

    @Override
    public void onProgressUpdate(int percentage, int mMessageIndex, String muid) {
        //HippoLog.v(TAG, "percentage = "+percentage);
        if (isFirstTime && percentage == 10) {
            isFirstTime = false;
            return;
        }
        if (!isFirstTime && percentage == 10)
            isFirstTime = true;

        if (isFirstTime)
            updateProgress(percentage, 100);

        //HippoLog.e(TAG, "In onProgressUpdate else 2");
    }

    @Override
    public void onError(int percentage, int mMessageIndex, String muid) {
//        ToastUtil.getInstance(this).showToast("onError");
        HippoLog.e(TAG, "In onError else 2");
    }

    @Override
    public void onFinish(int percentage, int mMessageIndex, String muid) {
//        ToastUtil.getInstance(this).showToast("onFinish");
        HippoLog.e(TAG, "In onFinish else 2");
    }

    private void getClient(final Long channelId, final JSONObject messageJson) {
        ConnectionManager.INSTANCE.initFayeConnection();
        ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(Prefs.with(MyForeGroundService.this).getString(KEY, ""), fileuploadType);
        for (FileuploadModel fileuploadModel : fileuploadModels) {
            if (fileuploadModel.isFileUploaded()) {
                ConnectionManager.INSTANCE.subScribeChannel("/" + String.valueOf(fileuploadModel.getChannelId()));
                ConnectionManager.INSTANCE.publish("/" + String.valueOf(fileuploadModel.getChannelId()), fileuploadModel.getMessageObject());
                break;
            }
        }

        afterSetUpFayeConnection(channelId, messageJson);
    }

    private void afterSetUpFayeConnection(Long channelId, JSONObject messageJson) {
        if (isNetworkAvailable() && ConnectionManager.INSTANCE.isConnected()) {
            HippoLog.e(TAG, "************publish*********");
            ConnectionManager.INSTANCE.publish("/" + String.valueOf(channelId), messageJson);
            updatePref();
        } else if (!isNetworkAvailable()) {
            Intent mIntent = new Intent(FuguAppConstant.HIPPO_FILE_UPLOAD);
            mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.UPLOADED_SUCESSFULLY);
            LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
            updatePref();
        }
    }

    public void onDisconnectedServer() {

    }


    public void onReceivedMessage(String msg, String channel) {
        try {
            JSONObject messageJson = new JSONObject(msg);
            if (messageJson.optInt(MESSAGE_TYPE, 0) == IMAGE_MESSAGE ||
                    messageJson.optInt(MESSAGE_TYPE, 0) == FILE_MESSAGE) {
                String value = channel.replace("/", "");
                if (!isOpenedChatActivityMsg(Long.parseLong(value))) {
                    updateLocalMessageObj(Long.parseLong(value), true, "", "", null, null);
                }
                updatePref();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onWebSocketError() {

    }


    public void onErrorReceived() {

    }

    private boolean isOpenedChatActivityMsg(Long channelId) {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if (taskList.get(0).topActivity.getClassName().equals("com.hippo.activity.FuguChatActivity")
                && FuguChatActivity.currentChannelId.compareTo(channelId) == 0) {
            return true;
        }
        return false;
    }

    private void uploadingFailed(long channelId) {
        String data = Prefs.with(MyForeGroundService.this).getString(KEY, "empty");
        if (data.equals("empty")) {
            apiInProgress = false;
            Log.v(TAG, "data = " + data);
            return;
        }
        ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(data, fileuploadType);
        int index = fileuploadModels.indexOf(new FileuploadModel(channelId));
        if (index == -1)
            return;
        FileuploadModel fileuploadModel = fileuploadModels.get(index);

        LinkedHashMap<String, Message> unsentMessages = CommonData.getUnsentMessageByChannel(channelId);
        LinkedHashMap<String, JSONObject> unsentMessageMapNew = CommonData.getUnsentMessageMapByChannel(channelId);

        Message listItem = unsentMessages.get(fileuploadModel.getMuid());
        if (listItem == null)
            return;

        listItem.setUploadStatus(UPLOAD_FAILED);
        listItem.setIsMessageExpired(1);
        unsentMessages.put(fileuploadModel.getMuid(), listItem);
        CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
    }


    private void setAllMessageExpired() {
        fileuploadModels = new Gson().fromJson(Prefs.with(this).getString(KEY, ""), fileuploadType);
        if (fileuploadModels == null || fileuploadModels.size() == 0) {
            Prefs.with(this).remove(KEY);
            stopFayeClient();
            stopForeground(true);
            stopSelf();
        } else {
            for (FileuploadModel fileuploadModel : fileuploadModels) {
                if (isOpenedChatActivityMsg(fileuploadModel.getChannelId())) {
                    sendLocalBroadcast(fileuploadModel);
                } else {
                    updateLocalMessages(fileuploadModels.get(0));
                }
            }
            fileuploadModels.clear();
            Prefs.with(this).remove(KEY);
            stopFayeClient();
            stopForeground(true);
            stopSelf();

        }
    }

    private void setMessageExpired() {
        String data = Prefs.with(this).getString(KEY, "");
        HippoLog.e(TAG, "In setMessageExpired: " + data);
        fileuploadModels = new Gson().fromJson(data, fileuploadType);
        if (fileuploadModels == null || fileuploadModels.size() == 0) {
            Prefs.with(this).remove(KEY);
            stopFayeClient();
            stopForeground(true);
            stopSelf();
        } else {
            //FileuploadModel fileuploadModel = fileuploadModels.get(0);
            if (isOpenedChatActivityMsg(fileuploadModels.get(0).getChannelId())) {
                sendLocalBroadcast(fileuploadModels.get(0));
            } else {
                updateLocalMessages(fileuploadModels.get(0));
            }
            fileuploadModels.remove(0);
            data = new Gson().toJson(fileuploadModels, fileuploadType);
            HippoLog.e(TAG, "In setMessageExpired after update: " + data);
            Prefs.with(this).save(KEY, data);

            uploadFileServerCall();
        }
    }

    private void sendLocalBroadcast(FileuploadModel fileuploadModel) {
        Intent mIntent = new Intent(FuguAppConstant.HIPPO_FILE_UPLOAD);
        mIntent.putExtra("muid", fileuploadModel.getMuid());
        mIntent.putExtra("messageIndex", fileuploadModel.getMessageIndex());
        mIntent.putExtra(BROADCAST_STATUS, BroadCastStatus.MESSAGE_EXPIRED);
        LocalBroadcastManager.getInstance(HippoConfig.getInstance().getContext()).sendBroadcast(mIntent);
    }

    private void updateLocalMessageObj(Long channelId, boolean isPublished, String url, String thumbnailUrl,
                                       JSONObject jsonObject, UpdateLocalMsgListener msgListener) {
        String data = Prefs.with(MyForeGroundService.this).getString(KEY, "empty");
        if (data.equals("empty")) {
            apiInProgress = false;
            HippoLog.v(TAG, "data = " + data);
            return;
        }
        ArrayList<FileuploadModel> fileuploadModels = new Gson().fromJson(data, fileuploadType);
        int index = fileuploadModels.indexOf(new FileuploadModel(channelId));
        if (index == -1)
            return;
        FileuploadModel fileuploadModel = fileuploadModels.get(index);

        LinkedHashMap<String, Message> sentMessages = CommonData.getSentMessageByChannel(channelId);
        LinkedHashMap<String, Message> unsentMessages = CommonData.getUnsentMessageByChannel(channelId);
        LinkedHashMap<String, JSONObject> unsentMessageMapNew = CommonData.getUnsentMessageMapByChannel(channelId);

        Message listItem = unsentMessages.get(fileuploadModel.getMuid());
        if (listItem == null)
            return;

        if (isPublished) {
            listItem.setMessageStatus(MESSAGE_SENT);
            List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
            Collections.reverse(reverseOrderedKeys);
            String tempSentAtUTC = "";
            for (String key : reverseOrderedKeys) {
                if (sentMessages.get(key).isDateView()) {
                    tempSentAtUTC = key;
                    break;
                }
            }
            String time = listItem.getSentAtUtc();
            String localDate = DateUtils.getInstance().convertToLocal(time, inputFormat, outputFormat);
            if (!tempSentAtUTC.equalsIgnoreCase(localDate)) {
                sentMessages.put(localDate, new Message(localDate, true));
            }
            sentMessages.put(fileuploadModel.getMuid(), listItem);

            unsentMessages.remove(fileuploadModel.getMuid());
            unsentMessageMapNew.remove(fileuploadModel.getMuid());

            CommonData.setSentMessageByChannel(channelId, sentMessages);

        } else {
            Message message = listItem;
            message.setFileUrl(url);
            message.setUrl(url);
            message.setThumbnailUrl(thumbnailUrl);
            message.setUploadStatus(UPLOAD_COMPLETED);

            unsentMessages.put(fileuploadModel.getMuid(), message);

            JSONObject object = null;
            try {
                object = unsentMessageMapNew.get(fileuploadModel.getMuid());
                object.put(IMAGE_URL, url);
                object.put(THUMBNAIL_URL, thumbnailUrl);
                object.put("url", url);
                unsentMessageMapNew.put(fileuploadModel.getMuid(), object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
        CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);

        if (msgListener != null)
            msgListener.onUpdation(jsonObject);

    }

    private void updateLocalMessages(FileuploadModel fileuploadModel) {
        String muid = fileuploadModel.getMuid();

        LinkedHashMap<String, Message> unsentMessages = CommonData.getUnsentMessageByChannel(fileuploadModel.getChannelId());
        LinkedHashMap<String, JSONObject> unsentMessageMapNew = CommonData.getUnsentMessageMapByChannel(fileuploadModel.getChannelId());

        if (unsentMessages == null)
            unsentMessages = new LinkedHashMap<>();

        unsentMessages.get(muid).setIsMessageExpired(1);
        try {
            JSONObject messageJson = unsentMessageMapNew.get(muid);
            messageJson.put("is_message_expired", 1);
            unsentMessageMapNew.put(muid, messageJson);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        CommonData.setUnsentMessageByChannel(fileuploadModel.getChannelId(), unsentMessages);
        CommonData.setUnsentMessageMapByChannel(fileuploadModel.getChannelId(), unsentMessageMapNew);

    }

    public interface UpdateLocalMsgListener {
        void onUpdation(JSONObject jsonObject);
    }
}
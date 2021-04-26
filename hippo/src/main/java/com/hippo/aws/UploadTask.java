package com.hippo.aws;

import android.os.AsyncTask;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<String, Void, AsyncTaskResult<Void>> {
    private static final int NETWORK_TIMEOUT_SEC = 60;
    private final String imagePath;
    private final String url;
    private final ImageUploadInterface imageUploadInterface;
    private File file;

    public UploadTask(String url, String imagePath, File file, ImageUploadInterface imageUploadInterface) {
        this.url = url;
        this.imagePath = imagePath;
        this.imageUploadInterface = imageUploadInterface;
        this.file = file;

    }

    @Override
    protected AsyncTaskResult doInBackground(String... params) {
        try {
            // Obtain the url
            if (file == null)
                file = new File(imagePath);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                    .connectTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .writeTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build();

            Request uploadFileRequest = new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(MediaType.parse(""), file))
                    .build();
            Response uploadResponse = client.newCall(uploadFileRequest).execute();
            if (!uploadResponse.isSuccessful())
                return new AsyncTaskResult(new Exception("Upload file response code: " + uploadResponse.code()));

            return new AsyncTaskResult(null);

        } catch (Exception e) {
            return new AsyncTaskResult(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult result) {
        super.onPostExecute(result);
        if (result.hasError()) {
            if (imageUploadInterface != null)
                imageUploadInterface.failureUpload(result.getError());
        } else {
            if (imageUploadInterface != null)
                imageUploadInterface.sucessfullupload();
        }
    }
}
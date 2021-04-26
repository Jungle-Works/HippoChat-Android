package com.hippo.utils.fileUpload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.utils.HippoLog;
import com.hippo.utils.filepicker.Util;

import java.io.*;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

/**
 * Created by gurmail on 17/01/19.
 *
 * @author gurmail
 */
public class FileManager {
    private static final String TAG = FileManager.class.getSimpleName();
    private static FileManager instance;
    private FileManager() {

    }

    public static FileManager getInstance() {
        if(instance == null) {
            synchronized (FileManager.class) {
                if(instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    public String getLocalPath(String fileName, String folderType) {
        try {
            String localName = Util.getDirectoryPath(folderType) + File.separator + fileName;
            File file = new File(localName);//.getParentFile();
            if(file.exists() && file.isFile()) {
             return file.getPath();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public Uri getPublicFilePath(Context context, String fileName) {
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            if(file.exists()) {
                Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)), fileName);
                //Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), fileName);
                System.out.println(mDestinationUri.getPath());
                return mDestinationUri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public void saveFile(Activity activity, String url, String folderType) {
        HippoLog.e(TAG, "saveFile");

        //String fileName = fuguFileDetails.getFileName() + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + "_" + timeStamp + type.extension;
        String localName = Util.extractFileNameWithSuffix(url);
        try {
            File file = new File(Util.getDirectoryPath(folderType), localName);
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                fileOutputStream.write(buffer, 0, bytesRead);

            fileOutputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    public void copyFile(String sourceUrl, String folderType, FileuploadModel fileuploadModel, FileCopyListener fileCopyListener) throws IOException {
        String localName = fileuploadModel.getFileName();
        //localName = Util.getFileName(localName, fileuploadModel.getMuid());

        File sourceFile = new File(sourceUrl);
        if(folderType.equalsIgnoreCase(FuguAppConstant.FOLDER_TYPE.get(FuguAppConstant.DocumentType.FILE.toString())) && sourceFile.length() > HippoConfig.getMaxSize()) {
            fileCopyListener.largeFileSize();
            return;
        } else if(!folderType.equalsIgnoreCase(FuguAppConstant.FOLDER_TYPE.get(FuguAppConstant.DocumentType.VIDEO.toString())) && sourceFile.length() > HippoConfig.getMaxSize()) {
            fileCopyListener.largeFileSize();
            return;
        } else if(!folderType.equalsIgnoreCase(FuguAppConstant.FOLDER_TYPE.get(FuguAppConstant.DocumentType.AUDIO.toString())) && sourceFile.length() > HippoConfig.getMaxSize()) {
            fileCopyListener.largeFileSize();
            return;
        } else if(!folderType.equalsIgnoreCase(FuguAppConstant.FOLDER_TYPE.get(FuguAppConstant.DocumentType.IMAGE.toString())) && sourceFile.length() > HippoConfig.getMaxSize()) {
            fileCopyListener.largeFileSize();
            return;
        }
        File destFile = new File(Util.getDirectoryPath(folderType), localName);

        if(sourceUrl.equalsIgnoreCase(Util.getDirectoryPath(folderType)+"/"+localName)) {
            if(fileCopyListener != null) {
                fileuploadModel.setFilePath(destFile.getPath());
                fileCopyListener.onCopingFile(true, fileuploadModel);
            }
            return;
        }

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }

        if(fileCopyListener != null) {
            fileuploadModel.setFilePath(destFile.getPath());
            fileCopyListener.onCopingFile(true, fileuploadModel);
        }

    }

    public void openFileInDevice(Context context, String localPath, FileCopyListener fileCopyListener) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(Util.getExtension(localPath));
        if(TextUtils.isEmpty(mimeType))
            mimeType =  URLConnection.guessContentTypeFromName(localPath);
        Uri uri = FileProvider.getUriForFile(context, CommonData.getProvider(), new File(localPath));
        newIntent.setDataAndType(uri, mimeType);
        newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(newIntent);
        } catch (Exception e) {
            if(fileCopyListener != null) {
                fileCopyListener.onError();
            } else {
                Toast.makeText(context, context.getString(R.string.no_handler), Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface FileCopyListener {
        void onCopingFile(boolean flag, FileuploadModel fileuploadModel);
        void largeFileSize();
        void onError();
    }


}

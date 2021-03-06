package hipposdk.com.hipposdk.retrofit;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.hippo.utils.HippoLog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;

/**
 * Retrofit Utils
 */

public class RetrofitUtils {


    /**
     *
     * @param value
     * @return
     */
    public static RequestBody getRequestBodyFromString(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }


    /**
     *
     * @param key
     * @param file
     * @return
     */
    public static MultipartBody.Part getPartBodyFromFile(String key, File file) {

        if(file == null)
            return null;
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(getMimeType(file)), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(key, file.getName(), requestFile);

        return body;
    }


    /**
     *
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        String mimeType = "image/png";
        try {
            Uri selectedUri = Uri.fromFile(file);
            String fileExtension
                    = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            mimeType
                    = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        } catch (Exception e) {
            HippoLog.e("mime type exception ", e.toString());
        }
        return mimeType;
    }
}

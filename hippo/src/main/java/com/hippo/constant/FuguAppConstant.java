package com.hippo.constant;

import android.os.Environment;

import com.hippo.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public interface FuguAppConstant {

    String CONFERENCING_TEST = "https://conference-dev.officechat.io";
    String CONFERENCING_LIVE = "https://conference.hippochat.io";
//    String CONFERENCING_LIVE = "https://meet.hippochat.io";

//    String CONFERENCING_LIVE = "https://meet.fugu.chat";
//    String CONFERENCING_LIVE = CONFERENCING_TEST;


    String DEV_SERVER = "https://hippo-api-dev.fuguchat.com:3002";
    String DEV_SERVER_3003 = "https://hippo-api-dev.fuguchat.com:3003";
    String DEV_SERVER_3004 = "https://hippo-api-dev.fuguchat.com:3004";
    String TEST_SERVER = "https://hippo-api-dev.fuguchat.com:3011";
    String BETA_SERVER = "https://beta-hippo.fuguchat.com";
    String BETA_LIVE_SERVER = "https://beta-live-api.fuguchat.com";
    //String LIVE_SERVER = "https://api.fuguchat.com";
    String LIVE_SERVER = "https://api.hippochat.io";


    String LIVE_SOCKEY_SERVER = "https://socketv2.hippochat.io";
    String BETA_LIVE_SOCKEY_SERVER = "https://beta-live-api.fuguchat.com:3001";

//    String DEV_SERVER = "https://api-new.fuguchat.com";
//    String DEV_SERVER_3004 = "https://api-new.fuguchat.com";
//    String LIVE_SERVER = "https://api-new.fuguchat.com";

//    String DEV_SERVER = "https://api-new.fuguchat.com";
//    String DEV_SERVER_3004 = "https://api-new.fuguchat.com";
//    String TEST_SERVER = "https://api-new.fuguchat.com";
//    String BETA_SERVER = "https://api-new.fuguchat.com";
//    String BETA_LIVE_SERVER = "https://api-new.fuguchat.com";
//    String LIVE_SERVER = "https://api-new.fuguchat.com";


    public static final String HIPPO_PAPER_NAME = "hippo.paperdb";

    int SESSION_EXPIRE = 403;
    int DATA_UNAVAILABLE = 406;
    int INVALID_VIDEO_CALL_CREDENTIALS = 413;
    String NETWORK_STATE_INTENT = "network_state_changed";
    String NOTIFICATION_INTENT = "notification_received";
    String NOTIFICATION_TAPPED = "notification_tapped";
    String FUGU_WEBSITE_URL = "https://fuguchat.com";
    String VIDEO_CALL_INTENT = "hippo_video_call_intent";
    String VIDEO_CALL_HUNGUP = "hippo_video_call_hungup";

    int IMAGE_MESSAGE = 10;
    int FILE_MESSAGE = 11;
    int ACTION_MESSAGE = 12;
    int ACTION_MESSAGE_NEW = 19;
    int TEXT_MESSAGE = 1;
    int PRIVATE_NOTE = 3;
    int READ_MESSAGE = 6;
    int FEEDBACK_MESSAGE = 14;
    int BOT_TEXT_MESSAGE = 15;
    int BOT_FORM_MESSAGE = 17;
    int VIDEO_CALL = 18;
    int CARD_LIST = 21;
    int PAYMENT_TYPE = 22;
    int MULTI_SELECTION = 23;
    int HIPPO_NEW_LEAD_FORM_TICKET = 29;

    int VIDEO_CALL_VIEW = 1;
    int AUDIO_CALL_VIEW = 2;

    int SYSTEM_USER = 0;
    int ANDROID_USER = 1;

    int CHANNEL_SUBSCRIBED = 1;
    int CHANNEL_UNSUBSCRIBED = 0;

    int STATUS_CHANNEL_CLOSED = 0;
    int STATUS_CHANNEL_OPEN = 1;

    int TYPING_SHOW_MESSAGE = 0;
    int TYPING_STARTED = 1;
    int TYPING_STOPPED = 2;

    int MESSAGE_SENT = 1;
    int MESSAGE_DELIVERED = 2;
    int MESSAGE_READ = 3;
    int MESSAGE_UNSENT = 4;
    int MESSAGE_IMAGE_RETRY = 5;
    int MESSAGE_FILE_RETRY = 6;
    int MESSAGE_FILE_UPLOADED = 7;

    int UPLOAD_FAILED = 0;
    int UPLOAD_IN_PROGRESS = 1;
    int UPLOAD_PAUSED = 2;
    int UPLOAD_COMPLETED = 3;

    int PERMISSION_CONSTANT_CAMERA = 9;
    int PERMISSION_CONSTANT_GALLERY = 8;
    int PERMISSION_READ_IMAGE_FILE = 4;
    int PERMISSION_SAVE_BITMAP = 5;
    int PERMISSION_READ_FILE = 6;

    final int RC_READ_EXTERNAL_STORAGE = 123;
    final int RC_OPEN_CAMERA = 124;
    int OPEN_CAMERA_ADD_IMAGE = 514;
    int OPEN_GALLERY_ADD_IMAGE = 515;
    int SELECT_FILE = 516;
    int SELECT_AUDIO = 518;
    int SELECT_DOCUMENT = 517;
    int SELECT_VIDEO = 519;
    int SELECT_NONE = 600;
    int SELECT_PAYMENT = 520;

    //Notification Type
    int NOTIFICATION_DEFAULT = -1;
    int NOTIFICATION_READ_ALL = 6;

    //For agent
    int AGENT_TEXT_MESSAGE = 1;
    int ASSIGN_CHAT = 3;
    int AGENT_REALALL = 6;
    int NEW_AGENT_ADDED = 10;
    int AGENT_STATUS_CHANGED = 11;

    // action
    String FUGU_CUSTOM_ACTION_SELECTED = "FUGU_CUSTOM_ACTION_SELECTED";
    String FUGU_CUSTOM_ACTION_PAYLOAD = "FUGU_CUSTOM_ACTION_PAYLOAD";
    String HIPPO_CALL_NOTIFICATION_PAYLOAD = "hippo_call_notification_data";
    String FUGU_LISTENER_NULL = "fugu_listener_null";
    String HIPPO_FILE_UPLOAD = "HIPPO_FILE_UPLOAD";

    String IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "fugu" +
            File.separator + "picture";
    String CONVERSATION = "conversation";
    String NOTIFICATION_TYPE = "notification_type";
    String USER_ID = "user_id";
    String EN_USER_ID = "en_user_id";
    String PEER_CHAT_PARAMS = "peer_chat_params";
    String APP_SECRET_KEY = "app_secret_key";
    String DEVICE_TYPE = "device_type";
    String DEVICE_DETAILS = "device_details";
    String RESELLER_TOKEN = "reseller_token";
    String REFERENCE_ID = "reference_id";
    String DEVICE_ID = "device_id";
    String APP_TYPE = "app_type";
    String APP_VERSION = "app_version";
    String APP_VERSION_CODE = "app_version_code";
    String ANDROID = "Android";
    String USER_UNIQUE_KEY = "user_unique_key";
    String FULL_NAME = "full_name";
    String MESSAGE = "message";
    String NEW_MESSAGE = "new_message";
    String MESSAGE_STATUS = "message_status";
    String MESSAGE_STATE = "message_state";
    String MESSAGE_INDEX = "message_index";
    String MESSAGE_TYPE = "message_type";
    String USER_TYPE = "user_type";
    String USER_IMAGE = "user_image";
    String DATE_TIME = "date_time";
    String EMAIL = "email";
    String PHONE_NUMBER = "phone_number";
    String DEVICE_TOKEN = "device_token";
    String IS_TYPING = "is_typing";
    String CHANNEL_ID = "channel_id";
    String LABEL_ID = "label_id";
    String UNREAD_COUNT = "unread_count";
    String ON_SUBSCRIBE = "on_subscribe";
    String IMAGE_URL = "image_url";
    String THUMBNAIL_URL = "thumbnail_url";
    String ADDRESS = "address";
    String COUNTRY_INFO = "country_info";
    String LAT_LONG = "lat_long";
    String IP_ADDRESS = "ip_address";
    String ZIP_CODE = "zip_code";
    String COUNTRY = "country";
    String REGION = "region";
    String CITY = "city";
    String ADDRESS_LINE1 = "address_line1";
    String ADDRESS_LINE2 = "address_line2";
    String ATTRIBUTES = "attributes";
    String CUSTOM_ATTRIBUTES = "custom_attributes";
    String ISP2P = "isP2P";
    String CHAT_TYPE = "chat_type";
    String ERROR = "error";
    String INTRO_MESSAGE = "intro_message";
    String CUSTOM_ACTION = "custom_action";
    String GROUPING_TAGS = "grouping_tags";
    String ACCESS_TOKEN = "access_token";
    String APP_SOURCE = "source";
    String STATUS = "status";
    String TYPE = "type";
    String PAGE_START = "page_start";
    String PAGE_END = "page_end";
    int ASSIGNMENT_MESSAGE = 2;
    String TAGS_DATA = "tag_data";
    String FRAGMENT_TYPE = "fragment_type";
    String AUTH_TOKEN = "auth_token";
    String AGENT_SECRET_KEY = "agent_secret_key";
    String CREATE_NEW_CHAT = "create_chat";
    String OTHER_USER_UNIQUE_KEY = "other_user_unique_key";
    String RESPONSE_TYPE = "response_type";
    String APP_SOURCE_TYPE = "source_type";
    String CREATED_BY = "created_by";
    String DEAL_ID = "deal_id";
    String USER_AGENT_CALL = "user_agent_call";
    String SUPPORT_ID = "support_id";
    String SUPPORT_TRANSACTION_ID = "support_transaction_id";
    String SUPPORT_IS_ACTIVE = "is_active";
    String SOURCE_KEY = "source";

    String VIDEO_CALL_MODEL = "video_Call_model";
    String INIT_FULL_SCREEN_SERVICE = "init_full_screen_service";
    String CALL_STATUS = "call_status";
    String CHANNEL_NAME = "channel_name";
    String MESSAGE_UNIQUE_ID = "muid";
    String VIDEO_CALL_TYPE = "video_call_type";
    String IS_SILENT = "is_silent";
    String TITLE = "title";
    int MAX_HEIGHT = 250;
    int MAX_WIDTH = 250;
    int MAX_WIDTH_OUTER_SPIKED = 252;
    int MAX_WIDTH_OUTER = 255;

    String BROADCAST_STATUS = "broadcast_status";
    final String KEY = "pref_upload_data";

    String IMAGE_FOLDER = "image";
    String DOC_FOLDER = "file";
    String AUDIO_FOLDER = "audio";
    String VIDEO_FOLDER = "video";

    // for file downloading
    String HIPPO_PROGRESS_INTENT = "hippo_progress_intent";
    String HIPPO_POSITION = "hippo_position";
    String HIPPO_PROGRESS = "hippo_progress";
    String HIPPO_STATUS_UPLOAD = "hippo_statusUpload";
    String HIPPO_USER_IMAGE_PATH = "user_image";

    String BOT_GROUP_ID = "bot_group_id";

    String INVITE_LINK = "invite_link";
    String VIDEO_CONFERENCE_HUNGUP_INTENT = "video_conference_hungup_intent";
    String INCOMING_VIDEO_CONFERENCE = "incoming_video_conference";
    String ROOM_NAME = "room_name";
    String BASE_URL = "base_url";
    String JITSI_URL = "jitsi_url";

    String URL_WEBVIEW = "url_webview";
    String VALUE_PAYMENT = "VALUE_PAYMENT";
    String PAYMENT_FOR_FLOW = "paymentForFlow";

    String LANG = "lang";


    //	Razor pay keys
    String KEY_ORDER_ID = "order_id";
    String KEY_RAZORPAY_PREFILL_EMAIL = "prefill.email";
    String KEY_RAZORPAY_PREFILL_CONTACT = "prefill.contact";
    String KEY_RAZORPAY_PREFILL_METHOD = "prefill.method";
    String KEY_RAZORPAY_PREFILL_VPA = "prefill.vpa";
    String KEY_USER_EMAIL = "user_email";
    String KEY_RAZORPAY_THEME_COLOR = "theme.color";
    String KEY_DESCRIPTION = "description";
    String KEY_RAZORPAY_PAYMENT_OBJECT = "razorpay_payment_object";
    String KEY_AUTH_ORDER_ID = "auth_order_id";
    String KEY_RAZORPAY_SIGNATURE = "razorpay_signature";
    String KEY_RAZORPAY_PAYMENT_ID = "razorpay_payment_id";
    String SP_RZP_ORDER_ID = "sp_rzp_order_id";
    String KEY_RAZORPAY_ERR = "razorpay_err";
    String SP_RZP_AUTH_ORDER_ID = "sp_rzp_auth_order_id";
    String SP_RZP_NEGATIVE_BALANCE_SETTLE = "sp_negative_balance_settle";
    String INTENT_ACTION_RAZOR_PAY_CALLBACK = "INTENT_ACTION_RAZOR_PAY_CALLBACK";
    String KEY_RESPONSE = "response";
    String KEY_AMOUNT = "amount";
    String KEY_CURRENCY = "currency";
    String KEY_NAME = "name";
    String KEY_PHONE_NO = "phone_no";


    /**
     * The type of file being Saved
     */
    enum FileType {

        LOG_FILE("logs", ".log"),
        DOC_FILE("Fugu", ".doc"),
        DOCX_FILE("Fugu", ".docx"),
        TXT_FILE("Fugu", ".txt"),
        PPT_FILE("Fugu", ".ppt"),
        PPTX_FILE("Fugu", ".pptx"),
        IPA_FILE("Fugu", ".ipa"),
        XLS_FILE("Fugu", ".xls"),
        XLSX_FILE("Fugu", ".xlsx"),
        APK_FILE("Fugu", ".apk"),
        CSV_FILE("Fugu", ".csv"),
        IMAGE_FILE("Fugu", ".jpg"),
        GENERAL_FILE("Fugu", ".txt"),
        PDF_FILE("Fugu", ".pdf"),
        MP3_FILE("Fugu", ".mp3"),
        PRIVATE_FILE("system", ".sys"),
        TGP_FILE("Fugu", ".3gp"),
        MIDI_FILE("Fugu", ".midi"),
        MPEG_FILE("Fugu", ".mpeg"),
        XAIFF_FILE("Fugu", ".x-aiff"),
        XWAV_FILE("Fugu", ".x-wav"),
        WEBM_FILE("Fugu", ".webm"),
        OGG_FILE("Fugu", ".ogg"),
        M4A_FILE("Fugu", ".m4a"),
        WAV_FILE("Fugu", ".wav"),
        MP4_FILE("Fugu", ".mp4"),
        FLV_FILE("Fugu", ".flv"),
        MKV_FILE("Fugu", ".mkv"),
        MOV_FILE("Fugu", ".mov"),
        MPG_FILE("Fugu", ".mpg"),
        JPG_FILE("Fugu", ".jpg"),
        MTS_FILE("Fugu", ".mts"),
        TGPP_FILE("Fugu", ".3gpp");

        public final String extension;
        public final String directory;

        FileType(String relativePath, String extension) {
            this.extension = extension;
            this.directory = relativePath;
        }
    }

    interface DataType {

        String NUMBER = "Number";
        String NAME = "name";
        String _NUMBER = "number";
        String TEXT = "Text";
        String IMAGE = "Image";
        String DATE = "Date";
        String DROP_DOWN = "Dropdown";
        String CHECKBOX = "Checkbox";
        String TELEPHONE = "Telephone";
        String PHONE = "phone";
        String ISSUETYPE = "issueType";
        String PRIORITY = "priority";
        String ATTACHMENT = "attachments";
        String PHONE_NUMBER = "phone_number";
        String EMAIL = "email";
        String URL = "URL";
        String DATE_FUTURE = "Date-Future";
        String DATE_PAST = "Date-Past";
        String DATE_TODAY = "Date-Today";
        String CHECKLIST = "Checklist";
        String TABLE = "Table";
        String DATETIME = "Date-Time";
        String DATETIME_FUTURE = "Datetime-Future";
        String DATETIME_PAST = "Datetime-Past";
        String BARCODE = "Barcode";

    }

    interface BroadCastStatus {
        int UPLOADED_SUCESSFULLY = 0;
        int UPLOADING_FAILED = 1;
        int PUBLISHED = 2;
        int MESSAGE_EXPIRED = 3;
        int CREATE_CHANNEL = 4;
        int FILE_TYPE_NOT_ALLOWED = 5;

    }

    public enum DocumentType {
        IMAGE("image"),
        AUDIO("audio"),
        VIDEO("video"),
        FILE("file");

        public final String type;

        DocumentType(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    HashMap<String, String> FOLDER_TYPE = new HashMap<String, String>() {{
        put("audio", "Hippo Audio");
        put("video", "Hippo Video");
        put("file", "Hippo Documents");
        put("image", "Hippo Images");
    }};

    public enum CallType {
        AUDIO("AUDIO"),
        VIDEO("VIDEO");

        public final String type;


        /**
         * @param type
         */
        CallType(final String type) {
            this.type = type;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return type;
        }
    }

    interface MimeTypeConstants {
        String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
        String MIME_TYPE_IMAGE_JPG = "image/pjpeg";
        String MIME_TYPE_IMAGE_PNG = "image/png";
        String MIME_TYPE_PDF = "application/pdf";
        String MIME_TYPE_CSV_1 = "text/csv";
        String MIME_TYPE_CSV_2 = "text/comma-separated-values";
        String MIME_TYPE_DOC = "application/msword";
        String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        String MIME_TYPE_PPT = "application/vnd.ms-powerpoint";
        String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        String MIME_TYPE_XLS = "application/vnd.ms-excel";
        String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String MIME_TYPE_TXT = "text/plain";
    }

    Map<String, String> MIME_TYPE_SET = new HashMap<String, String>() {{
        put(".doc", "application/msword");
        put(".ppt", "application/vnd.ms-powerpoint");
        put(".pdf", "application/pdf");
        put(".xls", "application/vnd.ms-excel");
        put(".zip", "application/x-wav");
        put(".rtf", "application/rtf");
        put(".wav", "audio/x-wav");
        put(".jpg", "image/jpeg");
        put(".csv", "text/csv");
        put(".apk", "application/vnd.android.package-archive");
        put(".3gp", "video/*");
        put("default", "*/*");
    }};

    HashMap<String, Integer> IMAGE_MAP = new HashMap<String, Integer>() {{
        put("txt", R.drawable.hippo_txt);
        put("pdf", R.drawable.hippo_pdf);
        put("csv", R.drawable.hippo_csv);
        put("doc", R.drawable.hippo_doc);
        put("docx", R.drawable.hippo_doc);
        put("ppt", R.drawable.hippo_ppt);
        put("pptx", R.drawable.hippo_ppt);
        put("xls", R.drawable.hippo_excel);
        put("xlsx", R.drawable.hippo_excel);
        put("apk", R.drawable.hippo_android);
        put("ipa", R.drawable.hippo_apple);
        put("zip", R.drawable.hippo_attachment);
        put("3gp", R.drawable.hippo_attachment);
        put("mp3", R.drawable.hippo_music);
        put("midi", R.drawable.hippo_music);
        put("mpeg", R.drawable.hippo_music);
        put("x-aiff", R.drawable.hippo_music);
        put("x-wav", R.drawable.hippo_music);
        put("webm", R.drawable.hippo_music);
        put("ogg", R.drawable.hippo_music);
        put("m4a", R.drawable.hippo_music);
        put("wav", R.drawable.hippo_music);

    }};
    HashMap<String, String> EXTENSION_MAP = new HashMap<String, String>() {{
        put("txt", ".txt");
        put("pdf", ".pdf");
        put("csv", ".csv");
        put("doc", ".doc");
        put("docx", ".doc");
        put("ppt", ".ppt");
        put("pptx", ".ppt");
        put("xls", ".xls");
        put("xlsx", ".xls");
        put("apk", ".apk");
        put("ipa", ".ipa");
        put("mp3", ".wav");
        put("3gp", ".3gp");
        put("midi", ".wav");
        put("mpeg", ".wav");
        put("x-aiff", ".wav");
        put("x-wav", ".wav");
        put("webm", ".wav");
        put("ogg", ".wav");
        put("m4a", ".wav");
        put("wav", ".wav");
        put("mpg", ".3gp");
        put("mpeg", ".3gp");
        put("mpe", ".3gp");
        put("mp4", ".3gp");
        put("avi", ".3gp");
    }};


    HashMap<String, FileType> FILE_TYPE_MAP = new HashMap<String, FileType>() {{
        put("pdf", FileType.PDF_FILE);
        put("ppt", FileType.PPT_FILE);
        put("pptx", FileType.PPTX_FILE);
        put("xls", FileType.XLS_FILE);
        put("xlsx", FileType.XLSX_FILE);
        put("txt", FileType.TXT_FILE);
        put("csv", FileType.CSV_FILE);
        put("doc", FileType.DOC_FILE);
        put("docx", FileType.DOCX_FILE);
        put("apk", FileType.APK_FILE);
        put("ipa", FileType.IPA_FILE);
        put("default", FileType.IMAGE_FILE);
        put("mp4", FileType.MP4_FILE);
        put("flv", FileType.FLV_FILE);
        put("mkv", FileType.MKV_FILE);
        put("mov", FileType.MOV_FILE);

        put("3gp", FileType.TGP_FILE);
        put("mp3", FileType.MP3_FILE);
        put("midi", FileType.MIDI_FILE);
        put("mpeg", FileType.MPEG_FILE);
        put("x-aiff", FileType.XAIFF_FILE);
        put("mpeg", FileType.MPEG_FILE);
        put("x-wav", FileType.XWAV_FILE);
        put("webm", FileType.WEBM_FILE);
        put("ogg", FileType.OGG_FILE);
        put("m4a", FileType.M4A_FILE);
        put("wav", FileType.WAV_FILE);

        put("mov", FileType.MOV_FILE);
        put("3gpp", FileType.TGPP_FILE);
        put("mts", FileType.MTS_FILE);
        put("mpg", FileType.MPG_FILE);
        put("jpg", FileType.JPG_FILE);
    }};

    enum DownloadStatus {
        DOWNLOAD_FAILED(0),
        DOWNLOAD_IN_PROGRESS(1),
        DOWNLOAD_PAUSED(2),
        DOWNLOAD_COMPLETED(3);

        public final int downloadStatus;

        DownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
        }
    }


    interface ACTION {
        String DEFAULT = "0";
        String ASSIGNMENT = "1";
        String AUDIO_CALL = "2";
        String VIDEO_CALL = "3";
        String CONTINUE_CHAT = "4";
        String OPEN_URL = "5";
    }

    interface INPUT_TYPE {
        String DEFAULT = "DEFAULT";
        String NUMBER = "NUMBER";
        String NONE = "NONE";
    }

    enum JitsiCallType {
        START_CONFERENCE {
            @Override
            public String toString() {
                return "START_CONFERENCE";
            }
        },
        START_CONFERENCE_IOS {
            @Override
            public String toString() {
                return "START_CONFERENCE";
            }
        },
        READY_TO_CONNECT_CONFERENCE {
            @Override
            public String toString() {
                return "READY_TO_CONNECT_CONFERENCE";
            }
        },
        READY_TO_CONNECT_CONFERENCE_IOS {
            @Override
            public String toString() {
                return "READY_TO_CONNECT_CONFERENCE_IOS";
            }
        },
        HUNGUP_CONFERENCE {
            @Override
            public String toString() {
                return "HUNGUP_CONFERENCE";
            }
        },
        REJECT_CONFERENCE {
            @Override
            public String toString() {
                return "REJECT_CONFERENCE";
            }
        },
        USER_BUSY_CONFERENCE {
            @Override
            public String toString() {
                return "USER_BUSY_CONFERENCE";
            }
        },
        OFFER_CONFERENCE {
            @Override
            public String toString() {
                return "OFFER_CONFERENCE";
            }
        },
        ANSWER_CONFERENCE {
            @Override
            public String toString() {
                return "ANSWER_CONFERENCE";
            }
        },
        START_GROUP_CALL {
            @Override
            public String toString() {
                return "START_GROUP_CALL";
            }
        },
        JOIN_GROUP_CALL {
            @Override
            public String toString() {
                return "JOIN_GROUP_CALL";
            }
        },
        END_GROUP_CALL {
            @Override
            public String toString() {
                return "END_GROUP_CALL";
            }
        }
    }

    enum FayeBusEvent {
        CONNECTED {
            @Override
            public String toString() {
                return "connected";
            }
        },
        DISCONNECTED {
            @Override
            public String toString() {
                return "disconnected";
            }
        },
        MESSAGE_RECEIVED {
            @Override
            public String toString() {
                return "message_received";
            }
        },
        ERROR {
            @Override
            public String toString() {
                return "error";
            }
        },
        PING {
            @Override
            public String toString() {
                return "ping";
            }
        }
    }

    enum Feedback {
        HELP {
            @Override
            public String toString() {
                return "HELP";
            }
        },
        AUDIO_CALL {
            @Override
            public String toString() {
                return "AUDIO_CALL";
            }
        },
        VIDEO_CALL {
            @Override
            public String toString() {
                return "VIDEO_CALL";
            }
        },
        VIDEO_CONFERENCE {
            @Override
            public String toString() {
                return "VIDEO_CONFERENCE";
            }
        }
    }

    /**
     * Request codes are used for segregating the various
     * requests used to perform actions and getting the
     * results back through that request code.
     * <p/>
     * In series of 500
     */

    interface Request {
        int RESULT_PAYMENT_ERROR = 200;  /*Whenever transaction failed in case of payment gateway either add card or transaction in process*/
    }
}

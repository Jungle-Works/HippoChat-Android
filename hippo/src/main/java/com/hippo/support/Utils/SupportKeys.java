package com.hippo.support.Utils;

/**
 * Created by gurmail on 30/03/18.
 */

public interface SupportKeys {

    interface SupportKey {
        String SUPPORT_DATA = "support_data";
        String DEFAULT_SUPPORT = "default_support";
        String SUPPORT_PATH = "support_path";
        String SUPPORT_TITLE = "support_title";
        String SUPPORT_DB_VERSION = "support_db_version";
        String SUPPORT_TRANSACTION_ID = "support_transaction_id";
        String SUPPORT_CATEGORY_ID = "support_category_id";
        String SUPPORT_CATEGORY_DATA = "support_category_data";
        String SUPPORT_POWERED_VIA = "powered_via";
    }

    enum SupportQueryType {
        NONE(0),
        CHAT(1),
        QUERY(2);

        private final int viewType;

        SupportQueryType(int viewType) {
            this.viewType = viewType;
        }

        public static SupportQueryType get(int viewType) {
            SupportQueryType actionType = NONE;

            for(SupportQueryType type : values()) {
                if(type.getSupportQueryType() == viewType) {
                    actionType = type;
                    return actionType;
                }
            }
            return actionType;
        }
        public int getSupportQueryType() {
            return viewType;
        }
    }

    enum SupportViewType {
        NONE(0),
        SUBMIT_VIEW(1),
        CHAT_VIEW(2),
        CALL_VIEW(3);

        private final int viewType;

        /**
         * Constructor
         *
         * @param viewType
         */
        SupportViewType(int viewType) {
            this.viewType = viewType;
        }

        /**
         * @param viewType
         * @return
         */
        public static SupportViewType get(int viewType) {
            SupportViewType status = NONE;

            for (SupportViewType value : values()) {
                if (value.getSupportViewType() == viewType) {
                    status = value;
                    return status;
                }
            }
            return status;
        }

        public int getSupportViewType() {
            return viewType;
        }
    }

    enum SupportActionType {
        // No Error
        NONE(-1),

        CATEGORY(0),
        LIST(1),
        CHAT_SUPPORT(2),
        DESCRIPTION(3),
        SHOW_CONVERSATION(4);

        private final int actionType;

        /**
         * Constructor
         *
         * @param actionType
         */
        SupportActionType(int actionType) {
            this.actionType = actionType;
        }

        /**
         * @param actionType
         * @return
         */
        public static SupportActionType get(int actionType) {

            SupportActionType status = NONE;

            for (SupportActionType value : values()) {
                if (value.getSupportActionType() == actionType) {
                    status = value;
                    break;
                }
            }

            return status;
        }

        public int getSupportActionType() {
            return actionType;
        }
    }
}

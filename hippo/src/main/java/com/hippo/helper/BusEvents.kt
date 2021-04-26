package com.hippo.helper

/**
 * Created by gurmail on 2020-04-19.
 * @author gurmail
 */
enum class BusEvents {
    CONNECTED_SERVER {
        override fun toString(): String {
            return "CONNECTED_SERVER"
        }
    },
    DISCONNECTED_SERVER {
        override fun toString(): String {
            return "DISCONNECTED_SERVER"
        }
    },
    RECEIVED_MESSAGE {
        override fun toString(): String {
            return "RECEIVED_MESSAGE"
        }
    },
    PONG_RECEIVED {
        override fun toString(): String {
            return "PONG_RECEIVED"
        }
    },
    WEBSOCKET_ERROR {
        override fun toString(): String {
            return "WEBSOCKET_ERROR"
        }
    },
    ERROR_RECEIVED {
        override fun toString(): String {
            return "ERROR_RECEIVED"
        }
    },
    NOT_CONNECTED {
        override fun toString(): String {
            return "NOT_CONNECTED"
        }
    },

}
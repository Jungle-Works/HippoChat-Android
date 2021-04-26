package com.hippo.datastructure;

/**
 * Created by ankit on 14/09/17.
 */

public enum ChatType {
    GENERAL_CHAT(0),
    P2P(1),
    GROUP_CHAT(2),
    CHAT_BY_TRANSACTION_ID(3);

    private int ordinal;

    ChatType(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}

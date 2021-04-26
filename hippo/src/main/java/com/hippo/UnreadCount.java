package com.hippo;

/**
 * Created by gurmail on 06/06/18.
 *
 * @author gurmail
 */
public interface UnreadCount {
    void count(int count);
    void unreadCountFor(int count);
    void unreadAnnouncementsCount(int count);
}

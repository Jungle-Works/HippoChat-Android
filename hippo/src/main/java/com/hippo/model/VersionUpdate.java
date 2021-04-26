package com.hippo.model;

/**
 * Created by gurmail on 2019-11-05.
 *
 * @author gurmail
 */
public class VersionUpdate {

    private int version;
    private int lastVersion;
    private boolean isEnable;
    private long lastShown;
    private long interval;
    private String text;
    private String link;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public long getLastShown() {
        return lastShown;
    }

    public void setLastShown(long lastShown) {
        this.lastShown = lastShown;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

package com.hippo.aws;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatamAws {

    @SerializedName("source_url")
    @Expose
    private String source_url;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnail_url;


    @SerializedName("thumbnail_file_name")
    @Expose
    private String thumbnail_file_name;
    @SerializedName("thumbnail_source_url")
    @Expose
    private String thumbnail_source_url;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("file_path")
    @Expose
    private String filePath;

    public String getUrl() {
        return url;
    }

    public String getThumbnail_file_name() {
        return thumbnail_file_name;
    }

    public void setThumbnail_file_name(String thumbnail_file_name) {
        this.thumbnail_file_name = thumbnail_file_name;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getThumbnail_source_url() {
        return thumbnail_source_url;
    }

    public void setThumbnail_source_url(String thumbnail_source_url) {
        this.thumbnail_source_url = thumbnail_source_url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}

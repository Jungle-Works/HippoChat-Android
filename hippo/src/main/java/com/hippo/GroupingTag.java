package com.hippo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 14/06/18.
 *
 * @author gurmail
 */

public class GroupingTag {

    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("reseller_team_id")
    @Expose
    private Integer teamId;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}

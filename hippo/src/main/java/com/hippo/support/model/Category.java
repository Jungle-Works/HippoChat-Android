package com.hippo.support.model;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public class Category {
    private int categoryId;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    private String categoryName;
    private String isActive;

    public Category(int categoryId, String categoryName, String isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isActive = isActive;
    }
}

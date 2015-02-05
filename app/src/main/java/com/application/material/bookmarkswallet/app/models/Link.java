package com.application.material.bookmarkswallet.app.models;

import com.application.material.bookmarkswallet.app.R;

public class Link {
    private int id;
    private String iconPath;
    private String url;
    private int userId;
    private String name;

    public Link(int id, String iconPath, String name,
                String url, int userId) {
        this.id = id;
        this.iconPath = iconPath;
        this.name = name;
        this.url = url;
        this.userId = userId;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public String getLinkUrl() {
        return this.url;
    }

    public void setLinkUrl(String value) {
        this.url = value;
    }

    public String getLinkName() {
        return this.name;
    }

    public void setLinkName(String value) {
        this.name = value;
    }

    public int getLinkId() {
        return this.id;
    }


}

package com.application.material.bookmarkswallet.app.models;

import android.graphics.Bitmap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Link {
    private long timestamp;
    private Bitmap iconBitmap;
    private int id;
    private String iconPath;
    private String url;
    private int userId;
    private String name;

    public Link(int id, String iconPath, Bitmap iconBitmap, String name,
                String url, int userId, long timestamp) {
        this.id = id;
        this.iconBitmap = iconBitmap;
        this.iconPath = null;
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public static long getTodayTimestamp() {
        return DateTime.now().getMillis();
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

    public long getTimestamp() {
        return timestamp;
    }

    public String getParsedTimestamp() {
        DateTime dt = new DateTime(this.timestamp);
        if(dt.getDayOfMonth() == DateTime.now().getDayOfMonth()) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm");
            return fmt.print(dt);
        }

//        if(dt.getDayOfMonth() == DateTime.now().minusDays(1).getDayOfMonth()) {
//            return "yesterday";
//        }

        if(dt.getYear() == DateTime.now().getYear()) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd");
            return fmt.print(dt);
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
        return fmt.print(dt);
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }
}

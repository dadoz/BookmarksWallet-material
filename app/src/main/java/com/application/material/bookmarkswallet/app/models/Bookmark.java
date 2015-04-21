package com.application.material.bookmarkswallet.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.UUID;

public class Bookmark extends RealmObject {

    @PrimaryKey
    private long id;
    private long timestamp;
    private String iconPath;
    private String url;
    private int userId;
    private String name;
    private byte[] blobIcon;

//    public Bookmark(int id, String iconPath, byte[] blobIcon, String name,
//                    String url, int userId, long timestamp) {
//        this.id = id;
//        this.blobIcon= blobIcon;
//        this.iconPath = iconPath;
//        this.name = name;
//        this.url = url;
//        this.userId = userId;
//        this.timestamp = timestamp;
//    }


    public int getUserId() {
        return this.userId;
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public String getName() {return this.name;}

    public void setName(String value) {
        this.name = value;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return timestamp;
    }



    public void setBlobIcon(byte[] blobIcon) {
        this.blobIcon = blobIcon;
    }

    public byte[] getBlobIcon() {
        return blobIcon;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static class Utils {
        public static String getParsedTimestamp(long timestamp) {
            DateTime dt = new DateTime(timestamp);
            if(dt.getDayOfMonth() == DateTime.now().getDayOfMonth()) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
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

        public static Bitmap getIconBitmap(byte[] blobIcon) {
            if(blobIcon == null) {
                return null;
            }

            return BitmapFactory.decodeByteArray(blobIcon, 0, blobIcon.length);
        }

        public static long getTodayTimestamp() {
            return DateTime.now().getMillis();
        }


    }
}

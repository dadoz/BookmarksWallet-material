package com.application.material.bookmarkswallet.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
    private long lastUpdate;
    private String iconPath;
    private String url;
    private int userId;
    private String name;
    private byte[] blobIcon;
    @Ignore
    public static String nameField = "name";
    @Ignore
    public static String urlField = "url";
    @Ignore
    private static final String NO_TITLE_STRING = "(no title)";
    @Ignore
    public static final String timestampField = "timestamp";

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

    public String getName() { return this.name; }

    public void setName(String value) {
        this.name = value;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public void setBlobIcon(byte[] blobIcon) {
        this.blobIcon = blobIcon;
    }

    public byte[] getBlobIcon() {
        Log.e("TAG", blobIcon == null ? "null" : blobIcon.length + "");
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

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static class Utils {
        /**
         *
         * @param timestamp
         * @return
         */
        public static String getParsedTimestamp(long timestamp) {
            DateTime dt = new DateTime(timestamp);
            if(dt.getDayOfMonth() == DateTime.now().getDayOfMonth()) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
                return fmt.print(dt);
            }

            if(dt.getYear() == DateTime.now().getYear()) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd");
                return fmt.print(dt);
            }

            DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
            return fmt.print(dt);
        }

        /**
         *
         * @return
         */
        public static long getTodayTimestamp() {
            return DateTime.now().getMillis();
        }

        /**
         *
         * @param bookmark
         * @return
         */
        public static String stringify(Bookmark bookmark) {
            if (bookmark == null) {
                return null;
            }

            return bookmark.getName() == null || bookmark.getName().compareTo("") == 0 ?
                    bookmark.getUrl() :
                    bookmark.getName() + " -\n" + bookmark.getUrl();
        }

        /**
         *
         * @param name
         * @return
         */
        public static String getBookmarkNameWrapper(String name) {
            return name == null || name.trim().equals("") ?
                    "(no title)" : name;
        }
    }
}

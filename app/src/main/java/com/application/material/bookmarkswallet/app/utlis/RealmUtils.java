package com.application.material.bookmarkswallet.app.utlis;

import android.support.annotation.NonNull;

import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmUtils {
    /**
     *
     * @return
     */
    public static RealmResults<Bookmark> getResults(@NonNull Realm realm) {
        return realm
                .where(Bookmark.class)
                .findAll()
                .sort(Bookmark.timestampField, Sort.DESCENDING);
    }

    /**
     *
     * @param realm
     * @return
     */
    public static ArrayList<Bookmark> getResultsList(@NonNull Realm realm) {
        ArrayList<Bookmark> list = new ArrayList<>();
        for (Bookmark bookmark : getResults(realm)) {
            list.add(bookmark);
        }
        return list;
    }


    /**
     *
     * @param bookmark
     */
    public static void deleteFromRealm(@NonNull Realm realm, Bookmark bookmark) {
        realm.beginTransaction();
        bookmark.deleteFromRealm();
        realm.commitTransaction();
    }

    /**
     *
     * @param list
     */
    public static void deleteListFromRealm(@NonNull Realm realm, ArrayList<Bookmark> list) {
        realm.beginTransaction();
        for (Bookmark bookmark: list) {
            bookmark.deleteFromRealm();
        }
        realm.commitTransaction();
    }

    /**
     * delete all bookmarks stored
     */
    public static void deleteAllFromRealm(@NonNull Realm realm) {
        realm.beginTransaction();
        realm.where(Bookmark.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    /**
     *
     * @param realm
     * @param title
     * @param iconPath
     * @param blobIcon
     * @param url
     * @return
     */
    public static boolean addItemOnRealm(@NonNull Realm realm, String title, String iconPath,
                                byte[] blobIcon, String url) {
        try {
            if (url == null) {
                return false;
            }
            realm.beginTransaction();
            Bookmark bookmark = realm.createObject(Bookmark.class);
            bookmark.setId(UUID.randomUUID().getLeastSignificantBits());
            bookmark.setName(title == null ? "" : title);
            if (iconPath != null) {
                bookmark.setIconPath(iconPath);
            }
            if (blobIcon != null) {
                bookmark.setBlobIcon(blobIcon);
            }
            bookmark.setUrl(url);
            bookmark.setTimestamp(Bookmark.Utils.getTodayTimestamp());
            bookmark.setLastUpdate(Bookmark.Utils.getTodayTimestamp());
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            realm.cancelTransaction();
            e.printStackTrace();
        }
        return false;
    }

}

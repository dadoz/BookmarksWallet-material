package com.application.material.bookmarkswallet.app.utlis;

import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by davide on 19/09/16.
 */
public class RealmUtils {
    /**
     *
     * @return
     */
    public static RealmResults<Bookmark> getResults(Realm realm) {
        if (realm == null) {
            return null;
        }
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
    public static ArrayList<Bookmark> getResultsList(Realm realm) {
        ArrayList<Bookmark> list = new ArrayList<>();
        for (Bookmark bookmark : getResults(realm)) {
            list.add(bookmark);
        }
        return list;
    }
}

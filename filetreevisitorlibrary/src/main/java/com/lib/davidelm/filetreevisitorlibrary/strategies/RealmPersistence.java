package com.lib.davidelm.filetreevisitorlibrary.strategies;

import android.content.Context;
import android.util.Log;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmPersistence implements PersistenceStrategyInterface {
    private static final String TAG = "RealmPersistence";

    RealmPersistence(WeakReference<Context> context) {
        Realm.init(context.get());
    }
    /**
     * reading on local storage
     */
    @Override
    public TreeNodeInterface getPersistentNode() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TreeNodeRealm> resultList = realm.where(TreeNodeRealm.class).findAll();
        return resultList.size() != 0 ? realm.copyFromRealm(resultList).get(0) : null;
    }

    /**
     * saving on local storage
     */

    @Override
    public void setPersistentNode(TreeNodeInterface node) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(((TreeNodeRealm) node));
        realm.commitTransaction();
    }

}
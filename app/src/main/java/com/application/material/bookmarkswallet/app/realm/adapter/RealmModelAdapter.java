package com.application.material.bookmarkswallet.app.realm.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by davide on 21/04/15.
 */
public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {
    public RealmModelAdapter(Context context, RealmResults realmResults) {
        super(context, realmResults);
        realmResults.sort("timestamp", Sort.DESCENDING);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}

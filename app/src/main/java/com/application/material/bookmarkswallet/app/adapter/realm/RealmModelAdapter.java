package com.application.material.bookmarkswallet.app.adapter.realm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by davide on 21/04/15.
 */
public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {
    public RealmModelAdapter(Context context, RealmResults realmResults, boolean automaticsUpdate) {
        super(context, realmResults, automaticsUpdate);
        realmResults.sort("timestamp", RealmResults.SORT_ORDER_DESCENDING);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}

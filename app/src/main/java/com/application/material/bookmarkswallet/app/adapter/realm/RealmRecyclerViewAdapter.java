package com.application.material.bookmarkswallet.app.adapter.realm;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

/**
 * Created by davide on 21/04/15.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmBaseAdapter<T> realmBaseAdapter;

    public void setRealmBaseAdapter(RealmBaseAdapter<T> adapter) {
        realmBaseAdapter = adapter;
    }

    public RealmBaseAdapter<T> getRealmBaseAdapter() {
        return realmBaseAdapter;
    }

    public RealmObject getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }

}

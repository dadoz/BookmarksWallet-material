package com.application.material.bookmarkswallet.app.adapter.realm;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by davide on 21/04/15.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmModelAdapter<T> realmBaseAdapter;

    public void setRealmBaseAdapter(RealmModelAdapter<T> adapter) {
        realmBaseAdapter = adapter;
    }

    public RealmBaseAdapter<T> getRealmBaseAdapter() {
        return realmBaseAdapter;
    }

    public RealmObject getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }

}

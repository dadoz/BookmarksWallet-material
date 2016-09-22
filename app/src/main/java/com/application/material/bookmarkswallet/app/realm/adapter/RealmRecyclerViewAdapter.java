package com.application.material.bookmarkswallet.app.realm.adapter;
import android.support.v7.widget.RecyclerView;

import com.application.material.bookmarkswallet.app.adapter.ItemTouchHelperAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmModelAdapter<T> realmBaseAdapter;

    public void setRealmBaseAdapter(RealmModelAdapter<T> adapter) {
        realmBaseAdapter = adapter;
    }

    public RealmBaseAdapter<T> getRealmBaseAdapter() {
        return realmBaseAdapter;
    }

    public Bookmark getItem(int position) {
        return (Bookmark) realmBaseAdapter.getItem(position);
    }

    public abstract void updateData(RealmResults<Bookmark> filteredList);
}

package com.application.material.bookmarkswallet.app.realm.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmModelAdapter<T> realmBaseAdapter;

    public RealmRecyclerViewAdapter(WeakReference<Context> ctx) {
        setRealmBaseAdapter(new RealmModelAdapter(ctx.get(),
                RealmUtils.getResults(Realm.getDefaultInstance())));
    }

    private void setRealmBaseAdapter(RealmModelAdapter<T> adapter) {
        realmBaseAdapter = adapter;
    }

    public Bookmark getItem(int position) {
        return (Bookmark) realmBaseAdapter.getItem(position);
    }

    public int getItemCount() {
        return realmBaseAdapter == null ? 0 : realmBaseAdapter.getCount();
    }

    public void updateData(RealmResults<T> filteredList) {
        realmBaseAdapter.updateData(filteredList);
    }
}

package com.application.material.bookmarkswallet.app.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;

import java.lang.ref.WeakReference;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmBaseAdapter<T> realmBaseAdapter;

    public RealmRecyclerViewAdapter(WeakReference<Context> context) {
        realmBaseAdapter = new RealmBaseAdapter<T>(context.get(), getOrderedResult()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return convertView;
            }
        };
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

    /**
     *
     * @return
     */
    public OrderedRealmCollection<T> getOrderedResult() {
        RealmResults realmResults = RealmUtils.getResults(Realm.getDefaultInstance());
        realmResults.sort("timestamp", Sort.DESCENDING);
        return realmResults;
    }
}

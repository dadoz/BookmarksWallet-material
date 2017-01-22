package com.application.material.bookmarkswallet.app.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.realm.Realm;

import static android.content.ContentValues.TAG;

public class DefaultBookmarkImportManager {
    private static final String DEFAULT_BOOKMARKS_FILE = "default_bookmarks.json";

    /**
     *
     */
    public static void handleImportDefaultBookmarks(WeakReference<Context> context, @NonNull View emptyLinkListView,
                                              @NonNull SwipeRefreshLayout swipeRefreshLayout,
                                              @NonNull RecyclerView recyclerView) {
        try {
            //update interface
            emptyLinkListView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);

            //parse data helper class
            String json = Utils.readAssetsToString(context.get().getAssets(), DEFAULT_BOOKMARKS_FILE);
            JSONArray bookmarksArray = new JSONArray(json);
            for (int i = 0; i < bookmarksArray.length(); i++) {
                JSONObject bookmark = bookmarksArray.getJSONObject(i);
                RealmUtils.addItemOnRealm(Realm.getDefaultInstance(), bookmark.getString("title"),
                        bookmark.getString("iconUrl"), null, bookmark.getString("url"));
            }

            //update interface
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }
}

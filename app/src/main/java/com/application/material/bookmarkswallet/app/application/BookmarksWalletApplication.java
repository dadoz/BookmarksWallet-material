package com.application.material.bookmarkswallet.app.application;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.realm.migrations.BookmarkRealmMigration;
import com.flurry.android.FlurryAgent;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BookmarksWalletApplication extends Application {
    private SparseArrayParcelable<String> searchParamsArray;
    private RealmConfiguration realmConfig;
    private DrawerImageLoader drawerImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));

        //calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica-Neue-47-Light-Condensed.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setRealmDefaultConfiguration();
        setDrawerImageLoader();
    }

    private void setDrawerImageLoader() {
        drawerImageLoader = DrawerImageLoader.init(new AbstractDrawerImageLoader() {

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView);
            }

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(getApplicationContext())
                        .cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        });
    }

    /**
     *
     * @return
     */
    public RealmConfiguration setRealmDefaultConfiguration() {
        if (realmConfig == null) {
            Realm.init(this);
            realmConfig = new RealmConfiguration.Builder()
                    .schemaVersion(BookmarkRealmMigration.BOOKMARK_SCHEMA_VERSION) // Must be bumped when the schema changes
                    .migration(new BookmarkRealmMigration()) // Migration to run instead of throwing an exception
                    .build();
            Realm.setDefaultConfiguration(realmConfig);
        }
        return realmConfig;
    }

    /**
     *
     * @return
     */
    public SparseArrayParcelable<String> getSearchParamsArray() {
        return searchParamsArray;
    }

    /**
     *
     * @param searchParamsArray
     */
    public void setSearchParamsArray(SparseArrayParcelable<String> searchParamsArray) {
        this.searchParamsArray = searchParamsArray;
    }

    public DrawerImageLoader getDrawerImageLoader() {
        return drawerImageLoader;
    }
}

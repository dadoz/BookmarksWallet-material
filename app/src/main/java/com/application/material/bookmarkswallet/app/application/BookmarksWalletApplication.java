package com.application.material.bookmarkswallet.app.application;

import android.app.Application;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.realm.migrations.BookmarkRealmMigration;
import com.flurry.android.FlurryAgent;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BookmarksWalletApplication extends Application {

    private RealmConfiguration realmConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));
        setRealmDefaultConfiguration();
    }

    /**
     *
     * @return
     */
    public RealmConfiguration setRealmDefaultConfiguration() {
        if (realmConfig == null) {
            realmConfig = new RealmConfiguration.Builder(getApplicationContext())
                    .schemaVersion(BookmarkRealmMigration.BOOKMARK_SCHEMA_VERSION) // Must be bumped when the schema changes
                    .migration(new BookmarkRealmMigration()) // Migration to run instead of throwing an exception
                    .build();
            Realm.setDefaultConfiguration(realmConfig);
        }
        return realmConfig;
    }
}

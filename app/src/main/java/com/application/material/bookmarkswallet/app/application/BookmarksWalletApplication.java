package com.application.material.bookmarkswallet.app.application;

import android.app.Application;
import android.util.SparseArray;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.realm.migrations.BookmarkRealmMigration;
import com.flurry.android.FlurryAgent;
import com.squareup.leakcanary.LeakCanary;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BookmarksWalletApplication extends Application {
    private SparseArray<String> searchParamsArray;
    private RealmConfiguration realmConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));

        //calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica-Neue-47-Light-Condensed.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setRealmDefaultConfiguration();

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
    public SparseArray<String> getSearchParamsArray() {
        return searchParamsArray;
    }

    /**
     *
     * @param searchParamsArray
     */
    public void setSearchParamsArray(SparseArray<String> searchParamsArray) {
        this.searchParamsArray = searchParamsArray;
    }
}

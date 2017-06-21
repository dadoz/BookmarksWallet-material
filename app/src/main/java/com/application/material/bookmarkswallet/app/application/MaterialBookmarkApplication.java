package com.application.material.bookmarkswallet.app.application;

import android.app.Application;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.realm.migrations.BookmarkRealmMigration;
import com.flurry.android.FlurryAgent;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MaterialBookmarkApplication extends Application {
    private SparseArrayParcelable<String> searchParamsArray;
    private RealmConfiguration realmConfig;
    private List bookmarksList;

    @Override
    public void onCreate() {
        super.onCreate();

        //init flurry
        initFlurry();

        //calligraphy
        initCalligraphy();

        //realm
        initRealmDefaultConfiguration();
    }

    /**
     *
     */
    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica-Neue-47-Light-Condensed.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

    }

    /**
     *
     */
    private void initFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));

    }

    /**
     *
     * @return
     */
    public void initRealmDefaultConfiguration() {
        if (realmConfig == null) {
            Realm.init(this);
            realmConfig = new RealmConfiguration.Builder()
                    .schemaVersion(BookmarkRealmMigration.BOOKMARK_SCHEMA_VERSION) // Must be bumped when the schema changes
                    .migration(new BookmarkRealmMigration()) // Migration to run instead of throwing an exception
                    .build();
            Realm.setDefaultConfiguration(realmConfig);
        }
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

    public void setBookmarksList(List bookmarksList) {
        this.bookmarksList = bookmarksList;
    }

    public List getBookmarksList() {
        return bookmarksList;
    }
}

package com.application.material.bookmarkswallet.app.realm.migrations;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class BookmarkRealmMigration implements RealmMigration {
    private static final String BOOKMARK_SCHEMA = "Bookmark";
    private static final String TREENODE_SCHEMA = "TreeNodeRealm";
    public static long BOOKMARK_SCHEMA_VERSION = 0;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
//        if (oldVersion == 0) {
//            schema.get(BOOKMARK_SCHEMA)
//                    .addIndex("id")
//                    .setNullable("iconPath", true)
//                    .setNullable("blobIcon", true);
//
//        }
        if (oldVersion == 1) {
            schema.get(TREENODE_SCHEMA)
                    .addIndex("id")
                    .setNullable("iconPath", true)
                    .setNullable("blobIcon", true);

        }
    }
}
package com.application.material.bookmarkswallet.app.persistence;

import static com.application.material.bookmarkswallet.app.persistence.PersistenceFactory.Type.FIREBASE;
import static com.application.material.bookmarkswallet.app.persistence.PersistenceFactory.Type.REALM;

public class PersistenceFactory {
    enum Type {FIREBASE, REALM}

    public static Persistence getPersistenceManager(Type type) {
        if (type == REALM)
            return new RealmPersistenceManager();
        if (type == FIREBASE)
            return new FirebasePersistenceManager();

        return new RealmPersistenceManager();
    }
}

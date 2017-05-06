package com.lib.davidelm.filetreevisitorlibrary.strategies;

import android.content.Context;

import java.lang.ref.WeakReference;

import static com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy.PersistenceType.UNDEFINED;


public class PersistenceStrategy {
    private final WeakReference<Context> context;
    public PersistenceStrategyInterface strategy;

    public enum PersistenceType {SHARED_PREF, FIREBASE, REALMIO, UNDEFINED}

    public PersistenceStrategy(WeakReference<Context> context, PersistenceType type) {
        this.context = context;
        setStrategy(type);
    }

    public PersistenceStrategyInterface getStrategy() {
        return strategy;
    }

    public PersistenceType getPersistenceType() {
        if (strategy instanceof SharedPrefPersistence)
            return PersistenceType.SHARED_PREF;
        if (strategy instanceof RealmPersistence)
            return PersistenceType.REALMIO;
        if (strategy instanceof FirebasePersistence)
            return PersistenceType.FIREBASE;
        return UNDEFINED;
    }

    void setStrategy(PersistenceType type) {
        switch (type.name()) {
            case "SHARED_PREF":
                this.strategy = new SharedPrefPersistence(context);
                break;
            case "FIREBASE":
                this.strategy = new FirebasePersistence();
                break;
            case "REALMIO":
                this.strategy = new RealmPersistence(context);
                break;
        }
    }

}

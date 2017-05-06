package com.lib.davidelm.filetreevisitorlibrary.manager;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;
import com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy;

class TreeNodeFactory {
    public static TreeNodeInterface getChildByPersistenceType(PersistenceStrategy.PersistenceType persistenceType, String nodeName, boolean folder, int i) {
        if (persistenceType == PersistenceStrategy.PersistenceType.FIREBASE)
            throw new UnsupportedOperationException("not implemented");
        if (persistenceType == PersistenceStrategy.PersistenceType.REALMIO)
            return new TreeNodeRealm(nodeName, folder, i);
        if (persistenceType == PersistenceStrategy.PersistenceType.SHARED_PREF)
            return new TreeNode(nodeName, folder, i);
        return null;
    }
}

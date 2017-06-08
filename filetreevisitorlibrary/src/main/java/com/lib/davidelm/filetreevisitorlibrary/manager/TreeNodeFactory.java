package com.lib.davidelm.filetreevisitorlibrary.manager;

import android.support.annotation.NonNull;

import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContentRealm;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;
import com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy;
import com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy.PersistenceType;

class TreeNodeFactory {
    public static TreeNodeInterface getChildByPersistenceType(PersistenceType persistenceType,
                                                              String nodeName, boolean folder, int i) {

        if (persistenceType == PersistenceType.FIREBASE)
            throw new UnsupportedOperationException("not implemented");
        if (persistenceType == PersistenceType.REALMIO)
            return new TreeNodeRealm(new TreeNodeContentRealm(nodeName, "fake description",
                    R.mipmap.ic_chevron_right, R.mipmap.ic_folder), folder, i);
        if (persistenceType == PersistenceType.SHARED_PREF)
            return new TreeNode(new TreeNodeContentRealm(nodeName, "fake description",
                    R.mipmap.ic_chevron_right, R.mipmap.ic_folder), folder, i);
        return null;
    }

    public static TreeNodeInterface getChildByPersistenceType(PersistenceType persistenceType,
                                                              @NonNull TreeNodeContent nodeContent,
                                                              boolean folder, int i) {

        if (persistenceType == PersistenceType.FIREBASE)
            throw new UnsupportedOperationException("not implemented");
        if (persistenceType == PersistenceType.REALMIO)
            return new TreeNodeRealm(nodeContent, folder, i);
        if (persistenceType == PersistenceType.SHARED_PREF)
            return new TreeNode(nodeContent, folder, i);
        return null;
    }
}

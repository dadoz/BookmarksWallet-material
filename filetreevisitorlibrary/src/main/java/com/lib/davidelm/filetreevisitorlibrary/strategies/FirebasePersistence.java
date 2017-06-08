package com.lib.davidelm.filetreevisitorlibrary.strategies;


import android.support.annotation.Nullable;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 22/04/2017.
 */

public class FirebasePersistence implements PersistenceStrategyInterface {
    @Nullable
    @Override
    public TreeNodeInterface getPersistentNode() {
        return null;
    }

    @Override
    public void setPersistentNode(TreeNodeInterface node) {

    }
}

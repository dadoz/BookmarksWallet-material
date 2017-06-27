package com.lib.davidelm.filetreevisitorlibrary.manager;

import android.content.Context;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by davide on 26/06/2017.
 */

public class NodeListManager implements NodeListInterface {

    private static NodeListManager instance;
    private final TreeNodeInterface rootNode;
    private WeakReference<Context> context;

    public NodeListManager(Context context) {
        this.context = new WeakReference<>(context);
        rootNode = RootNodeManager.getInstance(this.context).getRoot();
    }

    public static NodeListManager getInstance(Context context) {
        return instance == null ? instance = new NodeListManager(context) : instance;
    }

    @Override
    public List<Object> getNodeList() {
        ArrayList<Object> list = new ArrayList<>();
        if (rootNode == null)
            return list;

        return getChildrenOnNode(list, rootNode, 0);
    }

    /**
     *
     * @param list
     * @param node
     * @param index
     * @return
     */
    List<Object> getChildrenOnNode(List<Object> list, TreeNodeInterface node,
                                   int index) {
        for (int i = 0; i < node.getChildren().size() -1; i ++) {
            if (node.getChildren() != null &&
                    node.getChildren().size() > 0)
                 getChildrenOnNode(list, node.getChildren().get(index), index++);
        }
        //filter folder out
        Object[] items = node.getChildren()
                .stream()
                .filter(item -> !item.isFolder())
                .toArray();
        list.addAll(Arrays.asList(items));
        return list;
    }
}

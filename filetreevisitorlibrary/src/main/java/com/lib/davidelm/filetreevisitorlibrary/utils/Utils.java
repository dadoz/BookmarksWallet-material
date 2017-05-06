package com.lib.davidelm.filetreevisitorlibrary.utils;


import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;

public class Utils {
    /**
     *
     * @return
     */
    public static TreeNode initRoot() {
        //init tree node
        TreeNode root = TreeNode.root();
        TreeNode parent = new TreeNode("MyParentNode", true, 0);
        TreeNode child0 = new TreeNode("ChildNode0", true, 1);
        TreeNode child1 = new TreeNode("ChildNode1", true, 1);
        child0.addChild(new TreeNode("Test Child of Child", true, 2));
        parent.addChildren(child0, child1);
        root.addChild(parent);
        return root;
    }
}

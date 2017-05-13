package com.lib.davidelm.filetreevisitorlibrary.models;

import io.realm.RealmObject;

/**
 * Created by davide on 12/05/2017.
 */

public class TreeNodeContentRealm extends RealmObject implements TreeNodeContent  {
    private String name;
    private String description;
    private int fileResource;
    private int folderResource;
    public TreeNodeContentRealm() {}

    public TreeNodeContentRealm(String name, String description, int fileResource, int folderResource) {
        this.name = name;
        this.description = description;
        this.fileResource = fileResource;
        this.folderResource = folderResource;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getFileResource() {
        return fileResource;
    }

    @Override
    public int getFolderResource() {
        return folderResource;
    }
}

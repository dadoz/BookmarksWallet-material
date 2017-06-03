package com.lib.davidelm.filetreevisitorlibrary.models;

import android.graphics.Bitmap;

public interface TreeNodeContent {
    String getName();
    String getDescription();
    String getFileUri();
    byte[] getFileBlob();
    int getFileResource();
    int getFolderResource();
}

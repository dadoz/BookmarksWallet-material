package com.application.material.bookmarkswallet.app.utlis;

/**
 * Created by davide on 21/06/2017.
 */

public class TreeNodeContentUtils {
    public static String getBookmarkName(String name) {
        return name == null || name.trim().equals("") ?
                "(no title)" : name;
    }

}

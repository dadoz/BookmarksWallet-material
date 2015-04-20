package com.application.material.bookmarkswallet.app.dbAdapter_old;

import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.util.ArrayList;

/**
 * Created by davide on 27/01/15.
 */
public interface BookmarksDataInterface {
    boolean insertLink(Bookmark bookmarkObj);

    /**INSERT ROW in dbAdapterAdapter - overloading insert function*/
//    boolean insertLink(int linkId, int linkOrderInList,
//                                     String linkName, String iconPath,
//                                     String linkUrl, int linksUserId);
    /**GET ALL ROWS from dbAdapter**/
    ArrayList<Bookmark> getLinkList();

    /**GET ALL ROWS from dbAdapter**/
    ArrayList<Bookmark> getLinkListTest();

    /**GET ONE ROW from dbAdapter**/
    Bookmark getLinkById(int linkId);

    /**GET ONE ROW from dbAdapter**/
    boolean deleteAllLinks();
    /**GET ONE ROW from dbAdapter**/
    boolean deleteLinkById(int linkId);
    /**GET ONE ROW from dbAdapter**/
    boolean deleteLinkByObject(Bookmark bookmarkObj);
    void updateLinkByObject(Bookmark bookmarkObj);

}

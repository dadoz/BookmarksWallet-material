package com.application.material.bookmarkswallet.app.dbAdapter;

import android.database.Cursor;
import com.application.material.bookmarkswallet.app.models.Link;

import java.util.ArrayList;

/**
 * Created by davide on 27/01/15.
 */
public interface BookmarksDataInterface {
    boolean insertLink(Link linkObj);

    /**INSERT ROW in dbAdapterAdapter - overloading insert function*/
    boolean insertLink(int linkId, int linkOrderInList,
                                     String linkName, String iconPath,
                                     String linkUrl, int linksUserId);
    /**GET ALL ROWS from dbAdapter**/
    ArrayList<Link> getLinkList();

    /**GET ALL ROWS from dbAdapter**/
    ArrayList<Link> getLinkListTest();

    /**GET ONE ROW from dbAdapter**/
    Link getLinkById(int linkId);

    /**GET ONE ROW from dbAdapter**/
    boolean deleteAllLinks();
    /**GET ONE ROW from dbAdapter**/
    boolean deleteLinkById(int linkId);
    /**GET ONE ROW from dbAdapter**/
    boolean deleteLinkByObject(Link linkObj);
    void updateLinkByObject(Link linkObj);

}

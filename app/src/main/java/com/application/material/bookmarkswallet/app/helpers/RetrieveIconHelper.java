package com.application.material.bookmarkswallet.app.helpers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

public class RetrieveIconHelper {

    private static RetrieveIconHelper instance;
    private static WeakReference<OnRetrieveIconInterface> listener;
    private enum JobTypeEnum {BOOKMARK_ICON_URL, BOOKMARK_TITLE};

    public static RetrieveIconHelper getInstance(WeakReference<OnRetrieveIconInterface> lst) {
        listener = lst;
        return instance == null ? instance = new RetrieveIconHelper() : instance;
    }

    /**
     *
     * @param bookmarkUrl
     */
    public void retrieveIcon(String bookmarkUrl) {
        bookmarkUrl = Utils.buildUrl(bookmarkUrl, true);
        new RetrieveIconThread(bookmarkUrl, JobTypeEnum.BOOKMARK_ICON_URL).start();
    }

    /**
     *
     * @param bookmarkUrl
     */
    public void retrieveTitle(String bookmarkUrl) {
        bookmarkUrl = Utils.buildUrl(bookmarkUrl, true);
        new RetrieveIconThread(bookmarkUrl, JobTypeEnum.BOOKMARK_TITLE).start();
    }

    /**
     *
     */
    public class RetrieveIconThread extends Thread {
        private final static String LINK_SELECT_PARAM = "link[href~=.*\\.(png|ico)]";
        private final static String LINK_ATTR_PARAM = "abs:href";
        private final static String META_SELECT_PARAM = "meta[content~=.*\\.png]";
        private final static String META_ATTR_PARAM = "abs:content";
        private final String bookmarkUrl;
        private final JobTypeEnum jobType;
        private String TITLE_SELECT_PARAM = "title";

        public RetrieveIconThread(String bookmarkUrlString, JobTypeEnum type) {
            jobType = type;
            bookmarkUrl = bookmarkUrlString;
        }

        @Override
        public void run() {
//            Log.e("TAG", "----" + bookmarkUrl);
            String jobResult = doJob(bookmarkUrl, jobType);
            if (jobResult != null) {
                if (jobType == JobTypeEnum.BOOKMARK_ICON_URL) {
                    listener.get().onRetrieveIconSuccess(jobResult);
                } else if (jobType == JobTypeEnum.BOOKMARK_TITLE) {
                    listener.get().onRetrieveTitleSuccess(jobResult);
                }
                return;
            }

            listener.get().onRetrieveIconFailure("icon url not found!");
        }

        /**
         *
         * @param bookmarkUrl
         * @return
         */
        @Nullable
        private synchronized String doJob(String bookmarkUrl, JobTypeEnum jobType) {
            try {
                if (jobType == JobTypeEnum.BOOKMARK_ICON_URL) {
                    return getUrlByDoc(Jsoup.connect(bookmarkUrl).get());
                } else if (jobType == JobTypeEnum.BOOKMARK_TITLE) {
                    return getTitleByDoc(Jsoup.connect(bookmarkUrl).get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         *
         * @param doc
         * @return
         */
        public String getTitleByDoc(Document doc) {
            String temp = doc.head().select(TITLE_SELECT_PARAM).text();
            Log.e("TAG", "++++++" + temp);
            return temp;
        }

        /**
         *
         * @param doc
         * @return
         */
        public String getUrlByDoc(Document doc) {
            Elements elem;
            String attrParam = META_ATTR_PARAM;
            elem = doc.head().select(META_SELECT_PARAM);

            //find on href (header)
            if (elem == null ||
                    elem.first() == null) {
                attrParam = LINK_ATTR_PARAM;
                elem = doc.head().select(LINK_SELECT_PARAM);
            }

            String result = elem == null ||
                    elem.first() == null?
                    getImageUrlByDoc(doc)
                    : elem.first().attr(attrParam);
            Log.e("TAG", result);
            return result;
        }

        /**
         *
         * @param doc
         */
        private String getImageUrlByDoc(Document doc) {
            Elements elemArray = doc.select("img[src$=.png]");
            if (elemArray != null) {
                String url = elemArray.first().attr("src");
                return Utils.buildUrlToSearchIcon(url, bookmarkUrl);
            }
            return null;
        }
    }

    /**
     *
     */
    public interface OnRetrieveIconInterface {
        void onRetrieveIconSuccess(String iconUrl);
        void onRetrieveTitleSuccess(String title);
        void onRetrieveIconFailure(String error);
    }

}

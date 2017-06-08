package com.application.material.bookmarkswallet.app.helpers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

import static com.google.common.collect.ComparisonChain.start;

public class RetrieveIconHelper {

    private static RetrieveIconHelper instance;
    private static WeakReference<OnRetrieveIconInterface> listener;
    private RetrieveIconThread threadIcon;
    private RetrieveIconThread threadTitle;

    private enum JobTypeEnum {BOOKMARK_ICON_URL, BOOKMARK_TITLE}

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
        threadIcon = new RetrieveIconThread(bookmarkUrl, JobTypeEnum.BOOKMARK_ICON_URL);
        threadIcon.start();
    }

    /**
     *
     * @param bookmarkUrl
     */
    public void retrieveTitle(String bookmarkUrl) {
        bookmarkUrl = Utils.buildUrl(bookmarkUrl, true);
        threadTitle = new RetrieveIconThread(bookmarkUrl, JobTypeEnum.BOOKMARK_TITLE);
        threadTitle.start();
    }

    /**
     *
     */
    public void unsubscribe() {
        if (threadIcon != null)
            threadIcon.interrupt();
        if (threadTitle != null)
            threadTitle.interrupt();
    }
    /**
     * TODO add listener to this class
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
            String jobResult = doJob(bookmarkUrl, jobType);
            if (jobResult != null) {
                if (jobType == JobTypeEnum.BOOKMARK_ICON_URL) {
                    listener.get().onRetrieveIconSuccess(jobResult);
                } else if (jobType == JobTypeEnum.BOOKMARK_TITLE) {
                    listener.get().onRetrieveTitleSuccess(jobResult);
                }
                return;
            }

            if (listener.get() != null)
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
                Document doc = Jsoup.connect(bookmarkUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0")
                        .execute().parse();
                if (jobType == JobTypeEnum.BOOKMARK_ICON_URL) {
//                    return getIconUrlByGoogleService(bookmarkUrl);
                    return getIconUrlByDoc(doc);
                } else if (jobType == JobTypeEnum.BOOKMARK_TITLE) {
                    return getTitleByDoc(doc);
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
            return temp;
        }

        /**
         *
         * @param doc
         * @return
         */
        public String getIconUrlByDoc(Document doc) {
            Elements elem;
            String attrParam = META_ATTR_PARAM;
            elem = doc.head().select(META_SELECT_PARAM);

            //find on href (header)
            if (elem == null ||
                    elem.first() == null) {
                attrParam = LINK_ATTR_PARAM;
                elem = doc.head().select(LINK_SELECT_PARAM);
            }

            if (elem == null ||
                    elem.first() == null) {
                Elements elemArray = doc.select("img[src$=.png]");
                if (elemArray != null) {
                    String url = elemArray.first().attr("src");
                    return Utils.buildUrlToSearchIcon(url, bookmarkUrl);
                }
            }
            return elem.first().attr(attrParam);
        }

        /**
         *
         * @param doc
         */
        private String getFirstImageByDoc(Document doc) {
            Elements elemArray = doc.select("img[src$=.png]");
            if (elemArray != null) {
                String url = elemArray.first().attr("src");
                return Utils.buildUrlToSearchIcon(url, bookmarkUrl);
            }
            return null;
        }

        /**
         *
         * @param url
         * @return
         */
        public String getIconUrlByGoogleService(String url) {
            return "https://www.google.com/s2/favicons?domain=" + url;
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

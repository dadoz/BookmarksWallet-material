package com.application.material.bookmarkswallet.app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

public class RetrieveIconHelper {

    private static RetrieveIconHelper instance;
    private static WeakReference<OnRetrieveIconInterface> listener;

    public static RetrieveIconHelper getInstance(WeakReference<OnRetrieveIconInterface> lst) {
        //new WeakReference<OnTaskCompleted>(this))
        listener = lst;
        return instance == null ? instance = new RetrieveIconHelper() : instance;
    }

    /**
     *
     * @param bookmarkUrl
     */
    public void retrieveIcon(String bookmarkUrl) {
        new RetrieveIconThread(bookmarkUrl).start();
//        try {
//            new RetrieveIconAsyncTask(null)
//                    .execute(new URL(bookmarkUrl));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *
     */
    public class RetrieveIconThread extends Thread {
        private final static String HREF_SELECT_PARAM = "link[href~=.*\\.(png|ico)]";
        private final static String HREF_ATTR_PARAM = "abs:href";
        private final static String META_SELECT_PARAM = "meta[content~=.*\\.png]";
        private final static String META_ATTR_PARAM = "abs:content";
        private final String bookmarkUrl;

        public RetrieveIconThread(String bookmarkUrlString) {
            bookmarkUrl = bookmarkUrlString;
        }

        @Override
        public void run() {
            String iconUrl = doJob(bookmarkUrl);
            if (iconUrl == null) {
                listener.get().onRetrieveIconFailure("icon url not found!");
                return;
            }

            listener.get().onRetrieveIconSuccess(iconUrl);
        }


        /**
         *
         * @param bookmarkUrl
         * @return
         */
        private String doJob(String bookmarkUrl) {
            try {
                return getUrlByDoc(Jsoup.connect(bookmarkUrl).get());
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
        public String getUrlByDoc(Document doc) {
            Elements elem;
            String attrParam = META_ATTR_PARAM;
            elem = doc.head().select(META_SELECT_PARAM);

            //find on href (header)
            if (elem == null) {
                attrParam = HREF_ATTR_PARAM;
                elem = doc.head().select(HREF_SELECT_PARAM);
            }

            return elem == null ?
                getImageUrlByDoc(doc)
                : elem.first().attr(attrParam);
        }

        /**
         *
         * @param doc
         */
        private String getImageUrlByDoc(Document doc) {
            Elements elemArray = doc.select("img[src$=.png]");
            if (elemArray != null) {
                String url = elemArray.first().attr("src");
                return (url.split("/")[1]).compareTo("") != 0 ?
                        bookmarkUrl + url :
                        "http:" + url;
            }
            return null;
        }
    }

    /**
     *
     */
//    public class RetrieveIconAsyncTask extends AsyncTask<URL, Integer, String> {
//        private final WeakReference<OnRetrieveIconInterface> listener;
//        private final static String HREF_SELECT_PARAM = "link[href~=.*\\.(png|ico)]";
//        private final static String HREF_ATTR_PARAM = "abs:href";
//        private final static String META_SELECT_PARAM = "meta[content~=.*\\.png]";
//        private final static String META_ATTR_PARAM = "abs:content";
//
//        public RetrieveIconAsyncTask(WeakReference<OnRetrieveIconInterface> listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected String doInBackground(URL... linkUrlArray) {
//            try {
//                return getUrlByDoc(Jsoup.connect(linkUrlArray[0].toString()).get());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//        }
//
//        @Override
//        protected void onPostExecute(String iconUrl) {
//            listener.get().onTaskCompleted(iconUrl);
//        }
//
//        /**
//         *
//         * @param doc
//         * @return
//         */
//        public String getUrlByDoc(Document doc) {
//            Elements header;
//            String selector = META_SELECT_PARAM;
//            if ((header = doc.head()
//                    .select(selector)) == null) {
//                selector = HREF_SELECT_PARAM;
//                header = doc.head()
//                        .select(selector);
//            }
//
//            return header == null ? null : header.first().attr(selector);
//        }
//    }


    /**
     *
     */
    public interface OnRetrieveIconInterface {
        void onRetrieveIconSuccess(String bookmarkUrl);
        void onRetrieveIconFailure(String error);
//        void onTaskCompleted(byte [] data);
    }

}

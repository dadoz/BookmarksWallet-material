package com.application.material.bookmarkswallet.app.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by davide on 14/08/15.
 */
public class RetrieveIconAsyncTask extends AsyncTask<URL, Integer, String> {
    private final WeakReference<OnTaskCompleted> listener;
    private final static String HREF_SELECT_PARAM = "link[href~=.*\\.(png|ico)]";
    private final static String HREF_ATTR_PARAM = "abs:href";
    private final static String META_SELECT_PARAM = "meta[content~=.*\\.png]";
    private final static String META_ATTR_PARAM = "abs:content";

    public RetrieveIconAsyncTask(WeakReference<OnTaskCompleted> listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(URL... linkUrlArray) {
        Document doc = null;
        try {
            doc = Jsoup.connect(linkUrlArray[0].toString()).get();
            return getUrlByDoc(doc, true);
        } catch (Exception e) {
            try {
                return getUrlByDoc(doc, false);
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(String iconUrl) {
        listener.get().onTaskCompleted(iconUrl);
    }

    /**
     *
     * @param doc
     * @param isMetaTag
     * @return
     */
    public String getUrlByDoc(Document doc, boolean isMetaTag) {
        return doc.head()
                .select(isMetaTag ? META_SELECT_PARAM : HREF_SELECT_PARAM)
                .first()
                .attr(isMetaTag ? META_ATTR_PARAM : HREF_ATTR_PARAM);
    }
}

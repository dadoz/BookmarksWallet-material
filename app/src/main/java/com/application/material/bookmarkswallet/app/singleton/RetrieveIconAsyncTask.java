package com.application.material.bookmarkswallet.app.singleton;

import android.os.AsyncTask;
import android.util.Log;
import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by davide on 14/08/15.
 */
public class RetrieveIconAsyncTask extends AsyncTask<URL, Integer, Boolean> {
    private static final String TAG = "RetrieveIconAsyncTask";
    private final OnTaskCompleted listener;
    private String bookmarkUrl = null;
    private String bookmarkTitle = null;
    private String iconUrl = null;

    public RetrieveIconAsyncTask(OnTaskCompleted list) {
        listener = list;
    }

    @Override
    protected Boolean doInBackground(URL... linkUrlArray) {
        bookmarkUrl = linkUrlArray[0].toString();
        Document doc;
        try {
            doc = Jsoup.connect(bookmarkUrl).get();
//            bookmarkTitle = doc.title();
//            org.jsoup.nodes.Element elem = doc.head().select("link[href~=.*\\.ico]").first();
            org.jsoup.nodes.Element elem = doc.head().select("meta[content~=.*\\.png]").first();
            Log.e(TAG, "" + doc.head().select("meta[content~=.*\\.png]").first());
            iconUrl = elem.attr("abs:content");
            Log.d(TAG, " - " + iconUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Boolean isBookmarkInfoRetrieved) {
        try {
            if (! isBookmarkInfoRetrieved ||
                    iconUrl == null) {
                //notify to ui
                listener.onTaskCompleted(null);
            }

            getIconByUrl(iconUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get image by url
     * @param iconUrl
     * @throws MalformedURLException
     */
    public void getIconByUrl(String iconUrl)
            throws MalformedURLException {
        new AsyncTask<URL, Integer, byte[]>() {
            @Override
            protected byte[] doInBackground(URL... linkUrlArray) {
                return Utils.getBytesArrayByUrl(linkUrlArray[0]);
            }

            @Override
            protected void onPostExecute(byte[] iconByteArray) {
//                mSwipeRefreshLayout.setRefreshing(false);
                //CHECK out what u need
//                addBookmark(bookmarkUrl, iconByteArray, bookmarkTitle);
                //notify to ui
                listener.onTaskCompleted(iconByteArray);
            }
        }.execute(new URL(iconUrl));
    }
}

//package com.application.material.bookmarkswallet.app.asyncTask;
//
///**
// * Created by davide on 28/10/15.
// */
//
//import android.net.Uri;
//import android.os.AsyncTask;
//
//import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
//import com.application.material.bookmarkswallet.app.singleton.BookmarkProviderSingleton;
//
//import java.lang.ref.WeakReference;
//import java.net.URL;
//
///**
// * TODO move on service :O
// */
//public class BookmarkProviderAsyncTask2 extends AsyncTask<URL, Integer, Boolean> {
//    private final Integer N_OCCURENCES = 30;
//    private final Uri[] bookmarkUriArray;
//    private final Integer[] params = new Integer[1];
//    private final WeakReference<OnTaskCompleted> listener;
//    private final WeakReference<BookmarkProviderSingleton> bookmarkProviderRef;
//
//
//    public BookmarkProviderAsyncTask(Uri[] bookmarkUriArray,
//                                     WeakReference<OnTaskCompleted> onTaskCompletedWeakReference,
//                                     WeakReference<BookmarkProviderSingleton> bookmarkProviderSingletonWeakReference) {
//        this.bookmarkUriArray = bookmarkUriArray;
//        this.listener = onTaskCompletedWeakReference;
//        this.bookmarkProviderRef = bookmarkProviderSingletonWeakReference;
//    }
//
//    @Override
//    protected Boolean doInBackground(URL... params) {
////            setSyncStatus(SyncStatusEnum.RUNNING);
//        try {
//            bookmarkProviderRef.get()
//                    .addBookmarksByProviderJob(bookmarkUriArray[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                bookmarkProviderRef.get()
//                        .addBookmarksByProviderJob(bookmarkUriArray[1]);
//                publishProgress();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//
//        }
//        return null;
//    }
//
//    public void doProgress(int count) {
//        params[0] = count;
//        publishProgress(params);
//    }
//
//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        if (values.length != 0 &&
//                values[0] % N_OCCURENCES == 0) {
//            taskCompleted(true);
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Boolean result) {
////            mSwipeRefreshLayout.setRefreshing(false);
//        //taskCompleted
//        taskCompleted(false);
////            setSyncStatus(SyncStatusEnum.DONE);
//    }
//
//    /**
//     * update adapter to set data on interface
//     */
//    public void taskCompleted(boolean isRefreshingEnabled) {
//        listener.get().onTaskCompleted(isRefreshingEnabled);
//    }
//
//}

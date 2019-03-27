package com.application.material.bookmarkswallet.app.presenter;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.utlis.Utils;

public interface AddBookmarkResultView {
    void onRetrieveIconSuccess(final String url);
    void onRetrieveTitleSuccess(final String title);
    void onRetrieveIconFailure(final String error);
    void onRetrieveTitleFailure(final String error);
}

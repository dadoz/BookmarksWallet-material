package com.application.material.bookmarkswallet.app.fragments;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.RetrieveIconHelper;
import com.application.material.bookmarkswallet.app.manager.ClipboardManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.presenter.SearchBookmarkPresenter;
import com.application.material.bookmarkswallet.app.presenter.SearchResultPresenter;
import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddBookmarkResultLayout;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import io.realm.Realm;

public class AddBookmarkResultFragment extends Fragment implements
        View.OnClickListener,
        RetrieveIconHelper.OnRetrieveIconInterface, AddBookmarkActivity.OnHandleBackPressed {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private static final String TAG = "AddBookmarkFragment";
    @Bind(R.id.addBookmarkResultViewId)
    AddBookmarkResultLayout addBookmarkResultView;
    @Bind(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;

    @State
    public String bookmarkUrl;
    @State
    public String bookmarkTitle;
    private StatusManager statusManager;
    private RetrieveIconHelper retrieveIconHelper;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Icepick.saveInstanceState(this, savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Icepick.restoreInstanceState(this, savedInstance);
        statusManager = StatusManager.getInstance();
        retrieveIconHelper = RetrieveIconHelper
                .getInstance(new WeakReference<RetrieveIconHelper.OnRetrieveIconInterface>(this));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_add_bookmark_result_layout, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(savedInstanceState);
    }


    /**
     *
     * @param savedInstanceState
     */
    private void onInitView(Bundle savedInstanceState) {
//        searchResultPresenter.init(new View[] {addBookmarkMainLayout, addBookmarkResultLayout,
//                addBookmarkRelativeLayout});
        statusManager.setOnResultMode();
        Utils.hideKeyboard(getActivity());
        retrieveIcon();

        //init bookmark
        addBookmarkResultView.initView(bookmarkTitle, bookmarkUrl);
        if (bookmarkTitle != null &&
                bookmarkTitle.compareTo("") == 0) {
            retrieveIconHelper.retrieveTitle(bookmarkUrl);
            return;
        }

        if (savedInstanceState != null) {
            initResultViewOnConfigChanged();
        }
    }

    /**
     * add bookmark on orm db
     */
    public void addBookmark() {
        statusManager.setOnSearchMode();

        RealmUtils.addItemOnRealm(Realm.getDefaultInstance(), bookmarkTitle, null,
                Utils.convertBitmapToByteArray(addBookmarkResultView.getIconBitmap()), bookmarkUrl);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }



    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(getContext());
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
//            case R.id.addBookmarkDoneButtonId:
//                addBookmark();
//                break;
        }
    }

    /**
     *
     */
    private void initResultViewOnConfigChanged() {
        if (statusManager.isOnResultMode()) {
            addBookmarkResultView.initView(bookmarkTitle, bookmarkUrl);
        }
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
        refreshLayout.setRefreshing(true);
        retrieveIconHelper.retrieveIcon(bookmarkUrl);
    }

    /**
     *
     */
    @Override
    public void onRetrieveIconSuccess(final String url) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                addBookmarkResultView.setIconByUrl(url);
            }
        });
    }

    @Override
    public void onRetrieveTitleSuccess(final String title) {
        if (getActivity() == null) {
            return;
        }
        bookmarkTitle = title;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addBookmarkResultView.setTitle(title);
            }
        });
    }

    @Override
    public void onRetrieveIconFailure(final String error) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
                Utils.buildSnackbar(error, getView(), getContext(), true).show();
            }
        });
    }

    @Override
    public boolean handleBackPressed() {
//        if (statusManager.isOnResultMode()) {
////            searchResultPresenter.hideResultView();
//            statusManager.setOnSearchMode();
//            return true;
//        }
        return false;
    }
}

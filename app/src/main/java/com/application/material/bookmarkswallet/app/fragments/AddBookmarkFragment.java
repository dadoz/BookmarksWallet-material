package com.application.material.bookmarkswallet.app.fragments;

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
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.RetrieveIconHelper;
import com.application.material.bookmarkswallet.app.manager.ClipboardManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.AddNewStatusManager;
import com.application.material.bookmarkswallet.app.presenter.SearchBookmarkPresenter;
import com.application.material.bookmarkswallet.app.presenter.SearchResultPresenter;
import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.squareup.picasso.Picasso;

import icepick.Icepick;
import icepick.State;
import io.realm.Realm;

import java.lang.ref.WeakReference;

public class AddBookmarkFragment extends Fragment implements
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        RetrieveIconHelper.OnRetrieveIconInterface, AddBookmarkActivity.OnHandleBackPressed {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private static final String TAG = "AddBookmarkFragment";
    private ActionbarHelper mActionbarHelper;

    @BindView(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.urlEditTextId)
    EditText urlEditText;
    @BindView(R.id.addBookmarkUrlTextInputId)
    TextInputLayout addBookmarkUrlTextInput;
    @BindView(R.id.titleEditTextId)
    EditText addBookmarkTitleEditText;
    @BindView(R.id.addBookmarkHttpsCheckboxId)
    CheckBox addBookmarkHttpsCheckbox;
    @BindView(R.id.toggleNameViewSwitcherId)
    ViewSwitcher toggleNameViewSwitcher;
    @BindView(R.id.addBookmarkTitleTextInputId)
    TextInputLayout addBookmarkTitleTextInput;
    @BindView(R.id.pasteClipboardFabId)
    FloatingActionButton mPasteClipboardFab;
    @BindView(R.id.addBookmarkSearchButtonId)
    View addBookmarkSearchButton;
    @BindView(R.id.addBookmarkWebViewId)
    WebView addBookmarkWebView;
    @BindView(R.id.addBookmarkResultLayoutId)
    View addBookmarkResultLayout;
    @BindView(R.id.addBookmarkMainLayoutId)
    View addBookmarkMainLayout;
    @BindView(R.id.addBookmarkDoneButtonId)
    View addBookmarkDoneButton;
    @BindView(R.id.addBookmarkToggleWebViewButtonId)
    ImageView addBookmarkToggleWebViewButton;
    @BindView(R.id.addBookmarkIconImageId)
    ImageView addBookmarkIconImage;
    @BindView(R.id.titleTextViewId)
    TextView titleTextView;
    @BindView(R.id.urlTextViewId)
    TextView urlTextView;
    @BindView(R.id.addBookmarkRelativeLayoutId)
    View addBookmarkRelativeLayout;
    private View mainView;
    private SearchBookmarkPresenter searchBookmarkPresenter;
    private int MIN_ICON_SIZE = 64;
    private RetrieveIconHelper retrieveIconHelper;
    private SearchResultPresenter searchResultPresenter;
    private AddNewStatusManager statusManager;

    @State
    public String bookmarkUrl;
    @State
    public String bookmarkTitle;
    @State
    public boolean isTitleViewVisible = false;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Icepick.saveInstanceState(this, savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Icepick.restoreInstanceState(this, savedInstance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActionbarHelper = ActionbarHelper.getInstance(new WeakReference<>(context));
        searchBookmarkPresenter = SearchBookmarkPresenter.getInstance();
        retrieveIconHelper = RetrieveIconHelper.getInstance(new WeakReference<RetrieveIconHelper.OnRetrieveIconInterface>(this));
        searchResultPresenter = new SearchResultPresenter(new WeakReference<>(getContext()));
        statusManager = AddNewStatusManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mainView = inflater.inflate(R.layout.fragment_add_bookmark_layout, container, false);
        ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        initActionbar();
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.clipboard_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_terms_and_licences:
                handleTermsAndLicences();
                return true;
            case android.R.id.home:
                if (statusManager.isOnResultMode()) {
                    searchResultPresenter.hideResultView();
                    statusManager.setOnSearchMode();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * handle terms and licences
     */
    private void handleTermsAndLicences() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
        startActivity(browserIntent);
    }

    /**
     *
     * @param savedInstanceState
     */
    private void onInitView(Bundle savedInstanceState) {
        mPasteClipboardFab.setOnClickListener(this);
        addBookmarkSearchButton.setOnClickListener(this);
        addBookmarkDoneButton.setOnClickListener(this);
        initPullToRefresh();
        initToggleButton();
        searchBookmarkPresenter.init(new View[] { urlEditText, mPasteClipboardFab,
                addBookmarkSearchButton, addBookmarkUrlTextInput });
        searchResultPresenter.init(new View[] {addBookmarkMainLayout, addBookmarkResultLayout,
                addBookmarkRelativeLayout});

        urlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                setUrlAndTextResultFromSearch();
                searchAction();
                return true;
            }
        });

        if (savedInstanceState != null) {
            initResultViewOnConfigChanged();
        }

        if (getArguments() != null &&
                getArguments().getString(MainActivity.SHARED_URL_EXTRA_KEY) != null) {
            handleArguments();
        }

    }

    /**
     *
     */
    private void handleArguments() {
        String sharedUrl = getArguments().getString(MainActivity.SHARED_URL_EXTRA_KEY);
        urlEditText.setText(sharedUrl);
    }

    /**
     * pull to refresh init
     */
    private void initPullToRefresh() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.indigo_600,
                R.color.yellow_400);
    }

    /**
     *
     */
    private void initWebView() {
//        String url = "http://www.google.com/bookmarks";
        boolean isHttps = addBookmarkHttpsCheckbox.isChecked();
        String url = Utils.buildUrl(bookmarkUrl, isHttps);
        addBookmarkWebView.loadUrl(url);
        addBookmarkWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    /**
     *
     */
    private void initToggleButton() {
        toggleNameViewSwitcher.setOnClickListener(this);
    }

    /**
     *
     */
    private void toggleTitleVisibility() {
        initSearchTitleView(!isTitleViewVisible);
        resetTitleView();
        setIsTitleViewVisible();
    }

    /**
     *
     */
    private void setIsTitleViewVisible() {
        isTitleViewVisible = addBookmarkTitleTextInput.getVisibility() == View.VISIBLE;
    }

    /**
     * 
     */
    private void resetTitleView() {
        addBookmarkHttpsCheckbox.setChecked(false);
        addBookmarkTitleEditText.setText("");
    }

    /**
     * init action bar
     */
    private void initActionbar() {
        mActionbarHelper.initActionBar();
        mActionbarHelper.updateActionBar(true);
        mActionbarHelper.setElevation(0.0f);
        mActionbarHelper.setTitle("Add new");
    }

    /**
     * add bookmark on orm db
     */
    public void addBookmark() {
        statusManager.setOnSearchMode();
        RealmUtils.addItemOnRealm(Realm.getDefaultInstance(), bookmarkTitle, null,
                Utils.convertBitmapToByteArray(((BitmapDrawable) addBookmarkIconImage
                        .getDrawable()).getBitmap()), bookmarkUrl);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * show error message
     */
    private void showErrorMessage(String message) {
        message = (message == null) ? "Ops! Something went wrong!" : message;
        Snackbar snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_500));
        snackbar.show();
    }

    /**
     * paste clipboard content
     */
    private void pasteClipboard() {
        try {
            String url = ClipboardManager.getInstance(new WeakReference<>(getContext()))
                    .getTextFromClipboard();
            urlEditText.setText(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                Utils.hideKeyboard(getContext());
                addBookmark();
                break;
            case R.id.pasteClipboardFabId:
                pasteClipboard();
                break;
            case R.id.toggleNameViewSwitcherId:
                toggleTitleVisibility();
                break;
            case R.id.addBookmarkSearchButtonId:
                setUrlAndTextResultFromSearch();
                searchAction();
                break;
            case R.id.addBookmarkDoneButtonId:
                addBookmark();
                break;
        }
    }

    /**
     *
     */
    private void setUrlAndTextResultFromSearch() {
        bookmarkUrl = urlEditText.getText().toString();
        bookmarkTitle = addBookmarkTitleEditText.getText().toString();
    }

    /**
     *
     */
    private void searchAction() {
        if (!ConnectionUtils.isConnected(getContext())) {
            showErrorMessage(getString(R.string.no_network_connection));
            return;
        }

        if (!SearchManager.search(bookmarkUrl)) {
            searchBookmarkPresenter.showErrorOnUrlEditText(true);
            showErrorMessage(getString(R.string.no_item_found));
            return;
        }

        onSearchSuccess();
    }

    /**
     *
     */
    private void initResultViewOnConfigChanged() {
        if (statusManager.isOnResultMode()) {
            setBookmarkTitle();
            initWebView();
        }

        initSearchTitleView(isTitleViewVisible);
    }

    /**
     *
     * @param isVisible
     */
    private void initSearchTitleView(boolean isVisible) {
        addBookmarkHttpsCheckbox.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        addBookmarkTitleTextInput.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        toggleNameViewSwitcher.getCurrentView().setVisibility(!isVisible ? View.VISIBLE : View.GONE);
        toggleNameViewSwitcher.getNextView().setVisibility(!isVisible ? View.GONE : View.VISIBLE);
    }

    /**
     *
     */
    private void onSearchSuccess() {
        statusManager.setOnResultMode();
        searchResultPresenter.showResultView();
        Utils.hideKeyboard(getActivity());
        //init result view -- TODO move in callback
        urlTextView.setText(bookmarkUrl);
        setBookmarkTitle();
        retrieveIcon();
        initWebView();
    }

    /**
     *
     */
    private void setBookmarkTitle() {
        if (bookmarkTitle != null) {
            if (bookmarkTitle.compareTo("") == 0) {
                retrieveIconHelper.retrieveTitle(bookmarkUrl);
                return;
            }
            titleTextView.setText(bookmarkTitle);
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
                Picasso.with(getActivity().getApplicationContext())
                        .load(url)
                        .error(R.drawable.ic_bookmark_black_48dp)
                        .resize(0, MIN_ICON_SIZE)
                        .into(addBookmarkIconImage);
            }
        });
    }

    @Override
    public void onRetrieveTitleSuccess(final String title) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bookmarkTitle = title;
                titleTextView.setText(title);
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
                showErrorMessage(error);
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean handleBackPressed() {
        if (statusManager.isOnResultMode()) {
            searchResultPresenter.hideResultView();
            statusManager.setOnSearchMode();
            return true;
        }
        return false;
    }
}

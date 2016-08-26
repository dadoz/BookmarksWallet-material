package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.RetrieveIconHelper;
import com.application.material.bookmarkswallet.app.manager.ClipboardManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.presenter.SearchBookmarkPresenter;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.squareup.picasso.Picasso;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.lang.ref.WeakReference;

public class AddBookmarkFragment extends Fragment implements
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RetrieveIconHelper.OnRetrieveIconInterface {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private static final String TAG = "AddBookmarkFragment";
    private ActionbarSingleton mActionbarSingleton;
    private ProgressDialog mProgressDialog;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    @Bind(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.urlEditTextId)
    EditText urlEditText;
    @Bind(R.id.addBookmarkUrlTextInputId)
    TextInputLayout addBookmarkUrlTextInput;
    @Bind(R.id.titleEditTextId)
    EditText mTitleEditText;
    @Bind(R.id.addBookmarkHttpsCheckboxId)
    CheckBox addBookmarkHttpsCheckbox;
    @Bind(R.id.toggleNameButtonId)
    View toggleNameButton;
    @Bind(R.id.addBookmarkTitleTextInputId)
    TextInputLayout addBookmarkTitleTextInput;
    @Bind(R.id.pasteClipboardFabId)
    FloatingActionButton mPasteClipboardFab;
    @Bind(R.id.addBookmarkSearchButtonId)
    Button addBookmarkSearchButton;
    @Bind(R.id.addBookmarkWebViewId)
    WebView addBookmarkWebView;
    @Bind(R.id.addBookmarkResultLayoutId)
    View addBookmarkResultLayout;
    @Bind(R.id.addBookmarkMainLayoutId)
    View addBookmarkMainLayout;
    @Bind(R.id.addBookmarkDoneButtonId)
    Button addBookmarkDoneButton;
    @Bind(R.id.addBookmarkToggleWebViewButtonId)
    ImageView addBookmarkToggleWebViewButton;
    @Bind(R.id.addBookmarkIconImageId)
    ImageView addBookmarkIconImage;
    @Bind(R.id.titleTextViewId)
    TextView titleTextView;
    @Bind(R.id.urlTextViewId)
    TextView urlTextView;
    private View mainView;
    private String bookmarkUrl;
    private String bookmarkTitle;
    private SearchBookmarkPresenter searchBookmarkPresenter;
    private int MIN_ICON_SIZE = 96;
    private RetrieveIconHelper retrieveIconHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActionbarSingleton = ActionbarSingleton.getInstance((Activity) context);
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance((Activity) context);
        searchBookmarkPresenter = SearchBookmarkPresenter.getInstance();
        retrieveIconHelper = RetrieveIconHelper.getInstance(new WeakReference<RetrieveIconHelper.OnRetrieveIconInterface>(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mainView = inflater.inflate(R.layout.fragment_add_bookmark_layout, container, false);
        ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        initActionbar();
        onInitView();
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.clipboard_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_terms_and_licences:
                handleTermsAndLicences();
                return true;
        }
        //home is handled by default (super)
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
     * pull to refresh init
     */
    private void initPullToRefresh() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.blue_grey_700,
                        R.color.yellow_400);
    }

    /**
     *
     */
    private void onInitView() {
        mPasteClipboardFab.setOnClickListener(this);
        addBookmarkSearchButton.setOnClickListener(this);
        addBookmarkDoneButton.setOnClickListener(this);
        initPullToRefresh();
        initToggleButton();
        searchBookmarkPresenter.init(new View[] { urlEditText, mPasteClipboardFab,
                addBookmarkSearchButton, addBookmarkUrlTextInput });
    }

    /**
     *
     */
    private void initWebView() {
        String url = Utils.buildUrl(bookmarkUrl, true);
        Log.e(TAG, "webview " + url);
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
        toggleNameButton.setOnClickListener(this);
    }

    /**
     *
     */
    private void toggleTitleVisibility() {
        int visibility = addBookmarkTitleTextInput.getVisibility() == View.VISIBLE ?
                View.GONE : View.VISIBLE;
        addBookmarkHttpsCheckbox.setVisibility(visibility);
        addBookmarkTitleTextInput.setVisibility(visibility);
    }

    /**
     * init action bar
     */
    private void initActionbar() {
        mActionbarSingleton.initActionBar();
        mActionbarSingleton.updateActionBar(true); //, getActionbarColor(), getToolbarDrawableColor());
        mActionbarSingleton.setElevation(0.0f);
        mActionbarSingleton.setTitle("Add new");
    }

    /**
     * add bookmark on orm db
     */
    public void addBookmark() {
        mBookmarkActionSingleton.addOrmObject(Realm.getInstance(new RealmConfiguration.Builder(getContext()).build()),
                bookmarkTitle, null, Utils.convertBitmapToByteArray(((BitmapDrawable) addBookmarkIconImage
                        .getDrawable()).getBitmap()), bookmarkUrl);
        addBookmarkCallback();
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
     * exec the job result
     */
    public void addBookmarkCallback() {
        if (getActivity() != null) {
            getActivity().finish();
        }
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
            case R.id.toggleNameButtonId:
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
        bookmarkTitle = mTitleEditText.getText().toString();
    }

    /**
     *
     */
    private void searchAction() {
        if (!SearchManager.search(new WeakReference<>(getActivity().getApplicationContext()), bookmarkUrl)) {
            searchBookmarkPresenter.showErrorOnUrlEditText(true);
            showErrorMessage("Error - not found");
            return;
        }
        onSearchSuccess();
    }

    /**
     *
     */
    private void onSearchSuccess() {
        Utils.hideKeyboard(getActivity());
        addBookmarkResultLayout.setVisibility(View.VISIBLE);
        addBookmarkMainLayout.setVisibility(View.GONE);
        urlTextView.setText(bookmarkUrl);
        toggleBackgroundOnResultMode(true);
        setBookmarkTitle();
        retrieveIcon();
        initWebView();
    }

    /**
     *
     */
    private void setBookmarkTitle() {
        if (bookmarkTitle.compareTo("") == 0) {
            retrieveIconHelper.retrieveTitle(bookmarkUrl);
            return;
        }
        titleTextView.setText(bookmarkTitle);
    }

    /**
     * TODO implement it
     * @param isResult
     */
    private void toggleBackgroundOnResultMode(boolean isResult) {

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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                showErrorMessage(error); //"Ops! Icon not found for this bookmark!"
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }



    /**
     * set color on icon
     */
    private void setFindIconColor() {
        //colorize icon
//        Drawable res = mIconImageView.getDrawable();
//        Utils.setColorFilter(res, getResources().getColor(R.color.blue_grey_900));
//        mIconImageView.setImageDrawable(res);
    }

    /**
     *
     * @return
     */
    public Drawable getToolbarDrawableColor() {
        return ContextCompat.getDrawable(getContext(), R.color.blue_grey_700);
    }

    /**
     *
     * @return
     */
    public int getActionbarColor() {
        return ContextCompat.getColor(getContext(), R.color.blue_grey_800);
    }



    /**
     * validate input (url and/or title)
     * @return
     */
    private boolean validateInput() {
        return !bookmarkUrl.trim().equals("") &&
                Utils.isValidUrl(bookmarkUrl);
    }

    /**
     * dismiss progress dialog
     */

    private void cancelProgressDialog() {
        mProgressDialog.cancel();
    }

    /**
     * init progress dialog
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext(), R.style.CustomLollipopDialogStyle);
        mProgressDialog.setTitle("Saving ...");
        mProgressDialog.setMessage("Waiting for saving bookmark!");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     *
     * @param blobIcon
     */
    public void setBookmarkBlobIcon(byte[] blobIcon) {
//        this.mBookmarkBlobIcon = blobIcon;
    }

    /**
     *
     * @return
     */
    public byte[] getBookmarkBlobIcon() {
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) mIconImageView.getDrawable();
//        return Utils.getBlobFromBitmap(bitmapDrawable.getBitmap());
        return null;
    }

    /**
     * get image from gallery
     */
    private void retrieveIconByGallery() {
        Toast.makeText(getContext(), "feature will come soon!", Toast.LENGTH_LONG)
                .show();
    }

}

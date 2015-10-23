package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;
import com.application.material.bookmarkswallet.app.asyncTask.RetrieveIconAsyncTask;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.cocosw.bottomsheet.BottomSheet;
import io.realm.Realm;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by davide on 06/08/15.
 */
public class AddBookmarkFragment extends Fragment implements View.OnClickListener, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private Activity mAddActivityRef;
    private ActionbarSingleton mActionbarSingleton;
    private ProgressDialog mProgressDialog;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    @Bind(R.id.addBookmarkFabId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.urlEditTextId)
    EditText mUrlEditText;
    @Bind(R.id.titleEditTextId)
    EditText mTitleEditText;
    @Bind(R.id.iconImageViewId)
    View mIconImageView;
    @Bind(R.id.addBookmarkSwipeRefreshLayoutId)
    SwipeRefreshLayout mAddBookmarkSwipeRefreshLayout;
    @Bind(R.id.addInfoBookmarkScrollViewLayoutId)
    View mAddInfoBookmarkScrollViewLayout;
    @Bind(R.id.noBookmarkPreviewLayoutId)
    View mNoBookmarkPreviewLayout;
    private View mView;
    private byte[] mBookmarkBlobIcon = null;
    @Bind(R.id.pasteClipboardFabId)
    FloatingActionButton mPasteClipboardFab;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChangeFragmentWrapperInterface");
        }
        mAddActivityRef = activity;
        mActionbarSingleton = ActionbarSingleton.getInstance(mAddActivityRef);
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(mAddActivityRef);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mView = inflater.inflate(R.layout.add_bookmark_fragment, container, false);
        ButterKnife.bind(this, mView);

        setHasOptionsMenu(true);
        initActionbar();
        onInitView();
        return mView;
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
//            case R.id.clipboardActionId:
//                pasteClipboard();
//                return true;
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
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout
//                .setColorSchemeResources(R.color.blue_grey_700,
//                        R.color.yellow_400);
    }

    /**
     *
     */
    private void onInitView() {
        mAddBookmarkFab.setOnClickListener(this);
        mPasteClipboardFab.setOnClickListener(this);
        mIconImageView.setOnClickListener(this);
        setUrlInputOnTextChangeListener();
        initPullToRefresh();
        setFindIconColor();
    }

    /**
     * TODO move out
     */
    private void setUrlInputOnTextChangeListener() {
        mUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //isEmptyString
                if (s.length() != 0) {
                    //show paste clipboard fab
                    mPasteClipboardFab.hide();
                    mAddBookmarkFab.show();
                    mAddInfoBookmarkScrollViewLayout.setVisibility(View.VISIBLE);
                    mNoBookmarkPreviewLayout.setVisibility(View.GONE);
                    return;
                }
                //show add bookmark button
                mAddInfoBookmarkScrollViewLayout.setVisibility(View.GONE);
                mNoBookmarkPreviewLayout.setVisibility(View.VISIBLE);
                mTitleEditText.setText("");
                mPasteClipboardFab.show();
                mAddBookmarkFab.hide();

            }
        });
    }

    /**
     * set color on icon
     */
    private void setFindIconColor() {
        //colorize icon
        Drawable res = ((ImageView) mIconImageView).getDrawable();
        Utils.setColorFilter(res, getResources().getColor(R.color.blue_grey_900));
        ((ImageView) mIconImageView).setImageDrawable(res);
    }

    /**
     * init action bar
     */
    private void initActionbar() {
        mActionbarSingleton.initActionBar();
        mActionbarSingleton.udpateActionbar(true, getActionbarColor(), getToolbarDrawableColor());
        mActionbarSingleton.setElevation(0.0f);
        mActionbarSingleton.setTitle("Add new");
    }

    /**
     *
     * @return
     */
    public Drawable getToolbarDrawableColor() {
        return getResources().getDrawable(R.color.blue_grey_700);
    }

    /**
     *
     * @return
     */
    public int getActionbarColor() {
        return getResources().getColor(R.color.blue_grey_800);
    }

    /**
     * //TODO let's do it async :)
     * add bookmark on orm db
     */
    public void addBookmark() {
        if (! validateInput()) {
            showErrorMessage();
            return;
        }

        initProgressDialog();
        //do job
        mBookmarkActionSingleton.addOrmObject(
                Realm.getInstance(mAddActivityRef),
                getBookmarkTitle(),
                null,
                getBookmarkBlobIcon(),
                getBookmarkUrl());
        //post result
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addBookmarkCallback();
            }
        }, 3000);
    }

    /**
     * show error message
     */
    private void showErrorMessage() {
        Snackbar snackbar = Snackbar.make(mView, "Ops! Something went wrong!", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(getResources().getColor(R.color.red_500));
        snackbar.show();
    }

    /**
     * validate input (url and/or title)
     * @return
     */
    private boolean validateInput() {
        return ! getBookmarkUrl().trim().equals("") &&
                Utils.isValidUrl(getBookmarkUrl());
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
        mProgressDialog = new ProgressDialog(mAddActivityRef, R.style.CustomLollipopDialogStyle);
        mProgressDialog.setTitle("Saving ...");
        mProgressDialog.setMessage("Waiting for saving bookmark!");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * exec the job result
     */
    public void addBookmarkCallback() {
        try {
            cancelProgressDialog();
            mAddActivityRef.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public String getBookmarkTitle() {
        //TODO implement if title == not_set -> retrieve it from jsoup
        return mTitleEditText.getText().toString();
    }

    /**
     *
     * @return
     */
    public String getBookmarkUrl() {
        return Utils.buildUrl(mUrlEditText.getText().toString());
    }

    /**
     *
     * @param blobIcon
     */
    public void setBookmarkBlobIcon(byte[] blobIcon) {
        this.mBookmarkBlobIcon = blobIcon;
    }

    /**
     *
     * @return
     */
    public byte[] getBookmarkBlobIcon() {
        //TODO to be implemented
        return mBookmarkBlobIcon;
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
        new BottomSheet.Builder(mAddActivityRef)
                .sheet(R.menu.gallery_url_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.urlActionId:
                                retrieveIconByUrl(getBookmarkUrl());
                                break;
                            case R.id.galleryActionId:
                                retrieveIconByGallery();
                                break;
                        }
                    }
                })
                .grid()
                .show();
    }

    /**
     * by url - auto detect icon
     */
    private void retrieveIconByUrl(String url) {
        try {
            mAddBookmarkSwipeRefreshLayout.setRefreshing(true);
            new RetrieveIconAsyncTask(this).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get image from gallery
     */
    private void retrieveIconByGallery() {
        Toast.makeText(mAddActivityRef, "feature will come soon!", Toast.LENGTH_LONG)
                .show();
    }

    /**
     * paste clipboard content
     */
    private void pasteClipboard() {
        try {
            ClipboardSingleton clipboardSingleton =
                    ClipboardSingleton.getInstance(mAddActivityRef);
            String url = clipboardSingleton.getTextFromClipboard();
            mUrlEditText.setText(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                Utils.hideKeyboard(mAddActivityRef);
                addBookmark();
                break;
            case R.id.iconImageViewId:
                retrieveIcon();
                break;
            case R.id.pasteClipboardFabId:
                pasteClipboard();
                break;
        }
    }


    @Override
    public void onTaskCompleted(boolean isRefreshEnabled) {
        return;
    }

    @Override
    public void onTaskCompleted(byte[] data) {
        mAddBookmarkSwipeRefreshLayout.setRefreshing(false);
        if (data == null) {
            showErrorMessage();
            return;
        }

        setBookmarkBlobIcon(data);
        setIconOnUi();
    }

    /**
     *
     */
    private void setIconOnUi() {
        try {
            Bitmap icon = BitmapFactory.decodeByteArray(mBookmarkBlobIcon, 0, mBookmarkBlobIcon.length);
            ((ImageView) mIconImageView)
                    .setImageBitmap(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        mAddBookmarkSwipeRefreshLayout.setRefreshing(false);
    }
}

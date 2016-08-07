package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.presenter.AddBookmarkUrlEditTextPresenter;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;
import com.application.material.bookmarkswallet.app.asyncTask.RetrieveIconAsyncTask;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by davide on 06/08/15.
 */
public class AddBookmarkFragment extends Fragment implements
        View.OnClickListener, Callback, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private Activity mAddActivityRef;
    private ActionbarSingleton mActionbarSingleton;
    private ProgressDialog mProgressDialog;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    @Bind(R.id.addBookmarkFabId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.urlEditTextId)
    EditText mUrlEditText;
    @Bind(R.id.titleEditTextId)
    EditText mTitleEditText;
    @Bind(R.id.addIconImageFabId)
    FloatingActionButton addIconImageFab;
    @Bind(R.id.iconImageViewId)
    ImageView mIconImageView;
    private View mView;
    private byte[] mBookmarkBlobIcon = null;
    @Bind(R.id.pasteClipboardFabId)
    FloatingActionButton mPasteClipboardFab;
    private long SAVE_TIMEOUT = 500;

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
        refreshLayout
                .setColorSchemeResources(R.color.blue_grey_700,
                        R.color.yellow_400);

    }

    /**
     *
     */
    private void onInitView() {
        mAddBookmarkFab.setOnClickListener(this);
        mPasteClipboardFab.setOnClickListener(this);
        addIconImageFab.setOnClickListener(this);
        initPullToRefresh();
        setFindIconColor();
        AddBookmarkUrlEditTextPresenter.init(new WeakReference<>(mUrlEditText),
                new WeakReference<>(mTitleEditText),
                AnimatorBuilder.getInstance(new WeakReference<>(getContext())),
                mView);
    }

    /**
     * set color on icon
     */
    private void setFindIconColor() {
        //colorize icon
        Drawable res = mIconImageView.getDrawable();
        Utils.setColorFilter(res, getResources().getColor(R.color.blue_grey_900));
        mIconImageView.setImageDrawable(res);
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
        if (!validateInput()) {
            showErrorMessage(null);
            return;
        }

        initProgressDialog();
        //do job
        mBookmarkActionSingleton.addOrmObject(
                Realm.getInstance(new RealmConfiguration.Builder(mAddActivityRef).build()),
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
        }, SAVE_TIMEOUT);
    }

    /**
     * show error message
     */
    private void showErrorMessage(String message) {
        message = (message == null) ? "Ops! Something went wrong!" : message;
        Snackbar snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG);
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mIconImageView.getDrawable();
        return Utils.getBlobFromBitmap(bitmapDrawable.getBitmap());
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
        refreshLayout.setRefreshing(true);
        Utils.hideKeyboard(getActivity());
        retrieveIconByUrl(getBookmarkUrl());
    }

    /**
     * by url - auto detect icon
     * @param bookmarkUrl
     */
    private void retrieveIconByUrl(String bookmarkUrl) {
        try {
            new RetrieveIconAsyncTask(new WeakReference<OnTaskCompleted>(this))
                    .execute(new URL(bookmarkUrl));
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
            case R.id.addIconImageFabId:
                if (!refreshLayout.isRefreshing()) {
                    retrieveIcon();
                }
                break;
            case R.id.pasteClipboardFabId:
                pasteClipboard();
                break;
        }
    }

    @Override
    public void onTaskCompleted(boolean isRefreshEnabled) {

    }

    @Override
    public void onTaskCompleted(byte[] data) {

    }

    @Override
    public void onTaskCompleted(String url) {
        if (url == null) {
            onError();
            return;
        }
        Picasso.with(getActivity().getApplicationContext())
                .load(url)
//                .placeholder(R.drawable.dot_loader)
                .error(R.drawable.ic_bookmark_outline_black_48dp)
                .into(mIconImageView, this);
    }

    @Override
    public void onSuccess() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onError() {
        //picasso error
        refreshLayout.setRefreshing(false);
        mIconImageView.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_bookmark_outline_black_48dp));
        showErrorMessage("Ops! Icon not found for this bookmark!");
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }
}

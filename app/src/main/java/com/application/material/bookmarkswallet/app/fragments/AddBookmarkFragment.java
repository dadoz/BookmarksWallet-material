package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;
import com.application.material.bookmarkswallet.app.singleton.RetrieveIconAsyncTask;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.cocosw.bottomsheet.BottomSheet;
import io.realm.Realm;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by davide on 06/08/15.
 */
public class AddBookmarkFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
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
    @Bind(R.id.pasteClipboardButtonId)
    Button mPasteClipboardButton;
    private View mView;
    private byte[] mBookmarkBlobIcon;

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

        onInitView();
        return mView;
    }

    /**
     *
     */
    private void onInitView() {
        initStatusbar();
        mAddBookmarkFab.setOnClickListener(this);
        mPasteClipboardButton.setOnClickListener(this);
        mIconImageView.setOnClickListener(this);
    }

    /**
     * init title - set
     */
    private void initStatusbar() {
        mActionbarSingleton.setTitle("Add new", mAddActivityRef.getResources().getColor(R.color.blue_grey_900));
        mActionbarSingleton.udpateActionbar(false, getActionbarColor(), getToolbarDrawableColor());
    }

    /**
     *
     * @return
     */
    public Drawable getToolbarDrawableColor() {
        return mAddActivityRef
                .getResources().getDrawable(R.color.blue_grey_700);
    }

    /**
     *
     * @return
     */
    public int getActionbarColor() {
        return mAddActivityRef.getResources()
                .getColor(R.color.blue_grey_800);
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
        Snackbar snackbar = Snackbar.make(mView, "error message", Snackbar.LENGTH_LONG);
        snackbar.getView()
                .setBackgroundColor(mAddActivityRef.getResources().getColor(R.color.red_500));
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
        mProgressDialog = new ProgressDialog(mAddActivityRef);
        mProgressDialog.setTitle("Saving ...");
        mProgressDialog.setMessage("Fetching data for selected bookmark.");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * exec the job result
     */
    public void addBookmarkCallback() {
        try {
            cancelProgressDialog();
            //show result
//            mAddActivityRef.finish();
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
        return mUrlEditText.getText().toString();
    }

    /**
     *
     * @param mBookmarkBlobIcon
     */
    public void setBookmarkBlobIcon(byte[] mBookmarkBlobIcon) {
        this.mBookmarkBlobIcon = mBookmarkBlobIcon;
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
        //TODO implement
    }

    /**
     * by url - auto detect icon
     */
    private void retrieveIconByUrl(String url) {
        try {
            url = "http://" + url;
            initProgressDialog();
            new RetrieveIconAsyncTask(this).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get image from gallery
     */
    private void retrieveIconByGallery() {
        Toast.makeText(mAddActivityRef, "feature will be added soon!", Toast.LENGTH_LONG)
                .show();
    }

    /**
     * paste clipboard content
     */
    private void pasteClipboard() {
        ClipboardSingleton clipboardSingleton =
                ClipboardSingleton.getInstance(mAddActivityRef);
        String url = clipboardSingleton.getTextFromClipboard();
        mUrlEditText.setText(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                Utils.hideKeyboard(mAddActivityRef);
                addBookmark();
                break;
            case R.id.pasteClipboardButtonId:
                pasteClipboard();
                break;
            case R.id.iconImageViewId:
                retrieveIcon();
                break;
        }
    }


    @Override
    public void onTaskCompleted(boolean isRefreshEnabled) {
        return;
    }

    @Override
    public void onTaskCompleted(byte[] data) {
        cancelProgressDialog();
        if (data == null) {
            showErrorMessage();
            return;
        }

        setBookmarkBlobIcon(data);
        setIconOnUi();
    }

    private void setIconOnUi() {
        try {
            Bitmap icon = BitmapFactory.decodeByteArray(mBookmarkBlobIcon, 0, mBookmarkBlobIcon.length);
            ((ImageView) ((ViewGroup) mIconImageView).getChildAt(0))
                    .setImageBitmap(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

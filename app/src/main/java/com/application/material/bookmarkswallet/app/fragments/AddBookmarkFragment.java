package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.Realm;


/**
 * Created by davide on 06/08/15.
 */
public class AddBookmarkFragment extends Fragment implements View.OnClickListener {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private Activity mAddActivityRef;
    private ActionbarSingleton mActionbarSingleton;
    private ProgressDialog mProgressDialog;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    @Bind(R.id.addBookmarkFabId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.urlEditTextId)
    EditText urlEditText;
    @Bind(R.id.titleEditTextId)
    EditText titleEditText;
    @Bind(R.id.iconImageViewId)
    View mIconImageView;
    @Bind(R.id.pasteClipboardButtonId)
    Button mPasteClipboardButton;
    private View mView;

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
        mActionbarSingleton.setTitle("Add new", mAddActivityRef.getResources().getColor(R.color.grey_400));
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
                Utils.validateUrl(getBookmarkUrl());
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
        return titleEditText.getText().toString();
    }

    /**
     *
     * @return
     */
    public String getBookmarkUrl() {
        return urlEditText.getText().toString();
    }

    /**
     *
     * @return
     */
    public byte[] getBookmarkBlobIcon() {
        //TODO to be implemented
        return null;
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
        //TODO implement
    }

    /**
     * paste clipboard content
     */
    private void pasteClipboard() {
        Toast.makeText(mAddActivityRef, "clipboard", Toast.LENGTH_SHORT).show();
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


}

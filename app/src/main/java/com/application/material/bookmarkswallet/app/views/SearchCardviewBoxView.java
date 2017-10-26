package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.manager.ClipboardManager;
import com.lib.davidelm.filetreevisitorlibrary.views.FolderNodeView;

import java.lang.ref.WeakReference;

import butterknife.OnTextChanged;
import icepick.State;

/**
 * Created by davide on 05/06/2017.
 */

public class SearchCardviewBoxView extends FrameLayout implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {
    private ViewSwitcher toggleNameViewSwitcher;
    private EditText urlEditText;
    private TextInputLayout addBookmarkUrlTextInput;
    private EditText addBookmarkTitleEditText;
    private CheckBox addBookmarkHttpsCheckbox;
    private TextInputLayout addBookmarkTitleTextInput;
    private Button pasteClipboardFab;
    @State
    public boolean isTitleViewVisible = false;
    private WeakReference<OnTextChangedCb> lst;

    public SearchCardviewBoxView(Context context) {
        super(context);
        initView();
    }

    public SearchCardviewBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchCardviewBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     *
     */
    private void initView() {
        inflate(getContext(), R.layout.add_bookmark_search_cardview_view, this);
        toggleNameViewSwitcher = (ViewSwitcher) findViewById(R.id.toggleNameViewSwitcherId);
        urlEditText = (EditText) findViewById(R.id.urlEditTextId);
        addBookmarkUrlTextInput = (TextInputLayout) findViewById(R.id.addBookmarkUrlTextInputId);
        addBookmarkTitleEditText = (EditText) findViewById(R.id.titleEditTextId);
        addBookmarkHttpsCheckbox = (CheckBox) findViewById(R.id.addBookmarkHttpsCheckboxId);
        addBookmarkTitleTextInput = (TextInputLayout) findViewById(R.id.addBookmarkTitleTextInputId);
        pasteClipboardFab = (Button) findViewById(R.id.pasteClipboardFabId);

        toggleNameViewSwitcher.setOnClickListener(this);
        urlEditText.addTextChangedListener(this);
        urlEditText.setOnEditorActionListener(this);
        pasteClipboardFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggleNameViewSwitcherId:
                toggleTitleVisibility();
                break;
            case R.id.pasteClipboardFabId:
                String url = ClipboardManager.getInstance(new WeakReference<>(getContext()))
                        .getTextFromClipboard();
                setUrl(url);
                break;
        }

    }

    /**
     * toggle title
     */
    private void toggleTitleVisibility() {
        //set titleView visible status
        isTitleViewVisible = addBookmarkTitleTextInput.getVisibility() == View.VISIBLE;
        initSearchTitleView(!isTitleViewVisible);
    }

    /**
     *
     * @param isVisible
     */
    public void initSearchTitleView(boolean isVisible) {
        addBookmarkHttpsCheckbox.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        addBookmarkTitleTextInput.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        toggleNameViewSwitcher.getCurrentView().setVisibility(!isVisible ? View.VISIBLE : View.GONE);
        toggleNameViewSwitcher.getNextView().setVisibility(!isVisible ? View.GONE : View.VISIBLE);
        addBookmarkHttpsCheckbox.setChecked(false);
        addBookmarkTitleEditText.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        showErrorOnUrlEditText(false);
        boolean isQueryEmpty = charSequence.length() == 0;
        pasteClipboardFab.setVisibility(isQueryEmpty ? View.VISIBLE : View.GONE);

        //set cb
        if (lst != null &&
                lst.get() != null)
            lst.get().onTextChangedCb(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    /**
     *
     * @param showError
     */
    public void showErrorOnUrlEditText(boolean showError) {
        addBookmarkUrlTextInput.setError(!showError ? null : "wrong error");
    }

    public boolean isHttpsChecked() {
        return addBookmarkHttpsCheckbox.isChecked();
    }

    public String getUrl() {
        return urlEditText.getText().toString();
    }

    public void setListenerCb(WeakReference<OnTextChangedCb> lst) {
        this.lst = lst;
    }

    public String getTitle() {
        return addBookmarkTitleEditText.getText().toString();
    }

    public void setUrl(String url) {
        urlEditText.setText(url);
    }

    public interface OnTextChangedCb {
        void onTextChangedCb(CharSequence charSequence);
        void onEditorActionCb(TextView textView);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (lst != null &&
                lst.get() != null)
            lst.get().onEditorActionCb(textView);
        return true;
    }

}

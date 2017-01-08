package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.application.material.bookmarkswallet.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.State;

public class AddBookmarkSearchLayout extends RelativeLayout implements View.OnClickListener, TextWatcher {

    @State
    public boolean isTitleViewVisible = false;
    private View addBookmarkSearchButton;
    private EditText urlEditText;
    private TextInputLayout addBookmarkUrlTextInput;
    private EditText addBookmarkTitleEditText;
    private CheckBox addBookmarkHttpsCheckbox;
    private ViewSwitcher toggleNameViewSwitcher;
    private TextInputLayout addBookmarkTitleTextInput;


    public AddBookmarkSearchLayout(Context context) {
        super(context);
        initializeViews(context);
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    /**
     * initialize view
     * @param context
     */
    private void initializeViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_bookmark_search_layout, this);
        urlEditText = (EditText) view.findViewById(R.id.urlEditTextId);
        addBookmarkUrlTextInput = (TextInputLayout) view.findViewById(R.id.addBookmarkUrlTextInputId);
        addBookmarkTitleEditText = (EditText) view.findViewById(R.id.titleEditTextId);
        addBookmarkHttpsCheckbox = (CheckBox) view.findViewById(R.id.addBookmarkHttpsCheckboxId);
        toggleNameViewSwitcher = (ViewSwitcher) view.findViewById(R.id.toggleNameViewSwitcherId);
        addBookmarkTitleTextInput = (TextInputLayout) view.findViewById(R.id.addBookmarkTitleTextInputId);
        addBookmarkSearchButton = view.findViewById(R.id.addBookmarkSearchButtonId);

    }

    /**
     *
     * @param savedInstanceState
     */
    public void initView(Bundle savedInstanceState) {
//        searchBookmarkHelper.init(new View[] { urlEditText, pasteClipboardFab,
//                addBookmarkSearchButton, addBookmarkUrlTextInput });
        addBookmarkSearchButton.setOnClickListener(this);
        toggleNameViewSwitcher.setOnClickListener(this);
        urlEditText.addTextChangedListener(this);
    }


    /**
     * get search params
     */
    public SparseArray<String> getSearchParamsArray() {
        SparseArray<String> searchParamsArray = new SparseArray<>();
        searchParamsArray.put(0, urlEditText.getText().toString());
        searchParamsArray.put(1, addBookmarkTitleEditText.getText().toString());
        return searchParamsArray;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggleNameViewSwitcherId:
                toggleTitleVisibility();
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
//        clipboardFabButton.setVisibility(isQueryEmpty ? View.VISIBLE : View.GONE);
        addBookmarkSearchButton.setVisibility(isQueryEmpty ? View.GONE : View.VISIBLE);
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

    public EditText getUrlEditTextView() {
        return urlEditText;
    }
}

package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class AddBookmarkSearchLayout extends RelativeLayout implements SearchCardviewBoxView.OnTextChangedCb {

    private View addBookmarkSearchButton;
    private SearchCardviewBoxView searchCardviewBox;
    private WeakReference<OnEditorActionListenerCallbacks> onEditorActionLst;


    public AddBookmarkSearchLayout(Context context) {
        super(context);
        initView();
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * initialize view
     */
    private void initView() {
        inflate(getContext(), R.layout.add_bookmark_search_layout, this);
        addBookmarkSearchButton = findViewById(R.id.addBookmarkSearchButtonId);
        searchCardviewBox = (SearchCardviewBoxView) findViewById(R.id.addBookmarkSearchCardviewBoxId);
        searchCardviewBox.setListenerCb(new WeakReference<>(this));
    }

    /**
     * get search params
     * @param folderNodeId
     */
    public SparseArrayParcelable<String> getSearchParamsArray(int folderNodeId) {
        SparseArrayParcelable<String> searchParamsArray = new SparseArrayParcelable<>();
        searchParamsArray.put(0, Utils.buildUrl(searchCardviewBox.getUrl(),
                searchCardviewBox.isHttpsChecked()));
        searchParamsArray.put(1, searchCardviewBox.getTitle().equals("") ?
                getContext().getString(R.string.no_title) : searchCardviewBox.getTitle());
        searchParamsArray.put(2, null);//pos 2 empty to set icon url
        searchParamsArray.put(3, Integer.toString(folderNodeId)); //folder nodeId
        return searchParamsArray;
    }


    @Override
    public void onTextChangedCb(CharSequence charSequence) {
        addBookmarkSearchButton.setVisibility(charSequence.length() == 0 ? GONE : VISIBLE);
    }

    @Override
    public void onEditorActionCb(TextView textView) {
        if (onEditorActionLst != null &&
                onEditorActionLst.get() != null)
            onEditorActionLst.get().onEditorActionCb(textView);
    }

    public void setUrl(String url) {
        searchCardviewBox.setUrl(url);
    }

    public void showErrorOnUrlEditText(boolean b) {
        searchCardviewBox.showErrorOnUrlEditText(b);
    }

    public void setOnEditorActionLst(OnEditorActionListenerCallbacks onEditorActionLst) {
        this.onEditorActionLst = new WeakReference<>(onEditorActionLst);
    }

    public interface OnEditorActionListenerCallbacks {
        void onEditorActionCb(TextView textView);
    }
}

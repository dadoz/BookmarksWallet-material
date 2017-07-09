package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.lib.davidelm.filetreevisitorlibrary.views.FolderCardviewView;

import java.lang.ref.WeakReference;

public class AddBookmarkSearchLayout extends RelativeLayout implements SearchCardviewBoxView.OnTextChangedCb {

    private View addBookmarkSearchButton;
    private SearchCardviewBoxView searchCardviewBox;
    private FolderCardviewView addBookmarkFolderCardview;
    private WeakReference<OnEditorActionListenerCallbacks> onEditorActionLst;


    public AddBookmarkSearchLayout(Context context) {
        super(context);
        initView(context);
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddBookmarkSearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * initialize view
     * @param context
     */
    private void initView(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_bookmark_search_layout, this);
        addBookmarkSearchButton = view.findViewById(R.id.addBookmarkSearchButtonId);
        searchCardviewBox = (SearchCardviewBoxView) view.findViewById(R.id.addBookmarkSearchCardviewBoxId);
        addBookmarkFolderCardview = (FolderCardviewView) view.findViewById(R.id.addBookmarkFolderCardviewId);
        searchCardviewBox.setListenerCb(new WeakReference<>(this));

        addBookmarkFolderCardview.init();
    }

    /**
     * get search params
     */
    public SparseArrayParcelable<String> getSearchParamsArray() {
        SparseArrayParcelable<String> searchParamsArray = new SparseArrayParcelable<>();
        searchParamsArray.put(0, Utils.buildUrl(searchCardviewBox.getUrl(),
                searchCardviewBox.isHttpsChecked()));
        searchParamsArray.put(1, searchCardviewBox.getTitle().equals("") ?
                getContext().getString(R.string.no_title) : searchCardviewBox.getTitle());
        searchParamsArray.put(2, null);//pos 2 empty to set icon url
        searchParamsArray.put(3, Integer.toString(addBookmarkFolderCardview.getFolderListView()
                .getSelectedNodeId()));
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

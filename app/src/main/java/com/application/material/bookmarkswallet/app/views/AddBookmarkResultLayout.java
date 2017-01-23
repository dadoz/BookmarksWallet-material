package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.application.material.bookmarkswallet.app.R;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;

import butterknife.ButterKnife;
import icepick.State;

public class AddBookmarkResultLayout extends RelativeLayout {
    private static final int MIN_ICON_SIZE = 10;
    private TextView urlTextView;
    private TextView titleTextView;
    private ImageView addBookmarkIconImage;
    private WebView addBookmarkWebView;
//    private String title;
//    private String url;

    public AddBookmarkResultLayout(Context context) {
        super(context);
        initializeViews();
    }

    public AddBookmarkResultLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews();
    }

    public AddBookmarkResultLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews();
    }

    /**
     * initialize view
     */
    private void initializeViews() {
        View view = inflate(getContext(), R.layout.add_bookmark_result_layout, this);
        urlTextView = (TextView) view.findViewById(R.id.urlTextViewId);
        titleTextView = (TextView) view.findViewById(R.id.titleTextViewId);
        addBookmarkIconImage = (ImageView) view.findViewById(R.id.addBookmarkIconImageId);
        addBookmarkWebView = (WebView) view.findViewById(R.id.addBookmarkWebViewId);

    }

    /**
     *
     */
    public void initWebView(String bookmarkUrl) {
//        String url = "http://www.google.com/bookmarks";
//        boolean isHttps = addBookmarkHttpsCheckbox.isChecked();
//        String url = Utils.buildUrl(bookmarkUrl, isHttps);
        addBookmarkWebView.loadUrl(bookmarkUrl);
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
    public void setBookmarkTitle(String bookmarkTitle) {
        if (bookmarkTitle != null) {
            titleTextView.setText(bookmarkTitle);
        }
    }


    public Bitmap getIconBitmap() {
        return ((BitmapDrawable) addBookmarkIconImage
                .getDrawable()).getBitmap();
    }

    public void setTitle(String title) {
//        this.title = title;
        titleTextView.setText(title);

    }

    public void setUrl(String url) {
//        this.url = url;
        urlTextView.setText(url);
    }

    public void setIconByUrl(String url) {
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.ic_bookmark_black_48dp)
//                .resize(0, MIN_ICON_SIZE)
                .into(addBookmarkIconImage);

    }

    public void initView(SparseArray<String> searchParamsArray) {
        String url = searchParamsArray.get(0);
        String title = searchParamsArray.get(1);
        initWebView(url);
        setBookmarkTitle(title);
        setUrl(url);
    }
}

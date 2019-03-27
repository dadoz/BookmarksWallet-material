package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.squareup.picasso.Picasso;

public class AddBookmarkResultLayout extends RelativeLayout {
    private TextView urlTextView;
    private TextView titleTextView;
    private ImageView addBookmarkIconImage;
    private WebView addBookmarkWebView;

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
        urlTextView =  view.findViewById(R.id.urlTextViewId);
        titleTextView = view.findViewById(R.id.titleTextViewId);
        addBookmarkIconImage = view.findViewById(R.id.addBookmarkIconImageId);
        addBookmarkWebView = view.findViewById(R.id.addBookmarkWebViewId);
    }

    /**
     *
     */
    public void initWebView(String bookmarkUrl) {
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
        titleTextView.setText(title);

    }

    public void setUrl(String url) {
        urlTextView.setText(url);
    }

    public void setIconByUrl(String url) {
        Log.e(getClass().getName(), "-------->" + url);
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.ic_bookmark)
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

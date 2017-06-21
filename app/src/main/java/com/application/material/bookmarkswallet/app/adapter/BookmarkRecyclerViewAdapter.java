package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;

import java.lang.ref.WeakReference;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;

public class BookmarkRecyclerViewAdapter extends MultipleSelectorHelperAdapter {
    private final WeakReference<Context> context;
    private final Bitmap defaultIcon;
    private boolean isFaviconNotEnabled;

    /**
     *
     * @param ctx
     */
    public BookmarkRecyclerViewAdapter(Context ctx, OnMultipleSelectorCallback lst) {
        super(lst);
        context = new WeakReference<> (ctx);
        setIsFaviconIsEnabled(context);
        defaultIcon = BitmapFactory.decodeResource(context.get().getResources(),
                R.drawable.ic_bookmark_black_48dp);
    }


    /**
     *
     * @param ctx
     */
    public void setIsFaviconIsEnabled(WeakReference<Context> ctx) {
        isFaviconNotEnabled = (boolean) SharedPrefHelper.getInstance(ctx).getValue(NO_FAVICON_MODE, false);
    }
}

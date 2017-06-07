package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.viewholder.BookmarkViewHolder;
import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;


import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;
import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

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

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
    private final WeakReference<OnActionListenerInterface> listener;
    private final Bitmap defaultIcon;
    private boolean isFaviconNotEnabled;

    /**
     *
     * @param ctx
     * @param lst
     */
    public BookmarkRecyclerViewAdapter(WeakReference<Context> ctx,
                                       WeakReference<OnActionListenerInterface> lst) {
        super();
        context = ctx;
        listener = lst;
        setIsFaviconIsEnabled(ctx);
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



//    @Override
//    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
//        return new BookmarkViewHolder(view, listener);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
//        final BookmarkViewHolder holder = (BookmarkViewHolder) rvh;
//        final Bookmark bookmark = getItem(position);
//
//        holder.labelView.setText(getBookmarkNameWrapper(bookmark.getName()));
//        holder.urlView.setText(bookmark.getUrl());
//        holder.timestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
//                .getTimestamp()));
//        holder.selectItem(isSelectedPos(position)); //TODO handle night mode
//        if (bookmark.getIconPath() != null) {
//            holder.setIcon(bookmark.getIconPath(), defaultIcon, isSelectedPos(position),
//                    isFaviconNotEnabled);
//            return;
//        }
//        holder.setIcon(Utils.getIconBitmap(bookmark.getBlobIcon(), (int) context.get().getResources().getDimension(R.dimen.medium_icon_size)), defaultIcon,
//                isSelectedPos(position), isFaviconNotEnabled);
//    }

    /**
     *
     * @param position
     * @return
     */
    public boolean isSelectedPos(int position) {
        return super.isSelectedPos(position);
    }

    /**
     *
     */
    public interface OnActionListenerInterface {
        boolean onLongItemClick(View view, int position);
        void onItemClick(View view, int position);
    }
}

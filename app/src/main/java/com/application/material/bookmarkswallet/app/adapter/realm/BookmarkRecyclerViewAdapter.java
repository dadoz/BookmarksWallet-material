package com.application.material.bookmarkswallet.app.adapter.realm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;
import io.realm.RealmObject;

import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

/**
 * Created by davide on 21/04/15.
 */
public class BookmarkRecyclerViewAdapter<T extends RealmObject> extends
        RealmRecyclerViewAdapter<Bookmark> {
    private final Activity mActivityRef;
    private final Object mListener;
    private final StatusSingleton mStatusSingleton;
    private final int mDarkGrey;
    private final int mLightGrey;

    public BookmarkRecyclerViewAdapter(Activity activity, Object listener) {
        mActivityRef = activity;
        mListener = listener;
        mStatusSingleton = StatusSingleton.getInstance();
        mDarkGrey = mActivityRef.getResources().getColor(R.color.yellow_100);
        mLightGrey = Color.TRANSPARENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
        final ViewHolder holder = (ViewHolder) rvh;
        final Bookmark bookmark = (Bookmark) getItem(position);


        holder.mLabelView.setText(getBookmarkNameWrapper(bookmark.getName()));
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                .getTimestamp()));

        setItemListeners(holder);

        //ICON why this
        setIcon(holder.mIconView, null, false);
//        setIcon(holder.mIconOpenedView, bookmark, false);
        setEditItem(holder.mLayoutView);
    }

    private void setEditItem(View v) {
        v.setBackgroundColor(mStatusSingleton.isEditMode() ? mDarkGrey
                : mLightGrey);
    }

    @Override
    public int getItemCount() {
        return getRealmBaseAdapter() == null ? 0 : getRealmBaseAdapter().getCount();
    }

    /**
     *
     * @param holder
     */
    private void setItemListeners(ViewHolder holder) {
        holder.itemView.setEnabled(true);
        holder.itemView.setOnClickListener((View.OnClickListener) mListener);
        holder.itemView.setOnLongClickListener((View.OnLongClickListener) mListener);
    }

    /**
     *
     * @param iconView
     * @param bookmark
     * @param isSelectedItem
     */
    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        Drawable res = mActivityRef
                .getResources()
                .getDrawable(isSelectedItem ?
                        R.drawable.ic_bookmark_black_48dp :
                        R.drawable.ic_bookmark_outline_black_48dp);
        setColorFilter(res, R.color.blue_grey_700);
        iconView.setImageDrawable(res);

        if (bookmark != null) {
            Bitmap bitmapIcon = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());
            if (bitmapIcon != null &&
                    !isSelectedItem) {
                iconView.setImageBitmap(bitmapIcon);
            }
        }
    }

    /**
     *
     * @param drawable
     * @param color
     */
    public void setColorFilter(Drawable drawable, int color) {
        drawable.setColorFilter(mActivityRef.getResources()
                        .getColor(color),
                PorterDuff.Mode.SRC_IN);
    }

    /**
     * ViewHolder def
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayoutView;
        public ImageView mIconOpenedView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;

        public ViewHolder(View v) {
            super(v);
            mLayoutView = v.findViewById(R.id.backgroundLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mIconOpenedView = (ImageView) v.findViewById(R.id.linkIconOpenedIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
        }
    }

}

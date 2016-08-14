package com.application.material.bookmarkswallet.app.adapter.realm;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.RealmObject;
import io.realm.internal.Context;

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
        mDarkGrey = ContextCompat.getColor(mActivityRef.getApplicationContext(), R.color.indigo_50);
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

        boolean isEditMode = getEditModeStatus(position);
        setIcon(holder.mIconView, bookmark, isEditMode);
        setEditItem(holder.mLayoutView, isEditMode);
    }

    /**
     *
     * @param pos
     * @return
     */
    private boolean getEditModeStatus(int pos) {
        return mStatusSingleton.isEditMode() &&
                mStatusSingleton.getEditItemPos() == pos;
    }

    /**
     *
     * @param v
     * @param isEdit
     */
    private void setEditItem(View v, boolean isEdit) {
        v.setBackgroundColor(isEdit ? mDarkGrey : mLightGrey);
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
     * TODO refactor
     * @param iconView
     * @param bookmark
     * @param isSelectedItem
     */
    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        Drawable res = ContextCompat.getDrawable(mActivityRef.getApplicationContext(),
                R.drawable.ic_bookmark_black_48dp);
//                isSelectedItem ? R.drawable.ic_bookmark_black_48dp :
//                        R.drawable.ic_bookmark_outline_black_48dp);

        Utils.setColorFilter(res, ContextCompat.getColor(mActivityRef.getApplicationContext(),
                R.color.indigo_600));
//        iconView.setImageDrawable(res);
        Utils.setIconOnImageView(iconView,
                bookmark.getBlobIcon(), res, isSelectedItem,
                (int) mActivityRef.getResources().getDimension(R.dimen.medium_icon_size));
    }

    /**
     * ViewHolder def
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayoutView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;

        public ViewHolder(View v) {
            super(v);
            mLayoutView = v.findViewById(R.id.backgroundLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
        }
    }

}

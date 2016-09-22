package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.realm.adapter.RealmRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

import io.realm.RealmObject;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

public class BookmarkRecyclerViewAdapter<T extends RealmObject> extends
        RealmRecyclerViewAdapter<Bookmark> implements ItemTouchHelperAdapter {
    private final Activity mActivityRef;
    private final WeakReference<OnActionListenerInterface> listener;
    private final StatusHelper mStatusSingleton;
    private final int mDarkGrey;
    private final int mLightGrey;

    public BookmarkRecyclerViewAdapter(Activity activity, WeakReference<OnActionListenerInterface> lst) {
        mActivityRef = activity;
        listener = lst;
        mStatusSingleton = StatusHelper.getInstance();
        mDarkGrey = ContextCompat.getColor(mActivityRef.getApplicationContext(), R.color.indigo_50);
        mLightGrey = Color.TRANSPARENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
        final ViewHolder holder = (ViewHolder) rvh;
        final Bookmark bookmark = getItem(position);

        holder.mLabelView.setText(getBookmarkNameWrapper(bookmark.getName()));
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                .getTimestamp()));

        setEditMode(holder, bookmark, position);
    }

    /**
     * 
     */
    private void setEditMode(ViewHolder holder, Bookmark bookmark, int position) {
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
     * TODO refactor
     * @param iconView
     * @param bookmark
     * @param isSelectedItem
     */
    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        Drawable defaultIcon = ContextCompat.getDrawable(mActivityRef.getApplicationContext(),
                R.drawable.ic_bookmark_black_48dp);
        Utils.setIconOnImageView(iconView, bookmark.getBlobIcon(), defaultIcon, isSelectedItem,
                (int) mActivityRef.getResources().getDimension(R.dimen.medium_icon_size));
    }

    @Override
    public void updateData(RealmResults<Bookmark> filteredList) {
        getRealmBaseAdapter().updateData(filteredList);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {

    }

    /**
     * ViewHolder def
     */
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private final WeakReference<OnActionListenerInterface> listener;
        public View mLayoutView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;

        public ViewHolder(View v, WeakReference<OnActionListenerInterface> lst) {
            super(v);
            listener = lst;
            mLayoutView = v.findViewById(R.id.backgroundLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            return listener.get().onLongItemClick(view, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            listener.get().onItemClick(view, getAdapterPosition());
        }

    }

    /**
     *
     */
    public interface OnActionListenerInterface {
        boolean onLongItemClick(View view, int position);
        void onItemClick(View view, int position);
    }
}

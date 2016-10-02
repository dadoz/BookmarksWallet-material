package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
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
import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

public class BookmarkRecyclerViewAdapter<T extends RealmObject> extends
        RealmRecyclerViewAdapter<Bookmark> implements ItemTouchHelperAdapter {
    private final WeakReference<Context> context;
    private final WeakReference<OnActionListenerInterface> listener;
    private final StatusHelper mStatusSingleton;
    private static int mDarkGrey;
    private static int mLightGrey = Color.TRANSPARENT;
    private final MultipleSelector multipleSelector;
    private final Bitmap defaultIcon;

    public BookmarkRecyclerViewAdapter(WeakReference<Context> ctx, WeakReference<OnActionListenerInterface> lst) {
        context = ctx;
        listener = lst;
        mStatusSingleton = StatusHelper.getInstance();
        multipleSelector = new MultipleSelector();
        mDarkGrey = ContextCompat.getColor(context.get(), R.color.indigo_50);
        defaultIcon = BitmapFactory.decodeResource(context.get().getResources(),
                R.drawable.ic_bookmark_black_48dp);

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

        holder.labelView.setText(getBookmarkNameWrapper(bookmark.getName()));
        holder.urlView.setText(bookmark.getUrl());
        holder.timestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                .getTimestamp()));
        holder.selectItem(multipleSelector.isSelectedPos(position));
        holder.setIcon(Utils.getIconBitmap(bookmark.getBlobIcon(),
                (int) context.get().getResources().getDimension(R.dimen.medium_icon_size)), defaultIcon,
                multipleSelector.isSelectedPos(position));

    }

    @Override
    public int getItemCount() {
        return getRealmBaseAdapter() == null ? 0 : getRealmBaseAdapter().getCount();
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
     * TODO EXTEND class please
     */

    /**
     *
     * @param position
     */
    public void setSelectedItemPos(int position) {
        multipleSelector.setSelectedPos(position, !multipleSelector.isSelectedPos(position));
    }

    /**
     *
     * @return
     */
    public boolean isEmptySelectedPosArray() {
        return multipleSelector.getSelectedPosArraySize() == 0;
    }

    /**
     *
     */
    public void notifyRemovedSelectedItems() {
        for (int i = 0; i < multipleSelector.getSelectedPosArraySize(); i++) {
            int itemPos = multipleSelector.getSelectedPosArray().keyAt(i);
            notifyItemRemoved(itemPos);
        }
    }

    /**
     *
     */
    public void clearSelectedItemPosArray() {
        multipleSelector.clearSelectedItemPosArray();
    }

    /**
     *
     * @return
     */
    public ArrayList<Bookmark> getSelectedItemList() {
        ArrayList<Bookmark> selectedItemList = new ArrayList<>();
        //inside multiple selector
        for (int i = 0; i < multipleSelector.getSelectedPosArraySize(); i++) {
            int itemPos = multipleSelector.getSelectedPosArray().keyAt(i);
            selectedItemList.add(getRealmBaseAdapter().getItem(itemPos));
        }
        return selectedItemList.size() != 0 ? selectedItemList : null;
    }

    /**
     *
     * @return
     */
    public Bookmark getSelectedItem() {
        int itemPos = multipleSelector.getSelectedPosArray().keyAt(0);
        return getRealmBaseAdapter().getItem(itemPos);
    }

    /**
     *
     * @return
     */
    public int getSelectedItemListSize() {
        return multipleSelector.getSelectedPosArraySize();
    }

    /**
     * ViewHolder def
     */
    private static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private final WeakReference<OnActionListenerInterface> listener;
        private ImageView iconView;
        private TextView labelView;
        private TextView timestampView;
        private TextView urlView;

        private ViewHolder(View v, WeakReference<OnActionListenerInterface> lst) {
            super(v);
            listener = lst;
            iconView = (ImageView) v.findViewById(R.id.linkIconId);
            labelView = (TextView) v.findViewById(R.id.linkTitleId);
            urlView = (TextView) v.findViewById(R.id.linkUrlId);
            timestampView = (TextView) v.findViewById(R.id.linkTimestampId);
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

        /**
         *
         * @param selected
         */
        void selectItem(boolean selected) {
            itemView.setBackgroundColor(selected ? mDarkGrey : mLightGrey);
        }

        /**
         *
         * @param blobIcon
         * @param defaultIcon
         */
        void setIcon(Bitmap blobIcon, Bitmap defaultIcon, boolean isSelected) {
            Utils.setIconOnImageView(iconView, isSelected ? defaultIcon : blobIcon, defaultIcon);
        }
    }

    /**
     *
     */
    private class MultipleSelector {
        SparseBooleanArray selectedPosArray = new SparseBooleanArray();

        /**
         *
         * @param pos
         * @param selected
         */
        private void setSelectedPos(int pos, boolean selected) {
            if (!selected) {
                selectedPosArray.delete(pos);
                return;
            }
            selectedPosArray.put(pos, true);
        }

        /**
         *
         * @param pos
         * @return
         */
        private boolean isSelectedPos(int pos) {
            return selectedPosArray.get(pos, false);
        }

        /**
         *
         * @return
         */
        private int getSelectedPosArraySize() {
            return selectedPosArray.size();
        }

        /**
         *
         * @return
         */
        private SparseBooleanArray getSelectedPosArray() {
            return selectedPosArray;
        }

        /**
         *
         */
        private void clearSelectedItemPosArray() {
            selectedPosArray.clear();
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

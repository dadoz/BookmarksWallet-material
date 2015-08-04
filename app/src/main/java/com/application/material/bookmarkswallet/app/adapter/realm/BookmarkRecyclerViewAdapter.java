package com.application.material.bookmarkswallet.app.adapter.realm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by davide on 21/04/15.
 */
public class BookmarkRecyclerViewAdapter<T extends RealmObject> extends
        RealmRecyclerViewAdapter<Bookmark> implements
        View.OnClickListener, View.OnLongClickListener,
        SwipeDismissRecyclerViewTouchListener.DismissCallbacks {
    private final Activity mActivityRef;
    private final ActionbarSingleton mActionbarSingleton;
    private final RecyclerViewCustom mRecyclerView;
    private final RecyclerViewActionsSingleton mRvActionsSingleton;
    private View.OnTouchListener mTouchListener;
    private int MIN_LINES_COUNT = 2;
    private int MAX_LINES_COUNT = 10;

    public BookmarkRecyclerViewAdapter(Activity activity,
                                       RecyclerViewCustom recyclerView) {
        mActivityRef = activity;
        mRecyclerView = recyclerView;
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);
        mActionbarSingleton = ActionbarSingleton.getInstance(mActivityRef);
        mTouchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this, this); //LISTENER TO SWIPE
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

        Resources res = mActivityRef.getResources();
        String linkName = Bookmark.Utils.getBookmarkNameWrapper(bookmark.getName());
        holder.mLabelView.setText(linkName);
        holder.mLabelView.setTextColor(res.getColor(R.color.material_violet_500));

        holder.mUrlView.setTextColor(res.getColor(mActionbarSingleton
                .isSearchMode() ? R.color.material_grey_200 : R.color.material_violet_100));
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mUrlView.setVisibility(View.VISIBLE);
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                .getTimestamp()));

        boolean isSelectedItem = mActionbarSingleton.isEditMode();
        setIcon(holder.mIconView, null, false);
        setIcon(holder.mIconOpenedView, bookmark, false);
        holder.itemView.setBackgroundColor(mActivityRef
                .getResources().getColor(R.color.white));

        //CHANGE COLOR on more icon

        holder.mLinkIconFlipperView.setAnimateFirstView(false);
        holder.mLinkIconFlipperView.setDisplayedChild(0);

        setMoreInfoIconVisibility(holder, this, position);
        holder.mBackgroundLayoutView.setBackgroundColor(Color.TRANSPARENT);
        setItemSelected(holder, bookmark, position, isSelectedItem);
    }

    private void setMoreInfoIconVisibility(final ViewHolder holder, final BookmarkRecyclerViewAdapter adapter, final int position) {
        if (holder.mMoreInfoClosedIcon.getVisibility() == View.VISIBLE) {
            return;
        }

        holder.mUrlView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                boolean isEllipsis = holder.mUrlView.getLayout() != null &&
                        holder.mUrlView.getLayout().getEllipsisCount(holder.mUrlView.getLineCount() - 1) > 0 &&
                        holder.mUrlView.getLayout().getEllipsisCount(holder.mUrlView.getLineCount() - 1) < MAX_LINES_COUNT;
                holder.mMoreInfoClosedIcon.setVisibility(isEllipsis ? View.VISIBLE : View.INVISIBLE);
                holder.mMoreInfoClosedIcon.setOnClickListener(new MoreInfoFlipperClickListener(holder, adapter, position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return getRealmBaseAdapter() == null ? 0 : getRealmBaseAdapter().getCount();
    }

    @Override
    public void onClick(View v) {
//        Log.e("TAG", "click");
        int position = mRecyclerView.getChildPosition(v);

        Bookmark bookmark = (Bookmark) ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).getItem(position);
        mRvActionsSingleton.openLinkOnBrowser(bookmark.getUrl());
    }

    @Override
    public boolean onLongClick(View v) {
        if (isSearchMode()) {
            return true;
        }
        Log.e("TAG", "long click");
        int position = mRecyclerView.getChildPosition(v);
        BookmarkRecyclerViewAdapter.ViewHolder holder =
                (BookmarkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        holder.itemView.setSelected(true);
        mActionbarSingleton.setEditItemPos(position);

        // handle long press
        mRvActionsSingleton.selectBookmarkEditMenu(position);
        return true;
    }

    //SWIPE ACTION
    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
//        Log.e("TAG", reverseSortedPositions + "removing action");
        mRvActionsSingleton.onSwipeAction(reverseSortedPositions);
    }

    private void setItemSelected(ViewHolder holder, Bookmark bookmark,
                                 int position, boolean isSelectedItem) {
        Resources resources = mActivityRef.getResources();

        holder.itemView.setEnabled(!isSelectedItem);
        holder.itemView.setOnClickListener(isSelectedItem ? null : this);
        holder.itemView.setOnLongClickListener(isSelectedItem ? null : this);
        holder.itemView.setOnTouchListener(isSelectedItem ? null : mTouchListener);

        if (position == mActionbarSingleton.getEditItemPos() &&
                isSelectedItem) {
            setIcon(holder.mIconView, bookmark, isSelectedItem);
            holder.itemView.setBackgroundColor(resources
                    .getColor(R.color.material_violet_50));
            holder.mUrlView.setTextColor(resources.getColor(R.color.material_violet_100));
        }
    }

    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        Drawable res = mActivityRef
                .getResources()
                .getDrawable(isSelectedItem ?
                        R.drawable.ic_bookmark_black_48dp :
                        R.drawable.ic_bookmark_outline_black_48dp);
        mActionbarSingleton.setColorFilter(res, R.color.material_violet_500);
        iconView.setImageDrawable(res);

        if (bookmark != null) {
            Bitmap bitmapIcon = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());
            if (bitmapIcon != null &&
                    !isSelectedItem) {
                iconView.setImageBitmap(bitmapIcon);
            }
        }
    }

    public boolean isSearchMode() {
        return mActionbarSingleton.isSearchMode();
    }

    public void setSearchMode(boolean value) {
        mActionbarSingleton.setSearchMode(value);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView mUrlOpenedView;
        private final View mBackgroundLayoutView;
        private final ViewFlipper mLinkIconFlipperView;
        private final View mMoreInfoClosedIcon;
        public ImageView mIconOpenedView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;
//        private TextView mLastUpdateView;

        public ViewHolder(View v) {
            super(v);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mIconOpenedView = (ImageView) v.findViewById(R.id.linkIconOpenedIconId);
            mLinkIconFlipperView = (ViewFlipper) v.findViewById(R.id.linkIconFlipperIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
//            mUrlOpenedView = (TextView) v.findViewById(R.id.linkUrlOpenedId);
//            mLastUpdateView = (TextView) v.findViewById(R.id.lastUpdateTextId);
            mMoreInfoClosedIcon = v.findViewById(R.id.moreInfoClosedIconId);
            mBackgroundLayoutView = v.findViewById(R.id.backgroundLayoutId);
        }
    }

    public class MoreInfoFlipperClickListener implements View.OnClickListener {

        private final ViewHolder mHolder;

        public MoreInfoFlipperClickListener(ViewHolder hld, BookmarkRecyclerViewAdapter adapter, int position) {
            mHolder = hld;
        }

        @Override
        public void onClick(View v) {
            mHolder.mLinkIconFlipperView.setFlipInterval(500);
            Log.e("TAG", "" + mHolder.mUrlView.getLineCount());
            toggleView(mHolder.mUrlView.getLineCount() == 2);
        }

        public void toggleView(boolean isToggling) {
            if (isToggling) {
                mHolder.mUrlView.setMaxLines(MAX_LINES_COUNT);
//                mHolder.mLinkIconFlipperView.showNext();
                return;
            }

            mHolder.mUrlView.setLines(MIN_LINES_COUNT);
//            mHolder.mLinkIconFlipperView.setDisplayedChild(0);
        }
    }
}

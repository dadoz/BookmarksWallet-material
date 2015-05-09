package com.application.material.bookmarkswallet.app.adapter.realm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
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
    private final ActionBarHandlerSingleton mActionBarHandlerSingleton;
    private final RecyclerViewCustom mRecyclerView;
    private final RecyclerViewActionsSingleton mRvActionsSingleton;
    private boolean mSearchResult;
    private View.OnTouchListener mTouchListener;

    public BookmarkRecyclerViewAdapter(Activity activity,
                                       RecyclerViewCustom recyclerView) {
        mActivityRef = activity;
        mRecyclerView = recyclerView;
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
        mTouchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this, this); //LISTENER TO SWIPE
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mEditUrlLabelView;
        private final EditText mEditLabelView;
        private final View mEditLinkView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;
        private View mMainView;
        private String editNameTemp;
        private String editUrlTemp;
        private String TAG = "Holder";

        public ViewHolder(View v) {
            super(v);
            mMainView = v.findViewById(R.id.linkLayoutId);
            mEditLinkView = v.findViewById(R.id.editLinkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
            mEditUrlLabelView = (TextView) v.findViewById(R.id.editUrlLabelId);
            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
        final ViewHolder holder = (ViewHolder) rvh;
        Bookmark bookmark = (Bookmark) getItem(position);

        String linkName = Bookmark.Utils.getBookmarkNameWrapper(bookmark.getName());
        holder.mLabelView.setText(linkName);
        holder.mLabelView.setTextColor(mActivityRef
                .getResources().getColor(R.color.material_blue_grey));
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mUrlView.setVisibility(View.VISIBLE);
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark.getTimestamp()));

        boolean isSelectedItem = mActionBarHandlerSingleton.isEditMode();
        setIcon(holder.mIconView, bookmark, false);
        holder.itemView.setBackgroundColor(mActivityRef
                .getResources().getColor(R.color.white));

        setItemSelected(holder, bookmark, position, isSelectedItem);
    }

    @Override
    public int getItemCount() {
        return getRealmBaseAdapter() == null ? 0 : getRealmBaseAdapter().getCount();
    }

    @Override
    public void onClick(View v) {
        Log.e("TAG", "click");
        int position = mRecyclerView.getChildPosition(v);

        Bookmark bookmark = (Bookmark) ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).getItem(position);
        mRvActionsSingleton.openLinkOnBrowser(bookmark.getUrl());
    }

    @Override
    public boolean onLongClick(View v) {
        Log.e("TAG", "long click");
        int position = mRecyclerView.getChildPosition(v);
        BookmarkRecyclerViewAdapter.ViewHolder holder =
                (BookmarkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        holder.itemView.setSelected(true);
        mActionBarHandlerSingleton.setEditItemPos(position);

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
        Log.e("TAG", reverseSortedPositions + "removing action");
        mRvActionsSingleton.onSwipeAction(reverseSortedPositions);
//		setUndoDeletedLinkLayout(true);
    }

    private void setItemSelected(ViewHolder holder, Bookmark bookmark,
                                 int position, boolean isSelectedItem) {
        Resources resources = mActivityRef.getResources();

        holder.itemView.setEnabled(! isSelectedItem);
        holder.itemView.setOnClickListener(isSelectedItem ? null : this);
        holder.itemView.setOnLongClickListener(isSelectedItem ? null : this);
        holder.itemView.setOnTouchListener(isSelectedItem ? null : mTouchListener);

        if(position == mActionBarHandlerSingleton.getEditItemPos() &&
                isSelectedItem) {
            setIcon(holder.mIconView, bookmark, isSelectedItem);
            holder.mLabelView.setTextColor(resources.
                    getColor(R.color.white));
            holder.itemView.setBackgroundColor(resources
                    .getColor(R.color.material_blue_grey_900));
        }
    }

    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        iconView.setImageDrawable(mActivityRef
                .getResources()
                .getDrawable(isSelectedItem ?
                        R.drawable.ic_bookmark_white_48dp :
                        R.drawable.ic_bookmark_outline_black_48dp));
        Bitmap bitmapIcon = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());

        if(bitmapIcon != null &&
                ! isSelectedItem) {
            iconView.setImageBitmap(bitmapIcon);
        }
    }

    public boolean isSearchResult() {
        return mSearchResult;
    }

    public void setSearchResult(boolean mSearchResult) {
        this.mSearchResult = mSearchResult;
    }

}

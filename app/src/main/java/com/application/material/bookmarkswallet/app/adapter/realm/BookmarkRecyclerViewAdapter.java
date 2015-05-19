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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
    private Drawable colorFilter;

    public BookmarkRecyclerViewAdapter(Activity activity,
                                       RecyclerViewCustom recyclerView) {
        mActivityRef = activity;
        mRecyclerView = recyclerView;
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
        mTouchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this, this); //LISTENER TO SWIPE
    }

    private void setColorFilter(Drawable drawable, int color) {
        drawable.setColorFilter(mActivityRef.getResources().getColor(R.color.material_violet_500), PorterDuff.Mode.SRC_IN);
    }

    public Drawable getColorFilter() {
        return colorFilter;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mUrlOpenedView;
        private ImageView mMoreInfoClosedIconView;
        private final ViewFlipper mLinkIconFlipperView;
        private final View mMoreInfoClosedView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;
        private String TAG = "Holder";
        public ImageView mIconOpenedView;

        public ViewHolder(View v) {
            super(v);
//            mMainView = v.findViewById(R.id.linkLayoutId);
//            mEditLinkView = v.findViewById(R.id.editLinkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mIconOpenedView = (ImageView) v.findViewById(R.id.linkIconOpenedIconId);
            mLinkIconFlipperView = (ViewFlipper) v.findViewById(R.id.linkIconFlipperIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
//            mEditUrlLabelView = (TextView) v.findViewById(R.id.editUrlLabelId);
//            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
            mUrlOpenedView = (TextView) v.findViewById(R.id.linkUrlOpenedId);
            mMoreInfoClosedView = v.findViewById(R.id.moreInfoClosedContainerId);
            mMoreInfoClosedIconView = (ImageView) v.findViewById(R.id.moreInfoClosedIconId);
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
        final Bookmark bookmark = (Bookmark) getItem(position);

        String linkName = Bookmark.Utils.getBookmarkNameWrapper(bookmark.getName());
        holder.mLabelView.setText(linkName);
        holder.mLabelView.setTextColor(mActivityRef.getResources().getColor(R.color.material_violet_500));
//        holder.mLabelView.setTextColor(mActivityRef
//                .getResources().getColor(R.color.material_violet_500));
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mUrlView.setVisibility(View.VISIBLE);
        holder.mUrlOpenedView.setText(bookmark.getUrl());
        holder.mUrlOpenedView.setVisibility(View.GONE);
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark.getTimestamp()));

        boolean isSelectedItem = mActionBarHandlerSingleton.isEditMode();
        setIcon(holder.mIconView, null, false);
        setIcon(holder.mIconOpenedView, bookmark, false);
        holder.itemView.setBackgroundColor(mActivityRef
                .getResources().getColor(R.color.white));

        //CHANGE COLOR on more icon
        Drawable drawable = holder.mMoreInfoClosedIconView.getDrawable();
        setColorFilter(drawable, R.color.material_violet_500);
        holder.mMoreInfoClosedIconView.setImageDrawable(drawable);

//        holder.mLinkIconFlipperView.setInAnimation(mActivityRef, R.anim.card_flip_left_in);
//        holder.mLinkIconFlipperView.setOutAnimation(mActivityRef, R.anim.card_flip_left_out);
        holder.mLinkIconFlipperView.setAnimateFirstView(false);
        holder.mLinkIconFlipperView.setDisplayedChild(0);

        holder.mMoreInfoClosedView.setVisibility(View.VISIBLE);
        holder.mMoreInfoClosedView.setOnClickListener(new MoreInfoFlipperClickListener(holder, this, position));
//        holder.mUrlOpenedView.setOnClickListener(null);
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
//            holder.mLabelView.setTextColor(resources.
//                    getColor(R.color.white));
            holder.itemView.setBackgroundColor(resources
                    .getColor(R.color.material_violet_50));
            holder.mMoreInfoClosedView.setVisibility(View.INVISIBLE);
        }
    }

    private void setIcon(ImageView iconView, Bookmark bookmark, boolean isSelectedItem) {
        Drawable res = mActivityRef
                .getResources()
                .getDrawable(isSelectedItem ?
                        R.drawable.ic_bookmark_black_48dp :
                        R.drawable.ic_bookmark_outline_black_48dp);
        res.setColorFilter(mActivityRef.getResources().getColor(R.color.material_violet_500), PorterDuff.Mode.SRC_IN);
        iconView.setImageDrawable(res);

        if(bookmark != null) {
            Bitmap bitmapIcon = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());
            if(bitmapIcon != null &&
                    ! isSelectedItem) {
//                RelativeLayout.LayoutParams lp = new RelativeLayout
//                        .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT);
//                int padding = (int) mActivityRef.getResources().getDimension(R.dimen.medium_padding);
//                lp.setMargins(padding, padding, padding, padding);
//                iconView.setLayoutParams(lp);
                iconView.setImageBitmap(bitmapIcon);
            }
        }
    }

    public boolean isSearchResult() {
        return mSearchResult;
    }

    public void setSearchResult(boolean mSearchResult) {
        this.mSearchResult = mSearchResult;
    }

    public static class MoreInfoFlipperClickListener implements View.OnClickListener {

        private final ViewHolder mHolder;
        private final BookmarkRecyclerViewAdapter mAdapter;
        private final int mPosition;

        public MoreInfoFlipperClickListener(ViewHolder hld, BookmarkRecyclerViewAdapter adapter, int position) {
            mPosition = position;
            mAdapter = adapter;
            mHolder = hld;
        }

        @Override
        public void onClick(View v) {
            mHolder.mLinkIconFlipperView.setFlipInterval(500);
            toggleView(true);

            Handler flipOutHandler = new Handler();
            flipOutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleView(false);
                }
            }, 3000);
        }

        public void toggleView(boolean isToggling) {
            if (isToggling) {
                mHolder.mUrlOpenedView.setVisibility(View.VISIBLE);
                mHolder.mLinkIconFlipperView.showNext();
                return;
            }
            mHolder.mUrlOpenedView.setVisibility(View.GONE);
            mHolder.mLinkIconFlipperView.setDisplayedChild(0);

//            if(isToggling) {
//                mHolder.mLinkIconFlipperView.showNext();
//                mHolder.mUrlOpenedView.setVisibility(View.VISIBLE);
//                mHolder.mUrlView.setVisibility(View.INVISIBLE);
//                mHolder.mMoreInfoClosedView.setVisibility(View.GONE);
//                return;
//            }
//
//            mHolder.mLinkIconFlipperView.setDisplayedChild(0);
//            mHolder.mUrlOpenedView.setVisibility(View.GONE);
//            mHolder.mMoreInfoClosedView.setVisibility(View.VISIBLE);
//            mHolder.mUrlView.setVisibility(View.VISIBLE);

        }
    }
}

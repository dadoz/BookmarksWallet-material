package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;

/**
 * Created by davide on 17/01/15.
 */
public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.ViewHolder> {
    private final Fragment mListenerRef;
    private final boolean mIsSearchResult;
    private final Realm mRealm;
    private String TAG = "LinkRecyclerViewAdapter";
    private RealmResults<Bookmark> mDataset;
    private static Context mActivityRef;
    private static Bookmark deletedItem = null;
    private static int deletedItemPosition = -1;
    private int mSelectedItemPosition = -1;

    public LinkRecyclerViewAdapter(Fragment fragmentRef, RealmResults<Bookmark> myDataset, boolean isSearchResult) {
        mDataset = myDataset;
        mActivityRef = fragmentRef.getActivity();
        mListenerRef = fragmentRef;
        mIsSearchResult = isSearchResult;
        mRealm = Realm.getInstance(mActivityRef);
    }

    public Bookmark getDeletedItem() {
        return deletedItem;
    }

    public int getDeletedItemPosition() {
        return deletedItemPosition;
    }

    @Override
    public LinkRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.link_row, parent, false);
        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataset == null) {
            return;
        }
        Bookmark bookmark = mDataset.get(position);
        String linkName = bookmark.getName().trim().equals("") ?
                "(no title)" : bookmark.getName().trim();
        String urlName = bookmark.getUrl();
        holder.mLabelView.setText(linkName);
        holder.mUrlView.setText(urlName);
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark.getTimestamp()));
        Bitmap btmp = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());
        if(Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon()) != null) {
            holder.mIconView.setImageBitmap(btmp);
        } else {
            holder.mIconView
                    .setImageDrawable(mActivityRef.getResources()
                        .getDrawable(R.drawable.ic_bookmark_outline_black_48dp));
        }
        holder.mEditUrlLabelView.setOnClickListener((View.OnClickListener) mListenerRef);

        //BUG - big huge whtever u want
        boolean isSelectedItem = mSelectedItemPosition == position;
        if (isSelectedItem) {
            holder.mEditLabelView.setText(linkName);
            holder.mEditUrlLabelView.setTag(urlName);
        }

        holder.itemView.setPressed(false);
        if(isSelectedItem) {
            holder.mEditLabelView.requestFocus();
        }
        holder.itemView.setBackgroundColor(isSelectedItem ?
                mActivityRef.getResources().getColor(R.color.material_grey_200) :
                mActivityRef.getResources().getColor(R.color.white));
        holder.mEditLinkView.setVisibility(isSelectedItem ? View.VISIBLE : View.GONE);
        holder.mMainView.setVisibility(isSelectedItem ? View.GONE: View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void updateDataset() {
        mDataset = mRealm.where(Bookmark.class).findAll();
        notifyDataSetChanged();
    }

//    public void add(Bookmark item) {
//        mDataset.add(0, item);
//        notifyItemInserted(0);
//    }


    public void addOnPosition(Bookmark item, int position) {
//        mDataset.add(position, item);
        notifyItemInserted(position);
    }


    public void remove(int position) {
        deletedItemPosition = position;
        deletedItem = mDataset.get(position);
        mRealm.beginTransaction();
        deletedItem.removeFromRealm();
        mRealm.commitTransaction();
//        mDataset.remove(position);
        notifyItemRemoved(position);
    }

//    public void removeAll() {
////        mDataset.removeAll(mDataset);
//        if(mDataset == null) {
//            return;
//        }
//        int size = mDataset.size();
//        mDataset.clear();
//        notifyItemRangeRemoved(0, size);
//    }

    public void update(int position, String linkName, String linkUrl) {
        Bookmark bookmarkToBeUpdated = mDataset.get(position);
        if(linkName != null) {
            bookmarkToBeUpdated.setName(linkName);
        }
        if(linkUrl != null) {
            bookmarkToBeUpdated.setUrl(linkUrl);
        }

        notifyDataSetChanged();
    }

    public boolean isItemSelected() {
        return mSelectedItemPosition != -1;
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public boolean isSearchResult() {
        return mIsSearchResult;
    }

    public RealmResults<Bookmark> getDatasetRef() {
        return mDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView mEditUrlView;
        private final TextView mEditUrlLabelView;
        private final EditText mEditLabelView;
        private final View mEditLinkView;
//        private final TextView mEditUrlView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;
        private View mMainView;
        private String editNameTemp;
        private String editUrlTemp;
        private String TAG = "Holder";

        public ViewHolder(View v, LinkRecyclerViewAdapter adapterRef) {
            super(v);
            mMainView = v.findViewById(R.id.linkLayoutId);
            mEditLinkView = v.findViewById(R.id.editLinkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
//            mEditUrlView = (TextView) v.findViewById(R.id.editLinkUrlId);
            mEditUrlLabelView = (TextView) v.findViewById(R.id.editUrlLabelId);
            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
        }
        public String getEditLinkName() {
//            return mEditLabelView.getText().toString();
            return editNameTemp = mEditLabelView.getText().toString();
        }
        public String getEditUrlName() {
            return editUrlTemp;
        }

        public EditText getEditLinkView() {
            return mEditLabelView;
        }

        public void setEditUrlName(String value) {
            editUrlTemp = value;
        }
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.mSelectedItemPosition = selectedItemPosition;
    }

    public void deselectedItemPosition() {
        this.mSelectedItemPosition = -1;
    }


}
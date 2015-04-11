package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 17/01/15.
 */
public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.ViewHolder> {
    private final Fragment mListenerRef;
    private final boolean mIsSearchResult;
    private String TAG = "LinkRecyclerViewAdapter";
    private ArrayList<Link> mDataset;
    private static Context mActivityRef;
    private static Link deletedItem = null;
    private static int deletedItemPosition = -1;
    private int mSelectedItemPosition = -1;

    public LinkRecyclerViewAdapter(Fragment fragmentRef, ArrayList<Link> myDataset, boolean isSearchResult) {
        mDataset = myDataset;
        mActivityRef = fragmentRef.getActivity();
        mListenerRef = fragmentRef;
        mIsSearchResult = isSearchResult;
    }

    public Link getDeletedItem() {
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
        String linkName = mDataset.get(position).getLinkName().trim().equals("") ?
                "Bookmark (no title)" : mDataset.get(position).getLinkName().trim();
        String urlName = mDataset.get(position).getLinkUrl();
        holder.mLabelView.setText(linkName);
        holder.mUrlView.setText(urlName);

        holder.mEditUrlLabelView.setOnClickListener((View.OnClickListener) mListenerRef);

        //BUG - big huge whtever u want
        boolean isSelectedItem = mSelectedItemPosition == position;
        if(isSelectedItem) {
            holder.mEditLabelView.setText(linkName);
            holder.mEditUrlLabelView.setTag(urlName);
        }

        holder.itemView.setPressed(false);
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

    public void add(Link item) {
        mDataset.add(item);
        notifyItemInserted(mDataset.size());
    }

    public void addOnPosition(Link item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }


    public void remove(int position) {
        deletedItemPosition = position;
        deletedItem = mDataset.get(position);

        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        mDataset.removeAll(mDataset);
        notifyItemRemoved(0);
    }

    public void update(int position, String linkName, String linkUrl) {
        Link linkToBeUpdated = mDataset.get(position);
        if(linkName != null) {
            linkToBeUpdated.setLinkName(linkName);
        }
        if(linkUrl != null) {
            linkToBeUpdated.setLinkUrl(linkUrl);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView mEditUrlView;
        private final TextView mEditUrlLabelView;
        private final EditText mEditLabelView;
        private final View mEditLinkView;
//        private final TextView mEditUrlView;
        private ImageView mIconView;
        private TextView mLabelView;
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
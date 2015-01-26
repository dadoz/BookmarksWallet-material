package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.models.Link;

import java.util.ArrayList;

/**
 * Created by davide on 17/01/15.
 */
public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.ViewHolder> {
    private String TAG = "LinkRecyclerViewAdapter";
    private ArrayList<Link> mDataset;
    private static Context mActivityRef;
    private static Link deletedItem = null;
    private static int deletedItemPosition = -1;
    private int mSelectedItemPosition = -1;

    public LinkRecyclerViewAdapter(Fragment fragmentRef, ArrayList<Link> myDataset) {
        mDataset = myDataset;
        mActivityRef = fragmentRef.getActivity();
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
        holder.mLabelView.setText(mDataset.get(position).getLinkName());
        holder.mEditLabelView.setText(mDataset.get(position).getLinkName());
        holder.mEditUrlView.setText(mDataset.get(position).getLinkUrl());
        holder.mUrlView.setText(mDataset.get(position).getLinkUrl());

        //BUG - big huge whtever u want
        boolean isSelectedItem = mSelectedItemPosition == position;
        holder.itemView.setBackgroundColor(isSelectedItem ?
                mActivityRef.getResources().getColor(R.color.material_grey_200) :
                mActivityRef.getResources().getColor(R.color.white));
        holder.mEditLinkView.setVisibility(isSelectedItem ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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

    public void update(int position, String linkName, String linkUrl) {
        Link linkToBeUpdated = mDataset.get(position);
        linkToBeUpdated.setLinkName(linkName);
        linkToBeUpdated.setLinkUrl(linkUrl);

        notifyDataSetChanged();
    }

    public boolean isItemSelected() {
        return mSelectedItemPosition != -1;
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText mEditUrlView;
        private final EditText mEditLabelView;
        private final View mEditLinkView;
        public ImageView mIconView;
        public TextView mLabelView;
        public TextView mUrlView;
        public View mMainView;

        public ViewHolder(View v, LinkRecyclerViewAdapter adapterRef) {
            super(v);
            mMainView = v.findViewById(R.id.linkLayoutId);
            mEditLinkView = v.findViewById(R.id.editLinkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mEditUrlView = (EditText) v.findViewById(R.id.editLinkUrlId);
            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
        }
        public String getEditLinkName() {
            return mEditLabelView.getText().toString();
        }
        public String getEditUrlName() {
            return mEditUrlView.getText().toString();
        }
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.mSelectedItemPosition = selectedItemPosition;
    }

    public void deselectedItemPosition() {
        this.mSelectedItemPosition = -1;
    }

}
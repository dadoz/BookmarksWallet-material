package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.content.Context;
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
    private static Fragment mFragmentRef;
    private ArrayList<Link> mDataset;
    private static Context mActivityRef;
    private String TAG = "LinkRecyclerViewAdapter";
    private static Link deletedItem = null;
    private static int deletedItemPosition = -1;
    private int selectedItemPosition = -1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LinkRecyclerViewAdapter(Fragment fragmentRef, ArrayList<Link> myDataset) {
        mDataset = myDataset;
        mActivityRef = fragmentRef.getActivity();
        mFragmentRef = fragmentRef;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LinkRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.link_row, parent, false);
        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //fill view with data
//        holder.mTextView.setText(mDataset[position]);
        holder.mMainView.setOnClickListener(holder);
        holder.mLabelView.setText(mDataset.get(position).getLinkName());
        holder.mEditLabelView.setText(mDataset.get(position).getLinkName());
        holder.mEditUrlView.setText(mDataset.get(position).getLinkUrl());
        holder.mEditButtonView.setOnClickListener(holder);
        holder.mSaveButtonView.setOnClickListener(holder);
        holder.mDeleteButtonView.setOnClickListener(holder);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void add(Link item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
//        int position = mDataset.indexOf(item);
        deletedItemPosition = position;
        deletedItem = mDataset.get(position);

        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void update(int position, View view) {
        Link linkToBeUpdated = mDataset.get(position);
        linkToBeUpdated.setLinkName(((EditText) view.findViewById(R.id.editLinkTitleId)).getText().toString());
        linkToBeUpdated.setLinkUrl(((EditText) view.findViewById(R.id.editLinkUrlId)).getText().toString());

//        deselectedItemPosition();
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mSaveButtonView;
        private final TextView mDeleteButtonView;
        private final TextView mEditButtonView;
        private final EditText mEditUrlView;
        private final EditText mEditLabelView;
        private final LinkRecyclerViewAdapter mAdapterRef;
        // each data item is just a string in this case
        public ImageView mIconView;
        public TextView mLabelView;
        public View mMainView;

        public ViewHolder(View v, LinkRecyclerViewAdapter adapterRef) {
            super(v);
            mAdapterRef = adapterRef;
            mMainView = v.findViewById(R.id.linkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mEditButtonView = (TextView) v.findViewById(R.id.linkEditButtonId);
            mSaveButtonView = (TextView) v.findViewById(R.id.linkSaveButtonId);
            mDeleteButtonView = (TextView) v.findViewById(R.id.linkDeleteButtonId);
            mEditUrlView = (EditText) v.findViewById(R.id.editLinkUrlId);
            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
            ((LinksListFragment) mFragmentRef).setUndoLayoutListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linkLayoutId:
                    Toast.makeText(mActivityRef, "open url", Toast.LENGTH_SHORT).show();

//                int position = recyclerListView.getChildPosition(v);

//                String linkUrl = linkListTest.get(position).getLinkUrl();
//                openLinkOnBrowser(linkUrl);
                    break;
                case R.id.linkEditButtonId:
                    Toast.makeText(mActivityRef, "edit" + getPosition(), Toast.LENGTH_SHORT).show();
                    ((MainActivity) mActivityRef).toggleEditActionBar("Edit link", true);


//                    mAdapterRef.toggle(mMainView);
//                if(adapter.isSelectedItem()) {
//                    break;
//                }
//
//                View itemView = (View) v.getParent();
//                adapter.setSelectedItemPosition(recyclerListView.getChildPosition(itemView));
//
//                mainActivityRef.toggleEditActionBar("Edit link", true);
//                Log.e(TAG, "hey edit" + adapter.getSelectedItemPosition());
//				toggleEditView(true);
//                addLinkButton.setVisibility(View.GONE);
                    break;
                case R.id.linkSaveButtonId:
                    Toast.makeText(mActivityRef, "save", Toast.LENGTH_SHORT).show();
//				itemView = (View) v.getParent();
//                position = adapter.getSelectedItemPosition();
//                ((LinkRecyclerViewAdapter) recyclerListView.getAdapter()).
//                        update(position, recyclerListView.getChildAt(position));

//                mainActivityRef.toggleEditActionBar(null, false);
//				toggleEditView(false);
//                Log.e(TAG, "hey saving " + position);

//                addLinkButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.linkDeleteButtonId:
                    Toast.makeText(mActivityRef, "delete", Toast.LENGTH_SHORT).show();
                    deletedItemPosition = getPosition();
                    deletedItem = mAdapterRef.mDataset.get(getPosition()); //TODO replace
                    mAdapterRef.remove(getPosition());

                    ((LinksListFragment) mFragmentRef).linkDeleteUpdateUI(true);
                    break;
                case R.id.undoButtonId:
                    Toast.makeText(mActivityRef, "undo", Toast.LENGTH_SHORT).show();
                    mAdapterRef.add(deletedItem, deletedItemPosition);
                    ((LinksListFragment) mFragmentRef).linkDeleteUpdateUI(false);
                    break;
                case R.id.dismissButtonId:
                    Toast.makeText(mActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
                    ((LinksListFragment) mFragmentRef).linkDeleteUpdateUI(false);
                    break;
            }
        }
    }





/*

    public void notifyRemoveIsEnd() {
        deletedItemPosition = -1;
        deletedItem = null;
    }

    public int getDeletedItemPosition() {
        return deletedItemPosition;
    }

    public Link getDeletedItem() {
        return deletedItem;
    }


    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
    }

    public void deselectedItemPosition() {
        this.selectedItemPosition = -1;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public boolean isSelectedItem() {
        return selectedItemPosition != -1;
    }
*/
}
package com.application.material.bookmarkswallet.app.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.realm.RealmRecyclerViewAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmObject;

public abstract class MultipleSelectorHelperAdapter<T extends RealmObject> extends
        RealmRecyclerViewAdapter<Bookmark> {
    private final MultipleSelector multipleSelector;

    MultipleSelectorHelperAdapter(WeakReference<Context> ctx) {
        super(ctx);
        multipleSelector = new MultipleSelector();
    }

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
            selectedItemList.add(getItem(itemPos));
        }
        return selectedItemList.size() != 0 ? selectedItemList : null;
    }

    /**
     *
     * @return
     */
    public Bookmark getSelectedItem() {
        int itemPos = multipleSelector.getSelectedPosArray().keyAt(0);
        return getItem(itemPos);
    }

    /**
     *
     * @return
     */
    public int getSelectedItemListSize() {
        return multipleSelector.getSelectedPosArraySize();
    }

    /**
     *
     */
    public void setSelectedAllItemPos() {
        for (int i = 0; i < getItemCount(); i++) {
            multipleSelector.setSelectedPos(i, true);
        }
    }

    /**
     *
     * @param pos
     */
    public boolean isSelectedPos(int pos) {
        return multipleSelector.isSelectedPos(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    /**
     * multiple selector handler
     */
    private class MultipleSelector {
        SparseBooleanArray selectedPosArray = new SparseBooleanArray();

        /**
         *
         * @param pos
         * @param selected
         */
        protected void setSelectedPos(int pos, boolean selected) {
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
        boolean isSelectedPos(int pos) {
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
}
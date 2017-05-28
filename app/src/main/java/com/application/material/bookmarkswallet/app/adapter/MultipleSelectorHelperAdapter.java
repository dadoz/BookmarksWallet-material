package com.application.material.bookmarkswallet.app.adapter;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionModeCallback;
import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.adapter.TreeNodeAdapter;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmObject;

public abstract class MultipleSelectorHelperAdapter<T extends RealmObject> extends
        TreeNodeAdapter implements OnNodeClickListener {
    private final MultipleSelector multipleSelector;

    MultipleSelectorHelperAdapter() {
        super(new ArrayList<>());
        multipleSelector = new MultipleSelector();
        setOnNodeClickListener(this);
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
    public ArrayList<TreeNodeInterface> getSelectedItemList() {
        ArrayList<TreeNodeInterface> selectedItemList = new ArrayList<>(); //todo sparseArray
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
    public TreeNodeInterface getSelectedItem() {
        int itemPos = multipleSelector.getSelectedPosArray().keyAt(0);
        return getItem(itemPos);
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

    public void onFileNodeCLick(View v, int position, TreeNodeInterface node) {
        if (getSelectedItemListSize() != 0) {
            onFileNodeLongCLick(v, position, node);
            return;
        }
        Snackbar.make(v.getRootView(), node.getNodeContent().getName(), Snackbar.LENGTH_SHORT).show();
    }

    public void onFileNodeLongCLick(View v, int position, TreeNodeInterface item) {
        setSelectedItemPos(position);
        int grey = ContextCompat.getColor(v.getContext(), R.color.indigo_300);
        v.setBackgroundColor(isSelectedPos(position) ? grey : Color.WHITE);
    }

    public void onFolderNodeCLick(View v, int position, TreeNodeInterface node) {
    }

    public void onFolderNodeLongCLick(View v, int position, TreeNodeInterface item) {
    }

    public void onMoreSettingsClick(View v, int position, TreeNodeInterface item) {
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
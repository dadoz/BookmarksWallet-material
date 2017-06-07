package com.application.material.bookmarkswallet.app.adapter;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.adapter.TreeNodeAdapter;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmObject;

public abstract class MultipleSelectorHelperAdapter<T extends RealmObject> extends
        TreeNodeAdapter implements OnNodeClickListener {
    private final MultipleSelector multipleSelector;
    private WeakReference<OnMultipleSelectorCallback> lst;

    /**
     *
     * @param lst
     */
    MultipleSelectorHelperAdapter(OnMultipleSelectorCallback lst) {
        super(new ArrayList<>());
        this.lst = new WeakReference<>(lst);
        multipleSelector = new MultipleSelector();
        setOnNodeClickListener(this);
    }

    /**
     *
     */
    public void notifyRemovedSelectedItems() {
        for (int i = 0; i < multipleSelector.getSelectedIdArraySize(); i++) {
            int itemId = multipleSelector.getSelectedIdArray().keyAt(i);
//            notifyItemRemoved(itemPos);
        }
    }

    /**
     *
     */
    public void clearSelectedItemPosArray() {
        multipleSelector.clearSelectedItemIdArray();
    }

    /**
     *
     * @return
     */
    public SparseIntArray getSelectedItemIdArray() {
        return multipleSelector.getSelectedIdArray();
    }

    /**
     *
     * @return
     */
    public TreeNodeInterface getSelectedItem() {
        int itemPos = multipleSelector.getSelectedIdArray().keyAt(0);
        return getItem(itemPos);
    }

    @Override
    public void onBindViewHolder(TreeNodeAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        int selectedColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.indigo_300);
        holder.itemView.setBackgroundColor(isSelectedPos(position) ? selectedColor : Color.WHITE);
    }

    /**
     *
     * @param id
     */
    public void toggleSelectedItemPos(int id, int position) {
        multipleSelector.toggleSelectedId(id, position);
    }

    private void setSelectedItemPos(int id, int position) {
        multipleSelector.setSelectedId(id, position);
    }

    /**
     *
     * @return
     */
    public boolean isEmptySelectedPosArray() {
        return multipleSelector.getSelectedIdArraySize() == 0;
    }

    /**
     *
     * @return
     */
    public int getSelectedItemListSize() {
        return multipleSelector.getSelectedIdArraySize();
    }

    /**
     *
     * @param files
     */
    public void setSelectedAllItemPos(ArrayList<TreeNodeInterface> files) {
        if (files != null)
            for (int i = 0; i < files.size(); i++) {
                setSelectedItemPos(files.get(i).getId(), i);
            }
    }


    /**
     *
     * @param position
     */
    public boolean isSelectedPos(int position) {
        return multipleSelector.isSelectedPos(position);
    }

    /**
     *
     */
    public void removeSelectedItems() {
        clearSelectedItemPosArray();
    }

    /**
     *
     * @param v
     * @param position
     * @param node
     */
    public void onFileNodeCLick(View v, int position, TreeNodeInterface node) {
        if (getSelectedItemListSize() != 0) {
            onFileNodeLongCLick(v, position, node);
            return;
        }

        if (lst != null && lst.get() != null)
            lst.get().onFileNodeClickCb(v, position, node);
    }

    /**
     *
     * @param v
     * @param position
     * @param item
     */
    public void onFileNodeLongCLick(View v, int position, TreeNodeInterface item) {
        toggleSelectedItemPos(item.getId(), position);
        notifyDataSetChanged();
        if (lst != null && lst.get() != null)
            lst.get().onFileNodeLongClickCb(v, position, item);
    }

    public void onFolderNodeCLick(View v, int position, TreeNodeInterface node) {
    }

    public void onFolderNodeLongCLick(View v, int position, TreeNodeInterface item) {
    }

    public void onMoreSettingsClick(View v, int position, TreeNodeInterface item) {
    }

    public void onSelectButtonClick(View v, int position, TreeNodeInterface item) {
    }

    /**
     * multiple selector handler
     */
    private class MultipleSelector {
        SparseIntArray selectedPosArray = new SparseIntArray();

        /**
         *
         * @param id
         * @param position
         */
        protected void toggleSelectedId(int id, int position) {
            if (!isSelectedId(id)) {
                selectedPosArray.put(id, position);
                return;
            }
            selectedPosArray.delete(id);
        }
        /**
         *
         * @param id
         * @param position
         */
        protected void setSelectedId(int id, int position) {
            selectedPosArray.put(id, position);
        }

        /**
         *
         * @param id
         * @return
         */
        boolean isSelectedId(int id) {
            return selectedPosArray.get(id, -1) != -1;
        }

        /**
         *
         * @return
         */
        private int getSelectedIdArraySize() {
            return selectedPosArray.size();
        }

        /**
         *
         * @return
         */
        private SparseIntArray getSelectedIdArray() {
            return selectedPosArray;
        }

        /**
         *
         */
        private void clearSelectedItemIdArray() {
            selectedPosArray.clear();
        }

        public boolean isSelectedPos(int position) {
            return selectedPosArray.indexOfValue(position) != -1;
        }
    }
}
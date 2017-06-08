package com.lib.davidelm.filetreevisitorlibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.views.ExclusiveSelectionButton;

import java.lang.ref.WeakReference;
import java.util.List;


public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {
    private final List<TreeNodeInterface> items;
    private WeakReference<OnNodeClickListener> lst;
    @NonNull
    private String TAG = "TreeNodeAdapter";
    private boolean showMoreSettingButton;

    public TreeNodeAdapter(List<TreeNodeInterface> list, WeakReference<OnNodeClickListener> lst, boolean showMoreSettingButton) {
        this.items = list;
        this.lst = lst;
        this.showMoreSettingButton = showMoreSettingButton;

    }

    public TreeNodeAdapter(List<TreeNodeInterface> list) {
        this.items = list;
    }

    public void setOnNodeClickListener(OnNodeClickListener lst) {
        this.lst = new WeakReference<>(lst);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int nodeLayoutRes = viewType == 0 ? R.layout.linear_node_item : R.layout.cardview_node_item;
        View view = View.inflate(parent.getContext(), nodeLayoutRes, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isFolder() ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TreeNodeInterface item = items.get(position);
        TreeNodeContent nodeContent = item.getNodeContent();

        holder.nodeLabelText.setText(nodeContent.getName());

        holder.nodeDescriptionText.setVisibility(nodeContent.getDescription() == null ? View.GONE : View.VISIBLE);
        holder.nodeDescriptionText.setText(nodeContent.getDescription());
        setIcon(holder.nodeIconImage, holder.itemView.getContext(), items.get(position).isFolder(), nodeContent);

        //set long click listner
        setLongClickListener(holder, position, item);

        //set click listner
        setClickListener(holder, position, item);

        //set on more setting button click - only on linear views
        setMoreSettingClickListener(holder, position, item);

        //handle set select button click
        setSelectClickListener(holder, position, item);
    }

    /**
     *
     * @param holder
     * @param position
     * @param item
     */
    private void setSelectClickListener(@NonNull ViewHolder holder, final int position, @NonNull TreeNodeInterface item) {
        if (getItemViewType(position) == 0) {
            holder.nodeSelectButton.setVisibility(!showMoreSettingButton ?View.VISIBLE : View.GONE);
            holder.nodeSelectButton.setSelected(item.isSelected());

            holder.nodeSelectButton.setOnClickListener(v -> {
                exclusiveItemToggleSelection(item);
                notifyDataSetChanged();

                //set cbs
                if (lst != null && lst.get() != null)
                    lst.get().onSelectButtonClick(v, position, item);
            });
        }
    }

    /**
     * unselecte the other keep this one TODO please rm it
     * @param item
     */
    private void exclusiveItemSelection(@NonNull TreeNodeInterface item) {
        //unslect the others
        for (TreeNodeInterface temp : items)
            temp.setSelected(false);

        //select current item
        item.setSelected(true);
    }

    /**
     *
     * @param item
     */
    private void exclusiveItemToggleSelection(@NonNull TreeNodeInterface item) {
        //unslect the others
        for (TreeNodeInterface temp : items)
            if (item.getId() != temp.getId())
                temp.setSelected(false);

        //select current item
        item.toggleSelected();
    }

    /**
     *
     * @param holder
     * @param position
     * @param item
     */
    private void setMoreSettingClickListener(@NonNull ViewHolder holder, int position, TreeNodeInterface item) {
        if (getItemViewType(position) == 0) {
            holder.nodeMoreSettingsButton.setVisibility(showMoreSettingButton ?View.VISIBLE : View.GONE);
            holder.nodeMoreSettingsButton.setOnClickListener(v -> {
                if (lst != null && lst.get() != null)
                    lst.get().onMoreSettingsClick(v, position, item);
            });
        }

    }

    /**
     *
     * @param holder
     * @param position
     * @param item
     */
    private void setClickListener(@NonNull ViewHolder holder, int position, @NonNull TreeNodeInterface item) {
        holder.itemView.setOnClickListener(v -> {
            if (lst != null && lst.get() != null) {
                if (item.isFolder())
                    lst.get().onFolderNodeCLick(v, position, item);
                else
                    lst.get().onFileNodeCLick(v, position, item);
            }
        });

    }

    /**
     *
     * @param holder
     * @param position
     * @param item
     */
    private void setLongClickListener(@NonNull ViewHolder holder, int position, @NonNull TreeNodeInterface item) {
        holder.itemView.setOnLongClickListener(v -> {
            if (lst != null && lst.get() != null) {
                if (item.isFolder())
                    lst.get().onFolderNodeLongCLick(v, position, item);
                else
                    lst.get().onFileNodeLongCLick(v, position, item);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     *
     * @param position
     * @return
     */
    public TreeNodeInterface getItem(int position) {
        return items.get(position);
    }

    /**
     * add single item
     * @param node
     */
    public void addItem(TreeNodeInterface node) {
        items.add(node);
        notifyDataSetChanged();
    }

    /**
     * add items
     * @param list
     */
    public void addItems(@NonNull List<TreeNodeInterface> list) {
        items.clear(); //TODO fix it THIS IS WRONG
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }
    /**
     * remove item
     * @param childNode
     */
    public void removeItem(TreeNodeInterface childNode) {
        int position = items.indexOf(childNode);
        items.remove(position);
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    /**
     *
     * @param imageView
     * @param context
     * @param isFolder
     * @param nodeContent
     */
    private void setIcon(@NonNull ImageView imageView, @NonNull Context context, boolean isFolder, @NonNull TreeNodeContent nodeContent) {
        try {
            //FIXME ignoring folder nodeContent property
//            int folderResource = nodeContent.getFolderResource() == -1 ? R.mipmap.ic_folder : nodeContent.getFolderResource();
            int fileResource = nodeContent.getFileResource() == -1 ? R.mipmap.ic_file : nodeContent.getFileResource();
            imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    isFolder ? R.mipmap.ic_folder : fileResource));
        } catch (Exception e) {
            imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    isFolder ? R.mipmap.ic_folder : R.mipmap.ic_file));
        }
    }

    public void clearSelectedItem() {
        for (TreeNodeInterface item : items)
            item.setSelected(false);
        notifyDataSetChanged();
    }


    /**
     * view holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final TextView nodeLabelText;
        @NonNull
        private final ImageView nodeIconImage;
        @NonNull
        private final TextView nodeDescriptionText;
        @NonNull
        private final ImageView nodeMoreSettingsButton;
        @NonNull
        private final ExclusiveSelectionButton nodeSelectButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nodeSelectButton = (ExclusiveSelectionButton) itemView.findViewById(R.id.nodeMoreSelectButtonId);
            nodeMoreSettingsButton = (ImageView) itemView.findViewById(R.id.nodeMoreSettingsButtonId);
            nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
            nodeDescriptionText = (TextView) itemView.findViewById(R.id.nodeDescriptionTextId);
            nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
        }
    }

}

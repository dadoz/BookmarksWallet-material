package com.lib.davidelm.filetreevisitorlibrary.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {
    private final List<TreeNodeInterface> items;
    private WeakReference<OnNodeClickListener> lst;
    private String TAG = "TreeNodeAdapter";

    public TreeNodeAdapter(List<TreeNodeInterface> list, WeakReference<OnNodeClickListener> lst) {
        this.items = list;
        this.lst = lst;
    }

    public TreeNodeAdapter(List<TreeNodeInterface> list) {
        this.items = list;
    }

    public void setOnNodeClickListener(OnNodeClickListener lst) {
        this.lst = new WeakReference<>(lst);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int nodeLayoutRes = viewType == 0 ? R.layout.linear_node_item : R.layout.cardview_node_item;
        View view = View.inflate(parent.getContext(), nodeLayoutRes, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isFolder() ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TreeNodeInterface item = items.get(position);
        TreeNodeContent nodeContent = item.getNodeContent();

        holder.nodeLabelText.setText(nodeContent.getName());

        holder.nodeDescriptionText.setVisibility(nodeContent.getDescription() == null ? View.GONE : View.VISIBLE);
        holder.nodeDescriptionText.setText(nodeContent.getDescription());
        setIcon(holder.nodeIconImage, holder.itemView.getContext(), items.get(position).isFolder(), nodeContent);

        //set long click listner
        holder.itemView.setOnLongClickListener(v -> {
            if (lst != null && lst.get() != null) {
                if (item.isFolder())
                    lst.get().onFolderNodeLongCLick(v, position, item);
                else
                    lst.get().onFileNodeLongCLick(v, position, item);
            }
            return true;
        });

        //set click listner
        holder.itemView.setOnClickListener(v -> {
            if (lst != null && lst.get() != null) {
                if (item.isFolder())
                    lst.get().onFolderNodeCLick(v, position, item);
                else
                    lst.get().onFileNodeCLick(v, position, item);
            }
        });

        //set on more setting button click - only on linear views
        if (getItemViewType(position) == 0) {
            holder.nodeMoreSettingsButton.setOnClickListener(v -> {
                if (lst != null && lst.get() != null)
                    lst.get().onMoreSettingsClick(v, position, item);
            });
        }
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
    public void addItems(List<TreeNodeInterface> list) {
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

    /**
     *
     * @param imageView
     * @param context
     * @param isFolder
     * @param nodeContent
     */
    private void setIcon(ImageView imageView, Context context, boolean isFolder, TreeNodeContent nodeContent) {
        try {
            int folderResource = nodeContent.getFolderResource() == -1 ? R.mipmap.ic_folder : nodeContent.getFolderResource();
            int fileResource = nodeContent.getFileResource() == -1 ? R.mipmap.ic_file : nodeContent.getFileResource();
            imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    isFolder ? folderResource : fileResource));
        } catch (Exception e) {
            imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    isFolder ? R.mipmap.ic_folder : R.mipmap.ic_file));
        }
    }


    /**
     * view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nodeLabelText;
        private final ImageView nodeIconImage;
        private final TextView nodeDescriptionText;
        private final ImageView nodeMoreSettingsButton;

        ViewHolder(View itemView) {
            super(itemView);
            nodeMoreSettingsButton = (ImageView) itemView.findViewById(R.id.nodeMoreSettingsButtonId);
            nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
            nodeDescriptionText = (TextView) itemView.findViewById(R.id.nodeDescriptionTextId);
            nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
        }
    }

}

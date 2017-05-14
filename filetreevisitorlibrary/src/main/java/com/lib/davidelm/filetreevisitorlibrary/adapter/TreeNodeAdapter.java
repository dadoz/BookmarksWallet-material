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
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.text.BreakIterator;
import java.util.List;


public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {
    private final List<TreeNodeInterface> items;
    private WeakReference<OnNodeClickListener> lst;
    private String TAG = "TreeNodeAdapter";

    public TreeNodeAdapter(List<TreeNodeInterface> list, WeakReference<OnNodeClickListener> lst) {
        items = list;
        this.lst = lst;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.node_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TreeNodeInterface item = items.get(position);
        TreeNodeContent nodeContent = item.getNodeContent();

        holder.nodeLabelText.setText(nodeContent.getName());

//        holder.nodeDescriptionText.setVisibility(nodeContent.getDescription() == null ? View.GONE : View.VISIBLE);
        holder.nodeDescriptionText.setText(nodeContent.getDescription());
        setIcon(holder.nodeIconImage, holder.itemView.getContext(), items.get(position).isFolder(), nodeContent);

        holder.itemView.setOnClickListener(v -> {
            if (lst.get() != null && item.isFolder())
                lst.get().onFolderNodeCLick(v, position, item);

            if (lst.get() != null && !item.isFolder())
                lst.get().onFileNodeCLick(v, position, item);
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
    public void addItems(List<TreeNodeInterface> list) {
        items.clear();
        items.addAll(list);
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

        ViewHolder(View itemView) {
            super(itemView);
            nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
            nodeDescriptionText = (TextView) itemView.findViewById(R.id.nodeDescriptionTextId);
            nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
        }
    }

}

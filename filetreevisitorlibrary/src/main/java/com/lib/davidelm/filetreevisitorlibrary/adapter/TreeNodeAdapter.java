package com.lib.davidelm.filetreevisitorlibrary.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
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
        holder.itemView.setOnClickListener(v -> {
            if (lst.get() != null && item.isFolder())
                lst.get().onFolderNodeCLick(v, position, item);

            if (lst.get() != null && !item.isFolder())
                lst.get().onFileNodeCLick(v, position, item);
        });
        holder.nodeLabelText.setText(items.get(position).getName().toString());
        holder.nodeIconImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
                items.get(position).isFolder() ? R.mipmap.ic_folder : R.mipmap.ic_file));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(TreeNode node) {
        items.add(node);
        notifyDataSetChanged();
    }

    public void addItems(List<TreeNodeInterface> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void removeItem(TreeNodeInterface childNode) {
        int position = items.indexOf(childNode);
        Log.e(TAG, "DEL " + items.size() + " - " + childNode.getName().toString() + " - " + position);
        items.remove(position);
        notifyDataSetChanged();
    }


    /**
     * view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nodeLabelText;
        private final ImageView nodeIconImage;

        ViewHolder(View itemView) {
            super(itemView);
            nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
            nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
        }
    }

}

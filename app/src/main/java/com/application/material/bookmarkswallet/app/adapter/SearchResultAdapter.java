package com.application.material.bookmarkswallet.app.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by davide on 28/06/2017.
 */

/**
 * Adapter
 * TODO mv smwhere
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private List<TreeNodeInterface> items;
    private WeakReference<OnSearchItemClickListener> lst;

    public SearchResultAdapter(List<TreeNodeInterface> items, OnSearchItemClickListener lst) {
        this.items = items;
        this.lst = new WeakReference<>(lst);
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.linear_node_item, null);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        holder.nodeIconImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
                R.mipmap.ic_bookmark_border_dark));
        holder.nodeLabelText.setText(items.get(position).getNodeContent().getName());
        holder.nodeDescriptionText.setText(items.get(position).getNodeContent().getDescription());
        holder.nodeMoreSelectButton.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> lst.get().onSearchItemClick(items.get(position)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<TreeNodeInterface> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        public final ImageView nodeIconImage;
        public final TextView nodeLabelText;
        public final TextView nodeDescriptionText;
        private final View nodeMoreSelectButton;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
            nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
            nodeDescriptionText = (TextView) itemView.findViewById(R.id.nodeDescriptionTextId);
            nodeMoreSelectButton = itemView.findViewById(R.id.nodeMoreSelectButtonId);
        }
    }

}
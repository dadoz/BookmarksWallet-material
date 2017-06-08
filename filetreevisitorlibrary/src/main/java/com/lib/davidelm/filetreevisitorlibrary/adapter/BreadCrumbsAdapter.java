package com.lib.davidelm.filetreevisitorlibrary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lib.davidelm.filetreevisitorlibrary.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class BreadCrumbsAdapter extends RecyclerView.Adapter<BreadCrumbsAdapter.ViewHolder> {
    private final ArrayList<String> items;
    private WeakReference<OnSelectedItemClickListener> lst;

    public BreadCrumbsAdapter(ArrayList<String> list, WeakReference<OnSelectedItemClickListener> lst) {
        items = list;
        this.lst = lst;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.breadcrumbs_item_view, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> lst.get().onItemClick(v, position));
        holder.labelTextView.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(String breadCrumb) {
        items.add(breadCrumb);
        notifyDataSetChanged();
    }

    public void removeItem(String breadCrumb) {
        items.remove(breadCrumb);
        notifyDataSetChanged();
    }

    public void removeLastItem() {
        if (items.size() > 1) {
            items.remove(items.size() - 1);
            notifyDataSetChanged();
        }
    }

    public void removeItemTillPosition(int position) {
        for (int i = items.size() -1; i > position; i --)
            items.remove(i);
        notifyDataSetChanged();
    }


    /**
     * view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView labelTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = (TextView) itemView.findViewById(R.id.breadCrumbsLabelTextId);
        }
    }

    public interface  OnSelectedItemClickListener{
        void onItemClick(View view, int position);
    }
}

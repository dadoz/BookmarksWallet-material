package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.adapter.BreadCrumbsAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BreadCrumbsView extends RecyclerView implements BreadCrumbsAdapter.OnSelectedItemClickListener {


    private WeakReference<OnPopBackStackInterface> lst;

    public BreadCrumbsView(Context context) {
        super(context);
        initView();
    }

    public BreadCrumbsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BreadCrumbsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * init view setting up root breadcrumb
     */
    private void initView() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ArrayList<String> list = new ArrayList<>();
        list.add("root");

        //set layout manager and adapter
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new BreadCrumbsAdapter(list, new WeakReference<>(this)));
    }

    /**
     *
     * @param breadCrumb
     */
    public void addBreadCrumb(String breadCrumb) {
        ((BreadCrumbsAdapter) getAdapter()).addItem(breadCrumb);
    }
    /**
     *
     * @param breadCrumb
     */
    public void removeLatestBreadCrumb(String breadCrumb) {
        ((BreadCrumbsAdapter) getAdapter()).removeItem(breadCrumb);
    }

    public void removeLatestBreadCrumb() {
        ((BreadCrumbsAdapter) getAdapter()).removeLastItem();
    }

    @Override
    public void onItemClick(View view, int position) {
        //remove all item > position
        ((BreadCrumbsAdapter) getAdapter()).removeItemTillPosition(position);

        if (lst != null &&
                lst.get() != null)
            lst.get().popBackStackTillPosition(position);
    }

    public void setLst(WeakReference<OnPopBackStackInterface> lst) {
        this.lst = lst;
    }

    /**
     * on pop back stack cb
     */
    public interface OnPopBackStackInterface {
        void popBackStackTillPosition(int position);
    }

}

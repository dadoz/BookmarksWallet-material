package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.content.res.TypedArray;
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
    private int DEFAULT_BACKGROUND_COLOR = R.color.yellow_300;
    public BreadCrumbsView(Context context) {
        super(context);
        initView(null);
    }

    public BreadCrumbsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public BreadCrumbsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    /**
     * init view setting up root breadcrumb
     * @param attrs
     */
    private void initView(AttributeSet attrs) {
        //get color from attribute
        setBackgroundColorByAttrs(attrs);

        ArrayList<String> list = new ArrayList<>();
        list.add("root");

        //set layout manager and adapter
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new BreadCrumbsAdapter(list, new WeakReference<>(this)));
    }

    /**
     *
     * @param attrs
     */
    private void setBackgroundColorByAttrs(AttributeSet attrs) {
        if (attrs == null) {
            setBackgroundColor(ContextCompat.getColor(getContext(), DEFAULT_BACKGROUND_COLOR));
            return;
        }

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BreadCrumbsView, 0, 0);
        try {
            int color = typedArray.getResourceId(R.styleable.BreadCrumbsView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
            setBackgroundColor(ContextCompat.getColor(getContext(), color));
        } catch (Exception e) {
            setBackgroundColor(ContextCompat.getColor(getContext(), DEFAULT_BACKGROUND_COLOR));
            e.printStackTrace();
        }
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

    public void setRootNode() {
        onItemClick(null, 0);
    }

    /**
     * on pop back stack cb
     */
    public interface OnPopBackStackInterface {
        void popBackStackTillPosition(int position);
    }

}

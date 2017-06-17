package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.ActionMenuRevealHelper;
import com.application.material.bookmarkswallet.app.helpers.ActionMenuRevealHelper.ActionMenuRevealCallbacks;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

public class ContextRevealMenuView extends FrameLayout implements View.OnClickListener {
    private ImageView exportIcon;
    private ImageView settingsIcon;
    private ImageView gridviewResizeIcon;
    private WeakReference<ActionMenuRevealCallbacks> listenerCallbacks;
    private boolean isExpanded;

    public ContextRevealMenuView(Context context) {
        super(context);
        initView();
    }

    public ContextRevealMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public ContextRevealMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     *
     */
    private void initView() {
        View view = inflate(getContext(), R.layout.context_menu_layout, this);
        exportIcon = (ImageView) view.findViewById(R.id.actionMenuExportId);
        settingsIcon = (ImageView) view.findViewById(R.id.actionMenuSettingsId);
        gridviewResizeIcon = (ImageView) view.findViewById(R.id.actionMenuGridviewResizeId);

        exportIcon.setOnClickListener(this);
        settingsIcon.setOnClickListener(this);
        gridviewResizeIcon.setOnClickListener(this);
        setColorByNightMode();
    }

    /**
     *
     */
    private void setColorByNightMode() {
        int color = NightModeHelper.getInstance().isNightMode() ? R.color.grey_50 : R.color.indigo_600;
        for (ImageView view : new ImageView[] {exportIcon, settingsIcon, gridviewResizeIcon}) {
            view.setImageDrawable(Utils.getColoredIcon(getContext(), view.getDrawable(), color));
        }
    }

    /**
     *
     */
    public void initActionMenu(boolean isExpandedGridView,
                               WeakReference<ActionMenuRevealHelper.ActionMenuRevealCallbacks> lst ) {
        listenerCallbacks = lst;
        isExpanded = isExpandedGridView;
        toggleResizeIcon(isExpandedGridView);

    }


    /**
     *
     */
    public void showRevealActionMenu(boolean isShowing) {
        View appbarLayout = getRootView().findViewById(R.id.appBarLayoutId);
        ActionbarHelper.setElevationOnVIew(appbarLayout, false);

        ActionMenuRevealHelper.toggleRevealActionMenu(new WeakReference<Context>(getContext()), this,
                isShowing, listenerCallbacks);
    }
    /**
     *
     */
    public void toggleRevealActionMenu() {
        View appbarLayout = getRootView().findViewById(R.id.appBarLayoutId);
        ActionbarHelper.setElevationOnVIew(appbarLayout, !isShowing());

        ActionMenuRevealHelper.toggleRevealActionMenu(new WeakReference<Context>(getContext()),
                this, isShowing(), listenerCallbacks);
    }

    /**
     *
     */
    public void toggleResizeIcon(boolean expandedGridview) {
        gridviewResizeIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),
                expandedGridview ? R.drawable.ic_view_quilt_black_48dp :
                        R.drawable.ic_view_stream_black_48dp));

        gridviewResizeIcon.setImageDrawable(Utils.getColoredIcon(getContext(), gridviewResizeIcon.getDrawable(),
                NightModeHelper.getInstance().isNightMode() ? R.color.grey_50 : R.color.indigo_600));

    }

    @Override
    public void onClick(View view) {
        toggleRevealActionMenu();
        switch (view.getId()) {
            case R.id.actionMenuSettingsId:
                if (listenerCallbacks.get() != null)
                    listenerCallbacks.get().hanldeSettingsContextMenu();
                break;
            case R.id.actionMenuExportId:
                FlurryAgent.logEvent("export", true);
                if (listenerCallbacks.get() != null)
                    listenerCallbacks.get().hanldeExportContextMenu();
                break;
            case R.id.actionMenuGridviewResizeId:
                isExpanded = !isExpanded;
                toggleResizeIcon(isExpanded);
                if (listenerCallbacks.get() != null)
                    listenerCallbacks.get().hanldeExportGridviewResizeMenu();
                break;

        }
    }

    public boolean isShowing() {
        return !(getVisibility() == VISIBLE);
    }
}

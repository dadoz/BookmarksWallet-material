package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.ActionMenuRevealHelper.ActionMenuRevealCallbacks;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.ActionMenuRevealHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.EXPANDED_GRIDVIEW;

public class ContextRevealMenuView extends io.codetail.widget.RevealFrameLayout {
    private ImageView exportIcon;
    private ImageView settingsIcon;
    private ImageView gridviewResizeIcon;
    private WeakReference<ActionMenuRevealCallbacks> listenerCallbacks;

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
        setColorByNightMode();
    }

    /**
     *
     */
    private void setColorByNightMode() {
        int color = NightModeHelper.getInstance().getConfigMode() == AppCompatDelegate.MODE_NIGHT_NO ?
                R.color.indigo_600 : R.color.grey_50;
        for (ImageView view : new ImageView[] {exportIcon, settingsIcon, gridviewResizeIcon}) {
            Drawable drawable = view.getDrawable();
            drawable.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_ATOP);
            view.setImageDrawable(drawable);
        }
    }

    /**
     *
     */
    public void initActionMenu(boolean isExpandedGridView, WeakReference<ActionMenuRevealHelper.ActionMenuRevealCallbacks> lst ) {
        listenerCallbacks = lst;
        toggleResizeIcon(isExpandedGridView);
    }


    /**
     *
     * @param isShowing
     */
    public void toggleRevealActionMenu(boolean isShowing) {
        ActionMenuRevealHelper.getInstance(new WeakReference<Context>(getContext()))
                .toggleRevealActionMenu(this, isShowing, listenerCallbacks);
    }

    /**
     *
     */
    public void toggleResizeIcon(boolean expandedGridview) {
        gridviewResizeIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),
                expandedGridview ? R.drawable.ic_view_quilt_black_48dp :
                        R.drawable.ic_view_stream_black_48dp));

    }
}

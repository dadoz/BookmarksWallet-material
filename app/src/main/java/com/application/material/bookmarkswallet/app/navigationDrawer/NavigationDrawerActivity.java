package com.application.material.bookmarkswallet.app.navigationDrawer;

/**
 * Created by davide on 25/04/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.application.material.bookmarkswallet.app.BaseActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.ExportFragment;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.utlis.BrowserUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.willowtreeapps.saguaro.android.Saguaro;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.BuildConfig.KOFI_DAVE_URL;


public abstract class NavigationDrawerActivity extends BaseActivity {
    private static final String TAG = "BaseActivity";
    private final int resourceLayoutId;
    @BindView(R.id.drawerLayoutId)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbarId)
    Toolbar toolbar;
    @BindView(R.id.drawerNavigationViewId)
    NavigationView drawerNavigationView;
    private Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     *
     * @param resourceLayoutId
     */
    protected NavigationDrawerActivity(int resourceLayoutId) {
        this.resourceLayoutId = resourceLayoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceLayoutId);
        unbinder = ButterKnife.bind(this);

        //first handle frag
        ActivityUtils.onChangeFragment(getSupportFragmentManager(),
                new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG);

        //init actionbar
        initActionbar();

        //init navigation
        initNavigationView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     *
     */
    protected void initActionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.bookmark_list_title)); //def title
    }

    /**
     * build main and footer drawer menu
     * <p>
     * init view
     */
    private void initNavigationView() {
        new DrawerBuilder().withActivity(this)
                .withCloseOnClick(true)
                .withAccountHeader(getHeaderView())
                .withDrawerItems(getMenuList())
                .withFooterDivider(true)
//                .withStickyDrawerItems(getFooterItems())
                .withStickyFooterShadow(false)
                .withStickyFooterDivider(true)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> onItemMenuSelected(position))
                .build();
    }


    /**
     * build header view
     *
     * @return
     */
    public AccountHeader getHeaderView() {
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(new ProfileDrawerItem()
                        .withTextColorRes(R.color.md_white_1000)
                        .withDisabledTextColorRes(R.color.md_white_1000)
                        .withNameShown(false))
                .withSelectionListEnabledForSingleProfile(false) //ADD multiple profile
                .withTextColorRes(R.color.indigo_600)
                .withDividerBelowHeader(true)
                .build();

        //hide profile since we dont have any
        accountHeader.getView().findViewById(R.id.material_drawer_account_header_current)
                .setVisibility(View.GONE);

        return accountHeader;
    }

    /**
     * @return
     */
    public List<IDrawerItem> getMenuList() {
        List<IDrawerItem> menuList = new ArrayList<>();
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.export_bookmarks)
                .withSelectable(false)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(1)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.drawable.ic_archive_black_48dp)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.send_feedback_label)
                .withSelectable(false)
                .withIcon(R.mipmap.ic_feedback)
                .withIdentifier(4)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.history)
                .withSelectable(false)
                .withEnabled(false)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_200))
                .withIcon(R.mipmap.ic_history)
                .withDescription(R.string.available_soon)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(2)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.setting_cloud_sync)
                .withSelectable(false)
                .withEnabled(false)
                .withDescription(R.string.available_soon)
                .withIcon(R.drawable.ic_cloud_off_black_48dp)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_200))
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(3)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new DividerDrawerItem());
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.settings)
                .withSelectable(false)
                .withIcon(R.drawable.ic_settings_black_48dp)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(5)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new DividerDrawerItem());
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.offer_me_coffee)
                .withSelectable(false)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.mipmap.ic_local_cafe)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(6)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        return menuList;
    }


    /**
     * base selected item menu
     * @param position
     */
    public boolean onItemMenuSelected(int position) {
        Log.e(TAG, "position" + position);
        switch (position) {
            case 1:
                ActivityUtils.onChangeFragment(getSupportFragmentManager(), new ExportFragment(),
                        ExportFragment.FRAG_TAG);
                break;
            case 2:
                startActivity(Saguaro.getSendFeedbackIntent(getApplicationContext()));
                break;
            case 6:
                ActivityUtils.onChangeFragment(getSupportFragmentManager(), new SettingsFragment(),
                        SettingsFragment.FRAG_TAG);
                break;
            case 8:
                BrowserUtils.openUrl(KOFI_DAVE_URL, getApplicationContext());
                break;
        }
        return false;
    }

}

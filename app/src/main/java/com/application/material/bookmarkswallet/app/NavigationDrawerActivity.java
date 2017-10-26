package com.application.material.bookmarkswallet.app;

/**
 * Created by davide on 25/04/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.ExportFragment;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.utlis.ActivityUtils;
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
    @BindView(R.id.toolbarId)
    Toolbar toolbar;
    private Unbinder unbinder;
    private NightModeHelper nightModeHelper;

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
        nightModeHelper = new NightModeHelper(this);

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
        new DrawerBuilder()
                .withActivity(this)
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
                .addProfiles(new ProfileDrawerItem().withNameShown(false))
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
                .withName(R.string.bookmark_list_label)
                .withSelectable(true)
                .withIdentifier(0)
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withIcon(R.mipmap.ic_bookmark_border_dark)
                .withSelectedTextColorRes(R.color.indigo_600));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.export_bookmarks)
                .withSelectable(true)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(1)
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withIcon(R.drawable.ic_archive_black_48dp));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.send_feedback_label)
                .withSelectable(false)
                .withIcon(R.mipmap.ic_feedback)
                .withIdentifier(5));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.history)
                .withSelectable(false)
                .withEnabled(false)
                .withDisabledTextColor(nightModeHelper.getDefaultColorRes())
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withIcon(R.mipmap.ic_history)
                .withDescription(R.string.available_soon)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(6));
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.setting_cloud_sync)
                .withSelectable(false)
                .withEnabled(false)
                .withDisabledTextColor(nightModeHelper.getDefaultColorRes())
                .withDescription(R.string.available_soon)
                .withIcon(R.drawable.ic_cloud_off_black_48dp)
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(7));
        menuList.add(new DividerDrawerItem());
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.settings)
                .withSelectable(true)
                .withIcon(R.drawable.ic_settings_black_48dp)
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(8));
        menuList.add(new DividerDrawerItem());
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.offer_me_coffee)
                .withSelectable(false)
                .withIconColor(nightModeHelper.getDefaultColorRes())
                .withIcon(R.mipmap.ic_local_cafe)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(9));
        return menuList;
    }

    /**
     * base selected item menu
     * @param position
     */
    public boolean onItemMenuSelected(int position) {
        switch (position) {
            case 1:
                ActivityUtils.onChangeFragment(getSupportFragmentManager(), new BookmarkListFragment(),
                        BookmarkListFragment.FRAG_TAG);
                break;
            case 2:
                ActivityUtils.onChangeFragment(getSupportFragmentManager(), new ExportFragment(),
                        ExportFragment.FRAG_TAG);
                break;
            case 3:
                startActivity(Saguaro.getSendFeedbackIntent(getApplicationContext()));
                break;
            case 7:
                ActivityUtils.onChangeFragment(getSupportFragmentManager(), new SettingsFragment(),
                        SettingsFragment.FRAG_TAG);
                break;
            case 9:
                BrowserUtils.openUrl(KOFI_DAVE_URL, getApplicationContext());
                break;
        }
        return false;
    }


}

package com.application.material.bookmarkswallet.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.flurry.android.FlurryAgent;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.helpers.ExportHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class BaseDrawerMenuActivity extends AppCompatActivity {
    private final int contentViewLayoutId;
    private Unbinder unbinder;
    @BindView(R.id.toolbarId)
    Toolbar toolbar;

    BaseDrawerMenuActivity(int layoutId) {
        contentViewLayoutId = layoutId;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentViewLayoutId);
        FlurryAgent.onStartSession(this);

        unbinder = ButterKnife.bind(this);

        NightModeHelper.getInstance(this).setConfigurationMode();
        initActionbar();

        initActionbar();
        initNavigationView();
    }

    @Override
    public void onResume() {
        super.onResume();
        NightModeHelper.getInstance(this).setNightModeLocal();
        initActionbar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (((AddBookmarkActivity.OnHandleBackPressed) getSupportFragmentManager()
//                .findFragmentByTag(BookmarkListFragment.FRAG_TAG)).handleBackPressed()) {
//            return;
//        }
//        super.onBackPressed();
//    }

    /**
     *
     */
//    private void initActionbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarId);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setTitle(R.string.bookmark_list_title);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        ActionbarHelper.setElevationOnVIew(findViewById(R.id.appBarLayoutId), true);
//    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ExportStrategy.getInstance(new WeakReference<>(getApplicationContext()))
                        .handleRequestPermissionSuccess();
                return;
            }

            ExportStrategy.getInstance(new WeakReference<>(getApplicationContext()))
                    .handleRequestPermissionDeny();
        }
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
//        toolbar.setNavigationIcon(R.drawable.ic_user_blue_18dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");
    }

    /**
     * build main and footer drawer menu
     * <p>
     * init view
     */
    private void initNavigationView() {
        Drawer drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(getHeaderView())
                .withDrawerItems(getMenuList())
                .withFooterDivider(true)
//                .withSelectedItem()
//                .withStickyDrawerItems(getFooterItems())
                .withStickyFooterShadow(false)
                .withStickyFooterDivider(true)
                .build();
//        drawerMenu.setActionBarDrawerToggle(new ActionBarDrawerToggle(this, drawerMenu.getDrawerLayout(), toolbar, 0, 0));
    }


    /**
     * build header view
     *
     * @return
     */
    public AccountHeader getHeaderView() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(new ProfileDrawerItem()
                        .withName("hey")
                        .withEmail("dldl"))
                .withSelectionListEnabledForSingleProfile(false) //ADD multiple profile
                .build();
    }

    /**
     * @return
     */
    public List<IDrawerItem> getMenuList() {
        List<IDrawerItem> menuList = new ArrayList<>();
        List<PrimaryDrawerItem> list = Arrays.asList(new PrimaryDrawerItem().withName("bla"),
            new PrimaryDrawerItem().withName("Export"),
            new PrimaryDrawerItem().withName("List-Grid"),
            new PrimaryDrawerItem().withName("Upload from file"));
        menuList.addAll(list);
        return menuList;
    }

    /**
     * @return
     */
//    public List<IDrawerItem> getFooterItems() {
//        List<IDrawerItem> list = new ArrayList<>();
//        list.add(new PrimaryDrawerItem().withName(R.string.about_title)
//                .withTextColorRes(R.color.grey_700)
//                .withIcon(R.drawable.ic_info_black_18dp)
//                .withSelectedTextColorRes(R.color.grey_700)
//                .withSelectedColorRes(R.color.grey_100)
//                .withSelectable(false)
//                .withIdentifier(999)
//                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
//                    ActivityUtils.showAboutDialog(this);
//                    return false;
//                }));
//        list.add(new PrimaryDrawerItem().withName(R.string.logout)
//                .withTextColorRes(R.color.grey_700)
//                .withIcon(R.drawable.ic_exit_to_app_black_18dp)
//                .withSelectedTextColorRes(R.color.grey_700)
//                .withSelectedColorRes(R.color.grey_100)
//                .withSelectable(false)
//                .withIdentifier(999)
//                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
//                    LogoutHelper.logout(new WeakReference<>(this));
//                    return false;
//                }));
//        return list;
//    }

    /**
     * base selected item menu
     * @param position
     */
    public boolean onItemMenuSelected(int position) {
        return true;
    }


}

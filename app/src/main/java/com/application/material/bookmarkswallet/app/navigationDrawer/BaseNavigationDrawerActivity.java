package com.application.material.bookmarkswallet.app.navigationDrawer;

/**
 * Created by davide on 25/04/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.application.BookmarksWalletApplication;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.ExportFragment;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.BuildConfig.KOFI_DAVE_URL;
import static com.application.material.bookmarkswallet.app.MainActivity.SHARED_URL_EXTRA_KEY;
import static com.application.material.bookmarkswallet.app.helpers.ExportHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;


public abstract class BaseNavigationDrawerActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
//    private static final String HEADER_URL = "https://mir-s3-cdn-cf.behance.net/project_modules/max_3840/2120db18526001.569bc37d9869f.png";
    private static final String HEADER_URL = "https://raw.githubusercontent.com/Dahnark/Navigation-Drawer-Android-Design-Support-Library/master/app/src/main/res/drawable/header.jpg";
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
     * handle kind of layout
     *
     * @return
     */
    abstract public boolean onItemMenuSelectedCallback(int position);

    /**
     *
     * @param resourceLayoutId
     */
    protected BaseNavigationDrawerActivity(int resourceLayoutId) {
        this.resourceLayoutId = resourceLayoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceLayoutId);
        unbinder = ButterKnife.bind(this);

        //setContentView(R.layout.activity_main_layout);
        FlurryAgent.onStartSession(this);
        NightModeHelper.getInstance(this).setConfigurationMode();

        //init actionbar
        initActionbar();

        //first handle frag
        onInjectFragment(new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG);

        //then handleSharedIntent
        if (handleSharedIntent() != null) {
            Intent intent = new Intent(this, AddBookmarkActivity.class);
            intent.putExtras(handleSharedIntent());
            startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
        }

        initActionbar();
        initNavigationView();
    }

    /**
     * init fragment function
     */
    public void onInjectFragment(Fragment frag, String tag) {
        boolean isSameFrag = ActivityUtils.isSameFrag(getSupportFragmentManager(), frag);
        frag = isSameFrag ? ActivityUtils.findLastFragment(getSupportFragmentManager()) : frag;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerFrameLayoutId, frag, tag);
        if (!isSameFrag &&
                !(frag instanceof BookmarkListFragment))
            transaction.addToBackStack(tag);
        transaction.commit();
    }

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

    /**
     * handle shared intet
     */
    private Bundle handleSharedIntent() {
        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
//            Log.e(TAG, "hey" + getIntent().getStringExtra(Intent.EXTRA_TEXT));
            String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (sharedUrl == null) {
                return null;
            }

            Bundle sharedUrlBundle = new Bundle();
            sharedUrlBundle.putString(SHARED_URL_EXTRA_KEY, sharedUrl);
            return sharedUrlBundle;
        }
        return null;
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
                .withHeaderBackground(R.drawable.ic_archive_black_48dp)
                .addProfiles(new ProfileDrawerItem()
                        .withTextColorRes(R.color.md_white_1000)
                        .withDisabledTextColorRes(R.color.md_white_1000)
                        .withNameShown(false))
                .withSelectionListEnabledForSingleProfile(false) //ADD multiple profile
                .withTextColorRes(R.color.indigo_600)
                .withDividerBelowHeader(true)
                .build();

        //set drawerImage
        if (getApplication() != null) {
            ((BookmarksWalletApplication) getApplication())
                    .getDrawerImageLoader()
                    .setImage(accountHeader.getHeaderBackgroundView(),
                            Uri.parse(HEADER_URL), "tag");
        }
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
//                .withDescription("descriptions bla bla bla")
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(1)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.drawable.ic_archive_black_48dp)
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
                .withIdentifier(4)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new DividerDrawerItem());
        menuList.add(new PrimaryDrawerItem()
                .withName(R.string.offer_me_coffee)
                .withSelectable(false)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.mipmap.ic_local_cafe)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(5)
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
                onInjectFragment(new ExportFragment(), ExportFragment.FRAG_TAG);
                break;
            case 5:
                onInjectFragment(new SettingsFragment(), SettingsFragment.FRAG_TAG);
                break;
            case 7:
                new BookmarkActionHelper(getApplicationContext()).openLinkOnBrowser(KOFI_DAVE_URL, null);
                break;
        }
        return false;
    }

}

package com.application.material.bookmarkswallet.app.navigationDrawer;

/**
 * Created by davide on 25/04/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public abstract class BaseNavigationDrawerActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private final int resourceLayoutId;
    @BindView(R.id.drawerLayoutId)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbarId)
    Toolbar toolbar;
    @BindView(R.id.drawerNavigationViewId)
    NavigationView drawerNavigationView;
    public String title = "";
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
     * handle kind of layout
     *
     * @return
     */
    abstract public void inflateViewOnMainView();

    protected BaseNavigationDrawerActivity(int resourceLayoutId) {
        this.resourceLayoutId = resourceLayoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceLayoutId);
        inflateViewOnMainView();
        unbinder = ButterKnife.bind(this);

        initActionbar();
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
        Drawer drawerMenu = new DrawerBuilder().withActivity(this)
                .withAccountHeader(getHeaderView())
                .withDrawerItems(getMenuList())
                .withFooterDivider(true)
                .withStickyDrawerItems(getFooterItems())
                .withStickyFooterShadow(false)
                .withStickyFooterDivider(true)
                .withToolbar(toolbar)
//                .withOnDrawerItemClickListener((view, position, drawerItem) -> view.getId() != 999L && onItemMenuSelected(position))
                .build();
//        drawerMenu.setActionBarDrawerToggle(new ActionBarDrawerToggle(this, drawerMenu.getDrawerLayout(), toolbar, 0, 0));
    }


    /**
     * build header view
     *
     * @return
     */
    public AccountHeader getHeaderView() {
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(new ProfileDrawerItem()
                        .withName(getString(R.string.bsd2_name))
                        .withEmail(getString(R.string.send_feedback_email))
                        .withIcon(ContextCompat.getDrawable(this, R.drawable.ic_cloud_off_black_48dp)))
                .withSelectionListEnabledForSingleProfile(false) //ADD multiple profile
                .withTextColorRes(R.color.indigo_600)
                .withDividerBelowHeader(true)
                .build();

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView);
            }

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {

            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(getApplicationContext())
                        .cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        })
                .setImage(accountHeader.getHeaderBackgroundView(), Uri.parse("http://www.google.it"), "tag");
        return accountHeader;
    }

    /**
     * @return
     */
    public List<IDrawerItem> getMenuList() {
        List<IDrawerItem> menuList = new ArrayList<>();
        menuList.add(new PrimaryDrawerItem()
                .withName("Export Bookmarks")
//                .withDescription("descriptions bla bla bla")
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(1)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.drawable.ic_archive_black_48dp)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        menuList.add(new PrimaryDrawerItem()
                .withName("Minify/Expanded")
//                .withDescription("Expand or minifiy view description")
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.indigo_600))
                .withIcon(R.drawable.ic_view_stream_black_48dp)
                .withDescriptionTextColorRes(R.color.grey_400)
                .withIdentifier(2)
                .withSelectedTextColorRes(R.color.indigo_600)
                .withSelectedColorRes(R.color.grey_100));
        return menuList;
    }

    /**
     * @return
     */
    public List<IDrawerItem> getFooterItems() {
        List<IDrawerItem> list = new ArrayList<>();
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
        return list;
    }


    /**
     * base selected item menu
     * @param position
     */
    public boolean onItemMenuSelected(int position) {
        Log.e(TAG, "Hey click " + position);
        return true;
    }

}

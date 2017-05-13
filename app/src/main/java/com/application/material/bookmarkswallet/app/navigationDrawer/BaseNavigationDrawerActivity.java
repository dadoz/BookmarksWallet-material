package com.application.material.bookmarkswallet.app.navigationDrawer;

/**
 * Created by davide on 25/04/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.MainActivity.SHARED_URL_EXTRA_KEY;
import static com.application.material.bookmarkswallet.app.helpers.ExportHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;


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
        onInitFragment();

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
    public void onInitFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG)
                .commit();
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

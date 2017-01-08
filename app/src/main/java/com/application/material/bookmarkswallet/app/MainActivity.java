package com.application.material.bookmarkswallet.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.helpers.ExportHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public static String SHARED_URL_EXTRA_KEY = "SHARED_URL_EXTRA_KEY";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        NightModeHelper.getInstance().setNightModeIfEnabled(new WeakReference<>(getApplicationContext()));
        FlurryAgent.onStartSession(this);

        //first handle frag
        onInitFragment();

        //then handleSharedIntent
        if (handleSharedIntent() != null) {
            Intent intent = new Intent(this, AddBookmarkActivity.class);
            intent.putExtras(handleSharedIntent());
            startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * init fragment function
     */
    public void onInitFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        Fragment frag = getSupportFragmentManager().
                findFragmentByTag(BookmarkListFragment.FRAG_TAG);

        if (backStackCnt > 0) {
            handleBackStackEntry(transaction);
            return;
        }

        if (backStackCnt == 0 &&
                frag != null) {
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, BookmarkListFragment.FRAG_TAG).commit();
            return;
        }

        //no fragment already adedd
        transaction.add(R.id.fragmentContainerFrameLayoutId,
                new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG).commit();
    }

    /**
     *
     * @param transaction
     */
    private void handleBackStackEntry(FragmentTransaction transaction) {
        int fragCount = getSupportFragmentManager().getBackStackEntryCount();
        String fragTag = getSupportFragmentManager().getBackStackEntryAt(fragCount - 1).getName();
        Fragment frag = getSupportFragmentManager().findFragmentByTag(fragTag);
        transaction.replace(R.id.fragmentContainerFrameLayoutId,
                frag, fragTag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (((AddBookmarkActivity.OnHandleBackPressed) getSupportFragmentManager()
                .findFragmentByTag(BookmarkListFragment.FRAG_TAG)).handleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
     *
     */
    private Bundle handleSharedIntent()  {
        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
            Log.e(TAG, "hey" + getIntent().getStringExtra(Intent.EXTRA_TEXT));
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

}

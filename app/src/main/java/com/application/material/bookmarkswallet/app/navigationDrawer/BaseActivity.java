package com.application.material.bookmarkswallet.app.navigationDrawer;

/**
 * Created by davide on 25/04/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.flurry.android.FlurryAgent;

import pub.devrel.easypermissions.EasyPermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.application.material.bookmarkswallet.app.MainActivity.SHARED_URL_EXTRA_KEY;


public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlurryAgent.onStartSession(this);
        NightModeHelper.getInstance(this).setConfigurationMode();

        //then handleSharedIntent
        if (handleSharedIntent() != null) {
            Intent intent = new Intent(this, AddBookmarkActivity.class);
            intent.putExtras(handleSharedIntent());
            startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //forward result to easy permission
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * handle shared intet
     */
    private Bundle handleSharedIntent() {
        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
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
        OnBackPressedHandlerInterface backPressedHandler = ActivityUtils
                .getBackPressedHandler(getSupportFragmentManager());
        if (backPressedHandler != null &&
                backPressedHandler.handleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

//search mode
//        StatusManager status = StatusManager.getInstance();
//        if (status.isOnActionMenuMode() ||
//                status.isSearchActionbarMode()) {
//            status.unsetStatus();
//            addBookmarkFab.setVisibility(View.VISIBLE);
//            if (searchManager.getSearchView() != null)
//                searchManager.getSearchView().closeSearch();
//            return true;
//        }
//        return false;


    /**
     *
     */
    public interface OnBackPressedHandlerInterface {
        boolean handleBackPressed();
    }

}
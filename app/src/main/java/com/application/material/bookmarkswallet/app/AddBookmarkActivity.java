package com.application.material.bookmarkswallet.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.bookmarkswallet.app.fragments.AddBookmarkResultFragment;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkSearchFragment;
import com.application.material.bookmarkswallet.app.views.AddBookmarkResultLayout;

public class AddBookmarkActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_layout);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initActionbar();
        changeFrag();
    }

    private void initActionbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarId));
    }

    /**
     *
     */
    private void changeFrag() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        new AddBookmarkSearchFragment(), AddBookmarkSearchFragment.FRAG_TAG)
                .commit();
    }

    /**
     * on back handled
     */
    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(AddBookmarkResultFragment.FRAG_TAG);
        if (frag != null &&
                ((OnHandleBackPressed) frag).handleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     *
     */
    public interface OnHandleBackPressed {
        boolean handleBackPressed();
    }
}

package com.application.material.bookmarkswallet.app;

import android.app.UiModeManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.application.material.bookmarkswallet.app.fragments.AddBookmarkSearchFragment;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddBookmarkActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_layout);
        NightModeHelper.setMode(UiModeManager.MODE_NIGHT_NO, getApplicationContext());
        initActionbar();
        changeFrag();
    }

    /**
     *
     */
    private void initActionbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarId));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(getString(R.string.add_new_bookmark_title));
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void changeFrag() {
        AddBookmarkSearchFragment frag = new AddBookmarkSearchFragment();
        if (getIntent() != null)
            frag.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId, frag,
                        AddBookmarkSearchFragment.FRAG_TAG)
                .commit();
    }

//    /**
//     * on back handled
//     */
//    @Override
//    public void onBackPressed() {
//        Fragment frag = getSupportFragmentManager()
//                .findFragmentByTag(AddBookmarkResultFragment.FRAG_TAG);
//        if (frag != null &&
//                ((OnHandleBackPressed) frag).handleBackPressed()) {
//            return;
//        }
//        super.onBackPressed();
//    }

}

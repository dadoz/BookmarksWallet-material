package com.application.material.bookmarkswallet.app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.application.material.bookmarkswallet.app.fragments.AddBookmarkSearchFragment;

public class AddBookmarkActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

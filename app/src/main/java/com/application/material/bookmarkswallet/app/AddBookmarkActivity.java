package com.application.material.bookmarkswallet.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.bookmarkswallet.app.fragments.AddBookmarkResultFragment;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkSearchFragment;

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
        changeFrag();
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
//        if (((OnHandleBackPressed) getSupportFragmentManager()
//                .findFragmentByTag(AddBookmarkFragment.FRAG_TAG)).handleBackPressed()) {
//            return;
//        }
        super.onBackPressed();
    }

    /**
     *
     */
    public interface OnHandleBackPressed {
        boolean handleBackPressed();
    }
}

package com.application.material.bookmarkswallet.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.bookmarkswallet.app.fragments.AddBookmarkFragment;

public class AddBookmarkActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_layout);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                new AddBookmarkFragment(), AddBookmarkFragment.FRAG_TAG).commit();
    }

    /**
     * on back handled
     */
    @Override
    public void onBackPressed() {
        if (((OnHandleBackPressed) getSupportFragmentManager()
                .findFragmentByTag(AddBookmarkFragment.FRAG_TAG)).handleBackPressed()) {
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

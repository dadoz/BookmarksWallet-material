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
        ButterKnife.bind(this);
        onInitFragment();
    }


    /**
     * init fragment function
     */
    public void onInitFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentByTag(AddBookmarkFragment.FRAG_TAG) == null) {
            Fragment addBookmarkFrag = new AddBookmarkFragment();
            if (getIntent().getExtras() != null) {
                addBookmarkFrag.setArguments(getIntent().getExtras());
            }
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    addBookmarkFrag, AddBookmarkFragment.FRAG_TAG).commit();
        }
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

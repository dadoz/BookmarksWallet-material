package com.application.material.bookmarkswallet.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;

public class AddBookmarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onInitFragment();
    }


    /**
     * init fragment function
     */
    public void onInitFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentByTag(AddBookmarkFragment.FRAG_TAG) == null) {
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    new AddBookmarkFragment(), AddBookmarkFragment.FRAG_TAG).commit();
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

package com.application.material.bookmarkswallet.app;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkFragment;
import com.application.material.bookmarkswallet.app.fragments.BookmarkRecyclerViewFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;

/**
 * Created by davide on 05/08/15.
 */
public class AddBookmarkActivity extends AppCompatActivity
        implements OnChangeFragmentWrapperInterface {

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
        transaction.replace(R.id.fragmentContainerFrameLayoutId,
                new AddBookmarkFragment(), AddBookmarkFragment.FRAG_TAG).commit();
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
    }

    /**
     * on back handled
     */
    @Override
    public void onBackPressed() {
        Log.e("TAG", "hey");
        super.onBackPressed();
    }

}

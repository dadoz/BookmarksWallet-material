package com.application.material.bookmarkswallet.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

    private ActionbarSingleton mActionbarSingleton;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mActionbarSingleton = ActionbarSingleton.getInstance(this);
        mActionbarSingleton.initActionBar(); //must be the last one
        onInitFragment();
    }

    /**
     * init fragment function
     */
    public void onInitFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
//        Fragment frag = getSupportFragmentManager().
//                findFragmentByTag(BookmarkRecyclerViewFragment.FRAG_TAG);

//        if (backStackCnt > 0) {
//            handleBackStackEntry(transaction);
//            return;
//        }
//
//        if (backStackCnt == 0 &&
//                frag != null) {
//            transaction.replace(R.id.fragmentContainerFrameLayoutId,
//                    frag, BookmarkRecyclerViewFragment.FRAG_TAG).commit();
//            return;
//        }

        //no fragment already adedd
        transaction.add(R.id.fragmentContainerFrameLayoutId,
                new AddBookmarkFragment(), AddBookmarkFragment.FRAG_TAG).commit();
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {

    }

}

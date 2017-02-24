package com.application.material.bookmarkswallet.app;

import android.content.Intent;
import android.os.Bundle;

import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.utlis.Utils;

public class MainActivity extends BaseDrawerMenuActivity {
    private String TAG = "MainActivity";
    public static String SHARED_URL_EXTRA_KEY = "SHARED_URL_EXTRA_KEY";

    MainActivity() {
        super(R.layout.activity_main_layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //first handle frag
        if (new IntentHandler()
                .startActivityByIntent(new Intent(this, AddBookmarkActivity.class))) {
            return;
        }

        //init fragment
        onInitFragment();
    }

    /**
     * init fragment function
     */
    public void onInitFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG)
                .commit();
    }


    /**
     * handling intent by external request
     */
    public class IntentHandler {
        public boolean startActivityByIntent(Intent intent) {
            //then handleSharedIntent
            if (handleSharedIntent() == null) {
                return false;
            }

            intent.putExtras(handleSharedIntent());
            startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
            return true;
        }

        /**
         *
         */
        private Bundle handleSharedIntent() {
            if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
//            Log.e(TAG, "hey" + getIntent().getStringExtra(Intent.EXTRA_TEXT));
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
    }



}

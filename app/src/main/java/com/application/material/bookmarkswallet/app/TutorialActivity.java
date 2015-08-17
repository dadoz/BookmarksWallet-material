package com.application.material.bookmarkswallet.app;

import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.adapter.TutorialPagerAdapter;

/**
 * Created by davide on 17/07/15.
 */
public class TutorialActivity extends AppCompatActivity {

    @Bind(R.id.viewPagerId)
    ViewPager mViewPager;
    private static final int N_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.tutorial_layout);
        ButterKnife.bind(this);

        ((PagerTitleStrip) findViewById(R.id.titleStripId)).setNonPrimaryAlpha(0);
        mViewPager.setAdapter(new TutorialPagerAdapter(getSupportFragmentManager(), this, N_PAGES));
    }
}

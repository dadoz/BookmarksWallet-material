package com.application.material.bookmarkswallet.app.application;

import android.app.Application;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.flurry.android.FlurryAgent;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MaterialBookmarkApplication extends Application {
    private SparseArrayParcelable<String> searchParamsArray;

    @Override
    public void onCreate() {
        super.onCreate();

        //init flurry
        initFlurry();

        //calligraphy
        initCalligraphy();

    }

    /**
     *
     */
    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica-Neue-47-Light-Condensed.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    /**
     *
     */
    private void initFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));
    }


    /**
     *
     * @return
     */
    public SparseArrayParcelable<String> getSearchParamsArray() {
        return searchParamsArray;
    }

    /**
     *
     * @param searchParamsArray
     */
    public void setSearchParamsArray(SparseArrayParcelable<String> searchParamsArray) {
        this.searchParamsArray = searchParamsArray;
    }

}

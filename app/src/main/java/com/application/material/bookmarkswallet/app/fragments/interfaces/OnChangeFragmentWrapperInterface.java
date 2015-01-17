package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by davide on 07/11/14.
 */
public interface OnChangeFragmentWrapperInterface {
    public void changeFragment(Fragment fragment, Bundle bundle, String tag);
    public void pushCurrentFragTag(String tag);
    public void setCurrentFragTag(String tag);
    public String popCurrentFragTag();
    public String getCurrentFragTag();
    public void startActivityForResultWrapper(Class activityClassName, int requestCode, Bundle bundle);

}

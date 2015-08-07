package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;

/**
 * Created by davide on 06/08/15.
 */
public class AddBookmarkFragment extends Fragment {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private Activity mAddActivityRef;
    private ActionbarSingleton mActionbarSingleton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChangeFragmentWrapperInterface");
        }
        mAddActivityRef = activity;
        mActionbarSingleton = ActionbarSingleton.getInstance(mAddActivityRef);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.add_bookmark_fragment, container, false);
        onInitView();
        return view;
    }

    /**
     *
     */
    private void onInitView() {
        initStatusbar();
    }

    /**
     * init title - set
     */
    private void initStatusbar() {
        mActionbarSingleton.setTitle("Add new");
        mActionbarSingleton.setBackgroundColor(R.color.material_violet_500,
                R.color.material_violet_700);
    }

}

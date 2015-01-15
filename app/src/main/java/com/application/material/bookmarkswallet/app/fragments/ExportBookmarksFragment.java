package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 30/06/14.
 */
public class ExportBookmarksFragment extends Fragment {
    private View importExportView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        importExportView = inflater.inflate(R.layout.import_export_layout, null);
        return importExportView;
    }

}

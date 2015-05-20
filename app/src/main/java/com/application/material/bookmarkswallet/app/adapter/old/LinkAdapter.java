package com.application.material.bookmarkswallet.app.adapter.old;

/**
 * Created by davide on 14/01/15.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.util.ArrayList;

/**CUSTOM LAYOUT CLASS**/
public class LinkAdapter extends ArrayAdapter<Bookmark> {
    private final Fragment fragmentRef;

    public LinkAdapter(Fragment fragmentRef, int linkViewResourceId,
                             ArrayList<Bookmark> items) {
        super(fragmentRef.getActivity(), linkViewResourceId, items);
        this.fragmentRef = fragmentRef;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = inflater.inflate(R.layout.bookmark_item, null);

        TextView linkTitle = (TextView) view.findViewById(R.id.linkTitleId);
        linkTitle.setText(getItem(position).getName());

        return view;
    }
}
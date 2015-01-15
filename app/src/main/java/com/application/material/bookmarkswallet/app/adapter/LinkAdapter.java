package com.application.material.bookmarkswallet.app.adapter;

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
import com.application.material.bookmarkswallet.app.models.Link;

import java.util.ArrayList;

/**CUSTOM LAYOUT CLASS**/
public class LinkAdapter extends ArrayAdapter<Link> {
    private final Fragment fragmentRef;

    public LinkAdapter(Fragment fragmentRef, int linkViewResourceId,
                             ArrayList<Link> items) {
        super(fragmentRef.getActivity(), linkViewResourceId, items);
        this.fragmentRef = fragmentRef;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = inflater.inflate(R.layout.link_row, null);

        TextView linkTitle = (TextView) view.findViewById(R.id.link_title_id);
        linkTitle.setText(getItem(position).getLinkName());

        return view;
    }
}
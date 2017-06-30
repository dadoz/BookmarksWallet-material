package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Setting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;
    private final WeakReference<CompoundButton.OnCheckedChangeListener> listener;
    private final int type;
    private final static String TAG = "SettingListAdapter";

    /**
     *
     * @param context
     * @param resource
     * @param settingList
     * @param lst
     */
    public SettingListAdapter(int type, Context context, int resource, ArrayList<Setting> settingList,
                              WeakReference<CompoundButton.OnCheckedChangeListener> lst) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.listener = lst;
        this.type = type;
    }

    /**
     * TODO refactoring
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.setting_item, parent, false);
        }
        Setting settingObj = settingList.get(position);
        ListViewHolder viewHolder = new ListViewHolder(convertView);

//        if (type == 0 &&
//                position == 1)
//            viewHolder.itemView.setOnClickListener(null);

        //to handle type of button
        viewHolder.switchCompat.setTag(settingObj.getType() != null ? settingObj.getType().name() : null);
        viewHolder.label.setText(settingObj.getLabel());

        viewHolder.description.setVisibility(settingObj.getDescription() == null ? View.GONE : View.VISIBLE);
        viewHolder.description.setText(settingObj.getDescription());

        viewHolder.switchCompat.setVisibility(settingObj.isSwitchVisible() ? View.VISIBLE : View.GONE);
        viewHolder.switchCompat.setChecked(settingObj.isSwitchVisible() && settingObj.isSwitchCheck());
        viewHolder.switchCompat.setOnCheckedChangeListener(listener.get());
        return convertView;
    }

    class ListViewHolder {
        public final SwitchCompat switchCompat;
        public final TextView label;
        public final TextView description;

        ListViewHolder(View convertView) {
            label = (TextView) convertView.findViewById(R.id.settingLabelTextId);
            description = ((TextView) convertView.findViewById(R.id.settingDescriptionTextId));
            switchCompat = ((SwitchCompat) convertView.findViewById(R.id.settingSwitchId));
        }
    }
}

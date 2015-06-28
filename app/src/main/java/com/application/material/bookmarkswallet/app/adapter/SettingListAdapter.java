package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.models.Setting;

import java.util.ArrayList;

/**
 * Created by davide on 21/06/15.
 */

public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;
    private final Context context;
    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private String TAG = "SettingListAdapter";

    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList, SettingsFragment settingsFragment) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.context = context;
        this.mCheckedChangeListener = settingsFragment;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Setting settingObj = settingList.get(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.setting_item, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(R.id.settingLabelTextId);
        TextView description = ((TextView) convertView.findViewById(R.id.settingDescriptionTextId));
        SwitchCompat switchCompat = ((SwitchCompat) convertView.findViewById(R.id.settingSwitchId));

        label.setText(settingObj.getLabel());

        description.setVisibility(settingObj.getDescription() == null ? View.GONE : View.VISIBLE);
        description.setText(settingObj.getDescription());

        switchCompat.setVisibility(settingObj.isSwitchVisible() ? View.VISIBLE : View.GONE);
        switchCompat.setChecked(settingObj.isSwitchVisible() && settingObj.isSwitchCheck());
        switchCompat.setOnCheckedChangeListener(mCheckedChangeListener);
        return convertView;
    }
}

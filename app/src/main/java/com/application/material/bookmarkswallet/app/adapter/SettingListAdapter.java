package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Setting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;
    private final Context context;
    private final WeakReference<CompoundButton.OnCheckedChangeListener> listener;
    private String TAG = "SettingListAdapter";

    /**
     *
     * @param context
     * @param resource
     * @param settingList
     * @param lst
     */
    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList,
                              WeakReference<CompoundButton.OnCheckedChangeListener> lst) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.context = context;
        this.listener = lst;
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
        Setting settingObj = settingList.get(position);
        if (convertView == null) {
            convertView = ((LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.setting_item, parent, false);
        }

        TextView label = (TextView) convertView.findViewById(R.id.settingLabelTextId);
        TextView description = ((TextView) convertView.findViewById(R.id.settingDescriptionTextId));
        SwitchCompat switchCompat = ((SwitchCompat) convertView.findViewById(R.id.settingSwitchId));

        //to handle type of button
        switchCompat.setTag(settingObj.getType() != null ? settingObj.getType().name() : null);
        label.setText(settingObj.getLabel());

        description.setVisibility(settingObj.getDescription() == null ? View.GONE : View.VISIBLE);
        description.setText(settingObj.getDescription());

        if (settingObj.getType() != null &&
                settingObj.getType().equals(SharedPrefHelper.SharedPrefKeysEnum.CLOUD_SYNC)) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_800));
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_600));
            switchCompat.setEnabled(false);
            return convertView;
        }

        switchCompat.setVisibility(settingObj.isSwitchVisible() ? View.VISIBLE : View.GONE);
        switchCompat.setChecked(settingObj.isSwitchVisible() && settingObj.isSwitchCheck());
        switchCompat.setOnCheckedChangeListener(listener.get());

        if (position == 4) //build
            convertView.setOnClickListener(null);

        return convertView;
    }
}

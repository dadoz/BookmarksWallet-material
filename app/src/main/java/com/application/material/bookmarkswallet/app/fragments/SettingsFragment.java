package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.willowtreeapps.saguaro.android.Saguaro;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "SettingsFragment_FRAG";
    public static String SETTINGS_TITLE = "Settings";
    private ActionbarHelper actionbarHelper;
    private View mSettingsView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionbarHelper = ActionbarHelper.getInstance(new WeakReference<>(getContext()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSettingsView = inflater.inflate(R.layout.settings_layout, null);
        initActionbar();
        onInitView();
        return mSettingsView;
    }

    /**
     * init view
     */
    private void onInitView() {
        ArrayAdapter<Setting> adapter = new SettingListAdapter(getActivity().getBaseContext(),
                R.layout.setting_item, getSettingList(), this);
        ListView listView = (ListView) mSettingsView.findViewById(R.id.settingsListId);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
    }

    /**
     * init actionbar
     */
    private void initActionbar() {
        actionbarHelper.setTitle(SETTINGS_TITLE);
        actionbarHelper.setElevation(4);
        actionbarHelper.setDisplayHomeEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                goToMarket();
                break;
            case 3:
                startActivity(Saguaro.getSendFeedbackIntent(getActivity()));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //TODO no case since I've only one toggle button
        SharedPrefHelper.getInstance(new WeakReference<>(getActivity().getApplicationContext()))
                .setValue(SEARCH_URL_MODE, isChecked);
    }


    /**
     * set data for setting list
     * @return
     */
    public ArrayList<Setting> getSettingList() {
        ArrayList<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null,
                View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label),
                getResources().getString(R.string.setting_url_search_description),
                View.VISIBLE, Utils.getSearchOnUrlEnabledFromSharedPref(new WeakReference<>(getActivity()
                .getApplicationContext()))));
        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                View.VISIBLE, Utils.getSearchOnUrlEnabledFromSharedPref(new WeakReference<>(getActivity()
                .getApplicationContext()))));
        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label),
                null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label),
                Utils.getVersionName(new WeakReference<>(getContext())), View.GONE, false));

        return settingList;
    }

    /**
     * go to market
     */
    private void goToMarket() {
        try {
            startActivity(Utils.getMarketIntent(0, new WeakReference<>(getContext())));
        } catch (ActivityNotFoundException e) {
            startActivity(Utils.getMarketIntent(1, new WeakReference<>(getContext())));
        }
    }
}

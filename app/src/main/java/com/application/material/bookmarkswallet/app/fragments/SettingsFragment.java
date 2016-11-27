package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.ActivityNotFoundException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.willowtreeapps.saguaro.android.Saguaro;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "SettingsFragment_FRAG";
    public static String SETTINGS_TITLE = "Settings";
    private ActionbarHelper actionbarHelper;
    private View mSettingsView;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionbarHelper = ActionbarHelper.getInstance(new WeakReference<>(context));
        sharedPrefHelper = SharedPrefHelper.getInstance(new WeakReference<>(context));
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
        ArrayAdapter<Setting> adapter = new SettingListAdapter(getContext(),
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
        Log.e("TAG", "view" + view.getId());
        switch (position) {
            case 0:
                goToMarket();
                break;
            case 4:
                startActivity(Saguaro.getSendFeedbackIntent(getActivity()));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final String urlSearchMode = getResources().getString(R.string.setting_url_search_label);
        SharedPrefHelper.SharedPrefKeysEnum value = buttonView.getTag().equals(urlSearchMode) ?
                SEARCH_URL_MODE : NO_FAVICON_MODE;
        SharedPrefHelper.getInstance(new WeakReference<>(getActivity().getApplicationContext()))
                .setValue(value, isChecked);
    }


    /**
     * set data for setting list
     * @return
     */
    public ArrayList<Setting> getSettingList() {
        ArrayList<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null,
                null, View.GONE, false));

        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label),
                getResources().getString(R.string.setting_url_search_description),
                SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, View.VISIBLE, (Boolean) sharedPrefHelper
                    .getValue(SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, View.VISIBLE, (Boolean) sharedPrefHelper
                .getValue(SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_cloud_sync),
                getResources().getString(R.string.setting_cloud_sync_description),
                SharedPrefHelper.SharedPrefKeysEnum.CLOUD_SYNC,
                View.VISIBLE, (Boolean) sharedPrefHelper
                .getValue(SharedPrefHelper.SharedPrefKeysEnum.CLOUD_SYNC, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label),
                null, null, View.GONE, false));

        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label),
                Utils.getVersionName(new WeakReference<>(getContext())),
                null, View.GONE, false));

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

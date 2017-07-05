package com.application.material.bookmarkswallet.app.fragments;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by davide on 12/06/2017.
 */
public class SettingsFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener {
    public static String FRAG_TAG = "SettingsFragment";
    @BindView(R.id.generalSettingListViewId)
    public ListView generalSettingListView;
    @BindView(R.id.extraFeatureSettingListViewId)
    public ListView extraFeatureSettingListView;
    private Unbinder unbinder;

    {
        layoutId = R.layout.settings_layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        onInitView();
    }

    /**
     * init view
     */
    private void onInitView() {
        //general
        ArrayAdapter<Setting> adapter = new SettingListAdapter(0, getContext(),
                R.layout.setting_item, getGeneralSettingList(), this);
        generalSettingListView.setAdapter(adapter);
        generalSettingListView.setOnItemClickListener(this);

        //extra feature
        ArrayAdapter<Setting> adapter2 = new SettingListAdapter(1, getContext(),
                R.layout.setting_item, getSettingList(), this);
        extraFeatureSettingListView.setAdapter(adapter2);
        extraFeatureSettingListView.setOnItemClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("TAG", "view" + view.getId());
        switch (position) {
            case 0:
                goToMarket();
                break;
        }
    }

    /**
     * TODO modify to get also type on fx - add customer lst
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getTag() != null) {
            sharedPrefHelper.setValue(buttonView.getTag().toString(), isChecked);
//            if (SharedPrefHelper.isNightModeTag(buttonView.getTag().toString())) {
//                NightModeHelper.getInstance(getActivity()).toggle();
//            }
        }
    }


    /**
     * mv PRESENTER
     * set data for setting list
     * @return
     */
    public ArrayList<Setting> getSettingList() {
        ArrayList<Setting> settingList = new ArrayList<>();

        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label),
                getResources().getString(R.string.setting_url_search_description),
                SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, false)));

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

    public ArrayList<Setting> getGeneralSettingList() {
        ArrayList<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null,
                null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label),
                Utils.getVersionName(new WeakReference<>(getContext())),
                null, View.GONE, false));

        return settingList;

    }
}

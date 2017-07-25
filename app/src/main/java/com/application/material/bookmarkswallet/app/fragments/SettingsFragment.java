package com.application.material.bookmarkswallet.app.fragments;

import android.app.UiModeManager;
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
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE;

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
    private boolean isNightMode;

    {
        layoutId = R.layout.settings_layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        isNightMode = new NightModeHelper(getContext()).isNightMode();

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
        }
        handleCustomActionInterceptor(buttonView);
    }

    /**
     *
     * @param buttonView
     */
    private void handleCustomActionInterceptor(CompoundButton buttonView) {
        if (buttonView.getTag() != null && buttonView.getTag().equals(NIGHT_MODE.name()))
            new NightModeHelper(getActivity()).setMode(isNightMode ? UiModeManager.MODE_NIGHT_NO : UiModeManager.MODE_NIGHT_YES);
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
                SEARCH_URL_MODE, View.VISIBLE, sharedPrefHelper.getBoolValue(SEARCH_URL_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                NO_FAVICON_MODE, View.VISIBLE, sharedPrefHelper.getBoolValue(NO_FAVICON_MODE, false)));

        settingList.add(new Setting(getResources().getString(isNightMode ? R.string.setting_night_mode : R.string.setting_day_mode),
                getResources().getString(R.string.setting_night_mode_description),
                NIGHT_MODE, View.VISIBLE, sharedPrefHelper.getBoolValue(NIGHT_MODE, false)));

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

    /**
     *
     * @return
     */
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

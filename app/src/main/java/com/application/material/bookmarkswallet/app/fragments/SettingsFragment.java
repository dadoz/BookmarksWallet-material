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
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.willowtreeapps.saguaro.android.Saguaro;

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
    @BindView(R.id.settingsListId)
    public ListView listView;
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
        ArrayAdapter<Setting> adapter = new SettingListAdapter(getContext(),
                R.layout.setting_item, getSettingList(),
                new WeakReference<>(this));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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
            case 3:
                startActivity(Saguaro.getSendFeedbackIntent(getContext()));
                break;
        }
    }

    /**
     * TODO modify to get also type on fx
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sharedPrefHelper.setValue(SharedPrefHelper.SharedPrefKeysEnum.valueOf(buttonView.getTag().toString()), isChecked);
        if (SharedPrefHelper.SharedPrefKeysEnum.valueOf((String) buttonView.getTag()) == SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE) {
            NightModeHelper.getInstance(getActivity()).toggle();
        }
    }


    /**
     * mv PRESENTER
     * set data for setting list
     * @return
     */
    public ArrayList<Setting> getSettingList() {
        ArrayList<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null,
                null, View.GONE, false));

        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label),
                getResources().getString(R.string.setting_url_search_description),
                SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE, false)));

//        settingList.add(new Setting(getResources().getString(R.string.setting_cloud_sync),
//                getResources().getString(R.string.setting_cloud_sync_description),
//                SharedPrefHelper.SharedPrefKeysEnum.CLOUD_SYNC,
//                View.VISIBLE, (Boolean) sharedPrefHelper
//                .getValue(SharedPrefHelper.SharedPrefKeysEnum.CLOUD_SYNC, false)));

//        settingList.add(new Setting(getResources().getString(R.string.setting_night_mode),
//                getResources().getString(R.string.setting_night_mode_description),
//                SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE,
//                View.VISIBLE, (int) sharedPrefHelper.getValue(SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE,
//                AppCompatDelegate.MODE_NIGHT_AUTO) == AppCompatDelegate.MODE_NIGHT_YES));

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

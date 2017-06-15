package com.application.material.bookmarkswallet.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.willowtreeapps.saguaro.android.Saguaro;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    private String TAG = "MainActivity";
    private SharedPrefHelper sharedPrefHelper;
    @BindView(R.id.settingsListId)
    ListView listView;
    private Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        unbinder = ButterKnife.bind(this);

        NightModeHelper.getInstance(this).setConfigurationMode();
        sharedPrefHelper = SharedPrefHelper.getInstance(new WeakReference<>(getApplicationContext()));

        //mv
        initActionbar();
        onInitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * init view
     */
    private void onInitView() {
        ArrayAdapter<Setting> adapter = new SettingListAdapter(getApplicationContext(),
                R.layout.setting_item, getSettingList(),
                new WeakReference<CompoundButton.OnCheckedChangeListener>(this));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * init actionbar
     */
    private void initActionbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarId));
        getSupportActionBar().setTitle(getString(R.string.setting_actionbar_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(10f);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("TAG", "view" + view.getId());
        switch (position) {
            case 0:
                goToMarket();
                break;
            case 5:
                startActivity(Saguaro.getSendFeedbackIntent(this));
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
        sharedPrefHelper.setValue(SharedPrefKeysEnum.valueOf(buttonView.getTag().toString()), isChecked);
        if (SharedPrefKeysEnum.valueOf((String) buttonView.getTag()) == SharedPrefKeysEnum.NIGHT_MODE) {
            NightModeHelper.getInstance(this).toggle();
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
                SharedPrefKeysEnum.SEARCH_URL_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefKeysEnum.SEARCH_URL_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_no_favicon),
                getResources().getString(R.string.setting_no_favicon_description),
                SharedPrefKeysEnum.NO_FAVICON_MODE, View.VISIBLE,
                (Boolean) sharedPrefHelper.getValue(SharedPrefKeysEnum.NO_FAVICON_MODE, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_cloud_sync),
                getResources().getString(R.string.setting_cloud_sync_description),
                SharedPrefKeysEnum.CLOUD_SYNC,
                View.VISIBLE, (Boolean) sharedPrefHelper
                .getValue(SharedPrefKeysEnum.CLOUD_SYNC, false)));

        settingList.add(new Setting(getResources().getString(R.string.setting_night_mode),
                getResources().getString(R.string.setting_night_mode_description),
                SharedPrefKeysEnum.NIGHT_MODE,
                View.VISIBLE, (int) sharedPrefHelper.getValue(SharedPrefKeysEnum.NIGHT_MODE,
                        AppCompatDelegate.MODE_NIGHT_AUTO) == AppCompatDelegate.MODE_NIGHT_YES));

        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label),
                null, null, View.GONE, false));

        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label),
                Utils.getVersionName(new WeakReference<>(getApplicationContext())),
                null, View.GONE, false));

        return settingList;
    }

    /**
     * go to market
     */
    private void goToMarket() {
        try {
            startActivity(Utils.getMarketIntent(0, new WeakReference<>(getApplicationContext())));
        } catch (ActivityNotFoundException e) {
            startActivity(Utils.getMarketIntent(1, new WeakReference<>(getApplicationContext())));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

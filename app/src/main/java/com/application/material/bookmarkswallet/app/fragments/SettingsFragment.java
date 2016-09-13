package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.ActionsSingleton;
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
    private ActionbarSingleton mActionbarSingleton;
    private ActionsSingleton mBookmarkActionSingleton;
    private View mSettingsView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActionbarSingleton = ActionbarSingleton.getInstance(new WeakReference<>(getContext()));
        mBookmarkActionSingleton = ActionsSingleton.getInstance(new WeakReference<>(getContext()));
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
        mActionbarSingleton.setTitle(SETTINGS_TITLE);
        mActionbarSingleton.setDisplayHomeEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                goToMarket();
                break;
            case 2:
                deleteAllBookmarksDialog();
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
     * ret version name
     * @return
     */
    public String getVersionName() {
        String versionName = "0.0";
        try {
            versionName = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
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
        settingList.add(new Setting(getResources().getString(R.string.setting_delete_all_label),
                getResources().getString(R.string.setting_delete_all_description), View.GONE, true));
        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label),
                null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label),
                getVersionName(), View.GONE, false));

        return settingList;
    }

    /**
     * go to market
     */
    private void goToMarket() {
        try {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getActivity().getPackageName()));
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            getActivity().getPackageName())));
        }
    }

    /**
     *
     * @return
     */
    public boolean deleteAllBookmarks() {
        mBookmarkActionSingleton.deleteAllAction();
        return true;
    }

    /**
     * TODO refactor
     */
    private void deleteAllBookmarksDialog() {
        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomLollipopDialogStyle)
                .setTitle(getString(R.string.delete_dialog_label))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete))
                .setPositiveButton(getString(R.string.delete_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("TAG", "---" + which);
                        deleteAllBookmarks();
                        showErrorMessage();
                    }
                })
                .setNegativeButton(getString(R.string.dismiss_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("TAG", "---" + which);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getContext(), R.color.red_400));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getContext(), R.color.blue_grey_900));
    }

    /**
     *
     */
    private void showErrorMessage() {
        Snackbar snackbar = Snackbar.make(mSettingsView,
                getString(R.string.settings_all_bookmarks_has_been_deleted), Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_400));
        snackbar.show();

    }
}

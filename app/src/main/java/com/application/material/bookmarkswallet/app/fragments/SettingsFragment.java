package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
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
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.willowtreeapps.saguaro.android.Saguaro;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "SettingsFragment_FRAG";
    public static String SETTINGS_TITLE = "Settings";
    private Activity mActivityRef;
    private ActionbarSingleton mActionbarSingleton;
    private BookmarkActionSingleton mBookmarkActionSingleton;
    private View mSettingsView;
    private boolean mSearchOnUrlEnabled = false;
    private boolean mFindIconAuto = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (! (context instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnChangeFragmentWrapperInterface");
        }

        mActivityRef = (Activity) context;
        mActionbarSingleton = ActionbarSingleton.getInstance(new WeakReference<>(getContext()));
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(new WeakReference<>(getContext()));
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
            case 3:
                deleteAllBookmarksDialog();
                break;
            case 4:
                startActivity(Saguaro.getSendFeedbackIntent(mActivityRef));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(mActivityRef, "Feature will come soon!", Toast.LENGTH_SHORT).show();
    }

    /**
     * ret version name
     * @return
     */
    public String getVersionName() {
        String versionName = "0.0";
        try {
            versionName = mActivityRef.getPackageManager().getPackageInfo(mActivityRef.getPackageName(), 0).versionName;
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
        settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label), "extend bookmark search by URL string even that only search by title.", View.VISIBLE, mSearchOnUrlEnabled));
        settingList.add(new Setting(getResources().getString(R.string.setting_find_icon_label), "find bookmark's icon automatically.", View.VISIBLE, mFindIconAuto));
        settingList.add(new Setting(getResources().getString(R.string.setting_delete_all_label), "clear all your stored bookmarks.", View.GONE, true));
        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label), null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label), getVersionName(), View.GONE, false));

        return settingList;
    }

    /**
     * go to market
     */
    private void goToMarket() {
        try {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + mActivityRef.getPackageName()));
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mActivityRef.getPackageName())));
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
        Dialog dialog = new AlertDialog
                .Builder(mActivityRef, R.style.CustomLollipopDialogStyle)
                .setTitle("Delete bookmarks!")
                .setMessage("Are you sure you want to delete all your bookmarks?")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllBookmarks();
                        Toast.makeText(mActivityRef,
                                "All your bookmarks has been deleted with success", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}

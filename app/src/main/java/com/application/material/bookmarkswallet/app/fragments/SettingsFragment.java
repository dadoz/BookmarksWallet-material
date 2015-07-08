package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
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
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.google.android.gms.ads.AdRequest;
import com.willowtreeapps.saguaro.android.Saguaro;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SettingsFragment";
    public static String FRAG_TAG = "SettingsFragment_FRAG";
	public static String TITLE = "Settings";
	private Activity mActivityRef;
    private ActionbarSingleton mActionbarSingleton;
    private RecyclerViewActionsSingleton mRvActionsSingleton;

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}

		mActivityRef = activity;
		mActionbarSingleton = ActionbarSingleton.getInstance(mActivityRef);
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);

    }

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.settings_layout, null);
        mActionbarSingleton.setTitle(TITLE);
        mActionbarSingleton.setDisplayHomeEnabled(true);

        //load ads
        AdView adView = (AdView) settingsView.findViewById(R.id.adView2Id);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        return settingsView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        ListView settingsList = (ListView) getView().findViewById(R.id.settingsListId);

		ArrayList<Setting> settingList = new ArrayList<Setting>();
		settingList.add(new Setting(getResources().getString(R.string.setting_rate_label), null, View.GONE, false));
        settingList.add(new Setting(getResources().getString(R.string.setting_url_search_label), "extend bookmark search by URL string even that only search by title.", View.VISIBLE, mRvActionsSingleton.isSearchOnUrlEnabled()));
        settingList.add(new Setting(getResources().getString(R.string.setting_delete_all_label), "clear all your stored bookmarks.", View.GONE, true));
        settingList.add(new Setting(getResources().getString(R.string.setting_feedback_label), null, View.GONE, false));

        String versionName = "0.0";
        try {
            versionName = mActivityRef.getPackageManager().getPackageInfo(mActivityRef.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        settingList.add(new Setting(getResources().getString(R.string.setting_build_version_label), versionName, View.GONE, false));
		ArrayAdapter<Setting> adapter = new SettingListAdapter(getActivity().getBaseContext(),
				R.layout.setting_item, settingList, this);

        settingsList.setOnItemClickListener(this);
        settingsList.setAdapter(adapter);
	}


	private void openDeleteAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef, R.style.CustomLollipopDialogStyle);
		Dialog dialog = builder
                .setTitle("Delete bookmarks!")
				.setMessage("Are you sure you want to delete all your bookmarks?")
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteAllLinks();
						Toast.makeText(mActivityRef,
								"All your bookmarks has been deleted with success", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();					}
				})
				.create();
		dialog.show();
	}

	public boolean deleteAllLinks() {
        mRvActionsSingleton.deleteBookmarksList();
        return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
                Uri uri = Uri.parse("market://details?id=" + mActivityRef.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mActivityRef.getPackageName())));
                }
                break;
            case 2:
                openDeleteAllDialog();
                break;
            case 3:
                startActivity(Saguaro.getSendFeedbackIntent(mActivityRef));
                break;
		}
	}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mRvActionsSingleton.setSearchOnUrlEnabled(isChecked);
        Log.e(TAG, "hey " + isChecked);
    }
}

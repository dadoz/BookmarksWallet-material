package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.SettingListAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Setting;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.suredigit.inappfeedback.FeedbackDialog;
import io.realm.Realm;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SettingsFragment";
    public static String FRAG_TAG = "SettingsFragment_FRAG";
	public static String TITLE = "Settings";
	private Activity mActivityRef;
	private View settingsView;
	private ListView mSettingsList;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;
    private Realm mRealm;
    private RecyclerViewActionsSingleton mRvActionsSingleton;
    private FeedbackDialog mFeedBackDialog;

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}
//		if (! (activity instanceof OnInitActionBarInterface)) {
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnInitActionBarInterface");
//		}
		mActivityRef = activity;
		mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
		mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);
        mFeedBackDialog = new FeedbackDialog(mActivityRef,
                mActivityRef.getResources().getString(R.string.ANDROID_FEEDBACK_KEY));

    }

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		settingsView = inflater.inflate(R.layout.settings_layout, null);
        mActionBarHandlerSingleton.setTitle(TITLE);
        mActionBarHandlerSingleton.setDisplayHomeEnabled(true);
        mRealm = Realm.getInstance(mActivityRef);

        return settingsView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mSettingsList = (ListView) getView().findViewById(R.id.settingsListId);

		ArrayList<Setting> settingList = new ArrayList<Setting>();
		settingList.add(new Setting("Rate it!", View.GONE, false));
        settingList.add(new Setting("Search on URL enabled", View.VISIBLE, mRvActionsSingleton.getSearchOnUrlEnabled()));
        settingList.add(new Setting("Delete all bookmarks!", View.GONE, true));
        settingList.add(new Setting("Send a feedback", View.GONE, false));
        //add switchCompat v7 on sm option
		//I'm using the android std item layout to render listview
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
//				android.R.layout.simple_list_item_1, settingsNameList);
		ArrayAdapter<Setting> adapter = new SettingListAdapter(getActivity().getBaseContext(),
				R.layout.setting_item, settingList, this);

		mSettingsList.setOnItemClickListener(this);

		mSettingsList.setAdapter(adapter);
	}


	private void openDeleteAllDialog() {
        Resources res = mActivityRef.getResources();
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef);
		Dialog dialog = builder.setTitle("Delete bookmarks!").
				setMessage("Are you sure you want to delete all your bookmarks?").
				setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteAllLinks();
						Toast.makeText(mActivityRef,
								"All your bookmarks has been deleted with success", Toast.LENGTH_SHORT).show();
					}
				}).
				setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();					}
				}).
				create();
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
                mFeedBackDialog.show();
                break;
		}
	}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mRvActionsSingleton.setSearchOnUrlEnabled(isChecked);
        Log.e(TAG, "hey " + isChecked);
    }
}

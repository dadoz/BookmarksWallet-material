package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.suredigit.inappfeedback.FeedbackDialog;
import io.realm.Realm;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {
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

		ArrayList<String> settingsNameList = new ArrayList<String>();
//		settingsNameList.add("Remove ads");
		settingsNameList.add("Rate it!");
		settingsNameList.add("Delete all bookmarks!");
		settingsNameList.add("Send a feedback");

		//I'm using the android std item layout to render listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
				android.R.layout.simple_list_item_1, settingsNameList);

		mSettingsList.setOnItemClickListener(this);
		mSettingsList.setAdapter(adapter);
	}


	private void openDeleteAllDialog() {
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
			case 1:
                openDeleteAllDialog();
				break;
            case 2:
                mFeedBackDialog.show();
                break;
		}
	}
}

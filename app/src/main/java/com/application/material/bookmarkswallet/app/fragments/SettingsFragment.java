package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.dbAdapter.DbConnector;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {

	public static String FRAG_TAG = "SettingsFragment_FRAG";
	private Activity mActivityRef;
	private View settingsView;
	private DbConnector dbConnector;
	private ListView mSettingsList;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}
		if (! (activity instanceof OnInitActionBarInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnInitActionBarInterface");
		}
		mActivityRef = activity;
		dbConnector = DbConnector.getInstance(mActivityRef);

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		settingsView = inflater.inflate(R.layout.settings_layout, null);

		Toolbar toolbar = (Toolbar) settingsView .findViewById(R.id.toolbarId);
		try {
			((AddBookmarkActivity) mActivityRef).initActionBar(toolbar, "Settings");
		} catch (ClassCastException e) {
			((MainActivity) mActivityRef).initActionBar(toolbar, "Settings");
		}

		return settingsView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mSettingsList = (ListView) getView().findViewById(R.id.settingsListId);

		ArrayList<String> settingsNameList = new ArrayList<String>();
		settingsNameList.add("Remove ads");
		settingsNameList.add("Rate it!");
		settingsNameList.add("Delete all bookmarks!");
		settingsNameList.add("Send a feedback");


		//I'm using the android std item layout to render listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
				android.R.layout.simple_list_item_1, settingsNameList);

		mSettingsList.setOnItemClickListener(this);
		mSettingsList.setAdapter(adapter);
	}


	private void showDeleteAllDialog() {
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
//		with dialog
//		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).removeAll();
		return dbConnector.deleteAllLinks();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
			case 1:
			case 3:
				Toast.makeText(mActivityRef, "Still not implemented", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				showDeleteAllDialog();
				break;
		}
	}
}

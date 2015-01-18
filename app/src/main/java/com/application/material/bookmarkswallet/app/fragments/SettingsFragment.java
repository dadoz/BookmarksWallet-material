package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

	public static String FRAG_TAG = "SettingsFragment_FRAG";
	private Activity activityRef;
	private View settingsView;

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
		activityRef = activity;

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		settingsView = inflater.inflate(R.layout.settings_layout, null);

		Toolbar toolbar = (Toolbar) settingsView .findViewById(R.id.toolbarId);
		try {
			((AddBookmarkActivity) activityRef).initActionBar(toolbar, "Settings");
		} catch (ClassCastException e) {
			((MainActivity) activityRef).initActionBar(toolbar, "Settings");
		}

		return settingsView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListView settingsList = (ListView) getView().findViewById(R.id.settingsListId);

		ArrayList<String> settingsNameList=new ArrayList<String>();
		settingsNameList.add("Remove ads");
		settingsNameList.add("Rate it!");
		settingsNameList.add("Send a feedback");

		//I'm using the android std item layout to render listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,settingsNameList);
		settingsList.setAdapter(adapter);
	}


}

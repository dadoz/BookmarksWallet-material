package com.application.material.bookmarkswallet.app.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.application.material.bookmarkswallet.app.R;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_layout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListView settingsList = (ListView)getView().findViewById(R.id.settingsListId);

		ArrayList<String> settingsNameList=new ArrayList<String>();
		settingsNameList.add("Setting 1");
		settingsNameList.add("Setting 2");
		settingsNameList.add("Setting 3");
		settingsNameList.add("Setting 4");
		settingsNameList.add("Setting 5");
		
		//I'm using the android std item layout to render listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,settingsNameList);
		settingsList.setAdapter(adapter);
	}


}

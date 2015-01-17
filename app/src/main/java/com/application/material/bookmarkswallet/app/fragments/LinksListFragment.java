package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.adapter.LinkAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.R;
import com.getbase.floatingactionbutton.FloatingActionButton;


public class LinksListFragment extends Fragment
		implements View.OnClickListener, ListView.OnItemClickListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	public DbAdapter db;
	private MainActivity mainActivityRef;
	@InjectView(R.id.linksListId) ListView linksListView;
	@InjectView(R.id.addLinkButtonId)
	FloatingActionButton addLinkButton;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}
		mainActivityRef =  (MainActivity) activity;
		db = new DbAdapter(getActivity());

//		dataApplication = (DataApplication) addActivityRef.getApplication();
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstance) {
		View addReviewView = inflater.inflate(R.layout.links_list_layout,
				container, false);
		ButterKnife.inject(this, addReviewView);

//		setHasOptionsMenu(true);
		onInitView();
		return addReviewView;
	}

	private void onInitView() {
		ArrayList linkListTest = getLinkListMockup();
		LinkAdapter linkAdapter = new LinkAdapter(this, R.layout.link_row,
				linkListTest);
		linksListView.setAdapter(linkAdapter);
		linksListView.setOnItemClickListener(this);
		addLinkButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.addLinkButtonId:
				mainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkFragment.ADD_REQUEST, null);
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final String linkUrlFinal = ((Link) linksListView.getAdapter().getItem(position)).getLinkUrl();
		openLinkOnBrowser(linkUrlFinal);
		openLinkOnBrowser(linkUrlFinal);
		Toast.makeText(mainActivityRef, "hey openLink in browser", Toast.LENGTH_SHORT).show();
	}


	public void openLinkOnBrowser(String linkUrl){
		try {
			if(! checkURL(linkUrl)) {
				Toast.makeText(mainActivityRef, "your URL is wrong "
						+ linkUrl, Toast.LENGTH_SHORT).show();
				return;
			}

			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(linkUrl));
			getActivity().startActivity(browserIntent);

		} catch(Exception e) {
			Log.e(TAG, "error - " + e);
			Toast.makeText(mainActivityRef, "I cant load your URL "
					+ e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public boolean deleteLink(Link linkObj, ListView linksListView,
							  boolean isNetworkAvailable) {
//		if(!isNetworkAvailable){
//			NetworkNotAvailableDialog m = new NetworkNotAvailableDialog();
//	        m.show(getFragmentManager(), "NetworkNotAvailableDialog");
//			return false;
//		}
//		DeleteLinkDialog m = new DeleteLinkDialog();
//        m.show(getFragmentManager(), "DeleteLinkDialog");
		return true;
	}

	public boolean deleteAllLinks(){
//		DeleteAllLinksDialog m = new DeleteAllLinksDialog();
//        m.show(getFragmentManager(), "DeleteLinkDialog");
		return true;
	}

	public boolean editLink(){
//		EditLinkDialog m = new EditLinkDialog();
//        m.show(getFragmentManager(), "EditLinkDialog");
		return true;
	}

	//TODO TO BE IMPLEMENTED
	private boolean checkURL(String linkUrl) {
		return true;
	}

//    public void onInitView(){
//    	final ListView linksListView = (ListView)getActivity().findViewById(R.id.linksListId);
//
//    	ArrayList<Link> linksDataList = DbConnector.getLinksWrappLocalDb(db);
//    	if(linksDataList==null){
//    		Log.d(TAG, "set list from JSON data");
//    		try{
//	    		//TODO change iconPath on DB
//				linksDataList = DbConnector.getLinksListFromJSONData();
//				for(Link link:linksDataList)
//					DbConnector.insertLinkWrappLocalDb(db, link, false);
//
//	    	}catch(Exception e){
//	    		Log.e(TAG, "error - " + e);
//	    	}
//    	}
//    	if(linksDataList==null){
//    		//TODO handle empty - need to be refreshed
//    		toastMessageWrapper("empty List - img");
//
//    		//TEST
//    		Link emptyLink=new Link(Utils.EMPTY_LINKID, Utils.EMPTY_STRING, "Empty list - sorry",
//    				Utils.EMPTY_STRING, Utils.EMPTY_LINKID, Utils.EMPTY_STRING, false);
//    		linksDataList=new ArrayList<Link>();
//    		linksDataList.add(emptyLink);
//    	}
//
//    	Collections.reverse(linksDataList);
//    	ArrayAdapter<Link> adapter=new LinkCustomAdapter(getActivity(), R.layout.link_row, linksDataList);
//		linksListView.setAdapter(adapter);
//    	Utils.setLinksList(linksDataList);
//    }



	/**SHARE LINK***/
//	public boolean shareLink(Link linkObj,MenuItem item){
//		TODO need to be fixed - it doesnt work
//        ShareActionProvider actionProvider = (ShareActionProvider) item.getActionProvider();
//        if(actionProvider==null){
//        	Log.d(TAG, "providerNull");
//        	return false;
//        }
//        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.
//        actionProvider.setShareIntent(createShareIntent(linkObj.getLinkUrl()));
//		return true;
//	}

//    private Intent createShareIntent(String uri) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        return shareIntent;
//    }



    public ArrayList<Link> getLinkListMockup() {
    	ArrayList<Link> linksDataList = new ArrayList<Link>();
    	ArrayList<String> linksUrlArray = new ArrayList<String>();
		boolean deletedLinkFlag = false;

    	linksUrlArray.add("heavy metal1");
		linksUrlArray.add("pop1");
		linksUrlArray.add("underground");
		linksUrlArray.add("heavy metal");
		linksUrlArray.add("underground");
		linksUrlArray.add("hey_ure_fkin_my_shitty_dog_are_u_sure_u_want_to_cose_ure_crazy");
		linksUrlArray.add("bla1");
		linksUrlArray.add("link2");
    	String linkUrl = "http://www.google.it";
    	int userId = 0;
    	for(int i = 0; i < linksUrlArray.size(); i ++) {
			linksDataList.add(new Link(i, "ic_launcher", linksUrlArray.get(i), linkUrl, userId, "del_icon", deletedLinkFlag));
		}
    	return linksDataList;
    }


}

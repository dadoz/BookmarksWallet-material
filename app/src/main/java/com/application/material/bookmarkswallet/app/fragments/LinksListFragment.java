package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbAdapter;
import com.application.material.bookmarkswallet.app.decorator.DividerItemDecoration;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.R;
import com.getbase.floatingactionbutton.FloatingActionButton;


public class LinksListFragment extends Fragment
		implements View.OnClickListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	public DbAdapter db;
	private MainActivity mainActivityRef;
	@InjectView(R.id.linksListId)
	RecyclerView recyclerListView;
	@InjectView(R.id.addLinkButtonId)
	FloatingActionButton addLinkButton;
	@InjectView(R.id.undoLinkDeletedLayoutId)
	View undoLinkDeletedLayout;
	@InjectView(R.id.undoButtonId)
	View undoButton;
	@InjectView(R.id.dismissButtonId)
	View dismissButton;
	private LinearLayoutManager linearLayoutManager;
	private ArrayList<Link> linkListTest;

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
		linkListTest = getLinkListMockup();
//		LinkAdapter linkAdapter = new LinkAdapter(this, R.layout.link_row,
//				linkListTest);
		LinkRecyclerViewAdapter linkRecyclerViewAdapter =
				new LinkRecyclerViewAdapter(this, linkListTest);


		recyclerListView.setHasFixedSize(false);

		//set linear layout manager
		linearLayoutManager = new LinearLayoutManager(mainActivityRef);
		recyclerListView.setLayoutManager(linearLayoutManager);

		recyclerListView.setAdapter(linkRecyclerViewAdapter);
		recyclerListView.setItemAnimator(new DefaultItemAnimator());

		recyclerListView.addItemDecoration(new DividerItemDecoration(mainActivityRef,
				DividerItemDecoration.VERTICAL_LIST));

//		recyclerListView.setOnScrollListener(customScrollListener);
		addLinkButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		LinkRecyclerViewAdapter adapter = ((LinkRecyclerViewAdapter)
				recyclerListView.getAdapter());

		switch (v.getId()) {
			case R.id.addLinkButtonId:
				mainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkFragment.ADD_REQUEST, null);
				break;
		}
	}

/*	public void toggleEditView(boolean isShowEditView) {
		//TODO add animation from top to bottom for ex :D
		LinkRecyclerViewAdapter adapter = ((LinkRecyclerViewAdapter)
				recyclerListView.getAdapter());
		int position = adapter.getSelectedItemPosition();
		View selectedItemView = linearLayoutManager.g(position - 1);

		if(selectedItemView == null) {
			Log.e(TAG, "selectedItemView empty");
			return;
		}

		selectedItemView.setOnClickListener(isShowEditView ? null : this);
		selectedItemView.setBackgroundColor(getResources().getColor(isShowEditView ? R.color.material_grey_200 : R.color.white));
		//data text view
		selectedItemView.findViewById(R.id.linkTitleId).setVisibility(isShowEditView ? View.GONE : View.VISIBLE);
		selectedItemView.findViewById(R.id.editLinkLayoutId).setVisibility(isShowEditView ? View.VISIBLE : View.GONE);

		//button controller
		selectedItemView.findViewById(R.id.linkEditButtonId).setVisibility(isShowEditView ? View.GONE : View.VISIBLE);
		selectedItemView.findViewById(R.id.linkSaveButtonId).setVisibility(isShowEditView ? View.VISIBLE : View.GONE);
	}*/


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

	RecyclerView.OnScrollListener customScrollListener = new RecyclerView.OnScrollListener() {
		int mLastFirstVisibleItem = 0;

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			final int currentFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

			if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
				mainActivityRef.getSupportActionBar().hide();
			} else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
				mainActivityRef.getSupportActionBar().show();
			}

			this.mLastFirstVisibleItem = currentFirstVisibleItem;
		}
	};

	public void linkDeleteUpdateUI(boolean isDeleting) {
		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
	}

	public void setUndoLayoutListener(LinkRecyclerViewAdapter.ViewHolder undoLayoutListener) {
		undoButton.setOnClickListener(undoLayoutListener);
		dismissButton.setOnClickListener(undoLayoutListener);
	}


	public boolean deleteLink(Link linkObj, ListView recyclerListView,
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
//    	final ListView recyclerListView = (ListView)getActivity().findViewById(R.id.linksListId);
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
//		recyclerListView.setAdapter(adapter);
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
		linksUrlArray.add("underground");
		linksUrlArray.add("heavy metal");
		linksUrlArray.add("underground");
		linksUrlArray.add("hey_ure_fkin_my_shitty_dog_are_u_sure_u_want_to_cose_ure_crazy");
		linksUrlArray.add("bla1");
		linksUrlArray.add("link2");
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

package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.animators.CustomDefaultAnimator;
import com.application.material.bookmarkswallet.app.dbAdapter.DbAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.touchListener.*;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class LinksListFragment extends Fragment
		implements View.OnClickListener,
			SwipeDismissRecyclerViewTouchListener.DismissCallbacks,
			RecyclerView.OnItemTouchListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	public DbAdapter db;
	private MainActivity mainActivityRef;
	@InjectView(R.id.linksListId)
	RecyclerView mRecyclerView;
	@InjectView(R.id.addLinkButtonId)
	FloatingActionButton addLinkButton;
	@InjectView(R.id.undoLinkDeletedLayoutId)
	View undoLinkDeletedLayout;
	@InjectView(R.id.undoButtonId)
	View undoButton;
	@InjectView(R.id.dismissButtonId)
	View dismissButton;
	private LinearLayoutManager linearLayoutManager;
	private ArrayList<Link> mItems;
	private SwipeDismissRecyclerViewTouchListener touchListener;
	private GestureDetectorCompat detector;

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

		Toolbar toolbar = (Toolbar) addReviewView.findViewById(R.id.toolbarId);
		mainActivityRef.initActionBar(toolbar, null);

//		setHasOptionsMenu(true);
		onInitView();
		return addReviewView;
	}

	private void onInitView() {
		mItems = getLinkListMockup();
		LinkRecyclerViewAdapter linkRecyclerViewAdapter =
				new LinkRecyclerViewAdapter(this, mItems);

		mRecyclerView.setHasFixedSize(true);
//		mRecyclerView.setClickable(true);
//		mRecyclerView.setFocusableInTouchMode(true);
//		mRecyclerView.setFocusable(true);
//		mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

		linearLayoutManager = new LinearLayoutManager(mainActivityRef);
		mRecyclerView.setLayoutManager(linearLayoutManager);

		mRecyclerView.setAdapter(linkRecyclerViewAdapter);
		mRecyclerView.setItemAnimator(null);

		touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this);
		mRecyclerView.setOnTouchListener(touchListener);
		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

		detector = new GestureDetectorCompat(mainActivityRef, new RecyclerViewOnGestureListener());
		mRecyclerView.addOnItemTouchListener(this);
//		mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mainActivityRef, this));
//		mRecyclerView.addItemDecoration(new DividerItemDecoration(mainActivityRef,
//				DividerItemDecoration.VERTICAL_LIST));
//		mRecyclerView.setOnScrollListener(customScrollListener);

		addLinkButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		LinkRecyclerViewAdapter adapter = (LinkRecyclerViewAdapter) mRecyclerView.getAdapter();
		switch (v.getId()) {
			case R.id.addLinkButtonId:
				mainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkActivity.ADD_REQUEST, null);
				break;
			case R.id.undoButtonId:
				Toast.makeText(mainActivityRef, "undo", Toast.LENGTH_SHORT).show();
				Link deletedItem = adapter.getDeletedItem();
				int deletedItemPosition = adapter.getDeletedItemPosition();
				adapter.addOnPosition(deletedItem, deletedItemPosition);
				setUndoDeletedLinkLayout(false);
				break;
			case R.id.dismissButtonId:
				Toast.makeText(mainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				setUndoDeletedLinkLayout(false);
				break;

		}
	}

	//SWIPE ACTION
	@Override
	public boolean canDismiss(int position) {
		return true;
	}

	@Override
	public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
		Log.e(TAG, reverseSortedPositions.toString() + "removing action");
		int position = reverseSortedPositions[0];
		((LinkRecyclerViewAdapter) recyclerView.getAdapter()).remove(position);
		setUndoDeletedLinkLayout(true);
	}


	//onclick listener
	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
		detector.onTouchEvent(motionEvent);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

	}

	private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);

			// handle single tap
			String url = (mItems.get(position)).getLinkUrl();
			openLinkOnBrowser(url);

			return super.onSingleTapConfirmed(e);
		}

		public void onLongPress(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);

			// handle long press
			Log.e(TAG, "Hey long touch ");
			editLinkRecyclerView(position);
			super.onLongPress(e);
		}
	}

	public void saveLinkRecyclerView(int position) {
		Toast.makeText(mainActivityRef, "save", Toast.LENGTH_SHORT).show();
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).deselectedItemPosition();
		(mRecyclerView.getAdapter()).notifyDataSetChanged();
//		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).update(position,
//				mEditLabelView.getText().toString(), mEditUrlView.getText().toString());
		mainActivityRef.toggleEditActionBar(null, false);

	}

	public void undoEditLinkRecyclerView() {
		Toast.makeText(mainActivityRef, "undo edit", Toast.LENGTH_SHORT).show();
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).deselectedItemPosition();
		(mRecyclerView.getAdapter()).notifyDataSetChanged();
	}

	public void editLinkRecyclerView(int position) {
		Toast.makeText(mainActivityRef, "edit" + position, Toast.LENGTH_SHORT).show();
		mainActivityRef.toggleEditActionBar("Edit link", true);
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).setSelectedItemPosition(position);
		mRecyclerView.getAdapter().notifyDataSetChanged();
	}

	public void addLinkOnRecyclerView(String url) {
		Link link = new Link(-1, null, "NEW FAKE", url, -1, null, false);
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);

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

	//TODO rename it
	public void setUndoDeletedLinkLayout(boolean isDeleting) {
		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
	}
/*
	public void setUndoLayoutListener(LinkRecyclerViewAdapter.ViewHolder undoLayoutListener) {
		undoButton.setOnClickListener(undoLayoutListener);
		dismissButton.setOnClickListener(undoLayoutListener);
	}*/

/*
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
	};*/



	public boolean deleteLink(Link linkObj, ListView mRecyclerView,
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
//    	final ListView mRecyclerView = (ListView)getActivity().findViewById(R.id.linksListId);
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
//		mRecyclerView.setAdapter(adapter);
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
